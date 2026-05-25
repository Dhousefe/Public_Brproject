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
package ext.mods.security.services;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import ext.mods.commons.gui.ThemeManager;

/**
 * Gerencia persistência de UI e Banco de Dados.
 * Versão Final: Edição cirúrgica de arquivos (Zero alteração de formatação).
 */
public class DatabaseManager {

    private static final String PREFS_ROOT = "dashboard_settings";
    private static final String SERVER_PROPERTIES_PATH = "./game/config/server.properties";
    private static final String GEOENGINE_PROPERTIES_PATH = "./game/config/geoengine.properties";
    private static final String LOGINSERVER_PROPERTIES_PATH = "./login/config/loginserver.properties";
    
    private static DatabaseManager instance;
    private final Preferences prefs;
    private boolean lightModeEnabled;
    private boolean developerModeEnabled;
    
    private DatabaseManager() {
        this.prefs = Preferences.userRoot().node(PREFS_ROOT);
        loadPreferences();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void loadPreferences() {
        this.lightModeEnabled = isLightModeEnabled();
        this.developerModeEnabled = isDeveloperModeEnabled();
    }
    

    public String getServerHostname() {
        return loadProperty(SERVER_PROPERTIES_PATH, "Hostname", "*");
    }

    public boolean isLightModeEnabled() {
        return Boolean.parseBoolean(loadProperty(GEOENGINE_PROPERTIES_PATH, "UseMinimalGeoOnly", "false"));
    }

    public boolean isDeveloperModeEnabled() {
        return Boolean.parseBoolean(loadProperty(SERVER_PROPERTIES_PATH, "Developer", "false"));
    }


    public void setLightMode(boolean isEnabled, JFrame frame) {
        updateProperty(GEOENGINE_PROPERTIES_PATH, "UseMinimalGeoOnly", String.valueOf(isEnabled));
        this.lightModeEnabled = isEnabled;
        JOptionPane.showMessageDialog(frame, "Light Mode " + (isEnabled ? "ativado." : "desativado.") + "\n(Reinicie o servidor).", "Configuração", JOptionPane.INFORMATION_MESSAGE);
    }

    public void setDeveloperMode(boolean isEnabled, JFrame frame) {
        updateProperty(SERVER_PROPERTIES_PATH, "Developer", String.valueOf(isEnabled));
        this.developerModeEnabled = isEnabled;
        System.out.println("[CONFIG] Developer Mode " + (isEnabled ? "ativado" : "desativado"));
        JOptionPane.showMessageDialog(frame, "Developer Mode " + (isEnabled ? "ativado." : "desativado."), "Configuração", JOptionPane.INFORMATION_MESSAGE);
    }
    

    public Properties loadDatabaseConfig() {
        Properties dbProps = new Properties();
        dbProps.setProperty("host", "localhost");
        dbProps.setProperty("dbName", "l2jdb");
        dbProps.setProperty("user", "root");
        dbProps.setProperty("pass", "");

        File propsFile = new File(SERVER_PROPERTIES_PATH);
        if (propsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propsFile)) {
                Properties serverProps = new Properties();
                serverProps.load(fis);

                dbProps.setProperty("user", serverProps.getProperty("Login", serverProps.getProperty("sql.login", "root")));
                dbProps.setProperty("pass", serverProps.getProperty("Password", serverProps.getProperty("sql.password", "")));
                String url = serverProps.getProperty("URL", serverProps.getProperty("sql.url", ""));
                
                Pattern patternMariadb = Pattern.compile("jdbc:(?:mariadb|mysql)://([^:/]+)(?::\\d+)?/([^?]+)");
                Pattern patternSqlserver = Pattern.compile("jdbc:sqlserver://([^;]+);databaseName=([^;]+)");

                Matcher matcher = patternMariadb.matcher(url);
                if (matcher.find()) {
                    dbProps.setProperty("host", matcher.group(1));
                    dbProps.setProperty("dbName", matcher.group(2));
                } else {
                    matcher = patternSqlserver.matcher(url);
                    if (matcher.find()) {
                        dbProps.setProperty("host", matcher.group(1));
                        dbProps.setProperty("dbName", matcher.group(2));
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar config DB: " + e.getMessage());
            }
        }
        return dbProps;
    }
    
    public void configurarBanco(JFrame parent) {
        Properties dbConfig = loadDatabaseConfig();

        JTextField txtHost = new JTextField(dbConfig.getProperty("host"));
        JTextField txtUser = new JTextField(dbConfig.getProperty("user"));
        JPasswordField txtSenha = new JPasswordField(dbConfig.getProperty("pass"));
        JTextField txtDatabase = new JTextField(dbConfig.getProperty("dbName"));

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Host/IP do Banco:")); panel.add(txtHost);
        panel.add(new JLabel("Nome do Database:")); panel.add(txtDatabase);
        panel.add(new JLabel("Usuário DB:")); panel.add(txtUser);
        panel.add(new JLabel("Senha DB:")); panel.add(txtSenha);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Configurar Conexão Banco de Dados", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String host = txtHost.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtSenha.getPassword());
            String dbName = txtDatabase.getText().trim();

            String jdbcUrl = "jdbc:mariadb://" + host + "/" + dbName;
            Connection conn = null;
            try {
                System.out.println("Conectando a: " + jdbcUrl);
                Class.forName("org.mariadb.jdbc.Driver"); 
                conn = DriverManager.getConnection(jdbcUrl, user, pass);
                JOptionPane.showMessageDialog(parent, "✅ Conexão com '" + dbName + "' bem-sucedida!");

                if (verificarTabelasExistentes(conn)) {
                    int escolha = JOptionPane.showOptionDialog(parent,
                        "⚠️ O banco de dados '" + dbName + "' já possui tabelas!\nO que deseja fazer?",
                        "Banco de Dados Existente",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        new String[] {"Limpar e Reinstalar Tudo", "Apenas Salvar Config", "Cancelar"}, "Cancelar");

                    if (escolha == 0) {
                        if (JOptionPane.showConfirmDialog(parent, "TEM CERTEZA?\nIsso apagará TODAS as tabelas!", "Confirmação Final", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                            dropAllTables(conn, dbName);
                            try { executarSQL(conn, parent); } catch (Exception e) { System.err.println("Erro SQL (não crítico): " + e.getMessage()); }
                            generateAndRegisterHexId(conn); 
                            atualizarArquivosProperties(host, user, pass, dbName, parent); 
                            JOptionPane.showMessageDialog(parent, "✅ Reinstalação concluída!");
                        }
                    } else if (escolha == 1) {
                        sincronizarHexIdExistente(conn);
                        atualizarArquivosProperties(host, user, pass, dbName, parent);
                        JOptionPane.showMessageDialog(parent, "✅ Configurações salvas!");
                    }
                } else {
                    try { executarSQL(conn, parent); } catch (Exception e) { System.err.println("Erro SQL (não crítico): " + e.getMessage()); }
                    generateAndRegisterHexId(conn); 
                    atualizarArquivosProperties(host, user, pass, dbName, parent);
                    JOptionPane.showMessageDialog(parent, "✅ Instalação concluída!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "❌ Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                if (conn != null) try { conn.close(); } catch (SQLException ex) {}
            }
        }
    }

    
    private void executarSQL(Connection conn, JFrame frame) throws IOException, SQLException {
        File pastaSQL = new File("./tools/sql");
        File[] arquivos = pastaSQL.listFiles((dir, name) -> name.toLowerCase().endsWith(".sql"));
        if (arquivos == null || arquivos.length == 0) return;

        java.util.Arrays.sort(arquivos);
        try (Statement stmt = conn.createStatement()) {
            for (File sqlFile : arquivos) {
                StringBuilder sqlBuilder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(sqlFile, StandardCharsets.UTF_8))) {
                    String linha;
                    while ((linha = br.readLine()) != null) {
                        String trimmedLine = linha.trim();
                        if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("--") && !trimmedLine.startsWith("#")) {
                            sqlBuilder.append(linha).append("\n");
                        }
                    }
                }
                String[] comandos = sqlBuilder.toString().split(";\\s*(\\n|$)");
                for (String cmd : comandos) {
                    if (!cmd.trim().isEmpty()) {
                        try { stmt.execute(cmd.trim()); } catch (SQLException e) { System.err.println("Aviso SQL: " + e.getMessage()); }
                    }
                }
            }
        }
    }

