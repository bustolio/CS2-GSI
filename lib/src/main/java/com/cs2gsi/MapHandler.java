package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.map.CurrentSpectatorsChanged;
import com.cs2gsi.events.map.FreezetimeOver;
import com.cs2gsi.events.map.FreezetimeStarted;
import com.cs2gsi.events.map.GamemodeChanged;
import com.cs2gsi.events.map.Gameover;
import com.cs2gsi.events.map.IntermissionOver;
import com.cs2gsi.events.map.IntermissionStarted;
import com.cs2gsi.events.map.LevelChanged;
import com.cs2gsi.events.map.MapPhaseChanged;
import com.cs2gsi.events.map.MapUpdated;
import com.cs2gsi.events.map.MatchStarted;
import com.cs2gsi.events.map.PauseOver;
import com.cs2gsi.events.map.PauseStarted;
import com.cs2gsi.events.round.RoundChanged;
import com.cs2gsi.events.round.RoundConcluded;
import com.cs2gsi.events.round.RoundStarted;
import com.cs2gsi.events.map.RoundWinsChanged;
import com.cs2gsi.events.map.SouvenirsTotalChanged;
import com.cs2gsi.events.team.TeamRemainingTimeoutsChanged;
import com.cs2gsi.events.team.TeamScoreChanged;
import com.cs2gsi.events.team.TeamStatisticsUpdated;
import com.cs2gsi.events.map.TimeoutOver;
import com.cs2gsi.events.map.TimeoutStarted;
import com.cs2gsi.events.map.WarmupOver;
import com.cs2gsi.events.map.WarmupStarted;
import com.cs2gsi.nodes.PlayerTeam;
import com.cs2gsi.nodes.RoundConclusion;

class MapHandler extends EventHandler<CS2GameEvent> {
    private final int maxRounds = 24; // Hardcoded to 24

    MapHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(MapUpdated.class, this::onMapUpdated);
        dispatcher.subscribe(TeamStatisticsUpdated.class, this::onTeamStatisticsUpdated);
        dispatcher.subscribe(MapPhaseChanged.class, this::onMapPhaseChanged);
    }

    private void onMapUpdated(CS2GameEvent e) {
        if (!(e instanceof MapUpdated evt)) {
            return;
        }

        if (!evt.newValue.name.equals(evt.previousValue.name)) {
            dispatcher.broadcast(new LevelChanged(evt.newValue.name, evt.previousValue.name));
        }

        if (evt.newValue.mode != evt.previousValue.mode) {
            dispatcher.broadcast(new GamemodeChanged(evt.newValue.mode, evt.previousValue.mode));
        }

        if (!evt.newValue.ctStatistics.equals(evt.previousValue.ctStatistics)) {
            dispatcher.broadcast(new TeamStatisticsUpdated(evt.newValue.ctStatistics, evt.previousValue.ctStatistics, PlayerTeam.CT));
        }

        if (!evt.newValue.tStatistics.equals(evt.previousValue.tStatistics)) {
            dispatcher.broadcast(new TeamStatisticsUpdated(evt.newValue.tStatistics, evt.previousValue.tStatistics, PlayerTeam.T));
        }

        if (evt.newValue.round != evt.previousValue.round) {
            dispatcher.broadcast(new RoundChanged(evt.newValue.round, evt.previousValue.round));

            if (evt.newValue.round > evt.previousValue.round) {
                boolean hasRoundConclusion = evt.newValue.roundWins.containsKey(evt.previousValue.round + 1);

                if (evt.newValue.round != 0 && hasRoundConclusion) {
                    // RoundWins is off by one. Where RoundWins[1] == Round 0.
                    RoundConclusion roundConclusion = evt.newValue.roundWins.get(evt.previousValue.round + 1);
                    PlayerTeam winningTeam = switch (roundConclusion) {
                        case T_Win_Elimination, T_Win_Time, T_Win_Bomb -> PlayerTeam.T;
                        case CT_Win_Elimination, CT_Win_Time, CT_Win_Defuse, CT_Win_Rescue -> PlayerTeam.CT;
                        default -> PlayerTeam.Undefined;
                    };

                    // The flags describe the round that ended, not the one that starts.
                    dispatcher.broadcast(new RoundConcluded(evt.previousValue.round, roundConclusion, winningTeam,
                            isFirstRound(evt.previousValue.round), isLastRound(evt.previousValue.round)));
                }

                dispatcher.broadcast(new RoundStarted(evt.newValue.round,
                        isFirstRound(evt.newValue.round), isLastRound(evt.newValue.round)));
            }
        }

        if (evt.newValue.phase != evt.previousValue.phase) {
            dispatcher.broadcast(new MapPhaseChanged(evt.newValue.phase, evt.previousValue.phase));
        }

        if (!evt.newValue.roundWins.equals(evt.previousValue.roundWins)) {
            dispatcher.broadcast(new RoundWinsChanged(evt.newValue.roundWins, evt.previousValue.roundWins));
        }

        if (evt.newValue.currentSpectators != evt.previousValue.currentSpectators) {
            dispatcher.broadcast(new CurrentSpectatorsChanged(evt.newValue.currentSpectators, evt.previousValue.currentSpectators));
        }

        if (evt.newValue.souvenirsTotal != evt.previousValue.souvenirsTotal) {
            dispatcher.broadcast(new SouvenirsTotalChanged(evt.newValue.souvenirsTotal, evt.previousValue.souvenirsTotal));
        }
    }

    // First round of the match or of the second half.
    private boolean isFirstRound(int round) {
        return round == 0 || round == maxRounds / 2;
    }

    // Last round of the match or of the first half.
    private boolean isLastRound(int round) {
        return round + 1 == maxRounds || round + 1 == maxRounds / 2;
    }

    private void onTeamStatisticsUpdated(CS2GameEvent e) {
        if (!(e instanceof TeamStatisticsUpdated evt)) {
            return;
        }

        if (evt.newValue.score != evt.previousValue.score) {
            dispatcher.broadcast(new TeamScoreChanged(evt.newValue.score, evt.previousValue.score, evt.team));
        }

        if (evt.newValue.remainingTimeouts != evt.previousValue.remainingTimeouts) {
            dispatcher.broadcast(new TeamRemainingTimeoutsChanged(evt.newValue.remainingTimeouts, evt.previousValue.remainingTimeouts, evt.team));
        }
    }

    private void onMapPhaseChanged(CS2GameEvent e) {
        if (!(e instanceof MapPhaseChanged evt)) {
            return;
        }

        switch (evt.previousValue) {
            case Warmup -> dispatcher.broadcast(new WarmupOver());
            case Intermission -> dispatcher.broadcast(new IntermissionOver());
            case Freezetime -> dispatcher.broadcast(new FreezetimeOver());
            case Paused -> dispatcher.broadcast(new PauseOver());
            case Timeout_T -> dispatcher.broadcast(new TimeoutOver(PlayerTeam.T));
            case Timeout_CT -> dispatcher.broadcast(new TimeoutOver(PlayerTeam.CT));
            default -> {
            }
        }

        switch (evt.newValue) {
            case Warmup -> dispatcher.broadcast(new WarmupStarted());
            case Intermission -> dispatcher.broadcast(new IntermissionStarted());
            case Freezetime -> dispatcher.broadcast(new FreezetimeStarted());
            case Paused -> dispatcher.broadcast(new PauseStarted());
            case Timeout_T -> dispatcher.broadcast(new TimeoutStarted(PlayerTeam.T));
            case Timeout_CT -> dispatcher.broadcast(new TimeoutStarted(PlayerTeam.CT));
            case Live -> dispatcher.broadcast(new MatchStarted());
            case Gameover -> dispatcher.broadcast(new Gameover());
            default -> {
            }
        }
    }
}
