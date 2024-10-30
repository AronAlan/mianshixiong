package com.samoyer.mianshixiong.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.samoyer.mianshixiong.common.ErrorCode;
import com.samoyer.mianshixiong.exception.BusinessException;
import com.samoyer.mianshixiong.model.entity.QuestionLikeCollect;
import com.samoyer.mianshixiong.service.QuestionLikeCollectService;
import com.samoyer.mianshixiong.mapper.QuestionLikeCollectMapper;
import org.springframework.stereotype.Service;

/**
* @author XZC
* @description 针对表【question_like_collect(题目的点赞和收藏)】的数据库操作Service实现
* @createDate 2024-10-30 18:25:25
*/
@Service
public class QuestionLikeCollectServiceImpl extends ServiceImpl<QuestionLikeCollectMapper, QuestionLikeCollect>
    implements QuestionLikeCollectService{

    /**
     * 对question_like_collect表的点赞量加1
     * @param questionId
     */
    @Override
    public void addLikeCount(Long questionId) {
        try {
            this.lambdaUpdate()
                    .eq(QuestionLikeCollect::getQuestionId, questionId)
                    .setSql("likeCount = likeCount + 1").update();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"点赞失败");
        }
    }

    /**
     * 对question_like_collect表的点赞量减1
     * @param questionId
     */
    @Override
    public void reduceLikeCount(Long questionId) {
        try {
            this.lambdaUpdate()
                    .eq(QuestionLikeCollect::getQuestionId, questionId)
                    .setSql("likeCount = likeCount - 1").update();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"取消点赞失败");
        }
    }

    /**
     * 对question_like_collect表的收藏量加1
     * @param questionId
     */
    @Override
    public void addFavourCount(Long questionId) {
        try {
            this.lambdaUpdate()
                    .eq(QuestionLikeCollect::getQuestionId, questionId)
                    .setSql("favourCount = favourCount + 1").update();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"收藏失败");
        }
    }

    /**
     * 对question_like_collect表的收藏量减1
     * @param questionId
     */
    @Override
    public void reduceFavourCount(Long questionId) {
        try {
            this.lambdaUpdate()
                    .eq(QuestionLikeCollect::getQuestionId, questionId)
                    .setSql("favourCount = favourCount - 1").update();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"取消收藏失败");
        }
    }
}




