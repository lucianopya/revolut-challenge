package revolut.challenge.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;
    private Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR_500;

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(String code, String message, Integer statusCode) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
    }

    public ApiException(String code, String message, Integer statusCode, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.statusCode = statusCode;
    }

    public ApiException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String toJson() {
        Map<String, Object> exceptionMap = new LinkedHashMap<>();

        exceptionMap.put("error", this.code);
        exceptionMap.put("message", this.getMessage());
        exceptionMap.put("status", this.statusCode);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(exceptionMap);
        } catch (Exception exception) {
            return "{" +
                    "\"error\": " +
                    "\"" + this.code + "\", " +
                    "\"message\": " +
                    "\"" + this.getMessage() + "\", " +
                    "\"status\": " +
                    this.statusCode +
                    "}";
        }
    }

}
