/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.processor;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.modelgenerator.GeneratorOptions;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2ModelGeneratorPlugin;

/**
 * Uml2RelationalOptions
 */
public final class Uml2RelationalOptions implements GeneratorOptions {

    protected static class AbstractEnumeration {
        private final int value;
        private final String name;
        protected AbstractEnumeration( final int value, final String name ) {
            this.value=value;
            this.name=name;
        }
        public int getValue() {
            return this.value;
        }
        public String getName() {
            return this.name;
        }
        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.value;
        }
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public static class PackageUsage extends AbstractEnumeration {
        private static final String IGNORE_LABEL = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalOptions.PackageUsage_Label_Ignore"); //$NON-NLS-1$
        private static final String FLATTEN_LABEL = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalOptions.PackageUsage_Label_Flatten"); //$NON-NLS-1$

        public static final int IGNORE = 0;
        public static final int FLATTEN = 1;
        public static final PackageUsage IGNORE_LITERAL = new PackageUsage(IGNORE,IGNORE_LABEL);
        public static final PackageUsage FLATTEN_LITERAL = new PackageUsage(FLATTEN,FLATTEN_LABEL);

        private PackageUsage( final int value, final String name ) {
            super(value,name);
        }
    }
    
    /**
     * These are valid values for the reachability Constraint option.
     */
    public static final int GENERATE_ALL_REACHABLE_RECURSIVE = 0;
    public static final int IGNORE_REACHABLE_NOT_IN_SELECTION = 1;
    
    public static final PackageUsage DEFAULT_PACKAGE_USAGE = PackageUsage.IGNORE_LITERAL;
    
    
    /**
     * This List will hold Strings that denote that any Class which has a stereotype that 'contains' one
     * of these Strings (ignoring case) will not be processed and put into the output Virtual Relational
     * Model.
     */
    private List classStereotypesToIgnore;
    
    private PackageUsage packageUsage = DEFAULT_PACKAGE_USAGE;
    
    private int numberOfKeyColumns;
    private EObject typeOfKeyColumns;
    private String keyColumnBaseName;
    private int keyColumnLength;
    private List primaryKeyStereotypeNames;
    private EObject defaultRelationalColumnType;
    private int reachabilityConstraint;
    private List autoincrementStereotypeNames;
    private List readOnlyStereoTypeNames;
    private int generatedStringTypeColumnDefaultLength;
    private Map columnCustomPropsMap;
    private Map tableCustomPropsMap;
    
    /**
     * Construct an instance of Uml2RelationalOptions.
     * 
     */
    public Uml2RelationalOptions() {
        super();
    }

    /**
     * This method is used to set the parameters that will drive how Package objects in the input UML Model 
     * will be translated into the output Relational Model.
     * 
     * @return the PackageUsage Object that defines the options for generating Relational objects for the 
     * input Package UML Objects.
     */
    public PackageUsage getPackageUsage() {
        return this.packageUsage;
    }
    
