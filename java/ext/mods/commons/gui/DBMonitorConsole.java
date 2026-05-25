/*
* Copyleft © 2024-2026 L2Brproject
* * This file is part of L2Brproject derived from aCis409/RusaCis3.8
* * L2Brproject is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the
* Free Software Foundation, either version 3 of the License.
* * L2Brproject is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
* * You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date; 

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ext.mods.commons.gui.CustomTopPanel;
import ext.mods.commons.gui.ThemeManager;
import ext.mods.commons.gui.GuiUtils; 


/**
 * Console de monitoramento de queries SQL em tempo real (Runtime SQL Logger).
 */
public class DBMonitorConsole {

    private static final Logger LOG = Logger.getLogger(DBMonitorConsole.class.getName());
    private static final String SERVER_PROPERTIES_PATH = "./game/config/server.properties";
    
    private Timer monitoringTimer;
    private static final long MONITOR_INTERVAL_MS = 5000;
    
	private JFrame frame;
    private JTextArea console;
    private CustomTopPanel topPanel;
    
    private static DBMonitorConsole INSTANCE;
    
    public static DBMonitorConsole getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBMonitorConsole();
        }
        return INSTANCE;
    }

	public static void main(String[] args) {
		ThemeManager.applyTheme();
		
		SwingUtilities.invokeLater(() -> {
			DBMonitorConsole monitor = getInstance();
            monitor.initialize(); 
            
            if (monitor.frame != null) {
                monitor.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            }
            
            monitor.showWindow();
            
            monitor.startActiveMonitoring(monitor.loadDatabaseConfig()); 
		});
	}

	private DBMonitorConsole() {
	}

	public void initialize() {
		try {
            ThemeManager.applyTheme(); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Falha ao aplicar o tema do ThemeManager.", e);
        }
        
		frame = new JFrame("SQL Query Monitor");
		frame.setUndecorated(true);
        frame.setSize(1000, 600); 
        frame.setLocationRelativeTo(null);
        
		console = new JTextArea();
		console.setEditable(false);
		console.setLineWrap(true);
		console.setWrapStyleWord(true);
		console.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        console.setBackground(ThemeManager.COMPONENT_BACKGROUND);
        console.setForeground(ThemeManager.TEXT_COLOR);
        
		JScrollPane scroll = new JScrollPane(console);
		scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1)); 
		scroll.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND);
        
        Runnable closeAction = () -> {
            stopMonitoring();
            frame.dispose(); 
        };
        
        String iconPath = "./images/16x16.png"; 

        try {
            frame.setIconImages(GuiUtils.loadIcons()); 
        } catch (Exception e) {
            LOG.warning("Falha ao carregar ícones via GuiUtils.");
        }

        topPanel = new CustomTopPanel(frame, new JMenuBar(), closeAction, true, iconPath);
        
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
    
    /**
     * Lógica de leitura de arquivo da InterfaceLC.
     */
	private Properties loadDatabaseConfig() {
		Properties dbProps = new Properties();
		dbProps.setProperty("host", "localhost");
		dbProps.setProperty("dbName", "l2jdb"); 
		dbProps.setProperty("user", "root");
		dbProps.setProperty("pass", "");
        dbProps.setProperty("driver", "org.mariadb.jdbc.Driver"); 

		File propsFile = new File(SERVER_PROPERTIES_PATH);
		if (propsFile.exists()) {
			try (FileInputStream fis = new FileInputStream(propsFile)) {
				Properties serverProps = new Properties();
				serverProps.load(fis);

				dbProps.setProperty("user", serverProps.getProperty("Login", serverProps.getProperty("sql.login", "root")));
				dbProps.setProperty("pass", serverProps.getProperty("Password", serverProps.getProperty("sql.password", "")));
				String url = serverProps.getProperty("URL", serverProps.getProperty("sql.url", ""));
                
                dbProps.setProperty("url", url); 

				Pattern patternMariadb = Pattern.compile("jdbc:(?:mariadb|mysql)://([^:/]+)(?::\\d+)?/([^?]+)");

				Matcher matcher = patternMariadb.matcher(url);
				if (matcher.find()) {
					dbProps.setProperty("host", matcher.group(1));
					dbProps.setProperty("dbName", matcher.group(2));
				}
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Erro ao carregar configurações do DB de server.properties.", e);
			}
		} else {
             LOG.warning("Arquivo de configuração DB não encontrado em: " + SERVER_PROPERTIES_PATH);
        }
		return dbProps;
	}

    /**
     * Inicia a thread de monitoramento que consulta information_schema.processlist.
     */
    private void startActiveMonitoring(Properties dbConfig) {
        if (dbConfig == null || dbConfig.getProperty("url") == null) {
            logQuery("ERRO: Configuração DB ausente. Não é possível iniciar o monitoramento ativo.", 0);
            return;
        }
        
        if (monitoringTimer != null) {
            monitoringTimer.cancel();
        }
        
        final String url = dbConfig.getProperty("url");
        final String user = dbConfig.getProperty("user");
        final String pass = dbConfig.getProperty("pass");
        final String driver = dbConfig.getProperty("driver");

        try {
             Class.forName(driver); 
        } catch (ClassNotFoundException e) {
             logQuery("ERRO: Driver JDBC (" + driver + ") não encontrado. O monitoramento ativo falhará.", 0);
             return;
        }

        logQuery("Monitoramento ativo configurado para DB: " + dbConfig.getProperty("dbName"), 0);
        
        monitoringTimer = new Timer(true);
        monitoringTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                    checkLongRunningQueries(conn);
                    
                } catch (SQLException e) {
                    LOG.log(Level.SEVERE, "Falha na consulta de monitoramento ativo: " + e.getMessage(), e);
                    logQuery("ERRO FATAL NO MONITOR: Falha ao consultar o processlist. Monitoramento interrompido. Detalhe: " + e.getMessage(), 0);
                    stopMonitoring();
                }
            }
        }, MONITOR_INTERVAL_MS, MONITOR_INTERVAL_MS);
        
        logQuery("\n--- MONITORAMENTO ATIVO INICIADO ---", 0);
        logQuery("Checando queries globalmente a cada " + (MONITOR_INTERVAL_MS / 1000) + " segundos.", 0);
    }

    /**
     * Para o timer de monitoramento ativo.
     */
    private void stopMonitoring() {
        if (monitoringTimer != null) {
            monitoringTimer.cancel();
            monitoringTimer.purge();
            monitoringTimer = null;
            logQuery("\n--- MONITORAMENTO ATIVO INTERROMPIDO ---", 0);
        }
    }
    
    /**
     * Consulta o processlist para encontrar queries lentas (Time > 0)
     * 🟢 CORRIGIDO: Removemos o filtro WHERE DB = ? para ver todas as queries ativas.
     */
    private void checkLongRunningQueries(Connection conn) throws SQLException {
        final String SQL_PROCESSLIST = "SELECT ID, USER, HOST, DB, COMMAND, TIME, STATE, INFO FROM information_schema.processlist WHERE COMMAND != 'Sleep' AND TIME > 0";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL_PROCESSLIST);
             ResultSet rs = ps.executeQuery()) {
            
            long startTime = System.currentTimeMillis();
            
            int count = 0;
            StringBuilder sb = new StringBuilder();
            
            while (rs.next()) {
                long id = rs.getLong("ID"); 
                int timeSeconds = rs.getInt("TIME");
                String user = rs.getString("USER");
                String db = Objects.toString(rs.getString("DB"), "N/A");
                String command = rs.getString("COMMAND");
                String info = Objects.toString(rs.getString("INFO"), "N/A");
                
                if (info.toUpperCase().contains("INFORMATION_SCHEMA.PROCESSLIST")) {
                    continue;
                }
                
                count++;
                
                sb.append("--------------------------------------------------------------------------------\n");
                sb.append("ALERTA: Query em Gargalo (").append(timeSeconds).append("s)\n");
                sb.append("ID: ").append(id).append(" | DB: ").append(db).append(" | User: ").append(user).append("\n");
                sb.append("COMANDO: ").append(command).append("\n");
                sb.append("SQL: ").append(info).append("\n");
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            if (count > 0) {
                logQuery(sb.toString(), duration); 
            } else if (duration > 100) {
                 logQuery("Monitor OK, mas a consulta levou " + duration + "ms para ser executada.", duration);
            }
        }
    }
    
    /**
     * Exibe a query executada no console.
     */
    private void logQuery(String sql, long executionTimeMs) {
        if (console == null || sql == null) return;
        
        String timeStatus;
        if (executionTimeMs > 100) { 
            timeStatus = "MONITOR LENTO (" + executionTimeMs + "ms)";
        } else { 
            timeStatus = "MONITOR OK";
        }

        String logEntry = String.format("[%s] %s\n%s\n\n", 
                                        timeStatus, 
                                        new Date().toString(), 
                                        sql.trim());

        SwingUtilities.invokeLater(() -> {
            if (console.getText().length() > 50000) { 
                console.setText(console.getText().substring(40000));
            }
            console.append(logEntry); 
            console.setCaretPosition(console.getDocument().getLength());
        });
    }

    /**
     * Exibe a janela do monitor.
     */
    public void showWindow() {
        if (frame != null) {
            frame.setVisible(true);
        }
    }
    
    /**
     * 🟢 HOOK ESTÁTICO (Para o GameServer logar queries rápidas)
     */
    public static void log(String sql, long executionTimeMs) {
        getInstance().logQuery(sql, executionTimeMs);
    }
}