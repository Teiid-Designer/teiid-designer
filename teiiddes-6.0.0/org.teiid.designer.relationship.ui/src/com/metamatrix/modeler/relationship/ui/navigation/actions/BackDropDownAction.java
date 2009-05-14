/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.actions;

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

public class BackDropDownAction extends Action implements IMenuCreator, NavigationListener, UiConstants.Images {

    private static final String LABEL = UiConstants.Util.getString("BackDropDownAction.label"); //$NON-NLS-1$
    private static final String TOOLTIP = UiConstants.Util.getString("BackDropDownAction.tooltip"); //$NON-NLS-1$

    private NavigationView viewer;
    private Menu menu;
    private NavigationContextInfo backInfo;
    
    public BackDropDownAction(NavigationView viewer) {
        this.viewer= viewer;
        setText(LABEL);
        setToolTipText(TOOLTIP);
        setDisabledImageDescriptor(UiPlugin.getDefault().getImageDescriptor(BACK_D)); 
        setHoverImageDescriptor(UiPlugin.getDefault().getImageDescriptor(BACK_C));
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(BACK_E));

        setMenuCreator(this);
        
        setEnabled(viewer.getNavigationHistory().hasNext());
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
            List list =  history.getBackInfos();
            if ( list.isEmpty() ) {
                setEnabled(false);
                this.backInfo = null;
            } else {
                int index = list.size() - 1;
                backInfo = (NavigationContextInfo) list.get(index);
                for ( --index ; index >=0 ; --index ) {
                    NavigationContextInfo info = (NavigationContextInfo) list.get(index);
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

    @Override
    public void run() {
        this.viewer.setCurrentContext(this.backInfo);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.relationship.ui.navigation.NavigationListener#navigationChanged(com.metamatrix.modeler.relationship.NavigationContext)
     */
    public void navigationChanged(NavigationContext newContext) {
        NavigationHistory history = viewer.getNavigationHistory();
        if ( history != null ) {
            if ( history.getBackInfos().isEmpty() ) {
                setEnabled(false);
                backInfo = null;
                setToolTipText(TOOLTIP);
            } else {
                setEnabled(true);
                int index = history.getBackInfos().size() - 1;
                backInfo = (NavigationContextInfo) history.getBackInfos().get(index);
                setToolTipText(UiConstants.Util.getString("BackDropDownAction.contextTooltip", backInfo.getLabel())); //$NON-NLS-1$)
            }
        }
    }

}
