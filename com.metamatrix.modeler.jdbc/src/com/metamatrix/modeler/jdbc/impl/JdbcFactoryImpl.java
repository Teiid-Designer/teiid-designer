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

package com.metamatrix.modeler.jdbc.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.modeler.jdbc.CaseConversion;
import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcDriverContainer;
import com.metamatrix.modeler.jdbc.JdbcFactory;
import com.metamatrix.modeler.jdbc.JdbcImportOptions;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcPackage;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.JdbcSourceContainer;
import com.metamatrix.modeler.jdbc.JdbcSourceProperty;
import com.metamatrix.modeler.jdbc.SourceNames;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class JdbcFactoryImpl extends EFactoryImpl implements JdbcFactory {
    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcFactoryImpl() {
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
            case JdbcPackage.JDBC_SOURCE_PROPERTY:
                return createJdbcSourceProperty();
            case JdbcPackage.JDBC_DRIVER:
                return createJdbcDriver();
            case JdbcPackage.JDBC_SOURCE:
                return createJdbcSource();
            case JdbcPackage.JDBC_DRIVER_CONTAINER:
                return createJdbcDriverContainer();
            case JdbcPackage.JDBC_SOURCE_CONTAINER:
                return createJdbcSourceContainer();
            case JdbcPackage.JDBC_IMPORT_SETTINGS:
                return createJdbcImportSettings();
            case JdbcPackage.JDBC_IMPORT_OPTIONS:
                return createJdbcImportOptions();
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
    public Object createFromString( EDataType eDataType,
                                    String initialValue ) {
        switch (eDataType.getClassifierID()) {
            case JdbcPackage.CASE_CONVERSION: {
                CaseConversion result = CaseConversion.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case JdbcPackage.SOURCE_NAMES: {
                SourceNames result = SourceNames.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String convertToString( EDataType eDataType,
                                   Object instanceValue ) {
        switch (eDataType.getClassifierID()) {
            case JdbcPackage.CASE_CONVERSION:
                return instanceValue == null ? null : instanceValue.toString();
            case JdbcPackage.SOURCE_NAMES:
                return instanceValue == null ? null : instanceValue.toString();
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcSourceProperty createJdbcSourceProperty() {
        JdbcSourcePropertyImpl jdbcSourceProperty = new JdbcSourcePropertyImpl();
        return jdbcSourceProperty;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcDriver createJdbcDriver() {
        JdbcDriverImpl jdbcDriver = new JdbcDriverImpl();
        return jdbcDriver;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcSource createJdbcSource() {
        JdbcSourceImpl jdbcSource = new JdbcSourceImpl();
        return jdbcSource;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcDriverContainer createJdbcDriverContainer() {
        JdbcDriverContainerImpl jdbcDriverContainer = new JdbcDriverContainerImpl();
        return jdbcDriverContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcSourceContainer createJdbcSourceContainer() {
        JdbcSourceContainerImpl jdbcSourceContainer = new JdbcSourceContainerImpl();
        return jdbcSourceContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcImportSettings createJdbcImportSettings() {
        JdbcImportSettingsImpl jdbcImportSettings = new JdbcImportSettingsImpl();
        return jdbcImportSettings;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcImportOptions createJdbcImportOptions() {
        JdbcImportOptionsImpl jdbcImportOptions = new JdbcImportOptionsImpl();
        return jdbcImportOptions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public JdbcPackage getJdbcPackage() {
        return (JdbcPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static JdbcPackage getPackage() {
        return JdbcPackage.eINSTANCE;
    }

} // JdbcFactoryImpl
