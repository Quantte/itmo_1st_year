package lib.Objects;

import lib.Utils.CannotOpenBottleException;
import lib.Utils.Materials;
import lib.Utils.OpeningInstrument;

import java.util.Objects;

public class Bottle {
    private final String content;
    private boolean is_opened;

    public Bottle(String content, boolean is_opened) {
        this.content = content;
        this.is_opened = is_opened;
    }
    public Bottle(String content) {
        this.content = content;
        this.is_opened = false;
    }

    public void open(OpeningInstrument instrument) throws CannotOpenBottleException {
        try {
            if (this.is_opened) {
                throw new CannotOpenBottleException("Bottle is already opened");
            }
            if (!(instrument instanceof Teeth)) {
                throw new CannotOpenBottleException("No teeth provided");
            }
            if (!((Teeth) instrument).compareWithMaterial(Materials.IRON)) {
                throw new CannotOpenBottleException("Teeth are too weak");
            }

            this.is_opened = true;
            System.out.println("Бутылка с " + content + " успешно открыта!");
        } catch (CannotOpenBottleException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return "Бутылка с " + this.content;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.content, this.is_opened);
    }
}
