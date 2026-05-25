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
package ext.mods.commons.services;

import java.util.prefs.Preferences;
import java.lang.Math;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import ext.mods.commons.pool.CoroutinePool;

public class RamAllocationService implements IRamAllocationService {

    private static final Preferences prefs = Preferences.userRoot().node("ram_allocation_settings");
    
    private static final int MIN_GS_MB = 1024;
    private static final int MIN_LS_MB = 128;

    private int currentGsMemoryMB; 
    private int currentLsMemoryMB;
    private int availablePhysicalMemoryGB; 
    private int totalPhysicalMemoryGB; 
    

    public RamAllocationService() {
        this.totalPhysicalMemoryGB = determineTotalPhysicalMemoryGB(); 
        this.availablePhysicalMemoryGB = determineAvailablePhysicalMemoryGB(); 
        
        int savedGs = prefs.getInt("gsMemoryMB", 2048);
        int savedLs = prefs.getInt("lsMemoryMB", 512);

        validateAndApplyAllocation(savedGs, savedLs);
    }
    
    /**
     * Tenta aplicar os valores. Se ultrapassar a RAM livre, ajusta proporcionalmente.
     */
    private void validateAndApplyAllocation(int targetGs, int targetLs) {
        long availableMB = this.availablePhysicalMemoryGB * 1024L;

        targetLs = Math.max(targetLs, MIN_LS_MB);
        targetGs = Math.max(targetGs, MIN_GS_MB);

        if (targetGs + targetLs > availableMB) {
            targetGs = (int) (availableMB - targetLs);
            
            if (targetGs < MIN_GS_MB) {
                targetLs = MIN_LS_MB;
                targetGs = (int) (availableMB - targetLs);
                
                if (targetGs < MIN_GS_MB) targetGs = MIN_GS_MB;
            }
        }

        this.currentGsMemoryMB = targetGs;
        this.currentLsMemoryMB = targetLs;
        
        saveConfigurations();
    }

    private int determineTotalPhysicalMemoryGB() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return (int) (osBean.getTotalPhysicalMemorySize() / (1024L * 1024L * 1024L));
    }

    private int determineAvailablePhysicalMemoryGB() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double freeGB = (double) osBean.getFreePhysicalMemorySize() / (1024L * 1024L * 1024L);
        return Math.max(1, (int) Math.ceil(freeGB));
    }
    
    
    @Override public int getGsMemoryMB() { return currentGsMemoryMB; }
    
    @Override 
    public boolean setGsMemoryMB(int value) { 
        long maxAvailableMB = this.availablePhysicalMemoryGB * 1024L;
        
        if (value >= MIN_GS_MB && (value + currentLsMemoryMB) <= maxAvailableMB) {
            this.currentGsMemoryMB = value;
            saveConfigurations(); 
            return true;
        }
        return false;
    }

    @Override public int getLsMemoryMB() { return currentLsMemoryMB; }
    
    @Override 
    public boolean setLsMemoryMB(int value) { 
        long maxAvailableMB = this.availablePhysicalMemoryGB * 1024L;
        
        if (value >= MIN_LS_MB && (value + currentGsMemoryMB) <= maxAvailableMB) {
            this.currentLsMemoryMB = value;
            saveConfigurations(); 
            return true;
        }
        return false;
    }
    
    @Override public int getTotalPhysicalMemoryGB() { return totalPhysicalMemoryGB; }
    @Override public int getAvailablePhysicalMemoryGB() { return availablePhysicalMemoryGB; }
    @Override public int getTotalMemoryMB() { return currentGsMemoryMB + currentLsMemoryMB; }

    private void saveConfigurations() {
        prefs.putInt("gsMemoryMB", currentGsMemoryMB);
        prefs.putInt("lsMemoryMB", currentLsMemoryMB);
    }


    @Override
    public void startMemoryMonitoring(Runnable onUpdate) {
        
        CoroutinePool.scheduleAtFixedRate(() -> {
            try {
                int newAvailableGB = determineAvailablePhysicalMemoryGB();
                
                availablePhysicalMemoryGB = newAvailableGB;
                
                if ((currentGsMemoryMB + currentLsMemoryMB) > (newAvailableGB * 1024)) {
                     validateAndApplyAllocation(currentGsMemoryMB, currentLsMemoryMB);
                }
                
                if (onUpdate != null) {
                    onUpdate.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000);
    }

    @Override
    public void stopMemoryMonitoring() {
    }
}