/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.io.File;
import junit.framework.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.SmartTestDesignerSuite;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;

/**
 */
public class EmfContainerUtils {

    public static final String CONTAINER_NAME_PROPERTY = "container"; //$NON-NLS-1$

    private static ContainerImpl primaryContainer;

    /**
     * Constructor for TestEMFContainerUtils.
     */
    public EmfContainerUtils() {
        super();
    }

    public static void createContainer( final String containerName,
                                        final boolean isPrimary ) {
        try {
            // // Init the testing config - the PdeModelerCoreTestUtil.buildTestConfiguration should
            // // only be used when setting a configuration for a non-PDE test since it
            // // sets all MetamodelDescriptor ClassLoader references to the ClassLoader used to
            // // create the MetamodelDescriptor.
            // PdeModelerCoreTestUtil.buildTestConfiguration(ModelerCore.getConfiguration());

            // Create the container ...
            final ContainerImpl container = (ContainerImpl)ModelerCore.createContainer(containerName);

            // If primary ...
            if (isPrimary) {
                primaryContainer = container;
            }
        } catch (CoreException e) {
        }
    }

    public static ContainerImpl getContainer() {
        if (primaryContainer == null) {
            try {
                primaryContainer = (ContainerImpl)ModelerCore.getModelContainer();
            } catch (CoreException e) {
                createContainer("Test Container", true); //$NON-NLS-1$
            }

        }

        return primaryContainer;
    }

    public static ContainerImpl getContainer( final String containerName ) {
        if (containerName == null) {
            return primaryContainer;
        }
        // Look up the container in the Registry ...
        Object obj = ModelerCore.getRegistry().lookup(containerName);
        if (obj == null) {
            throw new IllegalArgumentException("Unable to find container registered with the name \"" + containerName + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (obj instanceof ContainerImpl) {
            return (ContainerImpl)obj;
        }
        throw new IllegalArgumentException(
                                           "Object registered with the name \"" + containerName + "\" is not an instance ofEmfContainerr but is instead instanceof " + obj.getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static void addModel( final String filenameInTestData ) {
        addModel(filenameInTestData, null);
    }

    public static void addModel( final String filenameInTestData,
                                 final String containerName ) {
        final ContainerImpl theContainer = getContainer(containerName);
        final File testFile = SmartTestDesignerSuite.getTestDataFile(filenameInTestData);
        final String fullPath = testFile.getAbsolutePath();

        // Add the model to the container ...
        System.out.println("Adding the file \"" + fullPath + "\" to \"" + theContainer + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Resource r = theContainer.getResource(URI.createFileURI(fullPath), true);
        final int numErrors = r.getErrors().size();
        if (numErrors != 0) {
            Assert.fail(numErrors + " errors while loading model " + fullPath + " into container " + containerName); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public static void shutdownContainer( final String containerName ) {
        try {
            getContainer(containerName).shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void shutdownContainer() {
        shutdownContainer(null);
    }

    public static void removeContainer() {
        if (primaryContainer == null) {
            return;
        }

        primaryContainer.shutdown();

        primaryContainer = null;
    }

}
