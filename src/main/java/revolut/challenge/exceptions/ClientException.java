package revolut.challenge.exceptions;

public class ClientException extends Exception {

    private static final long serialVersionUID = 1L;

    public int status;
    public Object body;

    public ClientException(int status, Object body) {
        super(String.format("Client returned error with status %d and body %s", status, body.toString()));
        this.status = status;
        this.body = body;
    }

}
