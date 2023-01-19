package valoeghese.ppconnector;

import benzenestudios.sulphate.Anchor;
import benzenestudios.sulphate.SulphateScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import valoeghese.ppconnector.util.NamedIconList;

import java.io.File;
import java.util.Locale;

public class PortProxyScreen extends SulphateScreen {
	public PortProxyScreen(Screen parent) {
		super(Component.translatable("menu.ppconnector.portproxy"), parent);
		this.setRows(2);
		this.setAnchorY(Anchor.TOP,  () -> this.height - 32 - 24);
		this.setXSeparation(4);
	}

	@Nullable
	private Host selected;
	private Button join;

	@Override
	protected void addWidgets() {
		// Selection

		NamedIconList<Host> hostSelection = new NamedIconList<>(this.minecraft, this, this.font, Host::getDisplayName, host -> {
			this.selected = host;
			this.join.active = true;
		});

		PortProxyConnector.forEachHost(h -> {
			ResourceLocation res = new ResourceLocation("ppconnector", "icon/" + h.getDisplayName().toLowerCase(Locale.ROOT) + h.getDisplayName().hashCode());
			String dashlessUUID = h.getUser().getId().toString().replace("-", "");

			File folder = new File(PortProxyConnector.cacheFile, dashlessUUID.substring(0, 2));
			folder.mkdirs();

			if (Minecraft.getInstance().getTextureManager().getTexture(res, null) == null) {
				Minecraft.getInstance().getTextureManager().register(
						res,
						new HttpTexture(
								new File(folder, dashlessUUID + ".png"),
								"https://minotar.net/avatar/" + dashlessUUID + "/32",
								new ResourceLocation("textures/misc/unknown_server.png"), false, null
						)
				);
			}

			hostSelection.add(
					h,
					res
			);
		});

		this.addRenderableWidget(hostSelection);

		// Buttons

		this.join = this.addButton(98, 20, Component.translatable("button.ppconnector.join"), b -> {
			this.minecraft.setScreen(new BridgingScreen(this, this.selected));
		});
		this.join.active = false;

		this.addButton(98, 20, Component.translatable("button.ppconnector.addhost"), b -> this.minecraft.setScreen(new AddHostScreen(this)));

		this.addDone(this.height - 32);
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredString(matrices, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		if (Host.shouldUpdateHosts) {
			PortProxyConnector.saveHosts();
		}

		super.onClose();
	}
}
