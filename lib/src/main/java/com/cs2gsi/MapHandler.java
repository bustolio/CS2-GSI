package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.CurrentSpectatorsChanged;
import com.cs2gsi.events.FreezetimeOver;
import com.cs2gsi.events.FreezetimeStarted;
import com.cs2gsi.events.GamemodeChanged;
import com.cs2gsi.events.Gameover;
import com.cs2gsi.events.IntermissionOver;
import com.cs2gsi.events.IntermissionStarted;
import com.cs2gsi.events.LevelChanged;
import com.cs2gsi.events.MapPhaseChanged;
import com.cs2gsi.events.MapUpdated;
import com.cs2gsi.events.MatchStarted;
import com.cs2gsi.events.PauseOver;
import com.cs2gsi.events.PauseStarted;
import com.cs2gsi.events.RoundChanged;
import com.cs2gsi.events.RoundConcluded;
import com.cs2gsi.events.RoundStarted;
import com.cs2gsi.events.RoundWinsChanged;
import com.cs2gsi.events.SouvenirsTotalChanged;
import com.cs2gsi.events.TeamRemainingTimeoutsChanged;
import com.cs2gsi.events.TeamScoreChanged;
import com.cs2gsi.events.TeamStatisticsUpdated;
import com.cs2gsi.events.TimeoutOver;
import com.cs2gsi.events.TimeoutStarted;
import com.cs2gsi.events.WarmupOver;
import com.cs2gsi.events.WarmupStarted;
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

        boolean isLastRound = ((evt.newValue.round + 1) == maxRounds) || ((evt.newValue.round + 1) / (float) maxRounds) == 0.5f; // Next round is half
        boolean isFirstRound = (evt.newValue.round == 0) || (evt.newValue.round / (float) maxRounds) == 0.5f; // Is first round or half round

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

                    dispatcher.broadcast(new RoundConcluded(evt.previousValue.round, roundConclusion, winningTeam, isFirstRound, isLastRound));
                }

                dispatcher.broadcast(new RoundStarted(evt.newValue.round, isFirstRound, isLastRound));
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
