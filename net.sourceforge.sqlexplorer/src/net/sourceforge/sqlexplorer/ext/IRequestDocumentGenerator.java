/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package net.sourceforge.sqlexplorer.ext;

/**
 * Extension Point Interface to produce input
 */
public interface IRequestDocumentGenerator {

    /**
     * Generate a string that is a request XML document for the specified web service operation.
     * 
     * @param webServiceModelID
     * @param webServiceOperationID
     * @return
     */
    String generateRequestDocument( String webServiceModelID,
                                    String webServiceOperationID );

}
