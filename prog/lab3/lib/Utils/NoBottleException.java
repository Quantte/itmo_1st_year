package lib.Utils;

public class NoBottleException extends Exception{
    public NoBottleException() {
        super("There is no bottles left");
    }

}
