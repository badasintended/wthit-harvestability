package squeek.wailaharvestability.helpers;

import net.minecraft.ChatFormatting;

public class ColorHelper
{
	private static final ChatFormatting[] colorRange = {
		ChatFormatting.DARK_RED,
		ChatFormatting.RED,
		ChatFormatting.GOLD,
		ChatFormatting.YELLOW,
		ChatFormatting.DARK_GREEN,
		ChatFormatting.GREEN,
		ChatFormatting.AQUA
	};

	private static final ChatFormatting[] booleanColorRange = {
		ChatFormatting.DARK_RED,
		ChatFormatting.RED,
		ChatFormatting.DARK_GREEN,
		ChatFormatting.GREEN
	};

	public static String getRelativeColor(double val, double min, double max)
	{
		if (min == max)
			return ChatFormatting.RESET.toString();
		else if ((max > min && val > max) || (min > max && val < max))
			return ChatFormatting.WHITE.toString() + ChatFormatting.BOLD;
		else if ((max > min && val < min) || (min > max && val > min))
			return colorRange[0].toString() + ChatFormatting.BOLD;

		int index = (int) (((val - min) / (max - min)) * (colorRange.length - 1));
		return colorRange[Math.max(0, Math.min(colorRange.length - 1, index))].toString();
	}

	public static String getBooleanColor(boolean val)
	{
		return getBooleanColor(val, false);
	}

	public static String getBooleanColor(boolean val, boolean modified)
	{
		return booleanColorRange[(val ? 2 : 0) + (modified ? 1 : 0)].toString();
	}
}