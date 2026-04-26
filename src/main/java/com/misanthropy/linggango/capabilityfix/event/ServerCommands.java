package com.misanthropy.linggango.capabilityfix.event;

import com.misanthropy.linggango.capabilityfix.init.CFConfig;
import com.misanthropy.linggango.capabilityfix.init.CapabilityFix;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CapabilityFix.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerCommands {

	private static final String ICON = "🔧";

	@SubscribeEvent
	public static void serverCommandRegister(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("capabilityfix");

		base.then(Commands.literal("reload_config").executes(ServerCommands::reload))
				.then(Commands.literal("toggle_recording").executes(ServerCommands::toggleRecording))
				.then(Commands.literal("generate_report").executes(ServerCommands::gather));

		event.getDispatcher().register(base);
	}

	public static MutableComponent getPrefix() {
		return Component.literal("[").withStyle(ChatFormatting.GOLD)
				.append(Component.literal(ICON).withStyle(ChatFormatting.GOLD))
				.append(Component.literal("] ").withStyle(ChatFormatting.GOLD))
				.append(Component.literal("Capability Fix | ").withStyle(ChatFormatting.GOLD));
	}

	public static MutableComponent getStatusMessage(boolean active) {
		MutableComponent state = active ?
				Component.literal("started").withStyle(ChatFormatting.GRAY) :
				Component.literal("stopped").withStyle(ChatFormatting.GRAY);

		return getPrefix()
				.append(Component.literal("recording has ").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC))
				.append(state);
	}

	private static int toggleRecording(CommandContext<CommandSourceStack> ctx) {
		CapabilityFix.isRecording = !CapabilityFix.isRecording;
		ctx.getSource().sendSystemMessage(getStatusMessage(CapabilityFix.isRecording));
		return 1;
	}

	private static int gather(CommandContext<CommandSourceStack> ctx) {
		if (CFConfig.COMMON.cacheAll.get()) {
			ctx.getSource().sendSystemMessage(
					getPrefix()
							.append(Component.literal("Cache all is enabled. Report would be empty.").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC))
			);
			return 0;
		}

		ctx.getSource().sendSystemMessage(
				getPrefix().append(Component.literal("Generating report...").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC))
		);

		CapabilityFix.writeReport(ctx.getSource());
		return 1;
	}

	private static int reload(CommandContext<CommandSourceStack> ctx) {
		CapabilityFix.CACHE.clear();
		CapabilityFix.SET = null;
		CapabilityFix.VERSION++;

		MutableComponent message = getPrefix()
				.append(Component.literal("Cleared the cache and config.").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

		ctx.getSource().sendSystemMessage(message);
		return 1;
	}
}