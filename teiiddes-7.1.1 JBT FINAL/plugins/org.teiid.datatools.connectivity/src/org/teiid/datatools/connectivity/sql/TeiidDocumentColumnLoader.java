/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.datatools.connectivity.sqm.core.rte.ICatalogObject;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCTableColumnLoader;

public class TeiidDocumentColumnLoader extends JDBCTableColumnLoader {

	private TeiidDocument document;

	public TeiidDocumentColumnLoader(TeiidDocument teiidDocument) {
		super((ICatalogObject) teiidDocument.getSchema().getCatalog());
		document = teiidDocument;
	}
	
	protected ResultSet createResultSet() throws SQLException {
		try {
			return getCatalogObject().getConnection().getMetaData().getColumns(
					document.getSchema().getCatalog().getName(), document.getSchema().getName(),
					document.getName(), null);
		}
		catch (RuntimeException e) {
			SQLException error = new SQLException(e);
			error.initCause(e);
			throw error;
		}
	}

	
	public static void close(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			// ignored
		}
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			// ignored
		}
	}


}
