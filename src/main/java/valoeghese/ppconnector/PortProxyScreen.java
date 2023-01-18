package valoeghese.ppconnector;

import benzenestudios.sulphate.Anchor;
import benzenestudios.sulphate.SulphateScreen;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import valoeghese.ppconnector.util.NamedIconList;

import java.io.File;
import java.util.UUID;

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

		ResourceLocation res = new ResourceLocation("ppconnector", "example");
		Minecraft.getInstance().getTextureManager().register(
				res,
				new HttpTexture(
						new File(FabricLoader.getInstance().getGameDir().toFile(), "ooga.png"),
						"https://minotar.net/avatar/e7d9c0366a02490a877c2df42df51b7f/32",
						new ResourceLocation("textures/misc/unknown_server.png"), false, null
				)
		);

		hostSelection.add(
				new Host(new GameProfile(UUID.fromString("e7d9c036-6a02-490a-877c-2df42df51b7f"), "Big Boy"), "https://eyezah.com/portproxy/join?3oZg17g7df"),
				res
		);

		this.addRenderableWidget(hostSelection);

		// Buttons

		this.join = this.addButton(98, 20, Component.translatable("button.ppconnector.join"), b -> {
			this.minecraft.setScreen(new BridgingScreen(this, this.selected));
		});
		this.join.active = false;

		this.addButton(98, 20, Component.translatable("button.ppconnector.addhost"), b -> {
			// TODO functionality
		});

		this.addDone(this.height - 32);
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredString(matrices, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
	}
}
