package revolut.challenge.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: jlarram on 14/9/18. *
 */

public class InternalServerErrorException extends ApiException {

    private final static int STATUS = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    private final static String CODE = "internal_server_error";

    public InternalServerErrorException(String description) {
        super(CODE, description, STATUS);
    }

    public InternalServerErrorException(String description, Throwable t) {
        super(CODE, description, STATUS, t);
    }
}
