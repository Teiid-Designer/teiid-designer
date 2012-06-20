/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.osgi.framework.Bundle;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.metamodel.MetamodelDescriptorImpl;

/**
 * Fake implementation of the MetamodelDescriptor interface
 */
public class FakeMetamodelDescriptor extends MetamodelDescriptorImpl {
    public static final String EXTENSION_ID;
    public static final String NAME;
    public static final String NAMESPACE_URI;
    public static final String INTERNAL_URI;
    public static final String NAMESPACE_PREFIX;
    public static final Class EFACTORY_CLASS;
    public static final String RESOURCE_LOCATION;
    public static final InputStream RESOURCE_INPUTSTREAM;
    public static final URI METAMODEL_URI;
    private static final EPackage EPACKAGE;

    static {
        EXTENSION_ID = "relational"; //$NON-NLS-1$
        EPACKAGE = EcoreFactory.eINSTANCE.createEPackage();
        NAME = "Relational"; //$NON-NLS-1$
        NAMESPACE_URI = "http://www.metamatrix.com/metabase/3.1/metamodels/Relational.xml"; //$NON-NLS-1$
        NAMESPACE_PREFIX = "Relational"; //$NON-NLS-1$
        INTERNAL_URI = null;
        EFACTORY_CLASS = EPACKAGE.getEFactoryInstance().getClass();
        RESOURCE_LOCATION = "E:/Plugins/current/plugins/org.teiid.designer.core/testdata/relational.ecore"; //$NON-NLS-1$
        RESOURCE_INPUTSTREAM = null;
        METAMODEL_URI = URI.createURI(NAMESPACE_URI);
    }
    
    /**
     * Constructor
     * 
     * @param theNamespaceURI
     * @param ePackageClassName
     * @param bundle
     */
    public FakeMetamodelDescriptor( final String theNamespaceURI,
                                    final String ePackageClassName,
                                    final Bundle bundle ) {
        super(theNamespaceURI, ePackageClassName, bundle);
    }

    /**
     * @see org.teiid.designer.core.metamodel.MetamodelDescriptor#getExtensionID()
     */
    @Override
    public String getExtensionID() {
        return EXTENSION_ID;
    }

    /**
     * @see MetamodelDescriptor#getName()
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * @return the namespace {@link URI}
     */
    public String getURI() {
        return NAMESPACE_URI;
    }

    /**
     * Return the internal address of the metamodel in the form of a URI; may not be the same as the result of getURI();
     * 
     * @return URI or null if no URI exists.
     */
    public String getInternalURI() {
        return INTERNAL_URI;
    }

    /**
     * @see MetamodelDescriptor#getNamespacePrefix()
     */
    @Override
    public String getNamespacePrefix() {
        return NAMESPACE_PREFIX;
    }

    /**
     * @return the factory class
     */
    public Class getFactoryClass() {
        return EFACTORY_CLASS;
    }

    /**
     * @return the resource location
     */
    public String getResourceURL() {
        return RESOURCE_LOCATION;
    }

    /**
     * @return an {@link InputStream} to the resource
     */
    public InputStream getInputStream() {
        File file = new File(RESOURCE_LOCATION);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace(System.err);
            }
        }
        return RESOURCE_INPUTSTREAM;
    }

}
