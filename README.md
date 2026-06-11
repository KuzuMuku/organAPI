# Organ API

Organ API 是一个基于 Forge 1.20.1 的器官/部位容器基础模组，目标是提供一个尽量简单、原版风格、便于二次开发的 API，而不是内置大量固定器官内容。

## 当前实现

- 按部位拆分的器官容器：head、chest、abdomen、left_arm、right_arm、left_leg、right_leg
- 玩家 Capability 持久化器官与部位扩容量
- 数据驱动 body part / organ 定义
- 示例器官与扩容道具
- 简单的分页式器官菜单（通过 `organ_pouch` 打开）

## 数据目录

- `data/<namespace>/organapi/body_parts/*.json`
- `data/<namespace>/organapi/organs/*.json`

## API 设计目标

- 查询部位与器官定义
- 查询玩家每个部位的容量 / 已安装器官
- 校验并安装器官
- 游戏内永久扩展部位容量
- 方便其他模组通过 JSON 或代码接入

## Configuration

模组使用 Forge common config，屠宰相关参数位于 `slaughter` 分组下，可调整：

- `health_threshold_ratio`：允许开胸的血量比例阈值
- `restriction_duration_ticks`：开胸后限制效果持续时间（tick）
- `slowness_amplifier`：缓慢效果强度
- `weakness_amplifier`：虚弱效果强度

说明：Minecraft 原生效果 amplifier 语义为 `0 = I 级`，`1 = II 级`，依此类推。屠宰室方块与便携屠宰器共享同一组配置。

## 开发者文档

- 器官控制 API 示例：`docs/organ-control-api-guide.md`
- 数据格式说明：`docs/organ-data-format.md`

## 示例内容说明

仓库内自带的 `sample_*` 器官和 `*_expansion_kit` 只是演示链路，不代表最终玩法内容。后续其他模组可以只依赖 API，而不依赖这些样例物品。
