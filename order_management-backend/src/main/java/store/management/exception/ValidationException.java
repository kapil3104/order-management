package store.management.exception;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ValidationException extends Exception implements StoreException{
    private String displayMessageKey;
    private String errorCode;
    private String[] params;
    private Map<String, List<String>> errorContext = new HashMap<>();

    public ValidationException(String message) {
        super(message, null);
    }

    @Override
    public Exception getException() {
        return this;
    }

    @Override
    public String getDisplayMessageKey() {
        return displayMessageKey;
    }

    @Override
    public String getDisplayMessage() {
        return null;
    }
}
