package revolut.challenge.exceptions;

import javax.servlet.http.HttpServletResponse;

public class NotFoundException extends ApiException {

    private final static int STATUS = HttpServletResponse.SC_NOT_FOUND;
    private final static String CODE = "not_found";

    public NotFoundException(String description) {
        super(CODE, description, STATUS);
    }

    public NotFoundException(String description, Throwable t) {
        super(CODE, description, STATUS, t);
    }
}
