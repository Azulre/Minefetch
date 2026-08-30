package io.azulre.minefetch;

import com.sun.management.OperatingSystemMXBean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.lang.management.ManagementFactory;

public class Loadometer {

    private double memUsage;
    private double cpuUsage;
    private double worldSize;
    private boolean worldGrabbed = false;

    public boolean init(Plugin plugin) {
        updateLoad();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, this::updateLoad, 0L, 2400L);
        scheduler.runTaskTimerAsynchronously(plugin, this::updateWorldSize, 600L, 1200L);
        scheduler.runTaskTimerAsynchronously(plugin, new TPS(), 100L, 1L);

        return true;
    }

    public void updateLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        cpuUsage = osBean.getSystemCpuLoad() * 100.0D;

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        memUsage = ((double) usedMemory / (double) totalMemory) * 100.0D;
    }

    public void updateWorldSize() {
        if (!worldGrabbed) {
            Bukkit.getLogger().info("[Minefetch] Loading world size for the first time..");
        }

        File worldDirectory = new File(
                Bukkit.getServer().getWorldContainer(),
                Bukkit.getServer().getWorlds().get(0).getName()
        );
        long sizeInBytes = getDirectorySize(worldDirectory);
        double sizeInGB = sizeInBytes / 1048576.0D;
        worldSize = sizeInGB;
        worldGrabbed = true;
    }

    public double getCPULoad() {
        return cpuUsage;
    }

    public double getMEMLoad() {
        return memUsage;
    }

    public double getWorldSize() {
        if (!worldGrabbed) {
            return 0.0D;
        }
        return worldSize;
    }

    private long getDirectorySize(File directory) {
        long size = 0L;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirectorySize(file);
                }
            }
        }
        return size;
    }

    public String barBuilder(double percentageFree) {
        ChatColor wrapColor = ChatColor.WHITE;
        ChatColor color;
        if (percentageFree >= 60.0D) {
            color = ChatColor.RED;
        } else if (percentageFree >= 35.0D) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.GREEN;
        }

        String bar = "";
        int looped = 0;
        while ((double) looped++ < percentageFree / 5.0D) {
            bar = bar + "#";
        }
        bar = bar + ChatColor.WHITE;
        while (looped++ <= 20) {
            bar = bar + "_";
        }

        return wrapColor + "[" + color + bar + wrapColor + "]  (" + Math.round(percentageFree) + "%)";
    }
}
