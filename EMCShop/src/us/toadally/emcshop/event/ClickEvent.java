package us.toadally.emcshop.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.jantuck.utils.reflection.MinecraftReflectionProvider;
import me.jantuck.utils.reflection.ReflectionUtil;
import net.md_5.bungee.api.ChatColor;
import us.toadally.emcshop.EMCShop;
import us.toadally.emcshop.util.EcoUtil;
import us.toadally.emcshop.util.MegaStack;
import us.toadally.emcshop.util.SignUtil;

public class ClickEvent implements Listener{
	
	EMCShop pl;
	
	public ClickEvent(EMCShop pl) {
		this.pl = pl;
	}
	
	
	@EventHandler
	public void onLeftClick(PlayerInteractEvent e) {
		
		Action a = e.getAction();
		
		if(a == Action.LEFT_CLICK_BLOCK) {
			
			Block b = e.getClickedBlock();
			if(b.getType() != Material.CHEST) { return; }
			
			Location signLoc = b.getLocation().add(0,1,0);

			if(signLoc.getBlock().getState() instanceof Sign) {
				Sign sign = (Sign) signLoc.getBlock().getState();
				
				if(SignUtil.isValidSign(sign.getLines())) {
					
					Chest chest = (Chest) b.getState();
					
					Inventory i = chest.getInventory();
					Inventory openInv = Bukkit.createInventory(null, chest.getInventory().getSize(), ChatColor.BLUE + "Viewing Shop Contents");
					openInv.setContents(i.getContents());
					
					e.getPlayer().openInventory(openInv);
					
				}
				
			}
			
			
		}
		
		
	}
	
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e) {
		
		if(e.getInventory().getName().equalsIgnoreCase(ChatColor.BLUE + "Viewing Shop Contents")) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		Action a = e.getAction();
		
		if(a != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		
		if(e.getClickedBlock() == null) { return; };
		
		if(true) {
			
			Block b = e.getClickedBlock();
			if(!(e.getClickedBlock().getState() instanceof Sign)) { return; }
			
			Sign sign = (Sign) b.getState();
			
			if(!SignUtil.isValidSign(sign.getLines())) return;
			
			Block ch = b.getLocation().add(0,-1,0).getBlock();
			if(ch.getType() != Material.CHEST) return;
			
			@SuppressWarnings("deprecation")
			OfflinePlayer owner = Bukkit.getOfflinePlayer(sign.getLine(1));
			
			Chest chest = (Chest) ch.getState();
			
			int amount = Integer.parseInt(sign.getLine(2));

			
			if(getAmount(chest.getInventory()) < amount) {
				p.sendMessage(ChatColor.RED + "This shop is out of stock!");
				if(owner.isOnline()) {
					
					((Player) owner).sendMessage(ChatColor.RED + "Your shop at X:"+b.getX() + " Y:"+b.getY() + " Z:"+b.getZ()+ " is empty!");
				} return;
			}
			
			int price = SignUtil.getPriceInNuggets(sign);
			String tryGold = EcoUtil.removeGold(p, price, sign, pl);
			if(!tryGold.equalsIgnoreCase("success")) {
				
				p.sendMessage(ChatColor.RED + tryGold);
				return;
			}
			
			if(owner.isOnline()) {
				
				for(ItemStack is : chest.getBlockInventory().getContents()) {
					if(is != null) {
						((Player) owner).sendMessage(ChatColor.GREEN + p.getName() + " purchased "+amount + " " + getItemName(is) +" from your shop!");
						break;
					}
				}
			}	
			removeChestItems(amount, chest, p); //Method to preserve enchanted and modified items		
		}
	}

	public ItemStack returnSetAmount(ItemStack i, int amount) {
		i.setAmount(amount);
		return i;
	}

    
	private void removeChestItems(int amount, Chest chest, Player p) {
		
		List<ItemStack> purchased = new ArrayList<ItemStack>();

		
		
		int ctLeft = amount;
		
		
		ItemStack[] contents = chest.getBlockInventory().getContents();
		for(int x = 0; x < contents.length; x++) {
			
			ItemStack[] updatedContents = chest.getInventory().getContents();
			
			if(updatedContents[x] == null) continue;
			
			if(ctLeft == 0) {
				break;
			}
			
			final int amt = contents[x].getAmount();
			
			if(amt > ctLeft) {
				updatedContents[x].setAmount(ctLeft);
				purchased.add(updatedContents[x].clone());
				contents[x].setAmount(amt-ctLeft);


				
				ctLeft = 0;
				
			} else {
				
				if(contents[x].getMaxStackSize() == 1) {
					ctLeft--;
				} else {
					ctLeft = ctLeft - amt;
				}
				purchased.add(updatedContents[x].clone());
				contents[x].setAmount(0);
			}
			
			
			
			
			
			
			
		}
		
		final ItemStack[] cts = chest.getBlockInventory().getContents();
		List<ItemStack> removedNulls = new ArrayList<ItemStack>();
		for(ItemStack its : cts) {
			if(its == null) continue;
			removedNulls.add(its);
		}
		chest.getInventory().clear();
		for(ItemStack its : removedNulls) {
			
			chest.getInventory().addItem(its);
			
		}
		
		
		
		
		

		
		
		ArrayList<MegaStack> combined = combine(purchased);
		int maxStackSize = 64;
		if(combined.size() > 0) {
			maxStackSize = combined.get(0).getItem().getMaxStackSize(); //All items are of the same MATERIAL, so this isn't illegal

		}
		contents = p.getInventory().getContents();
		
		int fullChecks = 0;
		
		for(int i = 0; i < contents.length; i++) {
			
			if(combined.size() == 0) {
				break;
			}
			
			if(fullChecks > 27) {
				break;
			}
			
			if(contents[i] == null || contents[i].getType() == Material.AIR) {
				
				p.getInventory().addItem(combined.get(0).getItemExact(maxStackSize));
				
			} else {
				if(contents[i].getAmount() >= maxStackSize) {
					fullChecks++;
					continue;
				}
				
				for(MegaStack ms : combined) {
					if(ms.getItem().isSimilar(contents[i])) {
						
						int amountNeeded = maxStackSize - contents[i].getAmount();
						p.getInventory().addItem(ms.getItemExact(amountNeeded));
						p.updateInventory();
						
					}
				}
			}
			
			for(int x = 0; x < combined.size(); x++) {
				if(combined.get(x).getAmount() <= 0) {
					combined.remove(x);
				}
			}
			
		}
		p.updateInventory();
		if(combined.size() > 0) {
			p.sendMessage(ChatColor.GREEN + "Not all of the purchased items could fit in your inventory, they have been dropped on the ground.");
			for(MegaStack ms : combined) {
				while (ms.getAmount() > 0) {
					ItemStack is = ms.getItem();
					
					
					p.getWorld().dropItem(p.getLocation(), ms.getItemExact(is.getMaxStackSize()));
				}
			}

		}
		
		
		
		
		
	}
	
	   public static ArrayList< MegaStack > combine( List< ItemStack > items ) {
	        ArrayList< MegaStack > megastacks = new ArrayList< MegaStack >();
	        
	        for(ItemStack i : items) {
	        	boolean cont = false;
	        	for(MegaStack ms : megastacks) {
	        		
	        		if(ms.getItem().isSimilar(i)) {
	        			ms.addAmount(ms.getItem().getAmount());
	        			cont = true;
	        			break;
	        		}
	        		
	        	}
	        	if(cont) continue;
	        	
	        	MegaStack ms = new MegaStack(i);
	        	megastacks.add(ms);
	        }
	        return megastacks;
	        
	        
	        
	        
	        
	        
	    }


	
	public boolean isValidChestInventory(Inventory i) {
		InventoryHolder ih = i.getHolder();
		if(!(ih instanceof Chest) || !(ih instanceof DoubleChest)) return false;
			
			Chest chest = (Chest) ih;
			
			Location signLoc = chest.getLocation().add(0,1,0);
			Material m = signLoc.getBlock().getType();
			if(m == Material.SIGN || m == Material.WALL_SIGN) {
				Sign sign = (Sign) signLoc.getBlock().getState();
				
				if(SignUtil.isValidSign(sign.getLines())) {
					
					return true;
					
					
					
				}
			
			}
			return false;
	}

	public String getItemName(ItemStack itemStack){
	    final String[] item = {itemStack.getType().name()};
	    ReflectionUtil.newCall().getMethod(MinecraftReflectionProvider.CRAFT_ITEMSTACK, "asNMSCopy", ItemStack.class)
	            .get().passIfValid(reflectionMethod -> {
	        Object nmsItemStack = reflectionMethod.invokeIfValid(null, itemStack);
	        item[0] = ReflectionUtil.newCall().getMethod(MinecraftReflectionProvider.NMS_ITEMSTACK, "getName").get().invokeIfValid(nmsItemStack);
	    });
	    return item[0];
	}
	
	
	private int getAmount(Inventory i){
	    int ct = 0;   
		
		for(ItemStack is : i.getStorageContents()) {
			
			if(is != null) {
			
				ct = ct + is.getAmount();
			}
			
		}
		return ct;
		
		
	}
}

