package valoeghese.ppconnector;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class Host {
	public Host(GameProfile user, String portProxyLink) {
		this.user = user;
		this.portProxyLink = portProxyLink;
	}

	private volatile GameProfile user;
	private volatile boolean resolved;
	private String portProxyLink;

	public GameProfile getUser() {
		return this.user;
	}

	public String getDisplayName() {
		return this.user.getName();
	}

	public void resolve() {
		GameProfile tempProfile = this.user;
		Thread completer = new Thread(() -> {
			this.user = Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(tempProfile, Boolean.getBoolean("ppconnector.requireSecure"));
			this.resolved = true;
		});
		completer.setDaemon(true);
		completer.start();
	}

	public boolean isResolved() {
		return this.resolved;
	}

	public String getPortProxyLink() {
		return this.portProxyLink;
	}

	public static Host of(JsonObject object) {
		GameProfile tempProfile = new GameProfile(UUID.fromString(object.get("owner").getAsString()), object.get("ownerName").getAsString());
		Host host = new Host(tempProfile, object.get("link").getAsString());
		host.resolve();
		return host;
	}
}
