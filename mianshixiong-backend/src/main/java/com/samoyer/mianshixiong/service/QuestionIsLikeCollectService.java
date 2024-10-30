package com.samoyer.mianshixiong.service;

import com.samoyer.mianshixiong.model.entity.QuestionIsLikeCollect;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author XZC
 * @description 针对表【question_is_like_collect(用户是否已经点赞或收藏了某题目)】的数据库操作Service
 * @createDate 2024-10-30 18:52:21
 */
public interface QuestionIsLikeCollectService extends IService<QuestionIsLikeCollect> {

    /**
     * 将question_is_like_collect表中该用户对于该题目的点赞设置为点赞或未点赞
     * @param userId
     * @param questionId
     * @param isLike
     */
    void updateLike(Long userId, Long questionId,Integer isLike);

    /**
     * 将question_is_like_collect表中该用户对于该题目的收藏设置为收藏或未收藏
     * @param userId
     * @param questionId
     * @param isFavour
     */
    void updateFavour(Long userId, Long questionId, Integer isFavour);
}
