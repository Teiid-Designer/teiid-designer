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
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.modeler.internal.ui.forms.DialogProvider;

public class FacetValueEditor implements DialogProvider {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private FacetValueDialog dlg;
    private boolean wasCancelled;

    public void showDialog( Shell shell,
                            Object initialValue ) {
        FacetValue fv;
        if (initialValue instanceof FacetValue) {
            fv = (FacetValue)initialValue;
        } else {
            // make a new one:
            fv = new FacetValue();
            fv.value = initialValue;
        } // endif

        if (fv.value == null) {
            fv.value = EMPTY_STRING;
        } // endif

        if (fv.description == null) {
            fv.description = EMPTY_STRING;
        } // endif

        dlg = new FacetValueDialog(shell, GUIFacetHelper.getString("FacetValueEditor.title"), fv); //$NON-NLS-1$
        dlg.setBlockOnOpen(true);
        wasCancelled = dlg.open() == Window.CANCEL;
    }

    public boolean wasCancelled() {
        return wasCancelled;
    }

    public Object getValue() {
        if (wasCancelled || dlg == null) {
            // cancelled or not init, return nothing:
            return null;
        } // endif

        return dlg.getValue();
    }

    public String getLaunchButtonText() {
        return GUIFacetHelper.getString("FacetValueEditor.launch"); //$NON-NLS-1$
    }

    public static class FacetValueDialog extends Dialog {
        final FacetValue fv;
        private final String title;

        protected FacetValueDialog( Shell shell,
                                    String title,
                                    FacetValue value ) {
            super(shell);
            this.title = title;
            fv = value;
            setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());

        }

        @Override
        protected void configureShell( Shell shell ) {
            super.configureShell(shell);
            if (title != null) shell.setText(title);
        }

        @Override
        protected Control createDialogArea( Composite parent ) {
            Composite composite = (Composite)super.createDialogArea(parent);
            GridLayout gl = (GridLayout)composite.getLayout();
            gl.numColumns = 2;

            // value:
            Label l = new Label(composite, SWT.NONE);
            l.setText(GUIFacetHelper.getString("FacetValueEditor.value")); //$NON-NLS-1$
            l.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
            final Text value = new Text(composite, SWT.BORDER);
            value.setText((String)fv.value);
            value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            value.addModifyListener(new ModifyListener() {
                public void modifyText( ModifyEvent e ) {
                    fv.value = value.getText();
                }
            });

            // description:
            l = new Label(composite, SWT.NONE);
            l.setText(GUIFacetHelper.getString("FacetValueEditor.desc")); //$NON-NLS-1$
            l.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));

            final Text desc = new Text(composite, SWT.BORDER);// | SWT.MULTI);
            desc.setText(fv.description);
            desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            desc.addModifyListener(new ModifyListener() {
                public void modifyText( ModifyEvent e ) {
                    fv.description = desc.getText();
                }
            });

            return composite;
        }

        public FacetValue getValue() {
            return fv;
        }
    }
}
