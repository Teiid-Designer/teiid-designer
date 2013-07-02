/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.HashCodeUtil;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.RelationalPlugin;


/**
 * 
 *
 * @since 8.0
 */
public class RelationalReference implements RelationalConstants {
	public static final String KEY_NAME = "NAME"; //$NON-NLS-1$
    public static final String KEY_NAME_IN_SOURCE = "NAMEINSOURCE"; //$NON-NLS-1$
    public static final String KEY_DESCRIPTION = "DESCRIPTION"; //$NON-NLS-1$
    
    public static final int IGNORE = -1;
    public static final int CREATE_ANYWAY = 0;
    public static final int REPLACE = 1;
    public static final int CREATE_UNIQUE_NAME = 2;
    
    private int type = TYPES.UNDEFINED;
    private RelationalReference parent;
    private String  name;
    private String  nameInSource;
    private String  description;
    
    private int processType;
    
    private IStatus currentStatus;
    
    private boolean isChecked = true;
    
    private int modelType = ModelType.PHYSICAL;
    
    private Properties extensionProperties = new Properties();
    
    private StringNameValidator nameValidator = new StringNameValidator();
    
    /**
     * RelationalReference constructor
     */
    public RelationalReference() {
        super();
        this.processType = CREATE_ANYWAY;
        this.currentStatus = Status.OK_STATUS; 
        this.isChecked = true;
    }
    
    /**
     * RelationalReference constructor
     * @param name the name of the object
     */
    public RelationalReference( String name ) {
        super();
        this.name = name;
        this.processType = CREATE_ANYWAY;
        this.isChecked = true;
    }
    


    /* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * @param obj the relational reference
	 */
	public void inject(RelationalReference obj) {
		
	}
	/**
     * @return parent
     */
    public RelationalReference getParent() {
        return parent;
    }

    /**
     * @param parent Sets parent to the specified value.
     */
    public void setParent( RelationalReference parent ) {
        this.parent = parent;
        handleInfoChanged();
    }
    /**
     * @return name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name Sets name to the specified value.
     */
    public void setName( String name ) {
    	if( StringUtilities.areDifferent(this.name, name) ) {
    		this.name = name;
    		handleInfoChanged();
    	}
    }
    /**
     * @return nameInSource
     */
    public String getNameInSource() {
        return nameInSource;
    }
    /**
     * @param nameInSource Sets nameInSource to the specified value.
     */
    public void setNameInSource( String nameInSource ) {
    	if( StringUtilities.areDifferent(this.nameInSource, nameInSource) ) {
    		this.nameInSource = nameInSource;
    		handleInfoChanged();
    	} 
    }
    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description Sets description to the specified value.
     */
    public void setDescription( String description ) {
    	if( StringUtilities.areDifferent(this.description, description) ) {
    		this.description = description;
    		handleInfoChanged();
    	} 
    }
    
    /**
     * @return the model type
     */
    public int getModelType() {
        return this.modelType;
    }
    
    /**
     * @param value the model type
     */
    public void setModelType(int value) {
        this.modelType = value;
    }
    
    /**
     * @return type
     */
    public int getType() {
        return type;
    }
    /**
     * @param name Sets name to the specified value.
     */
    protected void setType( int type ) {
        this.type = type;
        
    }
    
    /**
     * @return the process type
     */
    public int getProcessType() {
        return this.processType;
    }

    /**
     * @param value the type of processing
     * 
     */
    public void setDoProcessType(int value) {
        this.processType = value;
    }
    
    /**
     * @return the isChecked state
     */
    public boolean isChecked() {
        return this.isChecked;
    }

