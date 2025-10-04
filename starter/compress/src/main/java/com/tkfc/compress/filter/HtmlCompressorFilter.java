package com.tkfc.compress.filter;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.tkfc.compress.compressor.GoogleClosureCompressor;
import com.tkfc.compress.toolkit.CompressUtil;
import com.tkfc.compress.wrapper.ResponseWrapper;
import com.tkfc.core.toolkit.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * html压缩过滤器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-27 12:30:12
 */
@Slf4j
@WebFilter
@Component
@RequiredArgsConstructor
public class HtmlCompressorFilter implements Filter {

    private HtmlCompressor htmlCompressor;

    private static final Set<String> NOT_COMPRESSOR_FILE = new HashSet<>() {{
        add(".xml");
        add(".txt");
    }};


    @Override
    public void init(FilterConfig filterConfig) {
        htmlCompressor = new HtmlCompressor();
        htmlCompressor.setCompressCss(true);
        htmlCompressor.setCompressJavaScript(true);
        htmlCompressor.setJavaScriptCompressor(new GoogleClosureCompressor());
        htmlCompressor.setRemoveComments(true);
        htmlCompressor.setRemoveIntertagSpaces(true);
        htmlCompressor.setRemoveMultiSpaces(true);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Boolean enable = Optional.ofNullable(PropUtil.getInstance().getConfig("enable.html.compressor.filter")).map(ParseUtil::toBoolean).orElse(Boolean.FALSE);
        if (!enable) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = WebUtil.getHttpServletRequest();
        String accept = httpServletRequest.getHeader("Accept");
        String fileType = IoUtil.getFilePathTypeName(httpServletRequest.getRequestURL().toString());
        if (StringUtil.isNotBlank(accept) && accept.contains("text/html") && !NOT_COMPRESSOR_FILE.contains(fileType)) {
            ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, wrapper);
            response.setContentLength(-1);
            PrintWriter out = response.getWriter();
            String htmlRaw = wrapper.getResult();
            String compressHtml;
            if (Objects.equals(httpServletRequest.getParameter("raw"), Boolean.TRUE.toString())) {
                compressHtml = htmlRaw;
            } else {
                String htmlWithoutComments = CompressUtil.removeHtmlComments(htmlRaw);
                compressHtml = htmlCompressor.compress(htmlWithoutComments);
                compressHtml = CompressUtil.compressBlankAndNewLine(compressHtml);
            }
            out.write(compressHtml);
            out.flush();
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
