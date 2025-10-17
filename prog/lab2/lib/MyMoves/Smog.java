package lib.MyMoves;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Effect;


public final class Smog extends SpecialMove {
    public Smog() {
        super(Type.POISON, 30, 70);
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

