/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.validator;

import org.teiid.core.TeiidComponentException;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.validator.IValidator;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.validator.Validator;
import org.teiid.query.validator.ValidatorReport;
import org.teiid772.sql.impl.CrossQueryMetadata;
import org.teiid772.sql.impl.SyntaxFactory;

/**
 *
 */
public class ValidatorImpl implements IValidator {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public IValidatorReport validate(ILanguageObject languageObject, IQueryMetadataInterface queryMetadata) throws Exception {
        LanguageObject dLanguageObject = factory.convert(languageObject);
        CrossQueryMetadata dMetadata = new CrossQueryMetadata(queryMetadata);
        
        ValidatorReport validateReport;
        try {
            validateReport = Validator.validate(dLanguageObject, dMetadata);
            return new ValidatorReportImpl(validateReport);
        } catch (TeiidComponentException ex) {
            throw new Exception(ex.getMessage());
        }
    }

}
