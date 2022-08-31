package squeek.wthitharvestability.helpers;

import net.minecraft.ChatFormatting;

public class ColorHelper {

    private static final ChatFormatting[] booleanColorRange = {
        ChatFormatting.DARK_RED,
        ChatFormatting.RED,
        ChatFormatting.DARK_GREEN,
        ChatFormatting.GREEN
    };

    public static String getBooleanColor(boolean val) {
        return getBooleanColor(val, false);
    }

    public static String getBooleanColor(boolean val, boolean modified) {
        return booleanColorRange[(val ? 2 : 0) + (modified ? 1 : 0)].toString();
    }

}