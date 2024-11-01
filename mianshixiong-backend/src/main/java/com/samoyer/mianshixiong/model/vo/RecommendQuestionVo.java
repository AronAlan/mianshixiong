package com.samoyer.mianshixiong.model.vo;

import com.samoyer.mianshixiong.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 推荐题目视图
 *
 * @author Samoyer
 * 
 */
@Data
public class RecommendQuestionVo implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 封装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(RecommendQuestionVo questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        return question;
    }

    /**
     * 对象转封装类
     *
     * @param question
     * @return
     */
    public static RecommendQuestionVo objToVo(Question question) {
        if (question == null) {
            return null;
        }
        RecommendQuestionVo recommendQuestionVo = new RecommendQuestionVo();
        BeanUtils.copyProperties(question, recommendQuestionVo);
        return recommendQuestionVo;
    }
}
