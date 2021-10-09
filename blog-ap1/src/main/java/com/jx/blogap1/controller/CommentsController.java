package com.jx.blogap1.controller;

import com.jx.blogap1.result.Result;
import com.jx.blogap1.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @GetMapping("article/{id}")
    public Result comments(@PathVariable("id") Long articleId) {
        return commentsService.findCommentsByArticleId(articleId);
    }


}
