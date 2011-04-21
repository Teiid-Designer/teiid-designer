/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.connector.metadata.internal;

import java.util.Map;

public interface IObjectQuery {
	
    public static Integer NO_CASE = new Integer(0);
    public static Integer UPPER_CASE = new Integer(1);
    public static Integer LOWER_CASE = new Integer(2);

	String getTableNameInSource() throws MetadataException;

	String[] getColumnNames();

	void checkType(int i, Object value);

	void checkCaseType(int i, Object value);

	Integer getCaseType(int i);

	Map getCriteria() throws MetadataException;

}