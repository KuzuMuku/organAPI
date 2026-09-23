# Organ data format

## Body parts

路径：`data/<namespace>/organapi/body_parts/<id>.json`

这些 JSON 表示“部位模板”，用于定义一个部位的默认属性；实体实际拥有哪些部位、容量多少、渲染区域在哪，改由 `body_plans` 决定。

示例：

```json
{
  "translation_key": "body_part.organapi.chest",
  "default_capacity": 4,
  "max_capacity": 36,
  "sort_order": 1,
  "visual_width_ratio": 1.3,
  "visual_height_ratio": 1.2,
  "accepted_tags": ["organapi:organs"],
  "overview_area": {
    "x": 26,
    "y": 34,
    "width": 48,
    "height": 58
  }
}
```

字段：
- `translation_key`: 部位显示名称语言键
- `default_capacity`: 默认容量；如果某个 body plan 没有覆盖容量，则使用此值
- `max_capacity`: 可选，该部位的有效容量上限；省略则没有数据定义的上限
- `sort_order`: 默认菜单排序
- `visual_width_ratio` / `visual_height_ratio`: 默认器官格子布局的宽高倾向
- `accepted_tags`: 允许放入该部位的物品标签
- `overview_area`: 可选，默认总览中该部位的点击/预览区域；包含 `x`、`y`、`width`、`height`，省略时使用通用列表式回退布局

## Body plans

路径：`data/<namespace>/organapi/body_plans/<id>.json`

body plan 用于定义某类实体实际拥有哪些部位，以及对模板的覆盖。

示例：

```json
{
  "entity_types": ["minecraft:player", "minecraft:zombie"],
  "parts": {
    "organapi:head": {
      "enabled": true,
      "capacity": 2,
      "overview_area": {
        "x": 36,
        "y": 0,
        "width": 28,
        "height": 28
      }
    },
    "organapi:chest": {
      "enabled": true,
      "capacity": 4
    },
    "organapi:left_arm": {
      "enabled": false
    },
    "mymod:tail": {
      "enabled": true,
      "translation_key": "body_part.mymod.tail",
      "capacity": 2,
      "visual_width_ratio": 0.6,
      "visual_height_ratio": 1.2,
      "accepted_tags": ["organapi:organs"],
      "overview_area": {
        "x": 80,
        "y": 120,
        "width": 18,
        "height": 40
      }
    }
  }
}
```

字段：
- `entity_types`: 使用该 body plan 的实体类型 id 列表
- `parts`: 该实体实际拥有的部位定义，key 为部位 id
  - `enabled`: 是否启用该部位
  - `translation_key`: 可选，覆盖模板名称
  - `capacity`: 可选，覆盖模板默认容量
  - `max_capacity`: 可选，覆盖模板容量上限
  - `sort_order`: 可选，覆盖排序
  - `accepted_tags`: 可选，覆盖接受的器官标签
  - `visual_width_ratio` / `visual_height_ratio`: 可选，覆盖格子布局倾向
  - `overview_area`: 可选，覆盖总览中的渲染/点击区域

说明：
- 如果某个部位已存在于 `body_parts` 模板中，body plan 中未填写的字段会继承模板值。
- 如果某个部位只在 body plan 中出现，也可以直接定义为新部位。
- 内置默认 `organapi:humanoid` body plan 会为 `minecraft:player` 提供当前的人形默认布局；未命中的实体目前也会回退到该默认 plan。

## Organs

路径：`data/<namespace>/organapi/organs/<id>.json`

示例：

```json
{
  "item": "organapi:sample_heart",
  "valid_parts": ["organapi:chest"],
  "size": 1,
  "tooltips": ["Sample heart organ for chest capacity tests."],
  "tags": ["circulatory", "sample"]
}
```

字段：
- `item`: 对应物品 id
- `valid_parts`: 允许安装的部位列表
- `size`: 占用容量
- `tooltips`: 预留说明文本
- `tags`: 预留分类标签

## Surgery / Slaughter access

### Surgery room

- `SurgeryRoomBlock` 打开 `OrganOverviewMenu`
- target 为当前被手术/查看的实体
- 在界面中点击 body area 可切换当前编辑部位

### Portable organ editor

- `OrganPouchItem` / `chest_opener` 先打开 `BodyPartSelectionMenu`
- 选择部位后通过 `OpenOrganMenuC2SPacket` 打开 `OrganMenu`

### Slaughter room

- `SlaughterRoomBlock` 会查找方块正上方的活着 `LivingEntity`
- 若目标当前血量比例 `<= slaughter.health_threshold_ratio`，则为其施加限制效果并打开 `OrganOverviewMenu`
- 若没有目标或目标血量过高，则不会打开 UI

### Portable slaughter tool

- `SlaughterToolItem` 直接对目标 `LivingEntity` 使用
- 若目标当前血量比例 `<= slaughter.health_threshold_ratio`，则为其施加限制效果并打开同一个 `OrganOverviewMenu`

## Slaughter config

Forge common config 中的 `slaughter` 分组当前提供以下键：

