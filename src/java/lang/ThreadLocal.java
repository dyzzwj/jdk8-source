package java.lang;

import java.lang.ref.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 线程局部缓存：为线程缓存数据，数据独享
 * 原理：
 * 1. 每个线程由一个 ThreadLocalMap 属性，本质就是一个 map
 * 2. map 里面存储的 <key, value> 称为键值对，存储键值对时需要先求取哈希值
 * 3. map 里存储的 key 是一个弱引用，其包装了当前线程中构造的 ThreadLocal 对象
 * 这意味着，只要 ThreadLocal 对象丢掉了强引用，那么在下次 GC 后，map 中的 ThreadLocal 对象也会被清除
 * 对于那些 ThreadLocal 对象为空的 map 元素，会当为垃圾，稍后会被主动清理
 * 4. map 里存储的 value 就是缓存到当前线程的值，这个 value 没有弱引用去包装，需要专门的释放策略
 * 5. 一个线程对应多个 ThreadLocal，一个 ThreadLocal 只对应一个值
 *
 * 哈希值碰撞的问题：
 * 如果是单线程，因为魔数 HASH_INCREMENT 的存在，且不断扩容，这里不容易出现碰撞
 * 但如果是多线程，哈希值就很容易出现碰撞，因为属性 nextHashCode 是各线程共享的，会导致生成的哈希值出现重复
 *
 * ThreadLocal<T> value = new ThreadLocal<>();
 * <value, T> 形成 map 的键值对，value 作为 ThreadLocalMap 中的键，用它来查找匹配的值。
 *
 * @param <T>
 */
public class ThreadLocal<T> {

    /**
     * 当前 ThreadLocal 的 hashCode，由 nextHashCode() 计算而来，用于计算当前 ThreadLocal 在 ThreadLocalMap 中的索引位置
     * 一个线程可以有多个 ThreadLocal 实例，各实例之内的原始 hashCode 不相同
     * 一个 ThreadLocal 实例也可被多个线程共享，此时多个线程内看到的原始 hashCode 是相同的
     */
    private final int threadLocalHashCode = nextHashCode();

    /**
     * static + AtomicInteger 保证了在一台机器中每个 ThreadLocal 的 threadLocalHashCode 是唯一的
     * 被 static 修饰非常关键，因为一个线程在处理业务的过程中，ThreadLocalMap 是会被 set 多个 ThreadLocal 的，多个 ThreadLocal 就依靠 threadLocalHashCode 进行区分
     * 所有 ThreadLocal 共享，但每次构造一个 ThreadLocal 实例，其值都会更新
     */
    private static AtomicInteger nextHashCode =
            new AtomicInteger();

    /**
     * HASH_INCREMENT 是一个特殊哈希魔数，这主要与斐波那契散列法以及黄金分割有关
     */
    private static final int HASH_INCREMENT = 0x61c88647;

    /**
     * 返回计算出的下一个哈希值，其值为 i * HASH_INCREMENT，其中 i 代表调用次数
     */
    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    /**
     * 为 ThreadLocal 对象设置关联的初值，具体逻辑可由子类实现
     *
     * @return 该线程关联的初始值
     */
    protected T initialValue() {
        return null;
    }

