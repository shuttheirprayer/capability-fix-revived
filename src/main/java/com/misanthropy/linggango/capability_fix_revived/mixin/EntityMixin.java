package com.misanthropy.linggango.capability_fix_revived.mixin;

import com.google.common.collect.Maps;
import com.misanthropy.linggango.capability_fix_revived.content.CapabilityCache;
import com.misanthropy.linggango.capability_fix_revived.init.capability_fix_revived;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> {

	@Shadow
	public int tickCount;

	protected EntityMixin(Class<Entity> baseClass) {
		super(baseClass);
	}

	@Unique
	private int capability_fix_revived$cacheVersion = capability_fix_revived.VERSION;
	@Unique
	private final Map<Capability<?>, CapabilityCache<?>> capability_fix_revived$cache = Maps.newIdentityHashMap();

	@Override
	public void invalidateCaps() {
		capability_fix_revived$cache.clear();
		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		capability_fix_revived$cache.clear();
		super.reviveCaps();
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (side == null && capability_fix_revived.isValidForCaching(cap)) {
			if (capability_fix_revived$cacheVersion != capability_fix_revived.VERSION) {
				capability_fix_revived$cacheVersion = capability_fix_revived.VERSION;
				capability_fix_revived$cache.clear();
			}
			var lookup = capability_fix_revived$cache.get(cap);
			if (lookup != null) {
				return lookup.data().cast();
			}
			var ans = super.getCapability(cap, null);
			capability_fix_revived$cache.put(cap, new CapabilityCache<>(cap, ans, tickCount));
			return ans;
		}
		return super.getCapability(cap, side);
	}
}
