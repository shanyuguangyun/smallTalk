package com.europa.smallTalk.im.msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static com.europa.smallTalk.im.msg.Msg.Code.CONN;
import static com.europa.smallTalk.im.msg.Msg.Code.DIS_CONN;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description 消息内容
 **/
@Slf4j
@Data
public class Msg {

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
        public static Integer DIS_CONN = 500;
    }
}
