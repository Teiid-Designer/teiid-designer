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
package com.metamatrix.modeler.diagram.ui.editor;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * DiagramToolBarManager Class designed to provide specific management of diagram specific toobar actions. This includes special
 * wiring so an action can ask for a specific tool item which may be in focuse when pressed. This is to get around the
 * "part activation" problem of selection events being fired on a re-focused part. AddTransformationSourceAction needed to respond
 * to selected objects outside a diagram when the button action (ToolItem) was pressed.
 */
public class DiagramToolBarManager extends ToolBarManager {

    ActionContributionItem focusedToolItem;

    public DiagramToolBarManager( ToolBar paneToolBar ) {
        super(paneToolBar);

        // Required to provide actions the ability to know that it's item may
        // have been selected
        paneToolBar.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove( MouseEvent e ) {
                Point thisPoint = new Point(e.x, e.y);
                setFocusedToolItem(thisPoint);
            }
        });

        // Required to flush the focusedToolItem when entering or exiting the toolbar
        paneToolBar.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseEnter( MouseEvent e ) {
                focusedToolItem = null;
            }

            @Override
            public void mouseExit( MouseEvent e ) {
                focusedToolItem = null;
            }
        });
    }

    void setFocusedToolItem( Point currentPoint ) {
        focusedToolItem = getActionContributionItem(currentPoint);
    }

    public void resetFocusedToolItem() {
        focusedToolItem = null;
    }

    public ActionContributionItem getFocusedToolItem() {
        return focusedToolItem;
    }

    public ActionContributionItem getActionContributionItem( AbstractAction theAction ) {
        if (getControl() != null && getControl().getItemCount() > 0) {
            IContributionItem[] items = getItems();
            IContributionItem toolItem = null;
            ActionContributionItem nextACI = null;
            AbstractAction nextAction = null;
            for (int i = 0; i < getControl().getItems().length; i++) {
                toolItem = items[i];
                nextACI = (ActionContributionItem)toolItem;
                nextAction = (AbstractAction)nextACI.getAction();
                if (nextAction.equals(theAction)) {
                    return nextACI;
                }
            }
        }

        return null;
    }

    public ActionContributionItem getActionContributionItem( Point somePoint ) {
        int itemIndex = getToolItemIndex(somePoint);
        if (itemIndex < 0) return null;
        return getActionContributionItem(itemIndex);
    }

    public AbstractAction getAction( int toolItemIndex ) {
        if (toolItemIndex >= 0) {
            IContributionItem[] items = getItems();
            if (toolItemIndex < items.length) return (AbstractAction)((ActionContributionItem)items[toolItemIndex]).getAction();
        }
        return null;
    }

    public ToolItem getToolItem( Point point ) {

        if (getControl() != null && getControl().getItemCount() > 0) {
            ToolItem toolItem = null;

            for (int i = 0; i < getControl().getItems().length; i++) {
                toolItem = getControl().getItem(i);
                if (toolItem != null && toolItem.getBounds().contains(point)) {
                    return toolItem;
                }
            }
        }
        return null;
    }

    public ActionContributionItem getActionContributionItem( int toolItemIndex ) {
        if (getControl() != null && getControl().getItemCount() > 0 && toolItemIndex >= 0) {
            IContributionItem[] items = getItems();
            ActionContributionItem nextACI = null;

            if (items[toolItemIndex] instanceof ActionContributionItem) {
                nextACI = (ActionContributionItem)items[toolItemIndex];

                return nextACI;
            }
        }

        return null;
    }

    public int getToolItemIndex( Point point ) {

        if (getControl() != null && getControl().getItemCount() > 0) {
            ToolItem toolItem = null;

            for (int i = 0; i < getControl().getItems().length; i++) {
                toolItem = getControl().getItem(i);
                if (toolItem != null && toolItem.getBounds().contains(point)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    protected void relayout( ToolBar toolBar,
                             int oldCount,
                             int newCount ) {
        toolBar.layout();
        Composite parent = toolBar.getParent();
        parent.layout();
        if (parent.getParent() != null) parent.getParent().layout();
    }
}
