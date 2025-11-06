package lib.MyMoves;

import ru.ifmo.se.pokemon.*;


public final class Flamethrower extends SpecialMove {
    public Flamethrower() {
        super(Type.FIRE, 90, 100);
    }

    @Override
    protected boolean checkAccuracy(Pokemon att, Pokemon def) {
        if ((this.accuracy * att.getStat(Stat.ACCURACY) / def.getStat(Stat.EVASION)) > Math.random()) {
            if ((att.getClass().getSimpleName().equals("Litwick") ||
                    att.getClass().getSimpleName().equals("Lampent") ||
                    att.getClass().getSimpleName().equals("Chandelure")) &&
                    def.getClass().getSimpleName().equals("Mawile") && def.isAlive()) {
                def.setMod(Stat.EVASION, 2);
                System.out.println(def.getClass().getSimpleName() + "'s evasion rose by 2!");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void applyOppDamage(Pokemon def, double damage) {
        super.applyOppDamage(def, damage);
        if (Math.random() <= 0.1) {
            Effect.burn(def);
        }
    }

    @Override
    protected String describe() {
        return "uses Flamethrower";
    }
}
