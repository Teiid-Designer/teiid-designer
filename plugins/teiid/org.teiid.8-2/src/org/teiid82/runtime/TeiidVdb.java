package org.teiid82.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.VDB;
import org.teiid.core.util.ArgCheck;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidVdb;

/**
 * @since 8.0
 */
public class TeiidVdb implements ITeiidVdb, Comparable<TeiidVdb> {
    
    private static final String PREVIEW = "preview"; //$NON-NLS-1$
    
    private final VDB vdb;

    private final ITeiidServer teiidServer;

    private final boolean isPreview;

    public TeiidVdb( VDB vdb,
                     ITeiidServer teiidServer ) {
        ArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$
        ArgCheck.isNotNull(teiidServer, "teiidServer"); //$NON-NLS-1$

        this.vdb = vdb;
        this.teiidServer = teiidServer;
        isPreview = Boolean.parseBoolean(vdb.getProperties().getProperty(PREVIEW));
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.runtime.impl.ITeiidVdb#compareTo(org.teiid.designer.runtime.impl.TeiidVdb)
     */
    @Override
	public int compareTo( TeiidVdb vdb ) {
        ArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$
        return getName().compareTo(vdb.getName());
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.runtime.impl.ITeiidVdb#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        ITeiidVdb other = (ITeiidVdb)obj;

        if (getName().equals(other.getName())) return true;

        return false;
    }
    
    /* (non-Javadoc)
     * @see org.teiid.designer.runtime.impl.ITeiidVdb#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.runtime.impl.ITeiidVdb#getName()
     */
    @Override
    public String getName() {
        return this.vdb.getName();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.runtime.impl.ITeiidVdb#getVersion()
     */
    @Override
    public int getVersion() {
        return this.vdb.getVersion();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.runtime.impl.ITeiidVdb#isPreviewVdb()
     */
    @Override
    public boolean isPreviewVdb() {
        return isPreview;
    }

    @Override
    public boolean isActive() {
        return vdb.getStatus().equals(VDB.Status.ACTIVE);
    }
    
    @Override
    public boolean isLoading() {
        return vdb.getStatus().equals(VDB.Status.LOADING);
    }
    
    @Override
    public boolean hasFailed() {
        return vdb.getStatus().equals(VDB.Status.FAILED);
    }
    
    @Override
    public boolean wasRemoved() {
        return vdb.getStatus().equals(VDB.Status.REMOVED);
    }

    @Override
    public List<String> getValidityErrors() {
        List<String> errors = vdb.getValidityErrors();
        
        if (errors != null)
            return Collections.unmodifiableList(errors);
        
        return Collections.emptyList();
    }

    @Override
    public boolean hasModels() {
        return vdb.getModels().isEmpty();
    }
    
    @Override
    public Collection<String> getModelNames() {
        if (! hasModels())
            return Collections.emptyList();
        
        List<String> names = new ArrayList<String>();
        for (Model model : vdb.getModels()) {
            names.add(model.getName());
        }
        
        return names;
    }

    @Override
    public String getPropertyValue(String key) {
        return vdb.getPropertyValue(key);
    }
}
