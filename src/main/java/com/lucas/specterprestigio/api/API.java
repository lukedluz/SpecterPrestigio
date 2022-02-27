package com.lucas.specterprestigio.api;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.lucas.specterprestigio.commands.PrestigioCommand;
import com.lucas.specterprestigio.connection.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import com.lucas.specterrankup.Main;
import com.lucas.specterrankup.objetos.Rank;
import com.lucas.specterrunas.api.RunasAPI;
import com.lucas.specterlimite.api.LimiteAPI;
import com.lucas.specterprestigio.cache.Cache;

public class API {
	public static int getPrestigio(UUID p) {
		return Cache.jogadores.get(p);
	}

	public static int getPrestigio(Player p) {
		return Database.getJogador(p);
	}

	public static void setPrestigio(Player p, int nivel) {
		Cache.jogadores.replace(p.getUniqueId(), nivel);
	}

	public static void evoluir(Player p) {
		UUID uuid = p.getUniqueId();
		Rank r = com.lucas.specterrankup.api.API.getNextRank(uuid);
		if (r == null) {
			if (isNull(p)) {
				com.lucas.specterrankup.api.API.setRank(p, com.lucas.specterrankup.Main.defaultRank);
				com.lucas.specterprestigio.Main.eco.withdrawPlayer(Bukkit.getOfflinePlayer(uuid),
						com.lucas.specterprestigio.Main.eco.getBalance(Bukkit.getOfflinePlayer(uuid)));
				RunasAPI.resetarRunas(p);
				com.lucas.spectertesouros.cache.Cache.jogadores.get(p).setarTesouros(0.0);
				setPrestigio(p, Cache.jogadores.get(uuid) + 1);
				PermissionUser pu = PermissionsEx.getUser(p);
				pu.removeGroup(com.lucas.specterrankup.api.API.getRank(uuid).getNome());
				pu.addGroup(com.lucas.specterrankup.Main.defaultRank.getNome());
				p.closeInventory();
				p.openInventory(PrestigioCommand.getInv(p));
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"darkit prestigio" + Cache.jogadores.get(uuid) + " " + p.getName());
				LimiteAPI.adicionarLimites(p, 300000.0);
				p.sendMessage("§aYEAH! Você evoluiu seu prestigio com sucesso");
			} else {
				p.sendMessage("§cOPS! Você precisa esvaziar o inventário para fazer isto!");
			}
		} else {
			p.sendMessage("§cOPS! Você precisa estar na ultima classe para fazer isto!");
		}
	}

	private static boolean isNull(Player p) {
		boolean result = true;
		for (ItemStack item : p.getInventory().getContents()) {
			if (item != null)
				return false;
		}
		return result;
	}

	public static ItemStack head64(String nome, List<String> lore, String url) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.getEncoder()
				.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		try {
			profileField = itemMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(itemMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		itemMeta.setLore(lore);
		itemMeta.setDisplayName(nome);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack getVerde(String nome, String[] lore) {
		return head64(nome.replace("&", "§"), Arrays.asList(lore),
				"http://textures.minecraft.net/texture/22d145c93e5eac48a661c6f27fdaff5922cf433dd627bf23eec378b9956197");
	}

	public static ItemStack getCinza(String nome, String[] lore) {
		return head64(nome.replace("&", "§"), Arrays.asList(lore),
				"http://textures.minecraft.net/texture/608f323462fb434e928bd6728638c944ee3d812e162b9c6ba070fcac9bf9");
	}
}
