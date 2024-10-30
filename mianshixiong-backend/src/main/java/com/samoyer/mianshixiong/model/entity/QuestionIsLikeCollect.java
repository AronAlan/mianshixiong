package com.samoyer.mianshixiong.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户是否已经点赞或收藏了某题目
 * @TableName question_is_like_collect
 */
@TableName(value ="question_is_like_collect")
@Data
public class QuestionIsLikeCollect implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 是否已经点赞
     */
    private Integer isLike;

    /**
     * 是否已经收藏
     */
    private Integer isFavour;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}