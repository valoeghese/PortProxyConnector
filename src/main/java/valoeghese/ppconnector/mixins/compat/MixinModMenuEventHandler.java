package valoeghese.ppconnector.mixins.compat;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.event.ModMenuEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import valoeghese.ppconnector.PortProxyConnector;

@Pseudo
@Mixin(ModMenuEventHandler.class)
public class MixinModMenuEventHandler {
	@ModifyConstant(method = "afterTitleScreenInit", constant = @Constant(stringValue = "menu.online"))
	private static String alterModMenuTarget(String original) {
		return (PortProxyConnector.settings.getProperty("replaceRealms").equals("true") && ModMenuConfig.MODS_BUTTON_STYLE.getValue() != ModMenuConfig.ModsButtonStyle.REPLACE_REALMS) ? "menu.ppconnector.portproxy" : "menu.online";
	}
}
