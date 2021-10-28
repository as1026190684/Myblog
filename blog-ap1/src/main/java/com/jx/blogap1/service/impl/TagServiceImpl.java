package com.jx.blogap1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jx.blogap1.dao.mapper.TagMapper;
import com.jx.blogap1.dao.pojo.Tag;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.service.TagService;
import com.jx.blogap1.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);

        return copyList(tags);
    }

    @Override
    public List<TagVo> hot(int limit) {
        List<Long> hotsTagIds = tagMapper.findHotsTagIds(limit);

        if (CollectionUtils.isEmpty(hotsTagIds)) {
            return Collections.emptyList();
        }
        List<Tag> tagList = tagMapper.finTagsByTagIds(hotsTagIds);

        return copyList(tagList);
    }

    @Override
    public Result findAll() {
        List<Tag> tags = this.tagMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(copyList(tags));
    }

    /**
     * 查询所有的标签
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        List<Tag> tags = this.tagMapper.selectList(queryWrapper);
        return Result.success(copyList(tags));
    }

    /**
     * 标签文章列表
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param id
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result findDetailById(Long id) {
        Tag tag = tagMapper.selectById(id);
        TagVo copy = copy(tag);
        return Result.success(copy);
    }


    private TagVo copy(Tag tag) {
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag, tagVo);
        return tagVo;
    }

    public List<TagVo> copyList(List<Tag> tagList) {

        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }
}
