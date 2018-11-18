package us.toadally.emcshop.util;

import org.bukkit.inventory.ItemStack;

public class MegaStack{
	
	private ItemStack itemstack;
	private int amount;
	
	public MegaStack(ItemStack i) {
		this.amount = i.getAmount();
		this.itemstack = i;
	}
	
	public ItemStack getItemExact(int amount) {
		if(this.amount - amount < 0) {
			amount = this.amount;
		}
		ItemStack clone = itemstack.clone();
		clone.setAmount(amount);
		this.amount -= amount;
		return clone;
	}
	
	public void setItemStack(ItemStack i) {
		this.itemstack = i;
	}
	
	public ItemStack getItem() {
		return this.itemstack;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public void addAmount(int add) {
		this.amount += add;
	}
	
	public int getAmount() {
		return this.amount;
	}

}
