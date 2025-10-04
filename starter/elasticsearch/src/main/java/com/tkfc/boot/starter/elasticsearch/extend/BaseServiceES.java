package com.tkfc.boot.starter.elasticsearch.extend;

import com.tkfc.boot.starter.elasticsearch.pojo.PaginationES;
import com.tkfc.boot.starter.elasticsearch.pojo.SortES;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;

public interface BaseServiceES<T extends BaseModelES> {

    /**
     * 检查索引是否存在。
     *
     * @return 如果索引存在则返回true，否则返回false
     * @throws ElasticsearchException 如果在检查索引是否存在时发生IO异常
     */
    Boolean existIndex();

    /**
     * 创建一个新的Elasticsearch索引请求。
     *
     * @param shards    索引的主分片数量
     * @param replicas  每个主分片的副本数量
     * @throws ElasticsearchException 如果创建索引时发生IO异常或者索引已存在
     */
    void createIndex(int shards, int replicas);

    /**
     * 发送一个PUT请求来更新指定索引的映射。
     *
     * @throws ElasticsearchException 如果在处理ESField注解时发现name或type属性未指定，或者在发送请求时发生IO异常
     */
    void putMappingRequest();

    /**
     * 删除Elasticsearch索引。
     *
     * @throws ElasticsearchException 如果删除索引时发生IO异常
     */
    void deleteIndex();

    /**
     * 根据ID删除文档
     *
     * @param id 文档ID
     * @return 删除响应
     * @throws ElasticsearchException 如果删除失败
     */
    DeleteResponse deleteById(String id);

    /**
     * 批量删除文档
     *
     * @param ids 文档ID列表
     * @return 批量删除响应
     * @throws ElasticsearchException 如果批量删除失败
     */
    BulkResponse deleteByIds(List<String> ids);

    /**
     * 保存文档到索引
     *
     * @param document 要保存的文档
     * @return 索引响应
     * @throws ElasticsearchException 如果保存失败
     */
    IndexResponse index(T document);

    /**
     * 批量保存文档到索引
     *
     * @param documents 要保存的文档列表
     * @return 批量索引响应
     * @throws ElasticsearchException 如果批量保存失败
     */
    BulkResponse indexBatch(List<T> documents);

    /**
     * 更新文档
     *
     * @param document 要更新的文档
     * @return 更新响应
     * @throws ElasticsearchException 如果更新失败
     */
    UpdateResponse update(T document);

    /**
     * 批量更新文档
     *
     * @param documents 要更新的文档列表，需要包含ID
     * @return 批量更新响应
     * @throws ElasticsearchException 如果批量更新失败
     */
    BulkResponse updateBatch(List<T> documents);

    /**
     * 根据ID查询单条数据
     *
     * @param id 文档ID
     * @return 单条搜索结果，如果没有找到则返回null
     * @throws ElasticsearchException 如果查询失败
     */
    T findById(String id);

    /**
     * 根据ID列表批量查询数据
     *
     * @param ids 文档ID列表
     * @return 搜索结果列表
     * @throws ElasticsearchException 如果查询失败
     */
    List<T> findByIds(List<String> ids);

    /**
     * 根据SearchSourceBuilder进行查询
     *
     * @param searchSourceBuilder 搜索源构建器
     * @param pagination 分页参数
     * @param sort 排序参数
     * @return 搜索结果列表
     * @throws ElasticsearchException 如果查询失败
     */
    List<T> find(SearchSourceBuilder searchSourceBuilder, PaginationES pagination, SortES sort);

    /**
     * 根据SearchSourceBuilder进行查询（简化版本）
     *
     * @param searchSourceBuilder 搜索源构建器
     * @return 搜索结果列表
     * @throws ElasticsearchException 如果查询失败
     */
    List<T> find(SearchSourceBuilder searchSourceBuilder);

    /**
     * 根据SearchSourceBuilder进行查询（带排序）
     *
     * @param searchSourceBuilder 搜索源构建器
     * @param sort 排序参数
     * @return 搜索结果列表
     * @throws ElasticsearchException 如果查询失败
     */
    List<T> find(SearchSourceBuilder searchSourceBuilder, SortES sort);

    /**
     * 根据SearchSourceBuilder进行查询，只返回一条数据
     *
     * @param searchSourceBuilder 搜索源构建器
     * @return 单条搜索结果，如果没有找到则返回null
     * @throws ElasticsearchException 如果查询失败
     */
    T findOne(SearchSourceBuilder searchSourceBuilder);

    /**
     * 根据SearchSourceBuilder进行查询，只返回一条数据（带排序）
     *
     * @param searchSourceBuilder 搜索源构建器
     * @param sort 排序参数
     * @return 单条搜索结果，如果没有找到则返回null
     * @throws ElasticsearchException 如果查询失败
     */
    T findOne(SearchSourceBuilder searchSourceBuilder, SortES sort);

    /**
     * 根据SearchSourceBuilder进行查询，只返回一条数据（带分页和排序）
     *
     * @param searchSourceBuilder 搜索源构建器
     * @param pagination 分页参数
     * @param sort 排序参数
     * @return 单条搜索结果，如果没有找到则返回null
     * @throws ElasticsearchException 如果查询失败
     */
    T findOne(SearchSourceBuilder searchSourceBuilder, PaginationES pagination, SortES sort);

    /**
     * 统计当前索引下的数据总数
     *
     * @return 数据总数
     * @throws ElasticsearchException 如果查询失败
     */
    Long count();

    /**
     * 根据SearchSourceBuilder统计符合条件的文档数量
     *
     * @param searchSourceBuilder 搜索源构建器
     * @return 符合条件的文档数量
     * @throws ElasticsearchException 如果查询失败
     */
    Long count(SearchSourceBuilder searchSourceBuilder);
}
