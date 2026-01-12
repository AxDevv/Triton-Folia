package com.rexcantor64.triton.spigot.placeholderapi;

import com.rexcantor64.triton.Triton;
import com.rexcantor64.triton.spigot.SpigotTriton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TritonLanguagePlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "triton";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AxDevv";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline()) {
            return null;
        }

        if (params.equals("language")) {
            return Triton.get().getPlayerManager().get(player.getUniqueId()).getLang().getName();
        }

        return null;
    }
}
