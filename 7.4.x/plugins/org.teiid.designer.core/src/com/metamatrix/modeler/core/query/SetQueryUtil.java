/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.query;

import java.util.ArrayList;
import java.util.List;

import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SetQuery;

public class SetQueryUtil {

    public static int setQueryAtIndex(SetQuery query, int index, QueryCommand newQuery) {
        if (index < 0) {
            return index;
        }
        if (query.getLeftQuery() instanceof SetQuery) {
            index = setQueryAtIndex((SetQuery)query.getLeftQuery(), index, newQuery);
        } else if (index-- == 0) {
            query.setLeftQuery(newQuery);
            return -1;
        } 
        if (query.getRightQuery() instanceof SetQuery) {
            index = setQueryAtIndex((SetQuery)query.getRightQuery(), index, newQuery);
        } else if (index-- == 0){
            query.setRightQuery(newQuery);
            return -1;
        }
        return index;
    }
    
    public static List<Query> getQueryList(SetQuery query) {
        ArrayList<Query> queries = new ArrayList<Query>();
        addToQueryList(queries, query);
        return queries;
    }
    
    static void addToQueryList(List<Query> queries, QueryCommand command) {
        if (command instanceof SetQuery) {
            addToQueryList(queries, ((SetQuery)command).getLeftQuery());
            addToQueryList(queries, ((SetQuery)command).getRightQuery());
        } else {
            queries.add((Query)command);
        }
    }
    
}
