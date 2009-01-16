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

package com.metamatrix.query.internal.ui.builder.util;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.query.internal.ui.builder.ILanguageObjectInputProvider;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.CompoundCriteria;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.LogicalCriteria;
import com.metamatrix.query.sql.lang.NotCriteria;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.Function;
import com.metamatrix.query.ui.UiConstants;

/**
 * LanguageObjectContentProvider
 */
public class LanguageObjectContentProvider implements ITreeContentProvider,
                                                      UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** Properties key prefix. */
//    private static final String PREFIX = I18nUtil.getPropertyPrefix(LanguageObjectContentProvider.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private LanguageObject langObj;
    
    private Object[] roots = new Object[0];
    
//    private Viewer viewer;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
    }
    
    public Object getChildAt(int theIndex,
                             Object theParent) {
        Object[] kids = getChildren(theParent);
        return kids[theIndex];
    }
    
    public int getChildCount(Object theParent) {
        return getChildren(theParent).length;
    }
    
    public int getChildIndex(Object theChild) {
        int result = -1;
        Object parent = getParent(theChild);
        
        if (parent != null) {
            Object[] kids = getChildren(parent);
            
            for (int i = 0; i < kids.length; i++) {
                if (kids[i] == theChild) {
                    result = i;
                    break;
                }
            }
        }
        
        return result;
    }
    
    private Object[] getChildren(Criteria theCriteria) {
        Object[] result = null;

        if (theCriteria instanceof NotCriteria) {
            NotCriteria notCrit = (NotCriteria)theCriteria;
            Criteria criteria = notCrit.getCriteria();

            if (criteria instanceof LogicalCriteria) {
                result = getChildren(criteria);
            }
        } else {
            if (theCriteria instanceof CompoundCriteria) {
                CompoundCriteria compoundCrit = (CompoundCriteria)theCriteria;
                int size = compoundCrit.getCriteriaCount();
                result = new Object[size];

                for (int i = 0; i < size; i++) {
                    Criteria criteria = compoundCrit.getCriteria(i);
                    result[i] = (criteria == null) ? BuilderUtils.UNDEFINED
                                                   : (Object)criteria;
                }
            }
        }

        return (result == null) ? new Object[0] 
                                : result;
    }

    private Object[] getChildren(Expression theExpression) {
        Object[] result = null;

        if (theExpression instanceof Function) {
            result = getChildren((Function)theExpression);
        }

        return (result == null) ? new Object[0] 
                                : result;
    }

    private Object[] getChildren(Function theFunction) {
        Object[] result = null;

        if (theFunction.isImplicit()) {
            // according to Alex, all implicit functions are conversions and
            // the first argument is what is being converted (which could be a function or expression)
            Expression arg = theFunction.getArgs()[0];
            result = (arg instanceof Function) ? getChildren((Function)arg)
                                               : getChildren(arg);
        } else {
            Expression[] args = theFunction.getArgs();

            if ((args != null) && (args.length > 0)) {
                result = new Object[args.length];

                for (int i = 0; i < args.length; i++) {
                    // if arg is null need to return the undefined string
                    result[i] = (args[i] == null) ? BuilderUtils.UNDEFINED
                                                  : (Object)args[i];
                }
            }
        }

        return (result == null) ? new Object[0] : result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object theParent) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "getChildren():langObj=" + langObj+", parent="+theParent); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Object[] result = null;

        if (theParent != null) {
            if (theParent instanceof Criteria) {
                result = getChildren((Criteria)theParent);
            } else if (theParent instanceof Expression) {
                result = getChildren((Expression)theParent);
            }
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "getChildren()"); //$NON-NLS-1$
        }

        return (result == null) ? new Object[0] 
                                : result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object theInputElement) {
        return roots;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object theElement) {
        return getParent(langObj, theElement);
    }

    public Object getParent(Object theRoot,
                            Object theChild) {
        Object result = null;
        
        if (theRoot != theChild) {
            Object[] kids = getChildren(theRoot);
            
            for (int i = 0; i < kids.length; i++) {
                if (theChild == kids[i]) {
                    result = theRoot;
                    break;
                }
                result = getParent(kids[i], theChild);
                
                if (result != null) {
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Gets the root <code>LanguageObject</code> or a string representing an undefined element.
     * @return the root element
     */
    public Object getRoot() {
        return (roots.length > 0) ? roots[0]
                                  : null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object theElement) {
        return getChildren(theElement).length > 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer theViewer,
                             Object theOldInput,
                             Object theNewInput) {
        // theNewInput is null when disposing of the view
        if (theNewInput != null) {
            /*
             * Note: See documentation for ILanguageObjectInputProvider for reason why input cannot be the LanguageObject
             */
            Assertion.isInstanceOf(theNewInput, ILanguageObjectInputProvider.class, ILanguageObjectInputProvider.class.getName());

//            viewer = theViewer;
            langObj = ((ILanguageObjectInputProvider)theNewInput).getLanguageObject();
            Object root = (langObj == null) ? BuilderUtils.UNDEFINED 
                                            : (Object)langObj;
            roots = new Object[] {root};
        }
    }

}
