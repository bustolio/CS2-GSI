package com.cs2gsi.example;

import com.cs2gsi.GameState;
import com.cs2gsi.GameStateListener;
import com.cs2gsi.events.BombStateUpdated;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.KillFeed;
import com.cs2gsi.events.PlayerActiveWeaponChanged;
import com.cs2gsi.events.PlayerDied;
import com.cs2gsi.events.PlayerGotKill;
import com.cs2gsi.events.PlayerTookDamage;
import com.cs2gsi.events.PlayerWeaponsDropped;
import com.cs2gsi.events.PlayerWeaponsPickedUp;
import com.cs2gsi.events.RoundConcluded;
import com.cs2gsi.events.RoundStarted;
import com.cs2gsi.nodes.Weapon;

import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        try (GameStateListener gsl = new GameStateListener(4000)) {
            if (!gsl.generateGSIConfigFile("Example")) {
                System.out.println("Could not generate GSI configuration file.");
            }

            // There are many callbacks that can be subscribed.
            // This example shows a few.
            gsl.onNewGameState(Program::onNewGameState);
            gsl.onGameEvent(Program::onGameEvent);
            gsl.subscribe(BombStateUpdated.class, Program::onBombStateUpdated);
            gsl.subscribe(PlayerGotKill.class, Program::onPlayerGotKill);
            gsl.subscribe(PlayerDied.class, Program::onPlayerDied);
            gsl.subscribe(KillFeed.class, Program::onKillFeed);
            gsl.subscribe(PlayerWeaponsPickedUp.class, Program::onPlayerWeaponsPickedUp);
            gsl.subscribe(PlayerWeaponsDropped.class, Program::onPlayerWeaponsDropped);
            gsl.subscribe(RoundStarted.class, Program::onRoundStarted);
            gsl.subscribe(RoundConcluded.class, Program::onRoundConcluded);

            if (!gsl.start()) {
                System.out.println("GameStateListener could not start. Try running this program as Administrator. Exiting.");
                System.exit(0);
            }

            System.out.println("Listening for game integration calls...");

            System.out.println("Press ENTER to quit");
            System.in.read();
        }
    }

    private static void onNewGameState(GameState gameState) {
        // Guaranteed to fire before CS2GameEvent events.
    }

    private static void onGameEvent(CS2GameEvent gameEvent) {
        if (gameEvent instanceof PlayerTookDamage playerTookDamage) {
            System.out.println("The player " + playerTookDamage.player.name + " took "
                    + (playerTookDamage.previousValue - playerTookDamage.newValue) + " damage!");
        } else if (gameEvent instanceof PlayerActiveWeaponChanged activeWeaponChanged) {
            System.out.println("The player " + activeWeaponChanged.player.name
                    + " changed their active weapon to " + activeWeaponChanged.newValue.name
                    + " from " + activeWeaponChanged.previousValue.name + "!");
        }
    }

    private static void onBombStateUpdated(BombStateUpdated gameEvent) {
        System.out.println("The bomb is now " + gameEvent.newValue + ".");
    }

    private static void onPlayerGotKill(PlayerGotKill gameEvent) {
        System.out.println("The player " + gameEvent.player.name + " earned a "
                + (gameEvent.isHeadshot ? "headshot " : "") + "kill with " + gameEvent.weapon.name + "!"
                + (gameEvent.isAce ? " And it was an ACE!" : ""));
    }

    private static void onPlayerDied(PlayerDied gameEvent) {
        System.out.println("The player " + gameEvent.player.name + " died.");
    }

    private static void onKillFeed(KillFeed gameEvent) {
        System.out.println(gameEvent.killer.name + " killed " + gameEvent.victim.name
                + " with " + gameEvent.weapon.name + (gameEvent.isHeadshot ? " as a headshot." : "."));
    }

    private static void onPlayerWeaponsPickedUp(PlayerWeaponsPickedUp gameEvent) {
        System.out.println("The player " + gameEvent.player.name + " picked up the following weapons:");
        for (Weapon weapon : gameEvent.weapons) {
            System.out.println("\t" + weapon.name);
        }
    }

    private static void onPlayerWeaponsDropped(PlayerWeaponsDropped gameEvent) {
        System.out.println("The player " + gameEvent.player.name + " dropped the following weapons:");
        for (Weapon weapon : gameEvent.weapons) {
            System.out.println("\t" + weapon.name);
        }
    }

    private static void onRoundStarted(RoundStarted gameEvent) {
        if (gameEvent.isFirstRound) {
            System.out.println("First round " + gameEvent.round + " started.");
        } else if (gameEvent.isLastRound) {
            System.out.println("Last round " + gameEvent.round + " started.");
        } else {
            System.out.println("A new round " + gameEvent.round + " started.");
        }
    }

    private static void onRoundConcluded(RoundConcluded gameEvent) {
        System.out.println("Round " + gameEvent.round + " concluded by " + gameEvent.winningTeam
                + " for reason: " + gameEvent.roundConclusionReason);
    }
}
