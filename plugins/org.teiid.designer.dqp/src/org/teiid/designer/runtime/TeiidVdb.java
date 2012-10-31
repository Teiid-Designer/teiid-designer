package org.teiid.designer.runtime;

import org.teiid.adminapi.VDB;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.vdb.Vdb;

/**
 * @since 8.0
 */
public class TeiidVdb implements Comparable<TeiidVdb> {
    public static final String VDB_EXTENSION = "vdb"; //$NON-NLS-1$
    public static final String VDB_DOT_EXTENSION = ".vdb"; //$NON-NLS-1$

    private final VDB vdb;

    private final TeiidServer teiidServer;

    private final boolean isPreview;

    public TeiidVdb( VDB vdb,
                     TeiidServer teiidServer ) {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(teiidServer, "teiidServer"); //$NON-NLS-1$

        this.vdb = vdb;
        this.teiidServer = teiidServer;
        isPreview = Boolean.parseBoolean(vdb.getProperties().getProperty(Vdb.Xml.PREVIEW));
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
	public int compareTo( TeiidVdb vdb ) {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$
        return getName().compareTo(vdb.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        TeiidVdb other = (TeiidVdb)obj;

        if (getName().equals(other.getName())) return true;

        return false;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        return result;
    }

    public String getName() {
        return this.vdb.getName();
    }

    public VDB getVdb() {
        return this.vdb;
    }

    /**
     * @return the VDB version
     */
    public int getVersion() {
        return this.vdb.getVersion();
    }

    /**
     * @return <code>true</code> if this is a preview VDB
     */
    public boolean isPreviewVdb() {
        return isPreview;
    }

    public boolean isActive() {
        return vdb.getStatus().equals(VDB.Status.ACTIVE);
    }
}
