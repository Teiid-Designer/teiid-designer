/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import com.metamatrix.modeler.internal.core.container.ResourceDescriptorImpl;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceFactory;

/**
 * Fake implementation of the MetamodelDescriptor interface
 */
public class FakeResourceDescriptor extends ResourceDescriptorImpl {
    public static final String UNIQUE_ID;
    public static final Class FACTORY_CLASS;
    public static final List EXTENSIONS;

    static {
        UNIQUE_ID = "xmiResourceFactory"; //$NON-NLS-1$
        FACTORY_CLASS = MtkXmiResourceFactory.class;
        EXTENSIONS = new ArrayList(5);
        EXTENSIONS.add("ecore"); //$NON-NLS-1$
        EXTENSIONS.add("xmi"); //$NON-NLS-1$
        EXTENSIONS.add("xml"); //$NON-NLS-1$
    }

    public FakeResourceDescriptor() {
        super(UNIQUE_ID);
        initialize();
    }

    private void initialize() {
        // super.setResourceFactoryClass(FACTORY_CLASS.getName(),this.getClass().getClassLoader());
        super.getExtensions().add(EXTENSIONS.get(0));
        super.getExtensions().add(EXTENSIONS.get(1));
        super.getExtensions().add(EXTENSIONS.get(2));
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.container.ResourceDescriptor#getExtensions()
     */
    @Override
    public List getExtensions() {
        return super.getExtensions();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.container.ResourceDescriptor#getProtocols()
     */
    @Override
    public List getProtocols() {
        return super.getProtocols();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.container.ResourceDescriptor#getResourceFactory()
     */
    @Override
    public Factory getResourceFactory() throws ModelerCoreException {
        return super.getResourceFactory();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.container.ResourceDescriptor#getUniqueIdentifier()
     */
    @Override
    public String getUniqueIdentifier() {
        return super.getUniqueIdentifier();
    }

}
