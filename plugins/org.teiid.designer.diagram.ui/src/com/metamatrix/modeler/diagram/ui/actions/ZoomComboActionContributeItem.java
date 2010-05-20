/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.ZoomableEditor;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;

/**
 * @since 4.2
 */
public class ZoomComboActionContributeItem extends ActionContributionItem implements ZoomListener {

    private Combo combo;
    private String initString = "100%"; //$NON-NLS-1$
    private ToolItem toolitem;
    private ZoomManager zoomManager;
    private IPartService service;
    private IPartListener partListener;
    boolean isListening;
    private boolean thisIsSource = false;

    /**
     * Constructor for ComboToolItem.
     * 
     * @param partService used to add a PartListener
     */
    public ZoomComboActionContributeItem( IAction action,
                                          IPartService partService ) {
        super(action);
        service = partService;
        Assert.isNotNull(partService);
        partService.addPartListener(partListener = new IPartListener() {
            public void partActivated( IWorkbenchPart part ) {
                if (!isListening && getZoomManager() != null) getZoomManager().addZoomListener(getThis());
            }

            public void partBroughtToTop( IWorkbenchPart p ) {
            }

            public void partClosed( IWorkbenchPart p ) {
                if (isListening && getZoomManager() != null) getZoomManager().removeZoomListener(getThis());
                isListening = false;
            }

            public void partDeactivated( IWorkbenchPart p ) {
                if (isListening && getZoomManager() != null) getZoomManager().removeZoomListener(getThis());
                isListening = false;
            }

            public void partOpened( IWorkbenchPart p ) {
            }
        });
    }

    /**
     * Constructor for ComboToolItem.
     * 
     * @param partService used to add a PartListener
     */
    public ZoomComboActionContributeItem( IAction action ) {
        super(action);
    }

    ZoomListener getThis() {
        return this;
    }

    /**
     * Constructor for ComboToolItem.
     * 
     * @param partService used to add a PartListener
     * @param initString the initial string displayed in the combo
     */
    public ZoomComboActionContributeItem( IAction action,
                                          IPartService partService,
                                          String initString ) {
        super(action);
        this.initString = initString;
        service = partService;
        Assert.isNotNull(partService);
        partService.addPartListener(partListener = new IPartListener() {
            public void partActivated( IWorkbenchPart part ) {
                if (!isListening) getZoomManager().addZoomListener(getThis());
            }

            public void partBroughtToTop( IWorkbenchPart p ) {
            }

            public void partClosed( IWorkbenchPart p ) {
                if (isListening) getZoomManager().removeZoomListener(getThis());
                isListening = false;
            }

            public void partDeactivated( IWorkbenchPart p ) {
                if (isListening) getZoomManager().removeZoomListener(getThis());
                isListening = false;
            }

            public void partOpened( IWorkbenchPart p ) {
            }
        });
    }

    void refresh( ZoomManager zm ) {
        if (combo == null || combo.isDisposed()) return;
        // $TODO GTK workaround
        try {
            if (zm == null) {
                combo.setEnabled(false);
                combo.removeAll();
            } else {
                combo.setItems(zm.getZoomLevelsAsText());
                String zoom = zm.getZoomAsText();
                int index = combo.indexOf(zoom);
                if (index != -1) combo.select(index);
                else combo.setText(zoom);
                combo.setEnabled(true);
            }
        } catch (SWTException exception) {
            if (!SWT.getPlatform().equals("gtk")) //$NON-NLS-1$
            throw exception;
        }
    }

    private ModelEditor getActiveEditor() {
        IWorkbenchPage activePage = DiagramUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage != null) {
            IEditorPart editor = activePage.getActiveEditor();

            if (editor instanceof ModelEditor) {
                return (ModelEditor)editor;
            } // endif -- ME instance
        } // endif

