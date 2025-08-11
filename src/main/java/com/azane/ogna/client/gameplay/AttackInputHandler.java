package com.azane.ogna.client.gameplay;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.client.gui.hud.OgnaHuds;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.AsyncHandler;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_server.InputAttackPacket;
import com.azane.ogna.network.to_server.InputReloadPacket;
import com.azane.ogna.registry.ModAttribute;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

import static com.azane.ogna.client.lib.InputExtraCheck.isInGame;

/**
 * @author azaneNH37 (2025-08-10)
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID, value = Dist.CLIENT)
public class AttackInputHandler
{
    public static final KeyMapping RELOAD_KEY = new KeyMapping("key.ognmarts.reload.desc",
        KeyConflictContext.IN_GAME,
        KeyModifier.NONE,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.category.ognmarts");

    private static final AttackStateManager stateManager = AttackStateManager.getInstance();
    private static boolean wasMousePressed = false;
    private static long mouseHoldStartTime = 0;

    @SubscribeEvent
    public static void onLocalPlayerLogIn(ClientPlayerNetworkEvent.LoggingIn event)
    {
        DebugLogger.log("register player attack state");
        stateManager.registerPlayer(event.getPlayer());
    }
    @SubscribeEvent
    public static void onLocalPlayerLogOut(ClientPlayerNetworkEvent.LoggingOut event)
    {
        if(event.getPlayer() != null)
        {
            DebugLogger.log("remove player attack state");
            stateManager.removePlayer(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !isInGame()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        boolean isMousePressed = mc.options.keyAttack.isDown();

        // 处理鼠标输入
        handleMouseInput(mc.player, isMousePressed);

        wasMousePressed = isMousePressed;
    }

    @SubscribeEvent
    public static void onReloadPressInput(InputEvent.Key event)
    {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && RELOAD_KEY.matches(event.getKey(), event.getScanCode())) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            onReloadPressed(player);
        }
    }

    private static void handleMouseInput(Player player, boolean isMousePressed) {
        if (isMousePressed && !wasMousePressed) {
            // 鼠标刚按下
            onMousePressed(player);
        } else if (!isMousePressed && wasMousePressed) {
            // 鼠标刚释放
            onMouseReleased(player);
        } else if (isMousePressed && wasMousePressed) {
            // 鼠标持续按住
            onMouseHeld(player);
        }
    }

    private static void onMousePressed(Player player) {
        mouseHoldStartTime = System.currentTimeMillis();
        UUID playerId = player.getUUID();

        ItemStack mainHand = player.getMainHandItem();
        if (!IOgnaWeapon.isWeapon(mainHand)) return;

        IOgnaWeapon weapon = (IOgnaWeapon) mainHand.getItem();

        //DebugLogger.log("try enter charging");

        // 检查是否能攻击
        if (!stateManager.canPerformAction(playerId, AttackStateManager.ActionType.ATTACK)) {
            return;
        }

        //if (!weapon.canAttack(mainHand, player, AttackType.CLICK)) {
        //    return;
        //}

        // 开始蓄力
        String weaponUUID = weapon.getOrCreateStackUUID(mainHand);
        stateManager.startCharging(playerId, weaponUUID,(long) weapon.getWeaponCap(mainHand).submitAttrVal(
            ModAttribute.WEAPON_MAX_CHARGE_TIME.get(), player, mainHand,
            weapon.getWeaponCap(mainHand).getBaseAttrVal(ModAttribute.WEAPON_MAX_CHARGE_TIME.get(), mainHand)));
    }

    private static void onMouseReleased(Player player) {
        UUID playerId = player.getUUID();

        if(!stateManager.isState(playerId,AttackState.CHARGING)) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!IOgnaWeapon.isWeapon(mainHand))
        {
            stateManager.stopCharging(playerId);
            return;
        }

        IOgnaWeapon weapon = (IOgnaWeapon) mainHand.getItem();

        long chargeTime = stateManager.stopCharging(playerId);
        long holdTime = System.currentTimeMillis() - mouseHoldStartTime;

        // 判断攻击类型
        AttackType attackType;
        long maxChargeTime = (long) weapon.getWeaponCap(mainHand).submitAttrVal(
            ModAttribute.WEAPON_MAX_CHARGE_TIME.get(), player, mainHand,
            weapon.getWeaponCap(mainHand).getBaseAttrVal(ModAttribute.WEAPON_MAX_CHARGE_TIME.get(), mainHand));

        if (holdTime < 200) {
            attackType = AttackType.SIMPLE;
            chargeTime = 0;
        } else {
            attackType = AttackType.CHARGE;
            chargeTime = Math.min(chargeTime, maxChargeTime);
        }

        // 再次检查是否能攻击
        //if (!weapon.canAttack(mainHand, player, attackType)) {
        //    return;
        //}

        // 执行攻击
        executeAttack(player, mainHand, weapon, attackType, chargeTime);
    }

    private static void onMouseHeld(Player player) {
        UUID playerId = player.getUUID();
        if(!stateManager.isState(playerId,AttackState.CHARGING)) return;
        /*
        ItemStack mainHand = player.getMainHandItem();
        if (!WeaponUtils.isCustomWeapon(mainHand)) return;

        IOgnaWeapon weapon = (IOgnaWeapon) mainHand.getItem();
        long currentChargeTime = System.currentTimeMillis() - mouseHoldStartTime;
        long maxChargeTime = weapon.getMaxChargeTime(mainHand);

        // 蓄力到最大值时的效果
        if (currentChargeTime >= maxChargeTime) {
            playChargeCompleteEffect(player);
        }
         */
    }

    private static void onReloadPressed(Player player) {
        UUID playerId = player.getUUID();

        ItemStack mainHand = player.getMainHandItem();
        if (!IOgnaWeapon.isWeapon(mainHand)) return;

        IOgnaWeapon weapon = (IOgnaWeapon) mainHand.getItem();

        //DebugLogger.log("try reload");

        // 检查是否能重装
        if (!stateManager.canPerformAction(playerId, AttackStateManager.ActionType.RELOAD)) {
            return;
        }

        //if (!weapon.canReload(mainHand, player)) {
        //    return;
        //}

        // 执行重装
        executeReload(player, mainHand, weapon);
    }

    private static void executeAttack(Player player, ItemStack weapon, IOgnaWeapon weaponImpl,
                                      AttackType attackType, long chargeTime) {
        UUID playerId = player.getUUID();
        IOgnaWeaponCap weaponCap = weaponImpl.getWeaponCap(weapon);

        // 设置客户端冷却
        long cooldown = (long) weaponCap.submitAttrVal(ModAttribute.WEAPON_ATTACK_CD.get(), player, weapon,
            weaponCap.getBaseAttrVal(ModAttribute.WEAPON_ATTACK_CD.get(), weapon));
        //DebugLogger.log("{}",cooldown);
        stateManager.setAttackCooldown(playerId, cooldown);

        // 发送攻击包到服务端
        sendAttackPacket(player, attackType, chargeTime, weaponImpl.getStackUUID(weapon));

        // 异步处理客户端特效（上传到客户端主线程）
        Minecraft.getInstance().submitAsync(()->weaponImpl.onClientEffects(weapon, player, attackType, chargeTime));
    }

    private static void executeReload(Player player, ItemStack weapon, IOgnaWeapon weaponImpl) {
        UUID playerId = player.getUUID();
        IOgnaWeaponCap weaponCap = weaponImpl.getWeaponCap(weapon);

        // 获取重装时间和武器UUID
        long reloadTime = (long) weaponCap.submitAttrVal(ModAttribute.WEAPON_RELOAD_CD.get(), player, weapon,
            weaponCap.getBaseAttrVal(ModAttribute.WEAPON_RELOAD_CD.get(), weapon));
        String weaponUUID = weaponImpl.getOrCreateStackUUID(weapon);

        // 设置重装状态
        stateManager.startReloading(playerId, reloadTime, weaponUUID);
        OgnaHuds.RELOAD_STATUS_HUD.refreshReloadState(ReloadState.RELOADING, reloadTime);
        // 发送重装包到服务端
        sendReloadPacket(player, weaponUUID);
        // 异步处理客户端重装特效
        //Minecraft.getInstance().submitAsync(()->weaponImpl.onClientReloadEffects(weapon, player));

        // 设置重装完成的回调
        scheduleReloadCompletion(player, reloadTime, weaponUUID,weaponImpl);
    }

    private static void scheduleReloadCompletion(Player player, long reloadTime, String weaponUUID,IOgnaWeapon weaponImpl) {
        AsyncHandler.delayExecute(reloadTime,() -> {
            // 验证武器仍然在主手且UUID匹配
            ItemStack currentMainHand = player.getMainHandItem();
            if (weaponImpl.isStackMatching(currentMainHand, weaponUUID)) {
                // 发送重装完成包到服务端
                sendReloadCompletePacket(player, weaponUUID);
            }
            else OgnaHuds.RELOAD_STATUS_HUD.refreshReloadState(ReloadState.WEAPON_MISMATCH,2000);
        });
    }

    private static void playChargeCompleteEffect(Player player) {
        // 蓄力完成效果
    }

    private static void sendAttackPacket(Player player, AttackType attackType, long chargeTime,String weaponUUID) {
        //DebugLogger.error("send");
        OgnmChannel.DEFAULT.sendToServer(new InputAttackPacket(attackType,chargeTime,weaponUUID));
    }

    private static void sendReloadPacket(Player player, String weaponUUID) {
        OgnmChannel.DEFAULT.sendToServer(new InputReloadPacket(InputReloadPacket.ReloadType.START,weaponUUID));
    }

    private static void sendReloadCompletePacket(Player player, String weaponUUID) {
        OgnmChannel.DEFAULT.sendToServer(new InputReloadPacket(InputReloadPacket.ReloadType.END,weaponUUID));
    }
}
