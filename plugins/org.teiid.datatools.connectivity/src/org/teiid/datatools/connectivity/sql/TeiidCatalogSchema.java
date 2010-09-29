/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.sql;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.teiid.datatools.models.teiidsqlmodel.impl.TeiidSchemaImpl;

public class TeiidCatalogSchema extends TeiidSchemaImpl {

    private Boolean documentsLoaded = Boolean.FALSE;
    private final Object documents_LOCK = new Object();
	@SuppressWarnings("unchecked")
	private SoftReference documentLoaderRef;

    protected TeiidCatalogSchema() {
        super();
    }

    //@SuppressWarnings("rawtypes")
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

	@SuppressWarnings({ "unchecked" })
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