    private void generateAndRegisterHexId(Connection conn) throws IOException, SQLException {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        String hexId = new BigInteger(1, bytes).toString(16).toUpperCase();
        int serverId = 1;

        saveHexIdToFile("./game/config/hexid.txt", serverId, hexId);
        saveHexIdToFile("./login/config/hexid.txt", serverId, hexId);

        try (Statement stmt = conn.createStatement()) {
             stmt.execute("CREATE TABLE IF NOT EXISTS `gameservers` (`server_id` int NOT NULL DEFAULT 0, `hexid` varchar(50) NOT NULL DEFAULT '', `host` varchar(50) NOT NULL DEFAULT '', PRIMARY KEY (`server_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
        }
        
        String hostname = getServerHostname();
        if (hostname == null || hostname.trim().isEmpty() || hostname.trim().equalsIgnoreCase("localhost") || hostname.equals("*")) {
            hostname = "127.0.0.1";
        } else {
            hostname = hostname.trim();
        }
        
        String sql = "INSERT INTO gameservers (server_id, hexid, host) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE hexid = ?, host = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serverId); 
            ps.setString(2, hexId); 
            ps.setString(3, hostname);
            ps.setString(4, hexId); 
            ps.setString(5, hostname);
            ps.executeUpdate();
        }
    }

