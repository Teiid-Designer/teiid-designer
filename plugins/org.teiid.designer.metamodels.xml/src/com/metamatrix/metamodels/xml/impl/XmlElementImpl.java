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
import org.eclipse.xsd.XSDComponent;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.ProcessingInstructionHolder;
import com.metamatrix.metamodels.xml.ValueType;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlCommentHolder;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlEntityHolder;
import com.metamatrix.metamodels.xml.XmlHolderEntity;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlValueHolder;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Xml Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl#getComments <em>Comments</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl#getProcessingInstructions <em>Processing Instructions</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl#getEntities <em>Entities</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl#getValue <em>Value</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl#getValueType <em>Value Type</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl#isRecursive <em>Recursive</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl#getAttributes <em>Attributes</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class XmlElementImpl extends XmlBaseElementImpl implements XmlElement {
    /**
     * The cached value of the '{@link #getComments() <em>Comments</em>}' containment reference list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getComments()
     * @generated
     * @ordered
     */
    protected EList comments = null;

    /**
     * The cached value of the '{@link #getProcessingInstructions() <em>Processing Instructions</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getProcessingInstructions()
     * @generated
     * @ordered
     */
    protected EList processingInstructions = null;

    /**
     * The cached value of the '{@link #getEntities() <em>Entities</em>}' containment reference list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getEntities()
     * @generated
     * @ordered
     */
    protected EList entities = null;

    /**
     * The default value of the '{@link #getValue() <em>Value</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected static final String VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getValue() <em>Value</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected String value = VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getValueType() <em>Value Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getValueType()
     * @generated
     * @ordered
     */
    protected static final ValueType VALUE_TYPE_EDEFAULT = ValueType.IGNORED_LITERAL;

    /**
     * The cached value of the '{@link #getValueType() <em>Value Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getValueType()
     * @generated
     * @ordered
     */
    protected ValueType valueType = VALUE_TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #isRecursive() <em>Recursive</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isRecursive()
     * @generated
     * @ordered
     */
    protected static final boolean RECURSIVE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isRecursive() <em>Recursive</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isRecursive()
     * @generated
     * @ordered
     */
    protected boolean recursive = RECURSIVE_EDEFAULT;

    /**
     * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getAttributes()
     * @generated
     * @ordered
     */
    protected EList attributes = null;

    /**
     * The cached value of the '{@link #getDeclaredNamespaces() <em>Declared Namespaces</em>}' containment reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDeclaredNamespaces()
     * @generated
     * @ordered
     */
    protected EList declaredNamespaces = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected XmlElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID( final int derivedFeatureID,
                                         final Class baseClass ) {
        if (baseClass == XmlCommentHolder.class) switch (derivedFeatureID) {
            case XmlDocumentPackage.XML_ELEMENT__COMMENTS:
                return XmlDocumentPackage.XML_COMMENT_HOLDER__COMMENTS;
            default:
                return -1;
        }
        if (baseClass == ProcessingInstructionHolder.class) switch (derivedFeatureID) {
            case XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS:
                return XmlDocumentPackage.PROCESSING_INSTRUCTION_HOLDER__PROCESSING_INSTRUCTIONS;
            default:
                return -1;
        }
        if (baseClass == XmlEntityHolder.class) switch (derivedFeatureID) {
            case XmlDocumentPackage.XML_ELEMENT__ENTITIES:
                return XmlDocumentPackage.XML_ENTITY_HOLDER__ENTITIES;
            default:
                return -1;
        }
        if (baseClass == XmlValueHolder.class) switch (derivedFeatureID) {
            case XmlDocumentPackage.XML_ELEMENT__VALUE:
                return XmlDocumentPackage.XML_VALUE_HOLDER__VALUE;
            case XmlDocumentPackage.XML_ELEMENT__VALUE_TYPE:
                return XmlDocumentPackage.XML_VALUE_HOLDER__VALUE_TYPE;
            default:
                return -1;
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer( final NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) switch (eContainerFeatureID) {
            case XmlDocumentPackage.XML_ELEMENT__PARENT:
                return eContainer.eInverseRemove(this, XmlDocumentPackage.XML_ENTITY_HOLDER__ENTITIES, XmlEntityHolder.class, msgs);
            default:
                return eDynamicBasicRemoveFromContainer(msgs);
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID( final int baseFeatureID,
                                            final Class baseClass ) {
        if (baseClass == XmlCommentHolder.class) switch (baseFeatureID) {
            case XmlDocumentPackage.XML_COMMENT_HOLDER__COMMENTS:
                return XmlDocumentPackage.XML_ELEMENT__COMMENTS;
            default:
                return -1;
        }
        if (baseClass == ProcessingInstructionHolder.class) switch (baseFeatureID) {
            case XmlDocumentPackage.PROCESSING_INSTRUCTION_HOLDER__PROCESSING_INSTRUCTIONS:
                return XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS;
            default:
                return -1;
        }
        if (baseClass == XmlEntityHolder.class) switch (baseFeatureID) {
            case XmlDocumentPackage.XML_ENTITY_HOLDER__ENTITIES:
                return XmlDocumentPackage.XML_ELEMENT__ENTITIES;
            default:
                return -1;
        }
        if (baseClass == XmlValueHolder.class) switch (baseFeatureID) {
            case XmlDocumentPackage.XML_VALUE_HOLDER__VALUE:
                return XmlDocumentPackage.XML_ELEMENT__VALUE;
            case XmlDocumentPackage.XML_VALUE_HOLDER__VALUE_TYPE:
                return XmlDocumentPackage.XML_ELEMENT__VALUE_TYPE;
            default:
                return -1;
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet( final EStructuralFeature eFeature,
                        final boolean resolve ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_ELEMENT__BUILD_STATE:
                return getBuildState();
            case XmlDocumentPackage.XML_ELEMENT__NAME:
                return getName();
            case XmlDocumentPackage.XML_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                return isExcludeFromDocument() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_ELEMENT__MIN_OCCURS:
                return new Integer(getMinOccurs());
            case XmlDocumentPackage.XML_ELEMENT__MAX_OCCURS:
                return new Integer(getMaxOccurs());
            case XmlDocumentPackage.XML_ELEMENT__XSD_COMPONENT:
                if (resolve) return getXsdComponent();
                return basicGetXsdComponent();
            case XmlDocumentPackage.XML_ELEMENT__NAMESPACE:
                if (resolve) return getNamespace();
                return basicGetNamespace();
            case XmlDocumentPackage.XML_ELEMENT__CHOICE_CRITERIA:
                return getChoiceCriteria();
            case XmlDocumentPackage.XML_ELEMENT__CHOICE_ORDER:
                return new Integer(getChoiceOrder());
            case XmlDocumentPackage.XML_ELEMENT__DEFAULT_FOR:
                if (resolve) return getDefaultFor();
                return basicGetDefaultFor();
            case XmlDocumentPackage.XML_ELEMENT__PARENT:
                return getParent();
            case XmlDocumentPackage.XML_ELEMENT__COMMENTS:
                return getComments();
            case XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS:
                return getProcessingInstructions();
            case XmlDocumentPackage.XML_ELEMENT__ENTITIES:
                return getEntities();
            case XmlDocumentPackage.XML_ELEMENT__VALUE:
                return getValue();
            case XmlDocumentPackage.XML_ELEMENT__VALUE_TYPE:
                return getValueType();
            case XmlDocumentPackage.XML_ELEMENT__RECURSIVE:
                return isRecursive() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES:
                return getAttributes();
            case XmlDocumentPackage.XML_ELEMENT__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd( final InternalEObject otherEnd,
                                          final int featureID,
                                          final Class baseClass,
                                          NotificationChain msgs ) {
        if (featureID >= 0) switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
            case XmlDocumentPackage.XML_ELEMENT__DEFAULT_FOR:
                if (defaultFor != null) msgs = ((InternalEObject)defaultFor).eInverseRemove(this,
                                                                                            XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION,
                                                                                            XmlChoice.class,
                                                                                            msgs);
                return basicSetDefaultFor((XmlChoice)otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__PARENT:
                if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_ELEMENT__PARENT, msgs);
            case XmlDocumentPackage.XML_ELEMENT__COMMENTS:
                return ((InternalEList)getComments()).basicAdd(otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS:
                return ((InternalEList)getProcessingInstructions()).basicAdd(otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__ENTITIES:
                return ((InternalEList)getEntities()).basicAdd(otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES:
                return ((InternalEList)getAttributes()).basicAdd(otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__DECLARED_NAMESPACES:
                return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
            default:
                return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
        }
        if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove( final InternalEObject otherEnd,
                                             final int featureID,
                                             final Class baseClass,
                                             final NotificationChain msgs ) {
        if (featureID >= 0) switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
            case XmlDocumentPackage.XML_ELEMENT__DEFAULT_FOR:
                return basicSetDefaultFor(null, msgs);
            case XmlDocumentPackage.XML_ELEMENT__PARENT:
                return eBasicSetContainer(null, XmlDocumentPackage.XML_ELEMENT__PARENT, msgs);
            case XmlDocumentPackage.XML_ELEMENT__COMMENTS:
                return ((InternalEList)getComments()).basicRemove(otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS:
                return ((InternalEList)getProcessingInstructions()).basicRemove(otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__ENTITIES:
                return ((InternalEList)getEntities()).basicRemove(otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES:
                return ((InternalEList)getAttributes()).basicRemove(otherEnd, msgs);
            case XmlDocumentPackage.XML_ELEMENT__DECLARED_NAMESPACES:
                return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet( final EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_ELEMENT__BUILD_STATE:
                return buildState != BUILD_STATE_EDEFAULT;
            case XmlDocumentPackage.XML_ELEMENT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlDocumentPackage.XML_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                return excludeFromDocument != EXCLUDE_FROM_DOCUMENT_EDEFAULT;
            case XmlDocumentPackage.XML_ELEMENT__MIN_OCCURS:
                return getMinOccurs() != MIN_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_ELEMENT__MAX_OCCURS:
                return getMaxOccurs() != MAX_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_ELEMENT__XSD_COMPONENT:
                return xsdComponent != null;
            case XmlDocumentPackage.XML_ELEMENT__NAMESPACE:
                return namespace != null;
            case XmlDocumentPackage.XML_ELEMENT__CHOICE_CRITERIA:
                return CHOICE_CRITERIA_EDEFAULT == null ? choiceCriteria != null : !CHOICE_CRITERIA_EDEFAULT.equals(choiceCriteria);
            case XmlDocumentPackage.XML_ELEMENT__CHOICE_ORDER:
                return choiceOrder != CHOICE_ORDER_EDEFAULT;
            case XmlDocumentPackage.XML_ELEMENT__DEFAULT_FOR:
                return defaultFor != null;
            case XmlDocumentPackage.XML_ELEMENT__PARENT:
                return getParent() != null;
            case XmlDocumentPackage.XML_ELEMENT__COMMENTS:
                return comments != null && !comments.isEmpty();
            case XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS:
                return processingInstructions != null && !processingInstructions.isEmpty();
            case XmlDocumentPackage.XML_ELEMENT__ENTITIES:
                return entities != null && !entities.isEmpty();
            case XmlDocumentPackage.XML_ELEMENT__VALUE:
                return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
            case XmlDocumentPackage.XML_ELEMENT__VALUE_TYPE:
                return valueType != VALUE_TYPE_EDEFAULT;
            case XmlDocumentPackage.XML_ELEMENT__RECURSIVE:
                return recursive != RECURSIVE_EDEFAULT;
            case XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES:
                return attributes != null && !attributes.isEmpty();
            case XmlDocumentPackage.XML_ELEMENT__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet( final EStructuralFeature eFeature,
                      final Object newValue ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_ELEMENT__BUILD_STATE:
                setBuildState((BuildStatus)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__NAME:
                setName((String)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_ELEMENT__XSD_COMPONENT:
                setXsdComponent((XSDComponent)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__NAMESPACE:
                setNamespace((XmlNamespace)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__CHOICE_CRITERIA:
                setChoiceCriteria((String)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__CHOICE_ORDER:
                setChoiceOrder(((Integer)newValue).intValue());
                return;
            case XmlDocumentPackage.XML_ELEMENT__DEFAULT_FOR:
                setDefaultFor((XmlChoice)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__PARENT:
                setParent((XmlEntityHolder)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__COMMENTS:
                getComments().clear();
                getComments().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS:
                getProcessingInstructions().clear();
                getProcessingInstructions().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__ENTITIES:
                getEntities().clear();
                getEntities().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__VALUE:
                setValue((String)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__VALUE_TYPE:
                setValueType((ValueType)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__RECURSIVE:
                setRecursive(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES:
                getAttributes().clear();
                getAttributes().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ELEMENT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlElement();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( final EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_ELEMENT__BUILD_STATE:
                setBuildState(BUILD_STATE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ELEMENT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(EXCLUDE_FROM_DOCUMENT_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ELEMENT__XSD_COMPONENT:
                setXsdComponent((XSDComponent)null);
                return;
            case XmlDocumentPackage.XML_ELEMENT__NAMESPACE:
                setNamespace((XmlNamespace)null);
                return;
            case XmlDocumentPackage.XML_ELEMENT__CHOICE_CRITERIA:
                setChoiceCriteria(CHOICE_CRITERIA_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ELEMENT__CHOICE_ORDER:
                setChoiceOrder(CHOICE_ORDER_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ELEMENT__DEFAULT_FOR:
                setDefaultFor((XmlChoice)null);
                return;
            case XmlDocumentPackage.XML_ELEMENT__PARENT:
                setParent((XmlEntityHolder)null);
                return;
            case XmlDocumentPackage.XML_ELEMENT__COMMENTS:
                getComments().clear();
                return;
            case XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS:
                getProcessingInstructions().clear();
                return;
            case XmlDocumentPackage.XML_ELEMENT__ENTITIES:
                getEntities().clear();
                return;
            case XmlDocumentPackage.XML_ELEMENT__VALUE:
                setValue(VALUE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ELEMENT__VALUE_TYPE:
                setValueType(VALUE_TYPE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ELEMENT__RECURSIVE:
                setRecursive(RECURSIVE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES:
                getAttributes().clear();
                return;
            case XmlDocumentPackage.XML_ELEMENT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getAttributes() {
        if (attributes == null) attributes = new EObjectContainmentWithInverseEList(XmlAttribute.class, this,
                                                                                    XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES,
                                                                                    XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT);
        return attributes;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getComments() {
        if (comments == null) comments = new EObjectContainmentWithInverseEList(XmlComment.class, this,
                                                                                XmlDocumentPackage.XML_ELEMENT__COMMENTS,
                                                                                XmlDocumentPackage.XML_COMMENT__PARENT);
        return comments;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) declaredNamespaces = new EObjectContainmentWithInverseEList(
                                                                                                    XmlNamespace.class,
                                                                                                    this,
                                                                                                    XmlDocumentPackage.XML_ELEMENT__DECLARED_NAMESPACES,
                                                                                                    XmlDocumentPackage.XML_NAMESPACE__ELEMENT);
        return declaredNamespaces;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getEntities() {
        if (entities == null) entities = new EObjectContainmentWithInverseEList(XmlHolderEntity.class, this,
                                                                                XmlDocumentPackage.XML_ELEMENT__ENTITIES,
                                                                                XmlDocumentPackage.XML_HOLDER_ENTITY__PARENT);
        return entities;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getProcessingInstructions() {
        if (processingInstructions == null) processingInstructions = new EObjectContainmentWithInverseEList(
                                                                                                            ProcessingInstruction.class,
                                                                                                            this,
                                                                                                            XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS,
                                                                                                            XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT);
        return processingInstructions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isRecursive() {
        return recursive;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public boolean isValueDefault() {
        final ValueType valueType = getValueType();
        return ValueType.DEFAULT == valueType.getValue();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isValueDefaultGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public boolean isValueFixed() {
        final ValueType valueType = getValueType();
        return ValueType.FIXED == valueType.getValue();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isValueFixedGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setRecursive( final boolean newRecursive ) {
        final boolean oldRecursive = recursive;
        recursive = newRecursive;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   XmlDocumentPackage.XML_ELEMENT__RECURSIVE, oldRecursive,
                                                                   recursive));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setValue( final String newValue ) {
        final String oldValue = value;
        value = newValue;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_ELEMENT__VALUE,
                                                                   oldValue, value));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setValueType( final ValueType newValueType ) {
        final ValueType oldValueType = valueType;
        valueType = newValueType == null ? VALUE_TYPE_EDEFAULT : newValueType;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   XmlDocumentPackage.XML_ELEMENT__VALUE_TYPE, oldValueType,
                                                                   valueType));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        final StringBuffer result = new StringBuffer(super.toString());
        result.append(" (value: "); //$NON-NLS-1$
        result.append(value);
        result.append(", valueType: "); //$NON-NLS-1$
        result.append(valueType);
        result.append(", recursive: "); //$NON-NLS-1$
        result.append(recursive);
        result.append(')');
        return result.toString();
    }

} // XmlElementImpl
