package com.jx.blogap1.exception;



import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.qiniu.common.QiniuException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;


//对加了@Controller注解的方法进行拦截处理 AOP的实现
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)//进行异常处理，处理Exception.class的异常
    @ResponseBody//返回json数据
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    /**
     * 自定义异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(YyghException.class)
    @ResponseBody
    public Result error(YyghException e){
        return Result.build(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(QiniuException.class)
    @ResponseBody
    public Result error(QiniuException e){
        return Result.build(ResultCodeEnum.Qi_Niu_Exception.getCode(), ResultCodeEnum.Qi_Niu_Exception.getMessage());
    }

//    /**
//     * 处理Validated校验异常
//     * <p>
//     * 注: 常见的ConstraintViolationException异常， 也属于ValidationException异常
//     *
//     * @param e
//     *         捕获到的异常
//     * @return 返回给前端的data
//     */
//    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
//    public Result handleParameterVerificationException(Exception e) {
//        log.error(" handleParameterVerificationException has been invoked", e);
//
//        String msg = null;
//        /// BindException
//        if (e instanceof BindException) {
//            // getFieldError获取的是第一个不合法的参数(P.S.如果有多个参数不合法的话)
//
//
//            FieldError fieldError = ((BindException) e).getFieldError();
//            if (fieldError != null) {
//                msg = fieldError.getDefaultMessage();
//                log.error(msg);
//            }
//            /// MethodArgumentNotValidException
//        } else if (e instanceof ConstraintViolationException) {
//            /*
//             * ConstraintViolationException的e.getMessage()形如
//             *     {方法名}.{参数名}: {message}
//             *  这里只需要取后面的message即可
//             */
//            msg = e.getMessage();
//            if (msg != null) {
//                int lastIndex = msg.lastIndexOf(':');
//                if (lastIndex >= 0) {
//                    msg = msg.substring(lastIndex + 1).trim();
//                }
//            }
//            /// ValidationException 的其它子类异常
//        } else {
//            msg = "处理参数时异常";
//        }
//
//        return Result.build(ResultCodeEnum.DATA_ERROR.getCode(),msg);
//    }

}


