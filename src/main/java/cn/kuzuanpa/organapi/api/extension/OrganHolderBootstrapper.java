package cn.kuzuanpa.organapi.api.extension;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface OrganHolderBootstrapper {
    void bootstrap(@NotNull Entity entity, @NotNull IOrganHolder holder);
}
