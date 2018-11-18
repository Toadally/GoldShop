package us.toadally.emcshop;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import us.toadally.emcshop.util.EcoUtil;

public class BankAccount {
	
	private String owner;
	private int goldInNuggets;
	EMCShop pl;
	
	public BankAccount(String owner, EMCShop pl) {
		this.owner = owner;
		
		if(!(pl.getConfig().getInt("accounts."+this.owner) > -1)) {
			
			pl.getConfig().set("accounts."+this.owner, 0);
			pl.saveConfig();
			this.goldInNuggets = 0;
		} else {
			this.goldInNuggets = pl.getConfig().getInt("accounts."+this.owner);
		}
		
		EMCShop.accounts.put(owner, this);
		this.pl = pl;
		
	}
	
	public void addGold(int gold) {
		
		this.goldInNuggets += gold;
		pl.getConfig().set("accounts."+this.owner, goldInNuggets);
		pl.saveConfig();
	}
	
	public void withdraw() {
		
		Player p = (Player) Bukkit.getOfflinePlayer(UUID.fromString(owner));
		
		if(!(EcoUtil.getEmptySlots(p) > (getGoldBlocks() - (getGoldBlocks() % 64))) || 
				!(EcoUtil.getEmptySlots(p) > (getGoldBars() - (getGoldBars() % 64))) ||
				!(EcoUtil.getEmptySlots(p) > (getGoldNuggets() - (getGoldNuggets() % 64)))) {
			
			p.sendMessage(ChatColor.RED + "You don't have enough space in your inventory to withdraw your gold.");
			return;
		}
		
		if(EcoUtil.getEmptySlots(p) > (getGoldBlocks() - (getGoldBlocks() % 64))) {
			p.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, getGoldBlocks()));
		}
		if(EcoUtil.getEmptySlots(p) > (getGoldBars() - (getGoldBars() % 64))) {
			p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, getGoldBars()));
		}
		if(EcoUtil.getEmptySlots(p) > (getGoldNuggets() - (getGoldNuggets() % 64))) {
			p.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, getGoldNuggets()));
		}
		
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
		p.sendMessage(ChatColor.GREEN + "Withdrew "+getGoldBlocks()+"GB "+getGoldBars()+"G "+getGoldNuggets()+"n");
		this.goldInNuggets = 0;
		
	}
	
	
	
	public OfflinePlayer getOwner() {
		return Bukkit.getOfflinePlayer(UUID.fromString(owner));
	}
	public int getTotalGoldNuggets() {
		
		return goldInNuggets;
		
	}
	public int getGoldBlocks() {
		int bars = (goldInNuggets - (goldInNuggets % 9))/9;
		int blocks = (bars - (bars % 9))/9;	
		return blocks;
	}
	public int getGoldBars() {
		int leftoverGold = goldInNuggets - (getGoldBlocks()*81);
		int bars = (leftoverGold - (leftoverGold % 9))/9;
		return bars;
	}
	public int getGoldNuggets() {
		int leftoverGold = goldInNuggets - (getGoldBlocks()*81) - (getGoldBars()*9);
		return leftoverGold;
	}

}
