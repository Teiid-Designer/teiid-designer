/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.validator;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.lang.ILanguageObject;

/**
 *
 * @param <L> 
 */
public interface IValidator<L extends ILanguageObject> {
    
    /**
     *
     */
    public interface IValidatorReport {

        /**
         * @return
         */
        boolean hasItems();

        /**
         * @return
         */
        Collection<IValidatorFailure> getItems();
        
    }

    public interface IValidatorFailure {

        /**
         * @return
         */
        IStatus getStatus();
        
    }
    
    /**
     * Validate the given command
     * 
     * @param languageObject
     * @param queryMetadata
     * 
     * @return report of validation
     * @throws Exception 
     */
    IValidatorReport validate(L languageObject, IQueryMetadataInterface queryMetadata) throws Exception;

}
