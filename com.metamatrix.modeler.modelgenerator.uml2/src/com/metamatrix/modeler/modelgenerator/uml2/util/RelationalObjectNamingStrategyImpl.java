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

package com.metamatrix.modeler.modelgenerator.uml2.util;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2ModelGeneratorPlugin;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;

/**
 * RelationalObjectNamingStrategyImpl
 */
public class RelationalObjectNamingStrategyImpl implements RelationalObjectNamingStrategy {

    public static final String NAME_DELIMITER = "_"; //$NON-NLS-1$
    public static final String FOREIGN_KEY_NAME_PREFIX = "FK" + NAME_DELIMITER; //$NON-NLS-1$
    public static final String PRIMARY_KEY_NAME_PREFIX = "PK" + NAME_DELIMITER; //$NON-NLS-1$

    private final Uml2RelationalOptions options;

    /**
     * Construct an instance of RelationalObjectNamingStrategyImpl.
     */
    public RelationalObjectNamingStrategyImpl( final Uml2RelationalOptions options ) {
        this.options = options;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForPrimaryKey(org.eclipse.uml2.Class,
     *      com.metamatrix.metamodels.relational.BaseTable)
     */
    public String getNameForPrimaryKey( final Classifier klass,
                                        final BaseTable table ) {
        return PRIMARY_KEY_NAME_PREFIX + table.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForClassBaseTable(org.eclipse.uml2.Class)
     */
    public String getNameForClassBaseTable( final Classifier klass ) {

        if (klass == null) {
            throw new IllegalArgumentException(
                                               Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectNamingStrategyImpl.The_Class_instance_passed_to_the_getNameForBaseTable_null_1")); //$NON-NLS-1$
        }

        final String klassName = klass.getName();

        final String significantPackageName = getSignificantPackageName(klassName, klass.getPackage());

        return generatePackageQualifiedName(significantPackageName, klass.getName());

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForDatatypeTable(org.eclipse.uml2.Property,
     *      org.eclipse.uml2.DataType)
     */
    public String getNameForDatatypeTable( final Property referringProperty,
                                           final DataType datatype ) {
        return referringProperty.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForDatatypeValueColumn(org.eclipse.uml2.Property,
     *      org.eclipse.uml2.DataType)
     */
    public String getNameForDatatypeValueColumn( final Property referringProperty,
                                                 final DataType datatype,
                                                 final String nameSuffix ) {
        return referringProperty.getName() + nameSuffix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForCopiedPKColumn(com.metamatrix.metamodels.relational.BaseTable,
     *      int)
     */
    public String getNameForCopiedPKColumn( final BaseTable fromTable,
                                            final int i ) {
        return PRIMARY_KEY_NAME_PREFIX + i + NAME_DELIMITER + fromTable.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForForeignKey(com.metamatrix.metamodels.relational.BaseTable)
     */
    public String getNameForForeignKey( final BaseTable toTable ) {
        return FOREIGN_KEY_NAME_PREFIX + toTable.getName();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForUnidirectionalIntersectTable(com.metamatrix.metamodels.relational.BaseTable,
     *      org.eclipse.uml2.Property)
     */
    public String getNameForUnidirectionalIntersectTable( final BaseTable fromTable,
                                                          final BaseTable toTable,
                                                          final Property fromProperty ) {
        if (fromProperty == null) {
            throw new IllegalArgumentException(
                                               Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectNamingStrategyImpl.The_from_property_passed_into_the_etNameForUnidirectionalIntersectTable_null_1")); //$NON-NLS-1$
        }

        final Association association = fromProperty.getAssociation();
        String intersectTableName = null;
        if (association != null) {
            final String associationName = association.getName();
            intersectTableName = generatePackageQualifiedName(getSignificantPackageName(associationName, association.getPackage()),
                                                              associationName);
        } else {
            final String fromPropertyName = fromProperty.getName();
            final EObject container = fromProperty.eContainer();
            if (container instanceof Classifier) {
                final Classifier classifier = (Classifier)container;
                intersectTableName = generatePackageQualifiedName(getSignificantPackageName(fromPropertyName,
                                                                                            classifier.getPackage()),
                                                                  fromPropertyName);
            } else {
                intersectTableName = fromPropertyName;
            }
        }

        /*
         * if the Association does not have a name and the property does not have a name, then we make the name be the names of
         * the two tables the intersect table involves with a delimiter in between.
         */
        if (intersectTableName == null) {
            final String compositeName = fromTable.getName() + NAME_DELIMITER + toTable.getName();
            intersectTableName = generatePackageQualifiedName(getSignificantPackageName(compositeName, association.getPackage()),
                                                              compositeName);
        }

        return intersectTableName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForColumn(org.eclipse.uml2.Property)
     */
    public String getNameForColumn( final Property property ) {
        return property.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForArtificialPKColumn(int)
     */
    public String getNameForArtificialPKColumn( final int i ) {
        return options.getKeyColumnBaseName() + NAME_DELIMITER + i;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForBidirectionalIntersectTable(com.metamatrix.metamodels.relational.BaseTable,
     *      com.metamatrix.metamodels.relational.BaseTable, org.eclipse.uml2.Type, org.eclipse.uml2.Type)
     */
    public String getNameForIntersectTableRepresentingAssociation( final Association association ) {
        if (association == null) {
            throw new IllegalArgumentException(
                                               Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectNamingStrategyImpl.argument_passed_to_the_getNameForIntersectTableRepresentingAssociation_null_1")); //$NON-NLS-1$
        }

        String associationName = association.getName();
        if (associationName == null || associationName.trim().length() == 0) {
            // The association has no name, so try to build from the
            // Property names and/or the Classifier names...
            final StringBuffer sb = new StringBuffer();
            final List properties = association.getMembers();
            int numProperties = properties.size();
            final Iterator iter = properties.iterator();
            while (iter.hasNext()) {
                final Object member = iter.next();
                if (member instanceof Property) {
                    final Property prop = (Property)member;
                    final String propName = prop.getName();
                    if (propName != null && propName.trim().length() != 0) {
                        sb.append(propName);
                    } else {
                        final Class umlClass = prop.getClass_();
                        if (umlClass != null) {
                            final String umlClassName = umlClass.getName();
                            if (umlClassName != null) {
                                sb.append(umlClassName);
                            }
                        }
                    }
                    --numProperties;
                    if (numProperties != 0) {
                        sb.append("_"); //$NON-NLS-1$
                    }
                }
            }
            associationName = sb.toString();
        }
        final String packageName = getSignificantPackageName(associationName, association.getPackage());
        return generatePackageQualifiedName(packageName, associationName);

    }

    protected String generatePackageQualifiedName( final String packageName,
                                                   final String objectName ) {
        if (packageName == null || packageName.trim().length() == 0) {
            if (objectName == null || objectName.trim().length() == 0) {
                return null;
            }
            return objectName;
        }
        if (objectName == null || objectName.trim().length() == 0) {

            return packageName;
        }
        return objectName + NAME_DELIMITER + packageName;
    }

    protected String getSignificantPackageName( final String objectName,
                                                final Package pkg ) {
        String name = null;
        if (pkg != null) {
            final String pkgName = pkg.getName();
            if (pkgName != null) {
                /*
                 * per the rules we received from customer if this package has the same name as the Class it is containing, we
                 * move up a level until we find a package that does not have the same name and contains some classifiers.
                 */
                if (pkgName.trim().length() == 0 || pkgName.equals(objectName) || pkg.getOwnedMembers().size() == 0) {
                    final Element parentPackage = pkg.getOwner();
                    if (parentPackage != null && parentPackage.eClass().getClassifierID() == UMLPackage.PACKAGE) {

                        name = getSignificantPackageName(objectName, (Package)parentPackage);
                    }

                } else {
                    name = pkgName;
                }
            }
        }
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForForeignKey(org.eclipse.uml2.Property)
     */
    public String getNameForForeignKey( Property property,
                                        BaseTable toTable ) {
        final String name = property.getName();
        if (name != null && name.length() != 0) {
            return name;
        }
        Association association = property.getAssociation();
        if (association != null) {

            String assocName = association.getName();
            if (assocName != null && assocName.length() != 0) {
                return association.getName();
            }
            return getNameForForeignKey(toTable);
        }
        return getNameForForeignKey(toTable);
    }

}
