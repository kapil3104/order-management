package store.management.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import store.management.exception.StoreException;

import java.util.*;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
public class ResponseEntityBuilder {
    public static final String SUCCESS = "success";
    public static final String STATUS = "status";
    public static final String DATA = "data";
    public static final String FAILED = "failed";
    public static final String ERROR_DETAILS = "errorDetails";
    public static final String KEY = "key";
    public static final String DISPLAY_MESSAGE = "displayMessage";
    public static final String DEBUG_MESSAGE = "debugMessage";
    public static final String CONTEXT = "context";
    public static final String PARAMS = "params";
    private static final boolean debugEnabled = debugEnabled();
    public static final String EXCEPTION_TYPE = "eType";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_KEY = "errorKey";
    public static final String NO_DISPLAY_ERROR = "noDisplayError";
    public static final String errorTypeStoreException = "1";
    public static final String errorTypeNonStoreExceptiom = "0";
    public static final String errorTypeNPE = "2";
    public static final String errorESIndexNotFoundName = "TIndexNotFoundException";

    private static boolean debugEnabled() {
        String cluster_type = System.getenv("CLUSTER_TYPE");
        return StringUtils.isNotBlank(cluster_type) && !cluster_type.toLowerCase().contains("prod");
    }

    public static ResponseEntity<Map<String, Object>> okResponseEntity(Object data) {
        Map<String, Object> response = Maps.newHashMap();
        response.put(STATUS, SUCCESS);
        response.put(DATA, data);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<Map<String, Object>> okCreatedResponseEntity(Object data) {
        Map<String, Object> response = Maps.newHashMap();
        response.put(STATUS, SUCCESS);
        response.put(DATA, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static ResponseEntity<Map<String, Object>> errorResponseEntity(Object errorDetails, HttpStatus httpStatus,
                                                                          HttpHeaders headers) {
        Map<String, Object> response = Maps.newHashMap();
        response.put(STATUS, FAILED);
        response.put(ERROR_DETAILS, errorDetails);
        return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON_UTF8).headers(headers)
                             .body(response);
    }

    public static ResponseEntity<Map<String, Object>> errorResponseEntity(Object errorDetails, HttpStatus httpStatus) {
        Map<String, Object> response = Maps.newHashMap();
        response.put(STATUS, FAILED);
        response.put(ERROR_DETAILS, errorDetails);
        return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);

    }

    public static ResponseEntity<Map<String, Object>> errorResponseEntity(Throwable t) {
        return errorResponseEntity(t, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<Map<String, Object>> errorResponseEntity(Throwable t, HttpStatus httpStatus) {
        if (errorESIndexNotFoundName.equalsIgnoreCase(t.getClass().getSimpleName())) {
            return errorResponseFromESIndexNotFound(t);
        }

        if (t instanceof StoreException) {
            return errorResponseFromException((StoreException) t, httpStatus);
        }

        Map<String, Object> errorDetails = createErrorDetails("unexpected.error", null, null, t.getMessage(), null,
                null);
        HttpHeaders headers = new HttpHeaders();
        addToHeader(headers, EXCEPTION_TYPE, errorTypeNonStoreExceptiom);
        if (t instanceof NullPointerException) {
            addToHeader(headers, EXCEPTION_TYPE, errorTypeNPE);
        }
        final ResponseEntity<Map<String, Object>> responseEntity = errorResponseEntity(errorDetails, httpStatus);
        return responseEntity;
    }

    public static ResponseEntity<Map<String, Object>> errorResponseFromException(StoreException exception) {
        return errorResponseFromException(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<Map<String, Object>> errorResponseFromException(StoreException exception,
                                                                                 HttpStatus defaultStatus) {

        HttpHeaders headers = createHeadersForStoreException(exception);
        Map<String, Object> response = Maps.newHashMap();
        response.put(STATUS, FAILED);
        String displayMessage = doGetDisplayMessage(exception);

        Map<String, Object> details = createErrorDetails(exception.getDisplayMessageKey(),
                displayMessage, exception.getErrorCode(), exception.getMessage(),
                exception.getErrorContext(), safeArrayToList(exception.getParams()));
        response.put(ERROR_DETAILS, details);
        addToHeader(headers, NO_DISPLAY_ERROR, String.valueOf(StringUtils.isBlank(displayMessage)));
        final ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.status(defaultStatus).headers(headers)
                                                                                 .contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
        return responseEntity;
    }


    private static HttpHeaders createHeadersForStoreException(StoreException exception) {
        HttpHeaders headers = new HttpHeaders();
        addToHeader(headers, EXCEPTION_TYPE, errorTypeStoreException);
        if (exception.getErrorCode() != null) {
            addToHeader(headers, ERROR_CODE, exception.getErrorCode());
        }
        if (exception.getDisplayMessageKey() != null) {
            addToHeader(headers, ERROR_KEY, exception.getDisplayMessageKey());
        } else {
            addToHeader(headers, ERROR_KEY, "nd");
        }
        return headers;
    }

    private static void addToHeader(HttpHeaders headers, String key, String value) {
        headers.put(key, Collections.singletonList(value));
    }


    public static Map<String, Object> createErrorDetails(String key, String displayMessage, String errorCode,
                                                         String debugMessage, Map<String, List<String>> context, List<String> params) {
        Map<String, Object> details = Maps.newHashMap();
        safePut(details, KEY, key);
        safePut(details, DISPLAY_MESSAGE, displayMessage);
        safePut(details, ERROR_CODE, errorCode);
        if (debugEnabled) {
            safePut(details, DEBUG_MESSAGE, debugMessage);
        }
        safePut(details, CONTEXT, context);
        safePut(details, PARAMS, params);
        return details;
    }

    private static boolean safePut(Map<String, Object> map, String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        if (value == null) {
            return false;
        }
        map.put(key, value);
        return true;
    }

    private static List<String> safeArrayToList(String[] array) {
        if (array == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(array);
    }

    public static String doGetDisplayMessage(StoreException exception) {
        String displayMessage = null;
        try {
            displayMessage = exception.getDisplayMessageKey();
        } catch (Throwable e) {
            log.error("Error while creating displayMessage");
        }
        return isNull(displayMessage) ? exception.getDisplayMessage() : displayMessage;
    }

    private static ResponseEntity<Map<String, Object>> errorResponseFromESIndexNotFound(Throwable t) {
        Map<String, Object> errorDetails = createErrorDetails("es.index.not.found", null, null, t.getMessage(), null,
                null);
        HttpHeaders headers = new HttpHeaders();
        addToHeader(headers, EXCEPTION_TYPE, errorTypeNonStoreExceptiom);
        final ResponseEntity<Map<String, Object>> responseEntity = errorResponseEntity(errorDetails, UNPROCESSABLE_ENTITY);
        return responseEntity;
    }
}
