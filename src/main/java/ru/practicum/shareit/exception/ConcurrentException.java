package ru.practicum.shareit.exception;

public class ConcurrentException extends RuntimeException{
    public ConcurrentException(String message) {
        super(message);
    }
}
