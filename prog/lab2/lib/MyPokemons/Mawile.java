package lib.MyPokemons;

import lib.MyMoves.Astonish;
import lib.MyMoves.DoubleTeam;
import lib.MyMoves.FeintAttack;
import lib.MyMoves.Flamethrower;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public final class Mawile extends Pokemon {
    public Mawile(String name, int level) {
        super(name, level);
        setType(Type.FAIRY, Type.STEEL);
        setStats(50, 85, 85, 55, 55, 50);
        setMove(
                new Astonish(),
                new Flamethrower(),
                new FeintAttack(),
                new DoubleTeam()
        );
    }
}
