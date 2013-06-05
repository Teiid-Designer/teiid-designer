/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;


/**
 *
 */
public interface IDelete<C extends ICriteria, 
                                           G extends IGroupSymbol,
                                           E extends IExpression,
                                           LV extends ILanguageVisitor> extends IProcedureContainer<E, LV> {

    /**
     * Returns the group being deleted from
     * 
     * @return Group symbol
     */
    G getGroup();
    
    /**
     * Set the group for this Delete command
     * 
     * @param group Group to be associated with this command
     */
    void setGroup(G group);
    
    /**
     * Returns the criteria object for this command.
     * 
     * @return criteria
     */
    C getCriteria();
    
    /**
     * Set the criteria for this Delete command
     * 
     * @param criteria Criteria to be associated with this command
     */
    void setCriteria(C criteria);
    
}
