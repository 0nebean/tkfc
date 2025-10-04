package com.tkfc.core.throwable.base;


import com.tkfc.core.throwable.AssertFailException;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 断言工具
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@SuppressWarnings("all")
public final class Assert {
    private static final String DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE = "[Assertion failed] - The value %s is not in the specified exclu sive range of %s to %s";
    private static final String DEFAULT_GT_EX_MESSAGE = "[Assertion failed] - The value %s must greater than specified value %s";
    private static final String DEFAULT_GE_EX_MESSAGE = "[Assertion failed] - The value %s must greater than or equals specified value %s";
    private static final String DEFAULT_LT_EX_MESSAGE = "[Assertion failed] - The value %s must less than specified value %s";
    private static final String DEFAULT_LE_EX_MESSAGE = "[Assertion failed] - The value %s must less than or equals specified value %s";
    private static final String DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE = "[Assertion failed] - The value %s is not in the specified inclusive range of %s to %s";
    private static final String DEFAULT_MATCHES_PATTERN_EX = "[Assertion failed] - The string %s does not match the pattern %s";
    private static final String DEFAULT_IS_NULL_EX_MESSAGE = "[Assertion failed] - this argument is required; it must not be null";
    private static final String DEFAULT_NOT_NULL_EX_MESSAGE = "[Assertion failed] - this argument must be null";
    private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "[Assertion failed] - this expression must be true";
    private static final String DEFAULT_IS_EQUALS_EX_MESSAGE = "[Assertion failed] - these two objects must be equals";
    private static final String DEFAULT_NOT_EQUALS_EX_MESSAGE = "[Assertion failed] - these two objects must not equals";
    private static final String DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE = "[Assertion failed] - The validated array contains null element at index: %d";
    private static final String DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE = "[Assertion failed] - The validated collection contains null element at index: %d";
    private static final String DEFAULT_NOT_BLANK_EX_MESSAGE = "[Assertion failed] - The validated character sequence is blank";
    private static final String DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE = "[Assertion failed] - The validated array is empty";
    private static final String DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE = "[Assertion failed] - The validated character sequence is empty";
    private static final String DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE = "[Assertion failed] - The validated collection is empty";
    private static final String DEFAULT_IS_EMPTY_COLLECTION_EX_MESSAGE = "[Assertion failed] - The validated collection not empty";
    private static final String DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE = "[Assertion failed] - The validated map is empty";
    private static final String DEFAULT_VALID_INDEX_ARRAY_EX_MESSAGE = "[Assertion failed] - The validated array index is invalid: %d";
    private static final String DEFAULT_VALID_INDEX_CHAR_SEQUENCE_EX_MESSAGE = "[Assertion failed] - The validated character sequence index is invalid: %d";
    private static final String DEFAULT_VALID_INDEX_COLLECTION_EX_MESSAGE = "[Assertion failed] - The validated collection index is invalid: %d";
    private static final String DEFAULT_IS_ASSIGNABLE_EX_MESSAGE = "[Assertion failed] - Cannot assign a %s to a %s";
    private static final String DEFAULT_IS_INSTANCE_OF_EX_MESSAGE = "[Assertion failed] - Expected type: %s, actual: %s";
    private static final String DEFAULT_IS_NOT_JSON_EX_MESSAGE = "[Assertion failed] - The string %s is not json";
    private static final String DEFAULT_THROW_SPECIFY_EXCEPTION_EX_MESSAGE = "[Assertion failed] - catch specify exception";
    private static final String DEFAULT_FAIL_EX_MESSAGE = "[Assertion failed] - app running got an error : %s";

    private Assert() {
    }

    public static void isJson(@Nullable String json) {
        if (JsonUtil.isNotJson(json)) {
            throw new AssertFailException(String.format(DEFAULT_IS_NOT_JSON_EX_MESSAGE, json));
        }
    }

    public static void isJson(@Nullable String json, String message) {
        if (JsonUtil.isNotJson(json)) {
            throw new AssertFailException(message);
        }
    }

    public static void isEquals(@Nullable Object o1, @Nullable Object o2, String message) {
        if (!Objects.equals(o1, o2)) {
            throw new AssertFailException(message);
        }
    }

