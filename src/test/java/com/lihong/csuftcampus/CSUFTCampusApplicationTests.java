package com.lihong.csuftcampus;

import com.lihong.csuftcampus.mapper.UsersMapper;
import com.lihong.csuftcampus.model.domain.Users;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class CSUFTCampusApplicationTests {

    @Resource
    private UsersMapper usersMapper;

    @Test
    void contextLoads() {
        System.out.println("select from users:");
        List<Users> users = usersMapper.selectList(null);
        System.out.println(users);
    }

}
