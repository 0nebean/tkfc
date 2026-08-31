package com.tkfc.compress.wrapper;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 包装响应请求用于拦截并压缩html内容
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-27 12:29:01
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private final PrintWriter cachedWriter;
    private final CharArrayWriter bufferedWriter;


    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        // 这个是我们保存返回结果的地方
        bufferedWriter = new CharArrayWriter();
        // 这个是包装PrintWriter的，让所有结果通过这个PrintWriter写入到bufferedWriter中
        cachedWriter = new PrintWriter(bufferedWriter);
    }


    @Override
    public PrintWriter getWriter() {
        return cachedWriter;
    }


    /**
     * 获取原始的HTML页面内容。
     */
    public String getResult() {
        byte[] bytes = bufferedWriter.toString().getBytes();
        cachedWriter.close();
        bufferedWriter.close();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}