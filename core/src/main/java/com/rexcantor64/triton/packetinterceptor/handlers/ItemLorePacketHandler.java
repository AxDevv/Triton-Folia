package com.rexcantor64.triton.packetinterceptor.handlers;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentType;
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

import java.lang.reflect.Field;
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
        ComponentType<Component> itemNameType = getComponentType("item_name");
        ComponentType<Component> customNameType = getComponentType("custom_name");

        System.out.println("[Triton Item] item_name type: " + itemNameType + ", custom_name type: " + customNameType);

        if (itemNameType != null) {
            java.util.Optional<Component> nameOpt = item.getComponent(itemNameType);
            System.out.println("[Triton Item] item_name present: " + nameOpt.isPresent());
            if (nameOpt.isPresent()) {
                Component name = nameOpt.get();
                System.out.println("[Triton Item] item_name: " + name);
                TranslationResult<Component> result = parser.translateComponent(name, player, syntax);
                if (result.isChanged()) {
                    result.getResult().ifPresent(c -> item.setComponent(itemNameType, c));
                    modified = true;
                }
            }
        }

        if (customNameType != null) {
            java.util.Optional<Component> customNameOpt = item.getComponent(customNameType);
            System.out.println("[Triton Item] custom_name present: " + customNameOpt.isPresent());
            if (customNameOpt.isPresent()) {
                Component name = customNameOpt.get();
                System.out.println("[Triton Item] custom_name: " + name);
                TranslationResult<Component> result = parser.translateComponent(name, player, syntax);
                if (result.isChanged()) {
                    result.getResult().ifPresent(c -> item.setComponent(customNameType, c));
                    modified = true;
                }
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

    @SuppressWarnings("unchecked")
    private ComponentType<Component> getComponentType(String name) {
        try {
            Field field = ComponentTypes.class.getField(name);
            return (ComponentType<Component>) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
