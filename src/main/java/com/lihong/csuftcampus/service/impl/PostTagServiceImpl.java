package com.lihong.csuftcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.mapper.PostMapper;
import com.lihong.csuftcampus.mapper.PostTagMapper;
import com.lihong.csuftcampus.mapper.TagMapper;
import com.lihong.csuftcampus.model.domain.Post;
import com.lihong.csuftcampus.model.domain.PostTag;
import com.lihong.csuftcampus.model.domain.Tag;
import com.lihong.csuftcampus.service.PostTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【post_tag(帖子标签关联表)】的数据库操作Service实现
 */
@Service
@Slf4j
public class PostTagServiceImpl extends ServiceImpl<PostTagMapper, PostTag>
        implements PostTagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostTagMapper postTagMapper;

    @Override
    public void addPostTag(Long tagId, Long postId) {
        // 1. 校验参数合法性
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不存在");
        }
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
        }

        // 2. 插入参数
        PostTag postTag = new PostTag();
        postTag.setPostId(postId);
        postTag.setTagId(tagId);

        postTagMapper.insert(postTag);
    }

    @Override
    public List<Tag> searchByPostId(Long postId) {
        // 1. 根据 postId 查询与该帖子相关的标签记录
        List<PostTag> postTags = postTagMapper.selectList(
                new QueryWrapper<PostTag>().eq("postId", postId)
        );

        // 2. 提取与帖子关联的标签信息
        List<Long> tagIds = postTags.stream()
                .map(PostTag::getTagId)
                .collect(Collectors.toList());

        // 3. 根据标签 ID 查询标签信息
        if (!tagIds.isEmpty()) {
            return tagMapper.selectBatchIds(tagIds);
        }

        return Collections.emptyList();
    }
}




