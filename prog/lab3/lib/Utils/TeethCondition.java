package lib.Utils;


public record TeethCondition(Materials material, int local_durability) {
    /*
    Служебный класс для управления состоянием зубов -
    их материалом и локальным уровнем прочности.

    Локальный уровень прочности - некий счётчик
    по достижению некого значения (в нашем случае 10)
    нужно изменить материал на следующий по иерархии
    WOOD -> IRON -> STREEL -> DIAMOND
     */
    public boolean isStrongerThan(Materials origin) {
        /*
         * Проверяет, крепче ли текущее состояние указанного материала
         * @param origin - материал для сравнения
         * @return true, если прочность выше уровня материала
         */

        return this.material.ordinal() > origin.ordinal() ||
                (this.material == origin && this.local_durability > 5);
    }

    public String getDescription() {
        return "крепче - " + this.material.name() +
                " (уровень прочности: " + this.local_durability + ")";
    }
}
