package org.teiid.designer.runtime;

import java.util.Properties;

import org.teiid.core.util.HashCodeUtil;

import com.metamatrix.core.util.CoreArgCheck;

public class TeiidDataSource  implements Comparable<TeiidDataSource> {

	private final String displayName;
    private final String dataSourceName;
    private final String dataSourceType;
    private String connectionProfileName;
    private final Properties properties;
    
    private final ExecutionAdmin admin;


	public TeiidDataSource( String displayName, String dataSourceName, String dataSourceType, ExecutionAdmin admin) {
        CoreArgCheck.isNotEmpty(dataSourceName, "dataSourceName"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(dataSourceType, "dataSourceType"); //$NON-NLS-1$

        this.displayName = displayName;
        this.dataSourceName = dataSourceName;
        this.dataSourceType = dataSourceType;
        this.properties = new Properties();
        this.admin = admin;
    }
    
    public TeiidDataSource( String displayName,  String dataSourceName, String dataSourceType, Properties properties, ExecutionAdmin admin) {
        CoreArgCheck.isNotEmpty(dataSourceName, "dataSourceName"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(dataSourceType, "dataSourceType"); //$NON-NLS-1$

        this.displayName = displayName;
        this.dataSourceName = dataSourceName;
        this.dataSourceType = dataSourceType;
        this.properties = properties;
        this.admin = admin;
    }
    

    public ExecutionAdmin getAdmin() {
		return admin;
	}

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( TeiidDataSource dataSource ) {
        CoreArgCheck.isNotNull(dataSource, "dataSource"); //$NON-NLS-1$
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

        TeiidDataSource other = (TeiidDataSource)obj;

        if (getName().equals(other.getName())) return true;

        return false;
    }
    

    public String getDisplayName() {
    	if( this.connectionProfileName != null ) {
    		return this.displayName + ":" + this.connectionProfileName; //$NON-NLS-1$
    	}
        return this.displayName;
    }


    public String getName() {
        return this.dataSourceName;
    }
    
    /**
     * Returns the data source type name
     * 
     * @return the type
     */
    public String getType() {
        return this.dataSourceType;
    }


    public Properties getProperties() {
        return this.properties;
    }


    public String getPropertyValue( String name ) {
        return this.properties.getProperty(name);
    }
    
    public void setProfileName( String name ) {
    	this.connectionProfileName = name;
    }

    public String getProfileName() {
    	return this.connectionProfileName;
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
    	return "Teiid Data Source:\t" + getDisplayName() + "\nJNDI Name: \t\t" + getName();  //$NON-NLS-1$//$NON-NLS-2$
    }
}