- `slaughter.health_threshold_ratio`
  - 默认：`0.30`
  - 含义：目标当前血量 / 最大血量 `<=` 该值时，允许开胸
- `slaughter.restriction_duration_ticks`
  - 默认：`600`
  - 含义：开胸后施加的限制效果持续时长（tick）
- `slaughter.slowness_amplifier`
  - 默认：`4`
  - 含义：开胸后施加的缓慢效果 amplifier
- `slaughter.weakness_amplifier`
  - 默认：`2`
  - 含义：开胸后施加的虚弱效果 amplifier

说明：Minecraft 原生效果 amplifier 语义为 `0 = I 级`，`1 = II 级`，依此类推。屠宰室与便携屠宰器共用同一组配置。

## 持久化文件

器官状态不再直接写入生物实体 NBT，而是按世界和 UUID 保存为独立 JSON：

```text
saves/<世界名>/organAPI/player/<UUID>.json
saves/<世界名>/organAPI/entity/<UUID>.json
```

一个 UUID 使用一个共享文件；OrganAPI 写在顶层 `organapi` 字段，OrganEffects 写在顶层 `organeffects` 字段：

```json
{
  "format": 1,
  "organapi": {
    "body_parts": {
      "organapi:chest": {
        "bonus_capacity": 1,
        "organs": []
      }
    }
  },
  "organeffects": {}
}
```

其中：
- `bonus_capacity` 表示该部位的永久扩容值；最终有效容量会被当前实体 body plan 解析出的 `max_capacity` 限制（如果配置了该字段）
- `organs` 是该部位当前安装的非空器官列表；新存档中的每个条目会带 `slot` 字段保留原始槽位，旧存档没有 `slot` 时按列表顺序读取
- 当前菜单最多暴露 36 个可编辑器官槽，这是 UI 限制，不是全局容量上限
- 如果旧存档中存在当前 body plan 未启用的部位数据，这些数据会被保留，但不会出现在当前实体的部位列表中
- 旧存档中的 `organapi.organ_holder` / `organeffects.effect_holder` 实体 NBT 会在加载时自动迁移到 JSON，并清除旧的持久化键
- 非玩家实体在 OrganAPI bootstrap 完成前，且没有任何 body part 数据时不会写入文件；OrganEffects 也只在存在效果状态时写入
- 文件只应在服务端写入；客户端没有对应的本地世界路径

## 自定义包目录布局（游戏运行目录）

从本版本起，器官定义不再只从 mod 内置 datapack 读取，而是改为 **tacz 式自定义包**：organAPI 在游戏运行目录下扫描外部包并注入资源系统。这样无需重新打包 mod 即可增删器官/部位/body_plan 定义与其贴图模型。

目录约定（`<游戏目录>` 通常是 `.minecraft` 或开发环境的 `run/`）：

```text
<游戏目录>/organs/
  data/<pack名>/          服务端数据侧，走 PackType.SERVER_DATA（datapack 自带 server→client 同步）
    data/<ns>/organapi/organs/<id>.json
    data/<ns>/organapi/body_parts/<id>.json
    data/<ns>/organapi/body_plans/<id>.json
  resources/<pack名>/      客户端资源侧，走 PackType.CLIENT_RESOURCES（不同步、不校验）
    assets/<ns>/models/item/<id>.json
    assets/<ns>/textures/item/<id>.png
    assets/<ns>/lang/<locale>.json
```

要点：
- 每个 `organs/data/` 或 `organs/resources/` 的**直接子目录**就是一个独立子包（sub-pack）。所有子包会被聚合成一个内置包 `organapi_packs`，`Position.BOTTOM` + `PackSource.BUILT_IN` + `alwaysEnabled=true`，无需在世界创建界面手动启用。
- 子包内部的相对路径与原版 datapack / 资源包完全一致（`data/<ns>/...` 与 `assets/<ns>/...`），`<ns>` 是命名空间（示例包用 `organapi`）。
- 定义 JSON 的格式与本文档上述各节完全相同；只是加载来源从 mod jar 换成了外部包。
- 修改 `organs/data/` 下的内容后，游戏内 `/reload` 即可生效，无需重启。
- 首次启动时若 `organs/data/` 与 `organs/resources/` 不存在会自动创建，并释放一份内置示例包 `organapi_samples`（见下）。

### 内置示例包 organapi_samples

organAPI 首次启动时会把 jar 内置的示例包模板复制到：

```text
<游戏目录>/organs/data/organapi_samples/       示例器官定义（sample_heart / sample_lung / sample_brain / sample_tendon / sample_leg_muscle）
<游戏目录>/organs/resources/organapi_samples/   对应的物品模型与贴图
```

复制仅在目标目录不存在时进行，因此不会覆盖你的修改；想恢复默认删除该目录再启动即可。模板在 jar 内位于 `pack_templates/data/organapi_samples/` 与 `pack_templates/resources/organapi_samples/`（这两个根不在 `data/`/`assets/` 下，因此原版不会自动加载，仅作复制源）。

可把 `organapi_samples` 当作编写新包的参照：复制一份、改名、替换其中的 json/贴图即可得到一个新子包。
