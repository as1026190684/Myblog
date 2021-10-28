package com.jx.blogap1.vo.params;


import com.jx.blogap1.vo.CategoryVo;
import com.jx.blogap1.vo.TagVo;
import lombok.Data;

import java.util.List;

@Data
public class ArticleParam {

    private Long id;

    private ArticleBodyParam body;

    private CategoryVo category;

    private String summary;

    private List<TagVo> tags;

    private String title;


}
