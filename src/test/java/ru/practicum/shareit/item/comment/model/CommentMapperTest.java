package ru.practicum.shareit.item.comment.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void fromComment_ShouldMapCommentToCommentResponseDto() {
        User author = new User();
        author.setName("Test User");

        Item item = new Item();
        item.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        CommentResponseDto result = commentMapper.fromComment(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(author.getName(), result.getAuthorName());
        assertEquals(item.getId(), result.getItemId());
        assertEquals(comment.getCreated(), result.getCreated());
    }

    @Test
    void toEntity_ShouldMapCommentDtoToComment() {
        CommentDto dto = new CommentDto();
        dto.setText("Test comment");

        User author = new User();
        author.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Comment result = commentMapper.toEntity(dto, author, item);

        assertNotNull(result);
        assertEquals(dto.getText(), result.getText());
        assertEquals(author, result.getAuthor());
        assertEquals(item, result.getItem());
    }
}