package com.azane.ogna.client.gameplay;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AttackStateManager
{
    private static final AttackStateManager INSTANCE = new AttackStateManager();
    private final ConcurrentHashMap<UUID, AttackStateData> playerStates = new ConcurrentHashMap<>();

    public static AttackStateManager getInstance() {
        return INSTANCE;
    }

    public void registerPlayer(Player player)
    {
        playerStates.computeIfAbsent(player.getUUID(),(uuid)-> new AttackStateData());
    }

    public void removePlayer(Player player)
    {
        playerStates.remove(player.getUUID());
    }

    public AttackState getAttackState(UUID playerId) {
        AttackStateData state = playerStates.get(playerId);
        if (state == null) return AttackState.UNKNOWN;

        long currentTime = System.currentTimeMillis();

        // 检查状态是否过期
        if (state.stateEndTime > 0 && currentTime >= state.stateEndTime) {
            state.weaponState = AttackState.IDLE;
            state.stateEndTime = 0;
            state.weaponUUID = null;
        }

        return state.weaponState;
    }

    public boolean canPerformAction(UUID playerId, ActionType actionType) {
        AttackState currentState = getAttackState(playerId);

        return switch (actionType) {
            case ATTACK, RELOAD -> currentState == AttackState.IDLE;
            case CHARGE -> currentState == AttackState.IDLE || currentState == AttackState.CHARGING;
        };
    }

    public void setAttackState(UUID playerId, AttackState state, long durationMs, String weaponUUID) {
        playerStates.compute(playerId, (id, stateData) -> {
            if (stateData == null) stateData = new AttackStateData();
            stateData.weaponState = state;
            stateData.stateEndTime = durationMs > 0 ? System.currentTimeMillis() + durationMs : 0;
            stateData.weaponUUID = weaponUUID;
            if (state == AttackState.CHARGING) {
                stateData.chargeStartTime = System.currentTimeMillis();
            }
            return stateData;
        });
    }

    public void startCharging(UUID playerId, String weaponUUID) {
        setAttackState(playerId, AttackState.CHARGING, 0, weaponUUID);
    }

    public long stopCharging(UUID playerId) {
        AttackStateData state = playerStates.get(playerId);
        if (state == null || state.weaponState != AttackState.CHARGING) return 0;

        long chargeTime = System.currentTimeMillis() - state.chargeStartTime;
        setAttackState(playerId, AttackState.IDLE, 0, null);
        return chargeTime;
    }

    public void startReloading(UUID playerId, long reloadTimeMs, String weaponUUID) {
        setAttackState(playerId, AttackState.RELOADING, reloadTimeMs, weaponUUID);
    }

    public void setAttackCooldown(UUID playerId, long cooldownMs) {
        setAttackState(playerId, AttackState.COOLDOWN, cooldownMs, null);
    }

    public boolean isState(UUID playerId,AttackState state) {
        return getAttackState(playerId) == state;
    }

    public String getReloadingWeaponUUID(UUID playerId) {
        AttackStateData state = playerStates.get(playerId);
        if (state == null || state.weaponState != AttackState.RELOADING) return null;
        return state.weaponUUID;
    }

    private static class AttackStateData {
        volatile AttackState weaponState = AttackState.IDLE;
        volatile long stateEndTime = 0;
        volatile long chargeStartTime = 0;
        volatile String weaponUUID = null; // 执行动作时的武器UUID
    }

    public enum ActionType {
        ATTACK, RELOAD, CHARGE
    }
}
