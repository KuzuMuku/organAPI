# Organ control API guide

面向：想在其他模组里通过 Java API 读取/修改实体器官状态的开发者。

这份文档聚焦 **entity 级器官控制 API** 的实际用法，不展开 UI、菜单或样例玩法设计。

## 适用范围

当前这套 API 的关键语义如下：

- 目标对象是**服务端任意 `Entity`**，不再只限玩家
- 玩家目标在成功修改后会沿用现有同步逻辑自动同步到客户端
- 非玩家实体当前只保证**服务端 API 可查可改**，不承诺客户端自动可见
- 器官位置使用逻辑位置模型：`bodyPartId + slotIndex`
- 空槽位会保留为 `ItemStack.EMPTY`，**不会**在移除器官后压缩列表

## 先看哪些源码

公开 API 入口：

- `src/main/java/cn/kuzuanpa/organapi/api/OrganApi.java`
- `src/main/java/cn/kuzuanpa/organapi/api/query/OrganQueryService.java`
- `src/main/java/cn/kuzuanpa/organapi/api/query/OrganPosition.java`
- `src/main/java/cn/kuzuanpa/organapi/api/install/OrganInstallResult.java`

底层行为语义：

- `src/main/java/cn/kuzuanpa/organapi/common/capability/IOrganHolder.java`
- `src/main/java/cn/kuzuanpa/organapi/common/capability/PlayerOrganHolder.java`

## 主要公开入口

### `OrganApi`

用于执行带安全检查的器官修改：

- `install(Entity entity, ResourceLocation bodyPartId, ItemStack stack)`
- `setOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex, ItemStack stack)`
- `removeOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex)`
- `getOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex)`
- `addCapacity(Entity entity, ResourceLocation bodyPartId, int amount)`

### `OrganQueryService`

用于读取器官状态：

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

    // 在这里根据部位、槽位、器官内容做你自己的逻辑
}
```

说明：

- 这个接口只返回**非空器官**
- 适合做效果结算、部位扫描、调试输出
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
- 对玩家目标会自动复用当前同步逻辑
- 对非玩家实体只保证服务端能力已修改

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
- 最终有效容量仍会受 body part 数据定义里的 `max_capacity` 限制（如果配置了该字段）

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
- body part 不存在
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

## 同步规则

对玩家：

- 修改成功后会沿用当前 `OrganApi` 内部的同步路径
- 如果玩家当前正开着 organ 菜单，也会复用既有 broadcast 逻辑，避免客户端状态回弹

对非玩家实体：

- 当前只保证服务端状态可查可改
- 不会自动把 organ 状态同步到其他客户端观察者
- 如果你的玩法需要客户端展示，应由你的模组自行定义额外同步方式

## Gotchas

- **不要压缩器官列表**：空槽是有语义的位置
- **不要把 `getInstalledOrganPositions(...)` 当作完整槽位视图**：它只返回非空器官
- **不要默认所有实体修改后客户端都能看见**：目前只有玩家沿用自动同步路径
- **不要绕过公开 API 直接写 holder**，除非你明确知道自己要跳过哪些安全检查

## 相关文档

- 数据格式：`docs/organ-data-format.md`
- 项目总览：`README.md`
