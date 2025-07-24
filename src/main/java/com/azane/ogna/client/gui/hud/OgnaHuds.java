package com.azane.ogna.client.gui.hud;

import java.util.List;

public class OgnaHuds
{
    public static final AttackStateHud ATTACK_STATE = new AttackStateHud();
    public static final SkillHud SKILL_ICON = new SkillHud();
    public static final WeaponHud WEAPON_HUD = new WeaponHud();

    public static final WindowHud GAME_WINDOW = new WindowHud(List.of(ATTACK_STATE,WEAPON_HUD,SKILL_ICON));
}
