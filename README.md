# minesweeper
经典扫雷复刻!

基于之前开发的cino项目搭建
[GitHub](https://github.com/thrblock/cino)

![DEMO](https://raw.githubusercontent.com/thrblock/minesweeper/master/minesweeper.png) 

## 🚩回顾一下具体规则
![F1](https://raw.githubusercontent.com/thrblock/minesweeper/master/mineSrc/1.png) 
* 鼠标左键点击可以翻开一个格子，翻开所有非雷格子获胜，翻开地雷格子则失败；
* 格子上的数字代表周围8格范围内雷的数量；
* 用鼠标右键将一个格子标记为地雷，再次右键取消标记；
## 🚩翻牌子递归版
![F2](https://raw.githubusercontent.com/thrblock/minesweeper/master/mineSrc/14.png) 
其实在游戏过程中，经常会翻开一大片区域；

这里暗含了一个递归规则，即如果翻开的牌子下面没有数字（周围没有任何雷），则递归的翻开周围8个格子；

以此类推，如果没有数字的格子连成片，则会因任意一个格子的翻开而形成一大片被数字包裹的区域；

## 🚩杂七杂八
![F3](https://raw.githubusercontent.com/thrblock/minesweeper/master/mineSrc/19.png) 

### 标记其实没有用
‘翻开所有非雷格子获胜’，这意味着不标记雷而单纯的翻开其它牌子并赢得胜利是可行的，‘标记’雷只是辅助判断而已；

事实上，在一些竞速的扫雷比赛中，以最快速度翻格子才是常规操作；

### Lucky
想要赢得胜利，还是需要一些人品，对于标准的困难模式扫雷，一般需要有2~4次二选一的猜雷成分；

在开局翻开大片连续区域，将意味着后续进程中的猜雷概率对应增高；

### 双击
这里说的双击不是只鼠标的连点，而是左右键同时对已翻开的数字格进行点击；

如果周围格子被标记的雷数与数字相等，则会自动揭开剩余的格子；

否则，会以按压动画的形式标记周围的格子，仅仅是让它们更显眼；

## 🚩假装有结尾
![F4](https://raw.githubusercontent.com/thrblock/minesweeper/master/mineSrc/26.png) 

那么玩得愉快！
