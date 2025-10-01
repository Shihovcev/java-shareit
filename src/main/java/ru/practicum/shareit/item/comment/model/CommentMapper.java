package ru.practicum.shareit.item.comment.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class CommentMapper {

    public CommentResponseDto fromComment(Comment comment) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(comment.getId());
        commentResponseDto.setText(comment.getText());
        commentResponseDto.setAuthorName(comment.getAuthor().getName());
        commentResponseDto.setItemId(comment.getItem().getId());
        commentResponseDto.setCreated(comment.getCreated());
        return commentResponseDto;
    }

    public Comment toEntity(CommentDto dto, User author, Item item) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        return comment;
    }
}