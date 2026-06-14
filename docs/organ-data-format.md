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

## 持久化结构

实体 Capability 使用类似下面的 NBT：

```nbt
body_parts: {
  "organapi:chest": {
    bonus_capacity: 1,
    organs: [ ... ]
  }
}
```

其中：
- `bonus_capacity` 表示该部位的永久扩容值；最终有效容量会被当前实体 body plan 解析出的 `max_capacity` 限制（如果配置了该字段）
- `organs` 是该部位当前安装的器官物品列表
- 当前菜单最多暴露 36 个可编辑器官槽，这是 UI 限制，不是全局容量上限
- 如果旧存档中存在当前 body plan 未启用的部位数据，这些数据会被保留，但不会出现在当前实体的部位列表中
