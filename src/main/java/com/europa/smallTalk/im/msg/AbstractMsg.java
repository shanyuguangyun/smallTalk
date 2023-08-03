package com.europa.smallTalk.im.msg;

import lombok.Data;

import java.io.Serializable;

/**
 * EnhanceMsg
 * 想象用户可能除了对服务器端进行数据传输并非仅仅对话，
 * 用户登录，用户下线，以及后续可能的其他操作
 * 所以code还是用于做操作凭证，而from和to改为用户主键。
 * content的内容由于用户登录等其他操作除了id外，可能还需要携带其他数据，考虑设计为json字符串还是说使用Object，或者说使用字节存储。
 * 我暂时设计成父子类，子类为具体的数据类型，继承自父类。父类有固定的from和to以及code。
 * @version 1.2
 */
@Data
public abstract class AbstractMsg implements Serializable {

    private static final long serialVersionUID = 7829600742874787583L;
    protected Integer code;

    protected Integer from;

    protected Integer to;

}
