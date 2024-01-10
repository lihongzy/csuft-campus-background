package com.lihong.csuftcampus.controller;

import com.lihong.csuftcampus.common.BaseResponse;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.common.ResultUtil;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.service.FollowService;
import com.lihong.csuftcampus.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关注模块
 */
@RequestMapping("/follow")
@RestController
@Slf4j
public class FollowController {
    /**
     * 关注服务
     */
    @Autowired
    private FollowService followService;


    /**
     * 关注用户
     *
     * @param followUserId 关注用户Id
     * @return ok
     */
    @PostMapping("/{followUserId}")
    public BaseResponse<String> followUser(@PathVariable Long followUserId) {
        if (followUserId == null || followUserId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        followService.followUser(followUserId);
        return ResultUtil.success("ok");
    }

    /**
     * 获取我的粉丝
     *
     * @return {@link BaseResponse}<{@link List}<{@link UserDTO}>>
     */
    @GetMapping("/fans")
    public BaseResponse<List<UserDTO>> listFans() {
        UserDTO user = UserHolder.getUser();
        List<UserDTO> userDTOList = followService.listFans(user.getId());
        return ResultUtil.success(userDTOList);
    }


    /**
     * 获取我的关注
     *
     * @return {@link BaseResponse}<{@link List}<{@link UserDTO}>>
     */
    @GetMapping("/my")
    public BaseResponse<List<UserDTO>> listMyFollow() {
        UserDTO user = UserHolder.getUser();

        List<UserDTO> userDTOList = followService.listMyFollow(user.getId());
        return ResultUtil.success(userDTOList);
    }
}
