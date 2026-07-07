package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.player.PlayerActiveWeaponChanged;
import com.cs2gsi.events.player.PlayerActivityChanged;
import com.cs2gsi.events.player.PlayerArmorChanged;
import com.cs2gsi.events.player.PlayerAssistsChanged;
import com.cs2gsi.events.player.PlayerBurningAmountChanged;
import com.cs2gsi.events.player.PlayerClanChanged;
import com.cs2gsi.events.player.PlayerDeathsChanged;
import com.cs2gsi.events.player.PlayerDefusekitChanged;
import com.cs2gsi.events.player.PlayerDied;
import com.cs2gsi.events.player.PlayerEquipmentValueChanged;
import com.cs2gsi.events.player.PlayerFlashAmountChanged;
import com.cs2gsi.events.player.PlayerForwardDirectionChanged;
import com.cs2gsi.events.player.PlayerGotKill;
import com.cs2gsi.events.player.PlayerHealthChanged;
import com.cs2gsi.events.player.PlayerHelmetChanged;
import com.cs2gsi.events.player.PlayerKillsChanged;
import com.cs2gsi.events.player.PlayerMVPsChanged;
import com.cs2gsi.events.player.PlayerMoneyAmountChanged;
import com.cs2gsi.events.player.PlayerNameChanged;
import com.cs2gsi.events.player.PlayerObserverSlotChanged;
import com.cs2gsi.events.player.PlayerPositionChanged;
import com.cs2gsi.events.player.PlayerRespawned;
import com.cs2gsi.events.player.PlayerRoundHeadshotKillsChanged;
import com.cs2gsi.events.player.PlayerRoundKillsChanged;
import com.cs2gsi.events.player.PlayerRoundTotalDamageChanged;
import com.cs2gsi.events.player.PlayerScoreChanged;
import com.cs2gsi.events.player.PlayerSmokedAmountChanged;
import com.cs2gsi.events.player.PlayerSpectationTargetChanged;
import com.cs2gsi.events.player.PlayerStateChanged;
import com.cs2gsi.events.player.PlayerStatsChanged;
import com.cs2gsi.events.player.PlayerSteamIDChanged;
import com.cs2gsi.events.player.PlayerTeamChanged;
import com.cs2gsi.events.player.PlayerTookDamage;
import com.cs2gsi.events.player.PlayerUpdated;
import com.cs2gsi.events.player.PlayerWeaponAmmoClipChanged;
import com.cs2gsi.events.player.PlayerWeaponAmmoReserveChanged;
import com.cs2gsi.events.player.PlayerWeaponChanged;
import com.cs2gsi.events.player.PlayerWeaponsDropped;
import com.cs2gsi.events.player.PlayerWeaponsPickedUp;
import com.cs2gsi.events.player.PlayerXPOverloadLevelChanged;
import com.cs2gsi.events.provider.ProviderUpdated;
import com.cs2gsi.nodes.Provider;
import com.cs2gsi.nodes.Weapon;
import com.cs2gsi.nodes.WeaponType;

import java.util.ArrayList;
import java.util.List;

class PlayerHandler extends EventHandler<CS2GameEvent> {
    private Provider providerCache = new Provider();

    PlayerHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(ProviderUpdated.class, this::onProviderUpdated);
        dispatcher.subscribe(PlayerUpdated.class, this::onPlayerUpdated);
        dispatcher.subscribe(PlayerStateChanged.class, this::onPlayerStateChanged);
        dispatcher.subscribe(PlayerStatsChanged.class, this::onPlayerStatsChanged);
    }

    private void onProviderUpdated(CS2GameEvent e) {
        if (!(e instanceof ProviderUpdated evt)) {
            return;
        }

        providerCache = evt.newValue;
    }

    private static Weapon findWeaponByName(List<Weapon> weapons, String name) {
        for (Weapon weapon : weapons) {
            if (weapon.name.equals(name)) {
                return weapon;
            }
        }

        return new Weapon();
    }

    private void onPlayerUpdated(CS2GameEvent e) {
        if (!(e instanceof PlayerUpdated evt)) {
            return;
        }

        if (!evt.newValue.steamId.equals(evt.previousValue.steamId)) {
            dispatcher.broadcast(new PlayerSteamIDChanged(evt.newValue.steamId, evt.previousValue.steamId, evt.newValue));

            if (!evt.newValue.steamId.equals(providerCache.steamId)) {
                // Switched spectating to another player, no need for updates.
                return;
            }
        }

        if (!evt.newValue.name.equals(evt.previousValue.name)) {
            dispatcher.broadcast(new PlayerNameChanged(evt.newValue.name, evt.previousValue.name, evt.newValue));
        }

        if (!evt.newValue.clan.equals(evt.previousValue.clan)) {
            dispatcher.broadcast(new PlayerClanChanged(evt.newValue.clan, evt.previousValue.clan, evt.newValue));
        }

        if (evt.newValue.xpOverloadLevel != evt.previousValue.xpOverloadLevel) {
            dispatcher.broadcast(new PlayerXPOverloadLevelChanged(evt.newValue.xpOverloadLevel, evt.previousValue.xpOverloadLevel, evt.newValue));
        }

        if (evt.newValue.observerSlot != evt.previousValue.observerSlot) {
            dispatcher.broadcast(new PlayerObserverSlotChanged(evt.newValue.observerSlot, evt.previousValue.observerSlot, evt.newValue));
        }

        if (!evt.newValue.spectationTarget.equals(evt.previousValue.spectationTarget)) {
            dispatcher.broadcast(new PlayerSpectationTargetChanged(evt.newValue.spectationTarget, evt.previousValue.spectationTarget, evt.newValue));
        }

        if (!evt.newValue.position.equals(evt.previousValue.position)) {
            dispatcher.broadcast(new PlayerPositionChanged(evt.newValue.position, evt.previousValue.position, evt.newValue));
        }

        if (!evt.newValue.forwardDirection.equals(evt.previousValue.forwardDirection)) {
            dispatcher.broadcast(new PlayerForwardDirectionChanged(evt.newValue.forwardDirection, evt.previousValue.forwardDirection, evt.newValue));
        }

        if (evt.newValue.team != evt.previousValue.team) {
            dispatcher.broadcast(new PlayerTeamChanged(evt.newValue.team, evt.previousValue.team, evt.newValue));
        }

        if (evt.newValue.activity != evt.previousValue.activity) {
            dispatcher.broadcast(new PlayerActivityChanged(evt.newValue.activity, evt.previousValue.activity, evt.newValue));
        }

        if (!evt.newValue.state.equals(evt.previousValue.state)) {
            dispatcher.broadcast(new PlayerStateChanged(evt.newValue.state, evt.previousValue.state, evt.newValue));
        }

        if (!evt.newValue.weapons.equals(evt.previousValue.weapons)) {
            List<Weapon> newWeapons = new ArrayList<>();
            List<Weapon> lostWeapons = new ArrayList<>();

            for (Weapon weapon : evt.newValue.weapons) {
                Weapon previousWeapon = findWeaponByName(evt.previousValue.weapons, weapon.name);

                if (!previousWeapon.isValid() && weapon.type != WeaponType.Knife && weapon.type != WeaponType.Fists) {
                    // The player did not previously have the weapon.
                    newWeapons.add(weapon);
                } else {
                    if (!weapon.name.equals(previousWeapon.name) && (evt.newValue.state.health > 0)) {
                        // Dead players cannot change their weapons.
                        dispatcher.broadcast(new PlayerWeaponChanged(weapon, previousWeapon, evt.newValue));
                    }

                    if (previousWeapon.isValid()) {
                        if (weapon.ammoClip != previousWeapon.ammoClip) {
                            dispatcher.broadcast(new PlayerWeaponAmmoClipChanged(weapon.ammoClip, previousWeapon.ammoClip, weapon, evt.newValue));
                        }

                        if (weapon.ammoReserve != previousWeapon.ammoReserve) {
                            dispatcher.broadcast(new PlayerWeaponAmmoReserveChanged(
                                    weapon.ammoReserve, previousWeapon.ammoReserve, weapon, evt.newValue));
                        }
                    }
                }
            }

            for (Weapon weapon : evt.previousValue.weapons) {
                Weapon newWeapon = findWeaponByName(evt.newValue.weapons, weapon.name);

                if (!newWeapon.isValid() && weapon.type != WeaponType.Knife && weapon.type != WeaponType.Fists) {
                    // The player no longer has the weapon.
                    lostWeapons.add(weapon);
                }
            }

            if (!newWeapons.isEmpty()) {
                dispatcher.broadcast(new PlayerWeaponsPickedUp(newWeapons, evt.newValue));
            }

            if (!lostWeapons.isEmpty()) {
                dispatcher.broadcast(new PlayerWeaponsDropped(lostWeapons, evt.newValue));
            }

            Weapon activeWeapon = evt.newValue.getActiveWeapon();
            Weapon previousActiveWeapon = evt.previousValue.getActiveWeapon();

            if (!activeWeapon.name.equals(previousActiveWeapon.name) && (evt.newValue.state.health > 0)) {
                // Dead players cannot change their weapons.
                dispatcher.broadcast(new PlayerActiveWeaponChanged(activeWeapon, previousActiveWeapon, evt.newValue));
            }
        }

        if (!evt.newValue.matchStats.equals(evt.previousValue.matchStats)) {
            dispatcher.broadcast(new PlayerStatsChanged(evt.newValue.matchStats, evt.previousValue.matchStats, evt.newValue));
        }

        if ((evt.newValue.state.roundKills > evt.previousValue.state.roundKills)
                && (evt.previousValue.state.roundKills != -1)) {
            boolean gotAHeadshot = evt.newValue.state.roundHSKills > evt.previousValue.state.roundHSKills;
            Weapon activeWeapon = evt.newValue.getActiveWeapon();

            if (!activeWeapon.isValid()) {
                activeWeapon = evt.previousValue.getActiveWeapon();
            }

            dispatcher.broadcast(new PlayerGotKill(gotAHeadshot, activeWeapon, evt.newValue.state.roundKills >= 5, evt.newValue));
        }
    }

    private void onPlayerStateChanged(CS2GameEvent e) {
        if (!(e instanceof PlayerStateChanged evt)) {
            return;
        }

        if (evt.newValue.health != evt.previousValue.health) {
            dispatcher.broadcast(new PlayerHealthChanged(evt.newValue.health, evt.previousValue.health, evt.player));

            if (evt.previousValue.health > evt.newValue.health) {
                dispatcher.broadcast(new PlayerTookDamage(evt.newValue.health, evt.previousValue.health, evt.player));
            }

            if (evt.newValue.health == 0) {
                dispatcher.broadcast(new PlayerDied(evt.newValue.health, evt.previousValue.health, evt.player));
            } else if (evt.newValue.health > 0 && evt.previousValue.health == 0) {
                dispatcher.broadcast(new PlayerRespawned(evt.newValue.health, evt.previousValue.health, evt.player));
            }
        }

        if (evt.newValue.armor != evt.previousValue.armor) {
            dispatcher.broadcast(new PlayerArmorChanged(evt.newValue.armor, evt.previousValue.armor, evt.player));
        }

        if (evt.newValue.hasHelmet != evt.previousValue.hasHelmet) {
            dispatcher.broadcast(new PlayerHelmetChanged(evt.newValue.hasHelmet, evt.previousValue.hasHelmet, evt.player));
        }

        if (evt.newValue.flashAmount != evt.previousValue.flashAmount) {
            dispatcher.broadcast(new PlayerFlashAmountChanged(evt.newValue.flashAmount, evt.previousValue.flashAmount, evt.player));
        }

        if (evt.newValue.smokedAmount != evt.previousValue.smokedAmount) {
            dispatcher.broadcast(new PlayerSmokedAmountChanged(evt.newValue.smokedAmount, evt.previousValue.smokedAmount, evt.player));
        }

        if (evt.newValue.burningAmount != evt.previousValue.burningAmount) {
            dispatcher.broadcast(new PlayerBurningAmountChanged(evt.newValue.burningAmount, evt.previousValue.burningAmount, evt.player));
        }

        if (evt.newValue.money != evt.previousValue.money) {
            dispatcher.broadcast(new PlayerMoneyAmountChanged(evt.newValue.money, evt.previousValue.money, evt.player));
        }

        if (evt.newValue.roundKills != evt.previousValue.roundKills) {
            dispatcher.broadcast(new PlayerRoundKillsChanged(evt.newValue.roundKills, evt.previousValue.roundKills, evt.player));
        }

        if (evt.newValue.roundHSKills != evt.previousValue.roundHSKills) {
            dispatcher.broadcast(new PlayerRoundHeadshotKillsChanged(evt.newValue.roundHSKills, evt.previousValue.roundHSKills, evt.player));
        }

        if (evt.newValue.roundTotalDamage != evt.previousValue.roundTotalDamage) {
            dispatcher.broadcast(new PlayerRoundTotalDamageChanged(evt.newValue.roundTotalDamage, evt.previousValue.roundTotalDamage, evt.player));
        }

        if (evt.newValue.equipmentValue != evt.previousValue.equipmentValue) {
            dispatcher.broadcast(new PlayerEquipmentValueChanged(evt.newValue.equipmentValue, evt.previousValue.equipmentValue, evt.player));
        }

        if (evt.newValue.hasDefuseKit != evt.previousValue.hasDefuseKit) {
            dispatcher.broadcast(new PlayerDefusekitChanged(evt.newValue.hasDefuseKit, evt.previousValue.hasDefuseKit, evt.player));
        }
    }

    private void onPlayerStatsChanged(CS2GameEvent e) {
        if (!(e instanceof PlayerStatsChanged evt)) {
            return;
        }

        if (evt.newValue.kills != evt.previousValue.kills) {
            dispatcher.broadcast(new PlayerKillsChanged(evt.newValue.kills, evt.previousValue.kills, evt.player));
        }

        if (evt.newValue.assists != evt.previousValue.assists) {
            dispatcher.broadcast(new PlayerAssistsChanged(evt.newValue.assists, evt.previousValue.assists, evt.player));
        }

        if (evt.newValue.deaths != evt.previousValue.deaths) {
            dispatcher.broadcast(new PlayerDeathsChanged(evt.newValue.deaths, evt.previousValue.deaths, evt.player));
        }

        if (evt.newValue.mvps != evt.previousValue.mvps) {
            dispatcher.broadcast(new PlayerMVPsChanged(evt.newValue.mvps, evt.previousValue.mvps, evt.player));
        }

        if (evt.newValue.score != evt.previousValue.score) {
            dispatcher.broadcast(new PlayerScoreChanged(evt.newValue.score, evt.previousValue.score, evt.player));
        }
    }
}
