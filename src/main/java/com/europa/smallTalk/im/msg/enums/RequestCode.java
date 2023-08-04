package com.europa.smallTalk.im.msg.enums;

import lombok.Getter;

/**
 * 设计通用code
 * @version 1.2
 */
@Getter
public enum RequestCode {

    CONN(100),LOGIN(201),READ_WRITE(200),LOGOUT(500);
    private final Integer code;
    RequestCode(Integer code) {
        this.code = code;
    }

    public static RequestCode findByCode(int code) {
        for (RequestCode c : values()) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        return null;
    }
}
