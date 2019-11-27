package revolut.challenge.exceptions.errors;

public enum ErrorEnum {
    ACCOUNT_NOT_EXISTS("Account doesn't exists"),
    ACCOUNT_MUST_BE_NUMBER("Account must be number"),
    ACCOUNT_INSUFFICIENT_FUNDS("Insufficient funds"),
    ACCOUNT_CANT_BE_NEGATIVE("Account amount can't be negative"),
    ACCOUNT_CANT_BE_ZERO("Account can't be zero or negative"),

    TRANSFER_SAME_ACCOUNT("From and To account can't be the same"),
    TRANSFER_AMOUNT_ZERO_OR_NEGATIVE("Transfer amount can't be negative"),

    ;

    private String message;

    ErrorEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
