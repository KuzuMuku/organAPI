package cn.kuzuanpa.organapi.api;

import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import cn.kuzuanpa.organapi.api.organ.OrganDefinition;
import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class OrganApi {
    private OrganApi() {
    }

    public static Collection<BodyPartDefinition> getBodyParts() {
        return OrganRegistryAccess.getBodyParts();
    }

    public static Collection<OrganDefinition> getOrgans() {
        return OrganRegistryAccess.getOrgans();
    }

    public static Optional<BodyPartDefinition> getBodyPart(ResourceLocation id) {
        return OrganRegistryAccess.getBodyPart(id);
    }

    public static Optional<OrganDefinition> getOrgan(ResourceLocation id) {
        return OrganRegistryAccess.getOrgan(id);
    }

    public static Optional<OrganDefinition> getOrgan(ItemStack stack) {
        return OrganRegistryAccess.getOrgan(stack);
    }

    public static OrganInstallResult install(Player player, ResourceLocation bodyPartId, ItemStack stack) {
        return IOrganHolder.get(player)
                .map(holder -> holder.install(bodyPartId, stack))
                .orElseGet(() -> OrganInstallResult.fail(Component.translatable("message.organapi.no_holder")));
    }

    public static boolean addCapacity(Player player, ResourceLocation bodyPartId, int amount) {
        return IOrganHolder.get(player).map(holder -> {
            holder.addBonusCapacity(bodyPartId, amount);
            return true;
        }).orElse(false);
    }
}
