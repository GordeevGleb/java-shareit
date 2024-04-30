package ru.practicum.shareit.exception;

public class IncorrectUserOperationException extends RuntimeException{
    public IncorrectUserOperationException(String message) {
        super(message);
    }
}
