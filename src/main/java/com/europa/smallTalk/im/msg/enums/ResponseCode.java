package com.europa.smallTalk.im.msg.enums;

import lombok.Getter;

/**
 * @author fengwen
 * @date 2023/8/4
 * @description 响应状态码
 * @version 1.2
 **/
@Getter
public enum ResponseCode {

    SUCCEED(200, "SUCCEED"),
    LOGOUT(201, "LOGOUT SUCCEED"),
    UN_KNOW_REQUEST(404, "UN_KNOW REQUEST"),
    SERVER_ERROR(500, "SERVER ERROR"),
    TIMEOUT(504, "TIMEOUT");

    private final Integer code;
    private final String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseCode findByCode(int code) {
        for (ResponseCode c : values()) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        return null;
    }
}
