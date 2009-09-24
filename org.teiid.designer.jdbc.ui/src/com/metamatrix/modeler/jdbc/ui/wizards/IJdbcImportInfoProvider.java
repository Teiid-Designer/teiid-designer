/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.ui.wizards;

import java.util.List;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;


/** 
 * @since 5.0
 */
public interface IJdbcImportInfoProvider {
    
    /**
     * Obtains the <code>JdbcDatabase</code> used during the import.
     * @return the database
     * @since 5.0
     */
    JdbcDatabase getDatabase();
    
    /**
     * Obtains the difference report produced from the import. 
     * @return the difference report
     * @since 5.0
     */
    List getDifferenceReports();
    
    /**
     * Obtains the name of the model created from the import. 
     * @return the model name
     * @since 5.0
     */
    String getModelName();
    
    /** 
     * @return the model resource of the newly created model or <code>null</code> if the model has not yet been created
     * @since 5.5.3
     */
    ModelResource getModelResource();
    
    /**
     * The <code>JdbcSource</code> used during the import. 
     * @return the source
     * @since 5.0
     */
    JdbcSource getSource();
    
    /**
     * The password used during the import. 
     * @return the password
     * @since 5.0
     */
    String getPassword();

}
