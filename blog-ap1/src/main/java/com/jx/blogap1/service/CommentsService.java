package com.jx.blogap1.service;

import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.params.CommentParam;

public interface CommentsService {

    // 查找评论
    Result findCommentsByArticleId(Long articleId);

    //评论
    Result comment(CommentParam commentParam);
}
