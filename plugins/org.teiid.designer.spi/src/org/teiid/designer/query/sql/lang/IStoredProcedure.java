/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.List;

/**
 *
 */
public interface IStoredProcedure extends ICommand {

    void setProcedureID(Object procedureID);
    
    Object getProcedureID();
    
    List<ISPParameter> getInputParameters();

    void setParameter(ISPParameter parameter);
    
    String getProcedureCallableName();

    void setProcedureName(String procFullName);

    void setDisplayNamedParameters(boolean b);

    String getGroupName();

}
