/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.wizards.jdbc;

import java.lang.reflect.InvocationTargetException;
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
import org.teiid.designer.jdbc.JdbcDriver;
import org.teiid.designer.jdbc.JdbcDriverContainer;

/**
 * @since 8.0
 */
public class XmlImporterJdbcDriver implements JdbcDriver {

	/**
	 * 
	 */
	public XmlImporterJdbcDriver() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#setName(java.lang.String)
	 */
	@Override
	public void setName(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#getUrlSyntax()
	 */
	@Override
	public String getUrlSyntax() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#setUrlSyntax(java.lang.String)
	 */
	@Override
	public void setUrlSyntax(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#getJarFileUris()
	 */
	@Override
	public EList getJarFileUris() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#getAvailableDriverClassNames()
	 */
	@Override
	public EList getAvailableDriverClassNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#getPreferredDriverClassName()
	 */
	@Override
	public String getPreferredDriverClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#setPreferredDriverClassName(java.lang.String)
	 */
	@Override
	public void setPreferredDriverClassName(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#getJdbcDriverContainer()
	 */
	@Override
	public JdbcDriverContainer getJdbcDriverContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.jdbc.JdbcDriver#setJdbcDriverContainer(org.teiid.designer.jdbc.JdbcDriverContainer)
	 */
	@Override
	public void setJdbcDriverContainer(JdbcDriverContainer value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eClass()
	 */
	@Override
	public EClass eClass() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eResource()
	 */
	@Override
	public Resource eResource() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eContainer()
	 */
	@Override
	public EObject eContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eContainingFeature()
	 */
	@Override
	public EStructuralFeature eContainingFeature() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eContainmentFeature()
	 */
	@Override
	public EReference eContainmentFeature() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eContents()
	 */
	@Override
	public EList eContents() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eAllContents()
	 */
	@Override
	public TreeIterator eAllContents() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eIsProxy()
	 */
	@Override
	public boolean eIsProxy() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eCrossReferences()
	 */
	@Override
	public EList eCrossReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eGet(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public Object eGet(EStructuralFeature feature) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eGet(org.eclipse.emf.ecore.EStructuralFeature, boolean)
	 */
	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eSet(org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object)
	 */
	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eUnset(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public void eUnset(EStructuralFeature feature) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#getObjectId()
	 */
	public ObjectID getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#setObjectId((org.teiid.core.id.ObjectID)
	 */
	public void setObjectId(ObjectID uuid) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.common.notify.Notifier#eAdapters()
	 */
	@Override
	public EList eAdapters() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.common.notify.Notifier#eDeliver()
	 */
	@Override
	public boolean eDeliver() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.common.notify.Notifier#eSetDeliver(boolean)
	 */
	@Override
	public void eSetDeliver(boolean deliver) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.common.notify.Notifier#eNotify(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public void eNotify(Notification notification) {
		// TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.EObject#eInvoke(org.eclipse.emf.ecore.EOperation, org.eclipse.emf.common.util.EList)
     */
    @Override
    public Object eInvoke( EOperation arg0,
                           EList<?> arg1 ) throws InvocationTargetException {
        return null;
    }

}
