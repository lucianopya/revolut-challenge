package revolut.challenge.exceptions;

import javax.servlet.http.HttpServletResponse;

public class BadRequestException extends ApiException {

    private final static int STATUS = HttpServletResponse.SC_BAD_REQUEST;
    private final static String CODE = "bad_request";

    public BadRequestException(String description) {
        super(CODE, description, STATUS);
    }

    public BadRequestException(String description, Throwable t) {
        super(CODE, description, STATUS, t);
    }
}
