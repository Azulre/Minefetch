package io.azulre.minefetch;

import com.sun.management.OperatingSystemMXBean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Main extends JavaPlugin {

    private NeoParser neofetch;
    private Loadometer load;

    @Override
    public void onEnable() {
        getLogger().info("Minefetch v1.0 by Azulre loaded.");
        getLogger().info("Originally by clockwork04.");

        neofetch = new NeoParser();
        if (!neofetch.init(this)) {
            getLogger().severe("NeoParser failed to initialize. Is fastfetch installed?");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        load = new Loadometer();
        if (!load.init(this)) {
            getLogger().severe("Loadometer failed to initialize.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("neofetch")) {
            if (neofetch.getNeofetch("OS") == null) {
                sender.sendMessage(ChatColor.RED + "Neofetch Failed!");
                getLogger().severe("Neofetch Failed, called by: " + sender.getName());
                return false;
            }

            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            double cpuUsage = osBean.getSystemCpuLoad() * 100.0D;

            Runtime runtime = Runtime.getRuntime();
            long allocatedMemory = runtime.totalMemory();

            ArrayList<String> messageQueue = new ArrayList<>();
            ChatColor neocolor = neofetch.getNeocolor();

            messageQueue.add(neocolor + "--- " + ChatColor.WHITE + neofetch.getNeofetch("hostname") + neocolor + "---");
            messageQueue.add(neocolor + "OS: " + ChatColor.BOLD + ChatColor.WHITE + neofetch.getNeofetch("OS"));

            if (neofetch.getNeofetch("Host") != null) {
                messageQueue.add(neocolor + "Host: " + ChatColor.BOLD + ChatColor.WHITE + neofetch.getNeofetch("Host"));
            }

            messageQueue.add(neocolor + "Kernel: " + ChatColor.BOLD + ChatColor.WHITE + neofetch.getNeofetch("Kernel"));
            messageQueue.add(neocolor + "CPU: " + ChatColor.BOLD + ChatColor.WHITE + neofetch.getNeofetch("CPU") + " (" + Math.round(cpuUsage) + "%)");
            messageQueue.add(neocolor + "GPU: " + ChatColor.BOLD + ChatColor.WHITE + neofetch.getNeofetch("GPU"));
            messageQueue.add(neocolor + "Memory: " + ChatColor.BOLD + ChatColor.WHITE + neofetch.getNeofetch("Memory") + " (" + (allocatedMemory / 1048576L) + " MiB Allocated)");
            messageQueue.add(neocolor + "Uptime: " + ChatColor.BOLD + ChatColor.WHITE + neofetch.getNeofetch("Uptime"));

            String[] array = new String[messageQueue.size()];
            messageQueue.toArray(array);
            sender.sendMessage(array);
        }

        if (command.getName().equalsIgnoreCase("loadfetch")) {
            ArrayList<String> messageQueue = new ArrayList<>();
            String usage = ChatColor.BOLD + "USED:";

            messageQueue.add(ChatColor.BLUE + "--- " + ChatColor.WHITE + "Loadfetch" + ChatColor.BLUE + "---");
            messageQueue.add(ChatColor.BLUE.toString() + ChatColor.BOLD + ChatColor.WHITE + "CPU: " + neofetch.getNeofetch("CPU"));
            messageQueue.add(ChatColor.BLUE + usage + ChatColor.WHITE + load.barBuilder(load.getCPULoad()));
            messageQueue.add(ChatColor.BLUE.toString() + ChatColor.BOLD + ChatColor.WHITE + "RAM: " + neofetch.getNeofetch("Memory"));
            messageQueue.add(ChatColor.BLUE + usage + ChatColor.WHITE + load.barBuilder(load.getMEMLoad()) + " (JVM)");
            messageQueue.add(ChatColor.BOLD.toString() + ChatColor.RESET + "World size: " + Math.round(load.getWorldSize()) + " MB");

            double tps = TPS.getTPS();
            DecimalFormat df = new DecimalFormat("0.00");
            ChatColor tpsColor;
            if (tps >= 18.0D) {
                tpsColor = ChatColor.GREEN;
            } else if (tps <= 13.0D) {
                tpsColor = ChatColor.RED;
            } else {
                tpsColor = ChatColor.YELLOW;
            }
            messageQueue.add(ChatColor.BOLD + "Server TPS: " + tpsColor + df.format(tps) + " TPS");

            String[] array = new String[messageQueue.size()];
            messageQueue.toArray(array);
            sender.sendMessage(array);
        }

        return true;
    }
}
