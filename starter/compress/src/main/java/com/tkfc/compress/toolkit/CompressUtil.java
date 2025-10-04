package com.tkfc.compress.toolkit;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.IoUtil;
import com.yahoo.platform.yui.compressor.CssCompressor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 压缩静态资源工具类
 *
 * @author 0neBean
 * @since 2023-04-09 15:48:04
 */
@Slf4j
public class CompressUtil {


    private static final Pattern HTML_COMMENT_PATTERN = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    /**
     * 压缩css/js 文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException io异常
     */
    public static void compress(File source, File target) throws IOException {
        Assert.isTrue(source.exists());
        Assert.isTrue(target.exists());
        String sourcePath = source.getPath();
        String zipCode = null;
        if (IoUtil.isCSSFile(sourcePath)) {
            zipCode = CompressUtil.compressCss(sourcePath);
        } else if (IoUtil.isJSFile(sourcePath)) {
            zipCode = CompressUtil.compressJs(sourcePath);
        }
        Path targetPath = Paths.get(target.getPath());
        if (!Objects.isNull(zipCode)) {
            Files.write(targetPath, zipCode.getBytes());
        }
    }

    /**
     * 压缩css
     *
     * @param cssFilePath css文件路径
     * @return 压缩后的css
     * @throws IOException io异常
     */
    public static String compressCss(String cssFilePath) throws IOException {
        String cssContent = new IoUtil().read(cssFilePath);
        BufferedReader reader = null;
        ByteArrayOutputStream resultOutputStream;
        Writer writer = null;
        try {
            reader = IoUtil.getReader(new ByteArrayInputStream(cssContent.getBytes()), StandardCharsets.UTF_8);
            CssCompressor cssCompressor = new CssCompressor(reader);
            resultOutputStream = new ByteArrayOutputStream();
            writer = new OutputStreamWriter(resultOutputStream, StandardCharsets.UTF_8);
            cssCompressor.compress(writer, -1);
        } finally {
            IoUtil.closeQuietly(reader);
            IoUtil.closeQuietly(writer);
        }
        cssContent = IoUtil.toStr(resultOutputStream, StandardCharsets.UTF_8);
        return cssContent;
    }

    /**
     * 压缩js
     *
     * @param jsFilePath js文件路径
     * @return 压缩后的js
     */
    public static String compressJs(String jsFilePath) {
        CompilerOptions options = new CompilerOptions();
        CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);
        SourceFile extern = SourceFile.fromCode("externs.js", "function alert(x) {}");
        SourceFile jsFile = SourceFile.fromFile(jsFilePath);
        return CompressUtil.doCompilerJs(extern, jsFile, options);
    }

    /**
     * 执行压缩js的逻辑
     *
     * @param extern  扩展的语法
     * @param jsFile  js文件
     * @param options 压缩选项
     * @return 压缩后的js
     */
    public static String doCompilerJs(SourceFile extern, SourceFile jsFile, CompilerOptions options) {
        Compiler compiler = new Compiler();
        compiler.compile(extern, jsFile, options);
        if (compiler.getErrorCount() > 0) {
            StringBuilder errorMessage = new StringBuilder();
            for (JSError jsError : compiler.getErrors()) {
                errorMessage.append(jsError.toString());
            }
            log.error("compileJs got an error , {}", errorMessage);
        }
        return compiler.toSource();
    }

    /**
     * 移除html注释
     *
     * @param html html内容
     * @return 移除注释后的html
     */
    public static String removeHtmlComments(String html) {
        if (html == null) {
            return null;
        }
        return HTML_COMMENT_PATTERN.matcher(html).replaceAll(StringPool.SPACE);
    }

    /**
     * 压缩art template内容
     * 替换 \n 和 \r 为一个空格
     * 将多个空格替换为一个空格
     * 移除字符串首尾的空格
     *
     * @param source 输入
     * @return 压缩后的js
     */
    public static String compressBlankAndNewLine(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }
        String cleanedString = source.replaceAll("[\\n\\r]+", StringPool.SPACE);
        cleanedString = cleanedString.replaceAll("\\s{2,}", StringPool.SPACE);
        return cleanedString.trim();
    }
}
 

