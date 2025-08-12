package com.azane.ogna.client.gui.ldlib.custom;

import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.lib.ColorHelper;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.azane.ogna.client.gui.ldlib.custom.MenuItemWidget.BUTTON_HOVER;
import static com.azane.ogna.client.gui.ldlib.custom.MenuItemWidget.MENU_BG;
import static com.azane.ogna.lib.RegexHelper.*;

/**
 * @author azaneNH37 (2025-08-02)
 */
@LDLRegister(name = "chip",group = "widget.custom")
public class ChipWidget extends WidgetGroup
{
    public static final ColorRectTexture SWITCH_ACTIVE = new ColorRectTexture(0x44FFFFFF);

    @Nullable
    @Getter
    private IChip chip;

    public ChipWidget()
    {
        super(0,0,90,22);
        setBackground(MENU_BG.copy());
        var switcher = new SwitchWidget(3,3,84,16,(cd,b)->{});
        switcher.setId("switch");
        switcher.setHoverTexture(BUTTON_HOVER.copy());
        switcher.setPressedTexture(SWITCH_ACTIVE.copy());
        addWidget(switcher);
        var slotWidget = new SlotWidget().setId("item");
        slotWidget.setSelfPosition(4,2);
        addWidget(slotWidget);
        var text1 = new TextTextureWidget(24,3,45,18);
        text1.setId("name");
        text1.getTextTexture().setType(TextTexture.TextType.LEFT_ROLL);
        text1.getTextTexture().setRollSpeed(0.4f);
        addWidget(text1);
        var text2 = new TextTextureWidget(70,3,17,18);
        text2.setId("amt");
        text2.getTextTexture().setType(TextTexture.TextType.RIGHT);
        addWidget(text2);
    }

    @Override
    public void initTemplate()
    {
        super.initTemplate();
        setBackground(MENU_BG.copy());
    }

    @Override
    public void initWidget()
    {
        super.initWidget();
        SlotWidget widget = UiHelper.getAsNonnull(SlotWidget.class,endWith("item"),widgets);
        widget.setCanPutItems(false);
        widget.setCanTakeItems(false);
    }

    public void injectChipData(ChipSet chipSet, IChip chip, Container displayContainer, int index, BiConsumer<ClickData,Boolean> switchAction)
    {
        var item = UiHelper.getAsNonnull(SlotWidget.class,endWith("item"),widgets);
        var switcher = UiHelper.getAsNonnull(SwitchWidget.class,endWith("switch"),widgets);
        var name = UiHelper.getAsNonnull(TextTextureWidget.class,endWith("name"),widgets);

        this.chip = chip;
        item.setContainerSlot(displayContainer,index);
        item.setItem(chip.buildItemStack(1));
        name.setLastComponent(Component.translatable(chip.getDisplayContext().getName()));
        //DebugLogger.log(name.getLastComponent().getString());
        name.getTextTexture().setColor(chip.getDisplayContext().getColor());
        refreshChipAmount(chipSet,chip);
        switcher.setOnPressCallback(switchAction);
    }

    public int refreshChipAmount(ChipSet chipSet,IChip chip)
    {
        var amt = UiHelper.getAsNonnull(TextTextureWidget.class,endWith("amt"),widgets);
        int amount = chipSet.getChipCount(chip.getId());
        amt.setLastComponent(Component.literal(String.valueOf(amount)));
        //amt.setText();
       // DebugLogger.log(amt.getLastComponent().getString());
        amt.getTextTexture().setColor(ColorHelper.getGradientColor(amount));
        return amount;
    }

    public static void refreshChipList(DraggableScrollableWidgetGroup chipList, ChipSet chipSet,Container displayContainer,Consumer<ChipWidget> feedBack)
    {
        chipList.clearAllWidgets();
        displayContainer.clearContent();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        var storedChips = chipSet.getStoredChips(IChip::isItem);
        //DebugLogger.log("chip size: {}",storedChips.size());
        chipSet.getStoredChips(IChip::isItem).forEach(ichip -> {
            var cw = new ChipWidget();
            cw.injectChipData(chipSet,ichip, displayContainer, atomicInteger.getAndIncrement(), (cd, b) -> {
                if(b)
                {
                    chipList.widgets.forEach(widget -> {
                        if(widget instanceof ChipWidget chipWidget)
                        {
                            var switcher = UiHelper.getAsNonnull(SwitchWidget.class,endWith("switch"),chipWidget.widgets);
                            switcher.setPressed(false);
                        }
                    });
                    var switcher = UiHelper.getAsNonnull(SwitchWidget.class,endWith("switch"),cw.widgets);
                    switcher.setPressed(true);
                    feedBack.accept(cw);
                }
                else {
                    feedBack.accept(null);
                }
            });
            chipList.addWidget(cw);
        });
        chipList.setLayout(Layout.VERTICAL_CENTER);
        chipList.setLayout(Layout.NONE);
        chipList.computeMax();
    }
}