    public void setPackageUsage( PackageUsage usage ) {
        ArgCheck.isNotNull(usage);
        this.packageUsage = usage;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.GeneratorOptions#validate()
     */
    public IStatus validate() {
        final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalOptions.The_options_are_valid"); //$NON-NLS-1$
        return new Status(IStatus.OK,Uml2ModelGeneratorPlugin.PLUGIN_ID,0,msg,null);
    }

    /**
     * This method will return the 'base' name for columns that are generated in the output Virtual Relational
     * Model that represent the primary key.  This 'base' name will only be used if there are no Properties
     * of the input Class object that are determined to be part of the Primary Key.  The columns that are 
     * generated with this name will be considered 'artificial' columns as they have no matching property
     * in the input UML Model from which they are generated.  They are added to the output table for the
     * sole purpose of havning a key on the table so that relationships can be properly established
     * with the table.
     * 
     * @return the Base name of the artificial Primary Key Columns in the output Relational Model.
     */
    public String getKeyColumnBaseName() {
        return keyColumnBaseName;
    }

    /**
     * This method will return the number of 'artificial' Primary Key columns that will be generated in
     * thet output Virtual Relationa Model in the case where there are no Properties on a Class that can be 
     * determined to repreesent the Primary Key.
     * @return The number of artificial Primary Key Columns that should be generated in the output Relational Model.
     */
    public int getNumberOfKeyColumns() {
        return numberOfKeyColumns;
    }

    /**
     * This method will return a List of Strings that will indicate via stereotype or constraint on a Property
     * in the input UML model that the Column that will be generated from the Property should be considered
     * part of the Primary Key for the output Table.
     * 
     * @return a List of Strings that indicate the Property is part of the Primary Key for the input Class.
     */
    public List getPrimaryKeyStereotypeNames() {
        return primaryKeyStereotypeNames;
    }

    /**
     * This method will return an EObject representing the intended datatype of the 'artificial' key columns
     * that are generated for input UML Class objects that do not have an otherwise designated Primary Key.
     * 
     * @return a datatype for the 'artificial' key columns in the output Relational Model.
     */
    public EObject getTypeOfKeyColumns() {
        return typeOfKeyColumns;
    }

    /**
     * This method will return the length value to use for the 'artificial' key columns
     * 
     * @return the 'artificial' key column length value
     */
    public int getKeyColumnLength() {
        return keyColumnLength;
    }

    /**
     * @param string
     */
    public void setKeyColumnBaseName(String keyColumnBaseName) {
        this.keyColumnBaseName = keyColumnBaseName;
    }

    /**
     * @param i
     */
    public void setNumberOfKeyColumns(int numberOfKeyColumns) {
        this.numberOfKeyColumns = numberOfKeyColumns;
    }

    /**
     * @param string
     */
    public void setPrimaryKeyStereotypeNames(List primaryKeyStereotypeNames) {
        this.primaryKeyStereotypeNames =primaryKeyStereotypeNames;
    }

    /**
     * @param object
     */
    public void setTypeOfKeyColumns(EObject typeOfKeyColumns) {
        this.typeOfKeyColumns = typeOfKeyColumns;
    }
    
    /**
     * @param length the length to use for generated artifical keys
     */
    public void setKeyColumnLength(int length) {
        this.keyColumnLength = length;
    }
    
    public void setColumnCustomPropsMap(Map customPropsMap) {
        this.columnCustomPropsMap = customPropsMap;
    }
    
    public void setTableCustomPropsMap(Map customPropsMap) {
        this.tableCustomPropsMap = customPropsMap;
    }
    public Map getColumnCustomPropsMap() {
        return this.columnCustomPropsMap;
    }
    public Map getTableCustomPropsMap() {
        return this.tableCustomPropsMap;
    }
    /**
     * @return
     */
    public List getClassStereotypesToIgnore() {
        return classStereotypesToIgnore;
    }

    /**
     * @param list
     */
    public void setClassStereotypesToIgnore(List list) {
        this.classStereotypesToIgnore = list;
    }
   

    /**
     * @return
     */
    public EObject getDefaultRelationalColumnType() {
        return defaultRelationalColumnType;
    }

    /**
     * @param object
     */
    public void setDefaultRelationalColumnType(EObject type) {
        defaultRelationalColumnType = type;
    }

    /**
     * @return
     */
    public int getReachabilityConstraint() {
        return reachabilityConstraint;
    }

    /**
     * @param i
     */
    public void setReachabilityConstraint(int i) {
        reachabilityConstraint = i;
    }

    /**
     * @return
     */
    public List getAutoincrementStereotypeNames() {
        return autoincrementStereotypeNames;
    }

    /**
     * @param list
     */
    public void setAutoincrementStereotypeNames(List list) {
        autoincrementStereotypeNames = list;
    }

    /**
     * @return
     */
    public List getReadOnlyStereoTypeNames() {
        return readOnlyStereoTypeNames;
    }

    /**
     * @param list
     */
    public void setReadOnlyStereoTypeNames(List list) {
        readOnlyStereoTypeNames = list;
    }

    /**
     * @return
     */
    public int getGeneratedStringTypeColumnDefaultLength() {
        return generatedStringTypeColumnDefaultLength;
    }

    /**
     * @param i
     */
    public void setGeneratedStringTypeColumnDefaultLength(int i) {
        generatedStringTypeColumnDefaultLength = i;
    }

}

