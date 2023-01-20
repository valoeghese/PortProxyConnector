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

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class Selection<T> extends ObjectSelectionList<Selection<T>.Entry> {
	Selection(Minecraft minecraft, Screen parent, Font font, int floorOffset, int ceilOffset, int elementHeight, Consumer<@Nullable T> onSelect) {
		super(minecraft, parent.width, parent.height, 32 + ceilOffset, parent.height - 65 + 4 + floorOffset, elementHeight);

		this.parent = parent;
		this.font = font;
		this.onSelect = onSelect;
	}

	final Screen parent;
	protected final Font font;
	private final Consumer<@Nullable T> onSelect;

	@Override
	public void setSelected(@Nullable Entry entry) {
		super.setSelected(entry);
		this.onSelect.accept(entry == null ? null : entry.item);
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
		@Nullable Selection<T>.Entry selected = this.getSelected();
		return selected == null ? null : selected.item;
	}

	public void recenter() {
		if (this.getSelected() != null) {
			this.centerScrollOn(this.getSelected());
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	@Override
	protected void renderBackground(PoseStack poseStack) {
		this.parent.renderBackground(poseStack);
	}

	@Override
	protected boolean isFocused() {
		return this.parent.getFocused() == this;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		for (Selection<T>.Entry entry : this.children()) {
			entry.mouseMoved(mouseX, mouseY);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract class Entry extends ObjectSelectionList.Entry<Selection<T>.Entry> {
		public Entry(T item) {
			this.item = item;
		}

		protected final T item;

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i == 0) {
				this.select();
				return true;
			} else {
				return false;
			}
		}

		private void select() {
			Selection.this.setSelected(this);
		}
	}
}