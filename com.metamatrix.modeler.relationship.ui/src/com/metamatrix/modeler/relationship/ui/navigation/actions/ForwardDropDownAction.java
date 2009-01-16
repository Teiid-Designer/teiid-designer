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

package com.metamatrix.modeler.relationship.ui.navigation.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextInfo;
import com.metamatrix.modeler.relationship.NavigationHistory;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationListener;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationView;

public class ForwardDropDownAction extends Action implements IMenuCreator, NavigationListener, UiConstants.Images {

    private static final String LABEL = UiConstants.Util.getString("ForwardDropDownAction.label"); //$NON-NLS-1$
    private static final String TOOLTIP = UiConstants.Util.getString("ForwardDropDownAction.tooltip"); //$NON-NLS-1$

    private NavigationView viewer;
    private Menu menu;
    private NavigationContextInfo nextInfo;
    
    public ForwardDropDownAction(NavigationView viewer) {
        this.viewer= viewer;
        setText(LABEL);
        setToolTipText(TOOLTIP);
        setDisabledImageDescriptor(UiPlugin.getDefault().getImageDescriptor(FORWARD_D)); 
        setHoverImageDescriptor(UiPlugin.getDefault().getImageDescriptor(FORWARD_C));
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(FORWARD_E));

        setMenuCreator(this);
        
        setEnabled(viewer.getNavigationHistory().hasPrevious());
        viewer.addNavigationListener(this);
    }

    public void dispose() {
        if (menu != null)
        menu.dispose();
        
        viewer= null;
    }

    public Menu getMenu(Menu parent) {
        return null;
    }

    public Menu getMenu(Control parent) {
        if (menu != null) {
            menu.dispose();
        }
        
        menu= new Menu(parent);
        NavigationHistory history = viewer.getNavigationHistory();
        if ( history != null ) {
            List list =  history.getForwardInfos();
            if ( list.isEmpty() ) {
                setEnabled(false);
                this.nextInfo = null;
            } else {
                nextInfo = (NavigationContextInfo) list.get(0);
                Iterator iter = list.iterator();
                while ( iter.hasNext() ) {
                    NavigationContextInfo info = (NavigationContextInfo) iter.next();
                    ShowHistoryAction action= new ShowHistoryAction(this.viewer, info);
                    addActionToMenu(menu, action);
                }

                new MenuItem(menu, SWT.SEPARATOR);
                Action clearAction = new ClearHistoryAction(this.viewer);
                addActionToMenu(menu, clearAction);
            }
        }
        
        return menu;
    }

    protected void addActionToMenu(Menu parent, Action action) {
        ActionContributionItem item= new ActionContributionItem(action);
        item.fill(parent, -1);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.relationship.ui.navigation.NavigationListener#navigationChanged(com.metamatrix.modeler.relationship.NavigationContext)
     */
    public void navigationChanged(NavigationContext newContext) {
        NavigationHistory history = viewer.getNavigationHistory();
        if ( history != null ) {
            if ( history.getForwardInfos().isEmpty() ) {
                setEnabled(false);
                nextInfo = null;
                setToolTipText(TOOLTIP);
            } else {
                setEnabled(true);
                nextInfo = (NavigationContextInfo) history.getForwardInfos().get(0);
                setToolTipText(UiConstants.Util.getString("ForwardDropDownAction.contextTooltip", nextInfo.getLabel())); //$NON-NLS-1$)
            }
        }
    }

    @Override
    public void run() {
        this.viewer.setCurrentContext(this.nextInfo);
    }
}
