package valoeghese.ppconnector.mixins;

import com.terraformersmc.modmenu.event.ModMenuEventHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import valoeghese.ppconnector.PortProxyConnector;
import valoeghese.ppconnector.PortProxyScreen;
import valoeghese.ppconnector.compat.ModMenuCompat;
import valoeghese.ppconnector.compat.SettingsScreen;
import valoeghese.ppconnector.util.MutableButton;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
	protected MixinTitleScreen(Component component) {
		super(component);
	}

	@Inject(at = @At("RETURN"), method = "init")
	private void onNormalMenuOptions(CallbackInfo ci) {
		Button realms = null;
		Button multiplayer = null;

		for (GuiEventListener widget : this.children()) {
			if (widget instanceof Button b && b.getMessage().getContents() instanceof TranslatableContents tc) {
				if ("menu.online".equals(tc.getKey())) {
					realms = b;
				}
				if ("menu.multiplayer".equals(tc.getKey())) {
					multiplayer = b;
				}
			}
		}

		if (realms != null) {
			final Component portProxyText = Component.translatable("menu.ppconnector.portproxy");
			final boolean modmenuLoaded = FabricLoader.getInstance().isModLoaded("modmenu");
			boolean replaceRealms = PortProxyConnector.settings.getProperty("replaceRealms").equals("true");

			if (modmenuLoaded) {
				replaceRealms = ModMenuCompat.shouldReplaceRealms(replaceRealms);
			}

			if (replaceRealms) {
				realms.setMessage(portProxyText);
				((MutableButton) realms).setTooltip(Button.NO_TOOLTIP);
				((MutableButton) realms).setAction(b -> this.minecraft.setScreen(new PortProxyScreen(this)));
			}
			else {
				if (modmenuLoaded && multiplayer != null && ModMenuCompat.shouldBeAdjacentToMultiplayer()) {
					realms = multiplayer;
				}

				realms.setWidth(98);

				this.addRenderableWidget(new Button(realms.x + 100 + 2, realms.y + (modmenuLoaded ? ModMenuCompat.getShift() : 0), 98, 20, portProxyText, button -> {
					this.minecraft.setScreen(new PortProxyScreen(this));
				}));
			}
		}
	}
}
