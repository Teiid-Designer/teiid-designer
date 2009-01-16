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

import java.util.Iterator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XPackage</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class XPackageImpl extends EPackageImpl implements XPackage {
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
    protected XPackageImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return ExtensionPackage.Literals.XPACKAGE;
	}

    /**
     * <!-- begin-user-doc -->
     * Finds the first {@link XClass} that has the supplied class as it's
     * {@link XClass#getExtendedClass() extended class}.
     * @return the XClass that has extends the supplied class, or null if there
     * is no extension for the supplied class.
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public XClass findXClass(EClass extendedClass) {
        if ( extendedClass == null ) {
             return null;
         }
         final Iterator iter = this.getEClassifiers().iterator();
         while (iter.hasNext()) {
             final EClassifier eClassifier = (EClassifier)iter.next();
             if ( eClassifier instanceof XClass ) {
                 final XClass xclass = (XClass)eClassifier;
                 final EClass xedClass = xclass.getExtendedClass();
                 if ( extendedClass.equals(xedClass) ) {
                     return xclass;
                 }
             }
         }
         return null;
    }

    /**
	 * <!-- begin-user-doc -->
     * Finds the first {@link XClass} that has the supplied class as it's
     * {@link XClass#getExtendedClass() extended class}.
     * @return the XClass that has extends the supplied class, or null if there
     * is no extension for the supplied class.
     * <!-- end-user-doc -->
	 * @generated
	 */
    public XClass findXClassGen(EClass extendedClass) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

} //XPackageImpl
