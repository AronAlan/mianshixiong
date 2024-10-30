package com.samoyer.mianshixiong.service;

import com.samoyer.mianshixiong.common.ErrorCode;
import com.samoyer.mianshixiong.exception.BusinessException;
import com.samoyer.mianshixiong.model.entity.QuestionLikeCollect;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author XZC
* @description 针对表【question_like_collect(题目的点赞和收藏)】的数据库操作Service
* @createDate 2024-10-30 18:25:25
*/
public interface QuestionLikeCollectService extends IService<QuestionLikeCollect> {
    /**
     * 对question_like_collect表的点赞量加1
     * @param questionId
     */
    void addLikeCount(Long questionId);

    /**
     * 对question_like_collect表的点赞量减1
     * @param questionId
     */
    void reduceLikeCount(Long questionId);

    /**
     * 对question_like_collect表的收藏量加1
     * @param questionId
     */
    void addFavourCount(Long questionId);

    /**
     * 对question_like_collect表的收藏量减1
     * @param questionId
     */
    void reduceFavourCount(Long questionId);
}
