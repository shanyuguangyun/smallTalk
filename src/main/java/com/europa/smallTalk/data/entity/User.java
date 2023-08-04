package com.europa.smallTalk.data.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 用户
 * @version 1.2
 */
@Data
@Entity(name = "im_user")
public class User implements Serializable {

    private static final long serialVersionUID = 5613140009155117727L;
    /** 主键 **/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /** 名称 **/
    private String name;

    /** 1.男 0.女 -1.未知 **/
    private Integer gender;

}
