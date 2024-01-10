package com.lihong.csuftcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lihong.csuftcampus.model.domain.Tag;

import java.util.List;

/**
 * 针对表【tag(标签表)】的数据库操作Service
 */
public interface TagService extends IService<Tag> {
    List<Tag> getTagList(String tagName);

    boolean deleteTag(Long tagId);

    void addPostNum(Long tagId);
}
