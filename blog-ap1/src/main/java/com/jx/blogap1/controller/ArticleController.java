package com.jx.blogap1.controller;


import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.mapper.ArticleTagMapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.service.ArticleService;
import com.jx.blogap1.service.ThreadService;
import com.jx.blogap1.vo.ArticleVo;
//import com.jx.blogap1.vo.Result;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.params.ArticleParam;
import com.jx.blogap1.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("articles")
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 主页展示文章
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param pageParams
     * @return com.jx.blogap1.result.Result
     */
    //todo 这里未做外部阅读量实时更新。原因：这里做了缓存，不能继续执行获取阅读量的方法
    @Cacheable(value = "articles",keyGenerator = "keyGenerator")
    @PostMapping
    public Result articles(@RequestBody PageParams pageParams) {
        //ArticleVo 页面接收的数据
        return articleService.listArticlesPage(pageParams);
    }

    /**
     * 最热文章
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param
     * @return
     */
    @Cacheable(value = "articles",keyGenerator = "keyGenerator")
    @PostMapping("/hot")
    public Result hotArticle() {
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    /**
     * 最新文章
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "articles",keyGenerator = "keyGenerator")
    @PostMapping("/new")
    public Result newArticle() {
        int limit = 5;
        return articleService.newArticle(limit);
    }


    /**
     * 文章归档
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "articles",keyGenerator = "keyGenerator")
    @PostMapping("/listArchives")
    public Result listArchives() {
        return articleService.listArchives();
    }

    /**
     *
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param id
     * @return com.jx.blogap1.result.Result
     */
//    @Cacheable(value = "articles",keyGenerator = "keyGenerator")
    //这里不采用springCache注解的方式缓存，一旦文章被缓存，阅读量增加就不会被执行。
    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id")  Long id) {

        ArticleVo articleVo = articleService.findArticleById(id);
        return Result.success(articleVo);
    }

    /**
     *  发布文章
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param articleParam
     * @return com.jx.blogap1.result.Result
     */
    @CacheEvict(value = {"articles","tags","categorys"}, allEntries=true)
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }

    /**
     * 更新前获取文章
     * @author YYTE_JX
     * @date 2021/10/20 0020
     * @param id
     * @return com.jx.blogap1.result.Result
     */
    @PostMapping ("/{id}")
    public Result findArticleByIdToUpdate(@PathVariable("id")  Long id) {

        ArticleVo articleVo = articleService.findArticleByIdToUpdate(id);
        return Result.success(articleVo);
    }

    /**
     *  更新文章
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param articleParam
     * @return com.jx.blogap1.result.Result
     */
    @CacheEvict(value = {"articles","tags","categorys"}, allEntries=true)
    @PostMapping("update")
    public Result update(@RequestBody ArticleParam articleParam){
        return articleService.update(articleParam);
    }

    /**
     * 获取个人的所有文章
     * @author YYTE_JX
     * @date 2021/10/22 0022
     * @param id
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "articles",keyGenerator = "keyGenerator")
    @GetMapping("/getArticlesByUserId/{id}")
    public Result getArticlesByUserId(@PathVariable("id") Long id) {

        return articleService.getArticlesByUserId(id);
    }
}