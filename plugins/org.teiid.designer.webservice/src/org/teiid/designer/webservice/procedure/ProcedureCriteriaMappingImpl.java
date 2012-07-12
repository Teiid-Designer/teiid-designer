/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice.procedure;



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
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#getCriteriaElementName()
     * @since 4.3
     */
    public String getCriteriaElementName() {
        return this.criteriaElementName;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#getCriteriaElementUuid()
     * @since 4.3
     */
    public String getCriteriaElementUuid() {
        return this.criteriaElementUuid;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#getVariableName()
     * @since 4.3
     */
    public String getVariableName() {
        return this.variableName;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#getXPathExpression()
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
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#setCriteriaElementName(java.lang.String)
     * @since 4.3
     */
    public void setCriteriaElementName(String elementName) {
        this.criteriaElementName = elementName;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#setCriteriaElementUuid(java.lang.String)
     * @since 4.3
     */
    public void setCriteriaElementUuid(String elementuuid) {
        this.criteriaElementUuid = elementuuid;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#setVariableName(java.lang.String)
     * @since 4.3
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#getFunctionOnCriteriaElement()
     * @since 4.3
     */
    public String getFunctionOnCriteriaElement() {
        return this.criteriaElementFunction;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#getFunctionOnVariable()
     * @since 4.3
     */
    public String getFunctionOnVariable() {
        return this.variableFunction;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#setFunctionOnCriteriaElement(java.lang.String)
     * @since 4.3
     */
    public void setFunctionOnCriteriaElement(String criteriaElementFunction) {
        this.criteriaElementFunction = criteriaElementFunction;
    }

    /** 
     * @see org.teiid.designer.webservice.procedure.ProcedureCriteriaMapping#setFunctionOnVariable(java.lang.String)
     * @since 4.3
     */
    public void setFunctionOnVariable(String variableFunction) {
        this.variableFunction = variableFunction;
    }    
}
