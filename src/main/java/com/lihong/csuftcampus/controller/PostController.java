package com.lihong.csuftcampus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lihong.csuftcampus.annotation.AuthCheck;
import com.lihong.csuftcampus.common.BaseResponse;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.common.ResultUtil;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.model.domain.*;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.model.request.PostAddRequest;
import com.lihong.csuftcampus.model.request.PostDoThumbRequest;
import com.lihong.csuftcampus.service.*;
import com.lihong.csuftcampus.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.*;

import static com.lihong.csuftcampus.constant.UserConstant.ADMIN_ROLE;

/**
 * 帖子模块
 */
@RequestMapping("/post")
@RestController
@Slf4j
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PostThumbService postThumbService;
    @Autowired
    private PostTagService postTagService;

    /**
     * IO 型线程池
     */
    private final ExecutorService ioExecutorService = new ThreadPoolExecutor(4, 20, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 发布帖子
     *
     * @param postAddRequest 帖子对象
     * @return postId
     */
    @PostMapping("/add")
    @Transactional
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest) {
        //校验合法性
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //新增帖子
        Long postId = postService.addPost(postAddRequest);

        //获取标签
        List<Tag> tags = postAddRequest.getTags();

        //添加标签帖子关联
        for (Tag tag : tags) {
            Long tagId = tag.getId();
            // 进行标签的处理逻辑
            postTagService.addPostTag(tagId, postId);
        }

        //添加标签引用次数
        for (Tag tag : tags) {
            Long tagId = tag.getId();
            tagService.addPostNum(tagId);
        }

        //返回结果
        return ResultUtil.success(postId);
    }

    /**
     * 管理员 查询帖子信息
     *
     * @param userId 用户ID
     * @return 帖子列表
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<List<Post>> adminListPost(@RequestParam(required = false) String userId) {
        // 1. 判断是否根据用户ID搜索帖子
        if (userId == null) {
            // 1.1 返回所有帖子
            List<Post> list = postService.list();
            return ResultUtil.success(list);
        }
        // 2. 根据用户ID搜索帖子
        Long Id = Long.valueOf(userId);
        List<Post> list = postService.searchPosts(Id);
        return ResultUtil.success(list);
    }

    /**
     * 获取通过审核的帖子
     *
     * @return {@link BaseResponse}<{@link List}<{@link Post}>>
     */
    @GetMapping("/listByUser")
    public BaseResponse<List<Post>> userListPost() {
        //获取通过审核的帖子
        List<Post> postList = postService.listByUser();
        return ResultUtil.success(postList);
    }

    /**
     * 删除帖子(本人和管理员)
     *
     * @param postId 帖子ID
     */
    @DeleteMapping("/delete/{postId}")
    public BaseResponse<Boolean> deletePostByAdmin(@PathVariable Long postId) {
        // 校验参数非空
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 检查权限-仅本人和管理员可删除
        // 2. 获取当前用户
        UserDTO user = UserHolder.getUser();
        // 3. 判断删除的帖子是否存在
        Post oldPost = postService.getById(postId);
        if (oldPost == null) {
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR);
        }
        // 4. 仅本人或管理员可删除
        if (!(oldPost.getUserId().equals(user.getId()) || usersService.isAdmin())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        boolean deleteResult = postService.removeById(postId);
        if (!deleteResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        // 5. 异步删除点赞信息，和标签关联信息
        CompletableFuture.runAsync(() -> {
            LambdaQueryWrapper<PostThumb> postThumbQueryWrapper = new LambdaQueryWrapper<>();
            postThumbQueryWrapper
                    .eq(PostThumb::getPostId, postId);
            boolean thumbResult = postThumbService.remove(postThumbQueryWrapper);


            LambdaQueryWrapper<PostTag> postTagQueryWrapper = new LambdaQueryWrapper<>();
            postTagQueryWrapper
                    .eq(PostTag::getPostId, postId);
            boolean tagResult = postTagService.remove(postTagQueryWrapper);

            if (!thumbResult) {
                log.error("postThumb delete failed, postId = {}", postId);
            } else if (!tagResult) {
                log.error("postTag delete failed, postId = {}", postId);
            }
        }, ioExecutorService);


        return ResultUtil.success(null);
    }

    /**
     * 更新审核帖子状态
     *
     * @param post 修改的审核状态
     */
    @PutMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Object> updatePost(@RequestBody Post post) {
        // 1. 校验合法性
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (post.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 判断帖子是否存在
        Post oldPost = postService.getById(post.getId());
        if (oldPost == null) {
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR);
        }
        // 3. 更新帖子
        boolean result = postService.updateById(post);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(null);
    }

    /**
     * 根据发帖用户ID搜索帖子
     *
     * @param id 发帖用户ID
     * @return {@link BaseResponse}<{@link List}<{@link Post}>>
     */
    @GetMapping("/search/{id}")
    public BaseResponse<List<Post>> searchPostsByUser(@PathVariable String id) {
        //搜索帖子
        long userId = Long.parseLong(id);
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Post> list = postService.searchPosts(userId);
        return ResultUtil.success(list);
    }

    /**
     * 获取帖子详细信息
     *
     * @param postId 帖子ID
     * @return postDetail
     */
    @GetMapping("/detail/{postId}")
    public BaseResponse<Post> getPostDetail(@PathVariable Long postId) {
        if (postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //封装用户 标签列表 当前用户是否点赞
        Post postDetail = postService.getById(postId);

        Users user = usersService.getById(postDetail.getUserId());
        Users safetyUser = usersService.getSafetyUser(user);
        postDetail.setUser(safetyUser);

        List<Tag> tagsList = postTagService.searchByPostId(postId);
        postDetail.setTagList(tagsList);

        Long currentUserId = UserHolder.getUser().getId();
        Boolean isThumb = postThumbService.isThumb(currentUserId, postId);
        postDetail.setHasThumb(isThumb);

        return ResultUtil.success(postDetail);
    }

    /**
     * 点赞/取消点赞
     *
     * @param postDoThumbRequest 点赞请求对象
     * @return 1点赞成功 -1取消点赞
     */
    @PostMapping("/thumb")
    public BaseResponse<Long> postDoThumb(@RequestBody PostDoThumbRequest postDoThumbRequest) {
        // 1. 判断请求非空
        if (postDoThumbRequest == null || postDoThumbRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = UserHolder.getUser().getId();
        Long postId = postDoThumbRequest.getPostId();

        long result = postThumbService.doThumb(userId, postId);

        return ResultUtil.success(result);
    }

}
