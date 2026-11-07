package com.misanthropy.linggango.capability_fix_revived.event;

import com.misanthropy.linggango.capability_fix_revived.init.CFConfig;
import com.misanthropy.linggango.capability_fix_revived.init.capability_fix_revived;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerCommands {

	@SubscribeEvent
	public static void serverCommandRegister(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("capability_fix_revived")
				.then(Commands.literal("reload_config").executes(ServerCommands::reload))
				.then(Commands.literal("toggle_recording").executes(ServerCommands::toggleRecording))
				.then(Commands.literal("generate_report").executes(ServerCommands::gather)));
	}

	public static MutableComponent getPrefix() {
		return Component.literal("[🔧] Capability Fix | ").withStyle(ChatFormatting.GOLD);
	}

	private static MutableComponent info(String text) {
		return getPrefix().append(Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}

	public static MutableComponent getStatusMessage(boolean active) {
		return info("recording has ").append(Component.literal(active ? "started" : "stopped").withStyle(ChatFormatting.GRAY));
	}

	private static int toggleRecording(CommandContext<CommandSourceStack> ctx) {
		capability_fix_revived.isRecording = !capability_fix_revived.isRecording;
		ctx.getSource().sendSystemMessage(getStatusMessage(capability_fix_revived.isRecording));
		return 1;
	}

	private static int gather(CommandContext<CommandSourceStack> ctx) {
		if (CFConfig.COMMON.cacheAll.get()) {
			ctx.getSource().sendSystemMessage(info("Cache all is enabled. Report would be empty."));
			return 0;
		}
		ctx.getSource().sendSystemMessage(info("Generating report..."));
		capability_fix_revived.writeReport(ctx.getSource());
		return 1;
	}

	private static int reload(CommandContext<CommandSourceStack> ctx) {
		capability_fix_revived.CACHE.clear();
		capability_fix_revived.SET = null;
		capability_fix_revived.CACHE_ALL = CFConfig.COMMON.cacheAll.get();
		capability_fix_revived.VERSION++;
		ctx.getSource().sendSystemMessage(info("Cleared the cache and config."));
		return 1;
	}
}
