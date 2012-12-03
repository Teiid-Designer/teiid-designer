/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.query;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.lang.ISetQuery;

/**
 * @since 8.0
 */
public class SetQueryUtil {

    public static int setQueryAtIndex(ISetQuery query, int index, IQueryCommand newQuery) {
        if (index < 0) {
            return index;
        }
        if (query.getLeftQuery() instanceof ISetQuery) {
            index = setQueryAtIndex((ISetQuery)query.getLeftQuery(), index, newQuery);
        } else if (index-- == 0) {
            query.setLeftQuery(newQuery);
            return -1;
        } 
        if (query.getRightQuery() instanceof ISetQuery) {
            index = setQueryAtIndex((ISetQuery)query.getRightQuery(), index, newQuery);
        } else if (index-- == 0){
            query.setRightQuery(newQuery);
            return -1;
        }
        return index;
    }
    
    public static List<IQuery> getQueryList(ISetQuery query) {
        ArrayList<IQuery> queries = new ArrayList<IQuery>();
        addToQueryList(queries, query);
        return queries;
    }
    
    static void addToQueryList(List<IQuery> queries, IQueryCommand command) {
        if (command instanceof ISetQuery) {
            addToQueryList(queries, ((ISetQuery)command).getLeftQuery());
            addToQueryList(queries, ((ISetQuery)command).getRightQuery());
        } else {
            queries.add((IQuery)command);
        }
    }
    
}
