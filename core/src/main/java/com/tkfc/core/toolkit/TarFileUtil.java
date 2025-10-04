package com.tkfc.core.toolkit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 文件压缩工具类
 *
 * @author 0neBean
 */
@Slf4j
public class TarFileUtil {

    /**
     * 压缩目录文件
     *
     * @param paths  被压缩路径
     * @param target 输出路径
     * @author 0neBean
     * @since 2021-12-17 19:29:22
     */
    public static void compress(List<String> paths, String target) {
        File outputFile = new File(target);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             CompressorOutputStream gzippedOut = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP, fileOutputStream);
             TarArchiveOutputStream tabs = new TarArchiveOutputStream(gzippedOut)) {
            for (String path : paths) {
                putFile(path, tabs, "");
            }
        } catch (IOException | CompressorException e) {
            log.error("compress error", e);
        }
    }

    /**
     * 添加文件到压缩流
     *
     * @param path         文件路径
     * @param outputStream 输出流
     * @param prefix       文件前缀
     * @author 0neBean
     * @since 2021-12-17 19:30:05
     */
    private static void putFile(String path, TarArchiveOutputStream outputStream, String prefix) {
        File inputFile = new File(path);
        TarArchiveEntry tae = new TarArchiveEntry(inputFile, prefix + inputFile.getName());
        try {
            outputStream.putArchiveEntry(tae);
            if (inputFile.isDirectory()) {
                log.info("compressing directory: " + path);
                File[] files = inputFile.listFiles();
                if (null == files || files.length == 0) {
                    outputStream.closeArchiveEntry();
                    return;
                }
                for (File file : files) {
                    putFile(file.getAbsolutePath(), outputStream, prefix + inputFile.getName() + "/");
                }
            } else {
                log.info("compressing file: " + path);
                outputStream.write(FileUtils.readFileToByteArray(inputFile));
                outputStream.closeArchiveEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
