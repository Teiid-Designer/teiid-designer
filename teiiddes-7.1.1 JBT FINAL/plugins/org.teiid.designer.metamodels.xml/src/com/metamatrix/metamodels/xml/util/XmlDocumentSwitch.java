/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.util;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.xml.ChoiceOption;
import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.ProcessingInstructionHolder;
import com.metamatrix.metamodels.xml.XmlAll;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlBaseElement;
import com.metamatrix.metamodels.xml.XmlBuildable;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlCommentHolder;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlEntityHolder;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlFragmentUse;
import com.metamatrix.metamodels.xml.XmlHolderEntity;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.XmlSequence;
import com.metamatrix.metamodels.xml.XmlValueHolder;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call {@link #doSwitch
 * doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model, starting with the actual class of the
 * object and proceeding up the inheritance hierarchy until a non-null result is returned, which is the result of the switch. <!--
 * end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage
 * @generated
 */
public class XmlDocumentSwitch {
    /**
     * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected static XmlDocumentPackage modelPackage;

    /**
     * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlDocumentSwitch() {
        if (modelPackage == null) modelPackage = XmlDocumentPackage.eINSTANCE;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Choice Option</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Choice Option</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseChoiceOption( final ChoiceOption object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Processing Instruction</em>'. <!-- begin-user-doc -->
     * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Processing Instruction</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProcessingInstruction( final ProcessingInstruction object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Processing Instruction Holder</em>'. <!-- begin-user-doc
     * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Processing Instruction Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProcessingInstructionHolder( final ProcessingInstructionHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml All</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml All</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlAll( final XmlAll object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Attribute</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Attribute</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlAttribute( final XmlAttribute object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Base Element</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Base Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlBaseElement( final XmlBaseElement object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Buildable</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Buildable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlBuildable( final XmlBuildable object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Choice</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Choice</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlChoice( final XmlChoice object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Comment</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Comment</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlComment( final XmlComment object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Comment Holder</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Comment Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlCommentHolder( final XmlCommentHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Container Node</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Container Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlContainerNode( final XmlContainerNode object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Document</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Document</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlDocument( final XmlDocument object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Entity</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Entity</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlDocumentEntity( final XmlDocumentEntity object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Node</em>'. <!-- begin-user-doc --> This implementation
     * returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlDocumentNode( final XmlDocumentNode object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Element</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlElement( final XmlElement object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Entity Holder</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Entity Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlEntityHolder( final XmlEntityHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Fragment</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Fragment</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlFragment( final XmlFragment object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Fragment Use</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Fragment Use</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlFragmentUse( final XmlFragmentUse object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Holder Entity</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Holder Entity</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlHolderEntity( final XmlHolderEntity object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Namespace</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Namespace</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlNamespace( final XmlNamespace object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Root</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlRoot( final XmlRoot object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Sequence</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Sequence</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlSequence( final XmlSequence object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Xml Value Holder</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Xml Value Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlValueHolder( final XmlValueHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch, but this is the last case anyway. <!--
     * end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase( final EObject object ) {
        return null;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch( final EClass theEClass,
                               final EObject theEObject ) {
        if (theEClass.eContainer() == modelPackage) return doSwitch(theEClass.getClassifierID(), theEObject);
        final List eSuperTypes = theEClass.getESuperTypes();
        return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch( final EObject theEObject ) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch( final int classifierID,
                               final EObject theEObject ) {
        switch (classifierID) {
            case XmlDocumentPackage.XML_FRAGMENT: {
                final XmlFragment xmlFragment = (XmlFragment)theEObject;
                Object result = caseXmlFragment(xmlFragment);
                if (result == null) result = caseXmlDocumentEntity(xmlFragment);
                if (result == null) result = caseXmlCommentHolder(xmlFragment);
                if (result == null) result = caseProcessingInstructionHolder(xmlFragment);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_DOCUMENT: {
                final XmlDocument xmlDocument = (XmlDocument)theEObject;
                Object result = caseXmlDocument(xmlDocument);
                if (result == null) result = caseXmlFragment(xmlDocument);
                if (result == null) result = caseXmlDocumentEntity(xmlDocument);
                if (result == null) result = caseXmlCommentHolder(xmlDocument);
                if (result == null) result = caseProcessingInstructionHolder(xmlDocument);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_DOCUMENT_ENTITY: {
                final XmlDocumentEntity xmlDocumentEntity = (XmlDocumentEntity)theEObject;
                Object result = caseXmlDocumentEntity(xmlDocumentEntity);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_HOLDER_ENTITY: {
                final XmlHolderEntity xmlHolderEntity = (XmlHolderEntity)theEObject;
                Object result = caseXmlHolderEntity(xmlHolderEntity);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ELEMENT: {
                final XmlElement xmlElement = (XmlElement)theEObject;
                Object result = caseXmlElement(xmlElement);
                if (result == null) result = caseXmlBaseElement(xmlElement);
                if (result == null) result = caseXmlCommentHolder(xmlElement);
                if (result == null) result = caseProcessingInstructionHolder(xmlElement);
                if (result == null) result = caseXmlEntityHolder(xmlElement);
                if (result == null) result = caseXmlValueHolder(xmlElement);
                if (result == null) result = caseXmlDocumentNode(xmlElement);
                if (result == null) result = caseChoiceOption(xmlElement);
                if (result == null) result = caseXmlDocumentEntity(xmlElement);
                if (result == null) result = caseXmlBuildable(xmlElement);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ATTRIBUTE: {
                final XmlAttribute xmlAttribute = (XmlAttribute)theEObject;
                Object result = caseXmlAttribute(xmlAttribute);
                if (result == null) result = caseXmlDocumentNode(xmlAttribute);
                if (result == null) result = caseXmlValueHolder(xmlAttribute);
                if (result == null) result = caseXmlDocumentEntity(xmlAttribute);
                if (result == null) result = caseXmlBuildable(xmlAttribute);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_DOCUMENT_NODE: {
                final XmlDocumentNode xmlDocumentNode = (XmlDocumentNode)theEObject;
                Object result = caseXmlDocumentNode(xmlDocumentNode);
                if (result == null) result = caseXmlDocumentEntity(xmlDocumentNode);
                if (result == null) result = caseXmlBuildable(xmlDocumentNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ROOT: {
                final XmlRoot xmlRoot = (XmlRoot)theEObject;
                Object result = caseXmlRoot(xmlRoot);
                if (result == null) result = caseXmlElement(xmlRoot);
                if (result == null) result = caseXmlBaseElement(xmlRoot);
                if (result == null) result = caseXmlCommentHolder(xmlRoot);
                if (result == null) result = caseProcessingInstructionHolder(xmlRoot);
                if (result == null) result = caseXmlEntityHolder(xmlRoot);
                if (result == null) result = caseXmlValueHolder(xmlRoot);
                if (result == null) result = caseXmlDocumentNode(xmlRoot);
                if (result == null) result = caseChoiceOption(xmlRoot);
                if (result == null) result = caseXmlDocumentEntity(xmlRoot);
                if (result == null) result = caseXmlBuildable(xmlRoot);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_COMMENT: {
                final XmlComment xmlComment = (XmlComment)theEObject;
                Object result = caseXmlComment(xmlComment);
                if (result == null) result = caseXmlDocumentEntity(xmlComment);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_NAMESPACE: {
                final XmlNamespace xmlNamespace = (XmlNamespace)theEObject;
                Object result = caseXmlNamespace(xmlNamespace);
                if (result == null) result = caseXmlDocumentEntity(xmlNamespace);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_CONTAINER_NODE: {
                final XmlContainerNode xmlContainerNode = (XmlContainerNode)theEObject;
                Object result = caseXmlContainerNode(xmlContainerNode);
                if (result == null) result = caseXmlHolderEntity(xmlContainerNode);
                if (result == null) result = caseXmlDocumentEntity(xmlContainerNode);
                if (result == null) result = caseXmlEntityHolder(xmlContainerNode);
                if (result == null) result = caseChoiceOption(xmlContainerNode);
                if (result == null) result = caseXmlBuildable(xmlContainerNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_SEQUENCE: {
                final XmlSequence xmlSequence = (XmlSequence)theEObject;
                Object result = caseXmlSequence(xmlSequence);
                if (result == null) result = caseXmlContainerNode(xmlSequence);
                if (result == null) result = caseXmlDocumentEntity(xmlSequence);
                if (result == null) result = caseXmlEntityHolder(xmlSequence);
                if (result == null) result = caseChoiceOption(xmlSequence);
                if (result == null) result = caseXmlBuildable(xmlSequence);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ALL: {
                final XmlAll xmlAll = (XmlAll)theEObject;
                Object result = caseXmlAll(xmlAll);
                if (result == null) result = caseXmlContainerNode(xmlAll);
                if (result == null) result = caseXmlDocumentEntity(xmlAll);
                if (result == null) result = caseXmlEntityHolder(xmlAll);
                if (result == null) result = caseChoiceOption(xmlAll);
                if (result == null) result = caseXmlBuildable(xmlAll);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_CHOICE: {
                final XmlChoice xmlChoice = (XmlChoice)theEObject;
                Object result = caseXmlChoice(xmlChoice);
                if (result == null) result = caseXmlContainerNode(xmlChoice);
                if (result == null) result = caseXmlDocumentEntity(xmlChoice);
                if (result == null) result = caseXmlEntityHolder(xmlChoice);
                if (result == null) result = caseChoiceOption(xmlChoice);
                if (result == null) result = caseXmlBuildable(xmlChoice);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_COMMENT_HOLDER: {
                final XmlCommentHolder xmlCommentHolder = (XmlCommentHolder)theEObject;
                Object result = caseXmlCommentHolder(xmlCommentHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.PROCESSING_INSTRUCTION: {
                final ProcessingInstruction processingInstruction = (ProcessingInstruction)theEObject;
                Object result = caseProcessingInstruction(processingInstruction);
                if (result == null) result = caseXmlDocumentEntity(processingInstruction);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.PROCESSING_INSTRUCTION_HOLDER: {
                final ProcessingInstructionHolder processingInstructionHolder = (ProcessingInstructionHolder)theEObject;
                Object result = caseProcessingInstructionHolder(processingInstructionHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_FRAGMENT_USE: {
                final XmlFragmentUse xmlFragmentUse = (XmlFragmentUse)theEObject;
                Object result = caseXmlFragmentUse(xmlFragmentUse);
                if (result == null) result = caseXmlBaseElement(xmlFragmentUse);
                if (result == null) result = caseXmlDocumentNode(xmlFragmentUse);
                if (result == null) result = caseChoiceOption(xmlFragmentUse);
                if (result == null) result = caseXmlDocumentEntity(xmlFragmentUse);
                if (result == null) result = caseXmlBuildable(xmlFragmentUse);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_BASE_ELEMENT: {
                final XmlBaseElement xmlBaseElement = (XmlBaseElement)theEObject;
                Object result = caseXmlBaseElement(xmlBaseElement);
                if (result == null) result = caseXmlHolderEntity(xmlBaseElement);
                if (result == null) result = caseXmlDocumentNode(xmlBaseElement);
                if (result == null) result = caseChoiceOption(xmlBaseElement);
                if (result == null) result = caseXmlDocumentEntity(xmlBaseElement);
                if (result == null) result = caseXmlBuildable(xmlBaseElement);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ENTITY_HOLDER: {
                final XmlEntityHolder xmlEntityHolder = (XmlEntityHolder)theEObject;
                Object result = caseXmlEntityHolder(xmlEntityHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.CHOICE_OPTION: {
                final ChoiceOption choiceOption = (ChoiceOption)theEObject;
                Object result = caseChoiceOption(choiceOption);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_VALUE_HOLDER: {
                final XmlValueHolder xmlValueHolder = (XmlValueHolder)theEObject;
                Object result = caseXmlValueHolder(xmlValueHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_BUILDABLE: {
                final XmlBuildable xmlBuildable = (XmlBuildable)theEObject;
                Object result = caseXmlBuildable(xmlBuildable);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default:
                return defaultCase(theEObject);
        }
    }
}
