/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.modelextension;

import com.metamatrix.modeler.modelgenerator.xml.modelextension.BaseExtensionReplaceAction;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.ExtensionManager;

public class SOAPExtensionReplaceAction extends BaseExtensionReplaceAction {

    @Override
    public ExtensionManager getExtensionManager() {
        return new XMLWSDLExtensionManager();
    }

    @Override
    public String getExtensionName() {
        return "XMLHttpConnectorExtensions"; //$NON-NLS-1$
    }
}
