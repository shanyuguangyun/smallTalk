package com.europa.smallTalk.im.msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import static com.europa.smallTalk.im.msg.Msg.Code.*;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description 消息内容
 * @version 1.1
 **/
@Slf4j
@Data
public class Msg implements Serializable {

    private static final long serialVersionUID = 8604864936279217593L;

    public static ObjectMapper objectMapper = new ObjectMapper();

    private Integer code;

    private String from;

    private String to;

    private String content;

    public Msg() {
    }

    public Msg(Integer code) {
        this.code = code;
    }

    public Msg(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public Msg(Integer code, String from, String to, String content) {
        this(from, to, content);
        this.code = code;
    }

    public static Msg connect(String from, String to) {
        return new Msg(CONN, from, to, "");
    }

    public static Msg stop(String from, String to) {
        return new Msg(DIS_CONN, from, to, "");
    }

    public String msg(String msg) {
        this.content = msg;
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("消息写入错误", e);
        }
        return "{}";
    }

    public static Msg toMsg(String msg) {
        try {
            return objectMapper.readValue(msg, Msg.class);
        } catch (JsonProcessingException e) {
            log.error("消息读取错误", e);
        }
        return null;
    }

    public static class Code {
        public static Integer CONN = 100;
        public static Integer SEND_MSG = 200;

        public static Integer LOGIN = 201;
        public static Integer DIS_CONN = 500;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "code=" + code +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
