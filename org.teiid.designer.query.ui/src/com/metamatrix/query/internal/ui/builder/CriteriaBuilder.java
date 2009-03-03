/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.actions.AndCriteriaAction;
import com.metamatrix.query.internal.ui.builder.actions.NotCriteriaAction;
import com.metamatrix.query.internal.ui.builder.actions.OrCriteriaAction;
import com.metamatrix.query.internal.ui.builder.criteria.CriteriaEditor;
import com.metamatrix.query.internal.ui.builder.model.CriteriaEditorModel;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.CompoundCriteria;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.NotCriteria;
import com.metamatrix.query.sql.lang.PredicateCriteria;
import com.metamatrix.query.ui.builder.ILanguageObjectEditor;

/**
 * CriteriaBuilder
 */
public class CriteriaBuilder extends AbstractLanguageObjectBuilder {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(CriteriaBuilder.class);

    private IAction andAction;

    private IAction notAction;

    private IAction orAction;

    private CriteriaEditor editor;

    public CriteriaBuilder( Shell theShell ) {
        super(theShell, Util.getString(PREFIX + "title")); //$NON-NLS-1$

        setSizeRelativeToScreen(80, 80);
        setCenterOnDisplay(true);
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectBuilder#createTreeButtons(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createTreeButtons( Composite theParent ) {
        super.createTreeButtons(theParent);

        // add the AndCriteriaAction
        Runnable andRunner = new Runnable() {
            public void run() {
                handleAndSelected();
            }
        };
        andAction = new AndCriteriaAction(theParent, andRunner);

        // add the OrCriteriaAction
        Runnable orRunner = new Runnable() {
            public void run() {
                handleOrSelected();
            }
        };
        orAction = new OrCriteriaAction(theParent, orRunner);

        // add the NotCriteriaAction
        Runnable notRunner = new Runnable() {
            public void run() {
                handleNotSelected();
            }
        };
        notAction = new NotCriteriaAction(theParent, notRunner);
    }

    @Override
    protected void fillContextMenu( IMenuManager theMenuMgr ) {
        super.fillContextMenu(theMenuMgr);

        theMenuMgr.add(andAction);
        theMenuMgr.add(orAction);
        theMenuMgr.add(notAction);
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectBuilder#createEditorDetails(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected ILanguageObjectEditor createEditor( Composite theParent ) {
        editor = new CriteriaEditor(theParent, new CriteriaEditorModel());

        // needs to be done after construction because buttons aren't created yet
        getButton(IDialogConstants.OK_ID).setToolTipText(Util.getString(PREFIX + "okButton.tip")); //$NON-NLS-1$
        getButton(IDialogConstants.CANCEL_ID).setToolTipText(Util.getString(PREFIX + "cancelButton.tip")); //$NON-NLS-1$

        return editor;
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectBuilder#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

    void handleAndSelected() {
        getTreeViewer().addUndefinedAndCriteria();
    }

    void handleNotSelected() {
        getTreeViewer().modifyNotCriteriaStatus();
    }

    void handleOrSelected() {
        getTreeViewer().addUndefinedOrCriteria();
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectBuilder#setEditorLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    @Override
    protected void setEditorLanguageObject( LanguageObject theEditorLangObj ) {
        if (theEditorLangObj instanceof CompoundCriteria) {
            getEditor().clear();
        } else if (theEditorLangObj instanceof NotCriteria) {
            setEditorLanguageObject(((NotCriteria)theEditorLangObj).getCriteria());
        } else {
            getEditor().setLanguageObject(theEditorLangObj);
        }
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectBuilder#setEnabledStatus()
     */
    @Override
    protected void setEnabledStatus() {
        super.setEnabledStatus();

        Object selectedObj = getTreeViewer().getSelectedObject();

        // use the object currently in the editor
        setEnabledStatus((selectedObj instanceof LanguageObject) ? (LanguageObject)selectedObj : null);
    }

    private void setEnabledStatus( LanguageObject theLangObj ) {
        if (getTreeViewer().isUndefined(theLangObj)) {
            andAction.setEnabled(false);
            orAction.setEnabled(false);
            notAction.setEnabled(false);
            // Must enable the editor, else cannot define an object
            editor.setEnabled(true);
        } else {
            boolean enableEditor = true;

            if (theLangObj instanceof PredicateCriteria) {
                andAction.setEnabled(true);
                orAction.setEnabled(true);
                notAction.setEnabled(true);
            } else if (theLangObj instanceof CompoundCriteria) {
                // if an undefined node already exists don't allow another one
                CompoundCriteria crit = (CompoundCriteria)theLangObj;
                notAction.setEnabled(true);

                if (!getTreeViewer().hasUndefinedChild(theLangObj)) {
                    if (crit.getOperator() == CompoundCriteria.AND) {
                        andAction.setEnabled(true);
                        orAction.setEnabled(false);
                    } else {
                        andAction.setEnabled(false);
                        orAction.setEnabled(true);
                    }
                } else {
                    andAction.setEnabled(false);
                    orAction.setEnabled(false);
                }

                // editor.clear();
                enableEditor = false;
            } else if (theLangObj instanceof NotCriteria) {
                NotCriteria notCrit = (NotCriteria)theLangObj;
                Criteria subCrit = notCrit.getCriteria();
                setEnabledStatus(subCrit);
                return;
            } else {
                andAction.setEnabled(false);
                orAction.setEnabled(false);
                notAction.setEnabled(false);
                enableEditor = false;
            }

            editor.setEnabled(enableEditor);
            this.btnReset.setEnabled(enableEditor);
            this.btnSet.setEnabled(enableEditor);
        }
    }

}
