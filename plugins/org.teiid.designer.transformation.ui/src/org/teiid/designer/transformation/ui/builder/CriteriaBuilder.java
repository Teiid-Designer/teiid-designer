/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IPredicateCriteria;
import org.teiid.designer.transformation.ui.builder.actions.AndCriteriaAction;
import org.teiid.designer.transformation.ui.builder.actions.NotCriteriaAction;
import org.teiid.designer.transformation.ui.builder.actions.OrCriteriaAction;
import org.teiid.designer.transformation.ui.builder.criteria.CriteriaEditor;
import org.teiid.query.ui.builder.model.CriteriaEditorModel;

/**
 * CriteriaBuilder
 *
 * @since 8.0
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
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectBuilder#createTreeButtons(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createTreeButtons( Composite theParent ) {
        super.createTreeButtons(theParent);

        // add the AndCriteriaAction
        Runnable andRunner = new Runnable() {
            @Override
			public void run() {
                handleAndSelected();
            }
        };
        andAction = new AndCriteriaAction(theParent, andRunner);

        // add the OrCriteriaAction
        Runnable orRunner = new Runnable() {
            @Override
			public void run() {
                handleOrSelected();
            }
        };
        orAction = new OrCriteriaAction(theParent, orRunner);

        // add the NotCriteriaAction
        Runnable notRunner = new Runnable() {
            @Override
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
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectBuilder#createEditorDetails(org.eclipse.swt.widgets.Composite)
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
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectBuilder#getTitle()
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
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectBuilder#setEditorLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
    protected void setEditorLanguageObject( ILanguageObject theEditorLangObj ) {
        if (theEditorLangObj instanceof ICompoundCriteria) {
            getEditor().clear();
        } else if (theEditorLangObj instanceof INotCriteria) {
            setEditorLanguageObject(((INotCriteria)theEditorLangObj).getCriteria());
        } else {
            getEditor().setLanguageObject(theEditorLangObj);
        }
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectBuilder#setEnabledStatus()
     */
    @Override
    protected void setEnabledStatus() {
        super.setEnabledStatus();

        Object selectedObj = getTreeViewer().getSelectedObject();

        // use the object currently in the editor
        setEnabledStatus((selectedObj instanceof ILanguageObject) ? (ILanguageObject)selectedObj : null);
    }

    private void setEnabledStatus( ILanguageObject theLangObj ) {
        if (getTreeViewer().isUndefined(theLangObj)) {
            andAction.setEnabled(false);
            orAction.setEnabled(false);
            notAction.setEnabled(false);
            // Must enable the editor, else cannot define an object
            editor.setEnabled(true);
        } else {
            boolean enableEditor = true;

            if (theLangObj instanceof IPredicateCriteria) {
                andAction.setEnabled(true);
                orAction.setEnabled(true);
                notAction.setEnabled(true);
            } else if (theLangObj instanceof ICompoundCriteria) {
                // if an undefined node already exists don't allow another one
                ICompoundCriteria crit = (ICompoundCriteria)theLangObj;
                notAction.setEnabled(true);

                if (!getTreeViewer().hasUndefinedChild(theLangObj)) {
                    if (crit.getOperator() == ICompoundCriteria.AND) {
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
            } else if (theLangObj instanceof INotCriteria) {
                INotCriteria notCrit = (INotCriteria)theLangObj;
                ICriteria subCrit = notCrit.getCriteria();
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
