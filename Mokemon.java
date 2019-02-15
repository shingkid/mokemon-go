package aa;

import aa.util.LockFactory;

public class Mokemon extends MokemonData {
    int level;
    double attack;
    double defense;
    double maxHp;
    double hp;
    double cp;
    double cpScaler;
    private static final int POWER = 80;

    Mokemon(MokemonData data, int level) {
        super(data);
        this.level = level;
        // https://gaming.stackexchange.com/questions/280491/formula-to-calculate-pokemon-go-cp-and-hp
        // HP = (Base Stam + Stam IV) * Lvl(CPScalar)
        // CP = (Base Atk + Atk IV) * (Base Def + Def IV)^0.5 * (Base Stam + Stam IV)^0.5 * Lvl(CPScalar)^2 / 10
        // At levels 1-10: f(lvl) = ( 0.01885225 * level ) - 0.01001625
        // At levels 11-20: f(lvl) = ( 0.01783805 * ( level - 10 ) ) + 0.17850625
        // At levels 21-30: f(lvl) = ( 0.01784981 * ( level - 20 ) ) + 0.35688675
        // At levels 31-40: f(lvl) = ( 0.00891892 * ( level - 30 ) ) + 0.53538485
        // Attack * sqrt{Defense} * sqrt{Stamina} * f(lvl) / 10
        if (level <= 10) {
            cpScaler = ( 0.01885225 * level ) - 0.01001625;
        } else if (level <= 20) {
            cpScaler = ( 0.01783805 * ( level - 10 ) ) + 0.17850625;
        } else if (level <= 30) {
            cpScaler = ( 0.01784981 * ( level - 20 ) ) + 0.35688675;
        } else if (level <= 40) {
            cpScaler = ( 0.00891892 * ( level - 30 ) ) + 0.53538485;
        } else {
            // Scaler for legendaries
            // cpScaler = 3.2;
            cpScaler = 10;
        }

        // Attack=(BaseAttack+AttackIV)∗CpM
        attack = baseAttack * cpScaler;
        // Defense=(BaseDefense+DefenseIV)∗CpM
        defense = baseDefense * cpScaler;
        hp = maxHp = baseHp * cpScaler;
        cp = baseAttack * Math.sqrt(baseDefense) * Math.sqrt(baseHp) * cpScaler / 10;
    }

    // Get current HP of mokemon
    public double getHp() {
        return hp;
    }

    // Get maximum HP of mokemon
    public double getMaxHp() {
        return maxHp;
    }

    public double getTypeEffectiveness(Mokemon other) {
        return Mokedex.getEffectiveness(this.types[0], other.types[0]);
    }

    // Damage
    // https://bulbapedia.bulbagarden.net/wiki/Damage
    // Floor(0.5 ∗ Power ∗ Atk / Def ∗ STAB ∗ Effective) + 1
    public double computeDamage(double effectiveness) {
        double modifier = effectiveness * (0.85 + Math.random() * (1.00 - 0.85));
        // return ((2*level/5 + 2) * POWER * attack/defense / 50 + 2) * modifier;
        // return 0.5 * POWER * attack * defense * modifier + 1;
        return Math.floor(0.5 * POWER * attack / defense * modifier) + 1;
    }

    // A mokemon faints when its hp is 0 or below
    public boolean hasFainted() {
        return hp <= 0;
    }

    // Mokemon A attacks another mokemon B causing B to lose some portion of HP
    public double attack(Legendary other) {
        double effectiveness = getTypeEffectiveness(other);
        double dmg = computeDamage(effectiveness);
        String attackStatus = Mokedex.getAttackStatus(effectiveness);
        
        LockFactory.getLock("mok").lock();
        other.hp -= dmg;
        System.out.println(name + " dealt " + dmg + "HP to " + other.name + "!\n");
        System.out.println(attackStatus);
        other.display();
        System.out.println(other.name + " has " + Math.max(0, other.hp) + " remaining health.\n");
        if (other.hasFainted()) {
            System.out.println(other.name + " has fainted!\n");
        }
        LockFactory.getLock("mok").unlock();
        other.takeRevenge(this);

        return dmg;
    }

    // String representation of a Mokemon
    @Override
    public String toString() {
        String title = "\n#" + mokedexNo + " " + name + "\n";
        StringBuffer outputBuffer = new StringBuffer(title.length());
        for (int i = 0; i < title.length(); i++){
            outputBuffer.append("-");
        }
        String header = outputBuffer.toString();
        String levelStats = "\nLevel: " + level;
        String hpStats = "\nHP: " + hp;
        String attStats = "\nAttack: " + attack;
        String defStats = "\nDefense: " + defense;
        String cpStats = "\nCP: " + cp;
        return title + header + levelStats + hpStats + attStats + defStats + cpStats + "\n";
    }

    // String representation of health bar scaled to 40 units
    public String getHealthBar() {
        String healthBar = "|";
        for (int i=0; i<40; i++) {
            if (i < 40*hp/maxHp) {
                healthBar += "\u2588";
            } else {
                healthBar += " ";
            }
        }
        return healthBar + "|";
    }

    // Displays relevant info of mokemon
    public void display() {
        String box = "\n+--------------------------------------------+\n";
        String header = "| " + name;
        String lvlStat = "Lv" + level;
        for (int i=0; i<42-name.length()-lvlStat.length(); i++) {
            header += " ";
        }
        header += lvlStat + " |\n";
        System.out.println(box + header + "| " + getHealthBar() + " |" + box);
    }
}