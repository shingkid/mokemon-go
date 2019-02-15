package aa;

import java.util.Random;

import aa.util.LockFactory;

public class Legendary extends Mokemon { // Legendary is a subclass of Mokemon
    private static final int LEGENDARY_LVL = 50;
    private final double VINDICTIVENESS = 0.3;
    private Random rdm = new Random();

    // Constructor for Legendary class
    public Legendary(MokemonData data) {
        super(data, LEGENDARY_LVL); // Invoke super constructor
    }

    // Legendary mokemon may return blows
    public void takeRevenge(Mokemon enemy) {
        double effectiveness = getTypeEffectiveness(enemy);
        double dmg = computeDamage(effectiveness);
        double chance = rdm.nextDouble();
        String attackStatus = Mokedex.getAttackStatus(effectiveness);
        if (!hasFainted()) {
            LockFactory.getLock("mok").lock();
            try {
                if (chance < VINDICTIVENESS) {
                    enemy.hp -= dmg;
                    System.out.println(name + " lashed out at " + enemy.name + ", dealing " + dmg + "HP!\n");
                    System.out.println(attackStatus);
                    enemy.display();
                    System.out.println(enemy.name + " has " + Math.max(0, enemy.hp) + " remaining health.\n");
                    if (enemy.hasFainted()) {
                        System.out.println(enemy.name + " has fainted!\n");
                    }
                }
            } finally {
                LockFactory.getLock("mok").unlock();
            }
        }
    }
}