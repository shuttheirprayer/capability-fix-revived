package com.misanthropy.linggango.capability_fix_revived.content;

import net.minecraftforge.common.capabilities.Capability;

public final class CapabilityTypeCache {

	private final Capability<?> cap;
	private final boolean cached;
	private final boolean cachedAbsent;
	private final int version;
	public int count = 1;

	public CapabilityTypeCache(Capability<?> cap, boolean cached, boolean cachedAbsent, int version) {
		this.cap = cap;
		this.cached = cached;
		this.cachedAbsent = cachedAbsent;
		this.version = version;
	}

	public Capability<?> cap() {
		return cap;
	}

	public boolean cached() {
		return cached;
	}

	public boolean cachedAbsent() {
		return cachedAbsent;
	}

	public int version() {
		return version;
	}
}
