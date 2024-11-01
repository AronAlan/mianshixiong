package com.samoyer.mianshixiong.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.samoyer.mianshixiong.common.BaseResponse;
import com.samoyer.mianshixiong.common.DeleteRequest;
import com.samoyer.mianshixiong.common.ErrorCode;
import com.samoyer.mianshixiong.common.ResultUtils;
import com.samoyer.mianshixiong.constant.UserConstant;
import com.samoyer.mianshixiong.exception.BusinessException;
import com.samoyer.mianshixiong.exception.ThrowUtils;
import com.samoyer.mianshixiong.manager.CounterManager;
import com.samoyer.mianshixiong.model.dto.question.*;
import com.samoyer.mianshixiong.model.entity.Question;
import com.samoyer.mianshixiong.model.entity.QuestionLikeCollect;
import com.samoyer.mianshixiong.model.entity.User;
import com.samoyer.mianshixiong.model.vo.QuestionVO;
import com.samoyer.mianshixiong.model.vo.RecommendQuestionVo;
import com.samoyer.mianshixiong.sentinel.SentinelConstant;
import com.samoyer.mianshixiong.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 题目接口
 *
 * @author Samoyer
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private QuestionIsLikeCollectService questionIsLikeCollectService;

    @Resource
    private QuestionLikeCollectService questionLikeCollectService;

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        //写入点赞收藏表
        QuestionLikeCollect questionLikeCollect = new QuestionLikeCollect();
        questionLikeCollect.setQuestionId(newQuestionId);
        questionLikeCollect.setLikeCount(0L);
        questionLikeCollect.setFavourCount(0L);
        questionLikeCollectService.save(questionLikeCollect);
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 删除题目
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 删除题库题目关联表中所有questionId为id的数据
        questionBankQuestionService.removeByQuestionId(id);
        // 删除点赞收藏表的数据
        LambdaQueryWrapper<QuestionLikeCollect> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionLikeCollect.class)
                .eq(QuestionLikeCollect::getQuestionId, id);
        questionLikeCollectService.remove(lambdaQueryWrapper);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 检测和处置爬虫（获取题目详情的前端使用服务端渲染的话，sa-token的Bug,getLoginUser获取不到当前登录用户）
        User loginUser = userService.getLoginUser(request);
        crawlerDetect(loginUser.getId());

        // 对于敏感的内容，可以再打印一些日志，记录用户访问的内容
        // 查询题目详情
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(loginUser.getId(),question, request));
    }

    // 仅是为了方便，才把这段代码写到这里
    @Resource
    private CounterManager counterManager;

    /**
     * 检测爬虫
     *
     * @param loginUserId
     */
    private void crawlerDetect(long loginUserId) {
        // 调用多少次时告警
        final int WARN_COUNT = 10;
        // 调用多少次时封号
        final int BAN_COUNT = 20;
        // 拼接访问 key
        String key = String.format("user:access:%s", loginUserId);
        // 统计一分钟内访问次数，180 秒过期
        long count = counterManager.incrAndGetCounter(key, 1, TimeUnit.MINUTES, 180);
        // 是否封号
        if (count > BAN_COUNT) {
            // 踢下线
            StpUtil.kickout(loginUserId);
            // 封号
            User updateUser = new User();
            updateUser.setId(loginUserId);
            updateUser.setUserRole("ban");
            userService.updateById(updateUser);
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "访问次数过多，已被封号");
        }
        // 是否告警
        if (count == WARN_COUNT) {
            // 可以改为向管理员发送邮件通知
            throw new BusinessException(110, "警告：访问太频繁");
        }
    }

    /**
     * 分页获取题目列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取题目列表（封装类 - 限流版）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo/sentinel")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPageSentinel(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                       HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 基于 IP 限流
        String remoteAddr = request.getRemoteAddr();
        Entry entry = null;
        try {
            entry = SphU.entry(SentinelConstant.listQuestionVOByPage, EntryType.IN, 1, remoteAddr);
            // 被保护的业务逻辑
            // 查询数据库
            Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
            // 获取封装类
            return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
        } catch (Throwable ex) {
            // 业务异常
            if (!BlockException.isBlockException(ex)) {
                Tracer.trace(ex);
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
            }
            // 降级操作
            if (ex instanceof DegradeException) {
                return handleFallback(questionQueryRequest, request, ex);
            }
            // 限流操作
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "访问过于频繁，请稍后再试");
        } finally {
            if (entry != null) {
                entry.exit(1, remoteAddr);
            }
        }
    }

    /**
     * listQuestionVOByPageSentinel 降级操作：直接返回本地数据（此处为了方便演示，写在同一个类中）
     */
    public BaseResponse<Page<QuestionVO>> handleFallback(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                         HttpServletRequest request, Throwable ex) {
        // 可以返回本地数据或空数据
        return ResultUtils.success(null);
    }


    /**
     * 分页获取当前登录用户创建的题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑题目（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    @PostMapping("/search/page/vo")
    public BaseResponse<Page<QuestionVO>> searchQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);
        // todo 取消注释开启 ES（须先配置 ES）
        // 查询 ES
        // Page<Question> questionPage = questionService.searchFromEs(questionQueryRequest);
        // 查询数据库（作为没有 ES 的降级方案）
        Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    @PostMapping("/delete/batch")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchDeleteQuestions(@RequestBody QuestionBatchDeleteRequest questionBatchDeleteRequest) {
        ThrowUtils.throwIf(questionBatchDeleteRequest == null, ErrorCode.PARAMS_ERROR);
        questionService.batchDeleteQuestions(questionBatchDeleteRequest.getQuestionIdList());
        return ResultUtils.success(true);
    }

    /**
     * 点赞
     * @param questionId
     * @param request
     * @return
     */
    @PostMapping("/like/{questionId}")
    public BaseResponse<Boolean> addQuestionLikeCount(@PathVariable Long questionId, HttpServletRequest request) {
        ThrowUtils.throwIf(questionId == null, ErrorCode.PARAMS_ERROR);
        //将question_is_like_collect表中该用户对于该题目的点赞设置为已经点赞
        User loginUser = userService.getLoginUser(request);
        questionIsLikeCollectService.updateLike(loginUser.getId(), questionId,1);
        //对question_like_collect表的点赞量加1
        questionLikeCollectService.addLikeCount(questionId);
        return ResultUtils.success(true);
    }

    /**
     * 取消点赞
     * @param questionId
     * @param request
     * @return
     */
    @DeleteMapping("/like/{questionId}")
    public BaseResponse<Boolean> deleteQuestionLikeCount(@PathVariable Long questionId, HttpServletRequest request) {
        ThrowUtils.throwIf(questionId == null, ErrorCode.PARAMS_ERROR);
        //将question_is_like_collect表中该用户对于该题目的点赞设置为未点赞
        User loginUser = userService.getLoginUser(request);
        questionIsLikeCollectService.updateLike(loginUser.getId(), questionId,0);
        //对question_like_collect表的点赞量减1
        questionLikeCollectService.reduceLikeCount(questionId);
        return ResultUtils.success(true);
    }

    /**
     * 收藏
     * @param questionId
     * @param request
     * @return
     */
    @PostMapping("/favour/{questionId}")
    public BaseResponse<Boolean> addQuestionFavourCount(@PathVariable Long questionId, HttpServletRequest request) {
        ThrowUtils.throwIf(questionId == null, ErrorCode.PARAMS_ERROR);
        //将question_is_like_collect表中该用户对于该题目的收藏设置为已经收藏
        User loginUser = userService.getLoginUser(request);
        questionIsLikeCollectService.updateFavour(loginUser.getId(), questionId,1);
        //对question_like_collect表的收藏量加1
        questionLikeCollectService.addFavourCount(questionId);
        return ResultUtils.success(true);
    }

    /**
     * 取消点赞
     * @param questionId
     * @param request
     * @return
     */
    @DeleteMapping("/favour/{questionId}")
    public BaseResponse<Boolean> deleteQuestionFavourCount(@PathVariable Long questionId, HttpServletRequest request) {
        ThrowUtils.throwIf(questionId == null, ErrorCode.PARAMS_ERROR);
        //将question_is_like_collect表中该用户对于该题目的收藏设置为未收藏
        User loginUser = userService.getLoginUser(request);
        questionIsLikeCollectService.updateFavour(loginUser.getId(), questionId,0);
        //对question_like_collect表的收藏量减1
        questionLikeCollectService.reduceFavourCount(questionId);
        return ResultUtils.success(true);
    }

    /**
     * 获取推荐题目
     *
     * @return
     */
    @GetMapping("/get/recommend")
    public BaseResponse<List<RecommendQuestionVo>> getRecommendQuestionVoById(HttpServletRequest request) {
        // 方案1：TODO 改为：将题目表中所有题目的id都存到redis中，从redis中取
        // 方案2：TODO 将当前用户的刷题记录和对应的标签 记录到redis中。根据当前用户浏览记录，按照题目所属标签的热度（次数）排序，从此标签中获取响应的题目推荐
        // 方案3：TODO 按照总题目热度排序，每个题目被阅读时，redis中浏览量加1，推荐热度前十的题目

        //获取题目表中所有的ids
        List<RecommendQuestionVo> recommendQuestionVoList=questionService.getRecommendQuestionVos();
        //咱使用随机取的办法，随机取10个
        int sampleSize = Math.min(10, recommendQuestionVoList.size());
        Collections.shuffle(recommendQuestionVoList);
        return ResultUtils.success(recommendQuestionVoList.subList(0, sampleSize));
    }
}
