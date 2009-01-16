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
