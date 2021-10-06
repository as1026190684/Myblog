package com.jx.blogap1.service;

import com.jx.blogap1.result.Result;

public interface CommentsService {

    // 查找评论
    Result findCommentsByArticleId(Long articleId);
}
