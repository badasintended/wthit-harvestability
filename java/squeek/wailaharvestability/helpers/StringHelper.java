package squeek.wailaharvestability.helpers;

import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Tier;

public class StringHelper
{

	public static Class<?> harvestLevels = null;
	public static Method getHarvestLevelName = null;
	static
	{
		try
		{
			harvestLevels = Class.forName("slimeknights.tconstruct.library.utils.HarvestTiers");
			getHarvestLevelName = harvestLevels.getDeclaredMethod("getName", Tier.class);
		}
		catch (Exception e)
		{
		}
	}

	public static String getHarvestLevelName(Tier tier)
	{
		if (getHarvestLevelName != null)
		{
			try
			{
				return ((Component) getHarvestLevelName.invoke(null, tier)).getString();
			}
			catch (Exception e)
			{
			}
		}

		String unlocalized = "wailaharvestability.harvestlevel." + (tier.getLevel() + 1);

		if (I18n.exists(unlocalized))
			return I18n.get(unlocalized);

		return String.valueOf(tier.getLevel());
	}

	public static Component concatenateStringList(List<Component> textComponents, String separator)
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (Component s : textComponents)
		{
			sb.append(sep).append(s.getString());
			sep = separator;
		}
		return new TextComponent(sb.toString());
	}

	public static String stripFormatting(String str)
	{
		return ChatFormatting.stripFormatting(str);
	}

}
