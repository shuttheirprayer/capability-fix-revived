package com.misanthropy.linggango.capability_fix_revived.mixin;

import com.google.common.collect.Maps;
import com.misanthropy.linggango.capability_fix_revived.init.capability_fix_revived;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> {

	protected EntityMixin(Class<Entity> baseClass) {
		super(baseClass);
	}

	@Unique
	private int capability_fix_revived$cacheVersion;
	@Unique
	private Map<Capability<?>, LazyOptional<?>> capability_fix_revived$cache;

	@Override
	public void invalidateCaps() {
		if (capability_fix_revived$cache != null) capability_fix_revived$cache.clear();
		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		if (capability_fix_revived$cache != null) capability_fix_revived$cache.clear();
		super.reviveCaps();
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (side != null || !capability_fix_revived.isValidForCaching(cap)) return super.getCapability(cap, side);
		Map<Capability<?>, LazyOptional<?>> cache = capability_fix_revived$cache;
		if (cache != null) {
			if (capability_fix_revived$cacheVersion != capability_fix_revived.VERSION) {
				capability_fix_revived$cacheVersion = capability_fix_revived.VERSION;
				cache.clear();
			} else {
				LazyOptional<?> lookup = cache.get(cap);
				if (lookup != null) return lookup.cast();
			}
		}
		LazyOptional<T> ans = super.getCapability(cap, null);
		if (ans.isPresent()) {
			if (cache == null) {
				capability_fix_revived$cache = cache = Maps.newIdentityHashMap();
				capability_fix_revived$cacheVersion = capability_fix_revived.VERSION;
			}
			ans.addListener(opt -> capability_fix_revived$cache.remove(cap, opt));
			cache.put(cap, ans);
		}
		return ans;
	}
}
