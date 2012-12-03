/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.query.SetQueryUtil;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.IValueIteratorProviderCollectorVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.ISubqueryContainer;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;



/** 
 * QueryTreeContentProvider is the content provider for the QueryTreeViewer.  It handles
 * LanguageObjects in a Command and breaks out individual Query objects inside a SetQuery
 * and From and Criteria objects inside a Query.
 * @since 8.0
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
    @Override
	public Object[] getChildren(Object obj) {
        if ( obj instanceof ISetQuery) {
            return SetQueryUtil.getQueryList((ISetQuery)obj).toArray();
        } else if ( obj instanceof IQuery ) {
            if ( ((IQuery) obj).getCriteria() == null ) {
                return new Object[] { ((IQuery) obj).getFrom() };
            }
            return new Object[] { ((IQuery) obj).getFrom(), ((IQuery) obj).getCriteria() };
        } else if ( obj instanceof IFrom ) {
            Collection clauses = ((IFrom) obj).getClauses();
            ArrayList children = new ArrayList(clauses.size());
            for ( Iterator iter = clauses.iterator() ; iter.hasNext() ; ) {
                IFromClause clause = (IFromClause) iter.next();
                if ( clause instanceof IUnaryFromClause ) {
                    children.add(((IUnaryFromClause) clause).getGroup());
                } else {
                    children.add(clause);
                }
            }
            return children.toArray(); 
        } else if ( obj instanceof IFromClause ) {
            if ( obj instanceof IUnaryFromClause ) {
                return new Object[] { };
            } else if ( obj instanceof IJoinPredicate ) {
                return new Object[] { ((IJoinPredicate) obj).getLeftClause(), ((IJoinPredicate) obj).getRightClause() };
            } else if ( obj instanceof ISubqueryFromClause ) {
                return new Object[] { };
            }            
        } else if ( obj instanceof ICriteria ) {
            IQueryService queryService = ModelerCore.getTeiidQueryService();
            IValueIteratorProviderCollectorVisitor visitor = queryService.getValueIteratorProviderCollectorVisitor();
            List<ISubqueryContainer<?>> containers = visitor.getValueIteratorProviders((ICriteria)obj);
            List<ICommand> commands = new ArrayList<ICommand>();
            
            for (ISubqueryContainer container : containers) {
                commands.add(container.getCommand());
            }
            
            return commands.toArray();
        }

        return null;
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Object getParent(Object element) {
        return null;
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.2
     */
    @Override
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
    @Override
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
    @Override
	public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    @Override
	public void inputChanged(Viewer viewer,
                             Object oldInput,
                             Object newInput) {
    }

}
