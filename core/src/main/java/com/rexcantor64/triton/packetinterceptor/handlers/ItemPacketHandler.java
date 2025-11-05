package com.rexcantor64.triton.packetinterceptor.handlers;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import com.rexcantor64.triton.api.language.Localized;
import com.rexcantor64.triton.api.language.TranslationResult;
import com.rexcantor64.triton.config.MainConfig;
import com.rexcantor64.triton.language.parser.MessageParser;
import com.rexcantor64.triton.player.TritonLanguagePlayer;
import com.rexcantor64.triton.utils.ComponentUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ItemPacketHandler {

    private final @NotNull MessageParser parser;
    private final @NotNull MainConfig.FeatureSyntax syntax;

    public ItemPacketHandler(@NotNull MessageParser parser, @NotNull MainConfig config) {
        this.parser = parser;
        this.syntax = config.getItemsSyntax();
    }

    public void onSetCursorItemPacket(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        val packet = new WrapperPlayServerSetCursorItem(event);

        System.out.println("packet.getStack() = " + packet.getStack());
    }

    public void onSetPlayerInventoryPacket(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        val packet = new WrapperPlayServerSetPlayerInventory(event);

        System.out.println("packet.getStack() = " + packet.getStack());
    }

    public void onSetSlotPacket(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        val packet = new WrapperPlayServerSetSlot(event);

        val changed = translateItem(packet.getItem(), languagePlayer);
        if (changed) {
            event.markForReEncode(true);
        }
    }

    public void onWindowItemsPacket(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        val packet = new WrapperPlayServerWindowItems(event);

        System.out.println("packet.getItems() = " + packet.getItems());
        System.out.println("packet.getCarriedItem() = " + packet.getCarriedItem());
    }

    private boolean translateItem(ItemStack item, Localized locale) {
        AtomicBoolean changed = new AtomicBoolean(false);

        val loreOpt = item.getComponent(ComponentTypes.LORE);
        if (loreOpt.isPresent()) {
            val lore = loreOpt.get();
            AtomicBoolean loreChanged = new AtomicBoolean(false);
            List<Component> newLines = new ArrayList<>();
            for (Component line : lore.getLines()) {
                parser.translateComponent(
                                line,
                                locale,
                                this.syntax
                        )
                        .map(ComponentUtils::splitByNewLine)
                        .ifChanged(result -> {
                            newLines.addAll(
                                    result.stream()
                                            .map(ComponentUtils::ensureNotItalic)
                                            .collect(Collectors.toList())
                            );
                            loreChanged.set(true);
                        })
                        .ifUnchanged(() -> newLines.add(line))
                        .ifToRemove(() -> loreChanged.set(true));
            }
            if (loreChanged.get()) {
                lore.setLines(newLines);
                changed.set(true);
            }
        }

        return changed.get();
    }
}
