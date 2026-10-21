package com.misanthropy.linggango.capability_fix_revived.init;

import com.misanthropy.linggango.capability_fix_revived.content.CapabilityCacheHolder;
import com.misanthropy.linggango.capability_fix_revived.content.CapabilityTypeCache;
import com.misanthropy.linggango.capability_fix_revived.event.ServerCommands;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mod(capability_fix_revived.MODID)
public class capability_fix_revived {
	public static final String MODID = "capability_fix_revived";
	public static final Logger LOGGER = LogManager.getLogger();

	public static int VERSION = 0;
	public static boolean isRecording = false;
	public static final Map<Capability<?>, CapabilityTypeCache> CACHE = new ConcurrentHashMap<>();
	public static Set<String> SET = null;

	public capability_fix_revived() {
		CFConfig.init();
		MinecraftForge.EVENT_BUS.register(ServerCommands.class);
	}

	public static <T> boolean isValidForCaching(Capability<T> cap) {
		if (CFConfig.COMMON.cacheAll.get()) return true;

		CapabilityCacheHolder holder = (CapabilityCacheHolder) (Object) cap;
		int version = VERSION;
		CapabilityTypeCache lookup = holder.capability_fix_revived$entry();
		if (lookup != null && lookup.version() == version) {
			if (!lookup.cached() && isRecording) lookup.count++;
			return lookup.cached();
		}

		lookup = CACHE.get(cap);
		if (lookup == null) {
			Set<String> set = SET;
			if (set == null) SET = set = new HashSet<>(CFConfig.COMMON.capabilityCachingWhitelist.get());

			String name = cap.getName();
			boolean isWhitelisted = set.contains(name)
					|| set.contains(name.replace('.', '/'))
					|| set.contains(name.replace('/', '.'));

			lookup = new CapabilityTypeCache(cap, isWhitelisted, version);
			CACHE.put(cap, lookup);
		} else if (!lookup.cached() && isRecording) {
			lookup.count++;
		}
		if (lookup.version() == version) holder.capability_fix_revived$setEntry(lookup);
		return lookup.cached();
	}

	private record Entry(String name, int count) {}

	private static void pause() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {}
	}

	public static void writeReport(CommandSourceStack source) {
		CompletableFuture.runAsync(() -> {
			Path reportPath = Path.of("config").resolve(MODID).resolve("reports");
			try {
				Files.createDirectories(reportPath);
				Path reportFile = reportPath.resolve("capability_report.txt");

				try (FileWriter writer = new FileWriter(reportFile.toFile())) {
					writer.write("capability_fix_revived Report\n");
					writer.write("Instructions: Copy the IDs below into your capability_fix_revived-common.toml whitelist.\n");
					writer.write("WARNING: Only whitelist capabilities you know are static on entities.\n\n");

					for (Entry e : CACHE.values().stream()
							.filter(c -> !c.cached())
							.map(c -> new Entry(c.cap().getName(), c.count))
							.filter(c -> c.count() > 0)
							.sorted(Comparator.comparingInt(Entry::count).reversed())
							.toList()) {
						try {
							writer.write(String.format("%s (Calls tracked: %d)\n", e.name(), e.count()));
						} catch (IOException ioException) {
							LOGGER.error("Failed to write capability entry to report: {}", e.name(), ioException);
						}
					}

					LOGGER.info("capability_fix_revived: Report written to {}", reportFile.toAbsolutePath());
					pause();

					source.sendSystemMessage(ServerCommands.getPrefix()
							.append(Component.literal("Done! Report file is located at: ").withStyle(ChatFormatting.GRAY))
							.append(Component.literal("config/" + MODID + "/reports/capability_report.txt").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)));

					if (isRecording) {
						isRecording = false;
						pause();
						source.sendSystemMessage(ServerCommands.getStatusMessage(false));
					}
				}
			} catch (IOException e) {
				LOGGER.error("Failed to create directories or write capability_fix_revived report", e);
				pause();
				source.sendSystemMessage(ServerCommands.getPrefix()
						.append(Component.literal("Failed to generate report!").withStyle(ChatFormatting.RED)));
			}
		});
	}
}