    private void saveHexIdToFile(String filePath, int serverId, String hexId) throws IOException {
        String hexIdUpper = (hexId != null) ? hexId.toUpperCase() : "";
        
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        
        Properties hexSetting = new Properties();
        hexSetting.setProperty("ServerID", String.valueOf(serverId));
        hexSetting.setProperty("HexID", hexIdUpper);
        
        try (FileOutputStream out = new FileOutputStream(file)) {
            hexSetting.store(out, "the hexID to auth into login");
        }
    }

    /**
     * Sincroniza o hexid existente entre arquivos e banco de dados, sem gerar um novo.
     * Preserva o hexid atual se já existir.
     */
    private void sincronizarHexIdExistente(Connection conn) throws IOException, SQLException {
        int serverId = 1;
        String hexId = null;
        
        File gameHexFile = new File("./game/config/hexid.txt");
        File loginHexFile = new File("./login/config/hexid.txt");
        
        if (gameHexFile.exists()) {
            Properties hexProps = new Properties();
            try (FileInputStream fis = new FileInputStream(gameHexFile)) {
                hexProps.load(fis);
                hexId = hexProps.getProperty("HexID");
                String serverIdStr = hexProps.getProperty("ServerID");
                if (serverIdStr != null) {
                    try {
                        serverId = Integer.parseInt(serverIdStr);
                    } catch (NumberFormatException e) {
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler hexid.txt: " + e.getMessage());
            }
        }
        
        if ((hexId == null || hexId.trim().isEmpty()) && loginHexFile.exists()) {
            Properties hexProps = new Properties();
            try (FileInputStream fis = new FileInputStream(loginHexFile)) {
                hexProps.load(fis);
                hexId = hexProps.getProperty("HexID");
                String serverIdStr = hexProps.getProperty("ServerID");
                if (serverIdStr != null) {
                    try {
                        serverId = Integer.parseInt(serverIdStr);
                    } catch (NumberFormatException e) {
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler hexid.txt: " + e.getMessage());
            }
        }
        
        if ((hexId == null || hexId.trim().isEmpty())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS `gameservers` (`server_id` int NOT NULL DEFAULT 0, `hexid` varchar(50) NOT NULL DEFAULT '', `host` varchar(50) NOT NULL DEFAULT '', PRIMARY KEY (`server_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                
                try (ResultSet rs = stmt.executeQuery("SELECT hexid FROM gameservers WHERE server_id = " + serverId)) {
                    if (rs.next()) {
                        String dbHexId = rs.getString("hexid");
                        if (dbHexId != null && !dbHexId.trim().isEmpty()) {
                            try {
                                new BigInteger(dbHexId.trim(), 16);
                                hexId = dbHexId;
                            } catch (NumberFormatException e) {
                                System.err.println("HexID inválido encontrado no banco. Será gerado um novo.");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao ler hexid do banco: " + e.getMessage());
            }
        }
        
        if (hexId != null && !hexId.trim().isEmpty()) {
            hexId = hexId.trim().toUpperCase();
            
            try {
                new BigInteger(hexId, 16);
            } catch (NumberFormatException e) {
                System.err.println("HexID inválido encontrado: " + hexId + ". Gerando novo hexid...");
                hexId = null;
            }
        }
        
        if (hexId != null && !hexId.trim().isEmpty()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS `gameservers` (`server_id` int NOT NULL DEFAULT 0, `hexid` varchar(50) NOT NULL DEFAULT '', `host` varchar(50) NOT NULL DEFAULT '', PRIMARY KEY (`server_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
            }
            
            String hostname = getServerHostname();
            if (hostname == null || hostname.trim().isEmpty() || hostname.trim().equalsIgnoreCase("localhost") || hostname.equals("*")) {
                hostname = "127.0.0.1";
            } else {
                hostname = hostname.trim();
            }
            
            String sql = "INSERT INTO gameservers (server_id, hexid, host) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE hexid = ?, host = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, serverId);
                ps.setString(2, hexId);
                ps.setString(3, hostname);
                ps.setString(4, hexId);
                ps.setString(5, hostname);
                ps.executeUpdate();
            }
            
            saveHexIdToFile("./game/config/hexid.txt", serverId, hexId);
            saveHexIdToFile("./login/config/hexid.txt", serverId, hexId);
        } else {
            System.out.println("Nenhum hexid existente encontrado. Gerando novo hexid...");
            generateAndRegisterHexId(conn);
        }
    }

    private boolean verificarTabelasExistentes(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            return rs.next();
        }
    }
    
    private void dropAllTables(Connection conn, String dbName) throws SQLException {
        List<String> tabelas = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("SET FOREIGN_KEY_CHECKS = 0;"); } catch (SQLException e) {}
            try (ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                while (rs.next()) tabelas.add(rs.getString(1));
            }
            for (String tabela : tabelas) {
                try { stmt.executeUpdate("DROP TABLE IF EXISTS `" + tabela + "`"); } catch (SQLException e) {}
            }
            try { stmt.execute("SET FOREIGN_KEY_CHECKS = 1;"); } catch (SQLException e) {}
        }
    }


    private void atualizarArquivosProperties(String host, String user, String pass, String dbName, JFrame parent) throws IOException {
        System.out.println("Atualizando arquivos de configuração do DB...");
        
        File gameServerProps = new File(SERVER_PROPERTIES_PATH);
        File loginServerProps = new File(LOGINSERVER_PROPERTIES_PATH);

        atualizarDBProperties(gameServerProps, host, dbName, user, pass, "sql.url", "sql.login", "sql.password");
        atualizarDBProperties(loginServerProps, host, dbName, user, pass, "sql.url", "sql.login", "sql.password");
        
        updateProperty(SERVER_PROPERTIES_PATH, "Hostname", host);

        System.out.println("Atualização dos arquivos de configuração concluída.");
    }

    /**
     * Atualiza uma chave simples em um arquivo .properties preservando TUDO.
     */
    private void updateProperty(String filePath, String key, String newValue) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            List<String> newLines = new ArrayList<>();
            boolean keyFound = false;
            
            String regex = "^(\\s*" + Pattern.quote(key) + "\\s*)([=:])(.*)$";
            Pattern pattern = Pattern.compile(regex);

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#") || trimmed.startsWith("!")) {
                    newLines.add(line);
                    continue;
                }

