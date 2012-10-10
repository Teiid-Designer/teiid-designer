/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.util;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.builder.ILanguageObjectInputProvider;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.LogicalCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.ui.builder.util.BuilderUtils;


/**
 * LanguageObjectContentProvider
 *
 * @since 8.0
 */
public class LanguageObjectContentProvider implements ITreeContentProvider, UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Properties key prefix. */
    // private static final String PREFIX = I18nUtil.getPropertyPrefix(LanguageObjectContentProvider.class);

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private LanguageObject langObj;

    private Object[] roots = new Object[0];

    // private Viewer viewer;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
	public void dispose() {
    }

    public Object getChildAt( int theIndex,
                              Object theParent ) {
        Object[] kids = getChildren(theParent);
        return kids[theIndex];
    }

    public int getChildCount( Object theParent ) {
        return getChildren(theParent).length;
    }

    public int getChildIndex( Object theChild ) {
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

    private Object[] getChildren( Criteria theCriteria ) {
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
                    result[i] = (criteria == null) ? BuilderUtils.UNDEFINED : (Object)criteria;
                }
            }
        }

        return (result == null) ? new Object[0] : result;
    }

    private Object[] getChildren( Expression theExpression ) {
        Object[] result = null;

        if (theExpression instanceof Function) {
            result = getChildren((Function)theExpression);
        }

        return (result == null) ? new Object[0] : result;
    }

    private Object[] getChildren( Function theFunction ) {
        Object[] result = null;

        if (theFunction.isImplicit()) {
            // according to Alex, all implicit functions are conversions and
            // the first argument is what is being converted (which could be a function or expression)
            Expression arg = theFunction.getArgs()[0];
            result = (arg instanceof Function) ? getChildren((Function)arg) : getChildren(arg);
        } else {
            Expression[] args = theFunction.getArgs();

            if ((args != null) && (args.length > 0)) {
                result = new Object[args.length];

                for (int i = 0; i < args.length; i++) {
                    // if arg is null need to return the undefined string
                    result[i] = (args[i] == null) ? BuilderUtils.UNDEFINED : (Object)args[i];
                }
            }
        }

        return (result == null) ? new Object[0] : result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
	public Object[] getChildren( Object theParent ) {
        Object[] result = null;

        if (theParent != null) {
            if (theParent instanceof Criteria) {
                result = getChildren((Criteria)theParent);
            } else if (theParent instanceof Expression) {
                result = getChildren((Expression)theParent);
            }
        }
        return (result == null) ? new Object[0] : result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
	public Object[] getElements( Object theInputElement ) {
        return roots;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
	public Object getParent( Object theElement ) {
        return getParent(langObj, theElement);
    }

    public Object getParent( Object theRoot,
                             Object theChild ) {
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
     * 
     * @return the root element
     */
    public Object getRoot() {
        return (roots.length > 0) ? roots[0] : null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
	public boolean hasChildren( Object theElement ) {
        return getChildren(theElement).length > 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
	public void inputChanged( Viewer theViewer,
                              Object theOldInput,
                              Object theNewInput ) {
        // theNewInput is null when disposing of the view
        if (theNewInput != null) {
            /*
             * Note: See documentation for ILanguageObjectInputProvider for reason why input cannot be the LanguageObject
             */
            CoreArgCheck.isInstanceOf(ILanguageObjectInputProvider.class,
                                      theNewInput,
                                      ILanguageObjectInputProvider.class.getName());

            // viewer = theViewer;
            langObj = ((ILanguageObjectInputProvider)theNewInput).getLanguageObject();
            Object root = (langObj == null) ? BuilderUtils.UNDEFINED : (Object)langObj;
            roots = new Object[] {root};
        }
    }

}
