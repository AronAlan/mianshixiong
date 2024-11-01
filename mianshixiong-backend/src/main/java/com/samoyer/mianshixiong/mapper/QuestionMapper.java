package com.samoyer.mianshixiong.mapper;

import com.samoyer.mianshixiong.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.samoyer.mianshixiong.model.vo.RecommendQuestionVo;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @description 针对表【question(题目)】的数据库操作Mapper
* @Entity com.samoyer.mianshixiong.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 查询题目列表（包括已被删除的数据）
     */
    @Select("select * from question where updateTime >= #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime);

    /**
     * 获取所有未被删除的题目ids
     * @return
     */
    @Select("select id,title from question where isDelete=0")
    List<RecommendQuestionVo> getRecommendQuestionVos();
}
