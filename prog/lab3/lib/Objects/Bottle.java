package lib.Objects;

public class Bottle {
    private String content;
    private boolean is_opened;

    Bottle(String content, boolean is_opened) {
        this.content = content;
        this.is_opened = is_opened;
    }
    Bottle(String content) {
        this.content = content;
        this.is_opened = false;
    }


}
