package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.comment.CommentRequest;
import com.example.taskmanagement.dto.comment.CommentResponse;
import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-17T19:01:53+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Ubuntu)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment toEntity(CommentRequest request) {
        if ( request == null ) {
            return null;
        }

        Comment comment = new Comment();

        comment.setContent( request.getContent() );

        return comment;
    }

    @Override
    public CommentResponse toDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponse commentResponse = new CommentResponse();

        commentResponse.setId( comment.getId() );
        commentResponse.setContent( comment.getContent() );
        commentResponse.setAuthor( userToUserDto( comment.getAuthor() ) );
        commentResponse.setCreatedAt( comment.getCreatedAt() );

        return commentResponse;
    }

    protected CommentResponse.UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        CommentResponse.UserDto userDto = new CommentResponse.UserDto();

        userDto.setId( user.getId() );
        userDto.setEmail( user.getEmail() );
        userDto.setFirstName( user.getFirstName() );
        userDto.setLastName( user.getLastName() );

        return userDto;
    }
}
