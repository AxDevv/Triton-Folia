package com.rexcantor64.triton.spigot.utils;

import com.rexcantor64.triton.utils.ComponentUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class WrappedComponentUtils {

    private static final net.md_5.bungee.chat.ComponentSerializer BUNGEE_SERIALIZER = new net.md_5.bungee.chat.ComponentSerializer();

    public static @NotNull Component deserialize(@NotNull String json) {
        return ComponentUtils.deserializeFromJson(json);
    }

    public static @NotNull String serialize(@NotNull Component component) {
        return ComponentUtils.serializeToJson(component);
    }

}
