package com.europa.smallTalk.im.msg.logout;

import com.europa.smallTalk.im.msg.AbstractMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 登出
 * @version v1.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Logout extends AbstractMsg implements Serializable {
    private static final long serialVersionUID = 830519749053692016L;
}
