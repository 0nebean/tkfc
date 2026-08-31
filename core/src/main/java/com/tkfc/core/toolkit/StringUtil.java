package com.tkfc.core.toolkit;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.constants.StringPool;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * <p>
 * 该类封装了字符串类型数据的常用方法，该类中的方法均为静态方法。
 */
public class StringUtil {

    @SuppressWarnings("all")
    private final static String BASE_RANDOM_STRING = "_abcdefghijklmnopqrstuvwxyz0123456789";

    // 正则表达式，用于匹配合法的邮箱地址
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,}$";

    // 创建 Pattern 对象
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // 预编译正则表达式，提升多次调用的性能
    // 1. 匹配连续的<br>标签（兼容大小写，忽略标签间空白/换行）
    private static final Pattern CONSECUTIVE_BR_PATTERN = Pattern.compile("(<br\\s*/?>\\s*)+", Pattern.CASE_INSENSITIVE);
    // 2. 匹配一个或多个换行符（\n、\r、\r\n 都包含），并清理换行符前后的空白
    private static final Pattern CONSECUTIVE_NEWLINE_PATTERN = Pattern.compile("\\s*[\r\n]+\\s*");
    // 3. 匹配一个或多个空白字符（空格、制表符等），但不包含换行符（避免影响已处理的换行）
    private static final Pattern CONSECUTIVE_SPACE_PATTERN = Pattern.compile("[ \\t]+");

    /**
     * 判断邮箱是否合法
     *
     * @param email 邮箱
     * @return bool
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email 邮箱
     * @return bool
     */
    public static boolean notValidEmail(String email) {
        return !isValidEmail(email);
    }

    /**
     * 是否是字符串
     *
     * @param target 目标对象
     * @return bool
     */
    public static Boolean isString(Object target) {
        return target instanceof CharSequence;
    }


    /**
     * 一个字符包含另一个字符的count
     *
     * @param target 目标字符
     * @param chars  包含的字符
     * @return count
     */
    public static Integer countChars(String target, String chars) {
        int count = 0;
        int index = target.indexOf(chars);
        while (index != -1) {
            count++;
            index = target.indexOf(chars, index + chars.length());
        }
        return count;
    }

    /**
     * 首字母转小写
     *
     * @param s 参数
     * @return 字符串
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return 字符
     */
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(BASE_RANDOM_STRING.length());
            sb.append(BASE_RANDOM_STRING.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 将带有下划线的字符串转换成驼峰写法
     *
     * @param str 参数
     * @return 字符串
     */
    public static String replaceUnderLineToClassNameCase(String str) {
        if (isEmpty(str)) {
            return StringPool.EMPTY;
        }
        String[] parts = str.split(StringPool.UNDERSCORE);
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (isEmpty(part)) {
                continue;
            }
            String lowerPart = part.toLowerCase(Locale.ROOT);
            result.append(Character.toUpperCase(lowerPart.charAt(0)));
            if (lowerPart.length() > 1) {
                result.append(lowerPart.substring(1));
            }
        }
        return result.toString();
    }


    /**
     * 驼峰命名转为下划线命名
     *
     * @param para 驼峰命名的字符串
     * @return 字符串
     */
    public static String camelCaseToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        //定位
        int temp = 0;
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i))) {
                sb.insert(i + temp, "_");
                temp += 1;
            }
        }
        return sb.toString().toLowerCase();
    }


    /**
     * 字符串连接时的分隔符
     * 该分隔符用于{@link #toString(Collection)} 和
     * {@link #toString(Collection, String)}方法。
     */
    public static final String DEFAULT_SEPARATOR = ",";

    /**
     * 检查当前字符串是否为空
     * 如果字符串为null，或者长度为0，都被归为空。
     *
     * @param str 要检查的字符串
     * @return 返回结果，true表示不为空，false表示为空
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.equals(StringPool.NULL)) {
            return true;
        }
        return str.trim().equals(StringPool.EMPTY);
    }

    /**
     * 检查当前字符串是否为空
     * 如果字符串为null，或者长度为0，都被归为空。
     *
     * @param str 要检查的字符串
     * @return 返回结果，true表示为空，false表示不为空
     */
    public static boolean isEmpty(Object str) {
        if (str == null) {
            return true;
        }
        return StringPool.EMPTY.equals(str.toString().trim());
    }

    /**
     * 检查当前字符串是否不为空
     *
     * @param str 要检查的字符串
     * @return 返回结果，true表示为空，false表示不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 检查当前字符串是否不为空
     *
     * @param str 要检查的字符串
     * @return 返回结果，true表示为空，false表示不为空
     */
    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    /**
     * 检查当前字符串是否为数字
     *
     * @param s 要检查的字符串
     * @return 返回结果
     */
    public static boolean isNumberic(String s) {
        if (StringUtil.isEmpty(s)) {
            return false;
        }
        boolean rtn = validByRegex("^[-+]{0,1}\\d*\\.{0,1}\\d+$", s);
        if (rtn) {
            return true;
        }

        return validByRegex("^0[x|X][\\da-eA-E]+$", s);
    }

    /**
     * 检查当前字符串是否符合正则表达式
     *
     * @param regex 正则表达式
     * @param input 要检查的字符串
     * @return 返回结果
     */
    public static boolean validByRegex(String regex, String input) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher regexMatcher = p.matcher(input);
        return regexMatcher.find();
    }


    /**
     * 检查当前字符串是否为空
     * 如果字符串为null，或者调用 ava.lang.String.trim 后长度为0，都被归为空。
     *
     * @param str 要检查的字符串
     * @return 检查结果，true 为空，false不为空
     */
    public static boolean isTrimEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }


    /**
     * 替换字符串中的字符,该方法用于velocity层，只替换第一次匹配
     *
     * @param str   被替换的原始字符串
     * @param regex 替换的字符
     * @param value 替换的值
     * @return 替换结果
     */
    public static String replace(String str, String regex, String value) {
        // 检查是否为空
        if (StringUtil.isTrimEmpty(str)) {
            return str;
        }
        return str.replace(regex, value);
    }

    /**
     * 替换字符串中的字符,该方法用于velocity层，替换所有匹配
     *
     * @param str   被替换的原始字符串
     * @param regex 替换的字符
     * @param value 替换的值
     * @return 替换结果
     */
    public static String replaceAll(String str, String regex, String value) {
        // 检查是否为空
        if (StringUtil.isTrimEmpty(str)) {
            return str;
        }
        return str.replaceAll(regex, value);
    }


    /**
     * 提换html的部分特殊字符 and符号 尖括号
     *
     * @param str 要替换的字符串
     * @return 替换结果
     */
    public static String restoreHtml(String str) {
        // 检查是否为空
        if (StringUtil.isTrimEmpty(str)) {
            return str;
        }
        // 替换特殊字符串
        str = str.replaceAll("&amp;", "&");
        str = str.replaceAll("&lt;", "<");
        str = str.replaceAll("&gt;", ">");
        str = str.replaceAll("&quot;", "\"");
        str = str.replaceAll("&nbsp;", " ");
        return str;
    }


    /**
     * 提换html的部分特殊字符 and符号 尖括号
     *
     * @param str 要替换的字符串
     * @return 替换结果
     */
    public static String formatHtml(String str) {
        // 检查是否为空
        if (StringUtil.isTrimEmpty(str)) {
            return str;
        }
        // 替换特殊字符串
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        return str;
    }

    /**
     * 替换HTML的全部特殊字符
     * 替换了and符号 尖括号 空格
     *
     * @param str 要替换的字符串
     * @return 替换的结果
     */
    public static String formatAllHtml(String str) {
        // 检查是否为空
        if (StringUtil.isTrimEmpty(str)) {
            return str;
        }
        // 替换特殊字符串
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll(" ", "&nbsp;");
        return str;
    }


    /**
     * 将string 集合拼接成字符串，使用{@value #DEFAULT_SEPARATOR}分隔
     *
     * @param list 要处理的集合
     * @return 处理结果
     */
    public static String toString(Collection<String> list) {
        // 检查list是否存在
        if (list == null) {
            return null;
        }
        StringBuilder rs = new StringBuilder();
        Iterator<String> it = list.iterator();
        String next;
        while (it.hasNext()) {
            next = it.next();
            if (next == null) {
                continue;
            }
            rs.append(next);
            // 如果有下一个值，则添加分隔符
            if (it.hasNext()) {
                rs.append(DEFAULT_SEPARATOR);
            }
        }
        return rs.toString();
    }

    /**
     * 将string 集合拼接成字符串，使用特定字符分隔
     *
     * @param list      要处理的集合
     * @param separator 分隔符，如果为null，则默认使用{@value #DEFAULT_SEPARATOR}
     * @return 处理结果
     */
    public static String toString(Collection<String> list, String separator) {
        if (separator == null) {
            separator = DEFAULT_SEPARATOR;
        }
        // 检查list是否存在
        if (list == null) {
            return null;
        }
        StringBuilder rs = new StringBuilder();
        Iterator<String> it = list.iterator();
        String next;
        while (it.hasNext()) {
            next = it.next();
            if (next == null) {
                continue;
            }
            // 如果有下一个值，则添加分隔符
            if (it.hasNext()) {
                rs.append(separator);
            }
        }
        return rs.toString();
    }


    /**
     * toString 可读的输出
     *
     * @param obj       target
     * @param autoQuote 分隔符
     * @return java.lang.String
     * @author 0neBean
     * @since 2021-12-17 19:20:44
     */
    public static String toString(Object obj, boolean autoQuote) {
        StringBuilder sb = new StringBuilder();
        if (obj == null) {
            sb.append(StringPool.NULL);
        } else if ((obj instanceof Object[])) {
            for (int i = 0; i < ((Object[]) obj).length; i++) {
                sb.append(((Object[]) obj)[i]).append(", ");
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }
        } else {
            sb.append(obj);
        }

        if ((autoQuote)
                && (sb.length() > 0)
                && ((sb.charAt(0) != '[') || (sb.charAt(sb.length() - 1) != ']'))
                && ((sb.charAt(0) != '{') || (sb.charAt(sb.length() - 1) != '}'))) {
            sb.insert(0, "[").append("]");
        }
        return sb.toString();
    }


    /**
     * 判断是否为中文
     *
     * @param c 字符
     * @return 结果
     */
    public static boolean isChinese(char c) {
        // 汉字范围 \u4e00-\u9fa5 (中文)
        return c >= 19968;
    }

    /**
     * 判断字符串是否只包含英文字符
     *
     * @param input 需要判断的字符串
     * @return 如果字符串只包含英文字符返回true，否则返回false
     */
    public static boolean isEnglish(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        // 正则表达式匹配任何英文字符（包括大小写字母和空格）
        String regex = ".*[a-zA-Z].*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        // 检查是否匹配
        return matcher.matches();
    }

    /**
     * 判断是否数字
     *
     * @param ch 字符
     * @return 结果
     */
    public static boolean isNumeric(char ch) {
        return Character.isDigit(ch);
    }

    /**
     * 是否为有效的UTF8字符
     *
     * @param rawtext 字节数组
     * @return 结果
     */
    public static boolean isUTF8(byte[] rawtext) {
        int score = 0;
        int i, rawtextlen = 0;
        int goodbytes = 0, asciibytes = 0;
        // Maybe also use UTF8 Byte Order Mark: EF BB BF
        // Check to see if characters fit into acceptable ranges
        rawtextlen = rawtext.length;
        for (i = 0; i < rawtextlen; i++) {
            // -0x40~-0x21
            // Two bytes
            if ((rawtext[i] & (byte) 0x7F) == rawtext[i]) {
                // 最高位是0的ASCII字符
                asciibytes++;
                // Ignore ASCII, can throw off count
            } else // Three bytes
                if (-64 <= rawtext[i] && rawtext[i] <= -33 && i + 1 < rawtextlen && rawtext[i + 1] <= -65) {
                    goodbytes += 2;
                    i++;
                } else if (-32 <= rawtext[i] && rawtext[i] <= -17 && i + 2 < rawtextlen && rawtext[i + 1] <= -65 && rawtext[i + 2] <= -65) {
                    goodbytes += 3;
                    i += 2;
                }
        }
        if (asciibytes == rawtextlen) {
            return false;
        }
        score = 100 * goodbytes / (rawtextlen - asciibytes);
        // If not above 98, reduce to zero to prevent coincidental matches
        // Allows for some (few) bad formed sequences
        if (score > 98) {
            return true;
        } else {
            return score > 95 && goodbytes > 30;
        }
    }


    /**
     * 判断是否为数字
     *
     * @param str 字符串
     * @return 结果
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证是否为手机号
     *
     * @param mobiles 手机号
     * @return 结果
     */
    public static boolean isMobile(String mobiles) {
        if (StringUtil.isNotEmpty(mobiles)) {
            Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            return m.matches();
        }
        return false;
    }

    /**
     * 加密手机号
     *
     * @param phoneNumber 手机号
     * @return 结果
     */
    public static String encryptionPhoneNumber(String phoneNumber) {
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }


    /**
     * 截断显示在首页文章内容的数据
     *
     * @param content 文章内容
     * @param length  截断长度
     * @return 截断后内容
     */
    public static String setSummary(String content, Integer length) {
        // 由于直接拿前70个字符，如果前70个字符中包含图片，造成前台页面显示不完整
        // 现对前70字符有图片的做特殊处理
        // 先判断该文章内容中是否包含图片
        if (StringUtil.isBlank(content)) {
            return StringPool.EMPTY;
        }
        content = content.replaceAll("<[^>]+>", StringPool.EMPTY);
        if (StringUtil.isNotBlank(content) && content.length() > length) {
            content = content.substring(0, length) + "...";
        }
        return content;
    }


    /**
     * 是否空白字符
     * Checks if a String is whitespace, empty (StringPool.EMPTY) or null.
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank(StringPool.EMPTY)        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     *
     * @param str the String to check, may be null
     * @return true if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a String is not empty (StringPool.EMPTY), not null and not whitespace only.
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank(StringPool.EMPTY)        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     *
     * @param str the String to check, may be null
     * @return true if the String is not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        return !StringUtil.isBlank(str);
    }

    /**
     * emoji表情替换
     *
     * @param text 内容
     * @return 字符
     */
    public static String filterEmojiChars(String text) {
        // 定义一个正则表达式，匹配emoji和字体图标
        String emojiRegex = "[\\x{1F600}-\\x{1F64F}\\x{1F300}-\\x{1F5FF}\\x{1F680}-\\x{1F6FF}\\x{1F700}-\\x{1F77F}\\x{1F780}-\\x{1F7FF}\\x{1F800}-\\x{1F8FF}\\x{1F900}-\\x{1F9FF}\\x{1FA00}-\\x{1FA6F}\\x{1FA70}-\\x{1FAFF}\\x{2600}-\\x{26FF}\\x{2700}-\\x{27BF}\\x{2300}-\\x{23FF}\\x{2B50}]";
        // 使用正则替换所有emoji和字体图标
        Pattern pattern = Pattern.compile(emojiRegex, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(StringPool.EMPTY);
    }

    public static String toStrTrim(Object obj) {
        return obj == null ? StringPool.EMPTY : obj.toString().trim();
    }


    /**
     * 版本号比较
     *
     * @param v1 版本1
     * @param v2 版本2
     * @return 0代表相等，1代表左边大，-1代表右边大
     */
    public static int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }


    /**
     * 目标字符串中包含多少次字符串
     *
     * @param tagStr      tag标签
     * @param containsStr 容器字符
     * @return 次数
     */
    public static Integer containsCount(String tagStr, String containsStr) {
        int i = 0;
        while (tagStr.indexOf(containsStr) > 0) {
            i++;
            tagStr = tagStr.replaceFirst(containsStr, StringPool.EMPTY);
        }
        return i;
    }

    /**
     * 去除字符串外的数组括号
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String cleanArrayStr(String str) {
        if (str.startsWith("[") && str.endsWith("]")) {
            str = str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * 是否包含 忽略大消息
     *
     * @param str       字符
     * @param searchStr 目标
     * @return bool
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 连接字符串
     *
     * @param chars 字符 可变参数
     * @return string
     */
    public static String concat(String... chars) {
        StringBuilder stringBuilder = new StringBuilder();
        if (Objects.nonNull(chars)) {
            for (String c : chars) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 创建redis key
     *
     * @param keys keys
     * @return redis key
     */
    public static String buildRedisKey(String... keys) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : keys) {
            stringBuilder.append(s).append(StringPool.COLON);
        }
        String s = stringBuilder.toString();
        return s.substring(0, s.length() - 1);
    }

    /**
     * 替换目标中的变量
     *
     * @param target 命令
     * @param param  流程参数
     * @return 替换后的命令
     */
    public static String replaceExpression(String target, JSONObject param) {
        String result = target;
        Matcher m = Pattern.compile("\\$\\{(.*?)}").matcher(target);
        while (m.find()) {
            String variable = m.group(0);
            String variableKey = m.group(1);
            if (param.containsKey(variableKey)) {
                result = result.replace(variable, param.get(variableKey).toString());
            }
            if (variableKey.contains(StringPool.DOT) || (variableKey.startsWith(StringPool.LEFT_SQ_BRACKET) && variableKey.endsWith(StringPool.RIGHT_SQ_BRACKET))) {
                String value = JsonUtil.map(param, String.class, variableKey.split("\\."));
                if (StringUtil.isNotBlank(value)) {
                    result = result.replace(variable, value);
                }
            }
        }
        return result;
    }

    /**
     * 获取指定的字符串
     *
     * @param target  命令
     * @param regular 正则
     * @return 替换后的命令
     */
    public static List<String> getSpecifyChar(String target, String regular) {
        List<String> result = new ArrayList<>();
        Matcher m = Pattern.compile(regular).matcher(target);
        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }

    /**
     * 字符串转list
     *
     * @param listString list 字符串
     * @param regex      分隔符
     * @return list
     */
    public static List<String> join(String listString, String regex) {
        List<String> result = new ArrayList<>();
        String[] split = listString.split(regex);
        for (String str : split) {
            if (isNotBlank(str)) {
                result.add(str);
            }
        }
        return result;
    }

    /**
     * 过滤掉阿拉伯语字符
     *
     * @param str 目标字符
     * @return 过滤后的字符
     */
    public static String filterArab(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int ch = str.charAt(i);
            int min = Integer.parseInt("060C", 16);
            int max = Integer.parseInt("06FE", 16);
            boolean notArab = ch >= min && ch <= max;
            if (!notArab) {
                sb.append((char) ch);
            }
        }
        return sb.toString();
    }

    /**
     * 是否已指定后缀结尾
     *
     * @param str    目标字符
     * @param suffix 后缀
     * @return bool
     */
    public static boolean endsWith(CharSequence str, CharSequence suffix) {
        return StringUtils.endsWith(str, suffix);
    }

    /**
     * 是否已指定后缀结尾 忽略大小写
     *
     * @param str    目标字符
     * @param suffix 后缀
     * @return bool
     */
    public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return StringUtils.endsWith(str, suffix);
    }

    /**
     * 截取指定字符之前的字符
     *
     * @param str       目标字符
     * @param separator 分隔符
     * @return bool
     */
    public static String substringBefore(String str, String separator) {
        return StringUtils.substringBefore(str, separator);
    }

    /**
     * 从 String 的末尾去除一组字符中的任何一个
     *
     * @param str        目标字符
     * @param stripChars 分隔符
     * @return bool
     */
    public static String stripEnd(final String str, final String stripChars) {
        return StringUtils.stripEnd(str, stripChars);
    }

    /**
     * 从 String 的开头去除一组字符中的任何一个
     *
     * @param str        目标字符
     * @param stripChars 分隔符
     * @return bool
     */
    public static String stripStart(final String str, final String stripChars) {
        return StringUtils.stripStart(str, stripChars);
    }

    /**
     * 源字符是否包含搜索字符的任何字符
     *
     * @param cs          源字符
     * @param searchChars 搜索字符
     * @return bool
     */
    public static boolean containsAny(final CharSequence cs, final CharSequence searchChars) {
        return StringUtils.containsAny(cs, searchChars);
    }

    /**
     * 源字符是否包含字符
     *
     * @param seq       源字符
     * @param searchSeq 搜索字符
     * @return bool
     */
    public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        return StringUtils.contains(seq, searchSeq);
    }

    /**
     * 判断字符串是否为有效的IP地址
     *
     * @param ipAddress 待检查的IP地址字符串
     * @return true表示是IP地址，false表示不是IP地址
     */
    public static boolean isIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }

        // 移除端口号（如果存在）
        String cleanIp = ipAddress.split(":")[0];

        // IP地址正则表达式
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern ipPattern = Pattern.compile(ipRegex);

        return ipPattern.matcher(cleanIp).matches();
    }

    /**
     * 根据起始索引和指定长度截取字符串
     * @param str 原字符串
     * @param beginIndex 起始索引（从0开始）
     * @param length 要截取的长度
     * @return 截取后的子串，参数非法时返回空字符串
     */
    public static String substringByLength(String str, int beginIndex, int length) {
        // 边界检查：原字符串为空、起始索引非法、长度小于等于0，直接返回空字符串
        if (str == null || str.isEmpty() || beginIndex < 0 || beginIndex >= str.length() || length <= 0) {
            return "";
        }

        // 计算结束索引：起始索引 + 长度
        int endIndex = beginIndex + length;
        // 确保结束索引不超过字符串长度（避免越界）
        endIndex = Math.min(endIndex, str.length());

        // 调用原生substring方法截取
        return str.substring(beginIndex, endIndex);
    }

    /**
     * 统一格式化文本：
     * 1. 合并连续的<br>标签为单个<br>
     * 2. 合并多个换行符为一个\n
     * 3. 合并连续的多个空格/制表符为单个空格
     * @param originalText 原始文本（可包含HTML标签、换行符、多空格）
     * @return 格式化后的文本
     */
    public static String collapseConsecutiveNewLineAndSpace(String originalText) {
        // 边界处理：如果输入为空，直接返回原内容
        if (originalText == null || originalText.isEmpty()) {
            return originalText;
        }

        String processedText = originalText;
        // 第一步：合并连续的<br>标签为单个<br>
        processedText = CONSECUTIVE_BR_PATTERN.matcher(processedText).replaceAll("<br>");
        // 第二步：合并多个换行符（含前后空白）为单个换行符\n
        processedText = CONSECUTIVE_NEWLINE_PATTERN.matcher(processedText).replaceAll("\n");
        // 第三步：将连续的空格/制表符缩减为单个空格
        processedText = CONSECUTIVE_SPACE_PATTERN.matcher(processedText).replaceAll(" ");
        return processedText;
    }

    /**
     * 将所有换行符替换为空格，并合并连续的空格为单个空格
     * @param originalText 原始文本
     * @return 处理后的文本
     */
    public static String replaceNewLineWithSpace(String originalText) {
        // 边界处理：如果输入为空，直接返回原内容
        if (originalText == null || originalText.isEmpty()) {
            return originalText;
        }

        String processedText = originalText;
        // 第一步：将所有换行符（\n, \r\n, \r）替换为空格
        processedText = processedText.replaceAll("\\r\\n|\\r|\\n", " ");
        // 第二步：将连续的空格/制表符缩减为单个空格
        processedText = CONSECUTIVE_SPACE_PATTERN.matcher(processedText).replaceAll(" ");
        return processedText;
    }
}
