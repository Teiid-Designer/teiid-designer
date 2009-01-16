/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

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
import com.metamatrix.metamodels.xml.XmlContainerHolder;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlElementHolder;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlFragmentUse;
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
        if (modelPackage == null) {
            modelPackage = XmlDocumentPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch( EObject theEObject ) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch( EClass theEClass,
                               EObject theEObject ) {
        if (theEClass.eContainer() == modelPackage) return doSwitch(theEClass.getClassifierID(), theEObject);
        List eSuperTypes = theEClass.getESuperTypes();
        return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch( int classifierID,
                               EObject theEObject ) {
        switch (classifierID) {
            case XmlDocumentPackage.XML_FRAGMENT: {
                XmlFragment xmlFragment = (XmlFragment)theEObject;
                Object result = caseXmlFragment(xmlFragment);
                if (result == null) result = caseXmlDocumentEntity(xmlFragment);
                if (result == null) result = caseXmlCommentHolder(xmlFragment);
                if (result == null) result = caseProcessingInstructionHolder(xmlFragment);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_DOCUMENT: {
                XmlDocument xmlDocument = (XmlDocument)theEObject;
                Object result = caseXmlDocument(xmlDocument);
                if (result == null) result = caseXmlFragment(xmlDocument);
                if (result == null) result = caseXmlDocumentEntity(xmlDocument);
                if (result == null) result = caseXmlCommentHolder(xmlDocument);
                if (result == null) result = caseProcessingInstructionHolder(xmlDocument);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_DOCUMENT_ENTITY: {
                XmlDocumentEntity xmlDocumentEntity = (XmlDocumentEntity)theEObject;
                Object result = caseXmlDocumentEntity(xmlDocumentEntity);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ELEMENT: {
                XmlElement xmlElement = (XmlElement)theEObject;
                Object result = caseXmlElement(xmlElement);
                if (result == null) result = caseXmlBaseElement(xmlElement);
                if (result == null) result = caseXmlCommentHolder(xmlElement);
                if (result == null) result = caseProcessingInstructionHolder(xmlElement);
                if (result == null) result = caseXmlElementHolder(xmlElement);
                if (result == null) result = caseXmlContainerHolder(xmlElement);
                if (result == null) result = caseXmlValueHolder(xmlElement);
                if (result == null) result = caseXmlDocumentNode(xmlElement);
                if (result == null) result = caseChoiceOption(xmlElement);
                if (result == null) result = caseXmlDocumentEntity(xmlElement);
                if (result == null) result = caseXmlBuildable(xmlElement);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ATTRIBUTE: {
                XmlAttribute xmlAttribute = (XmlAttribute)theEObject;
                Object result = caseXmlAttribute(xmlAttribute);
                if (result == null) result = caseXmlDocumentNode(xmlAttribute);
                if (result == null) result = caseXmlValueHolder(xmlAttribute);
                if (result == null) result = caseXmlDocumentEntity(xmlAttribute);
                if (result == null) result = caseXmlBuildable(xmlAttribute);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_DOCUMENT_NODE: {
                XmlDocumentNode xmlDocumentNode = (XmlDocumentNode)theEObject;
                Object result = caseXmlDocumentNode(xmlDocumentNode);
                if (result == null) result = caseXmlDocumentEntity(xmlDocumentNode);
                if (result == null) result = caseXmlBuildable(xmlDocumentNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ROOT: {
                XmlRoot xmlRoot = (XmlRoot)theEObject;
                Object result = caseXmlRoot(xmlRoot);
                if (result == null) result = caseXmlElement(xmlRoot);
                if (result == null) result = caseXmlBaseElement(xmlRoot);
                if (result == null) result = caseXmlCommentHolder(xmlRoot);
                if (result == null) result = caseProcessingInstructionHolder(xmlRoot);
                if (result == null) result = caseXmlElementHolder(xmlRoot);
                if (result == null) result = caseXmlContainerHolder(xmlRoot);
                if (result == null) result = caseXmlValueHolder(xmlRoot);
                if (result == null) result = caseXmlDocumentNode(xmlRoot);
                if (result == null) result = caseChoiceOption(xmlRoot);
                if (result == null) result = caseXmlDocumentEntity(xmlRoot);
                if (result == null) result = caseXmlBuildable(xmlRoot);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_COMMENT: {
                XmlComment xmlComment = (XmlComment)theEObject;
                Object result = caseXmlComment(xmlComment);
                if (result == null) result = caseXmlDocumentEntity(xmlComment);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_NAMESPACE: {
                XmlNamespace xmlNamespace = (XmlNamespace)theEObject;
                Object result = caseXmlNamespace(xmlNamespace);
                if (result == null) result = caseXmlDocumentEntity(xmlNamespace);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_CONTAINER_NODE: {
                XmlContainerNode xmlContainerNode = (XmlContainerNode)theEObject;
                Object result = caseXmlContainerNode(xmlContainerNode);
                if (result == null) result = caseXmlDocumentEntity(xmlContainerNode);
                if (result == null) result = caseXmlElementHolder(xmlContainerNode);
                if (result == null) result = caseXmlContainerHolder(xmlContainerNode);
                if (result == null) result = caseChoiceOption(xmlContainerNode);
                if (result == null) result = caseXmlBuildable(xmlContainerNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_SEQUENCE: {
                XmlSequence xmlSequence = (XmlSequence)theEObject;
                Object result = caseXmlSequence(xmlSequence);
                if (result == null) result = caseXmlContainerNode(xmlSequence);
                if (result == null) result = caseXmlDocumentEntity(xmlSequence);
                if (result == null) result = caseXmlElementHolder(xmlSequence);
                if (result == null) result = caseXmlContainerHolder(xmlSequence);
                if (result == null) result = caseChoiceOption(xmlSequence);
                if (result == null) result = caseXmlBuildable(xmlSequence);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ALL: {
                XmlAll xmlAll = (XmlAll)theEObject;
                Object result = caseXmlAll(xmlAll);
                if (result == null) result = caseXmlContainerNode(xmlAll);
                if (result == null) result = caseXmlDocumentEntity(xmlAll);
                if (result == null) result = caseXmlElementHolder(xmlAll);
                if (result == null) result = caseXmlContainerHolder(xmlAll);
                if (result == null) result = caseChoiceOption(xmlAll);
                if (result == null) result = caseXmlBuildable(xmlAll);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_CHOICE: {
                XmlChoice xmlChoice = (XmlChoice)theEObject;
                Object result = caseXmlChoice(xmlChoice);
                if (result == null) result = caseXmlContainerNode(xmlChoice);
                if (result == null) result = caseXmlDocumentEntity(xmlChoice);
                if (result == null) result = caseXmlElementHolder(xmlChoice);
                if (result == null) result = caseXmlContainerHolder(xmlChoice);
                if (result == null) result = caseChoiceOption(xmlChoice);
                if (result == null) result = caseXmlBuildable(xmlChoice);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_COMMENT_HOLDER: {
                XmlCommentHolder xmlCommentHolder = (XmlCommentHolder)theEObject;
                Object result = caseXmlCommentHolder(xmlCommentHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.PROCESSING_INSTRUCTION: {
                ProcessingInstruction processingInstruction = (ProcessingInstruction)theEObject;
                Object result = caseProcessingInstruction(processingInstruction);
                if (result == null) result = caseXmlDocumentEntity(processingInstruction);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.PROCESSING_INSTRUCTION_HOLDER: {
                ProcessingInstructionHolder processingInstructionHolder = (ProcessingInstructionHolder)theEObject;
                Object result = caseProcessingInstructionHolder(processingInstructionHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_ELEMENT_HOLDER: {
                XmlElementHolder xmlElementHolder = (XmlElementHolder)theEObject;
                Object result = caseXmlElementHolder(xmlElementHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_FRAGMENT_USE: {
                XmlFragmentUse xmlFragmentUse = (XmlFragmentUse)theEObject;
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
                XmlBaseElement xmlBaseElement = (XmlBaseElement)theEObject;
                Object result = caseXmlBaseElement(xmlBaseElement);
                if (result == null) result = caseXmlDocumentNode(xmlBaseElement);
                if (result == null) result = caseChoiceOption(xmlBaseElement);
                if (result == null) result = caseXmlDocumentEntity(xmlBaseElement);
                if (result == null) result = caseXmlBuildable(xmlBaseElement);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_CONTAINER_HOLDER: {
                XmlContainerHolder xmlContainerHolder = (XmlContainerHolder)theEObject;
                Object result = caseXmlContainerHolder(xmlContainerHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.CHOICE_OPTION: {
                ChoiceOption choiceOption = (ChoiceOption)theEObject;
                Object result = caseChoiceOption(choiceOption);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_VALUE_HOLDER: {
                XmlValueHolder xmlValueHolder = (XmlValueHolder)theEObject;
                Object result = caseXmlValueHolder(xmlValueHolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XmlDocumentPackage.XML_BUILDABLE: {
                XmlBuildable xmlBuildable = (XmlBuildable)theEObject;
                Object result = caseXmlBuildable(xmlBuildable);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default:
                return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Fragment</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Fragment</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlFragment( XmlFragment object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Document</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Document</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlDocument( XmlDocument object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Entity</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Entity</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlDocumentEntity( XmlDocumentEntity object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Element</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlElement( XmlElement object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Attribute</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Attribute</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlAttribute( XmlAttribute object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Node</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlDocumentNode( XmlDocumentNode object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Root</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlRoot( XmlRoot object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Comment</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Comment</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlComment( XmlComment object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Namespace</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Namespace</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlNamespace( XmlNamespace object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Container Node</em>'. <!-- begin-user-doc -->
     * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Container Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlContainerNode( XmlContainerNode object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Sequence</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Sequence</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlSequence( XmlSequence object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml All</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml All</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlAll( XmlAll object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Choice</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Choice</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlChoice( XmlChoice object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Comment Holder</em>'. <!-- begin-user-doc -->
     * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Comment Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlCommentHolder( XmlCommentHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Processing Instruction</em>'. <!-- begin-user-doc -->
     * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Processing Instruction</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProcessingInstruction( ProcessingInstruction object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Processing Instruction Holder</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
     * end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Processing Instruction Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProcessingInstructionHolder( ProcessingInstructionHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Element Holder</em>'. <!-- begin-user-doc -->
     * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Element Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlElementHolder( XmlElementHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Fragment Use</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Fragment Use</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlFragmentUse( XmlFragmentUse object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Base Element</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Base Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlBaseElement( XmlBaseElement object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Container Holder</em>'. <!-- begin-user-doc -->
     * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Container Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlContainerHolder( XmlContainerHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Choice Option</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Choice Option</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseChoiceOption( ChoiceOption object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Value Holder</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Value Holder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlValueHolder( XmlValueHolder object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Xml Buildable</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Xml Buildable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXmlBuildable( XmlBuildable object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch, but this is the last case anyway. <!--
     * end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase( EObject object ) {
        return null;
    }

} // XmlDocumentSwitch
