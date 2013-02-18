/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.extension;

import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import org.teiid.designer.metamodels.webservice.WebServicePackage;

/**
 * Provides extendable metaclass names for the Web Service metamodel.
 * 
 * @since 8.0
 */
public class WebServiceExtendableClassnameProvider extends AbstractMetaclassNameProvider {

    /**
     * Constructs a provider.
     */
    public WebServiceExtendableClassnameProvider() {
        super(WebServicePackage.eNS_URI);

        final String operation = "org.teiid.designer.metamodels.webservice.impl.OperationImpl"; //$NON-NLS-1$
        final String input = "org.teiid.designer.metamodels.webservice.impl.InputImpl"; //$NON-NLS-1$
        final String output = "org.teiid.designer.metamodels.webservice.impl.OutputImpl"; //$NON-NLS-1$
        final String wsInterface = "org.teiid.designer.metamodels.webservice.impl.InterfaceImpl"; //$NON-NLS-1$
        final String sampleMessages = "org.teiid.designer.metamodels.webservice.impl.SampleMessagesImpl"; //$NON-NLS-1$
        final String sampleFile = "org.teiid.designer.metamodels.webservice.impl.SampleFileImpl"; //$NON-NLS-1$
        final String sampleXsd = "org.teiid.designer.metamodels.webservice.impl.SampleFromXsdImpl"; //$NON-NLS-1$

        addMetaclass(wsInterface, NO_PARENTS);
        addMetaclass(operation, wsInterface);

        addMetaclass(input, operation);
        addMetaclass(output, operation);

        addMetaclass(sampleMessages, input, output);
        addMetaclass(sampleFile, sampleMessages);
        addMetaclass(sampleXsd, sampleMessages);
    }

}
