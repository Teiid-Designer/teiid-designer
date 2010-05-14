/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.modelextension.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EcorePackage;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XEnumLiteral;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.ObjectExtension;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.XMLRequestResponseExtensionManager;

public abstract class XMLRequestResponseExtensionManagerImpl extends BaseXMLRelationalExtensionManagerImpl
    implements XMLRequestResponseExtensionManager {

    protected Map multipleValueEnumValues;
    protected Map columnRoleEnumValues;
    protected Map multipleValueEnumLookup;
    protected Map columnRoleEnumLookup;

    static final String COLUMN_INPUT_PARAM = "IsInputParameter"; //$NON-NLS-1$
    static final String COLUMN_PARENT_ATTRIBUTE = "AttributeOfParent"; //$NON-NLS-1$
    static final String COLUMN_DATA_ATTRIBUTE = "DataAttributeName"; //$NON-NLS-1$
    static final String COLUMN_MULTIPLE_VALUES = "MultipleValues"; //$NON-NLS-1$
    static final String COLUMN_REQUIRED_VALUE = "RequiredValue"; //$NON-NLS-1$
    static final String COLUMN_ALLOW_EMPTY_INPUT = "AllowEmptyInputElement"; //$NON-NLS-1$
    static final String COLUMN_XPATH_FOR_INPUT = "XPathForInputParameter"; //$NON-NLS-1$
    static final String COLUMN_ROLE = "Role"; //$NON-NLS-1$
    static final String TABLE_XPATH_ROOT_INPUT = "XPathRootForInput"; //$NON-NLS-1$

    public static final Integer DATA_ROLE = 0;
    public static final Integer RESPONSE_OUT_ROLE = 1;
    public static final Integer RESPONSE_IN_ROLE = 2;
    public static final Integer LOCATION_ROLE = 3;

    public static final Integer NO_MULTIPLE_VALUES = 0;
    public static final Integer COMMA_DELIMTED_MULTIPLE_VALUES = 1;
    public static final Integer MULTI_ELEMENT_MULTIPLE_VALUES = 2;

    private EEnum multipleValuesEnum;
    private EEnum columnRoleEnum;

    private XAttribute xPathRootForInputTableAttribute;
    private XAttribute isInputParameterColumnAttribute;
    private XAttribute attributeOfParentColumnAttribute;
    private XAttribute dataAttributeNameColumnAttribute;
    private XAttribute multipleValuesColumnAttribute;
    private XAttribute requiredValueColumnAttribute;
    private XAttribute allowEmptyInputElementColumnAttribute;
    private XAttribute xPathForInputParameterColumnAttribute;
    private XAttribute roleColumnAttribute;

    protected XMLRequestResponseExtensionManagerImpl() {
        multipleValueEnumLookup = new HashMap();
        columnRoleEnumLookup = new HashMap();

        multipleValueEnumValues = new HashMap();
        multipleValueEnumValues.put("No", NO_MULTIPLE_VALUES); //$NON-NLS-1$
        multipleValueEnumValues.put("CommaDelimited", COMMA_DELIMTED_MULTIPLE_VALUES); //$NON-NLS-1$
        multipleValueEnumValues.put("MultiElement", MULTI_ELEMENT_MULTIPLE_VALUES); //$NON-NLS-1$

        columnRoleEnumValues = new HashMap();
        columnRoleEnumValues.put("Data", DATA_ROLE); //$NON-NLS-1$
        columnRoleEnumValues.put("ResponseOut", RESPONSE_OUT_ROLE); //$NON-NLS-1$
        columnRoleEnumValues.put("ResponseIn", RESPONSE_IN_ROLE); //$NON-NLS-1$
        columnRoleEnumValues.put("Location", LOCATION_ROLE); //$NON-NLS-1$
    }

    @Override
    public void assignClassifier( EClassifier classifier ) {
        if (classifier.getName().equals("MultipleValuesEnum")) { //$NON-NLS-1$
            multipleValuesEnum = (EEnum)classifier;
            loadLiterals(multipleValuesEnum.getELiterals(), multipleValueEnumLookup);
        } else if (classifier.getName().equals("ColumnRoleEnum")) { //$NON-NLS-1$
            columnRoleEnum = (EEnum)classifier;
            loadLiterals(columnRoleEnum.getELiterals(), columnRoleEnumLookup);
        }
    }

    private void loadLiterals( EList literals,
                               Map lookup ) {
        Iterator iter = literals.iterator();
        EEnumLiteral lit;
        while (iter.hasNext()) {
            lit = (EEnumLiteral)iter.next();
            lookup.put(lit.getValue(), lit);
        }
    }

    @Override
    public void createTableExtensions( ExtensionFactory xFactory,
                                       XClass table ) {
        super.createTableExtensions(xFactory, table);
        xPathRootForInputTableAttribute = xFactory.createXAttribute();
        xPathRootForInputTableAttribute.setName(TABLE_XPATH_ROOT_INPUT);
        xPathRootForInputTableAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        table.getEStructuralFeatures().add(xPathRootForInputTableAttribute);
    }

    @Override
    public void createColumnExtensions( ExtensionFactory xFactory,
                                        XClass column ) {
        isInputParameterColumnAttribute = xFactory.createXAttribute();
        isInputParameterColumnAttribute.setName(COLUMN_INPUT_PARAM);
        isInputParameterColumnAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
        column.getEStructuralFeatures().add(isInputParameterColumnAttribute);

        attributeOfParentColumnAttribute = xFactory.createXAttribute();
        attributeOfParentColumnAttribute.setName(COLUMN_PARENT_ATTRIBUTE);
        attributeOfParentColumnAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
        column.getEStructuralFeatures().add(attributeOfParentColumnAttribute);

        dataAttributeNameColumnAttribute = xFactory.createXAttribute();
        dataAttributeNameColumnAttribute.setName(COLUMN_DATA_ATTRIBUTE);
        dataAttributeNameColumnAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        column.getEStructuralFeatures().add(dataAttributeNameColumnAttribute);

        multipleValuesColumnAttribute = xFactory.createXAttribute();
        multipleValuesColumnAttribute.setName(COLUMN_MULTIPLE_VALUES);
        multipleValuesColumnAttribute.setEType(multipleValuesEnum);
        column.getEStructuralFeatures().add(multipleValuesColumnAttribute);

        requiredValueColumnAttribute = xFactory.createXAttribute();
        requiredValueColumnAttribute.setName(COLUMN_REQUIRED_VALUE);
        requiredValueColumnAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
        requiredValueColumnAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
        column.getEStructuralFeatures().add(requiredValueColumnAttribute);

        allowEmptyInputElementColumnAttribute = xFactory.createXAttribute();
        allowEmptyInputElementColumnAttribute.setName(COLUMN_ALLOW_EMPTY_INPUT);
        allowEmptyInputElementColumnAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
        allowEmptyInputElementColumnAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
        column.getEStructuralFeatures().add(allowEmptyInputElementColumnAttribute);

        xPathForInputParameterColumnAttribute = xFactory.createXAttribute();
        xPathForInputParameterColumnAttribute.setName(COLUMN_XPATH_FOR_INPUT);
        xPathForInputParameterColumnAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        column.getEStructuralFeatures().add(xPathForInputParameterColumnAttribute);

        roleColumnAttribute = xFactory.createXAttribute();
        roleColumnAttribute.setName(COLUMN_ROLE);
        roleColumnAttribute.setEType(columnRoleEnum);
        roleColumnAttribute.setDefaultValue(columnRoleEnumLookup.get(DATA_ROLE));
        column.getEStructuralFeatures().add(roleColumnAttribute);

    }

    @Override
    public void createEnums( ExtensionFactory xFactory ) {
        multipleValuesEnum = xFactory.createXEnum();
        multipleValuesEnum.setName("MultipleValuesEnum"); //$NON-NLS-1$
        getPackage().getEClassifiers().add(multipleValuesEnum);
        createLiterals(xFactory, multipleValuesEnum, multipleValueEnumValues, multipleValueEnumLookup);

        columnRoleEnum = xFactory.createXEnum();
        columnRoleEnum.setName("ColumnRoleEnum"); //$NON-NLS-1$
        getPackage().getEClassifiers().add(columnRoleEnum);
        createLiterals(xFactory, columnRoleEnum, columnRoleEnumValues, columnRoleEnumLookup);
    }

    private void createLiterals( ExtensionFactory xFactory,
                                 EEnum xEnum,
                                 Map enumValues,
                                 Map lookup ) {
        Iterator values;
        values = enumValues.entrySet().iterator();
        while (values.hasNext()) {
            Map.Entry value = (Entry)values.next();
            XEnumLiteral lit = xFactory.createXEnumLiteral();
            lit.setName((String)value.getKey());
            lit.setValue(((Integer)value.getValue()).intValue());
            xEnum.getELiterals().add(lit);
            lookup.put(value.getValue(), lit);
        }
    }

    @Override
    public void assignAttribute( XAttribute attribute ) {
        super.assignAttribute(attribute);
        if (attribute.getName().equals(COLUMN_ALLOW_EMPTY_INPUT)) {
            allowEmptyInputElementColumnAttribute = attribute;
        } else if (attribute.getName().equals(COLUMN_DATA_ATTRIBUTE)) {
            dataAttributeNameColumnAttribute = attribute;
        } else if (attribute.getName().equals(COLUMN_INPUT_PARAM)) {
            isInputParameterColumnAttribute = attribute;
        } else if (attribute.getName().equals(COLUMN_MULTIPLE_VALUES)) {
            multipleValuesColumnAttribute = attribute;
        } else if (attribute.getName().equals(COLUMN_PARENT_ATTRIBUTE)) {
            attributeOfParentColumnAttribute = attribute;
        } else if (attribute.getName().equals(COLUMN_REQUIRED_VALUE)) {
            requiredValueColumnAttribute = attribute;
        } else if (attribute.getName().equals(COLUMN_ROLE)) {
            roleColumnAttribute = attribute;
        } else if (attribute.getName().equals(COLUMN_XPATH_FOR_INPUT)) {
            xPathForInputParameterColumnAttribute = attribute;
        } else if (attribute.getName().equals(TABLE_XPATH_ROOT_INPUT)) {
            xPathRootForInputTableAttribute = attribute;
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.wsdl.modelextension.IXMLRequestResponseExtensionManager#setColumnRoleAttribute(com.metamatrix.metamodels.relational.Column, java.lang.Integer)
     */
    public void setColumnRoleAttribute( Column column,
                                        Integer role ) {
        ObjectExtension extension = new ObjectExtension(column, theColumnXClass, ModelerCore.getModelEditor());
        extension.eDynamicSet(roleColumnAttribute, columnRoleEnumLookup.get(role));
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.wsdl.modelextension.IXMLRequestResponseExtensionManager#setColumnInputParamAttribute(com.metamatrix.metamodels.relational.Column, java.lang.Boolean)
     */
    public void setColumnInputParamAttribute( Column column,
                                              Boolean input ) {
        ObjectExtension extension = new ObjectExtension(column, theColumnXClass, ModelerCore.getModelEditor());
        extension.eDynamicSet(isInputParameterColumnAttribute, input);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.wsdl.modelextension.IXMLRequestResponseExtensionManager#setXPathRootForInputAttribute(com.metamatrix.metamodels.relational.RelationalEntity, java.lang.String)
     */
    public void setXPathRootForInputAttribute( RelationalEntity table,
                                               String xrfi_attribute_value ) {
        ObjectExtension extension = new ObjectExtension(table, theTableXClass, ModelerCore.getModelEditor());
        extension.eDynamicSet(xPathRootForInputTableAttribute, xrfi_attribute_value);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.wsdl.modelextension.IXMLRequestResponseExtensionManager#setColumnXPathForInput(com.metamatrix.metamodels.relational.Column, java.lang.String)
     */
    public void setColumnXPathForInput( Column relColumn,
                                        String xpath ) {
        ObjectExtension objectExtension2 = new ObjectExtension(relColumn, theColumnXClass, ModelerCore.getModelEditor());
        objectExtension2.eDynamicSet(xPathForInputParameterColumnAttribute, xpath);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.wsdl.modelextension.IXMLRequestResponseExtensionManager#setAllowEmptyInputElement(com.metamatrix.metamodels.relational.Column, boolean)
     */
    public void setAllowEmptyInputElement( Column relCol,
                                           boolean request ) {
        ObjectExtension objectExtension2 = new ObjectExtension(relCol, theColumnXClass, ModelerCore.getModelEditor());
        objectExtension2.eDynamicSet(allowEmptyInputElementColumnAttribute, Boolean.FALSE);
        ObjectExtension objExt = new ObjectExtension(relCol, theColumnXClass, ModelerCore.getModelEditor());
        objExt.eDynamicSet(requiredValueColumnAttribute, Boolean.valueOf(request));
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.wsdl.modelextension.IXMLRequestResponseExtensionManager#setMultipleValue(com.metamatrix.metamodels.relational.Column, java.lang.Integer)
     */
    public void setMultipleValue( Column relColumn,
                                  Integer value ) {
        ObjectExtension objectExtension = new ObjectExtension(relColumn, theColumnXClass, ModelerCore.getModelEditor());
        objectExtension.eDynamicSet(multipleValuesColumnAttribute, multipleValueEnumLookup.get(value));
    }

}
