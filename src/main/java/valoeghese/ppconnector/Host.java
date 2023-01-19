package valoeghese.ppconnector;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class Host {
	public Host(GameProfile user, String portProxyLink, boolean resolved) {
		this.user = user;
		this.portProxyLink = portProxyLink;
		this.resolved = resolved;
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

			if (!tempProfile.getName().equals(this.user.getName())) {
				shouldUpdateHosts = true;
			}
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

	public JsonObject serialise() {
		JsonObject result = new JsonObject();
		result.addProperty("owner", this.user.getId().toString());
		result.addProperty("ownerName", this.user.getName());
		result.addProperty("link", this.portProxyLink);
		return result;
	}

	public static Host of(JsonObject object) {
		GameProfile tempProfile = new GameProfile(UUID.fromString(object.get("owner").getAsString()), object.get("ownerName").getAsString());
		Host host = new Host(tempProfile, object.get("link").getAsString(), false);
		host.resolve();
		return host;
	}

	public static volatile boolean shouldUpdateHosts = false;
}
