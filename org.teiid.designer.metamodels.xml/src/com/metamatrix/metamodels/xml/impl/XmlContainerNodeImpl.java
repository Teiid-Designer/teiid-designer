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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDParticle;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.ChoiceOption;
import com.metamatrix.metamodels.xml.XmlBaseElement;
import com.metamatrix.metamodels.xml.XmlBuildable;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlContainerHolder;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElementHolder;
import com.metamatrix.metamodels.xsd.XsdUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Container Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getContainers <em>Containers</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getChoiceCriteria <em>Choice Criteria</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getChoiceOrder <em>Choice Order</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getDefaultFor <em>Default For</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getBuildState <em>Build State</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#isExcludeFromDocument <em>Exclude From Document</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getMinOccurs <em>Min Occurs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getMaxOccurs <em>Max Occurs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getXsdComponent <em>Xsd Component</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class XmlContainerNodeImpl extends XmlDocumentEntityImpl implements XmlContainerNode {
    /**
     * The cached value of the '{@link #getElements() <em>Elements</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getElements()
     * @generated
     * @ordered
     */
    protected EList elements = null;

    /**
     * The cached value of the '{@link #getContainers() <em>Containers</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getContainers()
     * @generated
     * @ordered
     */
    protected EList containers = null;

    /**
     * The default value of the '{@link #getChoiceCriteria() <em>Choice Criteria</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChoiceCriteria()
     * @generated
     * @ordered
     */
    protected static final String CHOICE_CRITERIA_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getChoiceCriteria() <em>Choice Criteria</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChoiceCriteria()
     * @generated
     * @ordered
     */
    protected String choiceCriteria = CHOICE_CRITERIA_EDEFAULT;

    /**
     * The default value of the '{@link #getChoiceOrder() <em>Choice Order</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getChoiceOrder()
     * @generated
     * @ordered
     */
	protected static final int CHOICE_ORDER_EDEFAULT = -1;

    /**
     * The cached value of the '{@link #getChoiceOrder() <em>Choice Order</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getChoiceOrder()
     * @generated
     * @ordered
     */
	protected int choiceOrder = CHOICE_ORDER_EDEFAULT;

    /**
     * The cached value of the '{@link #getDefaultFor() <em>Default For</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultFor()
     * @generated
     * @ordered
     */
    protected XmlChoice defaultFor = null;

    /**
     * The default value of the '{@link #getBuildState() <em>Build State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBuildState()
     * @generated
     * @ordered
     */
    protected static final BuildStatus BUILD_STATE_EDEFAULT = BuildStatus.COMPLETE_LITERAL;

    /**
     * The cached value of the '{@link #getBuildState() <em>Build State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBuildState()
     * @generated
     * @ordered
     */
    protected BuildStatus buildState = BUILD_STATE_EDEFAULT;

    /**
     * The default value of the '{@link #isExcludeFromDocument() <em>Exclude From Document</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isExcludeFromDocument()
     * @generated
     * @ordered
     */
    protected static final boolean EXCLUDE_FROM_DOCUMENT_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isExcludeFromDocument() <em>Exclude From Document</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isExcludeFromDocument()
     * @generated
     * @ordered
     */
    protected boolean excludeFromDocument = EXCLUDE_FROM_DOCUMENT_EDEFAULT;

    /**
     * The default value of the '{@link #getMinOccurs() <em>Min Occurs</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMinOccurs()
     * @generated
     * @ordered
     */
    protected static final int MIN_OCCURS_EDEFAULT = 0;

    /**
     * The default value of the '{@link #getMaxOccurs() <em>Max Occurs</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxOccurs()
     * @generated
     * @ordered
     */
    protected static final int MAX_OCCURS_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getXsdComponent() <em>Xsd Component</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getXsdComponent()
     * @generated
     * @ordered
     */
    protected XSDComponent xsdComponent = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlContainerNodeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlContainerNode();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getElements() {
        if (elements == null) {
            elements = new EObjectContainmentWithInverseEList(XmlBaseElement.class, this, XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS, XmlDocumentPackage.XML_BASE_ELEMENT__PARENT);
        }
        return elements;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getContainers() {
        if (containers == null) {
            containers = new EObjectContainmentWithInverseEList(XmlContainerNode.class, this, XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS, XmlDocumentPackage.XML_CONTAINER_NODE__PARENT);
        }
        return containers;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getChoiceCriteria() {
        return choiceCriteria;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setChoiceCriteria(String newChoiceCriteria) {
        String oldChoiceCriteria = choiceCriteria;
        choiceCriteria = newChoiceCriteria;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_CRITERIA, oldChoiceCriteria, choiceCriteria));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public int getChoiceOrder() {
        return choiceOrder;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setChoiceOrder(int newChoiceOrder) {
        int oldChoiceOrder = choiceOrder;
        choiceOrder = newChoiceOrder;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_ORDER, oldChoiceOrder, choiceOrder));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlChoice getDefaultFor() {
        if (defaultFor != null && defaultFor.eIsProxy()) {
            XmlChoice oldDefaultFor = defaultFor;
            defaultFor = (XmlChoice)eResolveProxy((InternalEObject)defaultFor);
            if (defaultFor != oldDefaultFor) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR, oldDefaultFor, defaultFor));
            }
        }
        return defaultFor;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlChoice basicGetDefaultFor() {
        return defaultFor;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetDefaultFor(XmlChoice newDefaultFor, NotificationChain msgs) {
        XmlChoice oldDefaultFor = defaultFor;
        defaultFor = newDefaultFor;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR, oldDefaultFor, newDefaultFor);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDefaultFor(XmlChoice newDefaultFor) {
        if (newDefaultFor != defaultFor) {
            NotificationChain msgs = null;
            if (defaultFor != null)
                msgs = ((InternalEObject)defaultFor).eInverseRemove(this, XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION, XmlChoice.class, msgs);
            if (newDefaultFor != null)
                msgs = ((InternalEObject)newDefaultFor).eInverseAdd(this, XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION, XmlChoice.class, msgs);
            msgs = basicSetDefaultFor(newDefaultFor, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR, newDefaultFor, newDefaultFor));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BuildStatus getBuildState() {
        return buildState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBuildState(BuildStatus newBuildState) {
        BuildStatus oldBuildState = buildState;
        buildState = newBuildState == null ? BUILD_STATE_EDEFAULT : newBuildState;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_CONTAINER_NODE__BUILD_STATE, oldBuildState, buildState));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isExcludeFromDocument() {
        return excludeFromDocument;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setExcludeFromDocument(boolean newExcludeFromDocument) {
        boolean oldExcludeFromDocument = excludeFromDocument;
        excludeFromDocument = newExcludeFromDocument;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT, oldExcludeFromDocument, excludeFromDocument));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XSDComponent getXsdComponent() {
        if (xsdComponent != null && xsdComponent.eIsProxy()) {
            XSDComponent oldXsdComponent = xsdComponent;
            xsdComponent = (XSDComponent)eResolveProxy((InternalEObject)xsdComponent);
            if (xsdComponent != oldXsdComponent) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, XmlDocumentPackage.XML_CONTAINER_NODE__XSD_COMPONENT, oldXsdComponent, xsdComponent));
            }
        }
        return xsdComponent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XSDComponent basicGetXsdComponent() {
        return xsdComponent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setXsdComponent(XSDComponent newXsdComponent) {
        XSDComponent oldXsdComponent = xsdComponent;
        xsdComponent = newXsdComponent;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_CONTAINER_NODE__XSD_COMPONENT, oldXsdComponent, xsdComponent));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlContainerHolder getParent() {
        if (eContainerFeatureID != XmlDocumentPackage.XML_CONTAINER_NODE__PARENT) return null;
        return (XmlContainerHolder)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setParent(XmlContainerHolder newParent) {
        if (newParent != eContainer || (eContainerFeatureID != XmlDocumentPackage.XML_CONTAINER_NODE__PARENT && newParent != null)) {
            if (EcoreUtil.isAncestor(this, newParent))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newParent != null)
                msgs = ((InternalEObject)newParent).eInverseAdd(this, XmlDocumentPackage.XML_CONTAINER_HOLDER__CONTAINERS, XmlContainerHolder.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newParent, XmlDocumentPackage.XML_CONTAINER_NODE__PARENT, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_CONTAINER_NODE__PARENT, newParent, newParent));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public int getMinOccurs() {
        final XSDComponent schemaComp = this.getXsdComponent();
        if ( schemaComp != null ) {
            if ( schemaComp instanceof XSDParticle ) {
                return XsdUtil.getMinOccurs(schemaComp);
            }
        }else{
            return 1;
        }
            
        //climb in the xsd until we find an XSDParticle or get to the root
        EObject parent = schemaComp.eContainer();
        XSDParticle particle = null;
        while(parent != null && particle == null){
            if(parent instanceof XSDParticle){
                particle = (XSDParticle)parent;
            }else{
                parent = parent.eContainer();
            }
        }
            
        if(particle != null){
            return particle.getMinOccurs();
        }

        return 1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getMinOccursGen() {
        // TODO: implement this method to return the 'Min Occurs' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public int getMaxOccurs() {
        final XSDComponent schemaComp = this.getXsdComponent();
        if ( schemaComp != null ) {
            if ( schemaComp instanceof XSDParticle ) {
                return XsdUtil.getMaxOccurs(schemaComp);
            }
        }else{
            return 1;
        }
        
        //climb in the xsd until we find an XSDParticle or get to the root
        EObject parent = schemaComp.eContainer();
        XSDParticle particle = null;
        while(parent != null && particle == null){
            if(parent instanceof XSDParticle){
                particle = (XSDParticle)parent;
            }else{
                parent = parent.eContainer();
            }
        }
        
        if(particle != null){
            return particle.getMaxOccurs();
        }
        
        return 1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getMaxOccursGen() {
        // TODO: implement this method to return the 'Max Occurs' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
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
                case XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS:
                    return ((InternalEList)getContainers()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR:
                    if (defaultFor != null)
                        msgs = ((InternalEObject)defaultFor).eInverseRemove(this, XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION, XmlChoice.class, msgs);
                    return basicSetDefaultFor((XmlChoice)otherEnd, msgs);
                case XmlDocumentPackage.XML_CONTAINER_NODE__PARENT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_CONTAINER_NODE__PARENT, msgs);
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
                case XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS:
                    return ((InternalEList)getContainers()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR:
                    return basicSetDefaultFor(null, msgs);
                case XmlDocumentPackage.XML_CONTAINER_NODE__PARENT:
                    return eBasicSetContainer(null, XmlDocumentPackage.XML_CONTAINER_NODE__PARENT, msgs);
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
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case XmlDocumentPackage.XML_CONTAINER_NODE__PARENT:
                    return eContainer.eInverseRemove(this, XmlDocumentPackage.XML_CONTAINER_HOLDER__CONTAINERS, XmlContainerHolder.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS:
                return getElements();
            case XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS:
                return getContainers();
            case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_CRITERIA:
                return getChoiceCriteria();
            case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_ORDER:
                return new Integer(getChoiceOrder());
            case XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR:
                if (resolve) return getDefaultFor();
                return basicGetDefaultFor();
            case XmlDocumentPackage.XML_CONTAINER_NODE__BUILD_STATE:
                return getBuildState();
            case XmlDocumentPackage.XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT:
                return isExcludeFromDocument() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_CONTAINER_NODE__MIN_OCCURS:
                return new Integer(getMinOccurs());
            case XmlDocumentPackage.XML_CONTAINER_NODE__MAX_OCCURS:
                return new Integer(getMaxOccurs());
            case XmlDocumentPackage.XML_CONTAINER_NODE__XSD_COMPONENT:
                if (resolve) return getXsdComponent();
                return basicGetXsdComponent();
            case XmlDocumentPackage.XML_CONTAINER_NODE__PARENT:
                return getParent();
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
            case XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS:
                getContainers().clear();
                getContainers().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_CRITERIA:
                setChoiceCriteria((String)newValue);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_ORDER:
                setChoiceOrder(((Integer)newValue).intValue());
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR:
                setDefaultFor((XmlChoice)newValue);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__BUILD_STATE:
                setBuildState((BuildStatus)newValue);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)newValue);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__PARENT:
                setParent((XmlContainerHolder)newValue);
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
            case XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS:
                getElements().clear();
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS:
                getContainers().clear();
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_CRITERIA:
                setChoiceCriteria(CHOICE_CRITERIA_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_ORDER:
                setChoiceOrder(CHOICE_ORDER_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR:
                setDefaultFor((XmlChoice)null);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__BUILD_STATE:
                setBuildState(BUILD_STATE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(EXCLUDE_FROM_DOCUMENT_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)null);
                return;
            case XmlDocumentPackage.XML_CONTAINER_NODE__PARENT:
                setParent((XmlContainerHolder)null);
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
            case XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS:
                return containers != null && !containers.isEmpty();
            case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_CRITERIA:
                return CHOICE_CRITERIA_EDEFAULT == null ? choiceCriteria != null : !CHOICE_CRITERIA_EDEFAULT.equals(choiceCriteria);
            case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_ORDER:
                return choiceOrder != CHOICE_ORDER_EDEFAULT;
            case XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR:
                return defaultFor != null;
            case XmlDocumentPackage.XML_CONTAINER_NODE__BUILD_STATE:
                return buildState != BUILD_STATE_EDEFAULT;
            case XmlDocumentPackage.XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT:
                return excludeFromDocument != EXCLUDE_FROM_DOCUMENT_EDEFAULT;
            case XmlDocumentPackage.XML_CONTAINER_NODE__MIN_OCCURS:
                return getMinOccurs() != MIN_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_CONTAINER_NODE__MAX_OCCURS:
                return getMaxOccurs() != MAX_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_CONTAINER_NODE__XSD_COMPONENT:
                return xsdComponent != null;
            case XmlDocumentPackage.XML_CONTAINER_NODE__PARENT:
                return getParent() != null;
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
        if (baseClass == XmlElementHolder.class) {
            switch (derivedFeatureID) {
                case XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS: return XmlDocumentPackage.XML_ELEMENT_HOLDER__ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == XmlContainerHolder.class) {
            switch (derivedFeatureID) {
                case XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS: return XmlDocumentPackage.XML_CONTAINER_HOLDER__CONTAINERS;
                default: return -1;
            }
        }
        if (baseClass == ChoiceOption.class) {
            switch (derivedFeatureID) {
                case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_CRITERIA: return XmlDocumentPackage.CHOICE_OPTION__CHOICE_CRITERIA;
                case XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_ORDER: return XmlDocumentPackage.CHOICE_OPTION__CHOICE_ORDER;
                case XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR: return XmlDocumentPackage.CHOICE_OPTION__DEFAULT_FOR;
                default: return -1;
            }
        }
        if (baseClass == XmlBuildable.class) {
            switch (derivedFeatureID) {
                case XmlDocumentPackage.XML_CONTAINER_NODE__BUILD_STATE: return XmlDocumentPackage.XML_BUILDABLE__BUILD_STATE;
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
        if (baseClass == XmlElementHolder.class) {
            switch (baseFeatureID) {
                case XmlDocumentPackage.XML_ELEMENT_HOLDER__ELEMENTS: return XmlDocumentPackage.XML_CONTAINER_NODE__ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == XmlContainerHolder.class) {
            switch (baseFeatureID) {
                case XmlDocumentPackage.XML_CONTAINER_HOLDER__CONTAINERS: return XmlDocumentPackage.XML_CONTAINER_NODE__CONTAINERS;
                default: return -1;
            }
        }
        if (baseClass == ChoiceOption.class) {
            switch (baseFeatureID) {
                case XmlDocumentPackage.CHOICE_OPTION__CHOICE_CRITERIA: return XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_CRITERIA;
                case XmlDocumentPackage.CHOICE_OPTION__CHOICE_ORDER: return XmlDocumentPackage.XML_CONTAINER_NODE__CHOICE_ORDER;
                case XmlDocumentPackage.CHOICE_OPTION__DEFAULT_FOR: return XmlDocumentPackage.XML_CONTAINER_NODE__DEFAULT_FOR;
                default: return -1;
            }
        }
        if (baseClass == XmlBuildable.class) {
            switch (baseFeatureID) {
                case XmlDocumentPackage.XML_BUILDABLE__BUILD_STATE: return XmlDocumentPackage.XML_CONTAINER_NODE__BUILD_STATE;
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
        result.append(" (choiceCriteria: "); //$NON-NLS-1$
        result.append(choiceCriteria);
        result.append(", choiceOrder: "); //$NON-NLS-1$
        result.append(choiceOrder);
        result.append(", buildState: "); //$NON-NLS-1$
        result.append(buildState);
        result.append(", excludeFromDocument: "); //$NON-NLS-1$
        result.append(excludeFromDocument);
        result.append(')');
        return result.toString();
    }

} //XmlContainerNodeImpl
