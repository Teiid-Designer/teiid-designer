/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jdom.JDOMException;
import com.metamatrix.core.util.SmartTestDesignerSuite;

/**
 */
public class TestDotProjectUtil extends TestCase {

    private IResource buildMockResourceFromTestData( final File file ) {
        if (file.isDirectory()) {
            final MockContainer container = new MockContainer();
            final File[] files = file.listFiles();
            if (files != null && files.length > 0) for (final File file2 : files) {
                final IResource resource = buildMockResourceFromTestData(file2);
                container.addResource(resource);
            }
            return container;
        }
        final MockFileResource resource = new MockFileResource(file);
        return resource;
    }

    public void testGetDotProjectCountFromFiles() throws IOException, JDOMException {
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nodotproject"), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject"), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/simpleproject"), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject"), true, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/simpleproject"), true, true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedsimpleproject"), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedsimpleproject"), true, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedmodelerproject"), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedmodelerproject"), true, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedcompound"), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedcompound"), true, true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedsimpleproject"), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedsimpleproject"), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedmodelerproject"), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedmodelerproject"), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedcompound"), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedcompound"), false, true)); //$NON-NLS-1$ //$NON-NLS-2$

        // Test that multiple nested compounds return 2 even if more than 2 dot projects exist.
        // This tests fail fast.
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/multiplenestedcompound"), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/multiplenestedcompound"), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/multiplenestedcompound"), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/multiplenestedcompound"), true, true)); //$NON-NLS-1$ //$NON-NLS-2$    
    }

    public void testGetDotProjectCountFromResource() throws CoreException, IOException, JDOMException {
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nodotproject")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/simpleproject")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/simpleproject")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedsimpleproject")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedsimpleproject")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedmodelerproject")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedmodelerproject")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedcompound")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedcompound")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedsimpleproject")), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedsimpleproject")), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedmodelerproject")), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedmodelerproject")), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedcompound")), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/nestedcompound")), false, true)); //$NON-NLS-1$ //$NON-NLS-2$

        // Test that multiple nested compounds return 2 even if more than 2 dot projects exist.
        // This tests fail fast.
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/multiplenestedcompound")), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/multiplenestedcompound")), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/multiplenestedcompound")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 2, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/multiplenestedcompound")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$    

        // Test with files
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/simpleproject/.project")), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/simpleproject/.project")), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/simpleproject/.project")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/simpleproject/.project")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/.project")), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/.project")), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/.project")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/.project")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/Books.xsd")), false, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/Books.xsd")), false, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/Books.xsd")), true, false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The wrong .project count was found.", 0, DotProjectUtils.getDotProjectCount(buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/Books.xsd")), true, true)); //$NON-NLS-1$ //$NON-NLS-2$

        // Test Nature
        final MockFileResource fileResource = (MockFileResource)buildMockResourceFromTestData(SmartTestDesignerSuite.getTestDataFile("/dotProjectFiles/modelerdotproject/.project")); //$NON-NLS-1$
        fileResource.setAccessible(true);
        fileResource.setModelNature(false);
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(fileResource, false, false)); //$NON-NLS-1$ 
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(fileResource, false, true)); //$NON-NLS-1$ 
        fileResource.setModelNature(true);
        assertEquals("The wrong .project count was found.", 1, DotProjectUtils.getDotProjectCount(fileResource, false, false)); //$NON-NLS-1$ 

    }

}
