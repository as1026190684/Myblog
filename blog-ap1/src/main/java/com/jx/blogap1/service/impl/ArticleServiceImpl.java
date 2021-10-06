package com.jx.blogap1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jx.blogap1.dao.dos.Archives;
import com.jx.blogap1.dao.mapper.ArticleBodyMapper;
import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.dao.pojo.ArticleBody;
import com.jx.blogap1.dao.pojo.Category;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.service.*;
import com.jx.blogap1.vo.ArticleBodyVo;
import com.jx.blogap1.vo.ArticleVo;
import com.jx.blogap1.vo.CategoryVo;
import com.jx.blogap1.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ThreadService threadService;

    @Override
    public List<ArticleVo> listArticlesPage(PageParams pageParams) {

        /**
         * 分页查询article表
         * @author YYTE_JX
         * @date 2021/10/1 0001
         * @param pageParams
         * @return java.util.List<com.jx.blogap1.vo.ArticleVo>
         */
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

//        获取page对象中的所有记录
        List<Article> records = articlePage.getRecords();

//        将Article型转换出ArticleVo型
        List<ArticleVo> articleVoList = copyList(records,true,true);
        return articleVoList;
    }

    /**
     * 最热文章
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param limit
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result hotArticle(int limit) {
        List<Article> hotArticle = articleMapper.findHotArticleByViewCounts(limit);
        if (CollectionUtils.isEmpty(hotArticle)) {
            return Result.fail("没有最热文章");
        }
        return Result.success(copyList(hotArticle,false,false));
    }

    /**
     * 最新文章
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param limit
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result newArticle(int limit) {

        List<Article> newArticle = articleMapper.findNewArticleByCreateTime(limit);
        if (CollectionUtils.isEmpty(newArticle)) {
            return Result.fail("没有最新文章");
        }
        return Result.success(copyList(newArticle,false,false));

    }

    /**
     * 文章归档
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        if (CollectionUtils.isEmpty(archivesList)) {
            return Result.build(ResultCodeEnum.DATA_IS_EMPTY.getCode(),ResultCodeEnum.DATA_IS_EMPTY.getMessage());
        }
        return Result.success(archivesList);
    }

    /**
     * 文章详情
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param id
     * @return com.jx.blogap1.dao.pojo.Article
     */
    @Override
    public ArticleVo findArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        threadService.updateViewCount(articleMapper, article);
        return copy(article,true,true,true,true);
    }


    private List<ArticleVo> copyList(List<Article> records,boolean isTag, boolean isAuthor) {

        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,false,false));
        }
        return articleVoList;
    }
    //copyList重载
    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor,boolean isBody,boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategory));
        }
        return articleVoList;
    }

    public ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {

        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);

//        时间格式处理方法，org.joda.time.DateTime;
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody) {
            ArticleBodyVo articleBodyVo = findArticleBody(article.getId());
            articleVo.setBody(articleBodyVo);
        }
        if (isCategory) {
            CategoryVo categoryVo = findCategory(article.getCategoryId());
            articleVo.setCategory(categoryVo);
        }
        return articleVo;
    }

    @Autowired
    private CategoryService categoryService;

    private CategoryVo findCategory(Long categoryId) {
        return categoryService.findCategoryById(categoryId);
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBody(Long id) {
        LambdaQueryWrapper<ArticleBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleBody::getArticleId, id);
        ArticleBody articleBody = articleBodyMapper.selectOne(queryWrapper);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }

}
