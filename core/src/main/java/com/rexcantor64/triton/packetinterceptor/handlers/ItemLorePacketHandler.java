package com.rexcantor64.triton.packetinterceptor.handlers;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import com.rexcantor64.triton.api.language.TranslationResult;
import com.rexcantor64.triton.config.MainConfig;
import com.rexcantor64.triton.language.parser.MessageParser;
import com.rexcantor64.triton.player.TritonLanguagePlayer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ItemLorePacketHandler {

    private final @NotNull MessageParser parser;
    private final @NotNull MainConfig.FeatureSyntax syntax;

    public ItemLorePacketHandler(@NotNull MessageParser parser, @NotNull MainConfig config) {
        this.parser = parser;
        this.syntax = config.getItemsSyntax();
    }

    public void onWindowItems(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);

        List<ItemStack> items = packet.getItems();
        boolean modified = false;

        for (ItemStack item : items) {
            if (translateItemLore(item, languagePlayer)) {
                modified = true;
            }
        }

        if (modified) {
            event.markForReEncode(true);
        }
    }

    public void onSetSlot(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);

        ItemStack item = packet.getItem();
        if (item != null && translateItemLore(item, languagePlayer)) {
            event.markForReEncode(true);
        }
    }

    private boolean translateItemLore(ItemStack item, TritonLanguagePlayer<?> player) {
        if (item.isEmpty()) {
            return false;
        }

        boolean modified = false;

        java.util.Optional<Component> nameOpt = item.getComponent(ComponentTypes.ITEM_NAME);
        if (nameOpt.isPresent()) {
            Component name = nameOpt.get();
            TranslationResult<Component> result = parser.translateComponent(name, player, syntax);
            if (result.isChanged()) {
                result.getResult().ifPresent(c -> item.setComponent(ComponentTypes.ITEM_NAME, c));
                modified = true;
            }
        }

        java.util.Optional<ItemLore> loreOpt = item.getComponent(ComponentTypes.LORE);
        if (loreOpt.isPresent()) {
            ItemLore lore = loreOpt.get();
            List<Component> lines = lore.getLines();
            if (lines != null && !lines.isEmpty()) {
                List<Component> translatedLines = new ArrayList<Component>();
                boolean loreModified = false;
                for (Component line : lines) {
                    TranslationResult<Component> result = parser.translateComponent(line, player, syntax);
                    if (result.isChanged()) {
                        result.getResult().ifPresent(translatedLines::add);
                        loreModified = true;
                    } else {
                        translatedLines.add(line);
                    }
                }
                if (loreModified) {
                    lore.setLines(translatedLines);
                    item.setComponent(ComponentTypes.LORE, lore);
                    modified = true;
                }
            }
        }

        return modified;
    }
}
