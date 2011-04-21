/*
 * Created on Oct 7, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.util.HashMap;
import java.util.Map;

import org.eclipse.datatools.modelbase.dbdefinition.CheckOption;
import org.eclipse.datatools.modelbase.dbdefinition.ColumnDefinition;
import org.eclipse.datatools.modelbase.dbdefinition.ConstraintDefinition;
import org.eclipse.datatools.modelbase.dbdefinition.DatabaseDefinitionFactory;
import org.eclipse.datatools.modelbase.dbdefinition.DatabaseVendorDefinition;
import org.eclipse.datatools.modelbase.dbdefinition.ParentDeleteDRIRuleType;
import org.eclipse.datatools.modelbase.dbdefinition.ParentUpdateDRIRuleType;
import org.eclipse.datatools.modelbase.dbdefinition.PredefinedDataTypeDefinition;
import org.eclipse.datatools.modelbase.sql.datatypes.PrimitiveType;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * @author hkolwalk To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class ExampleForVendorPrimitiveWrite {

    public static void main( String[] arg ) {
		URI uri = URI.createFileURI(".teiidDB.xmi"); //$NON-NLS-1$
        Resource rf = new XMIResourceImpl(uri);

        // Database vendor definitions
        DatabaseVendorDefinition databaseVendorDefinition = DatabaseDefinitionFactory.eINSTANCE.createDatabaseVendorDefinition();
		databaseVendorDefinition.setVendor("Teiid.org"); //$NON-NLS-1$
		databaseVendorDefinition.setVersion("6.2"); //$NON-NLS-1$
        databaseVendorDefinition.setViewTriggerSupported(false);
        databaseVendorDefinition.setMaximumIdentifierLength(64);
        databaseVendorDefinition.setMaximumCommentLength(64);
        databaseVendorDefinition.setSequenceSupported(false);
        databaseVendorDefinition.setMQTSupported(false);
        databaseVendorDefinition.setAliasSupported(true);

        ColumnDefinition columnDefinition = DatabaseDefinitionFactory.eINSTANCE.createColumnDefinition();
        columnDefinition.setIdentitySupported(true);
        columnDefinition.setComputedSupported(true);
        columnDefinition.setIdentityStartValueSupported(true);
        columnDefinition.setIdentityIncrementSupported(true);
        columnDefinition.setIdentityMaximumSupported(true);
        columnDefinition.setIdentityMinimumSupported(true);
        columnDefinition.setIdentityCycleSupported(true);
        databaseVendorDefinition.setColumnDefinition(columnDefinition);

        ConstraintDefinition constraintDefinition = DatabaseDefinitionFactory.eINSTANCE.createConstraintDefinition();
        constraintDefinition.setClusteredPrimaryKeySupported(true);
        constraintDefinition.setClusteredUniqueConstraintSupported(true);
        constraintDefinition.getParentDeleteDRIRuleType().add(ParentDeleteDRIRuleType.RESTRICT_LITERAL);
        constraintDefinition.getParentDeleteDRIRuleType().add(ParentDeleteDRIRuleType.CASCADE_LITERAL);
        constraintDefinition.getParentDeleteDRIRuleType().add(ParentDeleteDRIRuleType.SET_NULL_LITERAL);
        constraintDefinition.getParentDeleteDRIRuleType().add(ParentDeleteDRIRuleType.NO_ACTION_LITERAL);
        constraintDefinition.getParentUpdateDRIRuleType().add(ParentUpdateDRIRuleType.RESTRICT_LITERAL);
        constraintDefinition.getParentUpdateDRIRuleType().add(ParentUpdateDRIRuleType.NO_ACTION_LITERAL);
        constraintDefinition.getCheckOption().add(CheckOption.NONE_LITERAL);
        constraintDefinition.getCheckOption().add(CheckOption.LOCAL_LITERAL);
        constraintDefinition.getCheckOption().add(CheckOption.CASCADE_LITERAL);
        databaseVendorDefinition.setConstraintDefinition(constraintDefinition);

        // Primitive type definitions

        // CHARACTER
        PredefinedDataTypeDefinition characterDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
        characterDataTypeDefinition.setPrimitiveType(PrimitiveType.CHARACTER_LITERAL);
		characterDataTypeDefinition.getName().add("CHAR"); //$NON-NLS-1$
		characterDataTypeDefinition.getName().add("CHARACTER"); //$NON-NLS-1$
        characterDataTypeDefinition.setMaximumLength(254);
        characterDataTypeDefinition.setKeyConstraintSupported(true);
		characterDataTypeDefinition.getDefaultValueTypes().add("CURRENT_USER"); //$NON-NLS-1$
		characterDataTypeDefinition.getDefaultValueTypes().add("NULL"); //$NON-NLS-1$
        characterDataTypeDefinition.setLengthSupported(true);
        characterDataTypeDefinition.setJdbcEnumType(1);
		characterDataTypeDefinition.setJavaClassName("java.lang.String"); //$NON-NLS-1$
        databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(characterDataTypeDefinition);
		((XMIResource) rf).setID(characterDataTypeDefinition,
				PrimitiveType.CHARACTER_LITERAL + "_1"); //$NON-NLS-1$

        // DECIMAL
        PredefinedDataTypeDefinition decimalDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
        decimalDataTypeDefinition.setPrimitiveType(PrimitiveType.DECIMAL_LITERAL);
		decimalDataTypeDefinition.getName().add("DECIMAL"); //$NON-NLS-1$
		decimalDataTypeDefinition.getName().add("DEC"); //$NON-NLS-1$
        decimalDataTypeDefinition.setPrecisionSupported(true);
        decimalDataTypeDefinition.setScaleSupported(true);
        decimalDataTypeDefinition.setKeyConstraintSupported(true);
        decimalDataTypeDefinition.setIdentitySupported(true);
		decimalDataTypeDefinition.getDefaultValueTypes().add("NULL"); //$NON-NLS-1$
        decimalDataTypeDefinition.setJdbcEnumType(3);
		decimalDataTypeDefinition.setJavaClassName("java.math.BigDecimal"); //$NON-NLS-1$
        databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(decimalDataTypeDefinition);
		((XMIResource) rf).setID(decimalDataTypeDefinition,
				PrimitiveType.DECIMAL_LITERAL + "_1"); //$NON-NLS-1$

        // DOUBLE
        PredefinedDataTypeDefinition doublePrecisionDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
        doublePrecisionDataTypeDefinition.setPrimitiveType(PrimitiveType.DOUBLE_PRECISION_LITERAL);
		doublePrecisionDataTypeDefinition.getName().add("DOUBLE"); //$NON-NLS-1$
        doublePrecisionDataTypeDefinition.setKeyConstraintSupported(true);
		doublePrecisionDataTypeDefinition.getDefaultValueTypes().add("NULL"); //$NON-NLS-1$
        doublePrecisionDataTypeDefinition.setJdbcEnumType(8);
		doublePrecisionDataTypeDefinition.setJavaClassName("double"); //$NON-NLS-1$
        databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(doublePrecisionDataTypeDefinition);
		((XMIResource) rf).setID(doublePrecisionDataTypeDefinition,
				PrimitiveType.DOUBLE_PRECISION_LITERAL + "_1"); //$NON-NLS-1$

        // INTEGER
        PredefinedDataTypeDefinition integerDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
        integerDataTypeDefinition.setPrimitiveType(PrimitiveType.INTEGER_LITERAL);
		integerDataTypeDefinition.getName().add("INTEGER"); //$NON-NLS-1$
		integerDataTypeDefinition.getName().add("INT"); //$NON-NLS-1$
        integerDataTypeDefinition.setKeyConstraintSupported(true);
        integerDataTypeDefinition.setIdentitySupported(true);
		integerDataTypeDefinition.getDefaultValueTypes().add("NULL"); //$NON-NLS-1$
        integerDataTypeDefinition.setJdbcEnumType(4);
		integerDataTypeDefinition.setJavaClassName("int"); //$NON-NLS-1$
        databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(integerDataTypeDefinition);
		((XMIResource) rf).setID(integerDataTypeDefinition,
				PrimitiveType.INTEGER_LITERAL + "_1"); //$NON-NLS-1$

        // NUMERIC
        PredefinedDataTypeDefinition numericDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
        numericDataTypeDefinition.setPrimitiveType(PrimitiveType.NUMERIC_LITERAL);
		numericDataTypeDefinition.getName().add("NUMERIC"); //$NON-NLS-1$
		numericDataTypeDefinition.getName().add("NUM"); //$NON-NLS-1$
        numericDataTypeDefinition.setKeyConstraintSupported(true);
        numericDataTypeDefinition.setPrecisionSupported(true);
        numericDataTypeDefinition.setScaleSupported(true);
		numericDataTypeDefinition.getDefaultValueTypes().add("NULL"); //$NON-NLS-1$
        numericDataTypeDefinition.setJdbcEnumType(2);
		numericDataTypeDefinition.setJavaClassName("java.math.BigDecimal"); //$NON-NLS-1$
        databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(numericDataTypeDefinition);
		((XMIResource) rf).setID(numericDataTypeDefinition,
				PrimitiveType.NUMERIC_LITERAL + "_1"); //$NON-NLS-1$

        EList resourceContents = rf.getContents();
        resourceContents.add(databaseVendorDefinition);
        try {
            Map options = new HashMap();
            options.put(XMLResource.OPTION_DECLARE_XML, Boolean.TRUE);
            rf.save(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
