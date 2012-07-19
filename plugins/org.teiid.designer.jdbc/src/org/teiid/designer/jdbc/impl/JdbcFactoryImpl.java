/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.teiid.designer.jdbc.CaseConversion;
import org.teiid.designer.jdbc.JdbcDriver;
import org.teiid.designer.jdbc.JdbcDriverContainer;
import org.teiid.designer.jdbc.JdbcFactory;
import org.teiid.designer.jdbc.JdbcImportOptions;
import org.teiid.designer.jdbc.JdbcImportSettings;
import org.teiid.designer.jdbc.JdbcPackage;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.JdbcSourceContainer;
import org.teiid.designer.jdbc.JdbcSourceProperty;
import org.teiid.designer.jdbc.SourceNames;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 *
 * @since 8.0
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
    @Override
	public JdbcSourceProperty createJdbcSourceProperty() {
        JdbcSourcePropertyImpl jdbcSourceProperty = new JdbcSourcePropertyImpl();
        return jdbcSourceProperty;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public JdbcDriver createJdbcDriver() {
        JdbcDriverImpl jdbcDriver = new JdbcDriverImpl();
        return jdbcDriver;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public JdbcSource createJdbcSource() {
        JdbcSourceImpl jdbcSource = new JdbcSourceImpl();
        return jdbcSource;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public JdbcDriverContainer createJdbcDriverContainer() {
        JdbcDriverContainerImpl jdbcDriverContainer = new JdbcDriverContainerImpl();
        return jdbcDriverContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public JdbcSourceContainer createJdbcSourceContainer() {
        JdbcSourceContainerImpl jdbcSourceContainer = new JdbcSourceContainerImpl();
        return jdbcSourceContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public JdbcImportSettings createJdbcImportSettings() {
        JdbcImportSettingsImpl jdbcImportSettings = new JdbcImportSettingsImpl();
        return jdbcImportSettings;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public JdbcImportOptions createJdbcImportOptions() {
        JdbcImportOptionsImpl jdbcImportOptions = new JdbcImportOptionsImpl();
        return jdbcImportOptions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
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
