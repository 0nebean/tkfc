package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyParam;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 查询分页对象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
@Body(tag = "查询分页对象")
public class Pagination implements Serializable {

    private final static int DEFAULT_PAGE_SIZE = 10;
    public final static int DEFAULT_CURRENT_PAGE = 1;
    private static final long serialVersionUID = 3171038854688414986L;


    /**
     * 每页默认10条数据
     */
    @Getter
    @Setter
    @BodyProperty(tag = "每页默认10条数据")
    private Integer pageSize;
    /**
     * 当前页
     */
    @Getter
    @Setter
    @BodyProperty(tag = "当前页")
    private Integer currentPage;
    /**
     * 总页数
     */
    @Getter
    @Setter
    @BodyProperty(tag = "总页数")
    private Integer totalPages;
    /**
     * 总数据数
     */
    @Setter
    @BodyProperty(tag = "总数据数")
    private Integer totalCount;

    public Pagination(Integer totalCount, Integer pageSize) {
        this.init(totalCount, pageSize);
    }

    public Pagination() {
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.currentPage = DEFAULT_CURRENT_PAGE;
    }


    /**
     * 初始化分页参数:需要先设置totalRows
     *
     * @param totalCount 总条数
     * @param pageSize   每页数量
     */
    public void init(Integer totalCount, Integer pageSize) {
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        if ((totalCount % pageSize) == 0) {
            totalPages = totalCount / pageSize;
        } else {
            totalPages = totalCount / pageSize + 1;
        }
    }

    public void init(Integer totalCount, Integer pageSize, Integer currentPage) {
        this.currentPage = currentPage;
        this.init(totalCount, pageSize);
    }

    public Integer getTotalCount() {
        return Objects.requireNonNullElse(this.totalCount, 0);
    }

    public Integer offsetNum() {
        return (currentPage > DEFAULT_CURRENT_PAGE) ? pageSize * currentPage : 0;
    }

}
