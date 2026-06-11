package cn.kuzuanpa.organapi.common.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class OrganCapabilities {
    public static final Capability<IOrganHolder> ORGAN_HOLDER = CapabilityManager.get(new CapabilityToken<>() {
    });

    private OrganCapabilities() {
    }
}
