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
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, 
* SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo, Tiagorosendo, Schuster, LucasStark, damedd
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.gui.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ext.mods.commons.gui.ThemeManager;
import ext.mods.commons.util.JvmOptimizer;

public class ProcessManagerService {
    
    private static final Preferences prefs = Preferences.userRoot().node("ram_allocation_settings");

    public ProcessManagerService() {
    }

    private String getJavaExecutable() {
        String ext = System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : "";
        
        // 1º Tentativa: Usar a variável de ambiente JAVA_HOME do sistema operacional
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null && !javaHome.trim().isEmpty()) {
            return javaHome + File.separator + "bin" + File.separator + "java" + ext;
        }

        // 2º Tentativa (Fallback): Usar o diretório do Java que está rodando este próprio painel/launcher
        System.err.println("[AVISO] Variável de ambiente JAVA_HOME não encontrada. Usando java.home embutido.");
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java" + ext;
    }

    public void iniciarProcesso(String tipo, String licenseKey, String userEmail, boolean isLightModeEnabled, JFrame frame) {
        
        int memoryMB;
        if (tipo.equalsIgnoreCase("gameserver")) {
            memoryMB = prefs.getInt("gsMemoryMB", 2048);
        } else {
            memoryMB = prefs.getInt("lsMemoryMB", 512);
        }

        System.out.println("\n============================================================");
        System.out.println("  Iniciando " + tipo.toUpperCase() + " com JVM Otimizada");
        System.out.println("============================================================");
        System.out.println("  Memoria JVM: Xms=" + memoryMB + "MB | Xmx=" + memoryMB + "MB");
        
        String caminhoJava = getJavaExecutable();

        if (!new File(caminhoJava).exists()) {
            // Se mesmo com os fallbacks o arquivo não existir fisicamente, tentamos rodar apenas "java" 
            // e confiar que o executável está injetado diretamente na variável PATH do Windows/Linux.
            System.err.println("[AVISO] Caminho exato do Java não encontrado: " + caminhoJava + ". Tentando executar comando global 'java'.");
            caminhoJava = "java";
        }

        File diretorioExecucao = tipo.equals("gameserver") ? new File("game") : new File("login");

        if (!diretorioExecucao.exists()) {
            JOptionPane.showMessageDialog(frame, "A pasta '" + diretorioExecucao.getAbsolutePath() + "' não existe!", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /*StringBuilder cp = new StringBuilder();
        String sep = File.separator;
        cp.append(".").append(File.pathSeparator);
        try
        {
            final File libsDir = new File(diretorioExecucao, "../libs").getCanonicalFile();
            cp.append(JvmOptimizer.buildRuntimeClasspath(libsDir));
        }
        catch (Exception e)
        {
            System.err.println("[AVISO] Classpath ordenado falhou, usando libs/*: " + e.getMessage());
            cp.append("..").append(sep).append("libs").append(sep).append("*");
        }
        cp.append(File.pathSeparator).append("..").append(sep).append("bin"); 
        cp.append(File.pathSeparator).append("..").append(sep).append("build").append(sep).append("classes");
        cp.append(File.pathSeparator).append("..").append(sep).append("build").append(sep).append("classes").append(sep).append("java").append(sep).append("main");*/

        // --- INÍCIO DA CORREÇÃO DO CLASSPATH PARA APPCDS ---
        String cpString = "";
        try {
            final File libsDir = new File(diretorioExecucao, "../libs").getCanonicalFile();
            // JvmOptimizer já retorna a string perfeita só com os .jars organizados
            cpString = JvmOptimizer.buildRuntimeClasspath(libsDir); 
        } catch (Exception e) {
            System.err.println("[AVISO] Classpath ordenado falhou, usando libs/*: " + e.getMessage());
            // Se cair aqui, o AppCDS vai falhar de qualquer jeito por causa do asterisco, 
            // mas o servidor ainda liga sem cache.
            cpString = ".." + File.separator + "libs" + File.separator + "*"; 
        }
        // NOTA: Removemos os appends de ".", "bin" e "build/classes". 
        

        String mainClass = tipo.equals("gameserver") ? "ext.mods.gameserver.GameServer" : "ext.mods.loginserver.LoginServer";

        List<String> command = new ArrayList<>();
        command.add(caminhoJava);
        
        // Memória parametrizada pela GUI
        command.add("-Xms" + memoryMB + "m");
        command.add("-Xmx" + memoryMB + "m");
        
        if (ThemeManager.isSafeGraphics()) {
            command.add("-Dsun.java2d.opengl=false");
            command.add("-Dsun.java2d.d3d=false");
            command.add("-Dsun.java2d.pmoffscreen=false");
            command.add("-Dbrproject.safe.graphics=true");
        }
        
        // --- INÍCIO DAS FLAGS OTIMIZADAS DO INICIALIZADOR ---
        command.add("-XX:+UseG1GC");
        command.add("-XX:MaxGCPauseMillis=200");
        command.add("-XX:G1HeapRegionSize=16m");
        command.add("-XX:+UseStringDeduplication");
        command.add("-XX:+UseCompressedOops");
        command.add("-XX:+UseCompactObjectHeaders");
        command.add("-XX:+TieredCompilation");
        command.add("-XX:TieredStopAtLevel=4");
        
        // CDS Flags
        command.add("-XX:+AutoCreateSharedArchive");
        command.add("-XX:SharedArchiveFile=cache/brproject_cds.jsa");
        command.add("-Xlog:cds=error");
        // --- FIM DAS FLAGS OTIMIZADAS ---

        command.add("-cp");
        command.add(cpString);
        command.add(mainClass);
        
        if (tipo.equals("gameserver")) {
            command.add(licenseKey);
            command.add(userEmail);
        }

        System.out.println("\n--- COMANDO JVM OTIMIZADO ---");
        System.out.println(String.join(" ", command));
        System.out.println("-----------------------------\n");

        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(diretorioExecucao);
                pb.redirectErrorStream(true);
                Process processo = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream()))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        System.out.println("[" + tipo.toUpperCase() + "] " + linha);
                    }
                }

                int exitCode = processo.waitFor();
                
                if (exitCode == 2) {
                    System.out.println("Reiniciando servidor...");
                    Thread.sleep(1000);
                    iniciarProcesso(tipo, licenseKey, userEmail, isLightModeEnabled, frame);
                } 
                else if (exitCode != 0) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, 
                            "Erro no servidor (Código " + exitCode + ").", 
                            "Erro", JOptionPane.ERROR_MESSAGE)
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}