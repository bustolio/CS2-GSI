package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.PhaseCountdownsUpdated;
import com.cs2gsi.events.PhaseEndTimeChanged;
import com.cs2gsi.events.RoundPhaseUpdated;

public class PhaseCountdownsHandler extends EventHandler<CS2GameEvent> {
    public PhaseCountdownsHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(PhaseCountdownsUpdated.class, this::onPhaseCountdownsUpdated);
    }

    private void onPhaseCountdownsUpdated(CS2GameEvent e) {
        if (!(e instanceof PhaseCountdownsUpdated evt)) {
            return;
        }

        if (evt.newValue.phase != evt.previousValue.phase) {
            dispatcher.broadcast(new RoundPhaseUpdated(evt.newValue.phase, evt.previousValue.phase));
        }

        if (evt.newValue.phaseEndTime != evt.previousValue.phaseEndTime) {
            dispatcher.broadcast(new PhaseEndTimeChanged(evt.newValue.phaseEndTime, evt.previousValue.phaseEndTime));
        }
    }
}
