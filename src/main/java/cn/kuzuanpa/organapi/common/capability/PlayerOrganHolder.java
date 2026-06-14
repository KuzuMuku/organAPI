package cn.kuzuanpa.organapi.common.capability;

import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import cn.kuzuanpa.organapi.api.organ.OrganDefinition;
import cn.kuzuanpa.organapi.common.body.BodyPlanResolver;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerOrganHolder implements IOrganHolder, INBTSerializable<CompoundTag> {
    private final Entity owner;
    private final Map<ResourceLocation, BodyPartState> bodyParts = new HashMap<>();
    private Runnable dirtyListener;
    private boolean dirty;

    public PlayerOrganHolder(Entity owner) {
        this.owner = owner;
    }

    public void setDirtyListener(Runnable dirtyListener) {
        this.dirtyListener = dirtyListener;
    }

    @Override
    public int getCapacity(ResourceLocation bodyPartId) {
        Optional<BodyPartDefinition> definition = BodyPlanResolver.getBodyPart(owner, bodyPartId);
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
        ensureSlotCount(bodyPartId);
        BodyPartState state = getState(bodyPartId);
        for (int i = 0; i < state.organs.size(); i++) {
            if (state.organs.get(i).isEmpty()) {
                return trySetOrgan(bodyPartId, i, stack, true);
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
        trySetOrgan(bodyPartId, slotIndex, stack, false);
    }

    @Override
    public OrganInstallResult trySetOrgan(ResourceLocation bodyPartId, int slotIndex, ItemStack stack) {
        return trySetOrgan(bodyPartId, slotIndex, stack, false);
    }

    @Override
    public int getBonusCapacity(ResourceLocation bodyPartId) {
        return getState(bodyPartId).bonusCapacity;
    }

    @Override
    public boolean addBonusCapacity(ResourceLocation bodyPartId, int amount) {
        if (BodyPlanResolver.getBodyPart(owner, bodyPartId).isEmpty()) {
            return false;
        }
        BodyPartState state = getState(bodyPartId);
        state.bonusCapacity += amount;
        ensureSlotCount(bodyPartId);
        markDirty();
        return true;
    }

    @Override
    public void copyFrom(IOrganHolder other) {
        bodyParts.clear();
        for (ResourceLocation id : BodyPlanResolver.getOrderedBodyPartIds(owner)) {
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
        if (dirtyListener != null) {
            dirtyListener.run();
        }
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
            ResourceLocation id = ResourceLocation.parse(key);
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

    private OrganInstallResult trySetOrgan(ResourceLocation bodyPartId, int slotIndex, ItemStack stack, boolean consumeStack) {
        Optional<BodyPartDefinition> bodyPart = BodyPlanResolver.getBodyPart(owner, bodyPartId);
        if (bodyPart.isEmpty()) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.unknown_body_part"));
        }
        ensureSlotCount(bodyPartId);
        BodyPartState state = getState(bodyPartId);
        if (slotIndex < 0 || slotIndex >= state.organs.size()) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.invalid_slot"));
        }
        if (stack.isEmpty()) {
            ItemStack removed = state.organs.get(slotIndex);
            state.organs.set(slotIndex, ItemStack.EMPTY);
            if (!removed.isEmpty()) {
                markDirty();
            }
            return OrganInstallResult.success(Component.translatable("message.organapi.removed"));
        }
        Optional<OrganDefinition> organ = OrganRegistryAccess.getOrgan(stack);
        if (!bodyPart.get().accepts(stack)) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.invalid_body_part"));
        }
        if (organ.isPresent() && !organ.get().supports(bodyPartId)) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.invalid_body_part"));
        }
        ItemStack previous = state.organs.get(slotIndex);
        int usedWithoutPrevious = getUsedCapacity(bodyPartId) - getSize(previous);
        int stackSize = organ.map(OrganDefinition::size).orElseGet(() -> getSize(stack));
        if (usedWithoutPrevious + stackSize > getCapacity(bodyPartId)) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.not_enough_capacity"));
        }
        state.organs.set(slotIndex, stack.copyWithCount(1));
        if (consumeStack) {
            stack.shrink(1);
        }
        markDirty();
        return OrganInstallResult.success(Component.translatable(previous.isEmpty() ? "message.organapi.installed" : "message.organapi.replaced"));
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
