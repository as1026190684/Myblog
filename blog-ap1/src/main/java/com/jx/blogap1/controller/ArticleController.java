package com.jx.blogap1.controller;


import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.service.ArticleService;
import com.jx.blogap1.vo.ArticleVo;
//import com.jx.blogap1.vo.Result;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("articles")
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class ArticleController {

    @Autowired
    private ArticleService articleService;
	//Result是统一结果返回
    @PostMapping
    public Result articles(@RequestBody PageParams pageParams) {
        //ArticleVo 页面接收的数据
        List<ArticleVo> articles = articleService.listArticlesPage(pageParams);

        return Result.success(articles);
    }

    /**
     * 最热文章
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param
     * @return
     */
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
    @PostMapping("/listArchives")
    public Result listArchives() {
        return articleService.listArchives();
    }


    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id")  Long id) {
        ArticleVo articleVo = articleService.findArticleById(id);
        return Result.success(articleVo);
    }
}