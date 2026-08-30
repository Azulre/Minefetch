package io.azulre.minefetch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NeoParser {

    boolean debug = false;
    ChatColor neocolor;
    private final List<String> neofetch = new ArrayList<>();
    private final HashMap<String, String> neomap = new HashMap<>();

    public boolean init(Plugin plugin) {
        plugin.getLogger().info("Reading fastfetch output (first run can take a moment on some systems).");

        readNeofetch();
        parseNeofetch();
        distroDetect();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, this::updateNeofetch, 0L, 1200L);

        return true;
    }

    private void distroDetect() {
        String distroString = neomap.get("OS").toString();
        String mintRegex = "Arch";
        String redRegex = "Debian";
        String purpleRegex = "Gentoo";

        if (distroString.contains(mintRegex)) {
            neocolor = ChatColor.AQUA;
        } else if (distroString.contains(redRegex)) {
            neocolor = ChatColor.RED;
        } else if (distroString.contains(purpleRegex)) {
            neocolor = ChatColor.DARK_PURPLE;
        } else {
            neocolor = ChatColor.BLUE;
        }
    }

    public ChatColor getNeocolor() {
        return neocolor;
    }

    public void updateNeofetch() {
        readNeofetch();
        parseNeofetch();
    }

    public void readNeofetch() {
        neofetch.clear();
        try {
            Process process = Runtime.getRuntime().exec("fastfetch --logo none --pipe");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (debug) {
                    Bukkit.getLogger().info(line);
                }
                neofetch.add(line);
            }
            if (debug) {
                Bukkit.getLogger().info("Neofetch Read!");
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void parseNeofetch() {
        neomap.clear();
        neomap.put("hostname", neofetch.get(0));
        for (String outputLine : neofetch) {
            int colonIndex = outputLine.indexOf(":");
            if (colonIndex != -1) {
                String key = outputLine.substring(0, colonIndex).trim();
                String value = outputLine.substring(colonIndex + 1).trim();
                neomap.put(key, value);
            }
        }
        if (debug) {
            Bukkit.getLogger().info("Neofetch Parsed!");
        }
    }

    public String getNeofetch(String value) {
        Object result = neomap.get(value);
        return result == null ? null : result.toString();
    }
}
