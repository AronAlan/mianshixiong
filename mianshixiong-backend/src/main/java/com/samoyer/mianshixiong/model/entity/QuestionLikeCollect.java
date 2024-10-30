package com.samoyer.mianshixiong.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 题目的点赞和收藏
 * @TableName question_like_collect
 */
@TableName(value ="question_like_collect")
@Data
public class QuestionLikeCollect implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 点赞量
     */
    private Long likeCount;

    /**
     * 收藏量
     */
    private Long favourCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}