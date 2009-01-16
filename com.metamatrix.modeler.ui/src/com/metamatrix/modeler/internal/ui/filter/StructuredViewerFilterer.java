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
package com.metamatrix.modeler.internal.ui.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.deferred.DeferredContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class StructuredViewerFilterer {

    public static final int DEFAULT_FILTER_DELAY_TIME = 400;

    private ViewerFilter myVFilter;
    private IFilter myIFilter;
    private Map currentViewers = new HashMap();
    Timer lastTimer;
    private int delayMillis = DEFAULT_FILTER_DELAY_TIME;

    public abstract Control addControl( Composite parent,
                                        FormToolkit ftk );

    protected abstract ViewerFilter createViewerFilter();

    protected abstract IFilter createVirtualFilter();

    private void init( boolean virtual ) {
        if (virtual) {
            if (myIFilter == null) {
                myIFilter = createVirtualFilter();
            } // endif

        } else {
            // not virtual:
            if (myVFilter == null) {
                myVFilter = createViewerFilter();
            } // endif
        } // endif
    }

    /** Convenience method for when you don't have a FormToolKit */
    public Control addControl( Composite parent ) {
        return addControl(parent, null);
    }

    public void attachToViewer( StructuredViewer viewer,
                                boolean autoSelectFirstMatch ) {
        init(false);
        viewer.addFilter(myVFilter);
        currentViewers.put(viewer, new ViewerInfo(viewer, null, autoSelectFirstMatch));
    }

    public void attachToVirtualViewer( StructuredViewer viewer,
                                       DeferredContentProvider dcp,
                                       boolean autoSelectFirstMatch ) {
        init(true);
        currentViewers.put(viewer, new ViewerInfo(viewer, dcp, autoSelectFirstMatch));
    }

    public void removeFromViewer( StructuredViewer viewer ) {
        init(false);
        viewer.removeFilter(myVFilter);
        currentViewers.remove(viewer);
    }

    public void updateFilter() {
        Iterator itor = currentViewers.values().iterator();
        while (itor.hasNext()) {
            final ViewerInfo vi = (ViewerInfo)itor.next();
            if (vi.viewer.getControl().isDisposed()) continue;

            if (vi.dcp == null) {
                // non-virtual:
                vi.viewer.refresh();
                autoSelectIfNeeded(vi);

            } else {
                // virtual:
                vi.dcp.setFilter(myIFilter);
                // schedule a selection in a little while:
                Thread selector = new Thread("filter selector") {//$NON-NLS-1$
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(300);
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    autoSelectIfNeeded(vi);
                                }
                            }); // endAnon Runnable
                        } catch (InterruptedException err) {
                        } // endtry
                    }
                }; // endanon Thread

                selector.setDaemon(true);
                selector.setPriority(Thread.NORM_PRIORITY - 2);
                selector.start();
            } // endif
        } // endwhile -- all viewers
    }

    static void autoSelectIfNeeded( ViewerInfo vi ) {
        if (vi.autoSelect && vi.viewer.getSelection().isEmpty()) {
            // we want to auto-select, and there is no current selection, so
            // select the first entry:
            Control c = vi.viewer.getControl();
            Object toSelect = null;
            if (c instanceof Table) {
                Table t = (Table)c;
                if (t.getItemCount() > 0) {
                    toSelect = t.getItem(0).getData();
                } // endif -- has items
            } else if (c instanceof List) {
                List l = (List)c;
                if (l.getItemCount() > 0) {
                    toSelect = l.getItem(0);
                } // endif -- has items
            } else if (c instanceof Tree) {
                Tree t = (Tree)c;
                if (t.getItemCount() > 0) {
                    toSelect = t.getTopItem().getData();
                } // endif -- has items
            } // endif -- instanceof switch

            if (toSelect != null) {
                // we have something to select:
                vi.viewer.setSelection(new StructuredSelection(toSelect));
            } // endif
        } // endif -- autoselect
    }

    public void setDelayTime( int millis ) {
        delayMillis = millis;
    }

    public void scheduleUpdate() {
        // cancel last update request, if present:
        if (lastTimer != null) {
            lastTimer.cancel();
            lastTimer = null;
        } // endif

        if (delayMillis > 0) {
            // schedule an update for specified milliseconds later:
            lastTimer = new Timer(true);
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            updateFilter();
                        }
                    });
                    if (lastTimer != null) {
                        lastTimer.cancel();
                    }
                    lastTimer = null;
                }
            };
            lastTimer.schedule(tt, delayMillis);

        } else {
            // delay set to 0 or less; run right away:
            updateFilter();

        } // endif
    }

    private static class ViewerInfo {
        StructuredViewer viewer;
        DeferredContentProvider dcp;
        boolean autoSelect;

        public ViewerInfo( StructuredViewer viewer,
                           DeferredContentProvider dcp,
                           boolean select ) {
            this.viewer = viewer;
            this.dcp = dcp;
            this.autoSelect = select;
        }
    }
}
