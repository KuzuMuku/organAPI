---
name: organapi-onboarding
description: Quickly recover the OrganAPI mod workspace context: project layout, goals, API/data model, body plans, target-aware UI flows, slaughter mechanics, sync rules, config, and build commands for a new contributor or agent.
---

# OrganAPI workspace onboarding

Use this when a new agent needs to quickly understand this Forge 1.20.1 mod before editing. Stay inside this project directory; nearby sibling directories are unrelated.

## First commands

From the project root:

```bash
./gradlew compileJava
```

Optional source map:

```bash
find src/main/java/cn/kuzuanpa/organapi -type f | sort
```

## Project goal

OrganAPI is an API-first, Chest Cavity-style organ/body-part container framework for Forge 1.20.1. It is meant to provide reusable mechanics and data definitions, not a large fixed content mod. Sample organs/items are demo content for the API path.

Core concept:

- `body_parts` JSON defines reusable body-part templates.
- `body_plans` JSON defines which parts a specific entity type actually has, plus capacity/UI overrides.
- A target entity has body parts such as `head`, `chest`, `abdomen`, arms, legs, or modded custom parts depending on its resolved body plan.
- Each body part has capacity and an ordered list of organ slots.
- Organs are item-backed definitions with valid body parts and size.
- Menus are target-aware: `viewer` is the player opening the UI, while `target` is the entity whose organs are being viewed/edited.
- Other mods should be able to add body parts/organs/body plans through JSON and call the Java API.

## Layout to read first

- Mod bootstrap: `src/main/java/cn/kuzuanpa/organapi/OrganApiMod.java`
- Public API facade: `src/main/java/cn/kuzuanpa/organapi/api/OrganApi.java`
- Data records:
  - `api/body/BodyPartDefinition.java`
  - `api/body/BodyPlanDefinition.java`
  - `api/body/ResolvedBodyPlan.java`
  - `api/organ/OrganDefinition.java`
  - `api/query/BodyPartOverview.java`
- Capability/state:
  - `common/capability/IOrganHolder.java`
  - `common/capability/PlayerOrganHolder.java`
  - `common/capability/OrganHolderProvider.java`
- Body plan loading/resolution:
  - `common/data/BodyPlanDefinitionLoader.java`
  - `common/data/BodyPlanRegistryAccess.java`
  - `common/body/BodyPlanResolver.java`
- Data loading/registry access:
  - `common/data/BodyPartDefinitionLoader.java`
  - `common/data/OrganDefinitionLoader.java`
  - `common/data/OrganRegistryAccess.java`
- Menus/screens:
  - `common/menu/BodyPartSelectionMenu.java`
  - `common/menu/OrganMenu.java`
  - `common/menu/OrganOverviewMenu.java`
  - `client/screen/BodyPartSelectionScreen.java`
  - `client/screen/OrganScreen.java`
  - `client/screen/OrganOverviewScreen.java`
  - `client/screen/OrganOverviewLayout.java`
- Networking/sync:
  - `common/network/OrganApiNetwork.java`
  - `common/network/SyncOrganDataS2CPacket.java`
  - `common/network/OpenOrganMenuC2SPacket.java`
  - `common/network/CycleBodyPartC2SPacket.java`
  - `common/network/SelectBodyPartC2SPacket.java`
- Slaughter/gameplay helpers:
  - `common/util/SlaughterAccessHelper.java`
  - `common/block/SlaughterRoomBlock.java`
  - `common/item/SlaughterToolItem.java`
- Config:
  - `common/config/OrganApiConfig.java`
- JSON definitions:
  - `src/main/resources/data/organapi/organapi/body_parts/*.json`
  - `src/main/resources/data/organapi/organapi/body_plans/*.json`
  - `src/main/resources/data/organapi/organapi/organs/*.json`

## API and data model

`OrganApi` exposes the intended integration surface:

