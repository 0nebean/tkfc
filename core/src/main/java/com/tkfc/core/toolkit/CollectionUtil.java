package com.tkfc.core.toolkit;

import com.tkfc.core.constants.StringPool;
import com.tkfc.core.function.SerializableConsumer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 集合工具类
 *
 * @author 0neBean
 * @since 2021-11-30 00:00:04
 */
public class CollectionUtil {

    /**
     * 合并数组
     *
     * @param lists 所有数组
     * @param <T>   泛型
     * @return list
     */
    @SafeVarargs
    public static <T> List<T> mergeList(List<T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    /**
     * 返回字符串在字符数组中的下表
     *
     * @param strArr 字符数组
     * @param str    字符
     * @return index
     */
    public static int indexOfStringArray(String[] strArr, String str) {
        for (int i = 0; i < strArr.length; i++) {
            if (Objects.equals(strArr[i], str)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 合并byte数组
     *
     * @param args 字符串
     * @return byte[] 返回类型
     */
    public static byte[] byteArrMerge(String... args) {
        List<byte[]> srcArrays = new ArrayList<>();
        for (String str : args) {
            srcArrays.add(str.getBytes());
        }
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    /**
     * 合并byte数组
     *
     * @param arrays 数组
     * @return byte[]
     */
    public static byte[] byteArrMerge(byte[]... arrays) {
        int length = 0;
        int destPos = arrays.length;

        for (int var4 = 0; var4 < destPos; ++var4) {
            byte[] array = arrays[var4];
            length += array.length;
        }

        byte[] newArray = new byte[length];
        destPos = 0;

        for (byte[] array : arrays) {
            System.arraycopy(array, 0, newArray, destPos, array.length);
            destPos += array.length;
        }

        return newArray;
    }

    /**
     * 获取对象转成list后的长度
     *
     * @param obj target
     * @return size
     */
    public static Integer getObjCastListSize(Object obj) {
        if (Objects.isNull(obj)) {
            return 0;
        }
        if (CollectionUtil.isArray(obj)) {
            List<?> list = (List<?>) obj;
            return list.size();
        } else {
            return 0;
        }
    }

    /**
     * 数组转string 不带括号
     *
     * @param list list
     * @return str
     */
    public static String toStringWithOutSqBracket(List<?> list) {
        String str = list.toString();
        str = str.replace(StringPool.LEFT_SQ_BRACKET, StringPool.EMPTY);
        str = str.replace(StringPool.RIGHT_SQ_BRACKET, StringPool.EMPTY);
        str = str.replace(StringPool.SPACE, StringPool.EMPTY);
        return str;
    }

    /**
     * 是否是数组
     *
     * @param obj 对象
     * @return bool
     */
    public static boolean isArray(Object obj) {
        return obj.getClass().isArray() || obj instanceof List;
    }

    /**
     * 判断集合是否为空
     *
     * @param obj 集合
     * @return bool
     */
    public static boolean isEmpty(Collection<?> obj) {
        return obj == null || obj.size() == 0;
    }

    /**
     * 判断集合是否为空
     *
     * @param obj 集合
     * @return bool
     */
    public static boolean isNotEmpty(Collection<?> obj) {
        return obj != null && obj.size() != 0;
    }

    /**
     * 是否为空
     *
     * @param array 数组
     * @return bool
     */
    public static boolean isEmpty(@Nullable Object[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    /**
     * 不为空
     *
     * @param array 数组
     * @return bool
     */
    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !isEmpty(array);
    }

    /**
     * 获取数组
     *
     * @param items 元素
     * @param <T>   类型
     * @return []
     */
    @SafeVarargs
    public static <T> T[] asArray(T... items) {
        return items;
    }

    /**
     * 对象深度克隆---使用序列化进行深拷贝 注意： 使用序列化的方式来实现对象的深拷贝，但是前提是，对象必须是实现了 Serializable接口才可以，Map本身没有实现 Serializable
     * 这个接口，所以这种方式不能序列化Map，也就是不能深拷贝Map。但是HashMap是可以的，因为它实现了Serializable。
     *
     * @param obj 要克隆的对象
     * @param <T> 对象类型
     * @return 集合
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCloneHashMap(T obj) {
        T clonedObj = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
            oos.writeObject(obj);
            oos.close();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            clonedObj = (T) objectInputStream.readObject();
            objectInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clonedObj;
    }

    /**
     * 是否包含改元素
     *
     * @param arr  数组
     * @param item 元素
     * @return bool
     */
    public static Boolean isTargetContainsArrItem(String[] arr, String item) {
        boolean flag = false;
        for (String s : arr) {
            if (item.contains(s)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 方法的返回值 是否是集合
     *
     * @param m 方法
     * @return bool
     */
    public static boolean isCollectionType(Method m) {
        boolean result = false;
        if (m.getReturnType().isPrimitive()) return true;
        if (Collection.class.isAssignableFrom(m.getReturnType())) {
            result = true;
        } else if (Map.class.isAssignableFrom(m.getReturnType())) {
            result = true;
        }
        return result;
    }

    /**
     * 遍历map
     *
     * @param map  集合
     * @param each 遍历逻辑
     */
    public static <K, V> void forEachMap(Map<K, V> map, BiConsumer<K, V> each) {
        if (Objects.nonNull(map) && !map.isEmpty()) {
            for (Map.Entry<K, V> node : map.entrySet()) {
                Object key = node.getKey();
                Object value = node.getValue();
                if (Objects.isNull(key) || Objects.isNull(value)) {
                    continue;
                }
                each.accept(node.getKey(), node.getValue());
            }
        }
    }

    /**
     * 遍历map 的 key
     *
     * @param map  集合
     * @param each 遍历逻辑
     */
    public static <K> void forEachMapKey(Map<K, ?> map, Consumer<K> each) {
        if (Objects.nonNull(map) && !map.isEmpty()) {
            for (Map.Entry<K, ?> node : map.entrySet()) {
                Object key = node.getKey();
                Object value = node.getValue();
                if (Objects.isNull(key) || Objects.isNull(value)) {
                    continue;
                }
                each.accept(node.getKey());
            }
        }
    }

    /**
     * 遍历list带下标
     *
     * @param list list
     * @param each 遍历逻辑
     * @param <T>  泛型
     */
    public static <T> void forEachArrayWithIndex(List<T> list, BiConsumer<T, Integer> each) {
        for (int i = 0; i < list.size(); i++) {
            each.accept(list.get(i), i);
        }
    }

    /**
     * 遍历list
     *
     * @param <T>  泛型
     * @param list list
     * @param each 遍历逻辑
     */
    public static <T> void forEachArray(List<T> list, Consumer<T> each) {
        for (T t : list) {
            each.accept(t);
        }
    }

    /**
     * 数组转逗号分割字符串
     *
     * @param array 数组
     * @return string
     */
    public static String arrayToStringWithComma(String[] array) {
        String join = StringUtils.join(array, StringPool.COMMA);
        join = join.startsWith(StringPool.COMMA) ? join.substring(1) : join;
        return join;
    }

    /**
     * list转逗号分割字符串
     *
     * @param list list
     * @return string
     */
    public static String listToStringWithComma(List<?> list) {
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        String join = StringUtils.join(list, StringPool.COMMA);
        join = join.startsWith(StringPool.COMMA) ? join.substring(1) : join;
        return join;
    }

    /**
     * list转字符串
     *
     * @param list list
     * @return string
     */
    public static String listToString(List<?> list, String separator) {
        return StringUtils.join(listToStringArr(list), separator);
    }

    /**
     * list 转 数组
     *
     * @param list list
     * @return array
     */
    public static String[] listToStringArr(List<?> list) {
        String[] strArray = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strArray[i] = list.get(i).toString();
        }
        return strArray;
    }

    /**
     * list 转 数组
     *
     * @param list list
     * @return array
     */
    public static Object[] listToObjectArr(List<?> list) {
        Object[] array = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = (Object) list.get(i);
        }
        return array;
    }

    /**
     * 字符串数组转list
     *
     * @param stringArr 数组
     * @return list
     */
    @SuppressWarnings("unchecked")
    public static List<String> stringArrToList(String[] stringArr) {
        return (stringArr.length > 0) ? Arrays.asList(stringArr) : Collections.EMPTY_LIST;
    }

    /**
     * 从头部截取 n 个元素
     *
     * @param target   目标
     * @param itemSize 截取的数量
     * @param skipSize 跳过的数量
     * @author 0neBean
     * @since 2022/5/11 11:03
     */
    public static <T> List<T> cutFromStart(List<T> target, Integer itemSize, Integer skipSize) {
        List<T> result = new ArrayList<>();

        // 确保跳过的数量不会超出列表的范围
        int startIndex = Math.min(skipSize, target.size());

        for (int i = startIndex; i < target.size() && i < startIndex + itemSize; ++i) {
            result.add(target.get(i));
        }

        return result;
    }

    /**
     * 从头部截取 n 个元素
     *
     * @param target   目标
     * @param itemSize 截取的数量
     * @author 0neBean
     * @since 2022/5/11 11:03
     */
    public static <T> List<T> cutFromStart(List<T> target, Integer itemSize) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < target.size(); i++) {
            if (i > (itemSize - 1)) {
                break;
            }
            result.add(target.get(i));
        }
        return result;
    }

    /**
     * 从末尾截取 n 个元素
     *
     * @param target   目标
     * @param itemSize 截取的数量
     * @return java.util.List<T>
     * @author 0neBean
     * @since 2022/5/11 11:03
     */
    public static <T> List<T> cutFromEnd(List<T> target, Integer itemSize) {
        List<T> result = new ArrayList<>(target);
        Collections.reverse(result);
        return cutFromStart(result, itemSize);
    }

    /**
     * 是否包含元素
     *
     * @param array 数组
     * @param item  元素
     * @param <T>   泛型
     * @return bool
     */
    public static <T> boolean contains(T[] array, T item) {
        for (T t : array) {
            if (Objects.equals(t, item)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 获取两个集合a不在集合b中的元素
     *
     * @param a list a
     * @param b list b
     * @return 有差异的部分
     */
    public static List<String> aNotInb(List<String> a, List<String> b) {
        Set<String> set1 = new HashSet<>(a);
        Set<String> set2 = new HashSet<>(b);

        // 计算 list1 中不在 list2 中的元素
        set1.removeAll(set2);

        return new ArrayList<>(set1);
    }

    /**
     * 分片list
     *
     * @param chunkSize          分片个数
     * @param targetList         目标list
     * @param subListEachHandler 遍历逻辑
     * @param <T>                泛型类型
     */
    public static <T> void cutEachSubList(int chunkSize, List<T> targetList, SerializableConsumer<List<T>> subListEachHandler) {
        if (CollectionUtil.isEmpty(targetList)) {
            return;
        }
        for (int i = 0; i < targetList.size(); i += chunkSize) {
            List<T> sublist = targetList.subList(i, Math.min(i + chunkSize, targetList.size()));
            subListEachHandler.accept(sublist);
        }
    }
}
