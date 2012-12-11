/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.validator;

import java.util.List;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.validator.IUpdateValidator;
import org.teiid.designer.validator.IValidator.IValidatorReport;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.validator.UpdateValidator;
import org.teiid.query.validator.UpdateValidator.UpdateType;
import org.teiid.query.validator.ValidatorReport;
import org.teiid82.sql.impl.CrossQueryMetadata;
import org.teiid82.sql.impl.SyntaxFactory;

/**
 *
 */
public class UpdateValidatorImpl implements IUpdateValidator {

    private final UpdateValidator delegateVisitor;
    
    private final SyntaxFactory factory = new SyntaxFactory();
    
    /**
     * @param crossMetadata
     * @param insertType
     * @param updateType
     * @param deleteType
     */
    public UpdateValidatorImpl(CrossQueryMetadata crossMetadata,
                               UpdateType insertType,
                               UpdateType updateType,
                               UpdateType deleteType) {
        delegateVisitor = new UpdateValidator(crossMetadata, insertType, updateType, deleteType);
    }

    @Override
    public void validate(ICommand command, List<IElementSymbol> elemSymbols) throws Exception {
        Command dCommand = factory.convert(command);
        List<ElementSymbol> dSymbols = factory.unwrap(elemSymbols);
        
        delegateVisitor.validate(dCommand, dSymbols);
    }

    @Override
    public IValidatorReport getInsertReport() {
        ValidatorReport report = delegateVisitor.getInsertReport();
        return new ValidatorReportImpl(report);
    }

    @Override
    public IValidatorReport getUpdateReport() {
        ValidatorReport report = delegateVisitor.getUpdateReport();
        return new ValidatorReportImpl(report);
    }

    @Override
    public IValidatorReport getDeleteReport() {
        ValidatorReport report = delegateVisitor.getDeleteReport();
        return new ValidatorReportImpl(report);
    }

    @Override
    public IValidatorReport getReport() {
        ValidatorReport report = delegateVisitor.getReport();
        return new ValidatorReportImpl(report);
    }

}
