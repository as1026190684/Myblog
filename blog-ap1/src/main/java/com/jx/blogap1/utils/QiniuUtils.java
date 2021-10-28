package com.jx.blogap1.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class QiniuUtils {

    @Value("${qiniu.accessKey}")
    private  String ACCESS_KEY;
    @Value("${qiniu.accessSecretKey}")
    private  String SECRET_KEY;
    // 域名
    public static final String QINIU_IMAGE_DOMAIN = "http://static.ytte.top/";
    // 要上传的空间
    private String bucketName = "blogytte";




    // 简单上传，使用默认策略，只需要设置上传的空间名就可以了
//    public String getUpToken() {
//        return auth.uploadToken(bucketName);
//    }
    public String saveImage(MultipartFile file) throws IOException {
        // 密钥配置
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        // 构造一个带指定Zone对象的配置类,不同的七云牛存储区域调用不同的zone
        Configuration cfg = new Configuration(Region.huadong());
        // ...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        String upToken = auth.uploadToken(bucketName);
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            // 判断是否是合法的文件后缀
            if (!isFileAllowed(fileExt)) {
                return "TypeError";
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            // 调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, upToken);
            // 打印返回的信息
            if (res.isOK() && res.isJson()) {
                // 返回这张存储照片的地址
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key");
            } else {
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            return null;
        }
    }
    // 图片允许的后缀扩展名
    private static String[] IMAGE_FILE_EXTD = new String[] { "png", "bmp", "jpg", "jpeg","pdf" };

    private static boolean isFileAllowed(String fileName) {
        for (String ext : IMAGE_FILE_EXTD) {
            if (ext.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    //删除文件
    public int deleteFileFromQiNiu(String fileName){
        //构造一个带指定Zone对象的配置类
        Configuration cfg =  new Configuration(Region.huadong());
        String key = fileName;
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            Response delete = bucketManager.delete(bucketName, key);
            return delete.statusCode;
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            ex.printStackTrace();
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
        //未查询到头像时，出异常是返回-1
        return -1;
    }
}
