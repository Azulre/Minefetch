# Minefetch

Spigot plugin that parses the output from fastfetch, plus the current JVM, to display info and load about the current system.

This is a fork of [clockwork04/Minefetch](https://github.com/clockwork04/Minefetch), which itself was heavily based on [WamWooWam/PowerMacInfo](https://github.com/WamWooWam/PowerMacInfo). clockwork04's version was originally supposed to be a straight x86 port of PowerMacInfo, but PowerMacInfo mostly relies on "Windfarm" (a PowerMac G5 exclusive kernel driver) and OpenFirmware, so parsing neofetch ended up being way easier than rewriting all that in Java.

I forked it because neofetch got discontinued back in 2024 and stopped being packaged on most distros, which meant the plugin just silently died with a useless stacktrace on any half-recent server. Swapped it over to [fastfetch](https://github.com/fastfetch-cli/fastfetch) instead, since it's actively maintained and outputs basically the same key: value format neofetch used to. While I was in there I also fixed a crash that would happen whenever any fetched value (memory usage, most commonly) contained a `%` character, since the original code was throwing that straight into `String.format` for no real reason.

Code's still not great, I'm not much of a Java person either, but it works.

<img style="width: 75%" src=screenshots/minefetch1.png>

## Requirements
- A Minecraft server running on Linux
- Java 17+ (tested on Paper for MC 1.21+)
- Spigot compatible server (I used PaperMC)
- fastfetch installed and available on PATH for whichever user runs the server

## Commands
- `/neofetch` Parsed output from fastfetch printed to the sender.
- `/loadfetch` Output mostly from the JVM about CPU and RAM usage with cool graphs.

## About the release jar

The `.jar` in the Releases tab was compiled targeting Java 11, so it'll only run on servers using Java 11 or newer (anything running modern Paper builds). If you're on an older setup — like a 1.12.2 server stuck on Java 8 — that jar won't load and you'll get an `UnsupportedClassVersionError`.

In that case just clone this repo and compile it yourself against whatever Java version your server actually uses:

```bash
git clone https://github.com/Azulre/Minefetch.git
cd Minefetch/src
javac -cp .:/path/to/your/server.jar io/azulre/minefetch/*.java
jar cf Minefetch-1.0.jar plugin.yml io/azulre/minefetch/*.class
```

Point `-cp` at your own server's jar (or the cached patched one, if you're on Paper — usually sitting in `cache/patched_<version>.jar`), since that's what actually has the Bukkit/Paper API classes needed to compile against. Whatever `javac` version you run this with is the Java version the resulting jar will target.
