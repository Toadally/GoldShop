package us.toadally.emcshop.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.md_5.bungee.api.ChatColor;
import us.toadally.emcshop.util.SignUtil;

public class BreakBlockEvent implements Listener{
	
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		Block b = e.getBlock();
		
		if(b.getType() == Material.SIGN || b.getType() == Material.WALL_SIGN) {
			breakSign(b, e, p);
			return;
		}
		
		if(b.getType() != Material.CHEST) return;
		
		Location l = b.getLocation().add(0, 1, 0);
		
		if(l.getBlock().getType() == Material.SIGN || l.getBlock().getType() == Material.WALL_SIGN) {
			
			breakSign(l.getBlock(), e, p);
			
		}
		
	}
	
	
	public void breakSign(Block b, BlockBreakEvent e, Player p) {

		Sign sign = (Sign) b.getState();
		
		if(!p.hasPermission("shop.destroyall") && !(sign.getLine(1).equalsIgnoreCase(p.getName()))){
			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You cannot destroy someone else's shop.");
			e.setCancelled(true);
		}
		
		
		if(SignUtil.isValidSign(sign.getLines())) {
			
			b.breakNaturally();
			p.sendMessage(ChatColor.RED + "Shop destroyed.");
			
		}
	}
	
	
	

}
