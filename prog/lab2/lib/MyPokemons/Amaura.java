package lib.MyPokemons;

import lib.MyMoves.FrostBreath;
import lib.MyMoves.RockSlide;
import lib.MyMoves.RockThrow;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class Amaura extends Pokemon {
    public Amaura(String name, int level) {
        super(name, level);
        setType(Type.ROCK, Type.ICE);
        setStats(77, 59, 50, 67, 63, 46);
        setMove(
                new RockSlide(),
                new RockThrow(),
                new FrostBreath()
        );
    }
}