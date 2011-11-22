/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;

import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;


/**
 * 
 */
public class RelationalPrimaryKey extends RelationalReference {

    private Collection<RelationalColumn> columns;
    
    public RelationalPrimaryKey( ) {
        super();
        setType(TYPES.PK);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    /**
     * @param name
     */
    public RelationalPrimaryKey( String name ) {
        super(name);
        setType(TYPES.PK);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    /**
     * @return columns
     */
    public Collection<RelationalColumn> getColumns() {
        return columns;
    }
    /**
     * @param columns Sets columns to the specified value.
     */
    public void setColumns( Collection<RelationalColumn> columns ) {
        this.columns = columns;
        handleInfoChanged();
    }
    
    public void addColumn(RelationalColumn column) {
    	if( this.columns.add(column) ) {
    		column.setParent(this);
    		handleInfoChanged();
    	} 
    }
    
    public void setProperties(Properties props) {
        for( Object key : props.keySet() ) {
            String keyStr = (String)key;
            String value = props.getProperty(keyStr);

            if( value != null && value.length() == 0 ) {
                continue;
            }
            
            if( keyStr.equalsIgnoreCase(KEY_NAME) ) {
                setName(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NAME_IN_SOURCE) ) {
                setNameInSource(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DESCRIPTION) ) {
                setDescription(value);
            }
        }
        handleInfoChanged();
    }
    
	@Override
	public void validate() {
		// Walk through the properties for the table and set the status
		super.validate();
		
		if( !this.getStatus().isOK() ) {
			return;
		}
		
		if( this.getColumns().isEmpty() ) {
			setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
						NLS.bind(Messages.validate_error_pkNoColumnsDefined, getName())));
			return;
		}
	}
}
