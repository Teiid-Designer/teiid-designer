/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.procedure;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.query.sql.ProcedureReservedWords;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.internal.webservice.procedure.ProcedureCriteriaMappingImpl;


/**
 * Factory to create {com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping} objects.
 * @since 4.3
 */
public class ProcedureCriteriaMappingFactory {
    
    // constants for XPath expression to be generated
    private static String OPERATION_INPUT_PARAM_NAME = "input"; //$NON-NLS-1$
    private static String XPATH_XSD_ELEMENT_NAME = "xpathValueElement"; //$NON-NLS-1$
    private static String XPATH_FUNCTION_PARAM = "'//*[local-name()=\""+XPATH_XSD_ELEMENT_NAME+"\"]'"; //$NON-NLS-1$ //$NON-NLS-2$
    private static String XPATH_FUNCTION = "xpathValue("+OPERATION_INPUT_PARAM_NAME+", "+XPATH_FUNCTION_PARAM+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * Create a {com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping} object given
     * the Input object for the operation and the xsdElement chosen on the schema defining the
     * input xml document. 
     * @param operationInput The input object on the operation defined by the procedure.
     * @param xsdElement The xsdElement on the schema of the input document.
     * @return
     * @since 4.3
     */
    public ProcedureCriteriaMapping createMapping(final Input operationInput, final EObject xsdElement) {
        CoreArgCheck.isNotNull(operationInput);
        CoreArgCheck.isNotNull(xsdElement);
        
        // get the path for the xsd element to be used in XPath expression
        IPath xsdElementPath = ModelerCore.getModelEditor().getModelRelativePath(xsdElement);
        // get the fullName of the operationInput
        SqlAspect sqlAspect = AspectManager.getSqlAspect(operationInput); 
        String inputFullName = sqlAspect.getFullName(operationInput);
        String xPathExpression = generateXPathExpression(inputFullName, xsdElementPath.toString());
        
        // get variable name
        String variablename = generateVariableName(xsdElementPath);        
        
        ProcedureCriteriaMappingImpl mapping = new ProcedureCriteriaMappingImpl();
        mapping.setXPathExpression(xPathExpression);
        mapping.setVariableName(variablename);

        return mapping;        
    }

    /**
     * Generate the XPath expression given the input to the operation and path to the
     * xsd element on the input document's schema.
     * @param operationInputName The fullName of the input object on the operation defined by the procedure.
     * @param xsdElement The xsdElement on the schema of the input document.
     * @return The generated xPath expression.
     * @since 4.3
     */
    public String generateXPathExpression(final String operationInputName, final String xsdElementPath) {
        CoreArgCheck.isNotEmpty(operationInputName);
        CoreArgCheck.isNotEmpty(xsdElementPath);

        // get the expression replacing the tokens with the needed names
        String xPathExpression = XPATH_FUNCTION.replaceAll(OPERATION_INPUT_PARAM_NAME, operationInputName);
        xPathExpression = xPathExpression.replaceAll(XPATH_XSD_ELEMENT_NAME, xsdElementPath);

        return xPathExpression;
    }

    /**
     * Generate variable name given the path to the xsdElement its bounded to.
     * @param xsdElementPath The path to the xsdElement on the schema of the input document.
     * @return The name of the variable generated.
     * @since 4.3
     */
    public String generateVariableName(final IPath xsdElementPath) {
        String xsdElementName = xsdElementPath.lastSegment();
        return ProcedureReservedWords.VARIABLES + CoreStringUtil.Constants.DOT+xsdElementName;
    }
}
