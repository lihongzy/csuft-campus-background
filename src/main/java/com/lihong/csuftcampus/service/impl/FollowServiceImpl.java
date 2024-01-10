package com.lihong.csuftcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.mapper.FollowMapper;
import com.lihong.csuftcampus.model.domain.Follow;
import com.lihong.csuftcampus.model.domain.Users;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.service.FollowService;
import com.lihong.csuftcampus.service.UsersService;
import com.lihong.csuftcampus.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 针对表【follow(关注表)】的数据库操作Service实现
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
        implements FollowService {
    @Autowired
    private UsersService usersService;
    @Autowired
    private FollowMapper followMapper;


    @Transactional
    @Override
    public void followUser(Long followUserId) {
        // 1. 校验参数合法性
        Users followUser = usersService.lambdaQuery()
                .eq(Users::getId, followUserId)
                .getEntity();
        if (followUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 从线程中获取当前用户
        UserDTO user = UserHolder.getUser();

        Long userId = user.getId();
        // 修改数据库
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("followUserId", followUserId);
        Follow follow = getOne(queryWrapper);
        if (follow == null) {
            Follow followed = new Follow();
            followed.setFollowUserId(followUserId);
            followed.setUserId(userId);
            this.save(followed);
        } else {
            Integer isDelete = follow.getLogicDelete();
            if (isDelete == 0) {
                this.update().eq("id", follow.getId())
                        .set("logicDelete", 1).update();
            } else {
                this.update().eq("id", follow.getId())
                        .set("logicDelete", 0).update();
            }
        }
    }

    @Override
    public List<UserDTO> listFans(Long followUserId) {
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("followUserId", followUserId);
        List<Follow> list = this.list(queryWrapper);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<Users> usersList = list.stream().map((follow -> usersService.getById(follow.getUserId()))).filter(Objects::nonNull).collect(Collectors.toList());
        return usersList.stream().map((item) -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(item, userDTO);
            QueryWrapper<Follow> wrapper = new QueryWrapper<>();
            wrapper.eq("userId", followUserId).eq("followUserId", item.getId());
            long count = this.count(wrapper);
            userDTO.setIsFollow(count > 0);
            return userDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> listMyFollow(Long userId) {
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<Follow> list = this.list(queryWrapper);
        List<Users> usersList = list.stream().map((follow -> usersService.getById(follow.getFollowUserId()))).collect(Collectors.toList());

        return usersList.stream().map((user) -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            userDTO.setIsFollow(true);
            return userDTO;
        }).collect(Collectors.toList());
    }


}




