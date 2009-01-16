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
package com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.views.IPreviewDataContentProvider;
import com.metamatrix.modeler.internal.dqp.ui.views.PreviewDataView;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class PrunePreviewResultsDialog extends TitleAreaDialog implements DqpUiConstants, ISelectionChangedListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(PrunePreviewResultsDialog.class);

    private Button btnClose;

    private Button btnClear;

    private final IPreviewDataContentProvider contentProvider;

    private final ILabelProvider labelProvider;

    private final int maxAllowed;

    private final EObject previewObject;

    private TableViewer viewer;

    public PrunePreviewResultsDialog( PreviewDataView view,
                                      int maxAllowed,
                                      EObject previewObject ) {
        super(null /* view.getSite().getShell() */);
        this.contentProvider = view.createContentProvider(previewObject);
        this.labelProvider = view.getLabelProvider();
        this.maxAllowed = maxAllowed;
        this.previewObject = previewObject;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#close()
     * @since 5.5.3
     */
    @Override
    public boolean close() {
        return super.close();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     * @since 5.5.3
     */
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected Control createButtonBar( Composite parent ) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(OK).setText(UTIL.getString(PREFIX + "previewButton.text")); //$NON-NLS-1$
        updateState();

        return buttonBar;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite dialogArea = WidgetFactory.createGroup((Composite)super.createDialogArea(parent),
                                                         UTIL.getString(PREFIX + "resultsGroupTitle"), //$NON-NLS-1$
                                                         GridData.FILL_BOTH,
                                                         1,
                                                         2);
        createList(dialogArea);

        Composite pnlButtons = WidgetFactory.createPanel(dialogArea, SWT.NONE);
        this.btnClose = WidgetFactory.createButton(pnlButtons,
                                                   UTIL.getString(PREFIX + "closeResultsButton.text"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        this.btnClose.setToolTipText(UTIL.getString(PREFIX + "closeResultsButton.toolTip")); //$NON-NLS-1$
        this.btnClose.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleCloseResults();
            }
        });

        this.btnClear = WidgetFactory.createButton(pnlButtons,
                                                   UTIL.getString(PREFIX + "clearResultsButton.text"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        this.btnClear.setToolTipText(UTIL.getString(PREFIX + "clearResultsButton.toolTip")); //$NON-NLS-1$
        this.btnClear.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleCloseAllResults();
            }
        });

        StyledText note = new StyledText(dialogArea, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
        note.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        note.setText(UTIL.getString(PREFIX + "preferenceNote")); //$NON-NLS-1$

        setTitle(UTIL.getString(PREFIX + "header")); //$NON-NLS-1$

        return dialogArea;
    }

    private void createList( Composite parent ) {
        this.viewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
        this.viewer.addSelectionChangedListener(this);
        this.viewer.setLabelProvider(this.labelProvider);
        this.viewer.setContentProvider(this.contentProvider);
        this.viewer.setInput(this);

        // configure table
        Table table = this.viewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    void handleCloseAllResults() {
        this.contentProvider.clearAllResults();
        this.viewer.refresh();
        updateState();
    }

    void handleCloseResults() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        this.contentProvider.removeResults(selection.toArray());
        this.viewer.refresh();
        updateState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @since 5.5.3
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        updateState();
    }

    private void updateState() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        int count = this.contentProvider.getElements(null).length;
        int excessCount = count + 1 - this.maxAllowed;
        boolean enablePreview = (excessCount <= 0);

        this.btnClose.setEnabled(!selection.isEmpty());
        this.btnClear.setEnabled(count > 0);
        getButton(OK).setEnabled(enablePreview);

        if (enablePreview) {
            setErrorMessage(null);
            setMessage(UTIL.getString(PREFIX + "okMsg", this.labelProvider.getText(this.previewObject))); //$NON-NLS-1$
        } else {
            setErrorMessage(UTIL.getString(PREFIX + "errorMsg", Integer.valueOf(excessCount))); //$NON-NLS-1$
        }
    }
}
