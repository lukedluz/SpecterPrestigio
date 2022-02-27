package com.lucas.specterprestigio.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.lucas.specterprestigio.api.API;
import com.lucas.specterprestigio.cache.Cache;

public class PrestigioCommand implements CommandExecutor {
	public static Inventory getInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 5 * 9, "§7Prestigios");
		inv.setItem(0, API.head64("§6Como funciona o prestigio?",
				Arrays.asList(new String[] { "§7Ao chegar ao ultimo rank", "§7voc§ pode evoluir seu §ePrestigio", "§7",
						"§eBeneficios:", "§8- §7Ganha itens na evolu§§o", "§8- §7Tag diferente a cada n§vel",
						"§8- §7Recebe mais 300000 de limite de compra",
						"§8- §75x Spawner de Slime por n§vel §e(acumula)",
						"§8- §7Recebe 5% de desconto no /loja por n§vel §e(acumula)",
						"§8- §7Bonus de 110% por n§vel na minera§§o §e(acumula)" }),
				"http://textures.minecraft.net/texture/9e5bb8b31f46aa9af1baa88b74f0ff383518cd23faac52a3acb96cfe91e22ebc"));
		int nivel = 0;
		if (Cache.jogadores.get(p.getUniqueId()) != 0)
			nivel = Cache.jogadores.get(p.getUniqueId());
		int posicao = 20;
		for (int i = 1; i < 6; i++) {
			if (nivel >= i) {
				inv.setItem(posicao,
						API.getVerde("§6Prestigio " + i, new String[] { "§7Voc§ j§ alcan§ou este n§vel" }));
			} else {
				inv.setItem(posicao,
						API.getCinza("§8Prestigio " + i, new String[] { "§8Voc§ ainda n§o alcan§ou este n§vel" }));
			}
			posicao++;
		}
		if (com.lucas.specterrankup.api.API.getNextRank(p.getUniqueId()) == null) {
			inv.setItem(40, API.head64("§6Evoluir Prestigio",
					Arrays.asList(new String[] { "§7Voc§ perder§ seu rank, seus", "§7coins, seus meteoritos e",
							"§7suas runas", "§7", "§eEvoluindo para Prestigio " + (nivel + 1) }),
					"http://textures.minecraft.net/texture/dfeb39d71ef8e6a42646593393a5753ce26a1bee27a0ca8a32cb637b1ffae"));
		}
		return inv;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		if (s instanceof Player) {
			Player p = (Player) s;
			p.openInventory(getInv(p));
		}
		return false;
	}
}
