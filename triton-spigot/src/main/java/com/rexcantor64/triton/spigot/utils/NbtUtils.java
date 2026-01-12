package com.rexcantor64.triton.spigot.utils;

import com.google.gson.JsonElement;
import net.kyori.adventure.nbt.BinaryTag;
import org.jetbrains.annotations.NotNull;

public class NbtUtils {

    public static @NotNull JsonElement toJson(@NotNull BinaryTag tag) {
        throw new UnsupportedOperationException("NBT conversion not supported without ProtocolLib");
    }

    public static @NotNull BinaryTag fromJson(@NotNull JsonElement element) {
        throw new UnsupportedOperationException("NBT conversion not supported without ProtocolLib");
    }
}
