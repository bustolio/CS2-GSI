package com.cs2gsi;

import com.cs2gsi.nodes.AllGrenades;
import com.cs2gsi.nodes.AllPlayers;
import com.cs2gsi.nodes.Auth;
import com.cs2gsi.nodes.Bomb;
import com.cs2gsi.nodes.Map;
import com.cs2gsi.nodes.Node;
import com.cs2gsi.nodes.PhaseCountdowns;
import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Provider;
import com.cs2gsi.nodes.Round;
import com.cs2gsi.nodes.TournamentDraft;
import com.google.gson.JsonObject;

/**
 * A class representing various information pertaining to Game State Integration of Counter-Strike 2.
 */
public class GameState extends Node {
    /**
     * Information about GSI authentication.<br>
     * The game sends it when the cfg file has an {@code "auth"} block with a {@code "token"},
     * which {@link GameStateListener#installGSIConfigFile(String)} writes after
     * {@link GameStateListener#setAuthToken(String)}.
     */
    public final Auth auth;

    /**
     * Information about the provider of this GameState.<br>
     * Enabled by including {@code "provider" "1"} in the game state cfg file.
     */
    public final Provider provider;

    /**
     * Information about the current map.<br>
     * Enabled by including {@code "map" "1"} in the game state cfg file.
     */
    public final Map map;

    /**
     * Information about the current round.<br>
     * Enabled by including {@code "round" "1"} in the game state cfg file.
     */
    public final Round round;

    /**
     * Information about the local player or team players when spectating.<br>
     * Enabled by including any of the following in the game state cfg file:
     * {@code "player_id" "1"}, {@code "player_state" "1"}, {@code "player_match_stats" "1"},
     * {@code "player_weapons" "1"}, {@code "player_position" "1"}
     */
    public final Player player;

    /**
     * Information about the phase countdowns. (SPECTATOR ONLY)<br>
     * Enabled by including {@code "phase_countdowns" "1"} in the game state cfg file.
     */
    public final PhaseCountdowns phaseCountdowns;

    /**
     * Information about the all players in the game. (SPECTATOR ONLY)<br>
     * Enabled by including any of the following in the game state cfg file:
     * {@code "allplayers_id" "1"}, {@code "allplayers_state" "1"}, {@code "allplayers_match_stats" "1"},
     * {@code "allplayers_weapons" "1"}, {@code "allplayers_position" "1"}
     */
    public final AllPlayers allPlayers;

    /**
     * Information about the all grenades in the game. (SPECTATOR ONLY)<br>
     * Enabled by including {@code "allgrenades" "1"} in the game state cfg file.
     */
    public final AllGrenades allGrenades;

    /**
     * Information about the bomb in the game. (SPECTATOR ONLY)<br>
     * Enabled by including {@code "bomb" "1"} in the game state cfg file.
     */
    public final Bomb bomb;

    /**
     * Information about the tournament draft in the game. (SPECTATOR ONLY)<br>
     * Enabled by including {@code "tournamentdraft" "1"} in the game state cfg file.
     */
    public final TournamentDraft tournamentDraft;

    private GameState previousGameState;

    /**
     * Creates an empty GameState instance.
     */
    public GameState() {
        this(null);
    }

    /**
     * Creates a GameState instance based on the given json data.
     *
     * @param parsedData The parsed json data.
     */
    public GameState(JsonObject parsedData) {
        super(parsedData);

        auth = new Auth(getJObject("auth"));
        provider = new Provider(getJObject("provider"));
        map = new Map(getJObject("map"));
        round = new Round(getJObject("round"));
        player = new Player(getJObject("player"));
        phaseCountdowns = new PhaseCountdowns(getJObject("phase_countdowns"));
        allPlayers = new AllPlayers(getJObject("allplayers"));
        allGrenades = new AllGrenades(getJObject("grenades"));
        bomb = new Bomb(getJObject("bomb"));
        tournamentDraft = new TournamentDraft(getJObject("tournamentdraft"));
    }

    /**
     * Whether {@link #player} is the account that runs the game.<br>
     * The game fills the player node with whoever is on screen, so while spectating it describes
     * someone else. The provider node always names the local account.
     *
     * @return True only if both nodes carry the same Steam ID. False while spectating, and false if
     *         either ID is missing, because then nothing shows that the player node is the local one.
     */
    public boolean isLocalPlayer() {
        return !provider.steamId.isEmpty() && provider.steamId.equals(player.steamId);
    }

    /**
     * A previous GameState.
     */
    public synchronized GameState getPreviously() {
        if (previousGameState == null) {
            previousGameState = new GameState(getJObject("previously"));
        }

        return previousGameState;
    }
}
