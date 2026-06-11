# Organ data format

## Body parts

路径：`data/<namespace>/organapi/body_parts/<id>.json`

示例：

```json
{
  "translation_key": "body_part.organapi.chest",
  "default_capacity": 4,
  "sort_order": 1,
  "accepted_tags": ["organapi:organs"]
}
```

字段：
- `translation_key`: 部位显示名称语言键
- `default_capacity`: 初始容量
- `sort_order`: 菜单排序
- `accepted_tags`: 允许放入该部位的物品标签

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
- `bonus_capacity` 表示该部位的永久扩容值
- `organs` 是该部位当前安装的器官物品列表
