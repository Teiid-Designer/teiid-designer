/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.util;

import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Property;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;

/**
 * This naming strategy wraps another and ensures that the resulting name is considered a valid
 * Relational name.
 */
public class ValidatingRelationalObjectNameStrategy implements RelationalObjectNamingStrategy {

    private final RelationalObjectNamingStrategy delegate;
    private final StringNameValidator validator;

    /**
     * Construct an instance of ValidatingRelationalObjectNameStrategy.
     * @param strategy the actual strategy that will produce the name; may not be null
     */
    public ValidatingRelationalObjectNameStrategy(final RelationalObjectNamingStrategy strategy) {
        super();
        ArgCheck.isNotNull(strategy);
        this.delegate = strategy;
        this.validator = new StringNameValidator();
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForPrimaryKey(org.eclipse.uml2.Class, com.metamatrix.metamodels.relational.BaseTable)
     */
    public String getNameForPrimaryKey(Classifier klass, BaseTable table) {
        final String origName = this.delegate.getNameForPrimaryKey(klass,table);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForClassBaseTable(org.eclipse.uml2.Class)
     */
    public String getNameForClassBaseTable(Classifier klass) {
        final String origName = this.delegate.getNameForClassBaseTable(klass);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForDatatypeTable(org.eclipse.uml2.Property, org.eclipse.uml2.DataType)
     */
    public String getNameForDatatypeTable(Property referringProperty, DataType datatype) {
        final String origName = this.delegate.getNameForDatatypeTable(referringProperty,datatype);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForDatatypeValueColumn(org.eclipse.uml2.Property, org.eclipse.uml2.DataType)
     */
    public String getNameForDatatypeValueColumn(Property referringProperty, DataType datatype, final String namingSuffix) {
        final String origName = this.delegate.getNameForDatatypeValueColumn(referringProperty,datatype, namingSuffix);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForCopiedPKColumn(com.metamatrix.metamodels.relational.BaseTable, int)
     */
    public String getNameForCopiedPKColumn(BaseTable fromTable, int i) {
        final String origName = this.delegate.getNameForCopiedPKColumn(fromTable,i);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForForeignKey(com.metamatrix.metamodels.relational.BaseTable)
     */
    public String getNameForForeignKey(BaseTable toTable) {
        final String origName = this.delegate.getNameForForeignKey(toTable);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForForeignKey(org.eclipse.uml2.Property, com.metamatrix.metamodels.relational.BaseTable)
     */
    public String getNameForForeignKey(Property property, BaseTable table) {
        final String origName = this.delegate.getNameForForeignKey(property,table);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForUnidirectionalIntersectTable(com.metamatrix.metamodels.relational.BaseTable, com.metamatrix.metamodels.relational.BaseTable, org.eclipse.uml2.Property)
     */
    public String getNameForUnidirectionalIntersectTable( BaseTable fromTable, BaseTable toTable,
                                                          Property fromProperty) {
        final String origName = this.delegate.getNameForUnidirectionalIntersectTable(fromTable,toTable,fromProperty);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForColumn(org.eclipse.uml2.Property)
     */
    public String getNameForColumn(Property property) {
        final String origName = this.delegate.getNameForColumn(property);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForArtificialPKColumn(int)
     */
    public String getNameForArtificialPKColumn(int i) {
        final String origName = this.delegate.getNameForArtificialPKColumn(i);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForIntersectTableRepresentingAssociation(org.eclipse.uml2.Association)
     */
    public String getNameForIntersectTableRepresentingAssociation(Association association) {
        final String origName = this.delegate.getNameForIntersectTableRepresentingAssociation(association);
        if ( origName == null ) {
            return null;
        }
        final String newName = this.validator.createValidName(origName,true);
        return newName != null ? newName : origName;
    }

}
