/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IPredicateCriteria;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.builder.util.LanguageObjectContentProvider;
import org.teiid.query.ui.builder.util.BuilderUtils;
import org.teiid.query.ui.builder.util.LanguageObjectLabelProvider;

/**
 * @since 8.0
 */
public class LanguageObjectBuilderTreeViewer extends TreeViewer implements ILanguageObjectInputProvider, UiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(LanguageObjectBuilderTreeViewer.class);

    LanguageObjectContentProvider contentProvider;

    private ILanguageObject langObj;

    public LanguageObjectBuilderTreeViewer( Composite theParent ) {
        super(theParent, SWT.NONE);

        contentProvider = new LanguageObjectContentProvider();
        setContentProvider(contentProvider);
        setLabelProvider(new LanguageObjectLabelProvider());

        getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    public void addUndefinedAndCriteria() {
        addUndefinedCriteria((ICriteria)getSelectedObject(), true);
    }

    private void addUndefinedCriteria( ICriteria theCriteria,
                                       boolean theAndFlag ) {
        CoreArgCheck.isNotNull(theCriteria); // should not be calling if null

        Object newSelection = StructuredSelection.EMPTY;

        if (theCriteria instanceof ICompoundCriteria) {
            ICompoundCriteria criteria = (ICompoundCriteria)theCriteria;
            criteria.addCriteria((ICriteria)null);
            refresh(true);
            newSelection = contentProvider.getChildAt((criteria.getCriteriaCount() - 1), criteria);
        } else if (theCriteria instanceof INotCriteria) {
            // the contained Criteria must be a CompoundCriteria
            addUndefinedCriteria(((INotCriteria)theCriteria).getCriteria(), theAndFlag);
        } else if (theCriteria instanceof IPredicateCriteria) {
            IQueryService queryService = ModelerCore.getTeiidQueryService();
            IQueryFactory factory = queryService.createQueryFactory();
            
            List<? extends ICriteria> criteriaList = Arrays.asList(theCriteria, null);
            
            ICompoundCriteria compoundCriteria = factory.createCompoundCriteria(
                                                           (theAndFlag) ? ICompoundCriteria.AND : ICompoundCriteria.OR,
                                                           criteriaList);

            // modify parent here
            ILanguageObject parent = (ILanguageObject)contentProvider.getParent(theCriteria);
            int index = contentProvider.getChildIndex(theCriteria);

            if (parent == null) {
                setLanguageObject(compoundCriteria);
                refresh(true);

                // select undefined criteria
                newSelection = contentProvider.getChildAt(1, contentProvider.getRoot());
                expandToLevel(newSelection, ALL_LEVELS);
            } else if ((parent instanceof INotCriteria) || (parent instanceof ICompoundCriteria)) {
                ICompoundCriteria criteria = null;

                if (parent instanceof INotCriteria) {
                    // then NotCriteria's contained criteria must be a CompoundCriteria
                    criteria = (ICompoundCriteria)((INotCriteria)parent).getCriteria();
                } else {
                    criteria = (ICompoundCriteria)parent;
                }

                List crits = criteria.getCriteria();
                crits.set(index, compoundCriteria);
                refresh(true);

                // select undefined criteria
                newSelection = contentProvider.getChildAt(1, compoundCriteria);
            } else {
                CoreArgCheck.isTrue(false, Util.getString(PREFIX + "unexpectedType", //$NON-NLS-1$
                                                          new Object[] {"addUndefinedCriteria", parent.getClass()})); //$NON-NLS-1$
            }
        }

        // select appropriate tree item
        final Object selection = newSelection;

        Display.getDefault().asyncExec(new Runnable() {
            @Override
			public void run() {
                setSelection(new StructuredSelection(selection));
            }
        });
    }

    public void addUndefinedOrCriteria() {
        addUndefinedCriteria((ICriteria)getSelectedObject(), false);
    }

    private boolean canDelete( Object theLangObj ) {
        boolean result = true;
        ILanguageObject parent = (ILanguageObject)contentProvider.getParent(theLangObj);

        if (parent == null) {
            // object is root. can only delete if not undefined
            result = !isUndefined(theLangObj);
        } else if (parent instanceof IFunction) {
            // all function arguments except conversion type constants can be deleted
            if (theLangObj instanceof IConstant) {
                result = !BuilderUtils.isConversionType((IConstant)theLangObj);
            }
        } else if (parent instanceof INotCriteria) {
            // get compound criteria and ask again
            INotCriteria notCrit = (INotCriteria)parent;
            ICriteria criteria = notCrit.getCriteria();

            // NotCriteria must contain either another NotCriteria or a CompoundCriteria
            while (!(criteria instanceof ICompoundCriteria)) {
                // must be a NotCriteria
                criteria = ((INotCriteria)criteria).getCriteria();
                result = canDelete(criteria);
            }
        }

        return result;
    }

    public boolean canDeleteSelection() {
        return canDelete(getSelectedObject());
    }

    private void delete( Object theLangObj ) {
        if (canDelete(theLangObj)) {
            boolean doSelect = true;

            //
            // set the parent object to reflect the change in it's child. parent can't be undefined.
            //

            Object newSelection = StructuredSelection.EMPTY;
            ILanguageObject parent = (ILanguageObject)contentProvider.getParent(theLangObj);

            if (parent == null) {
                // object being delete is root language object
                setLanguageObject(null); // resets undefined count
                refresh(true);
                newSelection = contentProvider.getRoot();
                expandToLevel(newSelection, ALL_LEVELS);
            } else if (parent instanceof IFunction) {
                // set the arg to null in parent
                int index = contentProvider.getChildIndex(theLangObj);

                IExpression[] args = ((IFunction)parent).getArgs();
                args[index] = null;

                refresh(true);
                newSelection = contentProvider.getChildAt(index, parent);
            } else if (parent instanceof ICriteria) {
                if (contentProvider.getChildCount(parent) > 1) {
                    ICompoundCriteria compoundCriteria = null;

                    // parent is either a compound criteria or not criteria
                    if (parent instanceof ICompoundCriteria) {
                        compoundCriteria = (ICompoundCriteria)parent;
                    } else { // NotCriteria
                        INotCriteria notCrit = (INotCriteria)parent;
                        ICriteria criteria = notCrit.getCriteria();

                        // NotCriteria must contain either another NotCriteria or a CompoundCriteria
                        while (!(criteria instanceof ICompoundCriteria)) {
                            // must be a NotCriteria
                            criteria = ((INotCriteria)criteria).getCriteria();
                        }

                        compoundCriteria = (ICompoundCriteria)criteria;
                    }

                    List crits = compoundCriteria.getCriteria();
                    int index = contentProvider.getChildIndex(theLangObj);

                    // CompoundCriteria has to have at least 2 criteria to be compound. if it has more than 2
                    // just delete the selected obj. if there are exactly 2 criteria than this special processing block occurs.
                    if (contentProvider.getChildCount(parent) == 2) {
                        int siblingIndex = (index == 0) ? 1 : 0;

                        // if deleting this node would leave just an undefined node under the parent, delete parent
                        if (isUndefined(contentProvider.getChildAt(siblingIndex, parent))) {
                            delete(parent);
                        } else {
                            // sibling is not undefined

                            // if deleting an undefined node, delete from parent. this will leave one node
                            // under the parent.
                            if (isUndefined(theLangObj)) {
                                crits.remove(index);
                                // selection taken care of below in block checking for size of 1
                            } else {
                                // replace deleted node with undefined node
                                crits.set(index, (ICriteria)null);
                                refresh(true);
                                newSelection = contentProvider.getChildAt(index, parent);
                            }

                        }
                    } else {
                        // just remove from parent
                        crits.remove(index);
                        refresh(true);
                        newSelection = parent;
                    }

                    if (crits.size() == 1) {
                        // need to modify parent node with the child (refresh done there)
                        modifyLanguageObject(parent, (ILanguageObject)contentProvider.getChildAt(0, parent), false);
                        doSelect = false; // modify does select
                    }
                }
            }

            if (doSelect) {
                final Object selection = newSelection;

                Display.getDefault().asyncExec(new Runnable() {
                    @Override
					public void run() {
                        setSelection(new StructuredSelection(selection));
                    }
                });
            }
        }
    }

    public void deleteSelection() {
        delete(getSelectedObject());
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.ILanguageObjectInputProvider#getLanguageObject()
     */
    @Override
	public ILanguageObject getLanguageObject() {
        return langObj;
    }

    // returns null if no selection
    // if there is a selection it will either by a LanguageObject or BuilderUtils.UNDEFINED
    public Object getSelectedObject() {
        Object result = null;
        IStructuredSelection selection = (IStructuredSelection)getSelection();

        if (selection.size() == 1) {
            result = selection.getFirstElement();
        }

        return result;
    }

    /**
     * Indicates if the given object has undefined children.
     * 
     * @param theLangObj the object being queried
     * @return <code>true</code> if undefined child exist; <code>false</code> otherwise.
     */
    public boolean hasUndefinedChild( Object theLangObj ) {
        boolean result = false;
        Object[] kids = contentProvider.getChildren(theLangObj);

        if (kids.length > 0) {
            // go in reverse order since it is more likely undefined will be at the end
            for (int i = (kids.length - 1); i >= 0; i--) {
                if (isUndefined(kids[i])) {
                    result = true;
                } else {
                    result = hasUndefinedChild(kids[i]);
                }

                if (result) {
                    break;
                }
            }
        }

        return result;
    }

    public boolean isComplete() {
        Object root = contentProvider.getRoot();

        boolean result = ((root == null) || (isUndefined(root))) ? false : !hasUndefinedChild(root);

        return result;
    }

    public boolean isUndefined( Object theLangObj ) {
        return ((theLangObj == null) || theLangObj.equals(BuilderUtils.UNDEFINED));
    }

    private void modifyLanguageObject( Object theObject,
                                       ILanguageObject theNewValue,
                                       boolean retainSelection ) {
        CoreArgCheck.isNotNull(theNewValue); // should not be null when modifying

        //
        // set the parent object to reflect the change in it's child. parent's can't be undefined
        //

        Object newSelection = StructuredSelection.EMPTY;
        ILanguageObject parent = (ILanguageObject)contentProvider.getParent(theObject);

        Object newValue = theNewValue;

        if (parent == null) {
            // root language object
            setLanguageObject((ILanguageObject)newValue);
            refresh(true);
            newSelection = contentProvider.getRoot();
            expandToLevel(newSelection, ALL_LEVELS);

            if (newSelection instanceof IFunction) {
                IFunction function = (IFunction)newSelection;

                if (function.getArgs().length > 0) {
                    newSelection = contentProvider.getChildAt(0, function);
                }
            }
        } else if (parent instanceof IFunction) {
            // set the arg to new value in parent
            int index = contentProvider.getChildIndex(theObject);

            IExpression[] args = ((IFunction)parent).getArgs();
            args[index] = (IExpression)newValue;

            refresh(true);
            expandToLevel(parent, ALL_LEVELS);

            newSelection = contentProvider.getChildAt(index, parent);

            if (!retainSelection) {
                if (newSelection instanceof IFunction) {
                    // if function arg change to be a function, select first function arg
                    if (contentProvider.getChildCount(newSelection) > 0) {
                        newSelection = contentProvider.getChildAt(0, newSelection);
                    }
                } else {
                    // select next sibling function arg if exists
                    if ((args.length - 1) > index) {
                        newSelection = contentProvider.getChildAt(index + 1, parent);
                    } else if (index > 0) {
                        newSelection = contentProvider.getChildAt(0, parent);
                    } else {
                        newSelection = contentProvider.getChildAt(index, parent);
                    }
                }
            }
        } else if (parent instanceof ICriteria) {
            // theLangObj must also be a Criteria
            // since NotCriteria aren't really edited in the editor (their contained criteria is). Need to
            // save the state in order to restore it when modifying
            ICriteria newCriteria = (ICriteria)newValue;
            int index = contentProvider.getChildIndex(theObject);

            if ((parent instanceof ICompoundCriteria) || (parent instanceof INotCriteria)) {
                ICompoundCriteria compoundCriteria = null;

                if (parent instanceof ICompoundCriteria) {
                    compoundCriteria = (ICompoundCriteria)parent;
                } else {
                    compoundCriteria = (ICompoundCriteria)((INotCriteria)parent).getCriteria();
                }

                List criteriaCollection = compoundCriteria.getCriteria();
                criteriaCollection.set(index, newCriteria);
                refresh(true);

                if (retainSelection) {
                    newSelection = criteriaCollection.get(index);
                } else {
                    // set selection to next sibling criteria or to the first sibling
                    if ((criteriaCollection.size() - 1) > index) {
                        newSelection = criteriaCollection.get(index + 1);
                    } else if (index > 0) {
                        newSelection = criteriaCollection.get(0);
                    } else {
                        newSelection = contentProvider.getChildAt(index + 1, parent);
                    }
                }
            } else {
                CoreArgCheck.isTrue(false, Util.getString(PREFIX + "unexpectedType", //$NON-NLS-1$
                                                          new Object[] {"modifyLanguageObject", //$NON-NLS-1$
                                                              parent.getClass().getName()}));
            }

            expandToLevel(parent, ALL_LEVELS);
        }

        // select next node
        setSelection((newSelection == null) ? StructuredSelection.EMPTY : new StructuredSelection(newSelection));
    }

    public void modifyNotCriteriaStatus() {
        Object selectedObj = getSelectedObject();
        CoreArgCheck.isNotNull(selectedObj); // should not be calling if no row selected

        if (selectedObj instanceof INotCriteria) {
            modifySelectedItem(((INotCriteria)selectedObj).getCriteria(), true);
        } else if (selectedObj instanceof ICriteria) {
            IQueryService queryService = ModelerCore.getTeiidQueryService();
            IQueryFactory factory = queryService.createQueryFactory();
            modifySelectedItem(factory.createNotCriteria((ICriteria)selectedObj), true);
        } else if (isUndefined(selectedObj)) {
            CoreArgCheck.isTrue(false, Util.getString(PREFIX + "unexpectedType", //$NON-NLS-1$
                                                      new Object[] {"modifyNotCriteriaStatus", BuilderUtils.UNDEFINED})); //$NON-NLS-1$
        } else {
            CoreArgCheck.isTrue(false, Util.getString(PREFIX + "unexpectedType", //$NON-NLS-1$
                                                      new Object[] {"modifyNotCriteriaStatus", selectedObj.getClass()})); //$NON-NLS-1$
        }
    }

    public void modifySelectedItem( ILanguageObject theLangObj,
                                    boolean retainSelection ) {
        modifyLanguageObject(getSelectedObject(), theLangObj, retainSelection);
    }

    public void selectRoot() {
        // put this in the back of the UI thread event queue.
        // at startup the listeners we're working. this makes sure the listeners have all been wired.
        Display.getDefault().asyncExec(new Runnable() {
            @Override
			public void run() {
                setSelection(new StructuredSelection(contentProvider.getRoot()));
            }
        });
    }

    public void setLanguageObject( ILanguageObject theLangObj ) {
        langObj = theLangObj;
        setInput(this);
    }
}
