package com.lihong.csuftcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.mapper.TagMapper;
import com.lihong.csuftcampus.model.domain.PostTag;
import com.lihong.csuftcampus.model.domain.Tag;
import com.lihong.csuftcampus.service.PostTagService;
import com.lihong.csuftcampus.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {
    @Autowired
    PostTagService postTagService;


    /**
     * 获取标签列表
     *
     * @param tagName 标签名
     * @return tagList
     */
    @Override
    public List<Tag> getTagList(String tagName) {
        if (tagName == null) {
            return list();
        }
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(Tag::getTagName, tagName);
        List<Tag> tagList = list(queryWrapper);
        if (tagList == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return tagList;
    }

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @return boolean
     */
    @Transactional
    @Override
    public boolean deleteTag(Long tagId) {
        // 1. 校验参数是否合法
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 删除标签记录
        this.removeById(tagId);

        // 3. 删除标签帖子关联记录
        LambdaUpdateWrapper<PostTag> postTagQueryWrapper = new LambdaUpdateWrapper<>();
        postTagQueryWrapper.eq(PostTag::getTagId, tagId);
        boolean tagResult = postTagService.remove(postTagQueryWrapper);
        if (!tagResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 4. 返回结果
        return true;
    }

    /**
     * 添加标签引用次数
     *
     * @param tagId 标签
     */
    @Override
    public void addPostNum(Long tagId) {
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(Tag::getId, tagId);
        long count = this.count(tagLambdaQueryWrapper);
        if (count <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不存在");
        }
        LambdaUpdateWrapper<Tag> tagLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        tagLambdaUpdateWrapper
                .eq(Tag::getId, tagId)
                .setSql("postNum = postNum + 1");
        boolean update = this.update(tagLambdaUpdateWrapper);

        if (!update) {
            throw new BusinessException(ErrorCode.INSERT_ERROR);
        }
    }
}




