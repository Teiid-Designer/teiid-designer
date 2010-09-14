/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.dse.provider;

import org.eclipse.datatools.connectivity.sqm.core.internal.ui.explorer.services.IVirtualNodeServiceFactory;
import org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCColumn;
import org.eclipse.datatools.connectivity.sqm.core.ui.services.IDataToolsUIServiceManager;
import org.eclipse.datatools.connectivity.sqm.server.internal.ui.explorer.providers.content.impl.ServerExplorerContentProviderNav;
import org.eclipse.datatools.connectivity.sqm.server.internal.ui.util.resources.ResourceLoader;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.teiid.datatools.connectivity.sql.TeiidCatalogSchema;
import org.teiid.datatools.connectivity.sql.TeiidDocument;

/**
 * 
 */
public class TeiidContentProvider extends ServerExplorerContentProviderNav implements ICommonContentProvider {

    protected static final IVirtualNodeServiceFactory nodeFactory = IDataToolsUIServiceManager.INSTANCE.getVirtualNodeServiceFactory();

    protected static final ResourceLoader resourceLoader = ResourceLoader.INSTANCE;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.datatools.connectivity.sqm.server.internal.ui.explorer.providers.content.impl.ServerExplorerContentProviderNav#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement ) {
        if (parentElement instanceof TeiidCatalogSchema) {
            TeiidDocumentsFolder folder = new TeiidDocumentsFolder("Documents", "Documents", //$NON-NLS-1$ //$NON-NLS-2$
                                                                   parentElement);
            return new Object[] {folder};
        } else if (parentElement instanceof TeiidDocumentsFolder) {
        	TeiidDocumentsFolder docFolder = (TeiidDocumentsFolder) parentElement;
        	TeiidCatalogSchema schema = (TeiidCatalogSchema) docFolder.getParent();
			docFolder.addChildren(schema.getDocuments());
			return docFolder.getChildrenArray();
        } else if (parentElement instanceof TeiidDocument) {
        	   DocumentColumnFolder folder = new DocumentColumnFolder("Columns", "Columns", //$NON-NLS-1$ //$NON-NLS-2$
                       parentElement);
        	   return new Object[] {folder};
        } else if (parentElement instanceof DocumentColumnFolder) {
        	DocumentColumnFolder folder = (DocumentColumnFolder) parentElement;
        	TeiidDocument doc = (TeiidDocument) folder.getParent();
        	return doc.getColumns().toArray();
        } else if (parentElement instanceof JDBCColumn && ((JDBCColumn)parentElement).eContainer() instanceof TeiidDocument) {
        	return new Object[]{};
        }
        return super.getChildren(parentElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.ICommonContentProvider#init(org.eclipse.ui.navigator.ICommonContentExtensionSite)
     */
    public void init( ICommonContentExtensionSite config ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.IMementoAware#restoreState(org.eclipse.ui.IMemento)
     */
    public void restoreState( IMemento memento ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.IMementoAware#saveState(org.eclipse.ui.IMemento)
     */
    public void saveState( IMemento memento ) {
    }

}
