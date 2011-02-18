/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.compare;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher;


/** 
 * @since 4.2
 */
public class WebServiceComponentNameToNameIgnoreCaseMatcher extends AbstractEObjectNameMatcher {

    /** 
     * 
     * @since 4.2
     */
    public WebServiceComponentNameToNameIgnoreCaseMatcher() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher#getInputKey(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    protected String getInputKey(EObject entity) {
        if(entity instanceof WebServiceComponent) {
	        final String name = ((WebServiceComponent)entity).getName();
	        if(name != null) {
	            return name.toUpperCase();
	        }
        }
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher#getOutputKey(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    protected String getOutputKey(EObject entity) {
        if(entity instanceof WebServiceComponent) {
	        final String name = ((WebServiceComponent)entity).getName();
	        if(name != null) {
	            return name.toUpperCase();
	        }
        }
        return null;
    }

}
