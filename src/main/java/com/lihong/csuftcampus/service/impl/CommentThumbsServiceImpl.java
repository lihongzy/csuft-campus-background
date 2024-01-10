package com.lihong.csuftcampus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihong.csuftcampus.model.domain.CommentThumbs;
import com.lihong.csuftcampus.service.CommentThumbsService;
import com.lihong.csuftcampus.mapper.CommentThumbsMapper;
import org.springframework.stereotype.Service;

/**
 * 针对表【comment_thumbs(评论点赞表)】的数据库操作Service实现
 */
@Service
public class CommentThumbsServiceImpl extends ServiceImpl<CommentThumbsMapper, CommentThumbs>
        implements CommentThumbsService {

}




