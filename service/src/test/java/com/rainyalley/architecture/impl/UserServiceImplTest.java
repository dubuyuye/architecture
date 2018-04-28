package com.rainyalley.architecture.impl;

import com.rainyalley.architecture.config.ServiceConfig;
import com.rainyalley.architecture.model.User;
import com.rainyalley.architecture.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ServiceConfig.class})
public class UserServiceImplTest{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserService userService;

    @Test
    @Rollback
    @Transactional
    public void save() throws Exception {
        User user = new User();
        user.setName("hello");
        user.setPassword("world");
        user = userService.save(user);

        User userAfter = userService.get(user.getId());
        Assert.assertNotNull(userAfter);
    }

    @Test
    @Rollback
    @Transactional
    public void get() throws Exception {
        User user = new User();
        user.setName("hello");
        user.setPassword("world");
        //user = userService.save(user);
        user = userService.save(user);
        User entity =  userService.get(user.getId());
        Assert.assertEquals(entity, user);
    }

}