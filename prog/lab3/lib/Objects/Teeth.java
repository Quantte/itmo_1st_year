package lib.Objects;

import lib.Utils.CannotOpenBottleException;
import lib.Utils.OpeningInstrument;
import lib.Utils.TeethCondition;
import lib.Utils.Materials;

import java.util.Objects;

public class Teeth implements OpeningInstrument {
    private TeethCondition teethCondition;

    public Teeth(TeethCondition teethCondition) {
        this.teethCondition = teethCondition;
    }

    public boolean compareWithMaterial(Materials origin) {
        return teethCondition.isStrongerThan(origin);
    }

    public void getStronger() {
        Materials material = teethCondition.material();
        int durability = teethCondition.local_durability();

        // Если прочность максимальная для текущего материала - изменяем материал
        if (durability >= 10) {
            if (material.ordinal() < Materials.values().length - 1) {
                Materials newMaterial = Materials.values()[material.ordinal() + 1];
                this.teethCondition = new TeethCondition(newMaterial, 1);
                System.out.println("Зубы укрепились! Теперь они " + teethCondition.getDescription());
            }
        } else {
            // Просто увеличиваем прочность
            this.teethCondition = new TeethCondition(material, durability + 1);
            System.out.println("Зубы стали " + teethCondition.getDescription());
        }
    }

    public TeethCondition getTeethCondition() {
        return teethCondition;
    }

    @Override
    public void open(Bottle bottle) throws CannotOpenBottleException {
        bottle.open(this);
    }

    @Override
    public String toString() {
        return "Зубы";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.teethCondition);
    }
}
