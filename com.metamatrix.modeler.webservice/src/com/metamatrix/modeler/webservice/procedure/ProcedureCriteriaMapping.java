/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.webservice.procedure;


/** 
 * Query used in the procedure defining a WebService Operation have simple criteria comparing
 * an elemnt on the output document to a variable defined in the procedure whose value is an XPath
 * expression on an elemnt on the inputdocument to the procedure.
 * This object provides a mapping between element used in criteria, the variable it is bounded to and 
 * the XPath expression defined for that variable.
 * @since 4.3
 */
public interface ProcedureCriteriaMapping {
    
    /**
     * Get the name of the variable used in the procedure defining the webservice operation.
     * This variable is gets assigned an XPath expression depending the element selected on 
     * the XSD defing the input document.  
     * @return Variable name
     * @since 4.3
     */
    String getVariableName();
    
    /**
     * The XPath expression for the element on the input xsd schema.
     * @return The XPath expression
     * @since 4.3
     */
    String getXPathExpression();

    /**
     * The name of the element on the output xml document used on the criteria of the query
     * used in procedure.  
     * @return The output document element name
     * @since 4.3
     */
    String getCriteriaElementName();

    /**
     * The uuid of the element on the output xml document used on the criteria of the query
     * used in procedure.  
     * @return The output document element uuid
     * @return
     * @since 4.3
     */
    String getCriteriaElementUuid();

    /**
     * Get the function that is applied on the variable when used in a criteria of a query. 
     * @return The name of the function to be applied on the variable.
     * @since 4.3
     */
    String getFunctionOnVariable();

    /**
     * Get the function that is applied on the element in the criteria that is compared with the variable. 
     * @return The name of the function to be applied on the criteria element.
     * @since 4.3
     */
    String getFunctionOnCriteriaElement();

    /**
     * Set the variable name fro the mapping.
     * @param variableName The variable name
     * @since 4.3
     */
    void setVariableName(final String variableName);

    /**
     * Set the name of the output element of the output xml document. 
     * @param elementName The name of output entity
     * @since 4.3
     */
    void setCriteriaElementName(final String elementName);

    /**
     * Set the name of the output element of the output xml document.
     * @param elementuuid The uuid of output entity
     * @since 4.3
     */
    void setCriteriaElementUuid(final String elementuuid);

    /**
     * Set the function that is applied on the variable when used in a criteria of a query.
     * @param variableFunction The name of the function to be applied on the variable.
     * @since 4.3
     */
    void setFunctionOnVariable(String variableFunction);

    /**
     * Set the function that is applied on the element in the criteria that is compared with the variable. 
     * @param criteriaElementFunction The name of the function to be applied on the criteria element.
     * @since 4.3
     */
    void setFunctionOnCriteriaElement(String criteriaElementFunction);    
}
