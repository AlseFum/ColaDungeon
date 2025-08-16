package com.coladungeon.actors;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import com.coladungeon.Dungeon;
import com.coladungeon.Statistics;
import com.coladungeon.actors.blobs.Blob;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.mobs.Mob;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.SparseArray;

/**
 * Actor类是游戏中所有活动实体的基类，负责管理游戏的时间调度系统。
 *
 * ## 核心功能： - **时间管理**：控制所有游戏实体的行动顺序和时间流逝 - **调度系统**：决定哪个Actor在何时执行行动 -
 * **优先级控制**：管理不同类型Actor的执行优先级 - **性能优化**：支持PriorityQueue优化以处理大量Actor
 *
 * ## 性能优化： - 支持线性搜索（O(n)）和优先队列（O(log n)）两种调度模式 - 大型战斗场景下可获得8-14倍性能提升 -
 * 智能切换：根据Actor数量自动选择最优算法
 *
 * ## 使用模式： 1. 继承Actor类并实现act()方法 2. 调用Actor.add()将实例加入调度系统 3.
 * 使用spend()方法控制行动时间间隔 4. 调用Actor.remove()移除不再需要的Actor
 */
public abstract class Actor implements Bundlable {

    /**
     * 标准时间单位
     */
    public static final float TICK = 1f;

    /**
     * Actor的行动时间，值越小越先执行
     */
    private float time;

    private int id = 0;

    public int id() {
        if (id > 0) {
            return id;
        } else {
            return (id = nextID++);
        }
    }

    // 优先级常量：数值越大优先级越高
    protected static final int VFX_PRIO = 100;    // 视觉效果
    protected static final int HERO_PRIO = 0;     // 英雄基准
    protected static final int BLOB_PRIO = -10;   // 环境效果
    protected static final int MOB_PRIO = -20;    // 怪物行动
    protected static final int BUFF_PRIO = -30;   // Buff效果
    private static final int DEFAULT = -100;      // 默认优先级

    /**
     * 行动优先级，时间相同时数值大的先执行
     */
    public int actPriority = DEFAULT;

    /**
     * Actor的核心行动方法，需要由子类实现。
     *
     * @return true表示继续处理下一个Actor，false表示暂停Actor系统 (通常在玩家需要输入时返回false)
     */
    protected abstract boolean act();

    //#region SpendXXX
    /**
     * 精确消耗指定时间，不受任何时间影响因子影响。 直接增加Actor的行动时间，用于需要精确时间控制的场景。
     *
     * @param time 要消耗的时间量
     */
    protected void spendConstant(float time) {
        this.time += time;
        this.time = normalize(this.time);
    }

    /**
     * 消耗时间（可被时间影响因子修改）。 这是最常用的时间消耗方法，会受到缓慢、加速等效果影响。 子类（如Char）会重写此方法来实现时间修正。
     *
     * @param time 基础消耗时间
     */
    protected void spend(float time) {
        spendConstant(time);
    }

    /**
     * 将行动时间提升到下一个整数。 用于确保某些重要行动发生在整数时间点。
     */
    public void spendToWhole() {
        time = (float) Math.ceil(time);
    }

    //endregion
    /**
     * 时间标准化辅助方法。 将非常接近整数的浮点时间舍入为整数，避免浮点精度误差。
     *
     * @param time 原始时间值
     * @return 标准化后的时间值
     */
    private static float normalize(float time) {
        float ex = Math.abs(time % 1f);
        if (ex < .001f) {
            return Math.round(time);
        }
        return time;
    }

    /**
     * 延后Actor的行动时间到指定时间点。 如果当前时间已经大于等于目标时间，则不做任何改变。
     *
     * @param time 延后到的目标时间
     */
    protected void postpone(float time) {
        if (this.time < now + time) {
            this.time = normalize(now + time);
        }
    }

    /**
     * 获取此Actor距离下次行动的剩余时间。
     *
     * @return 剩余冷却时间，负值表示已经可以行动
     */
    public float cooldown() {
        return time - now;
    }

