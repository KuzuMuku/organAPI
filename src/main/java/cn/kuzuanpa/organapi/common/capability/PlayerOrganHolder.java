/*
 * This class was created by <kuzuanpa>. It is distributed as
 * part of the organAPI Mod. Get the Source Code in github:
 * https://github.com/KuzuMuku/organAPI
 *
 * organAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * organAPI is Open Source and distributed under the
 * AGPLv3 License: https://www.gnu.org/licenses/agpl-3.0.txt
 *
 */

package cn.kuzuanpa.organapi.common.capability;

import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import cn.kuzuanpa.organapi.common.body.BodyPlanResolver;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import cn.kuzuanpa.organapi.common.data.OrganItemMatcher;
import cn.kuzuanpa.organapi.core.model.BodyPartDefinition;
import cn.kuzuanpa.organapi.core.model.OrganDefinition;
import cn.kuzuanpa.organapi.core.state.OrganStateData;
import cn.kuzuanpa.organapi.platform.OrganIds;
import cn.kuzuanpa.organapi.platform.Platform;
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
import org.jetbrains.annotations.NotNull;

/**
 * Common organ-holder logic. It is loader-neutral: item persistence goes through
 * the {@link Platform#itemCodec()} seam (so the 1.20.1 {@code ItemStack}/NBT and
 * the 1.21.1 {@code ItemStack}/DataComponent encodings stay behind platform-api),
 * and the on-disk {@link CompoundTag} shape stores each organ as opaque bytes.
 *
 * <p>{@link #serializeNBT()}/{@link #deserializeNBT(CompoundTag)} back the Forge
 * JSON-sidecar persistence; {@link #toStateData()}/{@link #loadStateData} back the
 * loader-independent {@code OrganStateData} sync/attachment contract. Both funnel
 * item bytes through the same codec.
 */
public class PlayerOrganHolder implements IOrganHolder {
    private static final String ORGAN_SLOT_TAG = "slot";
    private final Entity owner;
    private final Map<ResourceLocation, BodyPartState> bodyParts = new HashMap<>();
    private Runnable dirtyListener;
    private boolean dirty;
    private boolean bootstrapInitialized;

    public PlayerOrganHolder(Entity owner) {
        this.owner = owner;
    }

    public void setDirtyListener(Runnable dirtyListener) {
        this.dirtyListener = dirtyListener;
    }

    @Override
    public int getCapacity(@NotNull ResourceLocation bodyPartId) {
        Optional<BodyPartDefinition> definition = BodyPlanResolver.getBodyPart(owner, bodyPartId);
        int capacity = Math.max(0, definition.map(BodyPartDefinition::defaultCapacity).orElse(0) + getState(bodyPartId).bonusCapacity);
        Integer maxCapacity = definition.map(BodyPartDefinition::maxCapacity).orElse(null);
        return maxCapacity == null ? capacity : Math.min(capacity, maxCapacity);
    }

    @Override
    public int getUsedCapacity(@NotNull ResourceLocation bodyPartId) {
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
    public int getFreeCapacity(@NotNull ResourceLocation bodyPartId) {
        return Math.max(0, getCapacity(bodyPartId) - getUsedCapacity(bodyPartId));
    }

    @Override
    public @NotNull List<ItemStack> getInstalledOrgans(@NotNull ResourceLocation bodyPartId) {
        ensureSlotCount(bodyPartId);
        return List.copyOf(getState(bodyPartId).organs);
    }

    @Override
    public @NotNull OrganInstallResult install(@NotNull ResourceLocation bodyPartId, @NotNull ItemStack stack) {
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
    public @NotNull ItemStack removeOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex) {
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
    public @NotNull ItemStack getOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex) {
        ensureSlotCount(bodyPartId);
        BodyPartState state = getState(bodyPartId);
        if (slotIndex < 0 || slotIndex >= state.organs.size()) {
            return ItemStack.EMPTY;
        }
        return state.organs.get(slotIndex);
    }

    @Override
    public void setOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex, @NotNull ItemStack stack) {
        trySetOrgan(bodyPartId, slotIndex, stack, false);
    }

