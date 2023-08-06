package store.management.exception;

import java.util.List;
import java.util.Map;

public interface StoreException {

    String getErrorCode();

    String getDisplayMessageKey();

    String getMessage();

    String getDisplayMessage();

    Exception getException();

    Map<String, List<String>> getErrorContext();

    String[] getParams();
}

