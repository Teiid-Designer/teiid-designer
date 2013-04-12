package org.teiid.designer.teiidimporter.ui.panels;

import java.io.Serializable;

/**
 * Table Item for DataSource obj which tracks Name and Type
 */
public class DataSourceItem extends Object implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String driver;

	/**
	 * 
	 */
	public DataSourceItem() {
	}
		
	/**
	 * Get the DataSource name
	 * @return the dataSource name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the DataSource name
	 * @param name the dataSource name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the DataSource driver (jar or rar)
	 * @return the dataSource driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * Set the DataSource driver (jar or rar name)
	 * @param driver the DataSource driver
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

}