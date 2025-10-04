package com.tkfc.core.toolkit;

import com.alibaba.fastjson2.*;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.base.Assert;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * json 工具类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/14 17:05
 */
public class JsonUtil {

    /**
     * 是否是json
     *
     * @param content 字符串
     * @return bool
     */
    public static boolean isJson(String content) {
        if (StringUtil.isBlank(content)) {
            return false;
        }
        try {
            toBean(content, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否是json
     *
     * @param content 字符串
     * @return bool
     */
    public static boolean isJsonObjectStr(String content) {
        return isJson(content) && content.startsWith(StringPool.LEFT_BRACE);
    }

    /**
     * 是否是json
     *
     * @param content 字符串
     * @return bool
     */
    public static boolean isJsonArrayStr(String content) {
        return isJson(content) && content.startsWith(StringPool.LEFT_SQ_BRACKET);
    }

    /**
     * 是否不是json
     *
     * @param content 字符串
     * @return bool
     */
    public static boolean isNotJson(String content) {
        return !isJson(content);
    }

    /**
     * 转json
     *
     * @param obj 对象
     * @return json
     */
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.WriteNonStringKeyAsString);
    }

    /**
     * 转json 不允许循环引用
     *
     * @param obj 对象
     * @return json
     */
    public static String toJsonWithReferenceDetection(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.ReferenceDetection, JSONWriter.Feature.WriteNonStringKeyAsString);
    }

    /**
     * 转json
     *
     * @param obj 对象
     * @return json
     */
    public static String toJsonWithFeatures(Object obj, JSONWriter.Feature... features) {
        return JSON.toJSONString(obj, features);
    }


    /**
     * 转json对象
     *
     * @param obj 对象
     * @return json对象
     */
    public static JSONObject toJsonObject(Object obj) {
        if (obj instanceof CharSequence && isJson(obj.toString())) {
            return JSON.parseObject(obj.toString());
        }
        return JSON.parseObject(toJson(obj));
    }


    /**
     * 转json对象
     *
     * @param obj 对象
     * @return json对象
     */
    public static JSONArray toJsonArray(Object obj) {
        if (obj instanceof CharSequence && isJson(obj.toString())) {
            return JSON.parseArray(obj.toString());
        }
        return JSON.parseArray(toJson(obj));
    }

    /**
     * json string转json对象
     *
     * @param jsonStr jsonStr
     * @return json对象
     */
    public static JSONObject jsonStringToJsonObject(Object jsonStr) {
        return JSONObject.parseObject(jsonStr.toString());
    }

    /**
     * json string转json数组
     *
     * @param jsonStr jsonStr
     * @return json对象
     */
    public static JSONArray jsonStringToJsonArray(Object jsonStr) {
        return JSONArray.parseArray(jsonStr.toString());
    }

    /**
     * 转json对象
     *
     * @param map 对象
     * @return json对象
     */
    public static JSONObject toJsonObject(MultiValueMap<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(map);
        return jsonObject;
    }

    /**
     * 转json对象
     *
     * @param json byte数组
     * @return json对象
     */
    public static JSONObject toJsonObject(byte[] json) {
        return JSONObject.parseObject(new String(json, StandardCharsets.UTF_8));
    }


    /**
     * 转对象
     *
     * @param json json字符
     * @param clz  类型
     * @param <T>  泛型
     * @return 对象
     */
    public static <T> T toBean(String json, Class<T> clz) {
        return JSON.parseObject(json, clz);
    }

    /**
     * 转对象
     *
     * @param json json字符
     * @param type 类型
     * @param <T>  泛型
     * @return 对象
     */
    public static <T> T toBean(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }


    /**
     * 转对象
     *
     * @param json json字符
     * @param clz  类型
     * @param <T>  泛型
     * @return 对象
     */
    public static <T> T toBean(byte[] json, Class<T> clz) {
        return toBean(new String(json, StandardCharsets.UTF_8), clz);
    }

    /**
     * 转对象
     *
     * @param json json字符
     * @param type 类型
     * @param <T>  泛型
     * @return 对象
     */
    public static <T> T toBean(byte[] json, TypeReference<T> type) {
        return toBean(new String(json, StandardCharsets.UTF_8), type);
    }

