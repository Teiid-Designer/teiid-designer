/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.xml.XmlDocumentNode;
import org.teiid.designer.metamodels.xml.XmlDocumentPlugin;
import org.teiid.designer.metamodels.xsd.XsdUtil;


/**
 * XmlDocumentNodeDatatypeRule, rule that validates the datatypes associated with XmlDocumentNodes
 */
public class XmlDocumentNodeDatatypeRule implements ObjectValidationRule {

    /**
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(final EObject eObject, final ValidationContext context) {

        // Check that the EObject is an instanceof XmlDocumentNode otherwise we cannot apply this rule
        if (eObject == null || !(eObject instanceof XmlDocumentNode)) {
            return;
        }
        
        // If there is no XSDComponent reference we cannot apply this rule
        final XSDComponent xsdComponent = ((XmlDocumentNode)eObject).getXsdComponent();
        if (xsdComponent == null) {
            return;
        }
        
        // Find the XSDComponent's type and attempt to resolve it if its a eProxy
        XSDTypeDefinition typeDefn = XsdUtil.getType(xsdComponent);
        if (typeDefn != null && typeDefn.eIsProxy() && eObject.eResource() != null ) {
            typeDefn = (XSDTypeDefinition)EcoreUtil.resolve(typeDefn, eObject.eResource().getResourceSet());
            if (typeDefn == null) {
                // create validation problem and add it to the result
                Object[] params = new Object[]{((XmlDocumentNode)eObject).getName(),((InternalEObject)XsdUtil.getType(xsdComponent)).eProxyURI()};
                String msg = XmlDocumentPlugin.Util.getString("XmlDocumentNodeDatatypeRule.xml_doc_node_references_type_that_cannot_be_resolved",params); //$NON-NLS-1$
                ValidationResult result = new ValidationResultImpl(eObject);
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, msg);
                result.addProblem(problem);
                context.addResult(result);
                return;
            }
        }
        
        // If the type is simple type then validate its marked as an enterprise datatype
        if (typeDefn instanceof XSDSimpleTypeDefinition) {
            final XSDSimpleTypeDefinition simpleTypeDefn = (XSDSimpleTypeDefinition)typeDefn;
            final DatatypeManager dtMgr = context.getDatatypeManager();
            
            // The referenced datatype must be an "Enterprise" datatype
            if(!dtMgr.isEnterpriseDatatype(simpleTypeDefn) ){
                final Object[] params = new Object[]{ ((XmlDocumentNode)eObject).getName() };
                final String msg = XmlDocumentPlugin.Util.getString("XmlDocumentNodeDatatypeRule.xml_doc_node_must_reference_enterprise_datatype",params); //$NON-NLS-1$
                ValidationResult result = new ValidationResultImpl(eObject);
                final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, msg);
                result.addProblem(problem);
                context.addResult(result);
            }
            
        }
    }

}
