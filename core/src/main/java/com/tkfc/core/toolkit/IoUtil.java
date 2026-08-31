package com.tkfc.core.toolkit;

import com.alibaba.fastjson2.JSON;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.base.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * IO操作工具类
 *
 * @author 0neBean
 */
@Slf4j
public class IoUtil {


    /**
     * 关闭流
     *
     * @param closeable 可关闭的对象
     * @author 0neBean
     * @since 2021-12-17 19:04:33
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignored) {
            ;
        }

    }


    /**
     * 从流中读取对象
     *
     * @param inputStream 流
     * @param clazz       类型
     * @param <T>         泛型类型
     * @return 对象
     */
    public static <T> T getBeanFromInputStream(InputStream inputStream, Class<T> clazz) {
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            String json = responseStrBuilder.toString().replaceAll(" ", "");
            return JSON.parseObject(json, clazz);
        } catch (IOException e) {
            log.error("getRequestBody got err = ", e);
            return null;
        }
    }


    /**
     * 从流中读取对象
     *
     * @param inputStream 流
     * @return 对象
     */
    public static String getJsonFromInputStream(InputStream inputStream) {
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            return responseStrBuilder.toString().replaceAll(" ", "");
        } catch (IOException e) {
            log.error("getRequestBody got err = ", e);
            return null;
        }
    }


    /**
     * 获取文件后缀名
     *
     * @param fileName 文件名
     * @return 后缀名
     */
    public static String getFilePathTypeName(String fileName) {
        if (StringUtil.isBlank(fileName) || !fileName.contains(StringPool.DOT)) {
            return null;
        }
        int index = fileName.lastIndexOf(StringPool.DOT);
        String result = fileName.substring(index);
        if (result.contains(StringPool.QUESTION_MARK)) {
            return result.substring(0, result.indexOf(StringPool.QUESTION_MARK));
        } else {
            return result;
        }
    }

    /**
     * 将流读成字节数组
     *
     * @param in in
     * @return byte[]
     */
    public static byte[] readStreamAsByteArray(InputStream in) {
        if (in == null) {
            return new byte[0];
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = in.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(output);
        }

        return output.toByteArray();
    }


    /**
     * 拷贝文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @throws IOException io异常
     */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(srcFile);
            outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }


    /**
     * 拷贝文件
     *
     * @param srcInput 源文件
     * @param destFile 目标文件
     * @throws IOException io异常
     */
    public static void copyFile(InputStream srcInput, File destFile) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = srcInput.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            closeQuietly(srcInput);
            closeQuietly(outputStream);
        }
    }

    /**
     * 拷贝目录
     *
     * @param sourceFolder 源目录
     * @param destFolder   目标目录
     * @throws IOException io异常
     */
    public static void copyDirectoryToDirectory(File sourceFolder, File destFolder) throws IOException {
        // 创建目标文件夹
        if (!destFolder.exists()) {
            Assert.isTrue(destFolder.mkdirs());
        }
        // 复制源文件夹中的所有文件和子文件夹
        File[] files = sourceFolder.listFiles();
        if (null == files) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            File destFile = new File(destFolder, fileName);
            if (file.isDirectory()) {
                copyDirectoryToDirectory(file, destFile);
            } else {
                copyFile(file, destFile);
            }
        }
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) {
        realFileName = URLEncoder.encode(realFileName, StandardCharsets.UTF_8);
        String percentEncodedFileName = realFileName.replaceAll("\\+", "%20");
        String contentDispositionValue = "attachment; filename=" +
                percentEncodedFileName +
                ";" +
                "filename*=" +
                "utf-8''" +
                percentEncodedFileName;
        response.setHeader("Content-disposition", contentDispositionValue);
    }

    /**
     * 删除文件
     *
     * @param targetPath 目标文件路径
     */
    public static void deleteQuietly(String targetPath) {
        deleteQuietly(new File(targetPath));
    }

    /**
     * 删除文件
     *
     * @param target 目标文件
     */
    public static void deleteQuietly(File target) {
        if (Objects.isNull(target) || !target.exists()) {
            return;
        }
        File[] files = target.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteQuietly(file);
                } else {
                    if (file.exists()) {
                        if (!file.delete()) {
                            file.deleteOnExit();
                        }
                    }
                }
            }
        }
        if (target.exists()) {
            if (!target.delete()) {
                target.deleteOnExit();
            }
        }
    }

    /**
     * 创建文件
     *
     * @param target 目标文件
     */
    public static void createQuietly(File target) {
        if (!target.exists()) {
            if (!target.mkdirs()) {
                log.error("create file failure ,path = {}", target.getPath());
            }
        }
    }

    /**
     * 获取Reader
     *
     * @param inputStream io in
     * @param charset     字符集
     * @return BufferedReader
     */
    public static BufferedReader getReader(InputStream inputStream, Charset charset) {
        if (null == inputStream) {
            return null;
        } else {
            InputStreamReader reader;
            if (null == charset) {
                reader = new InputStreamReader(inputStream);
            } else {
                reader = new InputStreamReader(inputStream, charset);
            }

            return new BufferedReader(reader);
        }
    }

    /**
     * 从流中读取字符
     *
     * @param out     字节输出流
     * @param charset 字符集
     * @return 读取的字符
     */
    public static String toStr(ByteArrayOutputStream out, Charset charset) {
        return out.toString(charset);
    }


    /**
     * 是否是css文件
     *
     * @param filePath 文件路径
     * @return bool
     */
    public static boolean isCSSFile(String filePath) {
        return filePath.toLowerCase().endsWith(".css");
    }

    /**
     * 是否是js文件
     *
     * @param filePath 文件路径
     * @return bool
     */
    public static boolean isJSFile(String filePath) {
        return filePath.toLowerCase().endsWith(".js");
    }

    /**
     * 是否是压缩文件
     *
     * @param filePath 文件路径
     * @return bool
     */
    public static boolean isMinFile(String filePath) {
        return filePath.toLowerCase().contains(".min");
    }

    /**
     * 不是压缩文件
     *
     * @param filePath 文件路径
     * @return bool
     */
    public static boolean notMinFile(String filePath) {
        return !isMinFile(filePath);
    }

    /**
     * 读取文件内容为字符串
     *
     * @param path   文件地址
     * @param encode 字符集
     * @return 读出的字符串
     * @throws IOException IO异常
     */
    public String read(Path path, Charset encode) throws IOException {
        if (Files.isDirectory(path))
            throw new IOException("path：" + path + " can not be directory");
        if (!Files.exists(path))
            throw new IOException(path + "　dose not exits");
        return Files.readString(path, encode);
    }

    /**
     * 读取文件内容为字符串
     *
     * @param fullPath 完整地址
     * @param encode   字符集
     * @return 读出的字符串
     * @throws IOException IO异常
     */
    public String read(String fullPath, Charset encode) throws IOException {
        Path path = Paths.get(fullPath);
        return read(path, encode);
    }

    /**
     * 读取文件内容为字符串
     *
     * @param fullPath 完整地址
     * @return 读出的字符串
     * @throws IOException IO异常
     */
    public String read(String fullPath) throws IOException {
        return read(fullPath, StandardCharsets.UTF_8);
    }

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } finally {
            closeQuietly(os);
            closeQuietly(fis);
        }
    }

    /**
     * 写文件到指定路径
     *
     * @param content 内容
     * @param path    文件路径
     * @return 文件路径
     */
    public static File writeFile(String content, String path) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            org.apache.commons.io.IOUtils.write(content, writer);
        } finally {
            closeQuietly(writer);
        }
        return new File(path);
    }

    /**
     * 创建临时拷贝对应
     *
     * @param source 源文件
     * @return 拷贝的目标文件
     * @throws IOException io 异常
     */
    public static File copyTempFile(File source) throws IOException {
        String separator = EnvUtil.CURRENT_FILE_PATH_SEPARATOR;
        File temp = new File(String.format("%s%s%s%s%s", source.getParentFile().getPath(), separator, "temp", separator, source.getName()));
        if (!temp.getParentFile().exists()) {
            Assert.isTrue(temp.getParentFile().mkdirs());
        }
        Assert.isTrue(temp.createNewFile());
        copyFile(source, temp);
        return temp;
    }

    /**
     * 目录不存在时创建目录
     *
     * @param directoryPath 目录
     */
    public static void createDirectoryIfNotExists(String directoryPath) {
        Path path = Paths.get(directoryPath);
        try {
            // 如果目录不存在，则创建目录
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException ignore) {
        }
    }

    public static byte[] getFileFromUrl(String mediaUrl) throws IOException {
        // 获取图片输入流
        InputStream in = new URL(mediaUrl).openStream();
        // 将文件转换成字节数组
        return IOUtils.toByteArray(in);
    }

    /**
     * 下载文件
     *
     * @param urlLink         链接
     * @param destinationFile 目标文件路径
     * @return 下载的文件
     * @throws IOException io 异常
     */
    public static File downloadFile(String urlLink, String destinationFile) throws IOException {
        URL url = new URL(urlLink);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.connect();
        // 检查响应代码
        if (httpURLConnection.getResponseCode() != 200) {
            throw new IOException("http error: " + httpURLConnection.getResponseCode());
        }
        // 创建输入流和输出流
        try (InputStream in = httpURLConnection.getInputStream();
             BufferedInputStream bis = new BufferedInputStream(in);
             FileOutputStream fos = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        return new File(destinationFile);
    }
}
