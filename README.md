# 可乐地牢
>但是并不是以可乐为主题

此改版旨在通过引入宏，加入大量默认行为来降低个人制作像素地牢改版的难度。

版权方面，采用GPL-3.0协议，具体我也不懂啦。想改版自己拿去fork什么的都行。

破碎像素地牢的原README文件改为在同目录的SPDREADME.md，参考便是。

## 宏

用Nodejs写的脚本系统，都放在script文件夹，入口是index.js。可以用`node script/index.js`打开REPL窗口。Nodejs版本最好在22以上。不用宏系统可以不管。

宏的教程还没写（咕咕咕

## 进度
宏操作基本完成，游戏本体完全没改

## 笔记

ItemSprite继承自Image，通过texture和frame来控制显示
texture是整张图片，frame控制图片范围
texture的设置是Image的。frame用int来操作