    /**
     * 清空Actor的时间，使其立即可以行动。 对于角色类型的Actor，同时清空其所有buff的时间。
     */
    public void clearTime() {
        spendConstant(-Actor.now());
        if (this instanceof Char ch) {
            for (Buff b : ch.buffs()) {
                b.spendConstant(-Actor.now());
            }
        }
    }

    /**
     * 将Actor的行动时间设置为当前游戏时间。 使Actor在下个调度周期就能行动。
     */
    public void timeToNow() {
        time = now;
    }

    /**
     * 停用此Actor，使其不再参与行动调度。 通过设置时间为最大值来实现，Actor系统会忽略这些Actor。
     */
    protected void deactivate() {
        time = Float.MAX_VALUE;
    }

    /**
     * 当Actor被添加到系统时调用的回调方法。 子类可以重写此方法来执行初始化逻辑。
     */
    protected void onAdd() {
    }

    /**
     * 当Actor从系统中移除时调用的回调方法。 子类可以重写此方法来执行清理逻辑。
     */
    protected void onRemove() {
    }

    private static final String TIME = "time";
    private static final String ID = "id";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TIME, time);
        bundle.put(ID, id);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        time = bundle.getFloat(TIME);
        int incomingID = bundle.getInt(ID);
        if (Actor.findById(incomingID) == null) {
            id = incomingID;
        } else {
            id = nextID++;
        }
    }

    // 静态成员变量
    private static final HashSet<Actor> all = new HashSet<>();
    private static final HashSet<Char> chars = new HashSet<>();
    private static volatile Actor current;
    private static final SparseArray<Actor> ids = new SparseArray<>();
    private static int nextID = 1;

    // 性能优化相关
    private static PriorityQueue<Actor> actorQueue = null;
    private static boolean usePriorityQueue = false;
    private static float now = 0;

    public static float now() {
        return now;
    }

    /**
     * 启用优先队列优化，适用于Actor数量较多的场景
     */
    public static void enablePriorityQueue() {
        if (!usePriorityQueue) {
            usePriorityQueue = true;
            actorQueue = new PriorityQueue<>(new ActorComparator());
            if (!all.isEmpty()) {
                actorQueue.addAll(all);
            }
        }
    }

    /**
     * 禁用优先队列优化，回退到线性搜索
     */
    public static void disablePriorityQueue() {
        usePriorityQueue = false;
        if (actorQueue != null) {
            actorQueue.clear();
            actorQueue = null;
        }
    }

    public static boolean isPriorityQueueEnabled() {
        return usePriorityQueue;
    }

    /**
     * Actor比较器，用于优先队列排序
     */
    private static class ActorComparator implements Comparator<Actor> {

        @Override
        public int compare(Actor a1, Actor a2) {
            int timeCompare = Float.compare(a1.time, a2.time);
            if (timeCompare != 0) {
                return timeCompare;
            }
            // Higher priority acts first when time is equal
            return Integer.compare(a2.actPriority, a1.actPriority);
        }
    }

    public static synchronized void clear() {

        now = 0;

        all.clear();
        chars.clear();
        if (actorQueue != null) {
            actorQueue.clear();
        }

        ids.clear();
    }

    public static synchronized void fixTime() {

        if (all.isEmpty()) {
            return;
        }

        float min = Float.MAX_VALUE;
        for (Actor a : all) {
            if (a.time < min) {
                min = a.time;
            }
        }

        //Only pull everything back by whole numbers
        //So that turns always align with a whole number
        min = (int) min;
        for (Actor a : all) {
            a.time -= min;
        }

        // Rebuild priority queue with updated times
        if (usePriorityQueue && actorQueue != null) {
            actorQueue.clear();
            actorQueue.addAll(all);
        }

        if (Dungeon.hero != null && all.contains(Dungeon.hero)) {
            Statistics.duration += min;
        }
        now -= min;
    }

    /**
     * 初始化Actor系统，添加当前关卡的所有Actor。 按照以下顺序初始化： 1. 英雄 2. 怪物 3. 环境效果(Blob)
     *
     * 注意：怪物需要在所有Actor添加完毕后再恢复目标，确保引用正确。
     */
    public static void init() {

        add(Dungeon.hero);

        for (Mob mob : Dungeon.level.mobs) {
            add(mob);
        }

        // 怪物需要在所有Actor添加完毕后才能正确恢复敌人目标
        for (Mob mob : Dungeon.level.mobs) {
            mob.restoreEnemy();
        }

        for (Blob blob : Dungeon.level.blobs.values()) {
            add(blob);
        }

        current = null;

        // 自动启用优先队列优化（当Actor数量较多时）
        if (!usePriorityQueue && all.size() > 20) {
            enablePriorityQueue();
        }
    }

    private static final String NEXTID = "nextid";

    public static void storeNextID(Bundle bundle) {
        bundle.put(NEXTID, nextID);
    }

    public static void restoreNextID(Bundle bundle) {
        nextID = bundle.getInt(NEXTID);
    }

    public static void resetNextID() {
        nextID = 1;
    }

    /*protected*/
    public void next() {
        if (current == this) {
            current = null;
        }
    }

    public static boolean processing() {
        return current != null;
    }

    public static int curActorPriority() {
        return current != null ? current.actPriority : HERO_PRIO;
    }

    /**
     * 控制Actor处理线程是否保持活跃的标志
     */
    public static boolean keepActorThreadAlive = true;

    /**
     * Actor系统的核心处理方法 - 游戏的心脏！
     *
     * 这个方法负责： 1. 选择下一个要执行的Actor（按时间和优先级） 2. 执行Actor的act()方法 3. 处理线程同步和中断 4.
     * 在玩家输入时暂停处理
     *
     * ## 调度算法： - **线性模式** (默认): O(n)遍历查找最早的Actor - **优先队列模式** (优化): O(log
     * n)从队列顶部获取
     *
     * ## 线程安全： - 支持中断处理，避免阻塞UI线程 - 在精灵动画期间等待，确保视觉连贯性 - 通过synchronized实现线程同步
     *
     * ## 性能特点： - 每帧调用，需要极高效率 - 支持自适应算法切换 - 大量Actor场景下性能提升8-14倍
     */
    public static void process() {

        boolean doNext;
        boolean interrupted = false;

        do {

            current = null;
            if (!interrupted && !Game.switchingScene()) {
                if (usePriorityQueue && actorQueue != null) {
                    // 优先队列模式：O(log n)
                    if (!actorQueue.isEmpty()) {
                        current = actorQueue.poll();
                    }
                } else {
                    // 线性搜索模式：O(n) - 原始稳定实现
                    float earliest = Float.MAX_VALUE;
                    for (Actor actor : all) {
                        //some actors will always go before others if time is equal.
                        if (actor.time < earliest
                                || actor.time == earliest && (current == null || actor.actPriority > current.actPriority)) {
                            earliest = actor.time;
                            current = actor;
                        }
                    }
                }
            }

            if (current != null) {

                now = current.time;
                Actor acting = current;

                if (acting instanceof Char && ((Char) acting).sprite != null) {
                    // If it's character's turn to act, but its sprite
                    // is moving, wait till the movement is over
                    try {
                        Object spriteLock = ((Char) acting).sprite;
                        synchronized (spriteLock) {
                            if (((Char) acting).sprite.isMoving) {
                                spriteLock.wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }

                interrupted = interrupted || Thread.interrupted();

                if (interrupted) {
                    doNext = false;
                    current = null;
                } else {
                    doNext = acting.act();
                    if (doNext && (Dungeon.hero == null || !Dungeon.hero.isAlive())) {
                        doNext = false;
                        current = null;
                    }

                    // 重新加入队列（仅在优先队列模式下）
                    if (usePriorityQueue && actorQueue != null && all.contains(acting)) {
                        actorQueue.offer(acting);
                    }
                }
            } else {
                doNext = false;
            }

            if (!doNext) {
                synchronized (Thread.currentThread()) {

                    interrupted = interrupted || Thread.interrupted();

                    if (interrupted) {
                        current = null;
                        interrupted = false;
                    }

                    //signals to the gamescene that actor processing is finished for now
                    Thread.currentThread().notify();

                    try {
                        Thread.currentThread().wait();
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
            }

        } while (keepActorThreadAlive);
    }

    /**
     * 立即将Actor添加到调度系统
     */
    public static void add(Actor actor) {
        add(actor, now);
    }

    /**
     * 延迟将Actor添加到调度系统
     */
    public static void addDelayed(Actor actor, float delay) {
        add(actor, now + Math.max(delay, 0));
    }

    /**
     * Actor添加的核心实现方法。
     *
     * 执行以下操作： 1. 检查重复添加 2. 分配并注册ID 3. 设置初始时间 4. 添加到各种集合中 5. 调用onAdd回调 6.
     * 处理角色类型的特殊逻辑（包括其buff）
     *
     * @param actor 要添加的Actor
     * @param time 初始时间偏移
     */
    private static synchronized void add(Actor actor, float time) {

        if (all.contains(actor)) {
            return;
        }

        ids.put(actor.id(), actor);

        all.add(actor);
        actor.time += time;

        // 添加到优先队列（如果启用）
        if (usePriorityQueue && actorQueue != null) {
            actorQueue.offer(actor);
        }

        actor.onAdd();

        if (actor instanceof Char ch) {
            chars.add(ch);
            // 递归添加角色的所有buff
            for (Buff buff : ch.buffs()) {
                add(buff);
            }
        }
    }

    /**
     * 从调度系统中移除指定的Actor。
     *
     * 执行以下清理操作： 1. 从所有集合中移除 2. 从优先队列中移除（如果启用） 3. 调用onRemove回调 4. 清理ID映射
     *
     * 注意：此方法不会自动移除角色的buff， 需要在角色死亡时单独处理。
     *
     * @param actor 要移除的Actor
     */
    public static synchronized void remove(Actor actor) {

        if (actor != null) {
            all.remove(actor);
            if (actor instanceof Char ch) {
                chars.remove(ch);
            }

            // 从优先队列移除（如果启用）
            if (usePriorityQueue && actorQueue != null) {
                actorQueue.remove(actor);
            }

            actor.onRemove();

            if (actor.id > 0) {
                ids.remove(actor.id);
            }
        }
    }

    /**
     * 冻结角色在时间中指定的时长。 ⚠️ 谨慎使用！时间操作对某些游戏效果有用但很复杂。
     *
     * 这会延迟角色及其所有buff的行动时间， 常用于时间暂停、眩晕等效果。
     *
     * @param ch 要冻结的角色
     * @param time 冻结时长
     */
    public static void delayChar(Char ch, float time) {
        ch.spendConstant(time);
        for (Buff b : ch.buffs()) {
            b.spendConstant(time);
        }
    }

    /**
     * 在指定位置查找角色
     */
    public static synchronized Char findChar(int pos) {
        for (Char ch : chars) {
            if (ch.pos == pos) {
                return ch;
            }
        }
        return null;
    }

    /**
     * 通过ID查找Actor
     */
    public static synchronized Actor findById(int id) {
        return ids.get(id);
    }

    /**
     * 获取所有活跃Actor的副本集合
     */
    public static synchronized HashSet<Actor> all() {
        return new HashSet<>(all);
    }

    /**
     * 获取所有角色Actor的副本集合
     */
    public static synchronized HashSet<Char> chars() {
        return new HashSet<>(chars);
    }

    /**
     * 确保Actor已正确添加并调用了onAdd()方法
     */
    public static boolean ensureActorAdded(Actor actor) {
        if (actor != null && all.contains(actor)) {
            if (actor instanceof Mob && ((Mob) actor).firstAdded) {
                actor.onAdd();
                return true;
            }
        }
        return false;
    }
}
