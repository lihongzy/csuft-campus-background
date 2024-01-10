package com.lihong.csuftcampus.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lihong.csuftcampus.annotation.AuthCheck;
import com.lihong.csuftcampus.common.BaseResponse;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.common.ResultUtil;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.model.domain.Tag;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.model.request.ChangeColorRequest;
import com.lihong.csuftcampus.model.request.DeleteRequest;
import com.lihong.csuftcampus.service.TagService;
import com.lihong.csuftcampus.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lihong.csuftcampus.constant.UserConstant.ADMIN_ROLE;

/**
 * 标签模块
 */
@RequestMapping("/tag")
@RestController
@Slf4j
public class TagController {
    @Autowired
    private TagService tagService;


    /**
     * 获取所有标签
     *
     * @param tagName 标签名
     * @return List<Tag>
     */
    @GetMapping("/list")
    public BaseResponse<List<Tag>> getTagList(@RequestParam(required = false) String tagName) {
        //判断是否根据标签名搜索
        List<Tag> list = tagService.getTagList(tagName);
        return ResultUtil.success(list);
    }


    /**
     * 创建标签
     *
     * @param tag 标签内容
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Object> addTag(@RequestBody Tag tag) {
        String tagName = tag.getTagName();
        String tagColor = tag.getTagColor();
        if (StrUtil.hasBlank(tagName, tagColor)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 获取用户登录态
        UserDTO user = UserHolder.getUser();
        tag.setUserId(user.getId());

        boolean result = tagService.save(tag);
        if (!result) {
            throw new BusinessException(ErrorCode.INSERT_ERROR);
        }

        return ResultUtil.success(null);
    }

    /**
     * 删除标签
     *
     * @param deleteRequest 删除标签ID
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Object> deleteTag(@RequestBody DeleteRequest deleteRequest) {
        // 1. 判断请求是否合法
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 获取标签ID
        Long tagId = deleteRequest.getId();
        boolean result = tagService.deleteTag(tagId);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(null);
    }

    /**
     * 更换颜色
     *
     * @param changeColorRequest 切换颜色封装类
     */
    @Transactional
    @PostMapping("/changeColor")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Object> changeColor(@RequestBody ChangeColorRequest changeColorRequest) {
        if (changeColorRequest == null || changeColorRequest.getTagId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper
                .eq(Tag::getId, changeColorRequest.getTagId());
        long count = tagService.count(tagLambdaQueryWrapper);
        if (count <= 0) {
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR, "标签不存在");
        }

        LambdaUpdateWrapper<Tag> tagLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        tagLambdaUpdateWrapper
                .eq(Tag::getId, changeColorRequest.getTagId())
                .set(Tag::getTagColor, changeColorRequest.getTagColor());
        boolean update = tagService.update(tagLambdaUpdateWrapper);

        if (!update) {
            throw new BusinessException(ErrorCode.INSERT_ERROR);
        }
        return ResultUtil.success(null);
    }

}
