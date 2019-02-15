package aa;

public class MokemonData {
    int mokedexNo;
    String name;
    String[] types; // Possible implementation: Effectiveness factor in damage
    int total;
    double baseHp;
    int baseAttack;
    int baseDefense;
    int speed; // Possible implementation: Give priority in attacking

    public MokemonData(int mokedexNo, String name, String[] types, int total, double baseHp, int baseAttack, int baseDefense, int speed) {
        this.mokedexNo = mokedexNo;
        this.name = name.toUpperCase();
        this.types = types;
        this.total = total;
        this.baseHp = baseHp;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.speed = speed;
    }

    public MokemonData(MokemonData data) {
        this(data.mokedexNo, data.name, data.types, data.total, data.baseHp, data.baseAttack, data.baseDefense, data.speed);
    }

    public int getMokedexNo() { 
        return mokedexNo;
    }

    public String getName() {
        return name;
    }

    public String[] getTypes() {
        return types;
    }

    public double getBaseHp() {
        return baseHp;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public void printAsciiArt() {
        Mokedex.printAsciiArt(mokedexNo);
    }
}