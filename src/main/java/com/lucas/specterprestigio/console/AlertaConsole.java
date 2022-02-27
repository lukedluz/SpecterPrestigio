package com.lucas.specterprestigio.console;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AlertaConsole {
	public enum AlertaNivel {
		NORMAL(1), ERRO(2), ALERTA(3);

		private int opcao;

		AlertaNivel(int opcao) {
			this.opcao = opcao;
		}

		public int getOpcao() {
			return opcao;
		}
	}

	public static void c(String c, JavaPlugin b, AlertaNivel a) {
		String cor = "§4";
		if (a == AlertaNivel.ALERTA)
			cor = "§c";
		if (a == AlertaNivel.NORMAL)
			cor = "§a";
		Bukkit.getConsoleSender().sendMessage(
				"[" + b.getDescription().getName() + " " + b.getDescription().getVersion() + "] " + cor + c);
	}
}
