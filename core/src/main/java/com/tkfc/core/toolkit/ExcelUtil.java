package com.tkfc.core.toolkit;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson2.TypeReference;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.function.SerializableConsumer;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表格编辑器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-04 20:22:46
 */
public class ExcelUtil {


    private final static String DEFAULT_SHEET_NAME = "sheetName";
    @SuppressWarnings("all")
    private final static String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * resp 把 excel 文件输出到浏览器
     *
     * @param fileName    文件名
     * @param sheetName   表单名
     * @param data        数据
     * @param eachHandler 遍历逻辑
     * @param clazz       类型
     * @param <T>         数据泛型
     * @return 文件路径
     */
    public static <T> String writeExcelFileOnResp(String fileName, String sheetName, List<T> data, SerializableConsumer<T> eachHandler, Class<T> clazz) {
        eachExcelDataItem(data, eachHandler);
        return writeExcelFileOnResp(fileName, sheetName, data, clazz);
    }

    /**
     * resp 把 excel 文件输出到浏览器
     *
     * @param fileName  文件名
     * @param sheetName 表单名
     * @param data      数据
     * @param clazz     类型
     * @param <T>       数据泛型
     * @return 文件路径
     */
    public static <T> String writeExcelFileOnResp(String fileName, String sheetName, List<?> data, Class<T> clazz) {
        String separator = StringUtil.isBlank(fileName) ? StringPool.EMPTY : StringPool.DASH;
        String finalFileName = String.format("%s%s%s.xlsx", fileName, separator, System.currentTimeMillis());
        String filePath = String.format("%s%s", getDownloadTempPath(), finalFileName);
        EasyExcel.write(filePath, clazz).sheet(sheetName).doWrite(data);
        return finalFileName;
    }

    /**
     * resp 把 excel 文件输出到浏览器
     *
     * @param fileName 文件名
     * @param data     数据
     * @param clazz    类型
     * @param <T>      数据泛型
     * @return 文件路径
     */
    public static <T> String writeExcelFileOnResp(String fileName, List<T> data, Class<T> clazz) {
        return writeExcelFileOnResp(fileName, DEFAULT_SHEET_NAME, data, clazz);
    }

    /**
     * resp 把 excel 文件输出到浏览器
     *
     * @param fileName    文件名
     * @param data        数据
     * @param eachHandler 遍历逻辑
     * @param clazz       类型
     * @param <T>         数据泛型
     * @return 文件路径
     */
    public static <T> String writeExcelFileOnResp(String fileName, List<T> data, SerializableConsumer<T> eachHandler, Class<T> clazz) {
        eachExcelDataItem(data, eachHandler);
        return writeExcelFileOnResp(fileName, DEFAULT_SHEET_NAME, data, clazz);
    }

    /**
     * resp 把 excel 文件输出到浏览器
     *
     * @param data  数据
     * @param clazz 类型
     * @param <T>   数据泛型
     * @return 文件路径
     */
    public static <T> String writeExcelFileOnResp(List<T> data, Class<T> clazz) {
        return writeExcelFileOnResp(StringPool.EMPTY, DEFAULT_SHEET_NAME, data, clazz);
    }

    /**
     * resp 把 excel 文件输出到浏览器
     *
     * @param data        数据
     * @param eachHandler 遍历逻辑
     * @param clazz       类型
     * @param <T>         数据泛型
     * @return 文件路径
     */
    public static <T> String writeExcelFileOnResp(List<T> data, SerializableConsumer<T> eachHandler, Class<T> clazz) {
        eachExcelDataItem(data, eachHandler);
        return writeExcelFileOnResp(StringPool.EMPTY, DEFAULT_SHEET_NAME, data, clazz);
    }

