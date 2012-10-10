/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.testsupport;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.id.ObjectID;

/**
 * FakeEObject
 */
public class FakeEObject implements EObject {

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eClass()
     */
    @Override
	public EClass eClass() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eResource()
     */
    @Override
	public Resource eResource() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eContainer()
     */
    @Override
	public EObject eContainer() {
        return null;
    }

    /**
     * @see org.eclipse.emf.ecore.EObject#eContainingFeature()
     * @since 4.3
     */
    @Override
	public EStructuralFeature eContainingFeature() {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eContainmentFeature()
     */
    @Override
	public EReference eContainmentFeature() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eContents()
     */
    @Override
	public EList eContents() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eAllContents()
     */
    @Override
	public TreeIterator eAllContents() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eIsProxy()
     */
    @Override
	public boolean eIsProxy() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eCrossReferences()
     */
    @Override
	public EList eCrossReferences() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eGet(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
	public Object eGet(EStructuralFeature feature) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eGet(org.eclipse.emf.ecore.EStructuralFeature, boolean)
     */
    @Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eSet(org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object)
     */
    @Override
	public void eSet(EStructuralFeature feature, Object newValue) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
	public boolean eIsSet(EStructuralFeature feature) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.EObject#eUnset(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
	public void eUnset(EStructuralFeature feature) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notifier#eAdapters()
     */
    @Override
	public EList eAdapters() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notifier#eDeliver()
     */
    @Override
	public boolean eDeliver() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notifier#eSetDeliver(boolean)
     */
    @Override
	public void eSetDeliver(boolean deliver) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notifier#eNotify(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void eNotify(Notification notification) {

    }    
    

    /** 
     * @see org.eclipse.emf.ecore.EObject#getObjectId()
     * @since 4.1
     */
    public ObjectID getObjectId() {
        return null;
    }
    /** 
     * @see org.eclipse.emf.ecore.EObject#setObjectId((org.teiid.core.id.ObjectID)
     * @since 4.1
     */
    public void setObjectId(ObjectID uuid) {
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.emf.ecore.EObject#eInvoke(org.eclipse.emf.ecore.EOperation, org.eclipse.emf.common.util.EList)
     */
    @Override
    public Object eInvoke( EOperation arg0,
                           EList<?> arg1 ) {
        return null;
    }
}
