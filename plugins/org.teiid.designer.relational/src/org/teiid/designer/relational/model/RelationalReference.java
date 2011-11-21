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
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.RelationalPlugin;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.ModelType;

/**
 * 
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
    
    private int modelType = ModelType.PHYSICAL;
    
    
    public RelationalReference() {
        super();
        this.processType = CREATE_ANYWAY;
        this.currentStatus = Status.OK_STATUS;
    }
    /**
     * @param name
     */
    public RelationalReference( String name ) {
        super();
        this.name = name;
        this.processType = CREATE_ANYWAY;
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
    	if( !StringUtilities.equals(this.name, name) ) {
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
    	if( !StringUtilities.equals(this.nameInSource, nameInSource) ) {
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
     * @param name Sets name to the specified value.
     */
    public void setDescription( String description ) {
    	if( !StringUtilities.equals(this.description, description) ) {
    		this.description = description;
    		handleInfoChanged();
    	} 
    }
    
    public int getModelType() {
        return this.modelType;
    }
    
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
    
    public int getProcessType() {
        return this.processType;
    }
    
    public void setDoProcessType(int value) {
        this.processType = value;
    }
    
    public String getDisplayName() {
    	return TYPE_NAMES[getType()];
    }
;    
    public void setStatus(IStatus status) {
    	this.currentStatus = status;
    }
    
    public IStatus getStatus() {
    	return this.currentStatus;
    }
    
    protected void handleInfoChanged() {
    	validate();
    }
    
    public void validate() {
		if( this.getName() == null || this.getName().trim().length() == 0 ) {
			setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
						NLS.bind(Messages.validate_error_nameCannotBeNullOrEmpty, getDisplayName())));
			return;
		}
		setStatus(Status.OK_STATUS);
    }
}
