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

import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Property;

import com.metamatrix.metamodels.relational.BaseTable;

/**
 * NullRelationalObjectNamingStrategy
 */
public class NullRelationalObjectNamingStrategy implements RelationalObjectNamingStrategy {




    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForClassBaseTable(org.eclipse.uml2.Class)
     */
    public String getNameForClassBaseTable(Classifier klass) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForCopiedPKColumn(com.metamatrix.metamodels.relational.BaseTable, int)
     */
    public String getNameForCopiedPKColumn(BaseTable fromTable, int i) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForDatatypeTable(org.eclipse.uml2.Property, org.eclipse.uml2.DataType)
     */
    public String getNameForDatatypeTable(Property referringProperty, DataType datatype) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForDatatypeValueColumn(org.eclipse.uml2.Property, org.eclipse.uml2.DataType)
     */
    public String getNameForDatatypeValueColumn(Property referringProperty, DataType datatype, String namingSuffix) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForForeignKey(com.metamatrix.metamodels.relational.BaseTable)
     */
    public String getNameForForeignKey(BaseTable fromTable) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForPrimaryKey(org.eclipse.uml2.Class, com.metamatrix.metamodels.relational.BaseTable)
     */
    public String getNameForPrimaryKey(Classifier klass, BaseTable table) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForUnidirectionalIntersectTable(com.metamatrix.metamodels.relational.BaseTable, org.eclipse.uml2.Property)
     */
    public String getNameForUnidirectionalIntersectTable(BaseTable fromTable, BaseTable toTable, Property property) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForColumn(org.eclipse.uml2.Property)
     */
    public String getNameForColumn(Property property) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForArtificialPKColumn(int)
     */
    public String getNameForArtificialPKColumn(int i) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForBidirectionalIntersectTable(com.metamatrix.metamodels.relational.BaseTable, com.metamatrix.metamodels.relational.BaseTable, org.eclipse.uml2.Type, org.eclipse.uml2.Type)
     */
    public String getNameForIntersectTableRepresentingAssociation(
        Association association) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy#getNameForForeignKey(org.eclipse.uml2.Property, com.metamatrix.metamodels.relational.BaseTable)
     */
    public String getNameForForeignKey(Property property, BaseTable table) {
        return null;
    }

}
