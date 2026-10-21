package com.misanthropy.linggango.capability_fix_revived.mixin;

import com.misanthropy.linggango.capability_fix_revived.content.CapabilityCacheHolder;
import com.misanthropy.linggango.capability_fix_revived.content.CapabilityTypeCache;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Capability.class, remap = false)
public abstract class CapabilityMixin implements CapabilityCacheHolder {

	@Unique
	private CapabilityTypeCache capability_fix_revived$entry;

	@Override
	public CapabilityTypeCache capability_fix_revived$entry() {
		return capability_fix_revived$entry;
	}

	@Override
	public void capability_fix_revived$setEntry(CapabilityTypeCache entry) {
		capability_fix_revived$entry = entry;
	}
}
