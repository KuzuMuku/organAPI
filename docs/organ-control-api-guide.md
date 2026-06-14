# Organ control API guide

面向：想在其他模组里通过 Java API 读取/修改实体器官状态的开发者。

这份文档聚焦 **entity 级器官控制 API** 的实际用法，并补充 body plan、target-aware 菜单与同步语义。

## 适用范围

当前这套 API 的关键语义如下：

- 目标对象是**任意 `Entity`**，不再只限玩家
- 实体当前可见/可编辑的 body parts 由其 resolved body plan 决定
- 玩家与其他实体都可以持有 organ holder 状态
- target-aware 菜单会把 **viewer**（打开界面的玩家）与 **target**（被查看/被编辑器官的实体）分离
- 器官位置使用逻辑位置模型：`bodyPartId + slotIndex`
- 空槽位会保留为 `ItemStack.EMPTY`，**不会**在移除器官后压缩列表

## 先看哪些源码

公开 API 入口：

- `src/main/java/cn/kuzuanpa/organapi/api/OrganApi.java`
- `src/main/java/cn/kuzuanpa/organapi/api/query/OrganQueryService.java`
- `src/main/java/cn/kuzuanpa/organapi/api/query/OrganPosition.java`
- `src/main/java/cn/kuzuanpa/organapi/api/query/BodyPartOverview.java`
- `src/main/java/cn/kuzuanpa/organapi/api/install/OrganInstallResult.java`

body plan / anatomy 解析：

- `src/main/java/cn/kuzuanpa/organapi/api/body/BodyPlanDefinition.java`
- `src/main/java/cn/kuzuanpa/organapi/api/body/ResolvedBodyPlan.java`
- `src/main/java/cn/kuzuanpa/organapi/common/body/BodyPlanResolver.java`

底层行为语义：

- `src/main/java/cn/kuzuanpa/organapi/common/capability/IOrganHolder.java`
- `src/main/java/cn/kuzuanpa/organapi/common/capability/PlayerOrganHolder.java`
- `src/main/java/cn/kuzuanpa/organapi/common/inventory/OrganPartContainer.java`
- `src/main/java/cn/kuzuanpa/organapi/common/menu/OrganOverviewMenu.java`
- `src/main/java/cn/kuzuanpa/organapi/common/network/SyncOrganDataS2CPacket.java`

## 主要公开入口

### `OrganApi`

用于执行带安全检查的器官修改与 anatomy 查询：

- `getBodyParts()`
- `getBodyParts(Entity entity)`
- `getBodyPart(ResourceLocation id)`
- `getBodyPart(Entity entity, ResourceLocation id)`
- `getBodyPlan(Entity entity)`
- `getOrgans()`
- `getOrgan(ResourceLocation id)`
- `getOrgan(ItemStack stack)`
- `install(Entity entity, ResourceLocation bodyPartId, ItemStack stack)`
- `setOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex, ItemStack stack)`
- `removeOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex)`
- `getOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex)`
- `addCapacity(Entity entity, ResourceLocation bodyPartId, int amount)`

### `OrganQueryService`

用于读取目标实体的器官状态：

- `getTotalCapacity(Entity entity, ResourceLocation bodyPartId)`
- `getUsedCapacity(Entity entity, ResourceLocation bodyPartId)`
- `getFreeCapacity(Entity entity, ResourceLocation bodyPartId)`
- `getInstalledOrgans(Entity entity, ResourceLocation bodyPartId)`
- `getOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex)`
- `getInstalledOrganPositions(Entity entity)`
- `getInstalledOrganPositions(Entity entity, ResourceLocation bodyPartId)`
- `getOverview(Entity entity, ResourceLocation bodyPartId)`

### `OrganPosition`

`OrganPosition` 用来表达一个已安装器官的逻辑位置：

- `bodyPartId()`
- `slotIndex()`
- `organ()`

其中位置语义是：
- 部位：`bodyPartId`
- 部位内第几个槽：`slotIndex`

## Body plan 与 body part 查询

### 查询实体当前可用部位

```java
Collection<BodyPartDefinition> availableParts = OrganApi.getBodyParts(entity);
```

说明：
- 返回的是该实体当前 resolved body plan 下的部位集合
- 不一定等于全局模板注册表里的全部部位

### 查询实体当前 body plan

```java
ResolvedBodyPlan plan = OrganApi.getBodyPlan(entity);
ResourceLocation defaultPart = plan.getDefaultBodyPartId(new ResourceLocation("organapi", "head"));
```

说明：
- body plan 决定实体实际拥有的 body parts、容量覆盖与 overview 区域覆盖
- 若某实体没有专门命中的 plan，会回退到默认 humanoid plan

## 示例：查询一个部位的容量与器官

```java
ResourceLocation chest = new ResourceLocation("organapi", "chest");

int total = OrganQueryService.getTotalCapacity(entity, chest);
int used = OrganQueryService.getUsedCapacity(entity, chest);
int free = OrganQueryService.getFreeCapacity(entity, chest);
List<ItemStack> organs = OrganQueryService.getInstalledOrgans(entity, chest);
```

说明：
- `organs` 列表按槽位顺序返回
- 空槽位会保留为 `ItemStack.EMPTY`
- 如果你需要“完整槽位列表”，直接用这个接口最合适

## 示例：查询某个精确槽位

