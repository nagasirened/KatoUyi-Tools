package com.katouyi.tools.mongo.service;


import com.katouyi.tools.mongo.Comment;
import com.katouyi.tools.mongo.dao.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public String save(Comment comment) {
        // Comment result = commentRepository.save(comment);
        Comment result = commentRepository.insert(comment);
        return result.getId();
    }

    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    public void deleteBatchByParam(Comment comment) {
        commentRepository.delete(comment);
    }

    public void deleteBatch(List<String> ids) {
        List<Comment> commentList = ids.stream().map(item -> {
            Comment comment = new Comment();
            comment.setId(item);
            return comment;
        }).collect(Collectors.toList());
        commentRepository.deleteAll(commentList);
    }

    public String update(Comment comment) {
        Comment result = commentRepository.save(comment);
        return result.getId();
    }

    public Comment getById(String id) {
        return commentRepository.findById(id).orElse(new Comment());
    }

    public List<Comment> findAllByParam(Comment comment) {
        /*PageRequest pageRequest = PageRequest.of(1, 10, Sort.by("id"));
        Page<Comment> pageInfo = commentRepository.findAll(pageRequest);*/
        List<Comment> commentList = commentRepository.findAll(Example.of(comment));
        return commentList;
    }

    /**
     * 根据上级评论ID，分页查询回复列表
     */
    public Page<Comment> findByParentId(String parentId, int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.DESC);
        /*Comment comment = new Comment();
        comment.setParentId(parentId);
        return commentRepository.findAll(Example.of(comment), pageRequest);*/
        return commentRepository.findByParentId(parentId, pageRequest);
    }

}
