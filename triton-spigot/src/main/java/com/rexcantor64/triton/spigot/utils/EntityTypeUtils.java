package com.rexcantor64.triton.spigot.utils;

import com.rexcantor64.triton.api.wrappers.EntityType;

public class EntityTypeUtils {

    public static EntityType getEntityTypeById(int id) {
        org.bukkit.entity.EntityType bukkitType = org.bukkit.entity.EntityType.values().length > id
                ? org.bukkit.entity.EntityType.values()[id]
                : null;
        return EntityType.fromBukkit(bukkitType);
    }

    public static EntityType getEntityTypeByObjectId(int id) {
        for (ObjectIds obj : ObjectIds.values())
            if (obj.id == id) return obj.entityType;
        return EntityType.UNKNOWN;
    }

    private enum ObjectIds {
        BOAT(1, EntityType.BOAT),
        ITEM_STACK(2, EntityType.DROPPED_ITEM),
        AREA_EFFECT_CLOUD(3, EntityType.AREA_EFFECT_CLOUD),
        MINECART(10, EntityType.MINECART),
        TNT(50, EntityType.PRIMED_TNT),
        ENDER_CRISTAL(51, EntityType.ENDER_CRYSTAL),
        ARROW(60, EntityType.ARROW),
        SNOWBALL(61, EntityType.SNOWBALL),
        EGG(62, EntityType.EGG),
        FIREBALL(63, EntityType.FIREBALL),
        FIRECHARGE(64, EntityType.SMALL_FIREBALL),
        ENDERPEARL(65, EntityType.ENDER_PEARL),
        WITHER_SKULL(66, EntityType.WITHER_SKULL),
        SHULKER_BULLET(67, EntityType.SHULKER_BULLET),
        LLAMA_SPIT(68, EntityType.LLAMA_SPIT),
        FALLING_OBJECTS(70, EntityType.FALLING_BLOCK),
        ITEM_FRAME(71, EntityType.ITEM_FRAME),
        EYE_OF_ENDER(72, EntityType.ENDER_SIGNAL),
        POTION(73, EntityType.SPLASH_POTION),
        EXP_BOTTLE(75, EntityType.THROWN_EXP_BOTTLE),
        FIREWORK(76, EntityType.FIREWORK),
        LEASH(77, EntityType.LEASH_HITCH),
        ARMOR_STAND(78, EntityType.ARMOR_STAND),
        EVOCATION_FANGS(79, EntityType.EVOKER_FANGS),
        FISHING_HOOK(90, EntityType.FISHING_HOOK),
        SPECTRAL_ARROW(91, EntityType.SPECTRAL_ARROW),
        DRAGON_FIREBALL(93, EntityType.DRAGON_FIREBALL),
        TRIDENT(94, EntityType.TRIDENT);
        int id;
        EntityType entityType;

        ObjectIds(int id, EntityType entityType) {
            this.id = id;
            this.entityType = entityType;
        }
    }

}
