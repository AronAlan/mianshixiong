package com.samoyer.mianshixiong.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.samoyer.mianshixiong.model.entity.QuestionIsLikeCollect;
import com.samoyer.mianshixiong.model.entity.User;
import com.samoyer.mianshixiong.service.QuestionIsLikeCollectService;
import com.samoyer.mianshixiong.mapper.QuestionIsLikeCollectMapper;
import org.springframework.stereotype.Service;

/**
 * @author XZC
 * @description 针对表【question_is_like_collect(用户是否已经点赞或收藏了某题目)】的数据库操作Service实现
 * @createDate 2024-10-30 18:52:21
 */
@Service
public class QuestionIsLikeCollectServiceImpl extends ServiceImpl<QuestionIsLikeCollectMapper, QuestionIsLikeCollect>
        implements QuestionIsLikeCollectService {

    /**
     * 将question_is_like_collect表中该用户对于该题目的点赞设置为点赞或未点赞
     * @param userId
     * @param questionId
     * @param isLike
     */
    @Override
    public void updateLike(Long userId, Long questionId,Integer isLike) {
        this.lambdaUpdate()
                .eq(QuestionIsLikeCollect::getUserId, userId)
                .eq(QuestionIsLikeCollect::getQuestionId, questionId)
                .set(QuestionIsLikeCollect::getIsLike, isLike).update();
    }

    /**
     * 将question_is_like_collect表中该用户对于该题目的收藏设置为收藏或未收藏
     * @param userId
     * @param questionId
     * @param isFavour
     */
    @Override
    public void updateFavour(Long userId, Long questionId, Integer isFavour) {
        this.lambdaUpdate()
                .eq(QuestionIsLikeCollect::getUserId, userId)
                .eq(QuestionIsLikeCollect::getQuestionId, questionId)
                .set(QuestionIsLikeCollect::getIsFavour, isFavour).update();
    }
}