    /**
     * 复制对象
     *
     * @param object 对象
     * @param clazz  类型
     * @param <T>    泛型
     * @return object
     */
    public static <T> T copyObject(Object object, Class<T> clazz) {
        return JSON.parseObject(toJson(object), clazz);
    }

    /**
     * 复制泛型List
     *
     * @param objects 对象
     * @param clazz   类型
     * @param <T>     泛型
     * @return object
     */
    public static <T> List<T> copyList(List<?> objects, Class<T> clazz) {
        return JSON.parseArray(toJson(objects), clazz);
    }

    /**
     * 复制泛型List
     *
     * @param objects 对象
     * @param clazz   类型
     * @param <T>     泛型
     * @return object
     */
    public static <T> T copyList(List<?> objects, TypeReference<T> clazz) {
        return JSON.parseObject(toJson(objects), clazz);
    }

    // 入口方法，处理 JSONObject

    /**
     * 给JSONObject属性设置值
     * <p>
     * 修改前: {"apple":{"app":[{"hua":{"gua":[{"hello":"world"}]}}]}}
     * 修改参数: mapValue(target, "win", "apple", "app[0]", "hua","gua[0]","hello");
     * 修改后:  {"apple":{"app":[{"hua":{"gua":[{"hello":"win"}]}}]}}
     *
     * @param target 目标对象
     * @param value  值
     * @param keys   keys
     */
    public static void mapValue(JSONObject target, Object value, String... keys) {
        modify(target, value, 0, keys);
    }

    /**
     * 给JSONArray属性设置值
     * <p>
     * 修改前: [{"apple":{"app":[{"hua":{"gua":[{"hello":"world"}]}}]}}]
     * 修改参数: mapValue(targetArray, "good","[0]", "apple", "app[0]", "hua","gua[0]","hello");
     * 修改后:  [{"apple":{"app":[{"hua":{"gua":[{"hello":"good"}]}}]}}]
     *
     * @param target 目标对象
     * @param value  值
     * @param keys   keys
     */
    public static void mapValue(JSONArray target, Object value, String... keys) {
        modify(target, value, 0, keys);
    }