- global template queries: `getBodyParts()`, `getBodyPart(id)`
- entity-aware anatomy queries: `getBodyParts(entity)`, `getBodyPart(entity, id)`, `getBodyPlan(entity)`
- organ queries: `getOrgans()`, `getOrgan(id)`, `getOrgan(stack)`
- mutations: `install(entity, bodyPartId, stack)`, `setOrgan(...)`, `removeOrgan(...)`, `addCapacity(entity, bodyPartId, amount)`

`BodyPartDefinition` fields:

- `id`, `translationKey`, `defaultCapacity`, `maxCapacity`, `sortOrder`
- `acceptedTags`: item tags allowed in that body part
- `visualWidthRatio`, `visualHeightRatio`: layout bias for UI grids
- `overviewArea`: UI body-map rectangle

`BodyPlanDefinition` defines:

- `entity_types`
- `parts`
- per-part enable/disable, capacity, sorting, accepted tags, layout, and overview-area overrides

Important relationship:

- `body_parts` = reusable templates
- `body_plans` = resolved per-entity anatomy

JSON loaders read from resource reload paths:

- Body parts: `data/<namespace>/organapi/body_parts/*.json`
- Body plans: `data/<namespace>/organapi/body_plans/*.json`
- Organs: `data/<namespace>/organapi/organs/*.json`

`BodyPlanResolver` is the canonical runtime bridge from entity -> resolved body plan.

## Entity state and slot invariants

`PlayerOrganHolder` is the authoritative holder implementation for entity organ state. It stores:

- `Map<ResourceLocation, BodyPartState>`
- per part `bonusCapacity`
- per part ordered `List<ItemStack> organs`

Important invariant: organ lists are positional and preserve `ItemStack.EMPTY`. Do not compact lists when removing organs. UI ordering depends on empty slots staying in place.

Mutation rules:

- `install(...)` validates organ item, body part support, free capacity, then fills the first empty slot.
- `removeOrgan(...)` replaces the exact slot with `ItemStack.EMPTY`.
- `setOrgan(...)` overwrites the exact slot with a single-count copy or empty.
- `ensureSlotCount(...)` grows the list to at least current capacity and never shrinks it.
- `dirty` marks server-side changes that menus should sync to the client.

`OrganPartContainer` adapts menu slots to the current selected body part by forwarding `getItem`, `setItem`, and `removeItem` to the holder of the current target entity.

## UI/menu flows

Portable opener flow:

1. `OrganPouchItem` / `chest_opener` opens `BodyPartSelectionMenu`.
2. `BodyPartSelectionScreen` renders the target entity body map using resolved body-part areas.
3. Clicking a region sends `OpenOrganMenuC2SPacket(targetEntityId, bodyPartId)`.
4. Server opens `OrganMenu` for the selected body part and target entity.
5. `OrganMenu` uses one `OrganPartContainer`; cycling body parts changes the container body-part id.

Surgery room overview flow:

1. `SurgeryRoomBlock` opens `OrganOverviewMenu`.
2. `OrganOverviewScreen` renders target body-part regions and an editor grid.
3. Clicking a region sends `SelectBodyPartC2SPacket`.
4. Server calls `OrganOverviewMenu.setSelectedBodyPartIndex(...)`.

Slaughter room / portable slaughter flow:

1. `SlaughterRoomBlock` looks for a living entity directly above the block.
2. `SlaughterToolItem` directly targets a `LivingEntity` via item interaction.
3. Both check `SlaughterAccessHelper.canOpenChestCavity(...)` against the `slaughter.health_threshold_ratio` config.
4. On success both apply configured restriction effects and open the same `OrganOverviewMenu` focused on `organapi:chest`.

Slot validation lives in `common/menu/slot/OrganSlot.java`:

- slot must be active (`isOrganSlotEnabled`)
- selected body part must accept the item's tags
- organ definition must support the selected body part

UI layout helpers are intentionally separate:

- `OrganScreenLayout` for the portable organ menu.
- `OrganOverviewLayout` for surgery/slaughter overview body map/editor/player inventory positioning.
- `SlotPositioning` mutates vanilla `Slot` x/y positions.