    public static void isEquals(@Nullable Object o1, @Nullable Object o2, String message, Object... values) {
        if (!Objects.equals(o1, o2)) {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static void isEquals(@Nullable Object o1, @Nullable Object o2) {
        if (!Objects.equals(o1, o2)) {
            throw new AssertFailException(DEFAULT_IS_EQUALS_EX_MESSAGE);
        }
    }

    public static void notEquals(@Nullable Object o1, @Nullable Object o2, String message) {
        if (Objects.equals(o1, o2)) {
            throw new AssertFailException(message);
        }
    }

    public static void notEquals(@Nullable Object o1, @Nullable Object o2, String message, Object... values) {
        if (Objects.equals(o1, o2)) {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static void notEquals(@Nullable Object o1, @Nullable Object o2) {
        if (Objects.equals(o1, o2)) {
            throw new AssertFailException(DEFAULT_NOT_EQUALS_EX_MESSAGE);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new AssertFailException(message);
        }
    }

    public static void isTrue(boolean expression, String message, Object... values) {
        if (!expression) {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static void isTrue(boolean expression) {
        if (!expression) {
            throw new AssertFailException(DEFAULT_IS_TRUE_EX_MESSAGE);
        }
    }

    public static void notTrue(boolean expression, String message) {
        if (expression) {
            throw new AssertFailException(message);
        }
    }

    public static void notTrue(boolean expression, String message, Object... values) {
        if (expression) {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static void notTrue(boolean expression) {
        if (expression) {
            throw new AssertFailException(DEFAULT_IS_TRUE_EX_MESSAGE);
        }
    }


    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new AssertFailException(message);
        }
    }


    public static void notNull(@Nullable Object object, String message, Object... values) {
        if (object == null) {
            throw new AssertFailException(null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }


    public static void notNull(@Nullable Object object) {
        if (object == null) {
            throw new AssertFailException(DEFAULT_IS_NULL_EX_MESSAGE);
        }
    }

    public static void isNull(@Nullable Object object, String message) {
        if (object != null) {
            throw new AssertFailException(message);
        }
    }


    public static void isNull(@Nullable Object object, String message, Object... values) {
        if (object != null) {
            throw new AssertFailException(null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static void isNull(@Nullable Object object) {
        if (object != null) {
            throw new AssertFailException(DEFAULT_NOT_NULL_EX_MESSAGE);
        }
    }

    public static <T> T[] notEmpty(@Nullable T[] array, String message) {
        if (array != null && array.length != 0) {
            return array;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T> T[] notEmpty(@Nullable T[] array, String message, Object... values) {
        if (array != null && array.length != 0) {
            return array;
        } else {
            throw new AssertFailException(null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T> T[] notEmpty(@Nullable T[] array) {
        if (array != null && array.length != 0) {
            return array;
        } else {
            throw new AssertFailException(DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE);
        }
    }

    public static <T extends Collection<?>> T notEmpty(@Nullable T collection, String message) {
        if (collection != null && !collection.isEmpty()) {
            return collection;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T extends Collection<?>> T notEmpty(@Nullable T collection, String message, Object... values) {
        if (collection != null && !collection.isEmpty()) {
            return collection;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T extends Collection<?>> T notEmpty(@Nullable T collection) {
        if (collection != null && !collection.isEmpty()) {
            return collection;
        } else {
            throw new AssertFailException(DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE);
        }
    }

    public static <T extends Map<?, ?>> T notEmpty(@Nullable T map, String message) {
        if (map != null && !map.isEmpty()) {
            return map;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T extends Map<?, ?>> T notEmpty(@Nullable T map, String message, Object... values) {
        if (map != null && !map.isEmpty()) {
            return map;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T extends Map<?, ?>> T notEmpty(@Nullable T map) {
        if (map != null && !map.isEmpty()) {
            return map;
        } else {
            throw new AssertFailException(DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE);
        }
    }

    public static <T extends CharSequence> T notEmpty(@Nullable T chars, String message) {
        if (chars != null && chars.length() != 0) {
            return chars;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T extends CharSequence> T notEmpty(@Nullable T chars, String message, Object... values) {
        if (chars != null && chars.length() != 0) {
            return chars;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T extends CharSequence> T notEmpty(@Nullable T chars) {
        if (chars != null && chars.length() != 0) {
            return chars;
        } else {
            throw new AssertFailException(DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE);
        }
    }

    public static <T> T[] isEmpty(@Nullable T[] array, String message) {
        if (array == null || array.length == 0) {
            return array;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T> T[] isEmpty(@Nullable T[] array, String message, Object... values) {
        if (array == null || array.length == 0) {
            return array;
        } else {
            throw new AssertFailException(
                    null != values && values.length == 0 ? String.format(message, values) : message);
        }
    }

    public static <T> T[] isEmpty(@Nullable T[] array) {
        if (array == null || array.length == 0) {
            return array;
        } else {
            throw new AssertFailException(DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE);
        }
    }

    public static <T extends Collection<?>> T isEmpty(@Nullable T collection, String message) {
        if (collection == null || collection.isEmpty()) {
            return collection;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T extends Collection<?>> T isEmpty(@Nullable T collection, String message, Object... values) {
        if (collection == null || collection.isEmpty()) {
            return collection;
        } else {
            throw new AssertFailException(
                    null != values && values.length == 0 ? String.format(message, values) : message);
        }
    }

    public static <T extends Collection<?>> T isEmpty(@Nullable T collection) {
        if (collection == null || collection.isEmpty()) {
            return collection;
        } else {
            throw new AssertFailException(DEFAULT_IS_EMPTY_COLLECTION_EX_MESSAGE);
        }
    }

    public static <T extends Map<?, ?>> T isEmpty(@Nullable T map, String message) {
        if (map == null || map.isEmpty()) {
            return map;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T extends Map<?, ?>> T isEmpty(@Nullable T map, String message, Object... values) {
        if (map == null || map.isEmpty()) {
            return map;
        } else {
            throw new AssertFailException(
                    null != values && values.length == 0 ? String.format(message, values) : message);
        }
    }

    public static <T extends Map<?, ?>> T isEmpty(@Nullable T map) {
        if (map == null || map.isEmpty()) {
            return map;
        } else {
            throw new AssertFailException(DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE);
        }
    }

    public static <T extends CharSequence> T isEmpty(@Nullable T chars, String message) {
        if (chars == null || chars.length() == 0) {
            return chars;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T extends CharSequence> T isEmpty(@Nullable T chars, String message, Object... values) {
        if (chars == null || chars.length() == 0) {
            return chars;
        } else {
            throw new AssertFailException(
                    null != values && values.length == 0 ? String.format(message, values) : message);

        }
    }

    public static <T extends CharSequence> T isEmpty(@Nullable T chars) {
        if (chars == null || chars.length() == 0) {
            return chars;
        } else {
            throw new AssertFailException(DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE);
        }
    }

    public static <T> T[] validIndex(@Nullable T[] array, int index, String message) {
        notNull(array);
        if (index >= 0 && index < array.length) {
            return array;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T> T[] validIndex(@Nullable T[] array, int index, String message, Object... values) {
        notNull(array);
        if (index >= 0 && index < array.length) {
            return array;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T> T[] validIndex(@Nullable T[] array, int index) {
        return validIndex(array, index, DEFAULT_VALID_INDEX_ARRAY_EX_MESSAGE, index);
    }

    public static <T> T[] doesNotContainsNull(@Nullable T[] array) {
        notNull(array);

        for (int i = 0; i < array.length; ++i) {
            if (null == array[i]) {
                throw new AssertFailException(String.format(DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE, i));
            }
        }

        return array;
    }

    public static <T extends Collection<?>> T validIndex(@Nullable T collection, int index, String message) {
        notNull(collection);
        if (index >= 0 && index < collection.size()) {
            return collection;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T extends Collection<?>> T validIndex(@Nullable T collection, int index, String message, Object... values) {
        notNull(collection);
        if (index >= 0 && index < collection.size()) {
            return collection;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T extends Collection<?>> T validIndex(@Nullable T collection, int index) {
        return validIndex(collection, index, DEFAULT_VALID_INDEX_COLLECTION_EX_MESSAGE, index);
    }

    public static <T extends Collection<?>> T doesNotContainsNull(@Nullable T collection) {
        notNull(collection);
        int i = 0;

        for (Iterator var2 = collection.iterator(); var2.hasNext(); ++i) {
            Object o = var2.next();
            if (null == o) {
                throw new AssertFailException(String.format(DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE, i));
            }
        }

        return collection;
    }

    public static <T extends CharSequence> T validIndex(@Nullable T chars, int index, String message, Object... values) {
        notNull(chars);
        if (index >= 0 && index < chars.length()) {
            return chars;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T extends CharSequence> T validIndex(@Nullable T chars, int index) {
        return validIndex(chars, index, DEFAULT_VALID_INDEX_CHAR_SEQUENCE_EX_MESSAGE, index);
    }

    public static String notBlank(@Nullable String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new AssertFailException(message);
        } else {
            return text;
        }
    }

    public static String notBlank(@Nullable String text) {
        return notBlank(text, DEFAULT_NOT_BLANK_EX_MESSAGE);
    }

    public static String notBlank(@Nullable Long text) {
        if (Objects.isNull(text)) {
            throw new AssertFailException(DEFAULT_NOT_BLANK_EX_MESSAGE);
        } else {
            return text.toString();
        }
    }

    public static String doesNotContain(@Nullable String textToSearch, @Nullable String substring, String message) {
        if (StringUtils.isBlank(textToSearch) && StringUtils.isBlank(substring)
                && textToSearch.contains(substring)) {
            throw new AssertFailException(message);
        } else {
            return textToSearch;
        }
    }

    public static String doesNotContain(@Nullable String textToSearch, @Nullable String substring) {
        return doesNotContain(textToSearch, substring,
                "[Assertion failed] - this String argument must not contain the substring [" + substring + "]");
    }

    public static <T extends CharSequence> T matchesPattern(@Nullable T input, @Nullable String pattern) {
        if (!Pattern.matches(pattern, input) || StringUtils.isBlank(input) || StringUtils.isBlank(pattern)) {
            throw new AssertFailException(String.format(DEFAULT_MATCHES_PATTERN_EX, input, pattern));
        } else {
            return input;
        }
    }

    public static <T extends CharSequence> T matchesPattern(@Nullable T input, @Nullable String pattern, String message, Object... values) {
        if (Pattern.matches(pattern, input) && StringUtils.isNotBlank(input) && StringUtils.isNotBlank(pattern)) {
            return input;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T extends Comparable<T>> T inclusiveBetween(@Nullable T start, @Nullable T end, @Nullable T value) {
        notNull(start);
        notNull(end);
        notNull(value);
        if (value.compareTo(start) >= 0 && value.compareTo(end) <= 0) {
            return value;
        } else {
            throw new AssertFailException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static <T extends Comparable<T>> T inclusiveBetween(@Nullable T start, @Nullable T end, @Nullable T value, String message, Object... values) {
        notNull(start);
        notNull(end);
        notNull(value);
        if (value.compareTo(start) >= 0 && value.compareTo(end) <= 0) {
            return value;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static int inclusiveBetween(int start, int end, int value) {
        if (value >= start && value <= end) {
            return value;
        } else {
            throw new AssertFailException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static int inclusiveBetween(int start, int end, int value, String message) {
        if (value >= start && value <= end) {
            return value;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static long inclusiveBetween(long start, long end, long value) {
        if (value >= start && value <= end) {
            return value;
        } else {
            throw new AssertFailException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static long inclusiveBetween(long start, long end, long value, String message) {
        if (value >= start && value <= end) {
            return value;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static double inclusiveBetween(double start, double end, double value) {
        if (value >= start && value <= end) {
            return value;
        } else {
            throw new AssertFailException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static double inclusiveBetween(double start, double end, double value, String message) {
        if (value >= start && value <= end) {
            return value;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static <T extends Comparable<T>> T exclusiveBetween(@Nullable T start, @Nullable T end, @Nullable T value) {
        notNull(start);
        notNull(end);
        notNull(value);
        if (value.compareTo(start) > 0 && value.compareTo(end) < 0) {
            return value;
        } else {
            throw new AssertFailException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static <T extends Comparable<T>> T exclusiveBetween(@Nullable T start, @Nullable T end, @Nullable T value, String message, Object... values) {
        notNull(start);
        notNull(end);
        notNull(value);
        if (value.compareTo(start) > 0 && value.compareTo(end) < 0) {
            return value;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static int exclusiveBetween(int start, int end, int value) {
        if (value > start && value < end) {
            return value;
        } else {
            throw new AssertFailException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static int exclusiveBetween(int start, int end, int value, String message) {
        if (value > start && value < end) {
            return value;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static long exclusiveBetween(long start, long end, long value) {
        if (value > start && value < end) {
            return value;
        } else {
            throw new AssertFailException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static long exclusiveBetween(long start, long end, long value, String message) {
        if (value > start && value < end) {
            return value;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static double exclusiveBetween(double start, double end, double value) {
        if (value > start && value < end) {
            return value;
        } else {
            throw new AssertFailException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static double exclusiveBetween(double start, double end, double value, String message) {
        if (value > start && value < end) {
            return value;
        } else {
            throw new AssertFailException(message);
        }
    }

    public static void gt(int value, int floor, String message) {
        if (value <= floor) {
            throw new AssertFailException(message);
        }
    }

    public static void gt(int value, int floor) {
        if (value <= floor) {
            throw new AssertFailException(String.format(DEFAULT_GT_EX_MESSAGE, floor, value));
        }
    }

    public static void gt(long value, long floor, String message) {
        if (value <= floor) {
            throw new AssertFailException(message);
        }
    }

    public static void gt(long value, long floor) {
        if (value <= floor) {
            throw new AssertFailException(String.format(DEFAULT_GT_EX_MESSAGE, floor, value));
        }
    }

    public static void gt(double value, double floor, String message) {
        if (value <= floor) {
            throw new AssertFailException(message);
        }
    }

    public static void gt(double value, double floor) {
        if (value <= floor) {
            throw new AssertFailException(String.format(DEFAULT_GT_EX_MESSAGE, floor, value));
        }
    }

    public static <T extends Comparable<T>> T gt(@Nullable T value, @Nullable T floor, String message) {
        notNull(value);
        notNull(floor);
        if (value.compareTo(floor) <= 0) {
            throw new AssertFailException(message);
        } else {
            return value;
        }
    }

    public static <T extends Comparable<T>> T gt(@Nullable T value, @Nullable T floor, String message, Object... params) {
        notNull(value);
        notNull(floor);
        if (value.compareTo(floor) > 0) {
            return value;
        } else {
            throw new AssertFailException(
                    null != params && params.length > 0 ? String.format(message, params) : message);
        }
    }

    public static <T extends Comparable<T>> T gt(@Nullable T floor, @Nullable T value) {
        notNull(value);
        notNull(floor);
        if (value.compareTo(floor) <= 0) {
            throw new AssertFailException(String.format(DEFAULT_GT_EX_MESSAGE, value, floor));
        } else {
            return value;
        }
    }

    public static void ge(int value, int floor, String message) {
        if (value < floor) {
            throw new AssertFailException(message);
        }
    }

    public static void ge(int value, int floor) {
        if (value < floor) {
            throw new AssertFailException(String.format(DEFAULT_GE_EX_MESSAGE, value, floor));
        }
    }

    public static void ge(long value, long floor, String message) {
        if (value < floor) {
            throw new AssertFailException(message);
        }
    }

    public static void ge(long value, long floor) {
        if (value < floor) {
            throw new AssertFailException(String.format(DEFAULT_GE_EX_MESSAGE, value, floor));
        }
    }

    public static void ge(double value, double floor, String message) {
        if (value < floor) {
            throw new AssertFailException(message);
        }
    }

    public static void ge(double value, double floor) {
        if (value < floor) {
            throw new AssertFailException(String.format(DEFAULT_GE_EX_MESSAGE, value, floor));
        }
    }

    public static <T extends Comparable<T>> T ge(@Nullable T value, @Nullable T floor, String message) {
        notNull(value);
        notNull(floor);
        if (value.compareTo(floor) < 0) {
            throw new AssertFailException(message);
        } else {
            return value;
        }
    }

    public static <T extends Comparable<T>> T ge(@Nullable T value, @Nullable T floor, String message, Object... params) {
        notNull(value);
        notNull(floor);
        if (value.compareTo(floor) >= 0) {
            return value;
        } else {
            throw new AssertFailException(
                    null != params && params.length > 0 ? String.format(message, params) : message);
        }
    }

    public static <T extends Comparable<T>> T ge(@Nullable T value, @Nullable T floor) {
        notNull(value);
        notNull(floor);
        if (value.compareTo(floor) < 0) {
            throw new AssertFailException(String.format(DEFAULT_GE_EX_MESSAGE, value, floor));
        } else {
            return value;
        }
    }

    public static void lt(int value, int ceil, String message) {
        if (value >= ceil) {
            throw new AssertFailException(message);
        }
    }

    public static void lt(int value, int ceil) {
        if (value >= ceil) {
            throw new AssertFailException(String.format(DEFAULT_LT_EX_MESSAGE, ceil, value));
        }
    }

    public static void lt(long value, long ceil, String message) {
        if (value >= ceil) {
            throw new AssertFailException(message);
        }
    }

    public static void lt(long value, long ceil) {
        if (value >= ceil) {
            throw new AssertFailException(String.format(DEFAULT_LT_EX_MESSAGE, ceil, value));
        }
    }

    public static void lt(double value, double ceil, String message) {
        if (value >= ceil) {
            throw new AssertFailException(message);
        }
    }

    public static void lt(double value, double ceil) {
        if (value >= ceil) {
            throw new AssertFailException(String.format(DEFAULT_LT_EX_MESSAGE, ceil, value));
        }
    }

    public static <T extends Comparable<T>> T lt(@Nullable T value, @Nullable T ceil, String message) {
        notNull(value);
        notNull(ceil);
        if (value.compareTo(ceil) >= 0) {
            throw new AssertFailException(message);
        } else {
            return value;
        }
    }

    public static <T extends Comparable<T>> T lt(@Nullable T value, @Nullable T ceil, String message, Object... params) {
        notNull(value);
        notNull(ceil);
        if (value.compareTo(ceil) < 0) {
            return value;
        } else {
            throw new AssertFailException(
                    null != params && params.length > 0 ? String.format(message, params) : message);
        }
    }

    public static <T extends Comparable<T>> T lt(@Nullable T value, @Nullable T ceil) {
        notNull(value);
        notNull(ceil);
        if (value.compareTo(ceil) >= 0) {
            throw new AssertFailException(String.format(DEFAULT_LT_EX_MESSAGE, ceil, value));
        } else {
            return value;
        }
    }

    public static void le(int value, int ceil, String message) {
        if (value > ceil) {
            throw new AssertFailException(message);
        }
    }

    public static void le(int value, int ceil) {
        if (value > ceil) {
            throw new AssertFailException(String.format(DEFAULT_LE_EX_MESSAGE, ceil, value));
        }
    }

    public static void le(long value, long ceil, String message) {
        if (value > ceil) {
            throw new AssertFailException(message);
        }
    }

    public static void le(long value, long ceil) {
        if (value > ceil) {
            throw new AssertFailException(String.format(DEFAULT_LE_EX_MESSAGE, ceil, value));
        }
    }

    public static void le(double value, double ceil, String message) {
        if (value > ceil) {
            throw new AssertFailException(message);
        }
    }

    public static void le(double value, double ceil) {
        if (value > ceil) {
            throw new AssertFailException(String.format(DEFAULT_LE_EX_MESSAGE, ceil, value));
        }
    }

    public static <T extends Comparable<T>> T le(@Nullable T value, @Nullable T ceil, String message) {
        notNull(value);
        notNull(ceil);
        if (value.compareTo(ceil) > 0) {
            throw new AssertFailException(message);
        } else {
            return value;
        }
    }

    public static <T extends Comparable<T>> T le(@Nullable T value, @Nullable T ceil, String message, Object... params) {
        notNull(value);
        notNull(ceil);
        if (value.compareTo(ceil) <= 0) {
            return value;
        } else {
            throw new AssertFailException(
                    null != params && params.length > 0 ? String.format(message, params) : message);
        }
    }

    public static <T extends Comparable<T>> T le(@Nullable T value, @Nullable T ceil) {
        notNull(value);
        notNull(ceil);
        if (value.compareTo(ceil) > 0) {
            throw new AssertFailException(String.format(DEFAULT_LE_EX_MESSAGE, ceil, value));
        } else {
            return value;
        }
    }

    public static <T> T isInstanceOf(Class<T> type, @Nullable Object obj) {
        notNull(obj);
        return isInstanceOf(type, obj, DEFAULT_IS_INSTANCE_OF_EX_MESSAGE, type.getName());
    }

    public static <T> T isInstanceOf(Class<?> type, @Nullable Object obj, String message, Object... values) {
        notNull(obj);
        notNull(type);
        if (type.isInstance(obj)) {
            return (T) obj;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    public static <T> Class<T> isAssignableFrom(Class<?> superType, Class<T> type) {
        return isAssignableFrom(superType, type, DEFAULT_IS_ASSIGNABLE_EX_MESSAGE, type.getName(), superType.getName());
    }

    public static <T> Class<T> isAssignableFrom(Class<?> superType, Class<T> type, String message, Object... values) {
        if (superType.isAssignableFrom(type)) {
            return type;
        } else {
            throw new AssertFailException(
                    null != values && values.length != 0 ? String.format(message, values) : message);
        }
    }

    /**
     * 用户捕获异常的逻辑执行体
     */
    public interface HandlerExceptionExecutor {
        void accept();
    }

    /**
     * 是否抛出指定异常
     *
     * @param exec           执行逻辑
     * @param exceptionClass 指定异常
     * @param erorDetail     错误日志信息
     * @param message        错误提示信息
     */
    public static void isThrowException(HandlerExceptionExecutor exec, Class<? extends Exception> exceptionClass, String erorDetail, String message) {
        try {
            exec.accept();
        } catch (Exception e) {
            if (StringUtil.isNotBlank(erorDetail) && e.getMessage().contains(erorDetail)) {

            }
            if (Objects.equals(e.getClass(), exceptionClass)) {
                throw new AssertFailException(message);
            } else {
                throw e;
            }
        }
    }

    /**
     * 是否抛出指定异常
     *
     * @param exec           执行逻辑
     * @param exceptionClass 指定异常
     * @param message        错误提示信息
     */
    public static void isThrowException(HandlerExceptionExecutor exec, Class<? extends Exception> exceptionClass, String message) {
        try {
            exec.accept();
        } catch (Exception e) {
            if (Objects.equals(e.getClass(), exceptionClass)) {
                throw new AssertFailException(message);
            } else {
                throw e;
            }
        }
    }

    /**
     * 是否抛出指定异常
     *
     * @param exec           执行逻辑
     * @param exceptionClass 指定异常
     */
    public static void isThrowException(HandlerExceptionExecutor exec, Class<? extends Exception> exceptionClass) {
        isThrowException(exec, exceptionClass, DEFAULT_THROW_SPECIFY_EXCEPTION_EX_MESSAGE);
    }

    /**
     * 断言错误信息
     *
     * @param e       异常
     * @param message 错误信息
     */
    public static void fail(Exception e, String message) {
        throw new AssertFailException(StringUtil.isEmpty(message) ? String.format(DEFAULT_FAIL_EX_MESSAGE, e.getMessage()) : message);
    }

    /**
     * 断言错误信息
     *
     * @param e 异常
     */
    public static void fail(Exception e) {
        fail(e, (String) null);
    }

    /**
     * 断言错误信息
     *
     * @param message 错误信息
     */
    public static void fail(String message) {
        fail(null, message);
    }

    /**
     * 断言错误信息
     *
     * @param e 异常
     */
    public static void fail(Throwable e) {
        throw new AssertFailException(e);
    }
}
