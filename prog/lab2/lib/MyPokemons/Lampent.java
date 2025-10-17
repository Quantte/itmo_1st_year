package lib.MyPokemons;

import lib.MyMoves.Astonish;


public class Lampent extends Litwick {
    public Lampent(String name, int level) {
        super(name, level);
        setStats(60, 40, 60, 95, 60, 55);
        addMove(new Astonish());
    }
}