    /**
     * Creates a thread local variable. The initial value of the variable is
     * determined by invoking the {@code get} method on the {@code Supplier}.
     *
     * @param <S> the type of the thread local's value
     * @param supplier the supplier to be used to determine the initial value
     * @return a new thread local variable
     * @throws NullPointerException if the specified supplier is null
     * @since 1.8
     */
    public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }

    /**
     * 构造 ThreadLocal 实例
     *
     * @see #withInitial(java.util.function.Supplier)
     */
    public ThreadLocal() {
    }

    /**
     * 返回当前 ThreadLocal 对象关联的值
     *
     * @return
     */
    public T get() {
        // 返回当前 ThreadLocal 所在的线程
        Thread t = Thread.currentThread();
        // 从线程中拿到 ThreadLocalMap
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            // 从 map 中拿到 entry
            ThreadLocalMap.Entry e = map.getEntry(this);
            // 如果不为空，读取当前 ThreadLocal 中保存的值
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T) e.value;
                return result;
            }
        }
        // 若 map 为空，则对当前线程的 ThreadLocal 进行初始化，最后返回当前的 ThreadLocal 对象关联的初值，即 value
        return setInitialValue();
    }

    /**
     * 初始化 ThreadLocalMap，并存储键值对 <key, value>，最后返回 value
     *
     * @return value
     */
    private T setInitialValue() {
        // 获取为 ThreadLocal 对象设置关联的初值
        T value = initialValue();
        Thread t = Thread.currentThread();
        // 返回当前线程 t 持有的 map
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            map.set(this, value);
        } else {
            // 为当前线程初始化 map，并存储键值对 <t, value>
            createMap(t, value);
        }
        return value;
    }

    /**
     * 为当前 ThreadLocal 对象关联 value 值
     *
     * @param value 要存储在此线程的线程副本的值
     */
    public void set(T value) {
        // 返回当前 ThreadLocal 所在的线程
        Thread t = Thread.currentThread();
        // 返回当前线程持有的map
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            // 如果 ThreadLocalMap 不为空，则直接存储<ThreadLocal, T>键值对
            map.set(this, value);
        } else {
            // 否则，需要为当前线程初始化 ThreadLocalMap，并存储键值对 <this, firstValue>
            createMap(t, value);
        }
    }

    /**
     * 清理当前 ThreadLocal 对象关联的键值对
     */
    public void remove() {
        // 返回当前线程持有的 map
        ThreadLocalMap m = getMap(Thread.currentThread());
        if (m != null) {
            // 从 map 中清理当前 ThreadLocal 对象关联的键值对
            m.remove(this);
        }
    }

    /**
     * 返回当前线程 thread 持有的 ThreadLocalMap
     *
     * @param t 当前线程
     * @return ThreadLocalMap
     */
    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }

    /**
     * 为当前线程初始化map，并存储键值对<this, firstValue>
     *
     * @param t          当前线程
     * @param firstValue 要设置的 value 值
     */
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }

    /**
     * Factory method to create map of inherited thread locals.
     * Designed to be called only from Thread constructor.
     *
     * @param  parentMap the map associated with parent thread
     * @return a map containing the parent's inheritable bindings
     */
    static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) {
        return new ThreadLocalMap(parentMap);
    }

    /**
     * Method childValue is visibly defined in subclass
     * InheritableThreadLocal, but is internally defined here for the
     * sake of providing createInheritedMap factory method without
     * needing to subclass the map class in InheritableThreadLocal.
     * This technique is preferable to the alternative of embedding
     * instanceof tests in methods.
     */
    T childValue(T parentValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * An extension of ThreadLocal that obtains its initial value from
     * the specified {@code Supplier}.
     */
    static final class SuppliedThreadLocal<T> extends ThreadLocal<T> {

        private final Supplier<? extends T> supplier;

        SuppliedThreadLocal(Supplier<? extends T> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }

    /**
     * 类似HashMap，进行元素存取时，要清理遇到的垃圾值，且合并原先紧密相邻的元素（除去垃圾值会造成新空槽）
     */
    static class ThreadLocalMap {

        /**
         * 键值对实体的存储结构  继承了弱引用 （发现即回收）
         *
         */
        static class Entry extends WeakReference<ThreadLocal<?>> {
            /**
             * 当前线程关联的 value，这个 value 并没有用弱引用追踪
             */
            Object value;

            /**
             * 构造键值对
             *
             * @param k k 作 key,作为 key 的 ThreadLocal 会被包装为一个弱引用
             * @param v v 作 value
             */
            Entry(ThreadLocal<?> k, Object v) {
               // Entry指向ThreadLocal实例的是弱引用 发现即回收
                super(k);
                value = v;
            }
        }

        /**
         * 初始容量是16，必须为 2 的幂
         */
        private static final int INITIAL_CAPACITY = 16;

        /**
         * 存储 ThreadLocal 的键值对实体数组，长度必须为 2 的幂
         * 区别于HashMap采用的链地址法，ThreadLocalMap采用的是开放地址法
         *  链地址法：，即在一个位置已经有元素了，就采用链表把冲突的元素链接在该元素后面
         *  开放地址法：即有冲突后，把要插入的元素放在要插入的位置后面为null的地方
         */
        private Entry[] table;

        /**
         * ThreadLocalMap 元素数量
         */
        private int size = 0;

        /**
         * 扩容的阈值，默认是数组大小的三分之二
         */
        private int threshold;

        /**
         * 设置扩容阙值
         */
        private void setThreshold(int len) {
            threshold = len * 2 / 3;
        }

        /**
         * 哈希值发生冲突时，计算下一个哈希值，此处使用线性探测寻址，只是简单地将索引加 1
         */
        private static int nextIndex(int i, int len) {
            return ((i + 1 < len) ? i + 1 : 0);
        }

        /**
         * 线性探测，向前遍历
         */
        private static int prevIndex(int i, int len) {
            return ((i - 1 >= 0) ? i - 1 : len - 1);
        }

        /**
         * 初始化 ThreadLocalMap，并存储键值对 <firstKey, firstValue>
         *
         * @param firstKey
         * @param firstValue
         */
        ThreadLocalMap(ThreadLocal<?> firstKey, Object firstValue) {
            table = new Entry[INITIAL_CAPACITY];
            // i 是一个 [0, INITIAL_CAPACITY) 之间的值
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
            setThreshold(INITIAL_CAPACITY);
        }

        /**
         * Construct a new map including all Inheritable ThreadLocals
         * from given parent map. Called only by createInheritedMap.
         *
         * @param parentMap the map associated with parent thread.
         */
        private ThreadLocalMap(ThreadLocalMap parentMap) {
            Entry[] parentTable = parentMap.table;
            int len = parentTable.length;
            setThreshold(len);
            table = new Entry[len];

            for (int j = 0; j < len; j++) {
                Entry e = parentTable[j];
                if (e != null) {
                    @SuppressWarnings("unchecked")
                    ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
                    if (key != null) {
                        Object value = key.childValue(e.value);
                        Entry c = new Entry(key, value);
                        int h = key.threadLocalHashCode & (len - 1);
                        while (table[h] != null)
                            h = nextIndex(h, len);
                        table[h] = c;
                        size++;
                    }
                }
            }
        }

        /**
         * 返回 key 关联的键值对实体
         *
         * @param key threadLocal
         * @return
         */
        private Entry getEntry(ThreadLocal<?> key) {
            //计算索引位置
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            // 若 e 不为空，并且 e 的 ThreadLocal 的内存地址和 key 相同，直接返回
            if (e != null && e.get() == key) {
                return e;
            } else {
                // e = null 或 e.get() != key
                // 从 i 开始向后遍历找到键值对实体
                return getEntryAfterMiss(key, i, e);
            }
        }

        /**
         * 从 i 开始向后遍历找到键值对实体
         *
         * @param  key the thread local object
         * @param  i the table index for key's hash code
         * @param  e the entry at table[i]
         * @return the entry associated with key, or null if no such
         */
        private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
            Entry[] tab = table;
            int len = tab.length;
            /**
             * e = null 或 e.get() != key
             * 所以首先e如果为null的话，那么直接返回null
             * 如果是不满足e.get() == key，那么进入while循环
             * ThreadLocalMap采用的是开放地址法，即把要插入的元素放在要插入的位置后面为null的地方
             * 所以下面的循环就是因为我们在第一次计算出来的i位置不一定存在key与我们想查找的key恰好相等的Entry，
             * 所以只能不断在后面循环，来查找是不是被插到后面了，直到找到为null的元素，因为若是插入也是到null为止的
             */
            while (e != null) {
                ThreadLocal<?> k = e.get();
                if (k == key) {
                   // 如果k==key,那么代表找到了这个所需要的Entry
                    return e;
                }
                // 遇到了垃圾值
                if (k == null) {
                    /**
                     *  k == null 因为k是弱引用v发现即回收 所以很有可能k被回收了，但其对应的value没被回收
                     */
                    // 从索引 i 开始，遍历一段连续的元素，清理其中的垃圾值，并使各元素排序更紧凑
                    /**
                     * 如果k==null，那么证明这个Entry中key已经为null,那么这个Entry就是一个过期对象，这里调用expungeStaleEntry清理该Entry。
                     * 这里解答了前面留下的一个坑，即ThreadLocal Ref销毁时，ThreadLocal实例由于只有Entry中的一条弱引用指着，那么就会被GC掉，
                     * Entry的key没了，value可能会内存泄露的，其实在每一个get，set操作时都会不断清理掉这种key为null的Entry的。
                     *
                     */
                    expungeStaleEntry(i);
                } else {
                    i = nextIndex(i, len);
                }
                e = tab[i];
            }

            //e = null 直接返回null
            return null;
        }

        /**
         * 在 map 中存储键值对<key, value>
         *
         * @param key   threadLocal
         * @param value 要设置的 value 值
         */
        private void set(ThreadLocal<?> key, Object value) {
            Entry[] tab = table;
            int len = tab.length;
            // 计算 key 在数组中的下标
            int i = key.threadLocalHashCode & (len - 1);
            // 遍历一段连续的元素，以查找匹配的 ThreadLocal 对象
            for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
                //tab[i]已经存在 发生hash冲突 采用链地址法

                // 获取该哈希值处的ThreadLocal对象
                ThreadLocal<?> k = e.get();

                // 键值ThreadLocal匹配，直接更改map中的value
                if (k == key) {
                    e.value = value;
                    return;
                }

                // 若 key 是 null，说明 ThreadLocal 被清理了，直接替换掉
                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }

            // 直到遇见了空槽也没找到匹配的ThreadLocal对象，那么在此空槽处安排ThreadLocal对象和缓存的value
            tab[i] = new Entry(key, value);
            int sz = ++size;
            // 如果没有元素被清理，那么就要检查当前元素数量是否超过了容量阙值(数组大小的三分之二)，以便决定是否扩容
            if (!cleanSomeSlots(i, sz) && sz >= threshold) {
                // 扩容的过程也是对所有的 key 重新哈希的过程
                rehash();
            }
        }

        /**
         * 从 map 中清理 key 关联的键值对
         *
         * @param key
         */
        private void remove(ThreadLocal<?> key) {
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len - 1);
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                if (e.get() == key) {
                    e.clear();
                    // 从索引 i 开始，遍历一段连续的元素，清理其中的垃圾值，并使各元素排序更紧凑
                    expungeStaleEntry(i);
                    return;
                }
            }
        }

        /**
         * Replace a stale entry encountered during a set operation
         * with an entry for the specified key.  The value passed in
         * the value parameter is stored in the entry, whether or not
         * an entry already exists for the specified key.
         *
         * As a side effect, this method expunges all stale entries in the
         * "run" containing the stale entry.  (A run is a sequence of entries
         * between two null slots.)
         *
         * @param  key the key
         * @param  value the value to be associated with key
         * @param  staleSlot index of the first stale entry encountered while
         *         searching for key.
         */
        private void replaceStaleEntry(ThreadLocal<?> key, Object value,
                                       int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;
            Entry e;

            // Back up to check for prior stale entry in current run.
            // We clean out whole runs at a time to avoid continual
            // incremental rehashing due to garbage collector freeing
            // up refs in bunches (i.e., whenever the collector runs).
            int slotToExpunge = staleSlot;
            //向前找到key为null的位置，记录为slotToExpunge,这里是为了后面的清理过程
            for (int i = prevIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = prevIndex(i, len))
                if (e.get() == null)
                    slotToExpunge = i;

            // Find either the key or trailing null slot of run, whichever
            // occurs first
            for (int i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocal<?> k = e.get();

                // If we find key, then we need to swap it
                // with the stale entry to maintain hash table order.
                // The newly stale slot, or any other stale slot
                // encountered above it, can then be sent to expungeStaleEntry
                // to remove or rehash all of the other entries in run.
                //我们从staleSlot起到下一个null为止，若是找到key和传入key相等的Entry，就给这个Entry赋新的value值，并且把它和staleSlot位置的Entry交换，然后调用CleanSomeSlots清理key为null的Entry。
                if (k == key) {
                    e.value = value;

                    tab[i] = tab[staleSlot];
                    tab[staleSlot] = e;

                    // Start expunge at preceding stale entry if it exists
                    if (slotToExpunge == staleSlot)
                        slotToExpunge = i;
                    cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
                    return;
                }

                // If we didn't find stale entry on backward scan, the
                // first stale entry seen while scanning for key is the
                // first still present in the run.
                if (k == null && slotToExpunge == staleSlot)
                    slotToExpunge = i;
            }
            //若是一直没有key和传入key相等的Entry，那么就在staleSlot处新建一个Entry。函数最后再清理一遍空key的Entry。
            // If key not found, put new entry in stale slot
            tab[staleSlot].value = null;
            tab[staleSlot] = new Entry(key, value);

            // If there are any other stale entries in run, expunge them
            if (slotToExpunge != staleSlot)
                cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
        }

        /**
         * 从索引staleSlot开始，遍历一段连续的元素，清理其中的垃圾值，并使各元素排序更紧凑
         *
         * @param staleSlot
         * @return 终止遍历过程的空槽下标
         */
        private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // 索引 staleSlot 处本身标识的就是一个垃圾值，所以需要首先清理掉
            //主要是将i位置上的Entry的value设为null，Entry的引用也设为null，那么系统GC的时候自然会清理掉这块内存；
            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            //扫描位置staleSlot之后，null之前的Entry数组，清除每一个key为null的Entry，同时若是key不为空，做rehash，调整其位置。
            /**
             * 为什么要做rehash呢?
             * 因为我们在清理的过程中会把某个值tab[j]设为null，那么这个值后面的区域如果之前是连着前面的，那么下次循环查找时，就会只查到null(tab[j])为止。
             *
             * 举个例子就是：...,<key1(hash1), value1>, <key2(hash1), value2>,...（即key1和key2的hash值相同）
             * 此时，若插入<key3(hash2), value3>，其hash计算的目标位置被<key2(hash1), value2>占了，于是往后寻找可用位置，hash表可能变为：
             * ..., <key1(hash1), value1>, <key2(hash1), value2>, <key3(hash2), value3>, ...
             * 此时，若<key2(hash1), value2>被清理，显然<key3(hash2), value3>应该往前移(即通过rehash调整位置)，否则若以key3查找hash表，将会找不到key3
             *
             */
            Entry e;
            int i;
            // 继续往后遍历连续的Entry数组，直到遇见一个空槽后停止遍历
            for (i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocal<?> k = e.get();
                // 如果当前Entry已经不包含 ThreadLocal，说明这是个垃圾值，需要清理
                if (k == null) {
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    int h = k.threadLocalHashCode & (len - 1);
                    //rehash
                    if (h != i) {
                        tab[i] = null;

                        // Unlike Knuth 6.4 Algorithm R, we must scan until
                        // null because multiple entries could have been stale.
                        while (tab[h] != null)
                            h = nextIndex(h, len);
                        tab[h] = e;
                    }
                }
            }
            return i;
        }

        /**
         * Heuristically scan some cells looking for stale entries.
         * This is invoked when either a new element is added, or
         * another stale one has been expunged. It performs a
         * logarithmic number of scans, as a balance between no
         * scanning (fast but retains garbage) and a number of scans
         * proportional to number of elements, that would find all
         * garbage but would cause some insertions to take O(n) time.
         *
         * @param i a position known NOT to hold a stale entry. The
         * scan starts at the element after i.
         *
         * @param n scan control: {@code log2(n)} cells are scanned,
         * unless a stale entry is found, in which case
         * {@code log2(table.length)-1} additional cells are scanned.
         * When called from insertions, this parameter is the number
         * of elements, but when from replaceStaleEntry, it is the
         * table length. (Note: all this could be changed to be either
         * more or less aggressive by weighting n instead of just
         * using straight log n. But this version is simple, fast, and
         * seems to work well.)
         *
         * @return true if any stale entries have been removed.
         */
        private boolean cleanSomeSlots(int i, int n) {
            boolean removed = false;
            Entry[] tab = table;
            int len = tab.length;
            do {
                i = nextIndex(i, len);
                Entry e = tab[i];
                if (e != null && e.get() == null) {
                    n = len;
                    removed = true;
                    i = expungeStaleEntry(i);
                }
            } while ( (n >>>= 1) != 0);
            return removed;
        }

        /**
         * Re-pack and/or re-size the table. First scan the entire
         * table removing stale entries. If this doesn't sufficiently
         * shrink the size of the table, double the table size.
         */
        private void rehash() {
            expungeStaleEntries();

            // Use lower threshold for doubling to avoid hysteresis
            if (size >= threshold - threshold / 4)
                resize();
        }

        /**
         * 扩容，重新计算索引，标记垃圾值，方便 GC 回收
         */
        private void resize() {
            Entry[] oldTab = table;
            int oldLen = oldTab.length;
            int newLen = oldLen * 2;
            // 新建一个数组，按照2倍长度扩容
            Entry[] newTab = new Entry[newLen];
            int count = 0;

            // 将旧数组的值拷贝到新数组上
            for (int j = 0; j < oldLen; ++j) {
                Entry e = oldTab[j];
                if (e != null) {
                    ThreadLocal<?> k = e.get();
                    // 若有垃圾值，则标记清理该元素的引用，以便GC回收
                    if (k == null) {
                        e.value = null;
                    } else {
                        // 计算 ThreadLocal 在新数组中的位置
                        int h = k.threadLocalHashCode & (newLen - 1);
                        // 如果发生冲突，使用线性探测往后寻找合适的位置
                        while (newTab[h] != null) {
                            h = nextIndex(h, newLen);
                        }
                        newTab[h] = e;
                        count++;
                    }
                }
            }
            // 设置新的扩容阈值，为数组长度的三分之二
            setThreshold(newLen);
            size = count;
            table = newTab;
        }

        /**
         * Expunge all stale entries in the table.
         */
        private void expungeStaleEntries() {
            Entry[] tab = table;
            int len = tab.length;
            for (int j = 0; j < len; j++) {
                Entry e = tab[j];
                if (e != null && e.get() == null)
                    expungeStaleEntry(j);
            }
        }
    }
}