    /**
     * sets selected flag
     * @param isChecked 'true' if the item is selected
     * 
     */
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }    

    /**
     * Set the extension properties
     * @param extProps the extension properties
     */
    public void setExtensionProperties(Properties extProps) {
    	clearExtensionProperties();
    	if(extProps!=null) {
    		Set<Object> propKeys = extProps.keySet();
    		for(Object propKey : propKeys) {
    			String strKey = (String)propKey;
    			String strValue = extProps.getProperty(strKey);
    			int index = strKey.indexOf(':');
    			if(index!=-1) {
    				strKey = strKey.substring(index+1);
    			}
    			// TODO: Supports ID Lookup is not being returned in DDL Options - need to resolve.
    			if(strKey!=null && !strKey.equalsIgnoreCase("Supports ID Lookup")) {  //$NON-NLS-1$
    				addExtensionProperty(strKey,strValue);
    			}
    		}
    	}
    }
    
    /**
     * Add an extension property
     * @param propName property name
     * @param propValue property value
     */
    public void addExtensionProperty(String propName, String propValue) {
    	if(propName!=null) this.extensionProperties.put(propName,propValue);
    }
    
    /**
     * remove an extension property
     * @param propName property name
     */
    public void removeExtensionProperty(String propName) {
    	this.extensionProperties.remove(propName);
    }
    
    /**
     * clear the extension properties
     */
    public void clearExtensionProperties() {
    	this.extensionProperties.clear();
    }

    /**
     * @return the extension properties
     */
    public Properties getExtensionProperties() {
    	return this.extensionProperties;
    }
    
    
    
    /**
     * @return the display name
     */
    public String getDisplayName() {
    	return TYPE_NAMES[getType()];
    }

    /**
     * @param status the status
     * 
     */
    public void setStatus(IStatus status) {
    	this.currentStatus = status;
    }

    /**
     * @return the current status
     */
    public IStatus getStatus() {
    	return this.currentStatus;
    }

    /**
     * @return the string name validator
     */
    public StringNameValidator getNameValidator() {
    	return this.nameValidator;
    }

    /**
     * @param nameValidator the name validator
     * 
     */
    public void setNameValidator(StringNameValidator nameValidator) {
    	CoreArgCheck.isNotNull(nameValidator, "nameValidator"); //$NON-NLS-1$
    	this.nameValidator = nameValidator;
    }
    
    protected void handleInfoChanged() {
    	validate();
    }
    
    /**
     * Check name validity
     * @return 'true' if value, 'false' if not.
     */
    public final boolean nameIsValid() {
		if( this.getName() == null || this.getName().length() == 0 ) {
			return false;
		}
		// Validate non-null string
		String errorMessage = getNameValidator().checkValidName(this.getName());
		if( errorMessage != null && !errorMessage.isEmpty() ) {
			return false;
		}
		return true;
    }

    /**
     * 
     */
    public void validate() {
		if( this.getName() == null || this.getName().length() == 0 ) {
			setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
						NLS.bind(Messages.validate_error_nameCannotBeNullOrEmpty, getDisplayName())));
			return;
		}
		// Validate non-null string
		String errorMessage = getNameValidator().checkValidName(this.getName());
		if( errorMessage != null && !errorMessage.isEmpty() ) {
			setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, errorMessage));
			return;
		}
		setStatus(Status.OK_STATUS);
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		sb.append(" : name = ").append(getName()); //$NON-NLS-1$
		return sb.toString();
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object object ) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        final RelationalReference other = (RelationalReference)object;

        // string properties
        if (!CoreStringUtil.valuesAreEqual(getName(), other.getName())
                || !CoreStringUtil.valuesAreEqual(getNameInSource(), other.getNameInSource())
                || !CoreStringUtil.valuesAreEqual(getDescription(), other.getDescription())) {
            return false;
        }
        
        if( !(getType()==other.getType()) ) {
        	return false;
        }
        if( !(getModelType()==other.getModelType()) ) {
        	return false;
        }
        if( !(getProcessType()==other.getProcessType()) ) {
        	return false;
        }
        if(!getExtensionProperties().equals(other.getExtensionProperties())) {
        	return false;
        }

        return true;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = HashCodeUtil.hashCode(0, getType());

        result = HashCodeUtil.hashCode(result, getType());
        result = HashCodeUtil.hashCode(result, getModelType());
        result = HashCodeUtil.hashCode(result, getProcessType());
        
        // string properties
        if (!CoreStringUtil.isEmpty(getName())) {
            result = HashCodeUtil.hashCode(result, getName());
        }
        
        if (!CoreStringUtil.isEmpty(getNameInSource())) {
            result = HashCodeUtil.hashCode(result, getNameInSource());
        }

        if (getDescription() != null && !getDescription().isEmpty()) {
            result = HashCodeUtil.hashCode(result, getDescription());
        }

        if ((this.extensionProperties != null) && !this.extensionProperties.isEmpty()) {
        	Iterator<Object> keyIter = this.extensionProperties.keySet().iterator();
        	while(keyIter.hasNext()) {
        		String key = (String)keyIter.next();
        		String value = this.extensionProperties.getProperty(key);
        		result = HashCodeUtil.hashCode(result, key);
        		result = HashCodeUtil.hashCode(result, value);
        	}
        }

        return result;
    } 
    
    /**
     * Reference comparator
     */
    public class ReferenceComparator implements Comparator<RelationalReference> {
    	@Override
    	public int compare(RelationalReference x, RelationalReference y) {
    		RelationalReference xParent = x.getParent();
    		RelationalReference yParent = y.getParent();

    		// if either of parents null, just use names
    		if(xParent==null || yParent==null) {
        	    return x.getName().compareTo(y.getName());
    		}
    		
    		int parentResult = xParent.getName().compareTo(yParent.getName());
    	    if (parentResult != 0) return parentResult;

    	    // if parent names match, use reference name
    	    return x.getName().compareTo(y.getName());
    	}

    }       
}
