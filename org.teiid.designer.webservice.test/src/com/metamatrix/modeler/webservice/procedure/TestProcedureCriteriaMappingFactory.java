/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.procedure;

import junit.framework.TestCase;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/** 
 * @since 4.3
 */
public class TestProcedureCriteriaMappingFactory extends TestCase {

    /** 
     * 
     * @since 4.3
     */
    public TestProcedureCriteriaMappingFactory() {
        super();
    }

    /** 
     * @param name
     * @since 4.3
     */
    public TestProcedureCriteriaMappingFactory(String name) {
        super(name);
    }

    public void testGenerateXPathExpression() {
        ProcedureCriteriaMappingFactory factory = new ProcedureCriteriaMappingFactory();
        String operationInputName = "Model.Interface.Operation.Input"; //$NON-NLS-1$
        String xsdElementPath = "/root/element/sequence/element/sequence/element"; //$NON-NLS-1$
        String xPathExpression = factory.generateXPathExpression(operationInputName, xsdElementPath);
        assertEquals("xpathValue(Model.Interface.Operation.Input, '//*[local-name()=\"/root/element/sequence/element/sequence/element\"]')", xPathExpression); //$NON-NLS-1$
    }
    
    public void testGenerateVariableName() {
        ProcedureCriteriaMappingFactory factory = new ProcedureCriteriaMappingFactory();
        IPath xsdElementPath = new Path("/root/element/sequence/element/sequence/element"); //$NON-NLS-1$
        assertEquals("VARIABLES.element", factory.generateVariableName(xsdElementPath)); //$NON-NLS-1$
    }
    
}
