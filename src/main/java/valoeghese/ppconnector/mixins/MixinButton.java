package valoeghese.ppconnector.mixins;

import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import valoeghese.ppconnector.util.MutableButton;

@Mixin(Button.class)
public abstract class MixinButton implements MutableButton {
	@Shadow @Final @Mutable
	protected Button.OnPress onPress;

	@Shadow @Final @Mutable
	protected Button.OnTooltip onTooltip;

	@Override
	public void setAction(Button.OnPress onPress) {
		this.onPress = onPress;
	}

	@Override
	public void setTooltip(Button.OnTooltip onTooltip) {
		this.onTooltip = onTooltip;
	}
}
