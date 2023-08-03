package com.europa.smallTalk.data.dao;

import com.europa.smallTalk.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @version 1.2
 */
@Repository("UserDao")
public interface UserDao extends JpaRepository<User, Integer> {

}
