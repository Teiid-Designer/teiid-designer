/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.AbstractEnumerator;
import org.teiid.core.designer.util.ModelType.Type;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Model Type</b></em>', and utility methods
 * for working with them. <!-- end-user-doc -->
 * 
 * @see org.teiid.designer.metamodels.core.CorePackage#getModelType()
 * @model
 * @generated
 *
 * @since 8.0
 */
public final class ModelType extends AbstractEnumerator {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$ // NO_UCD

    /**
     * The '<em><b>PHYSICAL</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #PHYSICAL_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int PHYSICAL = Type.PHYSICAL.getValue();

    /**
     * The '<em><b>VIRTUAL</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #VIRTUAL_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int VIRTUAL = Type.VIRTUAL.getValue();

    /**
     * The '<em><b>TYPE</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #TYPE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int TYPE = Type.TYPE.getValue();

    /**
     * The '<em><b>VDB ARCHIVE</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #VDB_ARCHIVE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int VDB_ARCHIVE = Type.VDB_ARCHIVE.getValue();

    /**
     * The '<em><b>UNKNOWN</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #UNKNOWN_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int UNKNOWN = Type.UNKNOWN.getValue();

    /**
     * The '<em><b>FUNCTION</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #FUNCTION_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int FUNCTION = Type.FUNCTION.getValue();

    /**
     * The '<em><b>CONFIGURATION</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #CONFIGURATION_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int CONFIGURATION = Type.CONFIGURATION.getValue();

    /**
     * The '<em><b>METAMODEL</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #METAMODEL_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int METAMODEL = Type.METAMODEL.getValue();

    /**
     * The '<em><b>EXTENSION</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #EXTENSION_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int EXTENSION = Type.EXTENSION.getValue();

    /**
     * The '<em><b>LOGICAL</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #LOGICAL_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int LOGICAL = Type.LOGICAL.getValue();

    /**
     * The '<em><b>MATERIALIZATION</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #MATERIALIZATION_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int MATERIALIZATION = Type.MATERIALIZATION.getValue();

    /**
     * The '<em><b>PHYSICAL</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>PHYSICAL</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType PHYSICAL_LITERAL = new ModelType(Type.PHYSICAL);

    /**
     * The '<em><b>VIRTUAL</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>VIRTUAL</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    public static final ModelType VIRTUAL_LITERAL = new ModelType(Type.VIRTUAL);

    /**
     * The '<em><b>TYPE</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>TYPE</b></em>' literal object isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType TYPE_LITERAL = new ModelType(Type.TYPE);

    /**
     * The '<em><b>VDB ARCHIVE</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>VDB ARCHIVE</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType VDB_ARCHIVE_LITERAL = new ModelType(Type.VDB_ARCHIVE);

    /**
     * The '<em><b>UNKNOWN</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>UNKNOWN</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType UNKNOWN_LITERAL = new ModelType(Type.UNKNOWN);

    /**
     * The '<em><b>FUNCTION</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>FUNCTION</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType FUNCTION_LITERAL = new ModelType(Type.FUNCTION);

    /**
     * The '<em><b>CONFIGURATION</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CONFIGURATION</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType CONFIGURATION_LITERAL = new ModelType(Type.CONFIGURATION);

    /**
     * The '<em><b>METAMODEL</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>METAMODEL</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType METAMODEL_LITERAL = new ModelType(Type.METAMODEL);

    /**
     * The '<em><b>EXTENSION</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>EXTENSION</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType EXTENSION_LITERAL = new ModelType(Type.EXTENSION);

    /**
     * The '<em><b>LOGICAL</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>LOGICAL</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType LOGICAL_LITERAL = new ModelType(Type.LOGICAL);

    /**
     * The '<em><b>MATERIALIZATION</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>MATERIALIZATION</b></em>' literal object isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     *
     * @generated
     * @ordered
     */
    public static final ModelType MATERIALIZATION_LITERAL = new ModelType(Type.MATERIALIZATION);

    /**
     * An array of all the '<em><b>Model Type</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private static final ModelType[] VALUES_ARRAY = new ModelType[] {PHYSICAL_LITERAL, VIRTUAL_LITERAL, TYPE_LITERAL,
        VDB_ARCHIVE_LITERAL, UNKNOWN_LITERAL, FUNCTION_LITERAL, CONFIGURATION_LITERAL, METAMODEL_LITERAL, EXTENSION_LITERAL,
        LOGICAL_LITERAL, MATERIALIZATION_LITERAL,};

    /**
     * A public read-only list of all the '<em><b>Model Type</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final List<ModelType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY)); // NO_UCD

    /**
     * Returns the '<em><b>Model Type</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param name
     * @return ModelType with the given name
     * 
     * @generated
     */
    public static ModelType get( String name ) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ModelType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Model Type</b></em>' literal with the specified value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value
     * @return ModelType with the given index
     * 
     * @generated
     */
    public static ModelType get( int value ) { // NO_UCD
        Type type = Type.getType(value);
        switch (type) {
            case PHYSICAL:
                return PHYSICAL_LITERAL;
            case VIRTUAL:
                return VIRTUAL_LITERAL;
            case TYPE:
                return TYPE_LITERAL;
            case VDB_ARCHIVE:
                return VDB_ARCHIVE_LITERAL;
            case UNKNOWN:
                return UNKNOWN_LITERAL;
            case FUNCTION:
                return FUNCTION_LITERAL;
            case CONFIGURATION:
                return CONFIGURATION_LITERAL;
            case METAMODEL:
                return METAMODEL_LITERAL;
            case EXTENSION:
                return EXTENSION_LITERAL;
            case LOGICAL:
                return LOGICAL_LITERAL;
            case MATERIALIZATION:
                return MATERIALIZATION_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param type
     */
    public ModelType(Type type) {
        super(type.getValue(), type.getName());
    }

    /**
     * Returns the display name of the enumerator.
     * 
     * @return the display name. <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public final String getDisplayName() {
        String label = CoreMetamodelPlugin.getPluginResourceLocator().getString("_UI_ModelType_" + this.getName()); //$NON-NLS-1$
        return label == null || label.length() == 0 ? this.getName() : label;
    }

} // ModelType
