/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.extension.rest;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.extension.ModelObjectExtensionAssistant;

/**
 * 
 */
public class RestModelExtensionAssistant extends ModelObjectExtensionAssistant {

    /**
     * @param modelObject the model object whose REST extension properties are being removed from (cannot be <code>null</code>)
     * @throws Exception if there is a problem accessing the model object's model resource
     */
    public void removeRestProperties( EObject modelObject ) throws Exception {
        removeProperty(modelObject, RestModelExtensionConstants.PropertyIds.REST_METHOD);
        removeProperty(modelObject, RestModelExtensionConstants.PropertyIds.URI);
    }

}
