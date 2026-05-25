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

    private static final String JAVA_25_PATH = "C:\\Program Files\\Eclipse Adoptium\\jdk-25.0.1.8-hotspot\\bin\\java.exe";
    
    private static final Preferences prefs = Preferences.userRoot().node("ram_allocation_settings");

    public ProcessManagerService() {
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
        
        String caminhoJava = JAVA_25_PATH;
        if (!new File(caminhoJava).exists()) {
            System.err.println("[AVISO] Java 25 fixo nao encontrado. Tentando variavel do sistema.");
            caminhoJava = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        }

        File diretorioExecucao = tipo.equals("gameserver") ? new File("game") : new File("login");

        if (!diretorioExecucao.exists()) {
            JOptionPane.showMessageDialog(frame, "A pasta '" + diretorioExecucao.getAbsolutePath() + "' não existe!", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder cp = new StringBuilder();
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
        cp.append(File.pathSeparator).append("..").append(sep).append("build").append(sep).append("classes").append(sep).append("java").append(sep).append("main");

        String mainClass = tipo.equals("gameserver") ? "ext.mods.gameserver.GameServer" : "ext.mods.loginserver.LoginServer";

        List<String> command = new ArrayList<>();
        command.add(caminhoJava);
        
        command.add("-Xms" + memoryMB + "m");
        command.add("-Xmx" + memoryMB + "m");
        
        if (ThemeManager.isSafeGraphics()) {
            command.add("-Dsun.java2d.opengl=false");
            command.add("-Dsun.java2d.d3d=false");
            command.add("-Dsun.java2d.pmoffscreen=false");
            command.add("-Dbrproject.safe.graphics=true");
        }
        
        try {
            final boolean useZgc = tipo.equalsIgnoreCase("gameserver");
            command.addAll(JvmOptimizer.getRecommendedJvmFlags(useZgc, false));
        } catch (Throwable t) {
            System.err.println("[AVISO] Falha ao aplicar flags do JvmOptimizer: " + t.getMessage());
        }

        command.add("-cp");
        command.add(cp.toString());
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