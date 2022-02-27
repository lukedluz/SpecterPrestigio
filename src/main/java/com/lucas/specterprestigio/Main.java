package com.lucas.specterprestigio;

import java.io.File;

import com.lucas.specterprestigio.cache.Cache;
import com.lucas.specterprestigio.commands.PrestigioCommand;
import com.lucas.specterprestigio.connection.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.economy.Economy;
import com.lucas.specterprestigio.listeners.PrestigioInventory;

public class Main extends JavaPlugin {
	public static Economy eco;
	public static Main m;

	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("§7==========================");
		Bukkit.getConsoleSender().sendMessage("§7| §bSpecterPrestigio       §7|");
		Bukkit.getConsoleSender().sendMessage("§7| §bVersão 1.0             §7|");
		Bukkit.getConsoleSender().sendMessage("§7| §fStatus: §aLigado         §7|");
		Bukkit.getConsoleSender().sendMessage("§7==========================");
		Bukkit.getConsoleSender().sendMessage("");
		m = this;
		File f = new File(getDataFolder(), "config.yml");
		if (!f.exists())
			saveDefaultConfig();
		c("A configuração 'config.yml' foi carregada/criada com sucesso");
		Database.openConnection();
		Database.criarTabela();
		for (Player on : Bukkit.getOnlinePlayers()) {
			if (Database.hasJogador(on))
				Database.addJogador(on);
			Cache.jogadores.put(on.getUniqueId(), Database.getJogador(on));
		}
		c("Jogadores carregados com sucesso");
		Bukkit.getPluginManager().registerEvents(new PrestigioInventory(), this);
		getCommand("prestigio").setExecutor(new PrestigioCommand());
		c("Comandos e Eventos registrados com sucesso");
		loadVault();
		c("Plugin associado com o vault");
		registerTask();
		c("Tasks de salvamento criada com sucesso");
		c("Plugin iniciado com sucesso");
	}

	private void registerTask() {
		new BukkitRunnable() {

			@Override
			public void run() {
				salvarJogadores();
			}
		}.runTaskTimerAsynchronously(this, 20 * 3 * 60, 20 * 3 * 60);
	}

	@Override
	public void onDisable() {
		salvarJogadores();
		Database.close();
	}

	private void salvarJogadores() {
		Cache.jogadores.keySet().forEach(t -> {
			Database.setJogador(t, Cache.jogadores.get(t));
		});
	}

	private void c(String s) {
		Bukkit.getConsoleSender().sendMessage("[SpecterPrestigio] " + s);
	}

	public static Main getInstance() {
		return m;
	}

	private void loadVault() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			eco = economyProvider.getProvider();
		}
	}
}
