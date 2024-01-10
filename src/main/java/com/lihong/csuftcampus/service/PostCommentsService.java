package com.lihong.csuftcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lihong.csuftcampus.model.domain.PostComments;
import com.lihong.csuftcampus.model.request.AddCommentRequest;
import com.lihong.csuftcampus.model.vo.PostCommentsVO;

import java.util.List;

/**
 * 针对表【post_comments(帖子评论表)】的数据库操作Service
 */
public interface PostCommentsService extends IService<PostComments> {

    boolean addComment(AddCommentRequest addCommentRequest);

    List<PostCommentsVO> getListComments(Long postId);

    int thumbComment(Long userId, Long commentId);
}
