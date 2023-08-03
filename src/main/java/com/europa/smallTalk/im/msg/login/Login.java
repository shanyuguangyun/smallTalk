package com.europa.smallTalk.im.msg.login;

import com.europa.smallTalk.im.msg.AbstractMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 登录
 * @version v1.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Login extends AbstractMsg implements Serializable {

    private static final long serialVersionUID = 612866537250758112L;

    /** 用户密码 **/
    private String password;

    /** 验证码 **/
    private String captcha;

}
