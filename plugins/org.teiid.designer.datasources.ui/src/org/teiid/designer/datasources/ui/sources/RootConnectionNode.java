package org.teiid.designer.datasources.ui.sources;

public class RootConnectionNode {
	public static int PROFILE = 0;
	public static int DATASOURCE = 1;
	
	String name;
	int type;

	public RootConnectionNode(String name, int type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isProfile() {
		return type == PROFILE;
	}
	
	public boolean isDataSource() {
		return type == DATASOURCE;
	}
}
