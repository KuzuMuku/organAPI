---
name: organ-control-api-guide
description: Explain how to query and mutate organ state on entities through OrganAPI, including body plans, target-aware menu semantics, and sync behavior.
---

# Organ control API guide

Use this when you need to understand or explain the Java API for reading and mutating organ state on entities.

This skill is a high-density index. For the full example guide, read:

- `docs/organ-control-api-guide.md`

## Read first

Canonical API files:

- `src/main/java/cn/kuzuanpa/organapi/api/OrganApi.java`
- `src/main/java/cn/kuzuanpa/organapi/api/query/OrganQueryService.java`
- `src/main/java/cn/kuzuanpa/organapi/api/query/OrganPosition.java`
- `src/main/java/cn/kuzuanpa/organapi/api/query/BodyPartOverview.java`
- `src/main/java/cn/kuzuanpa/organapi/api/install/OrganInstallResult.java`

Canonical anatomy files:

- `src/main/java/cn/kuzuanpa/organapi/api/body/BodyPlanDefinition.java`
- `src/main/java/cn/kuzuanpa/organapi/api/body/ResolvedBodyPlan.java`
- `src/main/java/cn/kuzuanpa/organapi/common/body/BodyPlanResolver.java`

Canonical behavior files:

- `src/main/java/cn/kuzuanpa/organapi/common/capability/IOrganHolder.java`
- `src/main/java/cn/kuzuanpa/organapi/common/capability/PlayerOrganHolder.java`
- `src/main/java/cn/kuzuanpa/organapi/common/inventory/OrganPartContainer.java`
- `src/main/java/cn/kuzuanpa/organapi/common/menu/OrganOverviewMenu.java`
- `src/main/java/cn/kuzuanpa/organapi/common/network/SyncOrganDataS2CPacket.java`

## API surface

`OrganApi` is the public mutation/anatomy facade:

- template queries: `getBodyParts()`, `getBodyPart(id)`
- entity-aware anatomy queries: `getBodyParts(entity)`, `getBodyPart(entity, id)`, `getBodyPlan(entity)`
- organ queries: `getOrgans()`, `getOrgan(id)`, `getOrgan(stack)`
- mutations:
  - `install(Entity, bodyPartId, stack)`
  - `setOrgan(Entity, bodyPartId, slotIndex, stack)`
  - `removeOrgan(Entity, bodyPartId, slotIndex)`
  - `getOrgan(Entity, bodyPartId, slotIndex)`
  - `addCapacity(Entity, bodyPartId, amount)`

`OrganQueryService` is the public query facade:

- capacity queries
- installed-organ list queries
- precise slot queries
- `OrganPosition` queries
- body-part overview queries

## Body plan semantics

- `body_parts` are reusable templates.
- `body_plans` resolve actual anatomy per entity type.
- `getBodyParts(entity)` and `getInstalledOrganPositions(entity)` are scoped to the target entity’s current resolved body plan.
- A body part may exist in the global template registry but still not be available for a specific target entity.

## Query patterns

Use:

- `getInstalledOrgans(entity, bodyPartId)` when you need the full slot list, including empty slots
- `getInstalledOrganPositions(entity)` when you only care about occupied slots and their logical positions
- `getOrgan(entity, bodyPartId, slotIndex)` for a precise lookup
- `OrganApi.getBodyPlan(entity)` when the target’s available parts/order matter to your logic

Position semantics are:

- `bodyPartId + slotIndex`

## Mutation patterns

Use:

- `install(...)` to place an organ into the first empty valid slot
- `setOrgan(...)` to overwrite a specific slot with full safety checks
- `removeOrgan(...)` to clear a specific slot without compacting later slots
- `addCapacity(...)` to raise bonus capacity for a body part

All of these are target-entity operations. The caller/player is not implicitly the edited subject.

## Validation and failure semantics

Safe mutation checks include:

- body part exists on the target
- slot index is valid
- item is a registered organ
- body part accepts the item
- organ supports the body part
- resulting used capacity does not exceed effective capacity

Failure details are returned through `OrganInstallResult.success()` and `message()`.

## Target-aware menu / sync rules

Important distinction:

- `viewer` = the player who opened the menu
- `target` = the entity whose organs are being viewed/edited

Current sync behavior:

- target-aware menus use the existing packet path to sync the target entity by `entityId`
- `SyncOrganDataS2CPacket` is no longer just a self-player packet in the UI path
- menu-driven target editing should be considered supported for current UI flows

Caveat:

- If you mutate remote entities purely through API calls and expect arbitrary observers to update outside menu/UI flows, you may still need to reason about additional observer sync in your own feature design

## Slaughter entrypoints

- `SlaughterRoomBlock` and `SlaughterToolItem` are gameplay wrappers around the same organ overview/editing pipeline
- They do not define a separate organ storage model
- They just:
  - validate the target via health threshold config
  - apply configured restriction effects
  - open the shared target-aware `OrganOverviewMenu`

## Gotchas

- Empty slots are meaningful and must be preserved
- `getInstalledOrganPositions(...)` is not a full slot dump; it omits empty slots
- `body plan` affects which parts are currently visible/editable on a target entity
- Do not confuse viewer inventory ownership with target organ ownership
- Avoid bypassing `OrganApi` unless you intentionally need lower-level behavior
- If UI appears stale, inspect `OrganApi` sync behavior and existing menu refresh paths before assuming holder logic is broken

## When editing this area

Keep these invariants stable:

- server remains authoritative
- target-aware menu sync keeps working
- slot ordering and `ItemStack.EMPTY` preservation must not regress
- body plan resolution stays the source of truth for entity anatomy
- slaughter entrypoints remain wrappers over the same core organ API/menu flow, not a parallel system
