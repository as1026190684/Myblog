package com.jx.blogap1.utils;

import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.vo.ArticleVo;
import com.jx.blogap1.vo.CommentVo;

import java.util.ArrayList;
import java.util.List;

public class CopyList {
    /**
     *
     * @author YYTE_JX
     * @date 2021/10/8 0008
     * @param objectList
     * @param objectVoList
     * @param objectVO
     * @return java.util.List<java.lang.Object>
     */
    public static List<Object> copyList(List<Object> objectList,List<Object> objectVoList,Object objectVO) {
        for (int i = 0; i < objectList.size(); i++) {
            objectVoList.add(Copy.copy(objectList.get(i),objectVO));
        }
        return objectVoList;
    }
}
