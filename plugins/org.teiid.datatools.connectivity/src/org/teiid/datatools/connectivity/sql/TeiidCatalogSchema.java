package org.teiid.datatools.connectivity.sql;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.datatools.modelbase.sql.schema.impl.SchemaImpl;
import org.eclipse.emf.common.util.EList;
import org.teiid.datatools.models.teiidsqlmodel.impl.TeiidSchemaImpl;

public class TeiidCatalogSchema extends TeiidSchemaImpl {

    private Boolean documentsLoaded = Boolean.FALSE;
    private final Object documents_LOCK = new Object();
	private SoftReference documentLoaderRef;

    protected TeiidCatalogSchema() {
        super();
    }

    public EList getDocuments() {
    	synchronized (documents_LOCK) {
			if (!documentsLoaded.booleanValue())
				loadDocuments();
		}
		return super.getDocuments();
    }

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCSchema#refresh()
	 */
	public void refresh() {
		synchronized (documents_LOCK) {
			if (documentsLoaded.booleanValue()) {
				documentsLoaded = Boolean.FALSE;
			}
		}
		super.refresh();
	}
	
	protected final TeiidDocumentLoader getDocumentLoader() {
		if (documentLoaderRef == null || documentLoaderRef.get() == null) {
			documentLoaderRef = new SoftReference(
					createDocumentLoader());
		}
		return (TeiidDocumentLoader) documentLoaderRef.get();
	}

	private void loadDocuments() {
		synchronized (documents_LOCK) {
			boolean deliver = eDeliver();
			try {
				List container = super.getDocuments();
				List existingDocuments = new ArrayList(container);

				eSetDeliver(false);

				container.clear();
				getDocumentLoader().loadDocuments(container, existingDocuments);
				getDocumentLoader().clearDocuments(existingDocuments);

				documentsLoaded = Boolean.TRUE;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eSetDeliver(deliver);
			}
		}
	}
	
	private TeiidDocumentLoader createDocumentLoader() {
		return new TeiidDocumentLoader(this);
	}

}
