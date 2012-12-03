/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.validator;

import java.util.List;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.validator.IValidator.IValidatorReport;

/**
 *
 */
public interface IUpdateValidator {

    /**
     * Update type enumerator
     */
    public enum TransformUpdateType {
        /**
         * The default handling should be used
         */
        INHERENT, 
        /**
         * An instead of trigger (TriggerAction) has been defined
         */
        INSTEAD_OF
    }
    
    /**
     * Validate the command
     * 
     * @param command
     * @param elemSymbols
     * @throws Exception 
     */
    void validate(ICommand command, List<IElementSymbol> elemSymbols) throws Exception;

    /**
     * @return insert report
     */
    IValidatorReport getInsertReport();

    /**
     * @return update report
     */
    IValidatorReport getUpdateReport();

    /**
     * @return delete report
     */
    IValidatorReport getDeleteReport();

    /**
     * @return report
     */
    IValidatorReport getReport();
}
