package com.europa.smallTalk.im.msg.readWrite;

import com.europa.smallTalk.im.msg.AbstractMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;


/**
 * 读写
 * @version v1.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ReadOrWrite extends AbstractMsg implements Serializable {

    private static final long serialVersionUID = 6425940866173384434L;

    private String content;

}
