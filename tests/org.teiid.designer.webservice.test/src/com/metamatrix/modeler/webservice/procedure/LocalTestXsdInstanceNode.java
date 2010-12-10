/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.procedure;

import java.io.File;
import java.util.Iterator;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import com.metamatrix.core.util.SmartTestDesignerSuite;

/**
 * @since 5.0.2
 */
public class LocalTestXsdInstanceNode extends TestCase {

    private static final String FOLDER = SmartTestDesignerSuite.getTestDataPath() + "/Cigna/"; //$NON-NLS-1$

    private static final String MEMBER_XSD = FOLDER + "member.xsd"; //$NON-NLS-1$

    /**
     * @since 5.0.2
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(LocalTestXsdInstanceNode.class);
        return new TestSetup(suite);
    }

    public void testCigna() {
        ResourceSet resrcSet = new ResourceSetImpl();
        resrcSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl()); //$NON-NLS-1$
        resrcSet.setURIConverter(new UriConverter(FOLDER));
        XSDSchema schema = load(resrcSet, MEMBER_XSD);
        for (Iterator iter = schema.getElementDeclarations().iterator(); iter.hasNext();) {
            getChildren(new XsdInstanceNode((XSDElementDeclaration)iter.next()), 0);
        }
    }

    private void getChildren( XsdInstanceNode node,
                              int level ) {
        for (int ndx = level; --ndx >= 0;) {
            System.out.print("  "); //$NON-NLS-1$
        }
        System.out.println(node.getName());
        XsdInstanceNode[] children = node.getChildren();
        for (int ndx = children.length; --ndx >= 0;) {
            getChildren(children[ndx], level + 1);
        }
    }

    private XSDSchema load( ResourceSet resourceSet,
                            String resource ) {
        Resource resrc = resourceSet.getResource(URI.createURI(resource), true);
        assertNotNull(resrc);
        assertFalse(resrc.getContents().isEmpty());
        assertTrue(resrc.getContents().get(0) instanceof XSDSchema);
        return (XSDSchema)resrc.getContents().get(0);
    }

    private class UriConverter extends ExtensibleURIConverterImpl {

        File root;

        UriConverter( String root ) {
            this.root = new File(root);
        }

        /**
         * @see org.eclipse.emf.ecore.resource.impl.URIConverterImpl#normalize(org.eclipse.emf.common.util.URI)
         * @since 5.0.2
         */
        @Override
        public URI normalize( URI uri ) {
            URI normalizedUri = super.normalize(uri);
            String path = normalizedUri.toString();
            if (!path.toLowerCase().startsWith("http")) { //$NON-NLS-1$
                File file = new File(path);
                path = (file.exists() ? file.getAbsolutePath() : new File(this.root, uri.lastSegment()).getAbsolutePath());
                normalizedUri = URI.createFileURI(path.replace('\\', '/'));
            }
            System.out.println("\nisFile=" + uri.isFile()); //$NON-NLS-1$ 
            System.out.println("           URI=" + uri + "\nNormalized URI=" + normalizedUri); //$NON-NLS-1$ //$NON-NLS-2$
            return normalizedUri;
        }
    }
}
