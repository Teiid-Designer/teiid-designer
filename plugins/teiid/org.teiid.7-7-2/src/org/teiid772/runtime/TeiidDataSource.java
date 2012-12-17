package org.teiid772.runtime;

import java.util.Properties;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.HashCodeUtil;
import org.teiid.designer.runtime.spi.ITeiidDataSource;

/**
 * @since 8.0
 */
public class TeiidDataSource implements Comparable<TeiidDataSource>, ITeiidDataSource {

    private final String displayName;
    private final String dataSourceName;
    private final String dataSourceType;
    private String connectionProfileName;
    private final Properties properties;

    private boolean isPreview = false;

    public TeiidDataSource( String displayName,
                            String dataSourceName,
                            String dataSourceType ) {
        this(displayName, dataSourceName, dataSourceType, new Properties());
    }

    public TeiidDataSource( String displayName,
                            String dataSourceName,
                            String dataSourceType,
                            Properties properties) {
        ArgCheck.isNotEmpty(dataSourceName, "dataSourceName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(dataSourceType, "dataSourceType"); //$NON-NLS-1$

        this.displayName = displayName;
        this.dataSourceName = dataSourceName;
        this.dataSourceType = dataSourceType;
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( TeiidDataSource dataSource ) {
        ArgCheck.isNotNull(dataSource, "dataSource"); //$NON-NLS-1$
        return getName().compareTo(dataSource.getName());
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

        ITeiidDataSource other = (ITeiidDataSource)obj;

        if (getName().equals(other.getName())) return true;

        return false;
    }

    @Override
    public String getDisplayName() {
        if (this.connectionProfileName != null) {
            return this.displayName + ":" + this.connectionProfileName; //$NON-NLS-1$
        }
        return this.displayName;
    }

    @Override
    public String getName() {
        return this.dataSourceName;
    }

    /**
     * Returns the data source type name
     * 
     * @return the type
     */
    @Override
    public String getType() {
        return this.dataSourceType;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public String getPropertyValue( String name ) {
        return this.properties.getProperty(name);
    }

    @Override
    public void setProfileName( String name ) {
        this.connectionProfileName = name;
    }

    @Override
    public String getProfileName() {
        return this.connectionProfileName;
    }

    /**
     * @return isPreview
     */
    @Override
    public boolean isPreview() {
        return isPreview;
    }

    /**
     * @param isPreview Sets isPreview to the specified value.
     */
    @Override
    public void setPreview( boolean isPreview ) {
        this.isPreview = isPreview;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, getName());
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Data Source:\t" + getName()); //$NON-NLS-1$
        if (!getType().equalsIgnoreCase("<unknown>")) { //$NON-NLS-1$
            sb.append("\nType: \t\t" + getType()); //$NON-NLS-1$
        }

        return sb.toString();
    }
}
