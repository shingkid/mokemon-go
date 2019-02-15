package aa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import aa.util.LockFactory;
import aa.util.StopWatch;

public class MokemonGo {
    static final int MIN_LVL = 5; // Minimum mokemon level owned by trainer
    static final int MAX_LVL = 40; // Maximum mokemon level owned by trainer
    public static void main(String[] args) throws InterruptedException {
        Random rdm = new Random();
        Mokedex.loadMokedex(); // Load mokemon data

        // Spin multiple threads for different players battling the same legendary
        final int NO_OF_THREADS = 10;
        final int NO_OF_CHALLENGERS = 1000;

        // Spawn random legendary mokemon
        Legendary legendary = new Legendary(Mokedex.legendaries.get(rdm.nextInt(Mokedex.legendaries.size())));
        System.out.println("\nA wild " + legendary.getName() + " has appeared!");
        legendary.printAsciiArt();
        legendary.display();

        // start timer
        System.out.println("Starting timer...");
        StopWatch timer = new StopWatch();
        timer.start();

        // Use Threadpool / ExecutorService
        ExecutorService exec = Executors.newFixedThreadPool(NO_OF_THREADS);

        HashMap<Integer, Future<Double>> map = new HashMap<>(); // Store trainers and their corresponding total damage.
        for (int i=0; i<NO_OF_CHALLENGERS; i++) {
            if (legendary.hasFainted()) {
                // No more challengers accepted if legendary has already been defeated.
                break;
            }

            // Create random mokemon for trainer
            int lvl = (int) (MIN_LVL + Math.random() * (MAX_LVL - MIN_LVL));
            Mokemon mokemon = new Mokemon(Mokedex.nonLegendaries.get(rdm.nextInt(Mokedex.nonLegendaries.size())), lvl);
            LockFactory.getLock("mok").lock();
            System.out.println("Trainer " + i + " chose " + mokemon.getName() + "!\n");
            mokemon.printAsciiArt();
            mokemon.display();
            LockFactory.getLock("mok").unlock();
            Future<Double> future = exec.submit(new MokemonTrainer(mokemon, legendary)); // MokemonTrainer is a callable.
            map.put(i, future);
        }

        exec.shutdown(); // does not shutdown the pool, just prevents more tasks from being submitted.
        exec.awaitTermination(10, TimeUnit.SECONDS);

        // Find winner
        int winner = -1;
        double highestDmg = 0;
        for (int key : map.keySet()) {
            try {
                double value = map.get(key).get();
                if (value > highestDmg) {
                    winner = key;
                    highestDmg = value;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Last line in program
        System.out.println("\nTrainer " + winner + " wins with " + highestDmg + " total damage!");
        System.out.println("Time elapsed : " + timer.toString());
    }
}

class MokemonTrainer implements Callable<Double> {
    private Mokemon mokemon;
    private Legendary opponent;
    private double totalDmg = 0;

    public MokemonTrainer(Mokemon mokemon, Legendary opponent) {
        this.mokemon = mokemon;
        this.opponent = opponent;
    }

    @Override
    public Double call() {
        while (!mokemon.hasFainted() && !opponent.hasFainted()) {
            synchronized(opponent) {
                if (opponent.hasFainted()) {
                    break;
                } else {
                    totalDmg += mokemon.attack(opponent);
                }
            }
        }

        if (mokemon.hasFainted()) {
            return -1.0;
        }

        return totalDmg;
    }
}