package com.lrm.service;

import com.lrm.VO.BlogQuery;
import com.lrm.po.Blog;
import com.lrm.po.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {
    Blog getBlog(Long id);
    Page<Blog> listBlog(Pageable pageable, BlogQuery blog);
    Page<Blog> listBlog(Pageable pageable, String query);
    Blog saveBlog(Blog blog);
    Blog updateBlog(Long id,Blog blog);
    void deleteBlog(Long id);
    Page<Blog> listBlog(Pageable pageable);
    List<Blog> listRecommendBlogTop(Integer size);
    Blog getAndConvert(Long id);
    Page<Blog> listBlog(Long tagId,Pageable pageable);
}
