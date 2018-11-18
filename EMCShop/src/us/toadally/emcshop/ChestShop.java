package us.toadally.emcshop;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChestShop {
	
	private Player owner;
	private ItemStack selling;
	private int price;
	private Location location;
	
	
	public ChestShop(Player owner, ItemStack selling, int price, Location l) {
		
		this.owner = owner;
		this.selling = selling;
		this.price = price;
		this.location = l;
		
	}

}
