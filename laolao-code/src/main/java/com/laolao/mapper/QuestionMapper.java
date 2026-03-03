package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.vo.QuestionBankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    @Select("""
            select q.id, q.title, u.name as advisor
            from question q
                     join user u on u.id = q.advisor_id
            where q.advisor_id = #{userId} and q.parent_id = 0
            """)
    Page<QuestionBankVO> selectPrivateBank(Page<QuestionBankVO> page, Integer userId);

    @Select("""
            select q.id, q.title, u.name as advisor
            from question q
                     join user u on u.id = q.advisor_id
            where q.is_public = 1 and q.parent_id = 0
            """)
    Page<QuestionBankVO> selectPublicBank(Page<QuestionBankVO> page);
}
