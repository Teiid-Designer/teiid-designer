/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.CoreArgCheck;
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
@SuppressWarnings("javadoc")
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
    
    private int modelType = ModelType.PHYSICAL;
    
    private StringNameValidator nameValidator = new StringNameValidator();
    
    /**
     * 
     */
    public RelationalReference() {
        super();
        this.processType = CREATE_ANYWAY;
        this.currentStatus = Status.OK_STATUS; 
    }
    /**
     * @param name the name of the object
     */
    public RelationalReference( String name ) {
        super();
        this.name = name;
        this.processType = CREATE_ANYWAY;
    }
    


    /* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * @param obj
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
}
