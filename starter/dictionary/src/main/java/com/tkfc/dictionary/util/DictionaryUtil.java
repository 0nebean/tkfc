package com.tkfc.dictionary.util;

import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.dictionary.dto.DictionaryDto;

import java.util.*;


/**
 * 字典工具类
 *
 * @author 0neBean
 * @since 2021-03-01 17:50:22
 */
public class DictionaryUtil {


    /**
     * 静态字典
     */
    private static final Map<String, List<DictionaryDto>> dictionary = new HashMap<>();

    public static void init(List<DictionaryDto> list) {
        List<DictionaryDto> tempList;
        if (CollectionUtil.isNotEmpty(list)) {
            for (DictionaryDto d : list) {
                String groupVal = d.getGroupVal();
                if (dictionary.containsKey(groupVal)) {
                    tempList = dictionary.get(groupVal);
                } else {
                    tempList = new ArrayList<>();
                }
                tempList.add(d);
                dictionary.put(groupVal, tempList);
            }
        }
    }

    /**
     * 获取字典词组
     *
     * @param groupVal 字典组编码
     * @return List<DicDictionary>
     */
    public static List<DictionaryDto> getDicGroup(String groupVal) {
        if (dictionary.containsKey(groupVal)) {
            List<DictionaryDto> list = dictionary.get(groupVal);
            list.sort(Comparator.comparing(DictionaryDto::getSort));
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 获取字典数据
     *
     * @param groupVal 字典组编码
     * @param val      字典项编码
     * @return 字典项的含义
     */
    public static String dic(String groupVal, String val) {
        String result = "nullDic";
        if (dictionary.containsKey(groupVal)) {
            List<DictionaryDto> list = dictionary.get(groupVal);
            for (DictionaryDto dicDictionary : list) {
                if (Objects.equals(dicDictionary.getVal(), val)) {
                    result = dicDictionary.getDic();
                }
            }
        }
        return result;
    }

}
