/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.container;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.teiid.core.designer.ModelerCoreRuntimeException;
import org.teiid.core.designer.id.IDGenerator;
import org.teiid.core.designer.id.InvalidIDException;
import org.teiid.core.designer.id.ObjectID;
import org.teiid.core.designer.id.UUID;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.resource.EmfResourceSetImpl;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.util.AbstractFinder;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;


/**
 * @since 8.0
 */
public class DefaultEObjectFinder extends AbstractFinder {

    private final Container container;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * @param container
     * @since 4.3
     */
    public DefaultEObjectFinder( final Container container ) {
        super();
        this.container = container;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see org.teiid.designer.core.container.EObjectFinder#find(java.lang.Object)
     * @since 4.3
     */
    @Override
	public Object find( Object key ) {
        if (key instanceof URI) {
            return getResourceSet().getEObject((URI)key, true);
        } else if (key instanceof String) {
            String id = (String)key;

            if (CoreStringUtil.startsWithIgnoreCase(id, UUID.PROTOCOL)) {
                try {
                    return findByObjectID(IDGenerator.getInstance().stringToObject(id.toLowerCase()), true);
                } catch (InvalidIDException e) {
                    throw new ModelerCoreRuntimeException(e.getMessage());
                }
            } else if (id.indexOf(DatatypeConstants.URI_REFERENCE_DELIMITER + UUID.PROTOCOL) != -1) {
                // searching by Object URI
                int index = id.indexOf(DatatypeConstants.URI_REFERENCE_DELIMITER);

                // fragment separator must not be found in first position
                // strip off the namespace and the delimeter to just leave the UUID (including the protocol)
                if (index != 0) {
                    Object obj = find(id.substring(index + 1));

                    if (obj != null) {
                        // make sure this object's model has a Namespace URI that matches the one
                        // found in the Object URI
                        String namespaceUri = id.substring(0, index);

                        try {
                            // get the Namespace URI of the model
                            ModelResource model = ModelUtil.getModel(obj);

                            if (model != null) {
                                String uri = model.getModelAnnotation().getNamespaceUri();

                                if ((uri != null) && uri.equals(namespaceUri)) {
                                    return obj;
                                }
                            }
                        } catch (ModelWorkspaceException theException) {
                            ModelerCore.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
                        }
                    }
                }
            }
        } else if (key instanceof ObjectID) {
            return findByObjectID((ObjectID)key, true);
        }
        throw new ModelerCoreRuntimeException(
                                              ModelerCore.Util.getString("ContainerImpl.Invalid_key_object_in_Finder.find_method____6") + key); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.container.EObjectFinder#findKey(java.lang.Object)
     * @since 4.3
     */
    @Override
    public Object findKey( Object object ) {
        if (object instanceof EObject) {
            return ModelerCore.getObjectId((EObject)object);
        }

        return null;
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    protected ResourceSet getResourceSet() {
        return this.container.getResourceSet();
    }

    protected ObjectManager getObjectManager() {
        return this.container.getObjectManager();
    }

    protected Object findByObjectID( final ObjectID objectID,
                                     final boolean searchExternalResourceSets ) {
        // Search the container associated with this finder
        Object result = getObjectManager().findEObject(objectID.toString());

        // If the UUID was not found in this container then search the external containers
        if (result == null && searchExternalResourceSets) {
            if (getResourceSet() instanceof EmfResourceSetImpl) {
                final ResourceSet[] externalResourceSets = ((EmfResourceSetImpl)getResourceSet()).getExternalResourceSets();
                for (int i = 0; i != externalResourceSets.length; ++i) {
                    ResourceSet resourceSet = externalResourceSets[i];
                    if (resourceSet instanceof Container) {
                        result = ((Container)resourceSet).getEObjectFinder().find(objectID);
                    }
                    if (result != null) {
                        break;
                    }
                }
            }
        }
        return result;
    }

}
