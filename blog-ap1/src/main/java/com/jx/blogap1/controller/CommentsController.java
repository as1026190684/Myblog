package com.jx.blogap1.controller;

import com.jx.blogap1.result.Result;
import com.jx.blogap1.service.CommentsService;
import com.jx.blogap1.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * findCommentsByArticleId
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param articleId
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "comments",keyGenerator = "keyGenerator")
    @GetMapping("/article/{id}")
    public Result comments(@PathVariable("id") Long articleId) {
        return commentsService.findCommentsByArticleId(articleId);
    }

    /**
     * 评论
     * @author YYTE_JX
     * @date 2021/10/17 0017
     * @param commentParam
     * @return com.jx.blogap1.result.Result
     */
    //todo 做评论 只 删除 对应文章 的 评论缓存
    @CacheEvict(value = "comments", allEntries=true)
    @PostMapping("create/change")
    public Result comment(@RequestBody CommentParam commentParam){
        return commentsService.comment(commentParam);
    }

}