    @Override
    public @NotNull OrganInstallResult trySetOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex, @NotNull ItemStack stack) {
        return trySetOrgan(bodyPartId, slotIndex, stack, false);
    }

    @Override
    public int getBonusCapacity(@NotNull ResourceLocation bodyPartId) {
        return getState(bodyPartId).bonusCapacity;
    }

    @Override
    public boolean addBonusCapacity(@NotNull ResourceLocation bodyPartId, int amount) {
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
    public void copyFrom(@NotNull IOrganHolder other) {
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
    public boolean isBootstrapInitialized() {
        return bootstrapInitialized;
    }

    @Override
    public void setBootstrapInitialized(boolean initialized) {
        bootstrapInitialized = initialized;
    }

    /** Serializes to the on-disk {@link CompoundTag} shape (Forge JSON sidecar). */
    public CompoundTag serializeNBT() {
        CompoundTag root = new CompoundTag();
        root.putBoolean(BOOTSTRAP_INITIALIZED_TAG, bootstrapInitialized);
        CompoundTag bodyPartsTag = new CompoundTag();
        for (Map.Entry<ResourceLocation, BodyPartState> entry : bodyParts.entrySet()) {
            BodyPartState state = entry.getValue();
            CompoundTag partTag = new CompoundTag();
            partTag.putInt("bonus_capacity", state.bonusCapacity);
            ListTag organsTag = new ListTag();
            int capacity = getCapacity(entry.getKey());
            for (int slot = 0; slot < capacity && slot < state.organs.size(); slot++) {
                ItemStack stack = state.organs.get(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                CompoundTag organTag = new CompoundTag();
                organTag.putInt(ORGAN_SLOT_TAG, slot);
                // Same on-disk inline item shape on both loaders; only the extra
                // HolderLookup.Provider parameter differs on 1.21.1 (minor sig diff).
                //? if forge {
                stack.save(organTag);
                //?} else {
                /*organTag.merge((CompoundTag) stack.save(owner.level().registryAccess(), new CompoundTag()));
                *///?}
                organsTag.add(organTag);
            }
            partTag.put("organs", organsTag);
            bodyPartsTag.put(entry.getKey().toString(), partTag);
        }
        root.put("body_parts", bodyPartsTag);
        return root;
    }

    /** Deserializes from the on-disk {@link CompoundTag} shape (Forge JSON sidecar). */
    public void deserializeNBT(CompoundTag nbt) {
        bodyParts.clear();
        bootstrapInitialized = nbt.getBoolean(BOOTSTRAP_INITIALIZED_TAG);
        CompoundTag bodyPartsTag = nbt.getCompound("body_parts");
        for (String key : bodyPartsTag.getAllKeys()) {
            ResourceLocation id = ResourceLocation.parse(key);
            CompoundTag partTag = bodyPartsTag.getCompound(key);
            BodyPartState state = getState(id);
            state.bonusCapacity = partTag.getInt("bonus_capacity");
            ListTag organsTag = partTag.getList("organs", Tag.TAG_COMPOUND);
            state.organs.clear();
            int capacity = getCapacity(id);
            int legacySlot = 0;
            for (int i = 0; i < organsTag.size(); i++) {
                CompoundTag organTag = organsTag.getCompound(i);
                int slot = organTag.contains(ORGAN_SLOT_TAG) ? organTag.getInt(ORGAN_SLOT_TAG) : legacySlot++;
                if (slot < 0 || slot >= capacity) {
                    continue;
                }
                //? if forge {
                ItemStack stack = ItemStack.of(organTag);
                //?} else {
                /*ItemStack stack = ItemStack.parseOptional(owner.level().registryAccess(), organTag);
                *///?}
                if (!stack.isEmpty()) {
                    while (state.organs.size() <= slot) {
                        state.organs.add(ItemStack.EMPTY);
                    }
                    state.organs.set(slot, stack);
                }
            }
            ensureSlotCount(id);
        }
        dirty = false;
    }

    /**
     * Snapshots the holder into the loader-independent {@link OrganStateData}
     * DTO. Mirrors {@link #serializeNBT()} (only occupied slots within capacity),
     * but carries item data as opaque bytes so the snapshot never depends on
     * {@code CompoundTag}. Used by the {@code OrganStorage}/network contract.
     */
    public OrganStateData toStateData() {
        OrganStateData data = new OrganStateData();
        data.setBootstrapInitialized(bootstrapInitialized);
        for (Map.Entry<ResourceLocation, BodyPartState> entry : bodyParts.entrySet()) {
            BodyPartState state = entry.getValue();
            OrganStateData.PartState partState = data.addPart(OrganIds.toOrganId(entry.getKey()), state.bonusCapacity);
            int capacity = getCapacity(entry.getKey());
            for (int slot = 0; slot < capacity && slot < state.organs.size(); slot++) {
                ItemStack stack = state.organs.get(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                partState.addSlot(slot, Platform.itemCodec().encode(owner, stack));
            }
        }
        return data;
    }

    /** Applies an {@link OrganStateData} snapshot; mirror of {@link #deserializeNBT(CompoundTag)}. */
    public void loadStateData(OrganStateData data) {
        bodyParts.clear();
        bootstrapInitialized = data.isBootstrapInitialized();
        for (OrganStateData.PartState partState : data.parts()) {
            ResourceLocation id = OrganIds.toResourceLocation(partState.bodyPartId());
            BodyPartState state = getState(id);
            state.bonusCapacity = partState.bonusCapacity();
            state.organs.clear();
            int capacity = getCapacity(id);
            for (OrganStateData.SlotData slotData : partState.slots()) {
                int slot = slotData.slot();
                if (slot < 0 || slot >= capacity) {
                    continue;
                }
                ItemStack stack = Platform.itemCodec().decode(owner, slotData.item());
                if (!stack.isEmpty()) {
                    while (state.organs.size() <= slot) {
                        state.organs.add(ItemStack.EMPTY);
                    }
                    state.organs.set(slot, stack);
                }
            }
            ensureSlotCount(id);
        }
        dirty = false;
    }

    private @NotNull OrganInstallResult trySetOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex, @NotNull ItemStack stack, boolean consumeStack) {
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
        if (!OrganItemMatcher.accepts(bodyPart.get(), stack)) {
            return OrganInstallResult.fail(Component.translatable("message.organapi.invalid_body_part"));
        }
        if (organ.isPresent() && !organ.get().supports(OrganIds.toOrganId(bodyPartId))) {
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

    private BodyPartState getState(@NotNull ResourceLocation bodyPartId) {
        return bodyParts.computeIfAbsent(bodyPartId, key -> new BodyPartState());
    }

    private void ensureSlotCount(@NotNull ResourceLocation bodyPartId) {
        BodyPartState state = getState(bodyPartId);
        int target = Math.max(0, getCapacity(bodyPartId));
        boolean changed = false;
        while (state.organs.size() > target) {
            state.organs.remove(state.organs.size() - 1);
            changed = true;
        }
        while (state.organs.size() < target) {
            state.organs.add(ItemStack.EMPTY);
        }
        if (changed) {
            markDirty();
        }
    }

    private int getSize(@NotNull ItemStack stack) {
        return OrganRegistryAccess.getOrgan(stack).map(OrganDefinition::size).orElse(1);
    }

    private static class BodyPartState {
        private int bonusCapacity;
        private final List<ItemStack> organs = new ArrayList<>();
    }
}
