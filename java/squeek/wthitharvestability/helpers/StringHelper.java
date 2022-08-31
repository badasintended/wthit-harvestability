package squeek.wthitharvestability.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Tier;

public class StringHelper {

    public static final List<Function<Tier, String>> TIER_NAME_GETTERS = new ArrayList<>();

    public static String getHarvestLevelName(Tier tier) {
        for (Function<Tier, String> getter : TIER_NAME_GETTERS) {
            String name = getter.apply(tier);
            if (name != null) return name;
        }

        String unlocalized = "wailaharvestability.harvestlevel." + (tier.getLevel() + 1);

        if (I18n.exists(unlocalized))
            return I18n.get(unlocalized);

        return String.valueOf(tier.getLevel());
    }

    public static Component concatenateStringList(List<Component> textComponents, String separator) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Component s : textComponents) {
            sb.append(sep).append(s.getString());
            sep = separator;
        }
        return new TextComponent(sb.toString());
    }

    public static String stripFormatting(String str) {
        return ChatFormatting.stripFormatting(str);
    }

}
