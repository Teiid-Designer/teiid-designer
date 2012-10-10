/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.container;

import java.util.Collection;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.id.UUID;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.resource.XResource;
import org.teiid.designer.core.resource.xmi.MtkXmiResourceImpl;


/**
 * ObjectManager - manager of maps containing EObject instances for models in the workspace. The maps are keyed on the UUID
 * associated with the EObject. Models not found in the "Model Container" cannot be found through the ObjectManager.
 *
 * @since 8.0
 */
public class ObjectManagerImpl implements ObjectManager {

	private final ContainerImpl container;

	public ObjectManagerImpl( final ContainerImpl container ) {
		CoreArgCheck.isNotNull(container);
		this.container = container;
	}

	/**
	 * @see org.teiid.designer.core.container.ObjectManager#addEObject(org.eclipse.emf.ecore.EObject)
	 */
	public void addEObject( EObject eObject ) {
	}

	/**
	 * @see org.teiid.designer.core.container.ObjectManager#clear()
	 */
	public void clear() {
	}

	/**
	 * @see org.teiid.designer.core.container.ObjectManager#findEObject(java.lang.String)
	 */
	@Override
	public EObject findEObject( String id ) {
		CoreArgCheck.isNotNull(id);

		// loop through resource sets resources, calling resource.getEObject()
		for (Resource resrc : container.getResources()) {
			if (resrc.isLoaded()) {
				EObject obj = findEObject(id, resrc);
				if (obj != null) {
					return obj;
				}
			}
		}

		// check unattached objects
		//return MtkXmiResourceImpl.DETACHED_UUID_TO_EOBJECT_MAP.get(id);
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.core.container.ObjectManager#findEObject(java.lang.String, org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public EObject findEObject( String id,
	                            Resource resource ) {
		CoreArgCheck.isNotNull(resource);
		return resource.getEObject(id);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.core.container.ObjectManager#getObjectId(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getObjectId( EObject object ) {
		CoreArgCheck.isNotNull(object);
		Resource resrc = object.eResource();
		if (resrc == null) {

			// check unattached objects
			return MtkXmiResourceImpl.DETACHED_EOBJECT_TO_UUID_MAP.get(object);
		}

		String uuid = resrc instanceof XResource ? ((XResource)resrc).getUuid(object) : resrc.getURIFragment(object);

		if ((uuid != null) && uuid.startsWith(UUID.PROTOCOL)) {
			return uuid;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.core.container.ObjectManager#processMassAdd(java.util.Collection)
	 */
	public void processMassAdd( Collection newObjects ) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.core.container.ObjectManager#processMassRemove(java.util.Collection)
	 */
	public void processMassRemove( Collection objects ) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.core.container.ObjectManager#processResourceChange(java.lang.Object,
	 *      org.eclipse.emf.ecore.resource.Resource, org.eclipse.emf.ecore.resource.Resource)
	 */
	public void processResourceChange( Object eObject,
	                                   Resource newResource,
	                                   Resource oldResource ) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.core.container.ObjectManager#removeEObject(org.eclipse.emf.ecore.EObject)
	 */
	public void removeEObject( EObject eObject ) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.core.container.ObjectManager#setObjectId(org.eclipse.emf.ecore.EObject, java.lang.String)
	 */
	@Override
	public void setObjectId( EObject object,
	                         String uuid ) {
		Resource resrc = object.eResource();
		if (resrc instanceof XResource) {
			((XResource)resrc).setUuid(object, uuid);
		} else {
			MtkXmiResourceImpl.DETACHED_EOBJECT_TO_UUID_MAP.put(object, uuid);
			MtkXmiResourceImpl.DETACHED_UUID_TO_EOBJECT_MAP.put(uuid, object);
		}
	}
}
