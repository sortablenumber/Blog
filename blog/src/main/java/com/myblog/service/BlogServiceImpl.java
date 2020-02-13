package com.myblog.service;

import com.myblog.NotFoundException;
import com.myblog.VO.BlogQuery;
import com.myblog.dao.BlogRepository;
import com.myblog.po.Blog;
import com.myblog.po.Type;
import com.myblog.util.MarkdownUtils;
import com.myblog.util.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service

public class BlogServiceImpl implements BlogService{
    @Autowired
    private BlogRepository repository;
    @Override
    public Blog getBlog(Long id) {
        return repository.findOne(id);
    }


    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return repository.findAll(new Specification<Blog>() {
            @Override //Root用于映射表中字段，CriteriaQuery是查询容器，用于放置条件
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if(!"".equals(blog.getTitle()) && blog.getTitle()!=null){
                    predicates.add(cb.like(root.<String>get("title"),"%"+blog.getTitle()+"%"));
                }
                if(blog.getTypeId() != null){
                    predicates.add(cb.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }
                if(blog.isRecommend()){
                    predicates.add(cb.equal(root.<Boolean>get("recommend"),blog.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, String query) {
        return repository.findByQuery(query,pageable);
    }

    @Override
    @Transactional  //由于新增和修改都是同一个方法，所以通过是否有id进行判断
    public Blog saveBlog(Blog blog) {
        if(blog.getId() == null){
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);    //初始化更新与操作时间，以及浏览次数
        } else{
            blog.setUpdateTime(new Date());
        }
        return repository.save(blog);
    }

    @Override
    @Transactional
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = repository.findOne(id);
        if(b == null){
            throw new NotFoundException("博客不存在");
        }
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return repository.save(b);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id) {
        repository.delete(id);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC,"updateTime");
        Pageable pageable = new PageRequest(0,size,sort);
        return repository.findTop(pageable);
    }

    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = repository.findOne(id);
        if(blog == null){
            throw new NotFoundException("该博客不存在");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog,b);
        String content = b.getContent();
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        return b;
    }

    @Override
    public Page<Blog> listBlog(Long tagId,Pageable pageable) {
        return repository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join = root.join("tags");
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }
}
