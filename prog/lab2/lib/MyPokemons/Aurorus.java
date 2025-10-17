package lib.MyPokemons;

import lib.MyMoves.Psychic;


public final class Aurorus extends Amaura {
    public Aurorus(String name, int level) {
        super(name, level);
        setStats(123, 77, 72, 99, 92, 58);
        addMove(new Psychic());
    }
}
