package com.tkfc.boot.starter.mybatis.builder.metadata;
import java.io.Serializable;
import java.util.List;

/**
 * DAO对象包装
 * @author 0neBean
 */
public class BeanInfo implements Serializable {

	private static final long serialVersionUID = 6665037435903059802L;

	private String tableName;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private String modelFullName;
	public String getModelFullName() {
		return modelFullName;
	}
	public void setModelFullName(String modelFullName) {
		this.modelFullName = modelFullName;
	}

	private String mapperFullName;
	public String getMapperFullName() {
		return mapperFullName;
	}
	public void setMapperFullName(String mapperFullName) {
		this.mapperFullName = mapperFullName;
	}

	private String orderBy ;
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	private Boolean hasOnDuplicateKeyUpdate;
	public Boolean getHasOnDuplicateKeyUpdate() {
		return hasOnDuplicateKeyUpdate;
	}
	public void setHasOnDuplicateKeyUpdate(Boolean hasOnDuplicateKeyUpdate) {
		this.hasOnDuplicateKeyUpdate = hasOnDuplicateKeyUpdate;
	}

	private Boolean customSystemField;
	public Boolean getOuterSystemModel() {
		return customSystemField;
	}
	public void setOuterSystemModel(Boolean outerSystemModel) {
		customSystemField = outerSystemModel;
	}

	private String logicalDeleteField;
	public String getLogicalDeleteField() {
		return logicalDeleteField;
	}
	public void setLogicalDeleteField(String logicalDeleteField) {
		this.logicalDeleteField = logicalDeleteField;
	}

	private List<PropertyInfo> properties;
	public List<PropertyInfo> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyInfo> properties) {
		this.properties = properties;
	}


}
