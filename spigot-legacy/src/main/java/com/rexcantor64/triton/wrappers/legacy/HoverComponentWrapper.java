package com.rexcantor64.triton.wrappers.legacy;

import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Optional;

public class HoverComponentWrapper {

    public static BaseComponent[] getValue(HoverEvent hover) {
        return hover.getValue();
    }

    public static HoverEvent setValue(HoverEvent hover, BaseComponent... components) {
        return new HoverEvent(hover.getAction(), components);
    }

    public static Optional<String> getNbtItemData(BaseComponent[] components) {
        if (components.length != 1) {
            return Optional.empty();
        }
        if (!(components[0] instanceof TextComponent)) {
            return Optional.empty();
        }
        val component = (TextComponent) components[0];
        val itemMojangson = component.getText();
        if (!itemMojangson.contains("{")) {
            return Optional.empty();
        }
        return Optional.of(itemMojangson);
    }

    public static BaseComponent[] fromNbtItemData(String nbtString) {
        return new BaseComponent[]{new TextComponent(nbtString)};
    }

}