    // 递归修改逻辑
    private static void modify(Object target, Object value, int depth, String... keys) {
        if (depth >= keys.length || target == null) {
            return;
        }
        String key = keys[depth];
        String arrayKey = null;
        int arrayIndex = -1;
        // 检查 key 是否包含数组索引
        if (key.contains("[") && key.contains("]")) {
            arrayKey = key.substring(0, key.indexOf("["));
            String indexStr = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
            arrayIndex = Integer.parseInt(indexStr);
        }

        if (target instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) target;
            if (arrayKey != null) {
                // 如果 key 是带有数组索引的形式，比如 "app[0]"
                JSONArray jsonArray = jsonObject.getJSONArray(arrayKey);
                if (jsonArray == null) {
                    jsonArray = new JSONArray();
                    jsonObject.put(arrayKey, jsonArray);
                }
                modify(jsonArray, value, depth, keys); // 递归到 JSONArray
            } else {
                // 普通 JSONObject 的 key 处理
                if (depth == keys.length - 1) {
                    jsonObject.put(key, value); // 最后一层直接修改值
                } else {
                    Object nextTarget = jsonObject.get(key);
                    if (nextTarget == null) {
                        nextTarget = keys[depth + 1].contains("[") ? new JSONArray() : new JSONObject();
                        jsonObject.put(key, nextTarget); // 创建下一层对象
                    }
                    modify(nextTarget, value, depth + 1, keys); // 递归到下一层
                }
            }
        } else if (target instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) target;
            if (arrayIndex != -1) {
                // 处理 JSONArray 中的元素
                if (depth == keys.length - 1) {
                    jsonArray.set(arrayIndex, value); // 最后一层直接修改值
                } else {
                    Object nextTarget = jsonArray.size() > arrayIndex ? jsonArray.get(arrayIndex) : null;
                    if (nextTarget == null) {
                        nextTarget = keys[depth + 1].contains("[") ? new JSONArray() : new JSONObject();
                        if (jsonArray.size() <= arrayIndex) {
                            while (jsonArray.size() <= arrayIndex) {
                                jsonArray.add(null); // 确保数组大小足够
                            }
                        }
                        jsonArray.set(arrayIndex, nextTarget); // 创建下一层对象
                    }
                    modify(nextTarget, value, depth + 1, keys); // 递归到下一层
                }
            }
        }
    }

    /**
     * 获取对象的值
     *
     * @param target    目标对象
     * @param valueType 值的类型
     * @param keys      值的keys
     * @param <T>       泛型类型
     * @return 获取的对象
     */
    public static <T> T map(JSONObject target, Class<T> valueType, String... keys) {
        Optional<Object> tempNode = mapJsonNode(target, keys);
        return tempNode.map(t -> toBean(toJson(t), valueType)).orElse(null);
    }

    /**
     * 获取对象的值
     *
     * @param target    目标对象
     * @param valueType 值的类型
     * @param keys      值的keys
     * @param <T>       泛型类型
     * @return 获取的对象
     */
    public static <T> T map(JSONObject target, TypeReference<T> valueType, String... keys) {
        Optional<Object> tempNode = mapJsonNode(target, keys);
        return tempNode.map(t -> toBean(toJson(t), valueType)).orElse(null);
    }


    /**
     * 遍历json节点
     *
     * @param target 目标对象
     * @param keys   值的keys
     * @return Optional<JSONObject>
     */
    private static Optional<Object> mapJsonNode(Object target, String... keys) {
        if (Objects.isNull(target)) {
            return Optional.empty();
        }
        Optional<Object> tempNode = Optional.of(target);
        for (String key : keys) {
            String finalKey = key;
            if (key.startsWith(StringPool.LEFT_SQ_BRACKET) && key.endsWith(StringPool.RIGHT_SQ_BRACKET)) {
                key = key.replace(StringPool.LEFT_SQ_BRACKET, StringPool.EMPTY);
                key = key.replace(StringPool.RIGHT_SQ_BRACKET, StringPool.EMPTY);
                Assert.isTrue(NumberUtil.isNum(key), String.format("[%s] is not legitimate", key));
                Assert.isTrue(target instanceof JSONArray, String.format("[%s] is not array", key));
                tempNode = tempNode.map(j -> ((JSONArray) j).get(ParseUtil.toInt(finalKey)));
            } else {
                Assert.isTrue(target instanceof JSONObject, String.format("[%s] is not json", key));
                tempNode = tempNode.map(j -> ((JSONObject) j).get(finalKey));
            }
        }
        return tempNode;
    }

    /**
     * 转json 并自动处理类型
     *
     * @param jsonString json 字符串
     * @return 自动类型对象
     */
    public static Object toJsonWithAutoType(String jsonString) {
        if (JsonUtil.isJsonArrayStr(jsonString)) {
            return JsonUtil.toJsonArray(jsonString);
        }
        if (JsonUtil.isJsonObjectStr(jsonString)) {
            return JsonUtil.toJsonObject(jsonString);
        }
        JSONObject result = new JSONObject();
        result.put("val", jsonString);
        return result;
    }

    /**
     * 获取json默认值 用户获取 toJsonWithAutoType 方法中的默认值
     *
     * @param jsonString json 字符串
     * @return 默认值
     */
    public static Object getAutoTypeWithDefaultValue(String jsonString) {
        if (JsonUtil.isJsonArrayStr(jsonString)) {
            return JsonUtil.toJsonArray(jsonString);
        }
        if (JsonUtil.isJsonObjectStr(jsonString)) {
            JSONObject resultObject = JsonUtil.toJsonObject(jsonString);
            if (resultObject.size() == 1 && resultObject.containsKey("val")) {
                return resultObject.get("val");
            } else {
                return resultObject;
            }
        }
        return null;
    }

    /**
     * json数组转成set
     *
     * @param jsonArray json 数组
     * @return set
     */
    public static Set<String> jsonArrayToSet(JSONArray jsonArray) {
        HashSet<String> result = new HashSet<>();
        if (Objects.isNull(jsonArray) || jsonArray.isEmpty()) {
            return result;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            result.add(jsonArray.getString(i));
        }
        return result;
    }
}
