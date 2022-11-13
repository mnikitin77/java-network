package lesson3.exception;

public class InvalidLoginException extends RuntimeException{

    public InvalidLoginException() {}

    public InvalidLoginException(String message) {
        super(message);
    }
};
