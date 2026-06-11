package cn.kuzuanpa.organapi.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class OrganItem extends Item {
    private final ResourceLocation definitionId;

    public OrganItem(Properties properties, ResourceLocation definitionId) {
        super(properties);
        this.definitionId = definitionId;
    }

    public ResourceLocation getDefinitionId(ItemStack stack) {
        return definitionId;
    }
}
