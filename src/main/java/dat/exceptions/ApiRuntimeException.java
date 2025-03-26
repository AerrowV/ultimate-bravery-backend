package dat.exceptions;

public class ApiRuntimeException extends RuntimeException {

    private int errorCode;

    public ApiRuntimeException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }


}