```java
ItemStack slotOrgan = OrganQueryService.getOrgan(entity, chest, 0);
```

说明：
- 用于读取某个确定位置上的器官
- 如果槽位为空或不存在，会返回 `ItemStack.EMPTY`

## 示例：遍历所有已安装器官及其位置

```java
for (OrganPosition position : OrganQueryService.getInstalledOrganPositions(entity)) {
    ResourceLocation bodyPartId = position.bodyPartId();
    int slotIndex = position.slotIndex();
    ItemStack organ = position.organ();
}
```

说明：
- 这个接口只返回**非空器官**
- `getInstalledOrganPositions(entity)` 会按该实体当前 body plan 的部位顺序枚举
- 如果你需要连空槽都遍历，请改用 `getInstalledOrgans(entity, bodyPartId)` 自己按索引扫描

## 示例：安装器官到首个空槽

```java
OrganInstallResult result = OrganApi.install(entity, chest, organStack);
if (!result.success()) {
    Component reason = result.message();
}
```

说明：
- `install(...)` 会寻找该部位的首个空槽
- 安装成功后会把传入栈缩减 1 个
- 若目标实体当前开着相关菜单，既有同步逻辑会刷新 target 数据

## 示例：安全地覆盖指定槽位

```java
OrganInstallResult result = OrganApi.setOrgan(entity, chest, 0, organStack);
if (!result.success()) {
    Component reason = result.message();
}
```

说明：
- 用于“定点替换”或“脚本式编辑”
- 会执行完整安全检查
- 成功时只会存入单个器官副本（count=1）

## 示例：移除指定槽位的器官

```java
ItemStack removed = OrganApi.removeOrgan(entity, chest, 0);
if (!removed.isEmpty()) {
    // removed 是被移除的器官
}
```

说明：
- 移除后该槽位会变成 `ItemStack.EMPTY`
- 不会把后面的槽位前移
- 这点对依赖位置的效果/逻辑非常重要

## 示例：给部位扩容

```java
boolean success = OrganApi.addCapacity(entity, chest, 1);
```

说明：
- 这是对目标部位的 bonus capacity 做修改
- 最终有效容量仍会受该实体当前 body plan 解析出的 `max_capacity` 限制（如果配置了该字段）

## 示例：处理失败结果

```java
OrganInstallResult result = OrganApi.setOrgan(entity, chest, slotIndex, organStack);
if (!result.success()) {
    LOGGER.warn("Failed to set organ: {}", result.message().getString());
    return;
}
```

常见失败原因包括：
- 目标实体没有 organ holder capability
- body part 不存在于目标当前 body plan 中
- slotIndex 非法
- 物品不是已注册 organ
- 该 body part 不接受这个物品
- 该 organ 不支持这个部位
- 替换后总容量超限
- `install(...)` 时找不到空槽

## 安全检查链条

`install(...)` 和 `setOrgan(...)` 当前都走统一的安全写入语义，至少会检查：

- body part 是否存在
- slot 是否有效
- 目标物品是否已注册为 organ
- body part 是否接受该物品
- organ definition 是否支持该 body part
- 替换后总容量是否超过该部位有效容量

如果你需要“忽略规则强行写入”，不要依赖公开 API；应在你自己的内部实现层明确承担后果。

## Target-aware menu / sync 语义

当前菜单体系中的关键语义是：
- `viewer`：打开界面的玩家
- `target`：被查看/被编辑器官的实体

这体现在：
- `OrganOverviewMenu`
- `OrganMenu`
- `OrganPartContainer`
- `SyncOrganDataS2CPacket`

开发时要记住：
- 玩家背包仍属于 viewer
- organ holder 查询、容量查询、slot 写入都应针对 target
- 修改 target 实体后，应依赖现有菜单 dirty + sync 路径来刷新客户端显示

## Sync 规则

- 成功修改后会沿用当前 `OrganApi` / menu 的同步路径
- `SyncOrganDataS2CPacket` 现在按 `entityId + data` 同步目标实体，而不是只同步本地玩家自己
- target-aware 菜单场景下，玩家客户端可以看到被编辑 target 的状态变化
- 如果你的玩法是在**没有菜单上下文**的情况下修改远端实体，并且还希望其他观察者客户端立即可见，仍应自行评估是否需要额外观察者同步策略

## 屠宰入口与 API 的关系

- `SlaughterRoomBlock` 与 `SlaughterToolItem` 只是 gameplay wrapper
- 它们只是：
  1. 判定目标是否满足低血量条件
  2. 施加限制效果
  3. 打开同一个 target-aware `OrganOverviewMenu`
- 底层器官编辑、容量检查、slot 语义、同步机制仍走统一 organ API / holder / menu 逻辑

## Gotchas

- **不要压缩器官列表**：空槽是有语义的位置
- **不要把 `getInstalledOrganPositions(...)` 当作完整槽位视图**：它只返回非空器官
- **不要忽略 body plan**：某实体当前可见/可编辑的部位集合取决于其 resolved body plan
- **不要混淆 viewer 与 target**：玩家打开菜单不代表被编辑器官的实体就是玩家自己
- **不要绕过公开 API 直接写 holder**，除非你明确知道自己要跳过哪些安全检查

## 相关文档

- 数据格式：`docs/organ-data-format.md`
- 项目总览：`README.md`
