package lib.MyPokemons;

import lib.MyMoves.Flamethrower;
import lib.MyMoves.Smog;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class Litwick extends Pokemon {
    public Litwick(String name, int level) {
        super(name, level);
        setType(Type.GHOST, Type.FIRE);
        setStats(50, 30, 55, 65, 55, 20);
        setMove(
                new Flamethrower(),
                new Smog()
        );
    }
}
