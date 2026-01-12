package com.rexcantor64.triton.packetinterceptor.handlers;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.rexcantor64.triton.Triton;
import com.rexcantor64.triton.api.language.TranslationResult;
import com.rexcantor64.triton.config.MainConfig;
import com.rexcantor64.triton.language.parser.MessageParser;
import com.rexcantor64.triton.player.TritonLanguagePlayer;
import com.rexcantor64.triton.utils.ComponentUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SignPacketHandler {

    private final @NotNull MessageParser parser;
    private final @NotNull MainConfig.FeatureSyntax syntax;

    public SignPacketHandler(@NotNull MessageParser parser, @NotNull MainConfig config) {
        this.parser = parser;
        this.syntax = config.getSignsSyntax();
    }

    public void onBlockEntityDataPacket(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        WrapperPlayServerBlockEntityData packet = new WrapperPlayServerBlockEntityData(event);

        NBTCompound nbt = packet.getNBT();
        if (nbt == null) return;

        NBTString typeId = nbt.getStringTagOrNull("id");
        if (typeId == null) return;

        String idValue = typeId.getValue();
        if (!idValue.equals("minecraft:sign") && !idValue.equals("minecraft:hanging_sign")) {
            return;
        }

        if (translateSignNbt(nbt, languagePlayer)) {
            event.markForReEncode(true);
        }
    }

    private boolean translateSignNbt(NBTCompound nbt, TritonLanguagePlayer<?> player) {
        NBTCompound frontText = nbt.getCompoundTagOrNull("front_text");
        NBTCompound backText = nbt.getCompoundTagOrNull("back_text");

        if (frontText != null) {
            translateSignSide(frontText, player, true);
        }
        if (backText != null) {
            translateSignSide(backText, player, false);
        }

        return true;
    }

    private void translateSignSide(NBTCompound sideText, TritonLanguagePlayer<?> player, boolean isFront) {
        NBTCompound filteredText = sideText.getCompoundTagOrNull("filtered_tags");
        if (filteredText == null) return;

        for (int i = 0; i < 4; i++) {
            String lineKey = "Text" + i;
            NBTString lineTag = filteredText.getStringTagOrNull(lineKey);
            if (lineTag == null) continue;

            String originalJson = lineTag.getValue();
            if (originalJson.isEmpty()) continue;

            try {
                Component originalComponent = ComponentUtils.deserializeFromJson(originalJson);
                TranslationResult<Component> result = parser.translateComponent(originalComponent, player, syntax);
                if (result.isChanged()) {
                    final int index = i;
                    result.getResult().ifPresent(c -> {
                        filteredText.setTag("Text" + index, new NBTString(ComponentUtils.serializeToJson(c)));
                    });
                }
            } catch (Exception e) {
                Triton.get().getLogger().logError(e, "Failed to parse sign line %1 (%2)", i + 1, isFront ? "front" : "back");
            }
        }
    }
}
