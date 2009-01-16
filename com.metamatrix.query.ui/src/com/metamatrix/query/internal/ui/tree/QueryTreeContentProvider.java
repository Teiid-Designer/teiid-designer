/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.query.internal.ui.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.From;
import com.metamatrix.query.sql.lang.FromClause;
import com.metamatrix.query.sql.lang.JoinPredicate;
import com.metamatrix.query.sql.lang.Query;
import com.metamatrix.query.sql.lang.SetQuery;
import com.metamatrix.query.sql.lang.SubqueryFromClause;
import com.metamatrix.query.sql.lang.UnaryFromClause;
import com.metamatrix.query.sql.visitor.CommandCollectorVisitor;


/** 
 * QueryTreeContentProvider is the content provider for the QueryTreeViewer.  It handles
 * LanguageObjects in a Command and breaks out individual Query objects inside a SetQuery
 * and From and Criteria objects inside a Query.
 * @since 4.2
 */
public class QueryTreeContentProvider implements ITreeContentProvider {

    ////////////////////////////////////////////////////////////////////////////////////////
    // STATIC VARIABLES
    ////////////////////////////////////////////////////////////////////////////////////////
    public static QueryTreeContentProvider instance = null;
    
    ////////////////////////////////////////////////////////////////////////////////////////
    // STATIC METHODS
    ////////////////////////////////////////////////////////////////////////////////////////
    public static QueryTreeContentProvider getInstance() {
        if (instance == null) {
            instance = new QueryTreeContentProvider();
        }
        return instance;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ////////////////////////////////////////////////////////////////////////////////////////
    private QueryTreeContentProvider() {
        super();
    }
    
    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
    public Object[] getChildren(Object obj) {
        if ( obj instanceof SetQuery) {
            return ((SetQuery) obj).getQueryCommands().toArray();
        } else if ( obj instanceof Query ) {
            if ( ((Query) obj).getCriteria() == null ) {
                return new Object[] { ((Query) obj).getFrom() };
            }
            return new Object[] { ((Query) obj).getFrom(), ((Query) obj).getCriteria() };
        } else if ( obj instanceof From ) {
            Collection clauses = ((From) obj).getClauses();
            ArrayList children = new ArrayList(clauses.size());
            for ( Iterator iter = clauses.iterator() ; iter.hasNext() ; ) {
                FromClause clause = (FromClause) iter.next();
                if ( clause instanceof UnaryFromClause ) {
                    children.add(((UnaryFromClause) clause).getGroup());
                } else {
                    children.add(clause);
                }
            }
            return children.toArray(); 
        } else if ( obj instanceof FromClause ) {
            if ( obj instanceof UnaryFromClause ) {
                return new Object[] { };
            } else if ( obj instanceof JoinPredicate ) {
                return new Object[] { ((JoinPredicate) obj).getLeftClause(), ((JoinPredicate) obj).getRightClause() };
            } else if ( obj instanceof SubqueryFromClause ) {
                return new Object[] { };
            }            
        } else if ( obj instanceof Criteria ) {
            return CommandCollectorVisitor.getCommands((Criteria) obj).toArray();
        }
        return null;
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    public Object getParent(Object element) {
        return null;
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.2
     */
    public boolean hasChildren(Object element) {
        Object[] children = getChildren(element);
        if ( children == null || children.length == 0 ) {
            return false;
        }
        return true;
    }    

    /** 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements(Object inputElement) {
        if ( inputElement instanceof ArrayList ) {
            return ((ArrayList) inputElement).toArray();
        }
        
        return getChildren(inputElement);
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void inputChanged(Viewer viewer,
                             Object oldInput,
                             Object newInput) {
    }

}
