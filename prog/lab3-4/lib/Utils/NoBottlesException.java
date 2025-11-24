package lib.Utils;

public class NoBottlesException extends RuntimeException {
    public NoBottlesException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
