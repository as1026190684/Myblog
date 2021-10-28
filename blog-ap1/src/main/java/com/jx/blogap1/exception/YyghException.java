package com.jx.blogap1.exception;


import com.jx.blogap1.result.ResultCodeEnum;

import lombok.Data;

/**
 * 自定义全局异常类
 *
 * @author qy
 */
//todo 异常类的作用
@Data
public class YyghException extends RuntimeException {

    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public YyghException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public YyghException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "YyghException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
