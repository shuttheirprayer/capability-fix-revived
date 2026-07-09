package com.misanthropy.linggango.capability_fix_revived.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class CFConfig {

	public static class Common {
		public final ForgeConfigSpec.BooleanValue cacheAll;
		public final ForgeConfigSpec.ConfigValue<List<String>> capabilityCachingWhitelist;

		Common(ForgeConfigSpec.Builder builder) {
			cacheAll = builder.comment("Cache all entity capabilities. Could cause problems. Use it with caution").define("cacheAll", false);
			capabilityCachingWhitelist = builder.define("capabilityCachingWhitelist", new ArrayList<>(List.of(
					"top/theillusivec4/curios/api/type/capability/ICuriosItemHandler",
					"net/v_black_cat/goetydelight/visual/EntityVisualEffects",
					"com/Polarice3/Goety/common/capabilities/soulenergy/ISoulEnergy",
					"org/thecelestialworkshop/celestisynth/common/capabilities/CSEntityCapability",
					"com/bobmowzie/mowziesmobs/server/capability/FrozenCapability$IFrozenCapability",
					"com/bobmowzie/mowziesmobs/server/capability/AbilityCapability$IAbilityCapability",
					"com/Polarice3/Goety/common/capabilities/lichdom/ILichdom",
					"com/Polarice3/Goety/common/capabilities/misc/IMisc"
			)));
		}
	}

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = common.getRight();
		COMMON = common.getLeft();
	}

	public static void init() {
		register();
	}

	private static void register() {
		String path = capability_fix_revived.MODID + "/" + capability_fix_revived.MODID + "-" + ModConfig.Type.COMMON.extension() + ".toml";
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CFConfig.COMMON_SPEC, path);
	}
}