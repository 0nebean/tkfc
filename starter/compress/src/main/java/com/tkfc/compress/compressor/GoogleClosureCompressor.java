package com.tkfc.compress.compressor;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.googlecode.htmlcompressor.compressor.Compressor;
import com.tkfc.compress.toolkit.CompressUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * GoogleClosure 的 Compressor 实现
 * 用于在html压缩中压缩js
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-27 13:43:31
 */
@Slf4j
public class GoogleClosureCompressor implements Compressor {


    private final static String ART_TEMPLATE_CHAR = "set langType='art'";

    @Override
    public String compress(String source) {
        if (source.contains(ART_TEMPLATE_CHAR)) {
            return CompressUtil.compressBlankAndNewLine(source);
        }
        CompilerOptions options = new CompilerOptions();
        CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);
        // 设置为移除所有注释
        options.setEmitUseStrict(false);  // 关闭 "use strict" 的输出，减少不必要的代码
        options.setPrettyPrint(false);    // 设置为 false 以确保最小化输出
        SourceFile extern = SourceFile.fromCode("externs.js", "function alert(x) {}");
        SourceFile jsFile = SourceFile.fromCode("input.js", source);
        return CompressUtil.doCompilerJs(extern, jsFile, options);
    }
}