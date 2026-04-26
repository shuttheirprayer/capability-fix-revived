package com.misanthropy.linggango.capability_fix_revived.event;

import com.misanthropy.linggango.capability_fix_revived.init.CFConfig;
import com.misanthropy.linggango.capability_fix_revived.init.capability_fix_revived;
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

@Mod.EventBusSubscriber(modid = capability_fix_revived.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerCommands {

	private static final String ICON = "🔧";

	@SubscribeEvent
	public static void serverCommandRegister(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("capability_fix_revived");

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
		capability_fix_revived.isRecording = !capability_fix_revived.isRecording;
		ctx.getSource().sendSystemMessage(getStatusMessage(capability_fix_revived.isRecording));
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

		capability_fix_revived.writeReport(ctx.getSource());
		return 1;
	}

	private static int reload(CommandContext<CommandSourceStack> ctx) {
		capability_fix_revived.CACHE.clear();
		capability_fix_revived.SET = null;
		capability_fix_revived.VERSION++;

		MutableComponent message = getPrefix()
				.append(Component.literal("Cleared the cache and config.").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

		ctx.getSource().sendSystemMessage(message);
		return 1;
	}
}