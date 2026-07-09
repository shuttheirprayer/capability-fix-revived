package com.misanthropy.linggango.capability_fix_revived.init;

import com.google.common.collect.Maps;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod(capability_fix_revived.MODID)
public class capability_fix_revived {
	public static final String MODID = "capability_fix_revived";
	public static final Logger LOGGER = LogManager.getLogger();

	public static int VERSION = 0;
	public static boolean isRecording = false;
	public static final Map<Capability<?>, CapabilityTypeCache> CACHE = Maps.newIdentityHashMap();
	public static Set<String> SET = null;

	public capability_fix_revived() {
		CFConfig.init();
		// Manually register ServerCommands to bypass EventBusSubscriber FML errors
		MinecraftForge.EVENT_BUS.register(ServerCommands.class);
	}

	public static <T> boolean isValidForCaching(Capability<T> cap) {
		if (CFConfig.COMMON.cacheAll.get()) return true;

		var lookup = CACHE.get(cap);
		if (lookup != null) {
			if (isRecording && !lookup.cached()) {
				lookup.count++;
			}
			return lookup.cached();
		}

		if (SET == null) {
			SET = new HashSet<>(CFConfig.COMMON.capabilityCachingWhitelist.get());
		}

		String name = cap.getName();
		// Normalize dots and slashes to fix config-versus-registry mismatch
		boolean isWhitelisted = SET.contains(name)
				|| SET.contains(name.replace('.', '/'))
				|| SET.contains(name.replace('/', '.'));

		CACHE.put(cap, new CapabilityTypeCache(cap, isWhitelisted));
		return isWhitelisted;
	}

	public static void writeReport(CommandSourceStack source) {
		CompletableFuture.runAsync(() -> {
			// Bypassed FMLPaths using standard Java Path API
			Path reportPath = Path.of("config").resolve(MODID).resolve("reports");

			try {
				Files.createDirectories(reportPath);
				Path reportFile = reportPath.resolve("capability_report.txt");

				try (FileWriter writer = new FileWriter(reportFile.toFile())) {
					writer.write("capability_fix_revived Report\n");
					writer.write("Instructions: Copy the IDs below into your capability_fix_revived-common.toml whitelist.\n");
					writer.write("WARNING: Only whitelist capabilities you know are static on entities.\n\n");

					CACHE.values().stream()
							.filter(e -> !e.cached() && e.count > 0)
							.sorted((a, b) -> Integer.compare(b.count, a.count))
							.forEach(e -> {
								try {
									writer.write(String.format("%s (Calls tracked: %d)\n", e.cap().getName(), e.count));
								} catch (IOException ioException) {
									LOGGER.error("Failed to write capability entry to report: {}", e.cap().getName(), ioException);
								}
							});

					LOGGER.info("capability_fix_revived: Report written to {}", reportFile.toAbsolutePath());

					try {
						Thread.sleep(500);
					} catch (InterruptedException ignored) {}

					source.sendSystemMessage(ServerCommands.getPrefix()
							.append(Component.literal("Done! Report file is located at: ").withStyle(ChatFormatting.GRAY))
							.append(Component.literal("config/" + MODID + "/reports/capability_report.txt").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)));

					if (isRecording) {
						isRecording = false;
						try {
							Thread.sleep(500);
						} catch (InterruptedException ignored) {}
						source.sendSystemMessage(ServerCommands.getStatusMessage(false));
					}
				}
			} catch (IOException e) {
				LOGGER.error("Failed to create directories or write capability_fix_revived report", e);
				try { Thread.sleep(500); } catch (InterruptedException ignored) {}
				source.sendSystemMessage(ServerCommands.getPrefix()
						.append(Component.literal("Failed to generate report!").withStyle(ChatFormatting.RED)));
			}
		});
	}
}