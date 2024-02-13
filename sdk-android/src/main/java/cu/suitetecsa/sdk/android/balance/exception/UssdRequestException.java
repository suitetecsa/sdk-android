package cu.suitetecsa.sdk.android.balance.exception;

public class UssdRequestException extends Exception {
    public UssdRequestException() {
        super();
    }

    public UssdRequestException(String message) {
        super(message);
    }

    public UssdRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
