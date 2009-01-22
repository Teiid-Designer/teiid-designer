/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.logview;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PropertyPage;

import com.metamatrix.ui.UiConstants;

public class LogEntryPropertyPage extends PropertyPage {
    public static final String DATE_TXT = UiConstants.Util.getString("LogView.propertyPage.date"); //$NON-NLS-1$
    public static final String SEVERITY_TXT = UiConstants.Util.getString("LogView.propertyPage.severity"); //$NON-NLS-1$
    public static final String MESSAGE_TXT = UiConstants.Util.getString("LogView.propertyPage.message"); //$NON-NLS-1$
    public static final String EXCEPTION_TXT = UiConstants.Util.getString("LogView.propertyPage.exception"); //$NON-NLS-1$
    private LogViewLabelProvider labelProvider;

    public LogEntryPropertyPage() {
        labelProvider = new LogViewLabelProvider();
        noDefaultAndApplyButton();
    }
    @Override
    protected Control createContents(Composite parent) {
        LogEntry entry = (LogEntry) getElement();
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        container.setLayout(layout);

        Label label = new Label(container, SWT.NULL);
        label.setText(DATE_TXT);
        label = new Label(container, SWT.NULL);
        label.setText(entry.getDate());
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);

        label = new Label(container, SWT.NULL);
        label.setText(SEVERITY_TXT);
        label = new Label(container, SWT.NULL);
        label.setImage(labelProvider.getColumnImage(entry, 1));

        label = new Label(container, SWT.NULL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        label.setText(entry.getSeverityText());
        label.setLayoutData(gd);

        label = new Label(container, SWT.NULL);
        label.setText(MESSAGE_TXT);
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        label.setLayoutData(gd);
        label = new Label(container, SWT.WRAP);
        label.setText(entry.getMessage());
        gd =
            new GridData(
                GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gd.widthHint = computeWidthLimit(label, 80);
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);

        String stack = entry.getStack();
        if (stack != null) {
            label = new Label(container, SWT.NULL);
            label.setText(EXCEPTION_TXT);
            gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 3;
            label.setLayoutData(gd);

            Text text =
                new Text(
                    container,
                    SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.READ_ONLY);
            gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 3;
            gd.widthHint = 300;
            gd.heightHint = 300;
            text.setLayoutData(gd);
            text.setText(stack);
        }
        return container;
    }
    
    @Override
    public void dispose() {
        labelProvider.dispose();
        super.dispose();
    }
    
    private int computeWidthLimit(Label label, int nchars) {
        GC gc = new GC(label);
        gc.setFont(label.getFont());
        FontMetrics fontMetrics= gc.getFontMetrics();
        gc.dispose();
        return Dialog.convertWidthInCharsToPixels(fontMetrics, nchars);
    }
}
