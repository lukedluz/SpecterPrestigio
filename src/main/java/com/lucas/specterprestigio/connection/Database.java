package com.lucas.specterprestigio.connection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.lucas.specterprestigio.console.AlertaConsole;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.lucas.specterprestigio.Main;

public class Database {
	public static Connection con;
	public static FileConfiguration c = Main.getInstance().getConfig();

	public static void openConnection() {
		if (!c.getBoolean("MySQL.Usar")) {
			openConnectionSQLite();
			return;
		}
		String host = c.getString("MySQL.IP");
		int port = c.getInt("MySQL.Porta");
		String user = c.getString("MySQL.Usuario");
		String password = c.getString("MySQL.Senha");
		String database = c.getString("MySQL.DataBase");
		String type = "jdbc:mysql://";
		String url = String.valueOf(type) + host + ":" + port + "/" + database;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, password);
			AlertaConsole.c("Conexao com o §2MySQL §asucedida!", Main.getInstance(), AlertaConsole.AlertaNivel.NORMAL);
		} catch (Exception e) {
			AlertaConsole.c("Conexao com o §4MySQL §cfalhou, alterando para §aSQLite", Main.getInstance(),
					AlertaConsole.AlertaNivel.ALERTA);
			openConnectionSQLite();
		}
	}

	public static void openConnectionSQLite() {
		File file = new File(Main.getInstance().getDataFolder(), "specterprestigio.db");
		String URL = "jdbc:sqlite:" + file;
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection(URL);
			AlertaConsole.c("Conexao com o §fSQLite §asucedida!", Main.getInstance(), AlertaConsole.AlertaNivel.NORMAL);
		} catch (Exception e) {
			AlertaConsole.c("Conexao com o §fSQLite §cfalhou, desabilitando plugin!", Main.getInstance(),
					AlertaConsole.AlertaNivel.ERRO);
			Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
		}
	}

	public static void close() {
		if (con != null) {
			try {
				con.close();
				con = null;
				AlertaConsole.c("Conexao com o banco de dados foi fechada.", Main.getInstance(),
						AlertaConsole.AlertaNivel.NORMAL);
			} catch (SQLException e) {
				e.printStackTrace();
				AlertaConsole.c("Nao foi possivel fechar a conexao com o banco de dados.", Main.getInstance(),
						AlertaConsole.AlertaNivel.ERRO);
			}
		}
	}

	public static void criarTabela() {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `SpecterPrestigio` (`player` VARCHAR(24) NULL, `uuid` VARCHAR(45) NULL, `prestigio` INTEGER NULL);");
			st.executeUpdate();
			Bukkit.getConsoleSender()
					.sendMessage("§a[SpecterPrestigio] §6Tabela §f`SpecterPrestigio` §6criada/carregada com sucesso");
		} catch (SQLException e) {
			Bukkit.getConsoleSender()
					.sendMessage("§a[SpecterPrestigio] §cNão foi possivel criar a tabela §f`SpecterPrestigio`");
			Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
			e.printStackTrace();
		}
	}

	public static boolean hasJogador(Player p) {
		String uuid = p.getUniqueId().toString();
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("SELECT * FROM `SpecterPrestigio` WHERE `uuid` = ?");
			st.setString(1, uuid);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			return false;
		}
	}

	public static int getJogador(Player p) {
		String uuid = p.getUniqueId().toString();
		if (hasJogador(p)) {
			PreparedStatement stm = null;
			try {
				stm = con.prepareStatement("SELECT * FROM `SpecterPrestigio` WHERE `uuid` = ?");
				stm.setString(1, uuid);
				ResultSet rs = stm.executeQuery();
				while (rs.next()) {
					return rs.getInt("prestigio");
				}
				return 0;
			} catch (SQLException e) {
				return 0;
			}
		} else {
			addJogador(p);
			return getJogador(p);
		}
	}

	public static void setJogador(UUID p, int rank) {
		String uuid = p.toString();
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE `SpecterPrestigio` SET `prestigio` = ? WHERE `uuid` = ?");
			st.setInt(1, rank);
			st.setString(2, uuid);
			st.executeUpdate();
		} catch (SQLException e) {
			AlertaConsole.c("Não foi possível atualizar um jogador na database", Main.getInstance(),
					AlertaConsole.AlertaNivel.ERRO);
		}
	}

	public static void addJogador(Player p) {
		String uuid = p.getUniqueId().toString();
		String nick = p.getName();
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("INSERT INTO `SpecterPrestigio`(`player`, `uuid`, `prestigio`) VALUES (?,?,?)");
			st.setString(1, nick);
			st.setString(2, uuid);
			st.setInt(3, 0);
			st.executeUpdate();
		} catch (SQLException e) {
			AlertaConsole.c("Não foi possível inserir o jogador " + p.getName() + " na database",
					Main.getInstance(), AlertaConsole.AlertaNivel.ERRO);
		}
	}
}