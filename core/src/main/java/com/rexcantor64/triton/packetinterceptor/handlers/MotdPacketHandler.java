package com.rexcantor64.triton.packetinterceptor.handlers;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.status.server.WrapperStatusServerResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rexcantor64.triton.api.language.TranslationResult;
import com.rexcantor64.triton.config.MainConfig;
import com.rexcantor64.triton.language.parser.MessageParser;
import com.rexcantor64.triton.player.TritonLanguagePlayer;
import com.rexcantor64.triton.utils.ComponentUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Optional;

@RequiredArgsConstructor
public class MotdPacketHandler {

    private final @NotNull MessageParser parser;
    private final @NotNull MainConfig.FeatureSyntax syntax;

    public MotdPacketHandler(@NotNull MessageParser parser, @NotNull MainConfig config) {
        this.parser = parser;
        this.syntax = config.getMotdSyntax();
    }

    public void onServerInfoPacket(@NotNull PacketSendEvent event, @NotNull TritonLanguagePlayer<?> languagePlayer) {
        WrapperStatusServerResponse packet = new WrapperStatusServerResponse(event);

        Optional<String> ipAddr = getPlayerIpAddress(event.getUser());
        if (!ipAddr.isPresent()) {
            return;
        }

        com.rexcantor64.triton.api.language.Language lang = com.rexcantor64.triton.Triton.get().getStorage().getLanguageFromIp(ipAddr.get());

        JsonObject component = packet.getComponent();
        boolean modified = false;

        JsonElement descriptionElement = component.get("description");
        if (descriptionElement != null && descriptionElement.isJsonPrimitive()) {
            String descriptionJson = descriptionElement.getAsString();
            try {
                Component descriptionComponent = ComponentUtils.deserializeFromJson(descriptionJson);
                TranslationResult<Component> result = parser.translateComponent(descriptionComponent, lang, syntax);
                if (result.isChanged()) {
                    result.getResult().ifPresent(c -> component.addProperty("description", ComponentUtils.serializeToJson(c)));
                    modified = true;
                }
            } catch (Exception e) {
                com.rexcantor64.triton.Triton.get().getLogger().logError(e, "Failed to parse MOTD");
            }
        }

        JsonElement playersElement = component.get("players");
        if (playersElement != null && playersElement.isJsonObject()) {
            JsonObject players = playersElement.getAsJsonObject();
            JsonElement sampleElement = players.get("sample");
            if (sampleElement != null && sampleElement.isJsonArray()) {
                com.google.gson.JsonArray sample = sampleElement.getAsJsonArray();
                boolean sampleModified = false;
                for (int i = 0; i < sample.size(); i++) {
                    JsonElement playerElement = sample.get(i);
                    if (playerElement != null && playerElement.isJsonObject()) {
                        JsonObject playerObj = playerElement.getAsJsonObject();
                        JsonElement nameElement = playerObj.get("name");
                        if (nameElement != null && nameElement.isJsonPrimitive()) {
                            String name = nameElement.getAsString();
                            TranslationResult<String> result = parser.translateString(name, lang, syntax);
                            if (result.isChanged()) {
                                result.getResult().ifPresent(n -> playerObj.addProperty("name", n));
                                sampleModified = true;
                            }
                        }
                    }
                }
                if (sampleModified) {
                    modified = true;
                }
            }
        }

        if (modified) {
            packet.setComponent(component);
            event.markForReEncode(true);
        }
    }

    private Optional<String> getPlayerIpAddress(User user) {
        return Optional.ofNullable(user.getAddress())
                .map(InetSocketAddress::getAddress)
                .map(java.net.InetAddress::getHostAddress);
    }
}
