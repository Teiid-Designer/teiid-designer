/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.modelextension;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EClassifier;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.metamodels.core.extension.ExtensionFactory;
import org.teiid.designer.metamodels.core.extension.XAttribute;
import org.teiid.designer.metamodels.core.extension.XClass;
import org.teiid.designer.metamodels.core.extension.XPackage;


/**
 * Base interface for creating model extensions. The envisioned implementation is limited to creating a single extension to the
 * Table class and a single extension to the Column class.
 *
 * @since 8.0
 */
public interface ExtensionManager {

    public XPackage getPackage();

    public void loadModelExtensions( IContainer targetModelLocation,
                                     IProgressMonitor monitor ) throws ModelerCoreException;

    /**
     * Provides the name of the file that will be created in a model project.
     * 
     * @return the file name
     */
    public String getModelFileName();

    /**
     * Provides the namespace URI for the extension model
     * 
     * @return the namespace URI
     */
    public String getPackageNsUri();

    /**
     * Provides the package prefix for the extension model
     * 
     * @return the package prefix
     */
    public String getPackagePrefix();

    /**
     * Provides the package name for the extension model
     * 
     * @return the package name
     */
    public String getPackageName();

    /**
     * Provides the name for the Catalog extension
     * 
     * @return the table name
     */
    public String getCatalogName();

    /**
     * Provides the name for the Schema extension
     * 
     * @return the schema name
     */
    public String getSchemaName();

    /**
     * Provides the name for the Table extension
     * 
     * @return the table name
     */
    public String getTableName();

    /**
     * Provides the name for the Column extension
     * 
     * @return the column name
     */
    public String getColumnName();

    /**
     * Create enums to assign to extension properties.
     * 
     * @param xFactory used to create the extension attributes
     */
    public void createEnums( ExtensionFactory xFactory );

    /**
     * Create the extensions to the Catalog class
     * 
     * @param xFactory used to create the extension attributes
     * @param catalog the catalog class to extend
     */
    public void createCatalogExtensions( ExtensionFactory xFactory,
                                         XClass catalog );

    /**
     * Create the extensions to the Schema class
     * 
     * @param xFactory used to create the extension attributes
     * @param schema the schema class to extend
     */
    public void createSchemaExtensions( ExtensionFactory xFactory,
                                        XClass schema );

    /**
     * Create the extensions to the Table class
     * 
     * @param xFactory used to create the extension attributes
     * @param table the table class to extend
     */
    public void createTableExtensions( ExtensionFactory xFactory,
                                       XClass table );

    /**
     * Create the extensions to the Column class
     * 
     * @param xFactory used to create the extension attributes
     * @param column the column class to extend
     */
    public void createColumnExtensions( ExtensionFactory xFactory,
                                        XClass column );

    /**
     * The method is called when an existing model extension is loaded. This method is called for each extension attribute on the
     * extended Table or Column. In this method you should assign the attribute to a field that is accessed when the metadata
     * attribute is set.
     * 
     * @param attribute
     */
    public void assignAttribute( XAttribute attribute );

    /**
     * The method is called when an existing model extension is loaded. This method is called for each classifier (enumeration) on
     * the extended Table or Column. In this method you should assign the enumeration to a field that is accessed when a metadata
     * attribute of the enum type is set.
     * 
     * @param classifier
     */
    public void assignClassifier( EClassifier classifier );

}
