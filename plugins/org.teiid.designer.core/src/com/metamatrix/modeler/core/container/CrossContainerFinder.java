/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.container;

import java.io.IOException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.EmfResourceSet;

public class CrossContainerFinder
{
    //
    // Instance variables:
    //
    private final Container sourceContainer;
    private final Container targetContainer;

    //
    // Constructors:
    //
    public CrossContainerFinder(Container container1, Container container2) {
        this.sourceContainer = container1;
        this.targetContainer = container2;
    }

    //
    // Data methods:
    //
    
    /** Find the corresponding EObject in the other container
      * 
      */
    public EObject find(EObject object) {
        if (object != null) {
            Resource sourceRes = object.eResource();
            // find the resource in the other container:
            Resource targetRes = find(sourceRes);
            if (targetRes != null) {
                if (!targetRes.isLoaded()) {
                    // need to load resource:
                    try {
                        targetRes.load(targetRes.getResourceSet().getLoadOptions());
                    } catch (IOException e) {
                        ModelerCore.Util.log(e);
                    } // endtry
                } // endif
                return targetRes.getEObject(sourceRes.getURIFragment(object));
            } // endif
        } //endif
        
        return null;
    }

    /** Find the corresponding Resource in the other container
     * 
     */
    public Resource find(Resource resource) {
        // find the source container:
        ResourceSet sourceResourceSet = resource.getResourceSet();
        Container srcCont;
        
        if (sourceResourceSet instanceof Container) {
            srcCont = (Container) sourceResourceSet;

        } else if (sourceResourceSet instanceof EmfResourceSet) {
            srcCont = ((EmfResourceSet)sourceResourceSet).getContainer();

        } else {
            // unknown resourceSet; cannot process:
            return null;
        } //endif

        // determine target container:
        Container tgtCont;
        if (srcCont == sourceContainer) {
            tgtCont = targetContainer;
        } else {
            tgtCont = sourceContainer;
        } // endif

        Resource rv;
        if (resource instanceof EmfResource) {
            EmfResource eRes = (EmfResource) resource;
            ObjectID uuid = eRes.getUuid();
            rv = tgtCont.getResourceFinder().findByUUID(uuid, false);

        } else {
            String xsdName = resource.getURI().lastSegment();
            Resource[] wsRes = tgtCont.getResourceFinder().findByName(xsdName, true, false);
//            if (wsRes.length != 1) {
//                throw new IllegalStateException("too many resources!");
//            } // endif

            rv = wsRes[0];
        } //endif

        // otherwise, unsupported resource type:
        return rv;
    }

    //
    // Static Utility methods:
    //
    public static CrossContainerFinder createWorkspaceFinder(Container source) {
        try {
            return new CrossContainerFinder(source, ModelerCore.getModelContainer());
        } catch (CoreException e) {
            // shouldn't happen:
            ModelerCore.Util.log(e);
            return null;
        } // endtry
    }
}
