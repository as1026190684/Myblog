package com.jx.blogap1.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jx.blogap1.dao.pojo.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TagMapper extends BaseMapper<Tag> {

    List<Tag> findTagsByArticleId(Long articleId);

    List<Long> findHotsTagIds(int limit);


    List<Tag> finTagsByTagIds(List<Long> hotsTagIds);


}