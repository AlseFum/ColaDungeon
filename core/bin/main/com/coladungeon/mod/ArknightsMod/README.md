这里放明日方舟同人内容

目标：
按活动设计主题包

实现部署干员

添加干员相关武器，装甲，神器，饰品和其他消耗品

干员召唤
使用命令终端进行，终端需要充能，充到当前最大值（99或关卡特殊）

干员命令
使用命令终端进行。会切换不同的行为模式
- 跟随
- 守卫
- 追猎
- 游击
- 清理

召唤出来的干员受博士等级的影响。可以在召唤时投入一些材料来格外强力

干员的召唤与扮演
有两种思路
一种是按分队来，主角是博士，有不同的分支
一种是观察者/讲述者，主角是对应干员，能有原天赋以外的特殊天赋

那么观察者模式的干员有类似职业的天赋，而博士模式下召唤的干员就没有，ok


接下来是根据分支的设想。
特别地，每个职业侧重的物品可能都不同
- =近卫
  偏向使用武器
  - 强攻手
    装甲特殊，每次攻击会同时攻击阻挡数上限的攻击自己的敌人
  - 斗士
    高速武器，积累层数，或攻击速度（为了体验，一定程度后改为一次多次攻击）
    戒指会有用
  - 术战者
    武器可消耗法杖进行附魔，获得特殊效果
    之后攻击会同步造成法术伤害
    特别地，会有辅助功能的神器
  - 教官
    使用类似长鞭
    攻击越过友方单位时有加成
  - 领主
    使用长武器
    missileweapon也能用
  - 剑豪
    短轴爆发技能，一次攻击两次
  - 武者
    禁疗，攻击加血加攻速，盾
    特别地，格挡性能加强
  - 无畏者
    力大砖飞，概率穿透
  - 收割者
    同时攻击多名附近敌人并加血，禁疗
  - 解放者
    平时不攻击，充能，激活时每次都是开大
  - 重剑手
    自身减攻，攻速慢，力量大于+1时造成眩晕
  - 撼地者
    对攻击对象所在格造成余震，可以投出去（？
  - 本源近卫
    造成特定损伤了
    
- =先锋
  使用武器，和其他的东西。这个职业注重功能性嘛
  - 尖兵
    武器充能，直接回cost
  - 执旗手
    武器 旗与刀 可以充能cost，释放buff和blob
  - 冲锋手
    武器杀敌可恢复cost，可充能连击
  - 战术家
    武器充能释放战术点，战术点和战术家一起移动
  - 情报官
    可长距离攻击，可装载missileweapon，装载投掷武器时武器耐久变大，每次攻击得cost

- =重装
  装甲为重了，为加快节奏可能有其他的物品协助
  - 铁卫
  - 守护者
    装甲受击回血
  - 不屈者
    禁疗，装甲有特殊恢复机制
  - 驭法铁卫
    装甲可能反伤？
  - 决战者
    周围有敌人时快速充能，有困住敌人的手段
  - 要塞
    装甲和武器配套，否则要么走不动要么打了会有后坐力造成额外伤害
  - 哨戒铁卫
    攻击距离远，有神器特殊效果
  - 本源铁卫
    装甲造成伤害

- =狙击
  使用gun，spiritbow与missileweapon来操作
  - 速射手
    主武器，概率双发/本次攻击加强，技能都另算
  - 重射手
    攻击范围比较近，但概率破甲buff，攻击力较高
  - 炮手
    慢，充能后可以在某个区域造成持续损伤
  - 散射手
    同时攻击多个目标，充能会推远
  - 神射手
    可以部分穿墙，可以呼叫轰炸，隐身
  - 投掷手
    攻击会造成余震，充能则余震范围加大或段数增加或怎么着
  - 猎手
    武器需要填充弹药多次上膛
  - 攻城手
    攻击距离必须足够远，有减速buff啊什么的
  - 回环射手
    与炮手类似，但是每次攻击回来（用chakram）


- =术师
  比较难搞，毕竟原地牢就有法杖
  有了，法师主武器不是法杖，能设置法术根源，效果能更强。
  - 中坚术师
    没什么好说的emmm
  - 扩散术师
    充能可以加大范围的法杖
  - 驭械术师
    充能，会有个release功能
  - 阵法术师
    常态不攻击.jpg
  - 轰击术师
    单线超远
  - 秘术师
    能攒球，攒球有增益
  - 本源术师
    损伤中的敌人有增益
  - 塑灵术师
    杀敌能召召唤物

- =辅助
  啥都用，主要是神器？戒指？
  - 凝滞师
    造成减速debuff，概率打断敌人
  - 削弱者
    神器激活造成debuff
  - 吟游者
    没有武器，只有神器，不攻击时给增益
  - 护佑者
    
  
  - 召唤师
    是神器，召唤召唤物，可以合成召唤物
  - 工匠
    用地牢的材料造装置
    武器可以加快掉落（？
    还是神器有个终结选项，用以提高掉落
  - 巫役
    武器攻击造成元素伤害，神器造成范围元素伤害，并监听元素伤害爆发

- =特种
  你知道的.jpg
  - 处决者
    使用武器进行普攻（伏击），神器在隐身之后触发落地效果(也不一定)
  - 推击手
    武器推击
  - 钩索师
    武器可拉近，或拉近自身到指定方块
  - 陷阱师
    神器，产生陷阱，可以加材料整活
  - 怪杰
    携带会自动掉血，攻击给自身加buff，
  - 行商
    携带会掉cost，有特殊技能
  - 傀儡师
    神器替死，和武器有联动
  - 炼金师
    与工匠类似，不过造的是投射物
  - 巡空者
    神器，浮空时有特殊功能

- =医疗
  这我真不知道怎么整啊，干脆不能单独带吧
  有了，较弱的普攻和有限的治疗，和大量的魅惑卷轴
  - 医师
    可以治疗友方单位
    武器充能后可以释放强力治疗
  - 护佑者
    可以给友方提供护盾和防御增益
    武器充能后可以释放强力护盾
  - 削弱者
    可以削弱敌人的攻击力和防御力
    武器充能后可以造成强力削弱效果
  - 召唤师
    可以召唤临时单位协助战斗
    武器充能后可以召唤强力单位
  - 元素师
    可以造成元素伤害，如火焰、冰霜等
    武器充能后可以释放元素爆发

关于藏品
打算做成饰品
饰品栏可能是特殊窗口

关于每层的feeling
除了boss层，都会抽取一个feeling
feeling需要可定制

关于无尽
不好说，到了无尽就不能往上走了，然后一直是当前某个level

关于敌人
敌人应该也有类似的分类，会掉落物品，不过没有rarity（这个真的有用吗）

说不定可以出个整合模式

关于理智
可以增强干员
在濒死时使用生命药水