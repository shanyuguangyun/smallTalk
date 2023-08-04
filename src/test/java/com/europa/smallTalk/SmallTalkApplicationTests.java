package com.europa.smallTalk;

import com.europa.smallTalk.data.dao.UserDao;
import com.europa.smallTalk.data.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Optional;

@SpringBootTest
class SmallTalkApplicationTests {

    @Autowired
    private UserDao userDao;

    @Test
    void contextLoads() {
        Optional<User> userOp = userDao.findById(1);
        Assert.isTrue(userOp.isPresent(), "连接数据库失败");
    }

}
