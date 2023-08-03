package com.europa.smallTalk.im.msg;

import lombok.Getter;

/**
 * 设计通用code
 * @version 1.2
 */
@Getter
public enum Code {

    CONN(100),LOGIN(201),READ_WRITE(200),LOGOUT(500);
    private final Integer code;
    Code(Integer code) {
        this.code = code;
    }

    public Code findByCode(int code) {
        for (Code c : values()) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        return null;
    }
}
