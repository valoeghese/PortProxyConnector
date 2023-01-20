package valoeghese.ppconnector.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;

public class ModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return SettingsScreen::new;
	}

	public static boolean shouldReplaceRealms(boolean replaceRealms) {
		return replaceRealms && !(ModMenuConfig.MODIFY_TITLE_SCREEN.getValue() && ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.REPLACE_REALMS);
	}

	public static boolean shouldBeAdjacentToMultiplayer() {
		return ModMenuConfig.MODIFY_TITLE_SCREEN.getValue() && ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.SHRINK;
	}

	public static int getShift() {
		return ModMenuConfig.MODIFY_TITLE_SCREEN.getValue() && (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.CLASSIC) ? -24 : 0;
	}
}
