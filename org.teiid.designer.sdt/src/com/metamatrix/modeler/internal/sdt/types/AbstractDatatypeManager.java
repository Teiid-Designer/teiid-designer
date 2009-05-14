/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.sdt.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.xsd.aspects.sql.XsdSimpleTypeDefinitionAspect;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.types.DatatypeManagerLifecycle;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.sdt.ModelerSdtPlugin;

/**
 * DatatypeManagerImpl
 */
public abstract class AbstractDatatypeManager implements DatatypeManager, DatatypeManagerLifecycle {

    private static SqlDatatypeAspect sqlDatatypeAspect;

    private static final Comparator TYPE_NAME_COMPARATOR = new DatatypeNameComparator();

    private ResourceSet container;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of DatatypeManagerImpl.
     */
    public AbstractDatatypeManager() {
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManagerLifecycle#initialize(com.metamatrix.modeler.core.container.Container)
     * @since 4.2
     */
    public void initialize( final ResourceSet container ) throws ModelerCoreException {
        this.container = container;
        doInitialize();
    }

    protected abstract void doInitialize() throws ModelerCoreException;

    public ResourceSet getContainer() {
        return this.container;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Return true if the identifier string includes a ObjectID string
     */
    protected static boolean containsUuidPattern( final String id ) {
        return (id != null && id.indexOf(UUID.PROTOCOL) >= 0);
    }

    /**
     * Extract the UUID String from the identifier
     * 
     * @param id
     * @return
     * @throws ModelerCoreException
     */
    protected static String extractUuidString( final String id ) {
        if (containsUuidPattern(id)) {
            int beginIndex = id.indexOf(UUID.PROTOCOL);
            return id.substring(beginIndex);
        }
        return null;
    }

    protected static ObjectID getObjectIDFromString( final String idString ) throws ModelerCoreException {
        try {
            return IDGenerator.getInstance().stringToObject(idString);
        } catch (Throwable e) {
            throw new ModelerCoreException(
                                           e,
                                           ModelerSdtPlugin.Util.getString("DatatypeManagerImpl.Error_finding_the_EObject_with_UUID_11", idString)); //$NON-NLS-1$
        }
    }

    protected static void sortByName( final List eObjs ) {
        Collections.sort(eObjs, TYPE_NAME_COMPARATOR);
    }

    protected static SqlDatatypeAspect getSqlAspect( final EObject eObject ) {
        if (eObject != null && eObject instanceof XSDSimpleTypeDefinition) {
            if (ModelerCore.getPlugin() == null) {
                if (AbstractDatatypeManager.sqlDatatypeAspect == null) {
                    AbstractDatatypeManager.sqlDatatypeAspect = new XsdSimpleTypeDefinitionAspect(null);
                }
                return AbstractDatatypeManager.sqlDatatypeAspect;
            }
            // Defect 23839 - rather than calling the metamodel registry to get aspect, use the AspectManager which is caching
            // these aspects!!!!
            SqlAspect sqlAspect = AspectManager.getSqlAspect(eObject);
            if (sqlAspect instanceof SqlDatatypeAspect) {
                return (SqlDatatypeAspect)sqlAspect;
            }
        }
        return null;
    }

    protected static void removeDuplicates( final List eObjs ) {
        List tmp = new ArrayList(eObjs.size());
        for (Iterator iter = eObjs.iterator(); iter.hasNext();) {
            EObject eObj = (EObject)iter.next();
            if (!tmp.contains(eObj)) {
                tmp.add(eObj);
            } else {
                iter.remove();
            }
        }
    }

    /**
     * Return an array of EMF Resource instances representing all user-defined datatype models known within the model workspace
     * 
     * @return
     * @throws ModelerCoreException
     */
    protected List getDatatypeResources() {
        /*
         * Defect 16802 - Goal: Improve this method so that no resources from closed projects
         *                      will be included in the returned list.
         *  Strategy:
         *  - First part (if in Eclipse and container is a ModelContainer)
         *      1. retrieve the set of all resources in open projects
         *      2. loop through the set doing the tests the old Resourcevisitor did
         *         to determine if the resource is either an XSD file
         *         OR if it contains user-defined datatypes.
         *  - Second part (Not in Eclipse or not a model container)
         *      Leave this to work as before.                              
         */
        final ResourceSet container = this.getContainer();
        if (container == null) {
            return Collections.EMPTY_LIST;
        }
        // Container is never null from this point on ...

        if (ModelerSdtPlugin.getDefault() != null && (container instanceof Container) && // this is in the Eclipse environment ...
            ModelerCore.isModelContainer((Container)container)) { // the container is the ModelContainer

            List lstResultResources = new ArrayList();

            try {
                // get the set of resources that are in open projects
                ModelResource[] mrResources = ModelerCore.getModelWorkspace().getModelResources();

                // walk the list and capture the resources that are related to datatypes
                for (int i = 0; i < mrResources.length; i++) {
                    ModelResource mrResource = mrResources[i];
                    IResource irResource = mrResource.getResource();

                    // If the IResource represents a model file or XSD ...
                    if (ModelUtil.isModelFile(irResource) && irResource.exists()) {

                        // If the resource is an XSD, automatically add it to the list of EMF resources ...
                        if (ModelUtil.isXsdFile(irResource)) {

                            final Resource xsdResource = mrResource.getEmfResource();
                            if (xsdResource != null) {
                                lstResultResources.add(xsdResource);
                            }
                        }

                        // } else {
                        // final IPath resourcePath = irResource.getLocation();
                        // final File resourceFile = resourcePath.toFile();
                        // final XMIHeader header = ModelUtil.getXmiHeader(resourceFile);
                        //
                        // // Check if the primary metamodel URI is the user-defined datatype metamodel
                        // if (header != null && DatatypeConstants.DATATYPE_METAMODEL_URI.equals(header.getPrimaryMetamodelURI()))
                        // {
                        // final Resource xsdResource = mrResource.getEmfResource();
                        // if (xsdResource != null) {
                        // lstResultResources.add(xsdResource);
                        // }
                        // }
                        // }
                    }
                }
            } catch (CoreException ce) {
                ModelerCore.Util.log(ce);
            }
            return lstResultResources;
        }

        // Otherwise it is not the model container, so get the list of resources from the resource set ...
        final List resources = container.getResources();
        final List datatypeResources = new ArrayList();
        final Iterator iter = resources.iterator();
        while (iter.hasNext()) {
            final Resource resource = (Resource)iter.next();
            // If the resource is an XSD, automatically add it to the list of EMF resources ...
            if (ModelUtil.isXsdFile(resource)) {
                datatypeResources.add(resource);
            }
            // } else {
            // // Looking for DATATYPE models ...
            // String metamodelUri = null;
            // if ( resource instanceof EmfResource ) {
            // final ModelAnnotation annotation = ((EmfResource)resource).getModelAnnotation();
            // if ( annotation != null ) {
            // metamodelUri = annotation.getPrimaryMetamodelUri();
            // }
            // } else {
            // final URI resourceUri = resource.getURI();
            // if ( resourceUri.isFile() ) {
            // final String location = resourceUri.toFileString();
            // final File resourceFile = new File(location);
            // if ( resourceFile.exists() ) {
            // try {
            // final XMIHeader header = XMIHeaderReader.readHeader(resourceFile);
            // metamodelUri = header.getPrimaryMetamodelURI();
            // } catch (CoreException e) {
            // ModelerCore.Util.log(e);
            // }
            // }
            // }
            // }
            //                    
            // // Check if the primary metamodel URI is the user-defined datatype metamodel
            // if (DatatypeConstants.DATATYPE_METAMODEL_URI.equals(metamodelUri)) {
            // datatypeResources.add(resource);
            // }
            // }

        }
        return datatypeResources;

    }

    /**
     * Return an array of EObject instances representing the type hierarchy for this simple datatype. The array is ordered such
     * that the first entry is the datatype itself and the last entry is the ur-type of "anySimpleType". If the specified EObject
     * is a XSDSimpleTypeDefinition instance of variety union or list then only the datatype itself is returned in the array.
     * 
     * @param type
     * @return EObject[]
     */
    public EObject[] getTypeHierarchy( final EObject type ) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, type);

        // If the simple type is not atomic then cannot navigate the hierarchy
        if (((XSDSimpleTypeDefinition)type).getVariety() != XSDVariety.ATOMIC_LITERAL) {
            return new EObject[] {type};
        }

        // Construct a list to hold the type hierarchy
        final List tmp = new ArrayList();
        tmp.add(type);

        // Navigate up the basetype hierarchy until we reach "anySimpleType"
        try {
            EObject simpleType = type;
            while (simpleType != this.getAnySimpleType()) {
                final EObject baseType = this.getBaseType(simpleType);
                if (baseType == null) {
                    break;
                } else if (baseType == this.getAnyType()) {
                    break;
                } else if (simpleType == baseType) {
                    break; // this would end up being recursion ...
                }
                tmp.add(baseType);
                simpleType = baseType;
            }
        } catch (ModelerCoreException e) {
            ModelerSdtPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        return (EObject[])tmp.toArray(new EObject[tmp.size()]);
    }

    // ==================================================================================
    // I N N E R C L A S S
    // ==================================================================================

    protected static class DatatypeNameComparator implements Comparator {
        public int compare( Object obj1,
                            Object obj2 ) {
            if (obj1 == null && obj2 == null) {
                return 0;
            } else if (obj1 == null && obj2 != null) {
                return -1;
            } else if (obj1 != null && obj2 == null) {
                return 1;
            }
            XSDTypeDefinition dt1 = (XSDTypeDefinition)obj1;
            XSDTypeDefinition dt2 = (XSDTypeDefinition)obj2;
            String value1 = dt1.getName();
            String value2 = dt2.getName();
            return value1.compareToIgnoreCase(value2);
            // return value1.compareTo(value2);
        }
    }

}
