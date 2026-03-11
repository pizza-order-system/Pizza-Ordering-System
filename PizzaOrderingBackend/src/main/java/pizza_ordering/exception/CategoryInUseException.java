package pizza_ordering.exception;

import org.springframework.http.HttpStatus;

public class CategoryInUseException extends ApiException {

    public CategoryInUseException(Long id) {
        super("Category " + id + " cannot be deleted because products exist",
                HttpStatus.BAD_REQUEST);
    }
}