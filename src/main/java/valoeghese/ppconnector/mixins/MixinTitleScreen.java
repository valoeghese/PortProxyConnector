package valoeghese.ppconnector.mixins;

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
import valoeghese.ppconnector.PortProxyScreen;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
	protected MixinTitleScreen(Component component) {
		super(component);
	}

	@Inject(at = @At("RETURN"), method = "init")
	private void onNormalMenuOptions(CallbackInfo ci) {
		Button realms = null;

		for (GuiEventListener widget : this.children()) {
			if (widget instanceof Button b && b.getMessage().getContents() instanceof TranslatableContents tc) {
				if ("menu.online".equals(tc.getKey())) {
					realms = b;
					break;
				}
			}
		}

		if (realms != null) {
			realms.setWidth(98);

			this.addRenderableWidget(new Button(realms.x + 100 + 2, realms.y + (FabricLoader.getInstance().isModLoaded("modmenu") ? -24 : 0), 98, 20, Component.translatable("menu.ppconnector.portproxy"), button -> {
				this.minecraft.setScreen(new PortProxyScreen(this));
			}));
		}
	}
}