    /**
     * 读取excel
     *
     * @param file  文件
     * @param clazz 类型
     * @param <T>   数据泛型
     * @return excel数据
     * @throws IOException IO异常
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> result = new ArrayList<>();
        ExcelReaderBuilder readerBuilder = EasyExcel.read(file.getInputStream(), clazz, new PageReadListener<T>(result::addAll));
        applyExcelType(readerBuilder, file.getOriginalFilename());
        readerBuilder.sheet().doRead();
        return result;
    }

    /**
     * 读取excel(指定泛型)
     *
     * @param file 文件
     * @param type 泛型类型
     * @param <T>  返回值泛型
     * @return excel数据
     * @throws IOException IO异常
     */
    public static <T> T readExcel(MultipartFile file, TypeReference<T> type) throws IOException {
        List<?> result = readExcel(file, resolveExcelType(type));
        return JsonUtil.toBean(JsonUtil.toJson(result), type);
    }

    /**
     * 读取excel
     *
     * @param inputStream 文件流
     * @param clazz       类型
     * @param <T>         数据泛型
     * @return excel数据
     */
    public static <T> List<T> readExcel(InputStream inputStream, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        EasyExcel.read(inputStream, clazz, new PageReadListener<T>(result::addAll)).sheet().doRead();
        return result;
    }

    /**
     * 读取excel(指定泛型)
     *
     * @param inputStream 文件流
     * @param type        泛型类型
     * @param <T>         返回值泛型
     * @return excel数据
     */
    public static <T> T readExcel(InputStream inputStream, TypeReference<T> type) {
        List<?> result = readExcel(inputStream, resolveExcelType(type));
        return JsonUtil.toBean(JsonUtil.toJson(result), type);
    }

    /**
     * 读取excel
     *
     * @param file  文件
     * @param clazz 类型
     * @param <T>   数据泛型
     * @return excel数据
     */
    public static <T> List<T> readExcel(File file, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        ExcelReaderBuilder readerBuilder = EasyExcel.read(file, clazz, new PageReadListener<T>(result::addAll));
        applyExcelType(readerBuilder, file.getName());
        readerBuilder.sheet().doRead();
        return result;
    }

    /**
     * 读取excel(指定泛型)
     *
     * @param file 文件
     * @param type 泛型类型
     * @param <T>  返回值泛型
     * @return excel数据
     */
    public static <T> T readExcel(File file, TypeReference<T> type) {
        List<?> result = readExcel(file, resolveExcelType(type));
        return JsonUtil.toBean(JsonUtil.toJson(result), type);
    }

    /**
     * 读取excel
     *
     * @param fileName 文件名称
     * @param clazz    类型
     * @param <T>      数据泛型
     * @return excel数据
     */
    public static <T> List<T> readExcel(String fileName, Class<T> clazz) {
        String filePath = resolveReadFilePath(fileName);
        List<T> result = new ArrayList<>();
        ExcelReaderBuilder readerBuilder = EasyExcel.read(filePath, clazz, new PageReadListener<T>(result::addAll));
        applyExcelType(readerBuilder, filePath);
        readerBuilder.sheet().doRead();
        return result;
    }

    /**
     * 读取无表头 csv
     *
     * @param fileName 文件名称
     * @param clazz    类型
     * @param <T>      数据泛型
     * @return csv数据
     */
    public static <T> List<T> readCsvNoHead(String fileName, Class<T> clazz) {
        String filePath = resolveReadFilePath(fileName);
        List<T> result = new ArrayList<>();
        ExcelReaderBuilder readerBuilder = EasyExcel.read(filePath, clazz, new PageReadListener<T>(result::addAll));
        readerBuilder.excelType(ExcelTypeEnum.CSV);
        readerBuilder.sheet().headRowNumber(0).doRead();
        return result;
    }

    /**
     * 读取无表头 csv
     *
     * @param file  文件
     * @param clazz 类型
     * @param <T>   数据泛型
     * @return csv数据
     */
    public static <T> List<T> readCsvNoHead(File file, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        ExcelReaderBuilder readerBuilder = EasyExcel.read(file, clazz, new PageReadListener<T>(result::addAll));
        readerBuilder.excelType(ExcelTypeEnum.CSV);
        readerBuilder.sheet().headRowNumber(0).doRead();
        return result;
    }

