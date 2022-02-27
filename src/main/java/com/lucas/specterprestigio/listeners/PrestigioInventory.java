package com.lucas.specterprestigio.listeners;

import com.lucas.specterprestigio.api.API;
import com.lucas.specterprestigio.cache.Cache;
import com.lucas.specterprestigio.connection.Database;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class PrestigioInventory implements Listener {
	@EventHandler
	public void inv(InventoryClickEvent e) {
		if (e.getInventory().getName().equalsIgnoreCase("§7Prestigios")) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if (e.getRawSlot() == 40 && e.getCurrentItem() != null
					&& e.getCurrentItem().getType() == Material.SKULL_ITEM) {
				API.evoluir(p);
			}
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		if (Database.hasJogador(e.getPlayer()))
			Database.addJogador(e.getPlayer());
		Cache.jogadores.put(e.getPlayer().getUniqueId(), Database.getJogador(e.getPlayer()));
	}

	@EventHandler
	public void tag(ChatMessageEvent e) {
		if (e.isCancelled())
			return;
		if (!e.getTags().contains("rank"))
			return;
		e.setTagValue("prestigio", "§" + color(e.getSender()) + "[⭐] ");
	}

	private String color(Player sender) {
		switch (API.getPrestigio(sender)) {
		case 1:
			return "b";
		case 2:
			return "6";
		case 3:
			return "a";
		case 4:
			return "e";
		case 5:
			return "5";
		}
		return "c";
	}
}
