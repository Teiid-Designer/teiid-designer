/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid8.sql;

import java.util.Set;
import org.teiid.core.types.JDBCSQLTypeInfo;
import org.teiid.designer.sql.IQueryService;
import org.teiid.language.SQLConstants;
import org.teiid.query.sql.ProcedureReservedWords;

/**
 *
 */
public class QueryService implements IQueryService {

    @Override
    public boolean isReservedWord(String word) {
        return SQLConstants.isReservedWord(word);
    }
    
    @Override
    public boolean isProcedureReservedWord(String word) {
        return ProcedureReservedWords.isProcedureReservedWord(word);
    }
    
    @Override
    public Set<String> getReservedWords() {
        return SQLConstants.getReservedWords();
    }
    
    @Override
    public Set<String> getNonReservedWords() {
        return SQLConstants.getNonReservedWords();
    }
    
    @Override
    public String getJDBCSQLTypeName(int jdbcType) {
        return JDBCSQLTypeInfo.getTypeName(jdbcType);
    }
}
