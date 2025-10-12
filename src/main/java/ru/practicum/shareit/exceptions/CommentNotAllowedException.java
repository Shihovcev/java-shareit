package ru.practicum.shareit.exceptions;

public class CommentNotAllowedException extends RuntimeException {
  public CommentNotAllowedException(String message) {
    super(message);
  }
}
