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

package com.metamatrix.query.ui.sqleditor;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.query.internal.ui.sqleditor.SqlEditorInternalEvent;
import com.metamatrix.query.internal.ui.sqleditor.actions.ImportFromFile;
import com.metamatrix.query.internal.ui.sqleditor.actions.LaunchCriteriaBuilder;
import com.metamatrix.query.internal.ui.sqleditor.actions.LaunchExpressionBuilder;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;


/**
 * SqlEditorPanelWrapper
 *   This serves as an adapter between a single set of Actions and multiple sets of
 *   SqlEditorPanels.
 */
public class SqlEditorPanelWrapper extends SqlEditorPanel
                                implements EventObjectListener {

    private SqlEditorPanel sepCurrentSqlEditorPanel;

    /**
     * Construct an instance of SqlEditorPanelWrapper.
     * @param parent
     * @param qmi
     */
    public SqlEditorPanelWrapper(Composite parent, QueryValidator queryValidator) {
        super(parent, queryValidator, QueryValidator.SELECT_TRNS);
    }

    public SqlEditorPanelWrapper(Composite parent, QueryValidator queryValidator, List actionList) {
        super(parent, queryValidator, QueryValidator.SELECT_TRNS, actionList);
    }


    public void setCurrentSqlEditorPanel( SqlEditorPanel sep ) {

        // when the current editor changes, remove/add interanal event listening

        // remove listening from old editor
        if ( sepCurrentSqlEditorPanel != null ) {
            sepCurrentSqlEditorPanel.removeInternalEventListener( this );
        }

        // update the editor var
        this.sepCurrentSqlEditorPanel = sep;

        // add listening to new editor
        sepCurrentSqlEditorPanel.addInternalEventListener( this );

        // point the font manager to the current editor's text viewer
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                refreshFontManager();
            }
        }, true);


    }

    public void refreshFontManager() {


        if ( getFontManager() != null ) {
            getFontManager().setViewer( getCurrentSqlEditorPanel().getTextViewer() );
            getFontManager().updateTextWidget();
        }
    }

    public SqlEditorPanel getCurrentSqlEditorPanel() {

        return sepCurrentSqlEditorPanel;
    }

    @Override
    public boolean canUseCriteriaBuilder() {
        return getCurrentSqlEditorPanel().canUseCriteriaBuilder();
    }

    @Override
    public void showCriteriaBuilder() {
         getCurrentSqlEditorPanel().showCriteriaBuilder();
    }

    @Override
    public boolean canUseExpressionBuilder() {
        return getCurrentSqlEditorPanel().canUseExpressionBuilder();
    }

    @Override
    public boolean canOptimize() {
        SqlEditorPanel sqlPanel = getCurrentSqlEditorPanel();
        return sqlPanel.canOptimize();
    }

    @Override
    public boolean isOptimizerOn() {
        return getCurrentSqlEditorPanel().isOptimizerOn();
    }

    @Override
    public void setOptimizerOn(boolean onStatus) {
        getCurrentSqlEditorPanel().setOptimizerOn(onStatus);
    }

    @Override
    public void showExpressionBuilder() {
        getCurrentSqlEditorPanel().showExpressionBuilder();
    }

    @Override
    public void showMessageArea( boolean show ) {
        if ( getCurrentSqlEditorPanel() != null ) {
            getCurrentSqlEditorPanel().showMessageArea( show );
        }
    }

    @Override
    public boolean isMessageAreaVisible() {
        return getCurrentSqlEditorPanel().isMessageAreaVisible( );
    }

    @Override
    public void validate() {
        getCurrentSqlEditorPanel().validate();
    }

    @Override
    public boolean isEditable() {
        return getCurrentSqlEditorPanel().isEditable();
    }

    @Override
    public boolean isParsable() {
        return getCurrentSqlEditorPanel().isParsable();
    }

    @Override
    public boolean hasPendingChanges() {
        return getCurrentSqlEditorPanel().hasPendingChanges();
    }

    @Override
    public boolean canExpandCurrentSelect() {
        return getCurrentSqlEditorPanel().canExpandCurrentSelect();
    }

    @Override
    public void expandCurrentSelect() {
        getCurrentSqlEditorPanel().expandCurrentSelect();
    }

    @Override
    public void exportToFile() {
        getCurrentSqlEditorPanel().exportToFile();
    }

    @Override
    public void importFromFile() {
        getCurrentSqlEditorPanel().importFromFile();
    }


    @Override
    public void setMessage (String messageText) {
        getCurrentSqlEditorPanel().setMessage( messageText );
    }


    /**
     * handle preference change.  This only responds to change in formatting preference.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String propStr = e.getProperty();
        if(propStr!=null && ( propStr.equals(UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE) ||
                              propStr.equals(UiConstants.Prefs.INDENT_CLAUSE_CONTENT) ) ) {
            if(!this.isDisposed() && this.isVisible()) {
                getCurrentSqlEditorPanel().setText(getCurrentSqlEditorPanel().getText());
            }
        }
    }

    public void processEvent( EventObject e ) {
        //------------------------------------------------
        // respond to internal events from SqlEditorPanel
        // - responds to CARET_CHANGED
        //------------------------------------------------
        if (e instanceof SqlEditorInternalEvent) {
            fireEditorInternalEvent( ((SqlEditorInternalEvent)e).getType() );
        }
    }

    public void updateReadOnlyState(boolean isReadOnly) {
        if( getLaunchCriteriaBuilderAction() != null )
            getLaunchCriteriaBuilderAction().setEnabled(!isReadOnly);
        if( getLaunchExpressionBuilderAction() != null )
            getLaunchExpressionBuilderAction().setEnabled(!isReadOnly);
        if( getImportFromFileAction() != null )
            getImportFromFileAction().setEnabled(!isReadOnly);
    }

    private LaunchCriteriaBuilder getLaunchCriteriaBuilderAction() {
        Iterator iter = getActions().iterator();
        Object nextObj = null;
        while( iter.hasNext()) {
            nextObj = iter.next();
            if( nextObj instanceof LaunchCriteriaBuilder) {
                return (LaunchCriteriaBuilder)nextObj;
            }
        }
        return null;
    }

    private LaunchExpressionBuilder getLaunchExpressionBuilderAction() {
        Iterator iter = getActions().iterator();
        Object nextObj = null;
        while( iter.hasNext()) {
            nextObj = iter.next();
            if( nextObj instanceof LaunchExpressionBuilder) {
                return (LaunchExpressionBuilder)nextObj;
            }
        }
        return null;
    }

    private ImportFromFile getImportFromFileAction() {
        Iterator iter = getActions().iterator();
        Object nextObj = null;
        while( iter.hasNext()) {
            nextObj = iter.next();
            if( nextObj instanceof ImportFromFile) {
                return (ImportFromFile)nextObj;
            }
        }
        return null;
    }
}