## Sync rules

Full organ state sync uses `OrganApiNetwork.sync(ServerPlayer viewer, Entity target)` and `SyncOrganDataS2CPacket(entityId, data)`.

Existing sync triggers:

- login
- dimension change
- respawn
- dirty menu changes in `OrganMenu` / `OrganOverviewMenu`

When changing menu/capability behavior, keep the server as authority and sync the full target holder after dirty changes. Client-side screens often query the local synced target capability through `OrganQueryService`, so stale sync looks like organs returning to inventory or target body state not changing.

## Config

Forge common config currently exposes a `slaughter` section in `OrganApiConfig`:

- `health_threshold_ratio`
- `restriction_duration_ticks`
- `slowness_amplifier`
- `weakness_amplifier`

These values are consumed centrally by `SlaughterAccessHelper`. If you change slaughter behavior, update both config comments and any related docs.

## Common edit patterns

Adding a body part template:

1. Add or edit JSON under `src/main/resources/data/<namespace>/organapi/body_parts/`.
2. Include translation key/default capacity/sort order/accepted tags/visual ratios.
3. Add language entries and assets as needed.

Adding or editing a body plan:

1. Add or edit JSON under `src/main/resources/data/<namespace>/organapi/body_plans/`.
2. Bind entity types via `entity_types`.
3. Override only the fields you need from the template.
4. Verify `BodyPlanResolver` returns the intended part set/order.

Adding an organ:

1. Register/create the backing item if this mod owns it (`OrganItems`, models, recipes).
2. Add item tag membership if body parts accept by tag.
3. Add JSON under `src/main/resources/data/<namespace>/organapi/organs/` with `item`, `valid_parts`, `size`, `tooltips`, `tags`.
4. Compile and test placement in `OrganSlot`-backed menus.

Adding a new target-aware surgery/slaughter entrypoint:

1. Resolve a `LivingEntity` target on the server.
2. Reuse `SlaughterAccessHelper` / `NetworkHooks.openScreen(...)` patterns where possible.
3. Pass `targetEntityId` through menu-opening buffer payloads.
4. Confirm the menu edits target state, not viewer state.

Changing organ storage:

- Preserve positional slots and empties.
- Update `serializeNBT`/`deserializeNBT` together.
- Keep target sync in mind; changes should eventually flow through `SyncOrganDataS2CPacket`.

Changing slaughter config:

- Update `OrganApiConfig` value declarations and comments.
- Keep `SlaughterAccessHelper` as the only consumer of slaughter tuning values.
- Update README / docs if semantics change.

Changing UI:

- Keep slot positions and custom preview rendering in sync.
- Hidden organ slots are moved offscreen and disabled by `isActive()`.
- Body-part switching reuses the same `Slot` objects; force menu state/full slot sync if selected body part changes.

## Build and run notes

Verified compile command:

```bash
./gradlew compileJava
```

ForgeGradle run tasks visible in this workspace include:

```bash
./gradlew tasks --all | grep -E 'run(Client|Server|GameTest|Data)|prepare.*Run'
```

Use `./gradlew runClient` for manual Minecraft client runs when a display is available. In this container, the command launched a Forge 1.20.1 Minecraft window on `DISPLAY=:0`, but this onboarding skill is for workspace recovery rather than automated UI driving.

## Gotchas

- Do not read or depend on sibling directories outside this project; they are unrelated projects.
- The mod id is `organapi`.
- Data-driven anatomy definitions live under `data/<namespace>/organapi/...`; that double `organapi` path is intentional because it is the loader directory inside a namespace.
- `body_parts` are templates; `body_plans` are resolved per-entity anatomy.
- Do not compact organ lists after removals; empty slots are meaningful.
- If UI appears stale after a server-side mutation, inspect dirty handling and `OrganApiNetwork.sync`, not only the screen code.
- Do not confuse viewer and target when tracing menus, packets, or inventory ownership.
