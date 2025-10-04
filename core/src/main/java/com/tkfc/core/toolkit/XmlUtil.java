package com.tkfc.core.toolkit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * xml 操作工具类
 *
 * @author 0neBean
 */
public class XmlUtil {

    private static final String PREFIX_XML = "<xml>";
    private static final String SUFFIX_XML = "</xml>";
    private static final String PREFIX_CDATA = "<![CDATA[";
    private static final String SUFFIX_CDATA = "]]>";

    /**
     * 从request 中获取 json
     *
     * @param request http request
     * @return com.alibaba.fastjson2.JSONObject
     * @author 0neBean
     * @since 2021-12-06 00:09:35
     */
    public static JSONObject getJsonFromRequestXml(HttpServletRequest request) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            InputStream inputStream = request.getInputStream();
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element element : elementList) {
                jsonObject.put(element.getName(), element.getText());
            }
            inputStream.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /**
     * 转化成xml, 单层无嵌套
     *
     * @param param      map
     * @param isAddXmlStarterMark 添加专业
     * @return xml str
     */
    public static String mapToXml(Map<String, Object> param, boolean isAddXmlStarterMark) {
        return StringUtil.isEmpty(param) ? "" : iterMap(param, isAddXmlStarterMark);
    }

    /**
     * 转换一个xml格式的字符串到json格式
     *
     * @param xml xml格式的字符串
     * @return 成功返回json 格式的字符串;失败反回null
     */
    public static JSONObject xmlToJson(String xml) {
        JSONObject obj = new JSONObject();
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            obj.put(root.getName(), iterateElement(root));
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 一个迭代方法
     *
     * @param element 元素
     * @return java.util.Map 实例
     */
    @SuppressWarnings("all")
    private static Map iterateElement(Element element) {
        List jiedian = element.elements();
        Element et = null;
        Map obj = new HashMap();
        Object temp;
        List list = null;
        for (int i = 0; i < jiedian.size(); i++) {
            list = new LinkedList();
            et = (Element) jiedian.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.elements().size() == 0)
                    continue;
                if (obj.containsKey(et.getName())) {
                    temp = obj.get(et.getName());
                    if (temp instanceof List) {
                        list = (List) temp;
                        list.add(iterateElement(et));
                    } else if (temp instanceof Map) {
                        list.add((HashMap) temp);
                        list.add(iterateElement(et));
                    } else {
                        list.add((String) temp);
                        list.add(iterateElement(et));
                    }
                    obj.put(et.getName(), list);
                } else {
                    obj.put(et.getName(), iterateElement(et));
                }
            } else {
                if (obj.containsKey(et.getName())) {
                    temp = obj.get(et.getName());
                    if (temp instanceof List) {
                        list = (List) temp;
                        list.add(et.getTextTrim());
                    } else if (temp instanceof Map) {
                        list.add((HashMap) temp);
                        list.add(iterateElement(et));
                    } else {
                        list.add((String) temp);
                        list.add(et.getTextTrim());
                    }
                    obj.put(et.getName(), list);
                } else {
                    obj.put(et.getName(), et.getTextTrim());
                }

            }

        }
        return obj;
    }

    /**
     * 转化成xml, 单层无嵌套
     *
     * @param jsonObject json
     * @param isAddXmlStarterMark 添加专业
     * @return xml str
     */
    public static String jsonToXml(JSONObject jsonObject, boolean isAddXmlStarterMark) {
        LinkedHashMap<String, Object> jsonMap = JSON.parseObject(JSON.toJSONString(jsonObject), new TypeReference<>() {
        });
        return StringUtil.isEmpty(jsonMap) ? "" : iterMap(jsonMap, isAddXmlStarterMark);
    }

    /**
     * 迭代map
     *
     * @param jsonMap    键值对
     * @param isAddXmlStarterMark 添加xml的起始符
     * @return java.lang.String
     * @author 0neBean
     * @since 2021-12-06 00:10:20
     */
    private static String iterMap(Map<String, Object> jsonMap, boolean isAddXmlStarterMark) {
        StringBuilder stringBuilder = new StringBuilder(PREFIX_XML);
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            stringBuilder.append("<").append(entry.getKey()).append(">");
            if (isAddXmlStarterMark) {
                stringBuilder.append(PREFIX_CDATA);
                if (null != entry.getValue()) {
                    stringBuilder.append(entry.getValue());
                }
                stringBuilder.append(SUFFIX_CDATA);
            } else {
                if (null != entry.getValue()) {
                    stringBuilder.append(entry.getValue());
                }
            }
            stringBuilder.append("</").append(entry.getKey()).append(">");
        }
        return stringBuilder.append(SUFFIX_XML).toString();
    }


    /**
     * 将xml字符串转换成json
     *
     * @param xml xm字符串
     * @return JSONObject
     */
    public static JSONObject json2Map(String xml) {
        JSONObject map = new JSONObject();
        Document doc;
        try {
            // 将字符串转为XML
            doc = DocumentHelper.parseText(xml);
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 获取根节点下所有节点
            List<Element> list = rootElt.elements();
            // 遍历节点
            for (Element element : list) {
                // 节点的name为map的key，text为map的value
                map.put(element.getName(), element.getText());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }
}
