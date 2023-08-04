package com.europa.smallTalk.im.msg;

import com.europa.smallTalk.im.msg.enums.ResponseCode;
import lombok.Data;
import org.springframework.util.ReflectionUtils;

import java.io.DataInputStream;
import java.io.Serializable;

/**
 * @author fengwen
 * @date 2023/8/4
 * @description 服务器响应
 * @version 1.2
 **/
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 4139508047838814984L;

    private Integer code;

    private String message;

    private Class<?> dataType;

    private T data;

    public Result() {
    }

    public Result(ResponseCode code, T data) {
        this(code.getCode(), code.getMessage(), data);
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        if (data != null) {
            this.dataType = data.getClass();
        }
    }

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ResponseCode.SUCCEED, data);
    }

    public static <T> Result<T> fail(ResponseCode responseCode) {
        return new Result<>(responseCode.getCode(), responseCode.getMessage());
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message);
    }

}
