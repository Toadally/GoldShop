package us.toadally.emcshop.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;

import net.md_5.bungee.api.ChatColor;

public class SignUtil {
	
	
	public static boolean isBuyFormatValid(String s) {
		s = s.trim();
		
		
		String[] parts = s.split(" ");
		
		for(String p : parts) {
			
			if(p.toLowerCase().contains("g") || p.toLowerCase().contains("n")) {
				String num = p.substring(0, p.length()-1);
				if(!StringUtils.isNumeric(num)) {
					return false;
				}
			} else {
				return false;
			}		
		}
		return true;	
	}
	
	@SuppressWarnings("deprecation")
	public static String getOwner(Sign sign) {
		
		return Bukkit.getOfflinePlayer(sign.getLine(1)).getUniqueId().toString();
		
		
	}
	
	public static boolean isValidSign(String[] lines) {
		
		if(!lines[0].equalsIgnoreCase(ChatColor.BLUE + "[Shop]")) return false;
		if(lines[1].length()<1) return false;
		if(StringUtils.isNumeric(lines[2])) {
			int x = Integer.parseInt(lines[2]);
			
			if(x < 1 || x > 64) {
				return false;
			}
			
		} else { return false; };
		
		if(!isBuyFormatValid(lines[3])) { return false; }
		return true;
		
	}
	
	public static int getPriceInNuggets(Sign sign) {
		
		String line = sign.getLine(3);
		
		String original = line.trim();
		int goldBars = 0;
		int goldNuggets = 0;
		Pattern goldBarPattern = Pattern.compile("(([0-9])+(?=[Gg\\s]))");
		Pattern goldNuggetPattern = Pattern.compile("(((?<=[\\s\\.]))([0-9]))|([0-9])(?=[Nn])");
		Matcher matcher = goldBarPattern.matcher(original);
		if(matcher.find()) {
			
			String result = matcher.group(1);
			if(StringUtils.isNumeric(result)) { goldBars = Integer.parseInt(matcher.group(1)); }
		}
		matcher = goldNuggetPattern.matcher(original);
		if(matcher.find()) {
			String result = matcher.group(1);
			if(matcher.group(1) == null) {
				result = matcher.group(4);
			}
			
			
			if(StringUtils.isNumeric(result)) { 
				
				goldNuggets = Integer.parseInt(result);
				}
		}
		return goldNuggets+(goldBars*9);
		
	}
	
	
	public static String makeBuyFormat(String original) {
		original = original.trim();
		
		int goldBars = 0;
		int goldNuggets = 0;
		
		Pattern goldBarPattern = Pattern.compile("(([0-9])+(?=[Gg\\s]))");
		Pattern goldNuggetPattern = Pattern.compile("(((?<=[\\s\\.]))([0-9]))|([0-9])(?=[Nn])");
		
		Matcher matcher = goldBarPattern.matcher(original);
		if(matcher.find()) {
			
			String result = matcher.group(1);
			if(StringUtils.isNumeric(result)) { goldBars = Integer.parseInt(matcher.group(1)); }
		}
		Matcher matcher2 = goldNuggetPattern.matcher(original);
		if(matcher2.find()) {
			
			String result = matcher2.group(1);
			if(matcher2.group(1) == null) {
				result = matcher2.group(4);
			}
			
			
			if(StringUtils.isNumeric(result)) { 
				
				goldNuggets = Integer.parseInt(result);
				}
		}
		if(goldNuggets > 8 || goldNuggets < 0) {
			return null;
		}
		if(goldBars < 0) {
			return null;
		}
		
		
		String format = "";
		if(goldBars > 0) {
			format = goldBars+"G";
		}
		if(goldBars > 0 && goldNuggets > 0) {
			format = format + " ";
		}
		if(goldNuggets > 0) {
			format = format + goldNuggets+"n";
		}
		
		if(format == "") {
			return null;
		} else {
			return format;
		}
		
		
		
		
	}

}
