import ru.ifmo.se.pokemon.Battle;

import lib.MyPokemons.Mawile;
import lib.MyPokemons.Amaura;
import lib.MyPokemons.Aurorus;
import lib.MyPokemons.Litwick;
import lib.MyPokemons.Lampent;
import lib.MyPokemons.Chandelure;


public class Main {
    public static void main(String[] args) {
        Battle b = new Battle();

        Mawile m1 = new Mawile("Apple", 25);
        Amaura a1 = new Amaura("Pineapple", 30);
        Aurorus au1 = new Aurorus("Coconut", 39);
        Litwick l1 = new Litwick("Strawberry", 38);
        Lampent la1 = new Lampent("Grape", 41);
        Chandelure c1 = new Chandelure("Pear", 41);

        b.addAlly(m1);
        b.addAlly(au1);
        b.addAlly(l1);

        b.addFoe(la1);
        b.addFoe(a1);
        b.addFoe(c1);

        b.go();
    }
}
