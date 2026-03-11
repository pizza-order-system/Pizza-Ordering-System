package pizza_ordering.exception;

import org.springframework.http.HttpStatus;

public class InvalidStockException extends ApiException {

    public InvalidStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}