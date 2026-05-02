package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.Tag;
import com.laolao.pojo.vo.QuestionBankTagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionTagMapper extends BaseMapper<Tag> {
    @Select("""
            select t.id, t.name, count(qt.question_id) count
            from tag t
                     left join question_tag qt on qt.tag_id = t.id
            group by t.id, t.name;
            """)
    List<QuestionBankTagVO> selectAllTags();

    @Select("""
            select t.name
            from question_tag qt
                     join tag t on t.id = qt.tag_id
            where question_id = #{questionId}
            """)
    List<String> selectTagsByQuestion(Integer questionId);
}
