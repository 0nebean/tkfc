package com.tkfc.boot.starter.elasticsearch.extend;

import com.tkfc.boot.starter.elasticsearch.pojo.PaginationES;
import com.tkfc.boot.starter.elasticsearch.pojo.SortES;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BaseServiceImplES<T extends BaseModelES, K extends BaseMapperES<T>> implements BaseServiceES<T> {

    /**
     * dao原型属性
     */
    @Resource
    protected K baseMapper;

    @Override
    public Boolean existIndex() {
        return baseMapper.existIndex();
    }

    @Override
    public void createIndex(int shards, int replicas) {
        baseMapper.createIndex(shards, replicas);
    }

    @Override
    public void putMappingRequest() {
        baseMapper.putMappingRequest();
    }

    @Override
    public void deleteIndex() {
        baseMapper.deleteIndex();
    }

    @Override
    public DeleteResponse deleteById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public BulkResponse deleteByIds(List<String> ids) {
        return baseMapper.deleteByIds(ids);
    }

    @Override
    public IndexResponse index(T document) {
        return baseMapper.index(document);
    }

    @Override
    public BulkResponse indexBatch(List<T> documents) {
        return baseMapper.indexBatch(documents);
    }

    @Override
    public UpdateResponse update(T document) {
        return baseMapper.update(document);
    }

    @Override
    public BulkResponse updateBatch(List<T> documents) {
        return baseMapper.updateBatch(documents);
    }

    @Override
    public T findById(String id) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("_id", id));
        searchSourceBuilder.size(1);
        return findOne(searchSourceBuilder);
    }

    @Override
    public List<T> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", ids));
        searchSourceBuilder.size(ids.size());
        return find(searchSourceBuilder);
    }

    @Override
    public List<T> find(SearchSourceBuilder searchSourceBuilder, PaginationES pagination, SortES sort) {
        // 应用分页参数
        if (pagination != null) {
            if (pagination.getPageSize() != null) {
                searchSourceBuilder.size(pagination.getPageSize());
            }

            // 应用 searchAfter 分页（必须和排序一起使用）
            if (pagination.getSearchAfter() != null && pagination.getSearchAfter().length > 0) {
                if (sort == null || sort.orders.isEmpty()) {
                    throw new ElasticsearchException("使用 searchAfter 分页时必须提供排序参数");
                }
                searchSourceBuilder.searchAfter(pagination.getSearchAfter());
            }
        }

        // 应用排序参数
        if (sort != null && !sort.orders.isEmpty()) {
            for (SortES.ESOrder order : sort.orders) {
                searchSourceBuilder.sort(order.getProperty(), order.getDirection());
            }
        }

        // 调用baseMapper的findWithResponse方法执行查询
        SearchResponse searchResponse = baseMapper.findWithResponse(searchSourceBuilder);

        // 解析搜索结果
        List<T> results = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();

        for (SearchHit hit : hits) {
            try {
                Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
                if (clazz == null) {
                    throw new ElasticsearchException("无法获取泛型类型，请检查类定义");
                }
                // 使用反射创建对象，提高性能
                T document = (T) JsonUtil.toBean(hit.getSourceAsString(), clazz);
                results.add(document);
            } catch (Exception e) {
                log.error("解析搜索结果失败，hit ID: " + hit.getId(), e);
            }
        }

        // 处理searchAfter分页
        if (pagination != null && hits.length > 0) {
            // 获取最后一个文档的 searchAfter 值，用于下次分页
            Object[] lastSearchAfter = hits[hits.length - 1].getSortValues();
            pagination.setSearchAfter(lastSearchAfter);
        }

        return results;
    }

    @Override
    public List<T> find(SearchSourceBuilder searchSourceBuilder) {
        return find(searchSourceBuilder, null, null);
    }

    @Override
    public List<T> find(SearchSourceBuilder searchSourceBuilder, SortES sort) {
        return find(searchSourceBuilder, null, sort);
    }

    @Override
    public T findOne(SearchSourceBuilder searchSourceBuilder) {
        List<T> results = find(searchSourceBuilder, null, null);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public T findOne(SearchSourceBuilder searchSourceBuilder, SortES sort) {
        List<T> results = find(searchSourceBuilder, null, sort);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public T findOne(SearchSourceBuilder searchSourceBuilder, PaginationES pagination, SortES sort) {
        List<T> results = find(searchSourceBuilder, pagination, sort);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Long count() {
        // 先检查索引是否存在
        if (!existIndex()) {
            return 0L;
        }
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0); // 只返回总数，不返回文档内容
        return count(searchSourceBuilder);
    }

    @Override
    public Long count(SearchSourceBuilder searchSourceBuilder) {
        // 先检查索引是否存在
        if (!existIndex()) {
            return 0L;
        }
        
        // 设置size为0，只返回总数，不返回文档内容
        searchSourceBuilder.size(0);
        
        // 调用baseMapper的findWithResponse方法执行查询
        SearchResponse searchResponse = baseMapper.findWithResponse(searchSourceBuilder);
        
        // 返回总数
        return searchResponse.getHits().getTotalHits().value;
    }

}
