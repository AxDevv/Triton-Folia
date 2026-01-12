package com.rexcantor64.triton.spigot.utils;

import com.google.gson.JsonSyntaxException;
import com.rexcantor64.triton.api.language.Localized;
import com.rexcantor64.triton.spigot.SpigotTriton;
import com.rexcantor64.triton.utils.ComponentUtils;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ItemStackTranslationUtils {

    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    public static @Nullable ItemStack translateItemStack(@Nullable ItemStack item, @NotNull Localized languagePlayer, boolean translateBooks) {
        if (item == null || item.getType() == Material.AIR) {
            return item;
        }
        return translateBukkitItemStack(item, languagePlayer);
    }

    private static @NotNull ItemStack translateBukkitItemStack(@NotNull ItemStack item, @NotNull Localized languagePlayer) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                main().getMessageParser()
                        .translateString(
                                meta.getDisplayName(),
                                languagePlayer,
                                main().getConfig().getItemsSyntax()
                        )
                        .ifChanged(meta::setDisplayName)
                        .ifToRemove(() -> meta.setDisplayName(null));
            }
            if (meta.hasLore()) {
                List<String> newLore = new ArrayList<>();
                for (String lore : meta.getLore()) {
                    main().getMessageParser()
                            .translateString(lore, languagePlayer, main().getConfig().getItemsSyntax())
                            .ifChanged(result -> newLore.addAll(Arrays.asList(result.split("\n"))))
                            .ifUnchanged(() -> newLore.add(lore));
                }
                meta.setLore(newLore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private static SpigotTriton main() {
        return SpigotTriton.asSpigot();
    }

}
