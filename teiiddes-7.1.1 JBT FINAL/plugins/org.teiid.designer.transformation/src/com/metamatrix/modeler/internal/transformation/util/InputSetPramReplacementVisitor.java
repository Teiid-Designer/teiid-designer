/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.ArrayList;
import java.util.List;
import org.teiid.core.id.UUID;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.visitor.ExpressionMappingVisitor;

/**
 * <p>
 * This visitor class will traverse a language object tree, it finds variables in the language object and replaces the
 * inputsetparameters with a <code>Reference</code> obj.,/p>
 * <p>
 * The easiest way to use this visitor is to call the static method which creates the the visitor by passing it the Langiuage
 * Object and the variable context to be looked up. The public visit() methods should NOT be called directly.
 * </p>
 */
public class InputSetPramReplacementVisitor extends ExpressionMappingVisitor {

    // constant indicting that the inputset groupname
    private static String INPUT_SET = "INPUT"; //$NON-NLS-1$

    // index of the referenc on the language object
    private int refCounter = 0;

    // inputset parameter names collected diring visitation
    private List inputSetParams;

    // inputset parameter names supplied externally
    private List inputSetParamNames;

    /**
     * Construct a new visitor with the list of references.
     * 
     * @param references A list of references on to be collected
     */
    public InputSetPramReplacementVisitor() {
        super(null);
        this.inputSetParams = new ArrayList();
    }

    public void setInputSetParamNames( final List inputSetParamNames ) {
        this.inputSetParamNames = inputSetParamNames;
    }

    // ############### Visitor methods for language objects ##################

    /**
     * <p>
     * Check the elementsymbol if it is an INPUT_SET parameter (element is an inputSet parameter if its group name is input), if
     * it is return a reference object which would be the placeholder for the parameter in the sql, else return back
     * elementsymbol.
     * </p>
     * 
     * @param variable The elementSymbol that could be a inputSet param
     * @return A reference object incase element is an inputSet param, else return the element
     */

    @Override
    public Expression replaceExpression( Expression element ) {
        if (!(element instanceof ElementSymbol)) {
            return element;
        }

        String elementName = ((ElementSymbol)element).getName();
        int index = elementName.lastIndexOf("."); //$NON-NLS-1$
        if (index != -1) {
            String grpName = elementName.substring(0, index);
            if (grpName.equalsIgnoreCase(INPUT_SET)) {
                this.inputSetParams.add(elementName.substring(index + 1));
                return new Reference(refCounter++);
            }
        }
        // If the element symbol does not have a group prefix then
        // check it against any supplied input parameter names
        if (index == -1 && !elementName.startsWith(UUID.PROTOCOL)) {
            if (this.inputSetParamNames != null && this.inputSetParamNames.contains(elementName)) {
                this.inputSetParams.add(elementName);
                return new Reference(refCounter++);
            }
        }

        return element;
    }

    /**
     * <p>
     * Get the list of inputset parameters collected.
     * </p>
     * 
     * @param obj The Language object that is to be visited
     * @return Return a list of references collected
     * @throws QueryValidatorException
     */
    public List getParameters() {
        return this.inputSetParams;
    }
}
