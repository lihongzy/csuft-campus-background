package com.lihong.csuftcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lihong.csuftcampus.model.domain.Users;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.model.request.UserAppendRequest;
import com.lihong.csuftcampus.model.request.UserLoginRequest;
import com.lihong.csuftcampus.model.request.UserRegisterRequest;
import com.lihong.csuftcampus.model.request.UserUpdateRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface UsersService extends IService<Users> {

    String userLogin(UserLoginRequest loginRequest);

    Boolean userLogout(HttpServletRequest request);

    Boolean userRegister(UserRegisterRequest registerRequest);

    UserDTO getCurrentUser();

    List<Users> searchUsers(String username);

    Boolean appendUser(UserAppendRequest userAppendRequest);

    Boolean updateUser(Long id, UserUpdateRequest updateUser, HttpServletRequest request);

    boolean isAdmin();

    Users getSafetyUser(Users originUser);


}
