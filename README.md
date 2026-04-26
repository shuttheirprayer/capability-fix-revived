# Capability Fix

## How to use:
- **Start Recording:** Run /capabilityfix toggle_recording. You will see a [🔧] Capability Fix | recording has started message.



- **Play the Game:** Let the server run for a while (or perform laggy actions) so the mod can track which capabilities are being fetched the most.


- **Generate Report:** Run /capabilityfix generate_report.

*The mod might pause for a moment to finalize the data. It will automatically stop recording for you.*


- **Configure:** Open config/capabilityfix/reports/capability_report.txt.



- **Copy the capability IDs:** (e.g., com/hollingsworth/arsnouveau/api/mana/IManaCap) into your config/capabilityfix/capabilityfix-common.toml whitelist.



- **Apply:** Run /capabilityfix reload_config ingame to apply the changes without restarting.


### Credits:
- **Icy0x1** - Original Mod Author: https://www.curseforge.com/minecraft/mc-mods/capability-fix