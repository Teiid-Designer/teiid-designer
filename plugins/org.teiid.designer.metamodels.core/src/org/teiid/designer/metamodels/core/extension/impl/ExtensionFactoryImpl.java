/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.core.extension.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.teiid.designer.metamodels.core.extension.ExtensionFactory;
import org.teiid.designer.metamodels.core.extension.ExtensionPackage;
import org.teiid.designer.metamodels.core.extension.XAttribute;
import org.teiid.designer.metamodels.core.extension.XClass;
import org.teiid.designer.metamodels.core.extension.XEnum;
import org.teiid.designer.metamodels.core.extension.XEnumLiteral;
import org.teiid.designer.metamodels.core.extension.XPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 *
 * @since 8.0
 */
public class ExtensionFactoryImpl extends EFactoryImpl implements ExtensionFactory {

    /**
     * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static ExtensionFactory init() { // NO_UCD
        try {
            ExtensionFactory theExtensionFactory = (ExtensionFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.metamatrix.com/metamodels/Extension"); //$NON-NLS-1$ 
            if (theExtensionFactory != null) {
                return theExtensionFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ExtensionFactoryImpl();
    }

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ExtensionFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EObject create( EClass eClass ) {
        switch (eClass.getClassifierID()) {
            case ExtensionPackage.XCLASS:
                return createXClass();
            case ExtensionPackage.XPACKAGE:
                return createXPackage();
            case ExtensionPackage.XATTRIBUTE:
                return createXAttribute();
            case ExtensionPackage.XENUM:
                return createXEnum();
            case ExtensionPackage.XENUM_LITERAL:
                return createXEnumLiteral();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XClass createXClass() {
        XClassImpl xClass = new XClassImpl();
        return xClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XPackage createXPackage() {
        XPackageImpl xPackage = new XPackageImpl();
        return xPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XAttribute createXAttribute() {
        XAttributeImpl xAttribute = new XAttributeImpl();
        return xAttribute;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XEnum createXEnum() {
        XEnumImpl xEnum = new XEnumImpl();
        return xEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XEnumLiteral createXEnumLiteral() {
        XEnumLiteralImpl xEnumLiteral = new XEnumLiteralImpl();
        return xEnumLiteral;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ExtensionPackage getExtensionPackage() {
        return (ExtensionPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ExtensionPackage getPackage() { // NO_UCD
        return ExtensionPackage.eINSTANCE;
    }

} // ExtensionFactoryImpl
