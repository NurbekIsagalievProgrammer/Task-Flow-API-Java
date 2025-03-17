package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.comment.CommentRequest;
import com.example.taskmanagement.dto.comment.CommentResponse;
import com.example.taskmanagement.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentRequest request);

    CommentResponse toDto(Comment comment);
} 