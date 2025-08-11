package com.azane.ogna.client.gui.hud;

import java.util.List;

/**
 * @author azaneNH37 (2025-08-10)
 */
public class OgnaHuds
{
    public static final AttackStateHud ATTACK_STATE = new AttackStateHud();
    public static final SkillHud SKILL_ICON = new SkillHud();
    public static final WeaponHud WEAPON_HUD = new WeaponHud();
    public static final ReloadStatusHud RELOAD_STATUS_HUD = new ReloadStatusHud();

    public static final WindowHud GAME_WINDOW = new WindowHud(List.of(ATTACK_STATE,WEAPON_HUD,SKILL_ICON,RELOAD_STATUS_HUD));
}