        return null;
    }

    private DiagramEditor getDiagramEditor() {
        ModelEditor editor = getActiveEditor();

        if (editor != null && editor.getCurrentPage() instanceof ZoomableEditor) {

            DiagramEditor deEditorPage = ((ZoomableEditor)editor.getCurrentPage()).getDiagramEditor();
            if (deEditorPage != null) {
                return deEditorPage;
            }
        }

        return null;
    }

    ZoomManager getZoomManager() {
        ZoomManager zm = null;

        ModelEditor editor = getActiveEditor();

        if (editor != null && editor.getCurrentPage() instanceof ZoomableEditor) {

            DiagramEditor deEditorPage = ((ZoomableEditor)editor.getCurrentPage()).getDiagramEditor();
            if (deEditorPage != null) {
                zm = (ZoomManager)deEditorPage.getAdapter(ZoomManager.class);
            }
        }
        return zm;
    }

    /**
     * Computes the width required by control
     * 
     * @param control The control to compute width
     * @return int The width required
     */
    protected int computeWidth( Control control ) {
        return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
    }

    /**
     * @see org.eclipse.jface.action.ControlContribution#createControl(Composite)
     */
    protected Control createControl( Composite parent ) {
        combo = new Combo(parent, SWT.DROP_DOWN);
        combo.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                handleWidgetSelected(e);
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                handleWidgetDefaultSelected(e);
            }
        });
        combo.addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent e ) {
                dispose();
            }
        });

        // Initialize width of combo

        //        if (SWT.getPlatform().equals("gtk")) //$NON-NLS-1$
        combo.setItems(DiagramUiConstants.Zoom.zoomStrings);
        combo.setText(initString);
        toolitem.setWidth(80); //computeWidth(combo) + 10);
        combo.setToolTipText(getAction().getToolTipText());
        return combo;
    }

    /**
     * @see org.eclipse.jface.action.ContributionItem#dispose()
     */
    @Override
    public void dispose() {
        if (partListener == null) return;
        service.removePartListener(partListener);
        if (zoomManager != null) {
            zoomManager.removeZoomListener(this);
            zoomManager = null;
        }
        combo = null;
        partListener = null;
    }

    /**
     * The control item implementation of this <code>IContributionItem</code> method calls the <code>createControl</code>
     * framework method. Subclasses must implement <code>createControl</code> rather than overriding this method.
     * 
     * @param parent The parent of the control to fill
     */
    @Override
    public final void fill( Composite parent ) {
        createControl(parent);
    }

    /**
     * The control item implementation of this <code>IContributionItem</code> method throws an exception since controls cannot be
     * added to menus.
     * 
     * @param parent The menu
     * @param index Menu index
     */
    @Override
    public final void fill( Menu parent,
                            int index ) {
        Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$
    }

    /**
     * The control item implementation of this <code>IContributionItem</code> method calls the <code>createControl</code>
     * framework method to create a control under the given parent, and then creates a new tool item to hold it. Subclasses must
     * implement <code>createControl</code> rather than overriding this method.
     * 
     * @param parent The ToolBar to add the new control to
     * @param index Index
     */
    @Override
    public void fill( ToolBar parent,
                      int index ) {
        toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
        Control control = createControl(parent);
        toolitem.setControl(control);
    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
     */
    void handleWidgetDefaultSelected( SelectionEvent event ) {
        thisIsSource = true;
        ZoomManager zm = getZoomManager();
        zm.setZoomAsText(combo.getText());
        DiagramEditor de = getDiagramEditor();
        if (de != null) {
            de.handleZoomChanged();
        }
        thisIsSource = false;
        refresh(zm);
    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
     */
    void handleWidgetSelected( SelectionEvent event ) {
        thisIsSource = true;
        ZoomManager zm = getZoomManager();
        zm.setZoomAsText(combo.getText());
        DiagramEditor de = getDiagramEditor();
        if (de != null) {
            de.handleZoomChanged();
        }
        thisIsSource = false;
        refresh(zm);
    }

    /**
     * @see ZoomListener#zoomChanged(double)
     */
    public void zoomChanged( double zoom ) {
        if (!thisIsSource && combo != null && !combo.isDisposed()) {
            String zoomString = "" + (int)(zoom * 100) + "%"; //$NON-NLS-1$ //$NON-NLS-2$
            combo.setText(zoomString);
            combo.setEnabled(true);
        }
    }

    public void refreshText() {
        DiagramEditor de = getDiagramEditor();
        if (de != null) {
            zoomChanged(de.getCurrentZoomFactor());
        }

    }

}
