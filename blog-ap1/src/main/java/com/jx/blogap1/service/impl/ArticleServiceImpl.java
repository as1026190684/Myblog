package com.jx.blogap1.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jx.blogap1.dao.dos.Archives;
import com.jx.blogap1.dao.mapper.ArticleBodyMapper;
import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.mapper.ArticleTagMapper;
import com.jx.blogap1.dao.pojo.*;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.service.*;
import com.jx.blogap1.utils.PageCovertUtil;
import com.jx.blogap1.utils.RedisDBChangeUtil;
import com.jx.blogap1.utils.UserThreadLocal;
import com.jx.blogap1.vo.*;
import com.jx.blogap1.vo.params.ArticleParam;
import com.jx.blogap1.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

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
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private RedisDBChangeUtil redisDBChangeUtil;

    /**
     * 分页查询article表
     * @author YYTE_JX
     * @date 2021/10/1 0001
     * @param pageParams
     * @return java.util.List<com.jx.blogap1.vo.ArticleVo>
     */
    @Override
    public Result listArticlesPage(PageParams pageParams) {

        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = this.articleMapper.listArticle(page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());
        List<ArticleVo> articleVoList = copyList(articleIPage.getRecords(), true, true);
        IPage<ArticleVo> articleVoIPage = PageCovertUtil.pageVoCovert(articleIPage, ArticleVo.class);
        articleVoIPage.setRecords(articleVoList);
        return Result.success(articleVoIPage);
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
        //手动缓存
//        boolean b = redisDBChangeUtil.hasKey("articles::com.jx.blogap1.controller.ArticleControllerfindArticleById" + id);
//        if (!b) {
            Article article = articleMapper.selectById(id);
            ArticleVo articleVo = copy(article, true, true, true, true);
//            redisDBChangeUtil.set("articles::com.jx.blogap1.controller.ArticleControllerfindArticleById" + id, articleVo);
            threadService.updateViewCount(articleMapper, articleVo);
//            return articleVo;
//        }
//        ArticleVo articleVo = (ArticleVo) redisDBChangeUtil.get("articles::com.jx.blogap1.controller.ArticleControllerfindArticleById" + id);
//        threadService.updateViewCount(articleMapper, articleVo);
        return articleVo;
    }

    /**
     * 发布文章
     * @author YYTE_JX
     * @date 2021/10/20 0020
     * @param articleParam
     * @return com.jx.blogap1.result.Result
     */
    @Override
    @Transactional
    public Result publish(ArticleParam articleParam) {
        SysUser sysUser = UserThreadLocal.get();

        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setCategoryId(articleParam.getCategory().getId());
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common);
        article.setBodyId(-1L);
        this.articleMapper.insert(article);

        //tags
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                this.articleTagMapper.insert(articleTag);
            }
        }
        ArticleBody articleBody = new ArticleBody();
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBody.setArticleId(article.getId());
        articleBodyMapper.insert(articleBody);
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(article.getId());

        redisDBChangeUtil.set("ViewCounts:"+article.getId().toString(), 0);
        redisDBChangeUtil.incr1("ViewCounts:"+article.getId().toString(), 1);
        redisDBChangeUtil.setPersist("ViewCounts:"+article.getId().toString());

        return Result.success(articleVo);
    }

    /**
     * 更新前获取文章
     * @author YYTE_JX
     * @date 2021/10/20 0020
     * @param id
     * @return com.jx.blogap1.vo.ArticleVo
     */
    @Override
    public ArticleVo findArticleByIdToUpdate(Long id) {
        Article article = articleMapper.selectById(id);
        return copy(article, true, false, true, true);
    }

    /**
     * 更新文章
     * @author YYTE_JX
     * @date 2021/10/20 0020
     * @param articleParam
     * @return com.jx.blogap1.result.Result
     */
    //todo 增加文章修改时间字段
    @Override
    @Transactional
    public Result update(ArticleParam articleParam) {

        //这里需要把访问路劲加入拦截器，才能获取到用户信息
        SysUser sysUser = UserThreadLocal.get();

        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setCategoryId(articleParam.getCategory().getId());
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common);
        article.setBodyId(-1L);
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();

        articleLambdaQueryWrapper.eq(Article::getId, articleParam.getId());//articleParam.getId()==article_id
        this.articleMapper.update(article,articleLambdaQueryWrapper);

        //tags
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId, articleParam.getId());
            articleTagMapper.delete(articleTagLambdaQueryWrapper);

            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleParam.getId());
                articleTag.setTagId(tag.getId());
                this.articleTagMapper.insert(articleTag);
            }
        }

        ArticleBody articleBody = new ArticleBody();
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBody.setArticleId(article.getId());
        LambdaQueryWrapper<ArticleBody> articleBodyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleBodyLambdaQueryWrapper.eq(ArticleBody::getArticleId, articleParam.getId());
        articleBodyMapper.update(articleBody,articleBodyLambdaQueryWrapper);

        return Result.success(articleParam.getId().toString());
    }

    /**
     * 获取个人的所有文字
     * @author YYTE_JX
     * @date 2021/10/22 0022
     * @param id
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result getArticlesByUserId(Long id) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.select(Article::getId, Article::getTitle);
        articleLambdaQueryWrapper.eq(Article::getAuthorId, id);
        articleLambdaQueryWrapper.orderByDesc(Article::getCreateDate);
        List<Article> userArticle = articleMapper.selectList(articleLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(userArticle)) {
            return Result.build(ResultCodeEnum.PERSONAL_ERROR.getCode(),ResultCodeEnum.PERSONAL_ERROR.getMessage());
        }
        return Result.success(copyList(userArticle,false,false));
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
            SysUser userById = sysUserService.findUserById(authorId);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(userById,userVo );
            articleVo.setAuthor(userVo);
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
