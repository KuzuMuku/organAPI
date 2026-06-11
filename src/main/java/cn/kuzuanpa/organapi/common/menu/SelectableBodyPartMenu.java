package cn.kuzuanpa.organapi.common.menu;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public interface SelectableBodyPartMenu {
    List<ResourceLocation> getBodyPartIds();

    int getSelectedBodyPartIndex();

    void setSelectedBodyPartIndex(int index);
}
