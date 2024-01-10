package com.lihong.csuftcampus.controller;

import com.lihong.csuftcampus.annotation.AuthCheck;
import com.lihong.csuftcampus.common.BaseResponse;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.common.ResultUtil;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.model.domain.Users;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.model.request.*;
import com.lihong.csuftcampus.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.lihong.csuftcampus.constant.UserConstant.ADMIN_ROLE;

/**
 * 用户模块
 */

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {
    @Autowired
    private UsersService usersService;

    /**
     * 用户登录
     *
     * @param loginRequest 用户登录封装类
     * @return token
     */
    @PostMapping("/login")
    public BaseResponse<String> userLogin(@RequestBody UserLoginRequest loginRequest) {
        String token = usersService.userLogin(loginRequest);
        return ResultUtil.success(token);
    }


    /**
     * 用户注册
     *
     * @param registerRequest 用户注册封装类
     */
    @PostMapping("/register")
    public BaseResponse<Object> userRegister(@RequestBody UserRegisterRequest registerRequest) {
        Boolean result = usersService.userRegister(registerRequest);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(null);
    }

    /**
     * 用户退出
     *
     * @param request HTTP请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/logout")
    public BaseResponse<Object> userLogout(HttpServletRequest request) {
        Boolean result = usersService.userLogout(request);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(null);
    }

    /**
     * 获取当前用户登录态
     *
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public BaseResponse<UserDTO> getCurrentUser() {
        // 获取当前用户登录态
        UserDTO safetyUser = usersService.getCurrentUser();
        return ResultUtil.success(safetyUser);
    }

    /**
     * 搜索用户信息（如果 username为空，则搜索全部信息）
     *
     * @param username 姓名
     * @return 用户信息
     */
    @GetMapping("/search")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<List<Users>> adminSearchUsers(@RequestParam(required = false) String username) {
        List<Users> list = usersService.searchUsers(username);
        return ResultUtil.success(list);
    }

    /**
     * 管理员 新增用户
     *
     * @param userAppendRequest 新增用户类
     */
    @PostMapping("/append")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Object> adminAddUser(@RequestBody UserAppendRequest userAppendRequest) {
        Boolean result = usersService.appendUser(userAppendRequest);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(null);
    }

    /**
     * 管理员 删除用户
     *
     * @param deleteRequest 删除用户封装类
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Object> adminDeleteUser(@RequestBody UserDeleteRequest deleteRequest) {
        // 检查参数是否为空
        Long id = deleteRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = usersService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(null);
    }

    /**
     * 更新当前用户信息
     *
     * @param id         用户ID
     * @param updateUser 用户更新信息
     * @param request    HTTP请求
     */
    @PutMapping("/update/{id}")
    public BaseResponse<Object> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest updateUser, HttpServletRequest request) {
        Boolean result = usersService.updateUser(id, updateUser, request);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(null);
    }

}
