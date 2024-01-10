package com.lihong.csuftcampus.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.mapper.PostMapper;
import com.lihong.csuftcampus.model.domain.Post;
import com.lihong.csuftcampus.model.domain.Tag;
import com.lihong.csuftcampus.model.domain.Users;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.model.request.PostAddRequest;
import com.lihong.csuftcampus.service.PostService;
import com.lihong.csuftcampus.service.PostTagService;
import com.lihong.csuftcampus.service.PostThumbService;
import com.lihong.csuftcampus.service.UsersService;
import com.lihong.csuftcampus.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 针对表【post(帖子表)】的数据库操作Service实现
 */
@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    @Autowired
    private UsersService usersService;
    @Autowired
    private PostTagService postTagService;
    @Autowired
    private PostThumbService postThumbService;

    /**
     * 不雅词汇数组
     */
    private static final String[] PROFANITY_WORDS = {
            "操你妈",
            "滚你妈",
            "吃屎",
            "去死",
            "鸡巴",
    };

    /**
     * 发布帖子
     *
     * @param postAddRequest 帖子信息
     * @return 帖子主键id
     */
    @Override
    public Long addPost(PostAddRequest postAddRequest) {
        UserDTO user = UserHolder.getUser();
        // 获取帖子内容和发布用户ID
        Post post = new Post();
        post.setContent(postAddRequest.getContent());
        post.setUserId(user.getId());

        // 检查帖子是否合法
        validPost(post);

        boolean saveResult = this.save(post);

        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子信息保存失败");
        }

        return post.getId();
    }

    /**
     * 查询用户帖子（仅管理员）
     *
     * @param userId 用户ID
     * @return postList
     */
    @Override
    public List<Post> searchPosts(Long userId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(Post::getUserId, userId);

        List<Post> postList = list(queryWrapper);
        if (postList == null) {
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR);
        }
        return postList;
    }

    /**
     * 查询通过审核的帖子
     *
     * @return 帖子列表
     */
    @Override
    public List<Post> listByUser() {
        // 只显示通过审核的
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Post::getReviewStatus, 1)
                .orderByDesc(Post::getCreateTime);
        List<Post> postList = list(queryWrapper);

        postList.forEach(post -> {
            Users user = usersService.getById(post.getUserId());
            Users safetyUser = usersService.getSafetyUser(user);
            post.setUser(safetyUser);

            List<Tag> tagList = postTagService.searchByPostId(post.getId());
            post.setTagList(tagList);

            Long userId = UserHolder.getUser().getId();
            post.setHasThumb(postThumbService.isThumb(userId, post.getId()));
        });

        return postList;
    }

    /**
     * 检查帖子是否合法
     *
     * @param post 帖子
     */
    @Override
    public void validPost(Post post) {
        String content = post.getContent();

        if (StrUtil.hasBlank(post.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子内容不能为空");
        }
        if (content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子内容过长");
        }
        if (containsProfanity(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请文明发言");
        }
    }

    /**
     * 检测是否包含不文明词汇
     *
     * @param content 帖子内容
     * @return 是否包含非法内容
     */
    @Override
    public boolean containsProfanity(String content) {
        for (String word : PROFANITY_WORDS) {
            if (content.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }
}