                Matcher m = pattern.matcher(line);
                if (m.matches()) {
                    String prefix = m.group(1);
                    String separator = m.group(2);
                    String oldTail = m.group(3);
                    
                    String comment = "";
                    int commentIndex = findInlineCommentIndex(oldTail);
                    if (commentIndex >= 0) {
                        comment = oldTail.substring(commentIndex);
                    }
                    
                    newLines.add(prefix + separator + " " + newValue + comment);
                    keyFound = true;
                } else {
                    newLines.add(line);
                }
            }

            if (!keyFound) {
                newLines.add(key + " = " + newValue);
            }

            Files.write(file.toPath(), newLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.err.println("Erro ao atualizar propriedade '" + key + "': " + e.getMessage());
        }
    }

    /**
     * Encontra comentário inline em uma linha de properties ("#" ou "!"), preservando ordem e comentários.
     */
    private int findInlineCommentIndex(String valueWithComment) {
        if (valueWithComment == null || valueWithComment.isEmpty()) {
            return -1;
        }
        
        for (int i = 0; i < valueWithComment.length(); i++) {
            char c = valueWithComment.charAt(i);
            if (c == '#' || c == '!') {
                return i;
            }
        }
        
        return -1;
    }

    /**
     * Atualiza as chaves específicas de banco de dados (URL, User, Pass)
     */
    private void atualizarDBProperties(File file, String host, String dbName, String user, String pass, String urlKey, String userKey, String passKey) throws IOException {
        if (!file.exists()) return;
        
        Path filePath = file.toPath();
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<String> newLines = new ArrayList<>();
        
        String regexTemplate = "^(\\s*%s\\s*[=:]\\s*)(.*)$"; 

        boolean urlFound = false, userFound = false, passFound = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#") || trimmed.startsWith("!")) {
                newLines.add(line);
                continue;
            }

            Matcher mUrl = Pattern.compile(String.format(regexTemplate, Pattern.quote(urlKey))).matcher(line);
            Matcher mUser = Pattern.compile(String.format(regexTemplate, Pattern.quote(userKey))).matcher(line);
            Matcher mPass = Pattern.compile(String.format(regexTemplate, Pattern.quote(passKey))).matcher(line);

            if (mUrl.matches()) {
                String prefix = mUrl.group(1);
                String oldValue = mUrl.group(2).trim();
                String newUrlValue = buildNewUrl(oldValue, host, dbName);
                newLines.add(prefix + newUrlValue);
                urlFound = true;
            } 
            else if (mUser.matches()) {
                String prefix = mUser.group(1);
                newLines.add(prefix + user);
                userFound = true;
            } 
            else if (mPass.matches()) {
                String prefix = mPass.group(1);
                newLines.add(prefix + pass);
                passFound = true;
            } 
            else {
                newLines.add(line);
            }
        }

        if (!urlFound) newLines.add(urlKey + " = jdbc:mariadb://" + host + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8");
        if (!userFound) newLines.add(userKey + " = " + user);
        if (!passFound) newLines.add(passKey + " = " + pass);

        Files.write(filePath, newLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    private String buildNewUrl(String oldUrl, String newHost, String newDbName) {
        int questionMarkIndex = oldUrl.indexOf('?');
        String params = "";
        
        if (questionMarkIndex != -1) {
            params = oldUrl.substring(questionMarkIndex); 
        } else {
            if (oldUrl.contains("mysql") || oldUrl.contains("mariadb")) {
                params = "?useUnicode=true&characterEncoding=UTF-8";
            }
        }

        String protocol = "jdbc:mariadb://";
        if (oldUrl.startsWith("jdbc:mysql:")) protocol = "jdbc:mysql://";
        
        return protocol + newHost + "/" + newDbName + params;
    }

    private String loadProperty(String filePath, String key, String defaultValue) {
        Properties props = new Properties();
        File file = new File(filePath);
        if (!file.exists()) return defaultValue;
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
            return props.getProperty(key, defaultValue);
        } catch (IOException e) {
            return defaultValue;
        }
    }
}