    /**
     * 读取无表头 csv
     *
     * @param file  文件
     * @param clazz 类型
     * @param <T>   数据泛型
     * @return csv数据
     * @throws IOException IO异常
     */
    public static <T> List<T> readCsvNoHead(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> result = new ArrayList<>();
        ExcelReaderBuilder readerBuilder = EasyExcel.read(file.getInputStream(), clazz, new PageReadListener<T>(result::addAll));
        readerBuilder.excelType(ExcelTypeEnum.CSV);
        readerBuilder.sheet().headRowNumber(0).doRead();
        return result;
    }

    /**
     * 读取excel(指定泛型)
     *
     * @param fileName 文件名称
     * @param type     泛型类型
     * @param <T>      返回值泛型
     * @return excel数据
     */
    public static <T> T readExcel(String fileName, TypeReference<T> type) {
        List<?> result = readExcel(fileName, resolveExcelType(type));
        return JsonUtil.toBean(JsonUtil.toJson(result), type);
    }

    /**
     * 获取下载临时目录
     *
     * @return 下载临时目录
     */
    private static String getDownloadTempPath() {
        String downloadPath;
        if (Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType())) {
            downloadPath = PropUtil.getInstance().getConfig("cdn.download.path.windows");
        } else {
            downloadPath = PropUtil.getInstance().getConfig("cdn.download.path.unix");
        }
        IoUtil.createDirectoryIfNotExists(downloadPath);
        return downloadPath;
    }

    /**
     * 获取上传临时目录
     *
     * @return 上传临时目录
     */
    private static String getUploadTempPath() {
        String uploadPath;
        if (Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType())) {
            uploadPath = PropUtil.getInstance().getConfig("cdn.upload.path.windows");
        } else {
            uploadPath = PropUtil.getInstance().getConfig("cdn.upload.path.unix");
        }
        IoUtil.createDirectoryIfNotExists(uploadPath);
        return uploadPath;
    }

    /**
     * 遍历excel数据
     *
     * @param data excel 数据
     * @param <T>  数据泛型
     */
    private static <T> void eachExcelDataItem(List<T> data, SerializableConsumer<T> eachHandler) {
        for (T datum : data) {
            eachHandler.accept(datum);
        }
    }

    /**
     * 获取excel行映射类型
     *
     * @param typeReference 泛型描述
     * @return 行映射类型
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> resolveExcelType(TypeReference<?> typeReference) {
        Type type = typeReference.getType();
        if (type instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class<?> rawClass && List.class.isAssignableFrom(rawClass)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class<?> actualClass) {
                    return (Class<T>) actualClass;
                }
            }
        }
        throw new IllegalArgumentException("TypeReference must be List<T> and T must be a class");
    }

    /**
     * 解析读取文件路径（支持绝对路径）
     *
     * @param fileName 文件名/路径
     * @return 可读取路径
     */
    private static String resolveReadFilePath(String fileName) {
        File file = new File(fileName);
        if (file.isAbsolute()) {
            return fileName;
        }
        return String.format("%s%s", getUploadTempPath(), fileName);
    }

    /**
     * 根据扩展名应用表格类型（支持 csv）
     *
     * @param readerBuilder 读取构建器
     * @param fileName      文件名/路径
     */
    private static void applyExcelType(ExcelReaderBuilder readerBuilder, String fileName) {
        ExcelTypeEnum excelTypeEnum = resolveExcelFileType(fileName);
        if (excelTypeEnum != null) {
            readerBuilder.excelType(excelTypeEnum);
        }
    }

    /**
     * 解析文件类型
     *
     * @param fileName 文件名/路径
     * @return excel 类型
     */
    private static ExcelTypeEnum resolveExcelFileType(String fileName) {
        if (StringUtil.isBlank(fileName)) {
            return null;
        }
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".csv")) {
            return ExcelTypeEnum.CSV;
        }
        if (lowerName.endsWith(".xls")) {
            return ExcelTypeEnum.XLS;
        }
        if (lowerName.endsWith(".xlsx")) {
            return ExcelTypeEnum.XLSX;
        }
        return null;
    }

}
