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

import com.metamatrix.metamodels.xml.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.xml.ChoiceOption;
import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.ProcessingInstructionHolder;
import com.metamatrix.metamodels.xml.XmlAll;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlBaseElement;
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
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage
 * @generated
 */
public class XmlDocumentAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static XmlDocumentPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlDocumentAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = XmlDocumentPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlDocumentSwitch modelSwitch =
        new XmlDocumentSwitch() {
            @Override
            public Object caseXmlFragment(XmlFragment object) {
                return createXmlFragmentAdapter();
            }
            @Override
            public Object caseXmlDocument(XmlDocument object) {
                return createXmlDocumentAdapter();
            }
            @Override
            public Object caseXmlDocumentEntity(XmlDocumentEntity object) {
                return createXmlDocumentEntityAdapter();
            }
            @Override
            public Object caseXmlElement(XmlElement object) {
                return createXmlElementAdapter();
            }
            @Override
            public Object caseXmlAttribute(XmlAttribute object) {
                return createXmlAttributeAdapter();
            }
            @Override
            public Object caseXmlDocumentNode(XmlDocumentNode object) {
                return createXmlDocumentNodeAdapter();
            }
            @Override
            public Object caseXmlRoot(XmlRoot object) {
                return createXmlRootAdapter();
            }
            @Override
            public Object caseXmlComment(XmlComment object) {
                return createXmlCommentAdapter();
            }
            @Override
            public Object caseXmlNamespace(XmlNamespace object) {
                return createXmlNamespaceAdapter();
            }
            @Override
            public Object caseXmlContainerNode(XmlContainerNode object) {
                return createXmlContainerNodeAdapter();
            }
            @Override
            public Object caseXmlSequence(XmlSequence object) {
                return createXmlSequenceAdapter();
            }
            @Override
            public Object caseXmlAll(XmlAll object) {
                return createXmlAllAdapter();
            }
            @Override
            public Object caseXmlChoice(XmlChoice object) {
                return createXmlChoiceAdapter();
            }
            @Override
            public Object caseXmlCommentHolder(XmlCommentHolder object) {
                return createXmlCommentHolderAdapter();
            }
            @Override
            public Object caseProcessingInstruction(ProcessingInstruction object) {
                return createProcessingInstructionAdapter();
            }
            @Override
            public Object caseProcessingInstructionHolder(ProcessingInstructionHolder object) {
                return createProcessingInstructionHolderAdapter();
            }
            @Override
            public Object caseXmlElementHolder(XmlElementHolder object) {
                return createXmlElementHolderAdapter();
            }
            @Override
            public Object caseXmlFragmentUse(XmlFragmentUse object) {
                return createXmlFragmentUseAdapter();
            }
            @Override
            public Object caseXmlBaseElement(XmlBaseElement object) {
                return createXmlBaseElementAdapter();
            }
            @Override
            public Object caseXmlContainerHolder(XmlContainerHolder object) {
                return createXmlContainerHolderAdapter();
            }
            @Override
            public Object caseChoiceOption(ChoiceOption object) {
                return createChoiceOptionAdapter();
            }
            @Override
            public Object caseXmlValueHolder(XmlValueHolder object) {
                return createXmlValueHolderAdapter();
            }
            @Override
            public Object caseXmlBuildable(XmlBuildable object) {
                return createXmlBuildableAdapter();
            }
            @Override
            public Object defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return (Adapter)modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlFragment <em>Xml Fragment</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlFragment
     * @generated
     */
    public Adapter createXmlFragmentAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlDocument <em>Xml Document</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlDocument
     * @generated
     */
    public Adapter createXmlDocumentAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlDocumentEntity <em>Entity</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlDocumentEntity
     * @generated
     */
    public Adapter createXmlDocumentEntityAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlElement <em>Xml Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlElement
     * @generated
     */
    public Adapter createXmlElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlAttribute <em>Xml Attribute</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlAttribute
     * @generated
     */
    public Adapter createXmlAttributeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlDocumentNode <em>Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode
     * @generated
     */
    public Adapter createXmlDocumentNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlRoot <em>Xml Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlRoot
     * @generated
     */
    public Adapter createXmlRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlComment <em>Xml Comment</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlComment
     * @generated
     */
    public Adapter createXmlCommentAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlNamespace <em>Xml Namespace</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlNamespace
     * @generated
     */
    public Adapter createXmlNamespaceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlContainerNode <em>Xml Container Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlContainerNode
     * @generated
     */
    public Adapter createXmlContainerNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlSequence <em>Xml Sequence</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlSequence
     * @generated
     */
    public Adapter createXmlSequenceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlAll <em>Xml All</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlAll
     * @generated
     */
    public Adapter createXmlAllAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlChoice <em>Xml Choice</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlChoice
     * @generated
     */
    public Adapter createXmlChoiceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlCommentHolder <em>Xml Comment Holder</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlCommentHolder
     * @generated
     */
    public Adapter createXmlCommentHolderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.ProcessingInstruction <em>Processing Instruction</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.ProcessingInstruction
     * @generated
     */
    public Adapter createProcessingInstructionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.ProcessingInstructionHolder <em>Processing Instruction Holder</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.ProcessingInstructionHolder
     * @generated
     */
    public Adapter createProcessingInstructionHolderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlElementHolder <em>Xml Element Holder</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlElementHolder
     * @generated
     */
    public Adapter createXmlElementHolderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlFragmentUse <em>Xml Fragment Use</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlFragmentUse
     * @generated
     */
    public Adapter createXmlFragmentUseAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlBaseElement <em>Xml Base Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlBaseElement
     * @generated
     */
    public Adapter createXmlBaseElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlContainerHolder <em>Xml Container Holder</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlContainerHolder
     * @generated
     */
    public Adapter createXmlContainerHolderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.ChoiceOption <em>Choice Option</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.ChoiceOption
     * @generated
     */
    public Adapter createChoiceOptionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlValueHolder <em>Xml Value Holder</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlValueHolder
     * @generated
     */
    public Adapter createXmlValueHolderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.xml.XmlBuildable <em>Xml Buildable</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.xml.XmlBuildable
     * @generated
     */
    public Adapter createXmlBuildableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //XmlDocumentAdapterFactory
