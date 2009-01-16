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
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.util.Arrays;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.ui.editors.IRevertable;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * @since 4.0
 */
public class VdbEditorIndexPage extends EditorPart implements VdbUiConstants, VdbEditor.Constants, IRevertable, IGotoMarker {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbEditorIndexPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 2;

    private static final String CONTENT_LABEL = getString("contentLabel"); //$NON-NLS-1$
    private static final String INDEX_LABEL = getString("indexLabel"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    VdbEditor editor;

    Combo ndxCombo;
    private Text contentBox;

    /**
     * @since 4.0
     */
    public VdbEditorIndexPage( final VdbEditor editor ) {
        ArgCheck.isNotNull(editor);
        this.editor = editor;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    public void createPartControl( final Composite parent ) {
        final Composite pg = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, COLUMN_COUNT);
        WidgetFactory.createLabel(pg, INDEX_LABEL);

        this.ndxCombo = WidgetFactory.createCombo(pg,
                                                  SWT.READ_ONLY,
                                                  GridData.HORIZONTAL_ALIGN_FILL,
                                                  Arrays.asList(this.editor.getContext().getIndexNames()));

        this.ndxCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                indexSelected();
            }
        });

        final ViewForm contentForm = WidgetFactory.createViewForm(pg, SWT.NONE, GridData.FILL_BOTH, COLUMN_COUNT);
        contentForm.setTopLeft(WidgetFactory.createLabel(contentForm, CONTENT_LABEL));
        this.contentBox = WidgetFactory.createTextBox(contentForm);
        contentBox.setEditable(false);
        contentForm.setContent(this.contentBox);

        if (this.ndxCombo.getItemCount() > 0) {
            this.ndxCombo.select(0);
            indexSelected();
        }
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.0
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
        indexSelected();
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     * @since 4.0
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     * @since 4.0
     */
    public void gotoMarker( final IMarker marker ) {
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 4.0
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) throws PartInitException {
        if (input != null && !(input instanceof IFileEditorInput)) {
            throw new PartInitException(INVALID_INPUT_MESSAGE);
        }
        setSite(site);
        setInput(input);
        setPartName(TITLE);
    }

    /**
     * @return False.
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     * @since 4.0
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * @return False.
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     * @since 4.0
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 4.0
     */
    @Override
    public void setFocus() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (editor.getContext() != null && editor.getContext().isOpen()) {
                    if (ndxCombo != null && !ndxCombo.isDisposed()) {
                        ndxCombo.setFocus();
                    }
                }
            }
        });
    }

    public void doRevertToSaved() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                // defect 18303 - make sure open and visible:
                if (editor.getContext() != null && editor.getContext().isOpen() && ndxCombo != null && !ndxCombo.isDisposed()) {
                    indexSelected();
                }
            }
        });
    }

    /**
     * @since 4.0
     */
    void indexSelected() {
        final String name = this.ndxCombo.getText();
        if (name == null || name.length() == 0) {
            return;
        }
        final String content = this.editor.getContext().getIndexContent(name);
        this.contentBox.setText(content);
    }
}
