/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.ProcessingInstructionHolder;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlCommentHolder;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlRoot;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Fragment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlFragmentImpl#getComments <em>Comments</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlFragmentImpl#getProcessingInstructions <em>Processing Instructions</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlFragmentImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlFragmentImpl#getRoot <em>Root</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XmlFragmentImpl extends XmlDocumentEntityImpl implements XmlFragment {
    /**
     * The cached value of the '{@link #getComments() <em>Comments</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getComments()
     * @generated
     * @ordered
     */
    protected EList comments = null;

    /**
     * The cached value of the '{@link #getProcessingInstructions() <em>Processing Instructions</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProcessingInstructions()
     * @generated
     * @ordered
     */
    protected EList processingInstructions = null;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getRoot() <em>Root</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRoot()
     * @generated
     * @ordered
     */
    protected XmlRoot root = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlFragmentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlFragment();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getComments() {
        if (comments == null) {
            comments = new EObjectContainmentWithInverseEList(XmlComment.class, this, XmlDocumentPackage.XML_FRAGMENT__COMMENTS, XmlDocumentPackage.XML_COMMENT__PARENT);
        }
        return comments;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getProcessingInstructions() {
        if (processingInstructions == null) {
            processingInstructions = new EObjectContainmentWithInverseEList(ProcessingInstruction.class, this, XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS, XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT);
        }
        return processingInstructions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_FRAGMENT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlRoot getRoot() {
        return root;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetRoot(XmlRoot newRoot, NotificationChain msgs) {
        XmlRoot oldRoot = root;
        root = newRoot;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_FRAGMENT__ROOT, oldRoot, newRoot);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRoot(XmlRoot newRoot) {
        if (newRoot != root) {
            NotificationChain msgs = null;
            if (root != null)
                msgs = ((InternalEObject)root).eInverseRemove(this, XmlDocumentPackage.XML_ROOT__FRAGMENT, XmlRoot.class, msgs);
            if (newRoot != null)
                msgs = ((InternalEObject)newRoot).eInverseAdd(this, XmlDocumentPackage.XML_ROOT__FRAGMENT, XmlRoot.class, msgs);
            msgs = basicSetRoot(newRoot, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_FRAGMENT__ROOT, newRoot, newRoot));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case XmlDocumentPackage.XML_FRAGMENT__COMMENTS:
                    return ((InternalEList)getComments()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS:
                    return ((InternalEList)getProcessingInstructions()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_FRAGMENT__ROOT:
                    if (root != null)
                        msgs = ((InternalEObject)root).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - XmlDocumentPackage.XML_FRAGMENT__ROOT, null, msgs);
                    return basicSetRoot((XmlRoot)otherEnd, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case XmlDocumentPackage.XML_FRAGMENT__COMMENTS:
                    return ((InternalEList)getComments()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS:
                    return ((InternalEList)getProcessingInstructions()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_FRAGMENT__ROOT:
                    return basicSetRoot(null, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_FRAGMENT__COMMENTS:
                return getComments();
            case XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS:
                return getProcessingInstructions();
            case XmlDocumentPackage.XML_FRAGMENT__NAME:
                return getName();
            case XmlDocumentPackage.XML_FRAGMENT__ROOT:
                return getRoot();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_FRAGMENT__COMMENTS:
                getComments().clear();
                getComments().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS:
                getProcessingInstructions().clear();
                getProcessingInstructions().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT__NAME:
                setName((String)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT__ROOT:
                setRoot((XmlRoot)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_FRAGMENT__COMMENTS:
                getComments().clear();
                return;
            case XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS:
                getProcessingInstructions().clear();
                return;
            case XmlDocumentPackage.XML_FRAGMENT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_FRAGMENT__ROOT:
                setRoot((XmlRoot)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_FRAGMENT__COMMENTS:
                return comments != null && !comments.isEmpty();
            case XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS:
                return processingInstructions != null && !processingInstructions.isEmpty();
            case XmlDocumentPackage.XML_FRAGMENT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlDocumentPackage.XML_FRAGMENT__ROOT:
                return root != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass) {
        if (baseClass == XmlCommentHolder.class) {
            switch (derivedFeatureID) {
                case XmlDocumentPackage.XML_FRAGMENT__COMMENTS: return XmlDocumentPackage.XML_COMMENT_HOLDER__COMMENTS;
                default: return -1;
            }
        }
        if (baseClass == ProcessingInstructionHolder.class) {
            switch (derivedFeatureID) {
                case XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS: return XmlDocumentPackage.PROCESSING_INSTRUCTION_HOLDER__PROCESSING_INSTRUCTIONS;
                default: return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass) {
        if (baseClass == XmlCommentHolder.class) {
            switch (baseFeatureID) {
                case XmlDocumentPackage.XML_COMMENT_HOLDER__COMMENTS: return XmlDocumentPackage.XML_FRAGMENT__COMMENTS;
                default: return -1;
            }
        }
        if (baseClass == ProcessingInstructionHolder.class) {
            switch (baseFeatureID) {
                case XmlDocumentPackage.PROCESSING_INSTRUCTION_HOLDER__PROCESSING_INSTRUCTIONS: return XmlDocumentPackage.XML_FRAGMENT__PROCESSING_INSTRUCTIONS;
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(')');
        return result.toString();
    }

} //XmlFragmentImpl
