package us.toadally.emcshop;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import us.toadally.emcshop.event.BreakBlockEvent;
import us.toadally.emcshop.event.ClickEvent;
import us.toadally.emcshop.event.CreateSignEvent;

public class EMCShop extends JavaPlugin{
	
	public static HashMap<String, BankAccount> accounts = new HashMap<String, BankAccount>();
	
	PluginManager pm = Bukkit.getPluginManager();
	
	
	
	public void onEnable() {
		
		pm.registerEvents(new BreakBlockEvent(), this);
		pm.registerEvents(new ClickEvent(this), this);
		pm.registerEvents(new CreateSignEvent(), this);
		
		for(String s : getConfig().getConfigurationSection("accounts").getKeys(false)) {
			@SuppressWarnings("unused")
			BankAccount ba = new BankAccount(s, this);
		}
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		if(label.equalsIgnoreCase("withdraw")) {
			if(accounts.containsKey(p.getUniqueId().toString())) {
				accounts.get(p.getUniqueId().toString()).withdraw();
			} else {
				BankAccount ba = new BankAccount(p.getUniqueId().toString(), this);
				ba.withdraw();
			}
			return true;
		}
		return false;
		
		
	}
	
	
	
	public void onDisable() {
		
		saveConfig();
	}
	

}
