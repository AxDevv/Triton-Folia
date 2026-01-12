package com.rexcantor64.triton.packetinterceptor.handlers;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.advancements.Advancement;
import com.github.retrooper.packetevents.protocol.advancements.AdvancementDisplay;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAdvancements;
import com.rexcantor64.triton.api.language.TranslationResult;
import com.rexcantor64.triton.config.MainConfig;
import com.rexcantor64.triton.language.parser.MessageParser;
import com.rexcantor64.triton.player.TritonLanguagePlayer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class AdvancementsPacketHandler {

    private final @NotNull MessageParser parser;
    private final @NotNull MainConfig.FeatureSyntax syntax;

    public AdvancementsPacketHandler(@NotNull MessageParser parser, @NotNull MainConfig config) {
        this.parser = parser;
        this.syntax = config.getAdvancementsSyntax();
    }

    public void onUpdateAdvancementsPacket(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        WrapperPlayServerUpdateAdvancements packet = new WrapperPlayServerUpdateAdvancements(event);

        boolean modified = false;
        List<?> holders = packet.getAddedAdvancements();
        for (Object holderObj : holders) {
            com.github.retrooper.packetevents.protocol.advancements.AdvancementHolder holder =
                (com.github.retrooper.packetevents.protocol.advancements.AdvancementHolder) holderObj;
            Advancement advancement = holder.getAdvancement();
            AdvancementDisplay display = advancement.getDisplay();
            if (display == null) continue;

            Component title = display.getTitle();
            TranslationResult<Component> result = parser.translateComponent(title, languagePlayer, syntax);
            if (result.isChanged()) {
                result.getResult().ifPresent(display::setTitle);
                modified = true;
            }

            Component description = display.getDescription();
            TranslationResult<Component> resultDesc = parser.translateComponent(description, languagePlayer, syntax);
            if (resultDesc.isChanged()) {
                resultDesc.getResult().ifPresent(display::setDescription);
                modified = true;
            }
        }

        if (modified) {
            event.markForReEncode(true);
        }
    }
}
