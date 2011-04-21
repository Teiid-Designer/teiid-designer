/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.query;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.teiid.query.sql.lang.Command;

/** 
 * ValidationResult, utility containign the status of validating sql.
 */
public interface QueryValidationResult {

    /**
     * Get the Parsable status
     * @return 'true' if parsable, 'false' if not
     */
    boolean isParsable();
    
    /**
     * Get the Resolvable status
     * @return 'true' if resolvable, 'false' if not
     */
    boolean isResolvable();
    
    /**
     * Get the Validatable status
     * @return 'true' if validatable, 'false' if not
     */
    boolean isValidatable();
    
	/**
	 * Get the Command language object.  This will be null if the
	 * SQL String was not parsable.
	 * @return the SQL command
	 */
	Command getCommand();
	
    /**
     * Get the status List indicating the success/ failure of validation
     * @return the Collection of IStatus objects
     */
    Collection<IStatus> getStatusList();
}
