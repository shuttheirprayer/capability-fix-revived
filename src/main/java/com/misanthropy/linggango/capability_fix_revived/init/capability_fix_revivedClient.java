package com.misanthropy.linggango.capability_fix_revived.init;

import com.misanthropy.linggango.capability_fix_revived.event.ClientCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class capability_fix_revivedClient {

    public static void onClientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ClientCommands.class);
    }
}