package pizza_ordering.exception;

import org.springframework.http.HttpStatus;

public class OutOfStockException extends ApiException{
    public OutOfStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
