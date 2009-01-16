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

package com.metamatrix.metamodels.core.extension.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XAttribute;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XAttribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class XAttributeImpl extends EAttributeImpl implements XAttribute {
    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2000-2008 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

				/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected XAttributeImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return ExtensionPackage.Literals.XATTRIBUTE;
	}

    /** 
     * @see org.eclipse.emf.ecore.ETypedElement#setEType(org.eclipse.emf.ecore.EClassifier)
     * @since 4.2
     */
    @Override
    public void setEType(EClassifier theValue) {
        // let the delegate be regenerated since the delegate caches the type
        this.settingDelegate = null;
        
        // let the factory get reassigned each time the type changes. if you don't the default value
        // doesn't get regenerated
        this.defaultValueFactory = null;
        
        // set the type
        super.setEType(theValue);
    }

    /** 
     * @see org.eclipse.emf.ecore.EStructuralFeature#getDefaultValue()
     * @since 4.2
     * @generated NOT
     */
    @Override
    public Object getDefaultValue() {
        Object result = null;
        final EDataType type = (EDataType)getEType();
        
        // make sure the type has been set
        if (type != null) {
	        final EPackage ePackage = type.getEPackage();
	        final EFactory factory = ePackage.getEFactoryInstance();
	      
	        try {
	            // this method will throw an exception if the value can't be converted to the proper type
	            factory.createFromString(type, getDefaultValueLiteral());

	            // if value literal is valid just call super
	            result = super.getDefaultValue();
	        } catch (RuntimeException theException) {
	            // just return null if the default value literal can't be converted
	        }
        }

        return result;
    }
    
} //XAttributeImpl
