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

package com.metamatrix.modeler.internal.webservice.procedure;

import com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping;


/** 
 * @since 4.3
 */
public class ProcedureCriteriaMappingImpl implements  ProcedureCriteriaMapping {

    private String variableName;
    private String xPathExpression;
    private String criteriaElementName;
    private String criteriaElementUuid;
    private String criteriaElementFunction;
    private String variableFunction;

    /** 
     * 
     * @since 4.3
     */
    public ProcedureCriteriaMappingImpl() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#getCriteriaElementName()
     * @since 4.3
     */
    public String getCriteriaElementName() {
        return this.criteriaElementName;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#getCriteriaElementUuid()
     * @since 4.3
     */
    public String getCriteriaElementUuid() {
        return this.criteriaElementUuid;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#getVariableName()
     * @since 4.3
     */
    public String getVariableName() {
        return this.variableName;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#getXPathExpression()
     * @since 4.3
     */
    public String getXPathExpression() {
        return this.xPathExpression;
    }

    /** 
     * @param pathExpression The xPathExpression to set.
     * @since 4.3
     */
    public void setXPathExpression(String pathExpression) {
        this.xPathExpression = pathExpression;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#setCriteriaElementName(java.lang.String)
     * @since 4.3
     */
    public void setCriteriaElementName(String elementName) {
        this.criteriaElementName = elementName;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#setCriteriaElementUuid(java.lang.String)
     * @since 4.3
     */
    public void setCriteriaElementUuid(String elementuuid) {
        this.criteriaElementUuid = elementuuid;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#setVariableName(java.lang.String)
     * @since 4.3
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#getFunctionOnCriteriaElement()
     * @since 4.3
     */
    public String getFunctionOnCriteriaElement() {
        return this.criteriaElementFunction;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#getFunctionOnVariable()
     * @since 4.3
     */
    public String getFunctionOnVariable() {
        return this.variableFunction;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#setFunctionOnCriteriaElement(java.lang.String)
     * @since 4.3
     */
    public void setFunctionOnCriteriaElement(String criteriaElementFunction) {
        this.criteriaElementFunction = criteriaElementFunction;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping#setFunctionOnVariable(java.lang.String)
     * @since 4.3
     */
    public void setFunctionOnVariable(String variableFunction) {
        this.variableFunction = variableFunction;
    }    
}
