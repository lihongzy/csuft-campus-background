package com.lihong.csuftcampus.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.mapper.PostCommentsMapper;
import com.lihong.csuftcampus.model.domain.CommentThumbs;
import com.lihong.csuftcampus.model.domain.Post;
import com.lihong.csuftcampus.model.domain.PostComments;
import com.lihong.csuftcampus.model.domain.Users;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.model.request.AddCommentRequest;
import com.lihong.csuftcampus.model.vo.PostCommentsVO;
import com.lihong.csuftcampus.service.CommentThumbsService;
import com.lihong.csuftcampus.service.PostCommentsService;
import com.lihong.csuftcampus.service.PostService;
import com.lihong.csuftcampus.service.UsersService;
import com.lihong.csuftcampus.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments>
        implements PostCommentsService {

    @Autowired
    private PostService postService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private CommentThumbsService commentThumbsService;

    /**
     * 发布评论
     *
     * @param addCommentRequest 发布评论封装类
     * @return true
     */
    @Transactional
    @Override
    public boolean addComment(AddCommentRequest addCommentRequest) {
        // 1. 从线程中获取当前用户
        UserDTO user = UserHolder.getUser();
        // 校验帖子ID是否合法
        Long postId = addCommentRequest.getPostId();
        if (postId == null || postId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子信息错误");
        }
        // 检查帖子是否非空
        Post postById = postService.getById(postId);
        if (postById == null) {
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR);
        }
        // 检查评论内容
        String content = addCommentRequest.getContent();
        if (postService.containsProfanity(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请文明发言");
        }
        if (StrUtil.hasBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容为空");
        }

        // 更新评论信息
        PostComments postComments = new PostComments();
        postComments.setPostId(postId);
        postComments.setContent(content);
        postComments.setUserId(user.getId());
        this.save(postComments);

        // 更新帖子评论数
        postService.lambdaUpdate()
                .eq(Post::getId, postId)
                .setSql("commentsNum = commentsNum + 1")
                .update();

        return true;
    }

    /**
     * 获取帖子评论列表
     *
     * @param postId 帖子ID
     * @return postCommentsVO列表
     */
    @Transactional
    @Override
    public List<PostCommentsVO> getListComments(Long postId) {
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<PostComments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(PostComments::getPostId, postId);
        List<PostComments> postComments = list(queryWrapper);

        return postComments.stream().map((comment) -> {
            PostCommentsVO postCommentsVO = new PostCommentsVO();
            BeanUtils.copyProperties(comment, postCommentsVO);

            Users user = usersService.getById(comment.getUserId());
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            postCommentsVO.setCommentUser(userDTO);

            //配置是否点赞
            Long currentUserId = UserHolder.getUser().getId();
            LambdaQueryWrapper<CommentThumbs> commentThumbsQueryWrapper = new LambdaQueryWrapper<>();
            commentThumbsQueryWrapper
                    .eq(CommentThumbs::getUserId, currentUserId)
                    .eq(CommentThumbs::getCommentId, comment.getId());
            long count = commentThumbsService.count(commentThumbsQueryWrapper);
            postCommentsVO.setHasThumb(count > 0);

            return postCommentsVO;
        }).collect(Collectors.toList());
    }

    /**
     * 评论点赞
     *
     * @param userId    用户Id
     * @param commentId 评论Id
     * @return 1点赞 -1取消点赞
     */
    @Override
    public int thumbComment(Long userId, Long commentId) {
        //判断评论是否存在
        long postCommentsCount = this.lambdaQuery()
                .eq(PostComments::getId, commentId)
                .count();
        if (postCommentsCount <= 0) {
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR, "评论不存在");
        }

        //判断是否已点赞
        LambdaUpdateWrapper<CommentThumbs> commentThumbsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        commentThumbsLambdaUpdateWrapper
                .eq(CommentThumbs::getUserId, userId)
                .eq(CommentThumbs::getCommentId, commentId);

        long commentThumbsCount = commentThumbsService.count(commentThumbsLambdaUpdateWrapper);

        //已点赞
        if (commentThumbsCount > 0) {
            boolean result = commentThumbsService.remove(commentThumbsLambdaUpdateWrapper);
            if (result) {
                //帖子点赞数量-1
                this.lambdaUpdate()
                        .eq(PostComments::getId, commentId)
                        .setSql("thumbNum = thumbNum - 1")
                        .update();
                return -1;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            //未点赞
            CommentThumbs commentThumbs = new CommentThumbs();
            commentThumbs.setCommentId(commentId);
            commentThumbs.setUserId(userId);

            boolean result = commentThumbsService.save(commentThumbs);
            if (result) {
                //帖子点赞数量+1
                this.lambdaUpdate()
                        .eq(PostComments::getId, commentId)
                        .setSql("thumbNum = thumbNum + 1")
                        .update();
                return 1;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

}

