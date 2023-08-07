package org.time.iic.gui;

import org.bukkit.inventory.Inventory;

public abstract class Gui {
	private String name;
	private int size;
	private Inventory inv;
	public Gui(String name, int size) {
		this.name = name;
		this.size = size;
		this.inv = presetInventory();
	}
	protected abstract Inventory presetInventory();
	public String getName() {
		return name;
	}
	public int getSize() {
		return size;
	}
	public Inventory getInventory() {
		return inv;
	}
}
