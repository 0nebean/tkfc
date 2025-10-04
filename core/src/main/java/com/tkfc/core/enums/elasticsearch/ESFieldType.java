package com.tkfc.core.enums.elasticsearch;

/**
 * @author 0neBean
 * @since  2024/1/18
 */
public enum ESFieldType {

	Text("text"),
	Byte("byte"),
	Short("short"),
	Integer("integer"),
	Long("long"),
	Date("date"),
	Float("float"),
	Double("double"),
	Object("object"),
	Boolean("boolean"),
	Keyword("keyword"),
	NESTED("nested");

	ESFieldType(String typeName) {
		this.typeName = typeName;
	}

	public final String typeName;
}
