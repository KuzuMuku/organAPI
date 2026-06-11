# Organ data format

## Body parts

路径：`data/<namespace>/organapi/body_parts/<id>.json`

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
- `default_capacity`: 初始容量
- `max_capacity`: 可选，该部位的有效容量上限；省略则没有数据定义的上限
- `sort_order`: 菜单排序
- `visual_width_ratio` / `visual_height_ratio`: 器官格子布局的宽高倾向
- `accepted_tags`: 允许放入该部位的物品标签
- `overview_area`: 可选，手术室总览中该部位的点击/预览区域；包含 `x`、`y`、`width`、`height`，省略时使用通用列表式回退布局

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

## 持久化结构

玩家 Capability 使用类似下面的 NBT：

```nbt
body_parts: {
  "organapi:chest": {
    bonus_capacity: 1,
    organs: [ ... ]
  }
}
```

其中：
- `bonus_capacity` 表示该部位的永久扩容值；最终有效容量会被该部位 JSON 的 `max_capacity` 限制（如果配置了该字段）
- `organs` 是该部位当前安装的器官物品列表
- 当前菜单最多暴露 36 个可编辑器官槽，这是 UI 限制，不是全局容量上限
