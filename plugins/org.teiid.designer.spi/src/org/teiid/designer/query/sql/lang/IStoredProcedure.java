/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 *
 */
public interface IStoredProcedure<P extends ISPParameter, E extends IExpression, LV extends ILanguageVisitor> 
    extends IProcedureContainer<E, LV> {

    void setProcedureID(Object procedureID);
    
    Object getProcedureID();
    
    List<P> getInputParameters();

    void setParameter(P parameter);
    
    String getProcedureCallableName();

    void setProcedureName(String procFullName);

    void setDisplayNamedParameters(boolean b);

    String getGroupName();

}
