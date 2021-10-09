package com.jx.blogap1.utils;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.dao.pojo.ArticleBody;
import com.jx.blogap1.service.SysUserService;
import com.jx.blogap1.service.TagService;
import com.jx.blogap1.vo.ArticleBodyVo;
import com.jx.blogap1.vo.ArticleVo;
import com.jx.blogap1.vo.CategoryVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class Copy {

    public static Object copy(Object object,Object objectVo) {
        BeanUtils.copyProperties(object, objectVo);
        return objectVo;
    }


}
