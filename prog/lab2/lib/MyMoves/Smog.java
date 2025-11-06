package lib.MyMoves;

import ru.ifmo.se.pokemon.*;


public final class Smog extends SpecialMove {
    public Smog() {
        super(Type.POISON, 30, 70);
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
    protected void applyOppEffects(Pokemon p) {
        if (!(p.hasType(Type.POISON) || p.hasType(Type.STEEL)) && Math.random() <= 0.4 ) {
            Effect.poison(p);
        }
    }

    @Override
    protected String describe() {
        return "uses Smog";
    }
}

