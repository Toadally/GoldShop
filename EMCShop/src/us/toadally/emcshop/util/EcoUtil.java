package us.toadally.emcshop.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import us.toadally.emcshop.BankAccount;
import us.toadally.emcshop.EMCShop;

public class EcoUtil {
	
	
	public static String removeGold(Player p, int nuggetsToRemove, Sign sign, EMCShop pl) {
		
		int nuggets = getAmountOfItem(Material.GOLD_NUGGET, p);
		int bars = getAmountOfItem(Material.GOLD_INGOT, p);
		int blocks = getAmountOfItem(Material.GOLD_BLOCK, p);
		
		
		int totalNuggets = nuggets+(bars*9)+(blocks*81);

		
		if(totalNuggets < nuggetsToRemove) {
			return "You can't afford that item.";
			
		}
		
		totalNuggets = totalNuggets - nuggetsToRemove;

		int newBlocks = getGoldBlocks(totalNuggets);
		int newBars = getGoldBars(totalNuggets);
		int newNuggets = getGoldNuggets(totalNuggets);
		
		p.getInventory().remove(Material.GOLD_BLOCK);
		p.getInventory().remove(Material.GOLD_INGOT);
		p.getInventory().remove(Material.GOLD_NUGGET);
		
		
		if(!(getEmptySlots(p) > (newBlocks - (newBlocks % 64))) || 
				!(getEmptySlots(p) > (newBars - (newBars % 64))) ||
				!(getEmptySlots(p) > (newNuggets - (newNuggets % 64)))) {
			
			return "You don't have enough space in your inventory to sort out your gold.";
		}
		
		if(getEmptySlots(p) > (newBlocks - (newBlocks % 64))) {
			p.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, newBlocks));
		}
		if(getEmptySlots(p) > (newBars - (newBars % 64))) {
			p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, newBars));
		}
		if(getEmptySlots(p) > (newNuggets - (newNuggets % 64))) {
			p.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, newNuggets));
		}
		
		p.updateInventory();
		
		String owner = SignUtil.getOwner(sign);
		
		BankAccount ba = null;
		if(!EMCShop.accounts.containsKey(owner)) {
			ba = new BankAccount(owner, pl);
		} else {
			ba = EMCShop.accounts.get(owner);
		}
		
		if(ba != null) {
			ba.addGold(nuggetsToRemove);
		}
		
		
		return "success";
		
	}
	
	private static int getAmountOfItem(Material m,Player p) {
		int amount = 0;
		
		for(ItemStack i : p.getInventory()) {
			if(i == null) continue;
			if(i.getType() == m) {
			amount += i.getAmount();
			}
		}
		return amount;
	}
	
	public static void removeMaterial(Player p, Material m, int amount) {
		
		for(ItemStack item : p.getInventory().getContents()) {
			if(item == null) continue;
            if (item.getType() == m) {
                item.setAmount(item.getAmount() - amount);
            }
        }
	}
	
	public static int getEmptySlots(Player p) {
		
		int slots = 0;
		
		for(ItemStack item : p.getInventory().getContents()) {
            if (item == null) {
                slots++;
            }
        }
		return slots;
	}
	
	public static int getGoldBlocks(int goldInNuggets) {
		int bars = (goldInNuggets - (goldInNuggets % 9))/9;
		int blocks = (bars - (bars % 9))/9;	
		return blocks;
	}
	public static int getGoldBars(int goldInNuggets) {
		int leftoverGold = goldInNuggets - (getGoldBlocks(goldInNuggets)*81);
		int bars = (leftoverGold - (leftoverGold % 9))/9;
		return bars;
	}
	public static int getGoldNuggets(int goldInNuggets) {
		int leftoverGold = goldInNuggets - (getGoldBlocks(goldInNuggets)*81) - (getGoldBars(goldInNuggets)*9);
		return leftoverGold;
	}

}
