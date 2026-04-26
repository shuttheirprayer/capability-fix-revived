# capability-fix-revived
This mod improves one key part of lags in 1.20.1 Forge: Capability fetching on entities. The implementation used on 1.20.1 searches across all capability providers to find one capability on one entity. This is extremely inefficient when there are many mods adding capability providers.
