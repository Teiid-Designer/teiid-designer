/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.datatools.connectivity.sqm.core.rte.ICatalogObject;
import org.eclipse.datatools.connectivity.sqm.loader.IConnectionFilterProvider;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCBaseLoader;
import org.eclipse.datatools.connectivity.sqm.loader.Messages;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.teiid.datatools.models.teiidsqlmodel.Document;

public class TeiidDocumentLoader extends JDBCBaseLoader {

	private static final String DOCUMENT_NAME = "TABLE_NAME"; //$NON-NLS-1$

	public TeiidDocumentLoader(ICatalogObject catalogObject) {
		super(catalogObject, null);
	}

	public TeiidDocumentLoader(ICatalogObject catalogObject,
			IConnectionFilterProvider connectionFilterProvider) {
		super(catalogObject, connectionFilterProvider);
	}

	@SuppressWarnings({ "unchecked" })
	public void loadDocuments(List container, List existingDocuments) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			rs = createResultSet();

			String docName = null;
			while (rs.next()) {
				docName = rs.getString(DOCUMENT_NAME).trim();

				Document doc = (Document) getAndRemoveSQLObject(
						existingDocuments, docName);

				if (doc == null) {
					doc = processRow(rs);
					if (doc != null) {
						container.add(doc);
					}
				} else {
					container.add(doc);
					if (doc instanceof ICatalogObject) {
						((ICatalogObject) doc).refresh();
					}
				}
			}
		} finally {
			close(rs);
			close(stmt);
		}
	}
	
	public void clearDocuments(List documents) {
		documents.clear();
	}

	protected ResultSet createResultSet()
			throws SQLException {
		try {
			Schema schema = getSchema();
			return getCatalogObject().getConnection().getMetaData().getTables(
					schema.getCatalog().getName(), schema.getName(),
					getJDBCFilterPattern(), null);
		}
		catch (RuntimeException e) {
			SQLException error = new SQLException(MessageFormat.format(
					Messages.Error_Unsupported_DatabaseMetaData_Method,
					new Object[] { "java.sql.DatabaseMetaData.getTables()"})); //$NON-NLS-1$
			error.initCause(e);
			throw error;
		}
	}

	protected Document processRow(ResultSet rs) throws SQLException {
		Document document = createDocument();
		initialize(document, rs);
		return document;
	}

	protected void initialize(Document document, ResultSet rs)
			throws SQLException {
		String triggerName = rs.getString(DOCUMENT_NAME).trim();
		document.setName(triggerName);
	}

	protected Document createDocument() {
		return new TeiidDocument();
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
	
	/**
	 * Utility method.
	 * 
	 * @return returns the catalog object being operated upon as a Schema (i.e.
	 *         (Schema) getCatalogObject()).
	 */
	protected Schema getSchema() {
		return (Schema) getCatalogObject();
	}


}
