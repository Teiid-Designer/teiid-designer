/*
 * Copyright © 2006 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.ext;


/** 
 * Extension Point Interface to produce input 
 */
public interface IRequestDocumentGenerator {
    
    /**
     * Generate a string that is a request XML document for the specified web service operation. 
     * @param webServiceModelID
     * @param webServiceOperationID
     * @return
     */
    String generateRequestDocument(String webServiceModelID, String webServiceOperationID);

}
