/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.symbol.IGroupSymbol;


/**
 *
 */
public interface IDelete extends ICommand {

    /**
     * Returns the group being deleted from
     * 
     * @return Group symbol
     */
    IGroupSymbol getGroup();
    
    /**
     * Set the group for this Delete command
     * 
     * @param group Group to be associated with this command
     */
    void setGroup(IGroupSymbol group);
    
    /**
     * Returns the criteria object for this command.
     * 
     * @return criteria
     */
    ICriteria getCriteria();
    
    /**
     * Set the criteria for this Delete command
     * 
     * @param criteria Criteria to be associated with this command
     */
    void setCriteria(ICriteria criteria);
    
}
