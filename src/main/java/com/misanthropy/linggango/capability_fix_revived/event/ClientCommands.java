package com.misanthropy.linggango.capability_fix_revived.event;


import com.misanthropy.linggango.capability_fix_revived.init.capability_fix_revived;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = capability_fix_revived.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientCommands {

	@SubscribeEvent
	public static void onClientCommandRegister(RegisterClientCommandsEvent event){

	}

}
