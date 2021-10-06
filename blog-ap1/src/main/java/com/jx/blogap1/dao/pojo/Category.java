package com.jx.blogap1.dao.pojo;

import lombok.Data;

@Data
public class Category {

    private Long id;

    private String avatar;

    private String categoryName;

    private String description;
}