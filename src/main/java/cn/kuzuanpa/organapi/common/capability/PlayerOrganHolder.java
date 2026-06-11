package cn.kuzuanpa.organapi.common.capability;

import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import cn.kuzuanpa.organapi.api.organ.OrganDefinition;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerOrganHolder implements IOrganHolder, INBTSerializable<CompoundTag> {
    private final Map<ResourceLocation, BodyPartState> bodyParts = new HashMap<>();
    private boolean dirty;

    @Override
    public int getCapacity(ResourceLocation bodyPartId) {
        Optional<BodyPartDefinition> definition = OrganRegistryAccess.getBodyPart(bodyPartId);
        int capacity = Math.max(0, definition.map(BodyPartDefinition::defaultCapacity).orElse(0) + getState(bodyPartId).bonusCapacity);
        Integer maxCapacity = definition.map(BodyPartDefinition::maxCapacity).orElse(null);
        return maxCapacity == null ? capacity : Math.min(capacity, maxCapacity);
    }

    @Override
    public int getUsedCapacity(ResourceLocation bodyPartId) {
        BodyPartState state = getState(bodyPartId);
        int used = 0;
        for (ItemStack stack : state.organs) {
            if (!stack.isEmpty()) {
                used += getSize(stack);
            }
        }
        return used;
    }

    @Override
    public int getFreeCapacity(ResourceLocation bodyPartId) {
        return Math.max(0, getCapacity(bodyPartId) - getUsedCapacity(bodyPartId));
    }

    @Override
    public List<ItemStack> getInstalledOrgans(ResourceLocation bodyPartId) {
        ensureSlotCount(bodyPartId);
        return List.copyOf(getState(bodyPartId).organs);
    }

    @Override
    public OrganInstallResult install(ResourceLocation bodyPartId, ItemStack stack) {
        if (stack.isEmpty()) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.empty_organ"));
        }
        Optional<OrganDefinition> organ = OrganRegistryAccess.getOrgan(stack);
        if (organ.isEmpty()) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.invalid_organ"));
        }
        if (!organ.get().supports(bodyPartId)) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.invalid_body_part"));
        }
        if (getFreeCapacity(bodyPartId) < organ.get().size()) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.not_enough_capacity"));
        }
        BodyPartState state = getState(bodyPartId);
        ensureSlotCount(bodyPartId);
        for (int i = 0; i < state.organs.size(); i++) {
            if (state.organs.get(i).isEmpty()) {
                ItemStack inserted = stack.copyWithCount(1);
                state.organs.set(i, inserted);
                stack.shrink(1);
                markDirty();
                return OrganInstallResult.success(Component.translatable("message.organapi.installed"));
            }
        }
        return OrganInstallResult.fail(Component.translatable("message.organapi.no_empty_slot"));
    }

    @Override
    public ItemStack removeOrgan(ResourceLocation bodyPartId, int slotIndex) {
        ensureSlotCount(bodyPartId);
        BodyPartState state = getState(bodyPartId);
        if (slotIndex < 0 || slotIndex >= state.organs.size()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = state.organs.get(slotIndex);
        state.organs.set(slotIndex, ItemStack.EMPTY);
        if (!removed.isEmpty()) {
            markDirty();
        }
        return removed;
    }

    @Override
    public ItemStack getOrgan(ResourceLocation bodyPartId, int slotIndex) {
        ensureSlotCount(bodyPartId);
        BodyPartState state = getState(bodyPartId);
        if (slotIndex < 0 || slotIndex >= state.organs.size()) {
            return ItemStack.EMPTY;
        }
        return state.organs.get(slotIndex);
    }

    @Override
    public void setOrgan(ResourceLocation bodyPartId, int slotIndex, ItemStack stack) {
        ensureSlotCount(bodyPartId);
        BodyPartState state = getState(bodyPartId);
        if (slotIndex < 0 || slotIndex >= state.organs.size()) {
            return;
        }
        ItemStack stored = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        state.organs.set(slotIndex, stored);
        markDirty();
    }

    @Override
    public int getBonusCapacity(ResourceLocation bodyPartId) {
        return getState(bodyPartId).bonusCapacity;
    }

    @Override
    public boolean addBonusCapacity(ResourceLocation bodyPartId, int amount) {
        BodyPartState state = getState(bodyPartId);
        state.bonusCapacity += amount;
        ensureSlotCount(bodyPartId);
        markDirty();
        return true;
    }

    @Override
    public void copyFrom(IOrganHolder other) {
        bodyParts.clear();
        for (BodyPartDefinition definition : OrganRegistryAccess.getBodyParts()) {
            ResourceLocation id = definition.id();
            BodyPartState state = getState(id);
            state.bonusCapacity = other.getBonusCapacity(id);
            List<ItemStack> copied = other.getInstalledOrgans(id);
            state.organs.clear();
            for (ItemStack stack : copied) {
                state.organs.add(stack.copy());
            }
            ensureSlotCount(id);
        }
        markDirty();
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void clearDirty() {
        dirty = false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag root = new CompoundTag();
        CompoundTag bodyPartsTag = new CompoundTag();
        for (Map.Entry<ResourceLocation, BodyPartState> entry : bodyParts.entrySet()) {
            CompoundTag partTag = new CompoundTag();
            partTag.putInt("bonus_capacity", entry.getValue().bonusCapacity);
            ListTag organsTag = new ListTag();
            for (ItemStack stack : entry.getValue().organs) {
                CompoundTag organTag = new CompoundTag();
                stack.save(organTag);
                organsTag.add(organTag);
            }
            partTag.put("organs", organsTag);
            bodyPartsTag.put(entry.getKey().toString(), partTag);
        }
        root.put("body_parts", bodyPartsTag);
        return root;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        bodyParts.clear();
        CompoundTag bodyPartsTag = nbt.getCompound("body_parts");
        for (String key : bodyPartsTag.getAllKeys()) {
            ResourceLocation id = new ResourceLocation(key);
            CompoundTag partTag = bodyPartsTag.getCompound(key);
            BodyPartState state = getState(id);
            state.bonusCapacity = partTag.getInt("bonus_capacity");
            ListTag organsTag = partTag.getList("organs", Tag.TAG_COMPOUND);
            state.organs.clear();
            for (int i = 0; i < organsTag.size(); i++) {
                state.organs.add(ItemStack.of(organsTag.getCompound(i)));
            }
            ensureSlotCount(id);
        }
        dirty = false;
    }

    private BodyPartState getState(ResourceLocation bodyPartId) {
        return bodyParts.computeIfAbsent(bodyPartId, key -> new BodyPartState());
    }

    private void ensureSlotCount(ResourceLocation bodyPartId) {
        BodyPartState state = getState(bodyPartId);
        int target = Math.max(getCapacity(bodyPartId), state.organs.size());
        while (state.organs.size() < target) {
            state.organs.add(ItemStack.EMPTY);
        }
    }

    private int getSize(ItemStack stack) {
        return OrganRegistryAccess.getOrgan(stack).map(OrganDefinition::size).orElse(1);
    }

    private static class BodyPartState {
        private int bonusCapacity;
        private final List<ItemStack> organs = new ArrayList<>();
    }
}
