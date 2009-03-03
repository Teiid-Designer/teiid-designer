/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.wsdl.Attribute;
import com.metamatrix.metamodels.wsdl.AttributeOwner;
import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.BindingFault;
import com.metamatrix.metamodels.wsdl.BindingInput;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.BindingOutput;
import com.metamatrix.metamodels.wsdl.BindingParam;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.ElementOwner;
import com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented;
import com.metamatrix.metamodels.wsdl.ExtensibleDocumented;
import com.metamatrix.metamodels.wsdl.Fault;
import com.metamatrix.metamodels.wsdl.Import;
import com.metamatrix.metamodels.wsdl.Input;
import com.metamatrix.metamodels.wsdl.Message;
import com.metamatrix.metamodels.wsdl.MessagePart;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.Operation;
import com.metamatrix.metamodels.wsdl.Output;
import com.metamatrix.metamodels.wsdl.ParamType;
import com.metamatrix.metamodels.wsdl.Port;
import com.metamatrix.metamodels.wsdl.PortType;
import com.metamatrix.metamodels.wsdl.Service;
import com.metamatrix.metamodels.wsdl.Types;
import com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity;
import com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage
 * @generated
 */
public class WsdlSwitch {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached model package
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected static WsdlPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public WsdlSwitch() {
        if (modelPackage == null) {
            modelPackage = WsdlPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
	public Object doSwitch(EObject theEObject) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(EClass theEClass, EObject theEObject) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        }
        List eSuperTypes = theEClass.getESuperTypes();
        return
            eSuperTypes.isEmpty() ?
                defaultCase(theEObject) :
                doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case WsdlPackage.DEFINITIONS: {
                Definitions definitions = (Definitions)theEObject;
                Object result = caseDefinitions(definitions);
                if (result == null) result = caseWsdlNameOptionalEntity(definitions);
                if (result == null) result = caseExtensibleDocumented(definitions);
                if (result == null) result = caseDocumented(definitions);
                if (result == null) result = caseElementOwner(definitions);
                if (result == null) result = caseNamespaceDeclarationOwner(definitions);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.DOCUMENTATION: {
                Documentation documentation = (Documentation)theEObject;
                Object result = caseDocumentation(documentation);
                if (result == null) result = caseElementOwner(documentation);
                if (result == null) result = caseNamespaceDeclarationOwner(documentation);
                if (result == null) result = caseDocumented(documentation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.DOCUMENTED: {
                Documented documented = (Documented)theEObject;
                Object result = caseDocumented(documented);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.EXTENSIBLE_ATTRIBUTES_DOCUMENTED: {
                ExtensibleAttributesDocumented extensibleAttributesDocumented = (ExtensibleAttributesDocumented)theEObject;
                Object result = caseExtensibleAttributesDocumented(extensibleAttributesDocumented);
                if (result == null) result = caseDocumented(extensibleAttributesDocumented);
                if (result == null) result = caseAttributeOwner(extensibleAttributesDocumented);
                if (result == null) result = caseNamespaceDeclarationOwner(extensibleAttributesDocumented);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.ATTRIBUTE: {
                Attribute attribute = (Attribute)theEObject;
                Object result = caseAttribute(attribute);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.MESSAGE: {
                Message message = (Message)theEObject;
                Object result = caseMessage(message);
                if (result == null) result = caseWsdlNameRequiredEntity(message);
                if (result == null) result = caseExtensibleDocumented(message);
                if (result == null) result = caseDocumented(message);
                if (result == null) result = caseElementOwner(message);
                if (result == null) result = caseNamespaceDeclarationOwner(message);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.PORT_TYPE: {
                PortType portType = (PortType)theEObject;
                Object result = casePortType(portType);
                if (result == null) result = caseWsdlNameRequiredEntity(portType);
                if (result == null) result = caseExtensibleAttributesDocumented(portType);
                if (result == null) result = caseDocumented(portType);
                if (result == null) result = caseAttributeOwner(portType);
                if (result == null) result = caseNamespaceDeclarationOwner(portType);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.BINDING: {
                Binding binding = (Binding)theEObject;
                Object result = caseBinding(binding);
                if (result == null) result = caseWsdlNameRequiredEntity(binding);
                if (result == null) result = caseExtensibleDocumented(binding);
                if (result == null) result = caseDocumented(binding);
                if (result == null) result = caseElementOwner(binding);
                if (result == null) result = caseNamespaceDeclarationOwner(binding);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.SERVICE: {
                Service service = (Service)theEObject;
                Object result = caseService(service);
                if (result == null) result = caseWsdlNameRequiredEntity(service);
                if (result == null) result = caseExtensibleDocumented(service);
                if (result == null) result = caseDocumented(service);
                if (result == null) result = caseElementOwner(service);
                if (result == null) result = caseNamespaceDeclarationOwner(service);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.IMPORT: {
                Import import_ = (Import)theEObject;
                Object result = caseImport(import_);
                if (result == null) result = caseExtensibleAttributesDocumented(import_);
                if (result == null) result = caseDocumented(import_);
                if (result == null) result = caseAttributeOwner(import_);
                if (result == null) result = caseNamespaceDeclarationOwner(import_);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.PORT: {
                Port port = (Port)theEObject;
                Object result = casePort(port);
                if (result == null) result = caseWsdlNameRequiredEntity(port);
                if (result == null) result = caseExtensibleDocumented(port);
                if (result == null) result = caseDocumented(port);
                if (result == null) result = caseElementOwner(port);
                if (result == null) result = caseNamespaceDeclarationOwner(port);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.ELEMENT: {
                Element element = (Element)theEObject;
                Object result = caseElement(element);
                if (result == null) result = caseAttributeOwner(element);
                if (result == null) result = caseElementOwner(element);
                if (result == null) result = caseNamespaceDeclarationOwner(element);
                if (result == null) result = caseDocumented(element);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.ATTRIBUTE_OWNER: {
                AttributeOwner attributeOwner = (AttributeOwner)theEObject;
                Object result = caseAttributeOwner(attributeOwner);
                if (result == null) result = caseNamespaceDeclarationOwner(attributeOwner);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.ELEMENT_OWNER: {
                ElementOwner elementOwner = (ElementOwner)theEObject;
                Object result = caseElementOwner(elementOwner);
                if (result == null) result = caseNamespaceDeclarationOwner(elementOwner);
                if (result == null) result = caseDocumented(elementOwner);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.WSDL_NAME_REQUIRED_ENTITY: {
                WsdlNameRequiredEntity wsdlNameRequiredEntity = (WsdlNameRequiredEntity)theEObject;
                Object result = caseWsdlNameRequiredEntity(wsdlNameRequiredEntity);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.TYPES: {
                Types types = (Types)theEObject;
                Object result = caseTypes(types);
                if (result == null) result = caseExtensibleDocumented(types);
                if (result == null) result = caseDocumented(types);
                if (result == null) result = caseElementOwner(types);
                if (result == null) result = caseNamespaceDeclarationOwner(types);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.MESSAGE_PART: {
                MessagePart messagePart = (MessagePart)theEObject;
                Object result = caseMessagePart(messagePart);
                if (result == null) result = caseExtensibleAttributesDocumented(messagePart);
                if (result == null) result = caseWsdlNameOptionalEntity(messagePart);
                if (result == null) result = caseDocumented(messagePart);
                if (result == null) result = caseAttributeOwner(messagePart);
                if (result == null) result = caseNamespaceDeclarationOwner(messagePart);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.WSDL_NAME_OPTIONAL_ENTITY: {
                WsdlNameOptionalEntity wsdlNameOptionalEntity = (WsdlNameOptionalEntity)theEObject;
                Object result = caseWsdlNameOptionalEntity(wsdlNameOptionalEntity);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.OPERATION: {
                Operation operation = (Operation)theEObject;
                Object result = caseOperation(operation);
                if (result == null) result = caseWsdlNameRequiredEntity(operation);
                if (result == null) result = caseExtensibleDocumented(operation);
                if (result == null) result = caseDocumented(operation);
                if (result == null) result = caseElementOwner(operation);
                if (result == null) result = caseNamespaceDeclarationOwner(operation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.INPUT: {
                Input input = (Input)theEObject;
                Object result = caseInput(input);
                if (result == null) result = caseParamType(input);
                if (result == null) result = caseWsdlNameOptionalEntity(input);
                if (result == null) result = caseExtensibleAttributesDocumented(input);
                if (result == null) result = caseDocumented(input);
                if (result == null) result = caseAttributeOwner(input);
                if (result == null) result = caseNamespaceDeclarationOwner(input);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.OUTPUT: {
                Output output = (Output)theEObject;
                Object result = caseOutput(output);
                if (result == null) result = caseParamType(output);
                if (result == null) result = caseWsdlNameOptionalEntity(output);
                if (result == null) result = caseExtensibleAttributesDocumented(output);
                if (result == null) result = caseDocumented(output);
                if (result == null) result = caseAttributeOwner(output);
                if (result == null) result = caseNamespaceDeclarationOwner(output);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.FAULT: {
                Fault fault = (Fault)theEObject;
                Object result = caseFault(fault);
                if (result == null) result = caseWsdlNameRequiredEntity(fault);
                if (result == null) result = caseExtensibleAttributesDocumented(fault);
                if (result == null) result = caseDocumented(fault);
                if (result == null) result = caseAttributeOwner(fault);
                if (result == null) result = caseNamespaceDeclarationOwner(fault);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.PARAM_TYPE: {
                ParamType paramType = (ParamType)theEObject;
                Object result = caseParamType(paramType);
                if (result == null) result = caseWsdlNameOptionalEntity(paramType);
                if (result == null) result = caseExtensibleAttributesDocumented(paramType);
                if (result == null) result = caseDocumented(paramType);
                if (result == null) result = caseAttributeOwner(paramType);
                if (result == null) result = caseNamespaceDeclarationOwner(paramType);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.EXTENSIBLE_DOCUMENTED: {
                ExtensibleDocumented extensibleDocumented = (ExtensibleDocumented)theEObject;
                Object result = caseExtensibleDocumented(extensibleDocumented);
                if (result == null) result = caseDocumented(extensibleDocumented);
                if (result == null) result = caseElementOwner(extensibleDocumented);
                if (result == null) result = caseNamespaceDeclarationOwner(extensibleDocumented);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.BINDING_OPERATION: {
                BindingOperation bindingOperation = (BindingOperation)theEObject;
                Object result = caseBindingOperation(bindingOperation);
                if (result == null) result = caseExtensibleDocumented(bindingOperation);
                if (result == null) result = caseWsdlNameRequiredEntity(bindingOperation);
                if (result == null) result = caseDocumented(bindingOperation);
                if (result == null) result = caseElementOwner(bindingOperation);
                if (result == null) result = caseNamespaceDeclarationOwner(bindingOperation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.BINDING_INPUT: {
                BindingInput bindingInput = (BindingInput)theEObject;
                Object result = caseBindingInput(bindingInput);
                if (result == null) result = caseBindingParam(bindingInput);
                if (result == null) result = caseExtensibleDocumented(bindingInput);
                if (result == null) result = caseMimeElementOwner(bindingInput);
                if (result == null) result = caseWsdlNameOptionalEntity(bindingInput);
                if (result == null) result = caseDocumented(bindingInput);
                if (result == null) result = caseElementOwner(bindingInput);
                if (result == null) result = caseNamespaceDeclarationOwner(bindingInput);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.BINDING_OUTPUT: {
                BindingOutput bindingOutput = (BindingOutput)theEObject;
                Object result = caseBindingOutput(bindingOutput);
                if (result == null) result = caseBindingParam(bindingOutput);
                if (result == null) result = caseExtensibleDocumented(bindingOutput);
                if (result == null) result = caseMimeElementOwner(bindingOutput);
                if (result == null) result = caseWsdlNameOptionalEntity(bindingOutput);
                if (result == null) result = caseDocumented(bindingOutput);
                if (result == null) result = caseElementOwner(bindingOutput);
                if (result == null) result = caseNamespaceDeclarationOwner(bindingOutput);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.BINDING_FAULT: {
                BindingFault bindingFault = (BindingFault)theEObject;
                Object result = caseBindingFault(bindingFault);
                if (result == null) result = caseExtensibleDocumented(bindingFault);
                if (result == null) result = caseWsdlNameRequiredEntity(bindingFault);
                if (result == null) result = caseDocumented(bindingFault);
                if (result == null) result = caseElementOwner(bindingFault);
                if (result == null) result = caseNamespaceDeclarationOwner(bindingFault);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.BINDING_PARAM: {
                BindingParam bindingParam = (BindingParam)theEObject;
                Object result = caseBindingParam(bindingParam);
                if (result == null) result = caseExtensibleDocumented(bindingParam);
                if (result == null) result = caseMimeElementOwner(bindingParam);
                if (result == null) result = caseWsdlNameOptionalEntity(bindingParam);
                if (result == null) result = caseDocumented(bindingParam);
                if (result == null) result = caseElementOwner(bindingParam);
                if (result == null) result = caseNamespaceDeclarationOwner(bindingParam);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.NAMESPACE_DECLARATION: {
                NamespaceDeclaration namespaceDeclaration = (NamespaceDeclaration)theEObject;
                Object result = caseNamespaceDeclaration(namespaceDeclaration);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WsdlPackage.NAMESPACE_DECLARATION_OWNER: {
                NamespaceDeclarationOwner namespaceDeclarationOwner = (NamespaceDeclarationOwner)theEObject;
                Object result = caseNamespaceDeclarationOwner(namespaceDeclarationOwner);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Definitions</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Definitions</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseDefinitions(Definitions object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Documentation</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Documentation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseDocumentation(Documentation object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Documented</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Documented</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseDocumented(Documented object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Extensible Attributes Documented</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Extensible Attributes Documented</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseExtensibleAttributesDocumented(ExtensibleAttributesDocumented object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Attribute</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Attribute</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseAttribute(Attribute object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Message</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Message</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseMessage(Message object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Port Type</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Port Type</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object casePortType(PortType object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Binding</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Binding</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseBinding(Binding object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Service</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Service</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseService(Service object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Import</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Import</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseImport(Import object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Port</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Port</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object casePort(Port object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Element</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseElement(Element object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Attribute Owner</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Attribute Owner</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseAttributeOwner(AttributeOwner object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Element Owner</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Element Owner</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseElementOwner(ElementOwner object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Name Required Entity</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Name Required Entity</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseWsdlNameRequiredEntity(WsdlNameRequiredEntity object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Types</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Types</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseTypes(Types object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Message Part</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Message Part</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseMessagePart(MessagePart object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Name Optional Entity</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Name Optional Entity</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseWsdlNameOptionalEntity(WsdlNameOptionalEntity object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Operation</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Operation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseOperation(Operation object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Input</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Input</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseInput(Input object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Output</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Output</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseOutput(Output object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Fault</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Fault</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseFault(Fault object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Param Type</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Param Type</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseParamType(ParamType object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Extensible Documented</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Extensible Documented</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseExtensibleDocumented(ExtensibleDocumented object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Binding Operation</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Binding Operation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseBindingOperation(BindingOperation object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Binding Input</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Binding Input</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseBindingInput(BindingInput object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Binding Output</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Binding Output</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseBindingOutput(BindingOutput object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Binding Fault</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Binding Fault</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseBindingFault(BindingFault object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Binding Param</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Binding Param</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseBindingParam(BindingParam object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Namespace Declaration</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Namespace Declaration</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseNamespaceDeclaration(NamespaceDeclaration object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Namespace Declaration Owner</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Namespace Declaration Owner</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseNamespaceDeclarationOwner(NamespaceDeclarationOwner object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Element Owner</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Element Owner</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseMimeElementOwner(MimeElementOwner object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
	public Object defaultCase(EObject object) {
        return null;
    }

} //WsdlSwitch
