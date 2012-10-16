/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.impl;

import java.util.HashMap;
import java.util.Map;
import org.teiid.core.designer.id.ObjectID;
import org.teiid.core.designer.id.UUID;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.util.Assertion;
import org.teiid.core.util.EquivalenceUtil;
import org.teiid.core.designer.HashCodeUtil;
import org.teiid.designer.core.container.EObjectFinder;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metadata.runtime.RuntimeMetadataPlugin;
import org.teiid.logging.LogManager;

/**
 * AbstractMetadataRecord
 *
 * @since 8.0
 */
public abstract class AbstractMetadataRecord implements MetadataRecord {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_INDEX_VERSION = 0;

    private String pathString;
    private String modelName;
    private String resourcePath;
    private int indexVersion = DEFAULT_INDEX_VERSION;
    private char recordType;
    private transient Map propValues;

    protected Object eObject;
    protected MetadataRecordDelegate delegate;
    private EObjectFinder finder;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    public AbstractMetadataRecord() {
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getUUID()
     */
    @Override
	public String getUUID() {
        return this.delegate.getUUID();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getParentUUID()
     */
    @Override
	public String getParentUUID() {
        return this.delegate.getParentUUID();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getNameInSource()
     */
    @Override
	public String getNameInSource() {
        return this.delegate.getNameInSource();
    }

    /**
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getModelName()
     */
    @Override
	public String getModelName() {
        if (this.modelName == null) {
            int prntIdx = getFullName() != null ? getFullName().indexOf(IndexConstants.NAME_DELIM_CHAR) : -1;
            if (prntIdx <= 0) {
                this.modelName = getFullName();
            } else {
                this.modelName = getFullName() != null ? getFullName().substring(0, prntIdx) : null;
            }
        }

        return this.modelName;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getFullName()
     */
    @Override
	public String getFullName() {
        return this.delegate.getFullName();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getName()
     */
    @Override
	public String getName() {
        return this.delegate.getName();
    }

    /*
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getPathString()
     */
    @Override
	public String getPathString() {
        if (this.pathString == null) {
            this.pathString = getFullName() != null ? getFullName().replace(IndexConstants.NAME_DELIM_CHAR, FileUtils.SEPARATOR) : null;
        }
        return this.pathString;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getPath()
     */
    @Override
	public String getPath() {
        return getPathString();
    }

    /**
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getParentFullName()
     * @deprecated the returned value may be incorrect in the case of an XML element (see defects #11326 and #11362)
     */
    @Override
	@Deprecated
    public String getParentFullName() {
        int prntIdx = getFullName() != null ? getFullName().lastIndexOf(IndexConstants.NAME_DELIM_CHAR + getName()) : -1;
        if (prntIdx <= 0) {
            return CoreStringUtil.Constants.EMPTY_STRING;
        }
        return getFullName().substring(0, prntIdx);
    }

    /**
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getParentPathString()
     * @deprecated the returned value may be incorrect in the case of an XML element (see defects #11326 and #11362)
     */
    @Override
	@Deprecated
    public String getParentPathString() {
        String parentFullName = getParentFullName();
        return parentFullName != null ? parentFullName.replace(IndexConstants.NAME_DELIM_CHAR, FileUtils.SEPARATOR) : null;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getRecordType()
     */
    @Override
	public char getRecordType() {
        return this.recordType;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    public void setNameInSource( String string ) {
        this.delegate.setNameInSource(string);
    }

    /**
     * @param path
     */
    public void setFullName( String fullName ) {
        this.delegate.setFullName(fullName);
    }

    /**
     * @param String
     */
    public void setName( String name ) {
        this.delegate.setName(name);
    }

    /**
     * @param string
     */
    public void setUUID( String string ) {
        this.delegate.setUUID(string);
    }

    /**
     * @param string
     */
    public void setParentUUID( String string ) {
        this.delegate.setParentUUID(string);
    }

    /**
     * @return index version number
     */
    public int getIndexVersion() {
        return this.indexVersion;
    }

    /**
     * @param int
     */
    public void setIndexVersion( final int version ) {
        this.indexVersion = version;
    }

    /**
     * @param container The container to set.
     * @since 4.2
     */
    public void setEObjectFinder( EObjectFinder finder ) {
        this.finder = finder;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getEObject()
     */
    @Override
	public Object getEObject() {
        if (eObject != null) {
            return eObject;
        }
        Assertion.isNotNull(this.finder);
        try {
            // get the Object ID given a UUID
            String uuid = getUUID();
            int delimitIndex = uuid.indexOf(ObjectID.DELIMITER);
            UUID objID = DatatypeConstants.stringToObject(uuid.substring(delimitIndex + 1));
            // Return the EObject instance found in the ModelContainer ...
            return this.finder.find(objID);
        } catch (Exception e) {
            LogManager.logWarning(RuntimeMetadataPlugin.PLUGIN_ID, e, e.getMessage());
        }
        return null;
    }

    /**
     * @return
     */
    @Override
	public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @param path
     */
    public void setResourcePath( String path ) {
        resourcePath = path;
    }

    /**
     * @param c
     */
    public void setRecordType( char c ) {
        recordType = c;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getPropertyValue(java.lang.String)
     */
    @Override
	public Object getPropertyValue( String propertyName ) {
        CoreArgCheck.isNotNull(propertyName);
        if (propValues != null) {
            return propValues.get(propertyName);
        }
        return null;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#setPropertyValue(java.lang.String, java.lang.Object)
     */
    @Override
	public void setPropertyValue( String propertyName,
                                  Object propertyVame ) {
        if (propertyName != null && propertyVame != null) {
            if (propValues == null) {
                propValues = new HashMap();
            }
            propValues.put(propertyName, propertyVame);
        }
    }

    @Override
    public String toString() {
        return getFullName();
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Compare two records for equality.
     */
    @Override
    public boolean equals( Object obj ) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof AbstractMetadataRecord)) {
            return false;
        }

        AbstractMetadataRecord other = (AbstractMetadataRecord)obj;

        if (this.getRecordType() != other.getRecordType()) {
            return false;
        }
        if (!EquivalenceUtil.areEqual(this.getUUID(), other.getUUID())) {
            return false;
        }
        if (!EquivalenceUtil.areEqual(this.getParentUUID(), other.getParentUUID())) {
            return false;
        }
        if (!EquivalenceUtil.areEqual(this.getFullName(), other.getFullName())) {
            return false;
        }
        if (!EquivalenceUtil.areEqual(this.getNameInSource(), other.getNameInSource())) {
            return false;
        }

        return true;
    }

    /**
     * Get hashcode for From. WARNING: The hash code relies on the variables in the record, so changing the variables will change
     * the hash code, causing a select to be lost in a hash structure. Do not hash a record if you plan to change it.
     */
    @Override
    public int hashCode() {
        int myHash = 0;
        myHash = HashCodeUtil.hashCode(myHash, this.recordType);
        myHash = HashCodeUtil.hashCode(myHash, this.delegate.getFullName());
        myHash = HashCodeUtil.hashCode(myHash, this.delegate.getUUID());
        myHash = HashCodeUtil.hashCode(myHash, this.delegate.getParentUUID());
        myHash = HashCodeUtil.hashCode(myHash, this.delegate.getNameInSource());
        return myHash;
    }

}
