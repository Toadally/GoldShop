package us.toadally.emcshop.event;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.md_5.bungee.api.ChatColor;
import us.toadally.emcshop.util.SignUtil;

public class CreateSignEvent implements Listener{
	
	
	@EventHandler
	public void onChangeSign(SignChangeEvent e) {
		Player p = e.getPlayer();
		
		Location l = e.getBlock().getLocation().add(0,-1,0);
		
		
		String line0 = ChatColor.stripColor(e.getLine(0));
		
		if(!(line0.equalsIgnoreCase("shop") || line0.equalsIgnoreCase("[shop]"))){
			e.getBlock().breakNaturally();
			
			return;
		}
		if(!e.getLine(1).toLowerCase().equalsIgnoreCase(p.getName().toLowerCase())) {
			e.getBlock().breakNaturally();
			p.sendMessage(ChatColor.RED + "Set the second line to your name.");
			return;
		}
		if(!StringUtils.isNumeric(e.getLine(2))) {
			e.getBlock().breakNaturally();
			p.sendMessage(ChatColor.RED + "Set the third line to the item amount.");
			return;
		} else {
			if(Integer.parseInt(e.getLine(2)) > 64 || Integer.parseInt(e.getLine(2)) < 1) {
				e.getBlock().breakNaturally();
				p.sendMessage(ChatColor.RED + "Item amount must be above 0 and below or equal to 64.");
				return;
			}
		}
		String buyLine = SignUtil.makeBuyFormat(e.getLine(3));
		if(buyLine == null) {
			e.getBlock().breakNaturally();
			p.sendMessage(ChatColor.RED + "Correct format: '5G 3N' or '5 3' for 5 gold, 3 nuggets; '5G' for 5 gold");
			return;
		}
		
		if(l.getBlock().getType() != Material.CHEST) {
			e.getBlock().breakNaturally();
			p.sendMessage(ChatColor.RED + "A chest must be placed under the shop.");
			return;
		}
		
		e.setLine(0, ChatColor.BLUE + "[Shop]");
		e.setLine(1, p.getName());
		e.setLine(3, buyLine);
		
		p.sendMessage(ChatColor.GREEN + "New shop created!");
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 3f);
		
	}
	

}
