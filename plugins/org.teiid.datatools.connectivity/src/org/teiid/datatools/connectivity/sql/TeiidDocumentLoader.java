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
import org.teiid.datatools.models.teiidsqlmodel.TeiidsqlmodelFactory;

public class TeiidDocumentLoader extends JDBCBaseLoader {

	private static final String DOCUMENT_QUERY = null;
	private static final String DOCUMENT_NAME = "TABLE_NAME";

	public TeiidDocumentLoader(ICatalogObject catalogObject) {
		super(catalogObject, null);
	}

	public TeiidDocumentLoader(ICatalogObject catalogObject,
			IConnectionFilterProvider connectionFilterProvider) {
		super(catalogObject, connectionFilterProvider);
	}

	public void loadDocuments(List container, List existingDocuments) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			rs = createResultSet();

			String dbEventName = null;
			while (rs.next()) {
				dbEventName = rs.getString(DOCUMENT_NAME).trim();

				Document dbEvent = (Document) getAndRemoveSQLObject(
						existingDocuments, dbEventName);

				if (dbEvent == null) {
					dbEvent = processRow(rs);
					if (dbEvent != null) {
						container.add(dbEvent);
					}
				} else {
					container.add(dbEvent);
					if (dbEvent instanceof ICatalogObject) {
						((ICatalogObject) dbEvent).refresh();
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
		// document.setSchema(((Schema) getCatalogObject()).getSchema());
	}

	protected Document createDocument() {
		return TeiidsqlmodelFactory.eINSTANCE.createDocument();
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
