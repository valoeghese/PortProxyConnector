// Adapted from CosmeticSelection from Cosmetica

/*
 * Copyright 2022 EyezahMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package valoeghese.ppconnector.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class NamedIconList<T> extends Selection<T> {
	public NamedIconList(Minecraft minecraft, Screen parent, Font font, Function<T, String> textGetter, Consumer<T> onSelect) {
		super(minecraft, parent, font, 0, 16, 48, onSelect);
		this.textGetter = textGetter;
	}

	private final Function<T, String> textGetter;

	private static final int OFFSET_X = 45;

	public void add(T item, ResourceLocation texture) {
		Entry entry = new Entry(item, texture);
		this.addEntry(entry);
	}

	public void select(@Nullable T item) {
		if (item == null) {
			this.setSelected(null);
		}
		else {
			for (Selection<T>.Entry entry : this.children()) {
				if (item.equals(entry.item)) {
					this.setSelected(entry);
					return;
				}
			}

			// if there is no such entry, deselect
			this.setSelected(null);
		}
	}
	@Nullable
	public T getSelectedItem() {
		@Nullable NamedIconList<T>.Entry selected = (Entry) this.getSelected();
		return selected == null ? null : selected.item;
	}

	@Override
	protected void renderSelection(PoseStack poseStack, int y0, int j, int dy, int colour1, int colour2) {
		int x0 = this.x0 + (this.width - j) / 2;
		int x1 = this.x0 + (this.width + j) / 2;

		fill(poseStack, x0, y0 - 2, x1, y0 + dy + 2, colour1);
		fill(poseStack, x0 + 1, y0 - 1, x1 - 1, y0 + dy + 1, colour2);
	}

	protected class Entry extends Selection<T>.Entry {
		public Entry(T entry, ResourceLocation texture) {
			super(entry);
			this.texture = texture;
			this.displayName = NamedIconList.this.textGetter.apply(entry);
		}

		private final ResourceLocation texture;
		private final String displayName;

		@Override
		public void render(PoseStack poseStack, int x, int y, int k, int l, int m, int n, int o, boolean isHovered, float f) {
			x = NamedIconList.this.parent.width / 2 - 60;
			final int textY = y;
			y += 28;

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, this.texture);
			blit(poseStack, x - 66, y - 22, NamedIconList.this.getBlitOffset(), 0.0f, 0.0f, 32, 32, 32, 32);

			NamedIconList.this.font.drawShadow(poseStack, this.displayName, (float) x, (float) (textY + 6), 16777215, true);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int i) {
			if (mouseX >= NamedIconList.this.x0 + NamedIconList.this.width / 2f - NamedIconList.this.getRowWidth() / 2f + OFFSET_X) {
				return super.mouseClicked(mouseX, mouseY, i);
			} else {
				return false;
			}
		}

		@Override
		public Component getNarration() {
			return Component.translatable("narrator.select", this.displayName);
		}
	}
}