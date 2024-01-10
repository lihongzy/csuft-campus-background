package com.lihong.csuftcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lihong.csuftcampus.model.domain.Follow;
import com.lihong.csuftcampus.model.dto.UserDTO;

import java.util.List;

/**
 * 针对表【follow(关注表)】的数据库操作Service
 */
public interface FollowService extends IService<Follow> {

    void followUser(Long followUserId);

    List<UserDTO> listFans(Long followUserId);

    List<UserDTO> listMyFollow(Long userId);

}
