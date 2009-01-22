/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.ui.wizards;



/** 
 * @since 5.0
 */
public interface IJdbcImportPostProcessor {

    void postProcess(IJdbcImportInfoProvider theInfoProvider) throws Exception;
    
}
