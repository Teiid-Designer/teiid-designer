/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.filter;

import java.util.regex.Pattern;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.ui.UiConstants;

public class StructuredViewerTextFilterer extends StructuredViewerFilterer implements IFilter {

    public static final String DEFAULT_PROMPT = UiConstants.Util.getString("StructuredViewerTextFilterer.defaultPromptText"); //$NON-NLS-1$
    public static final String DEFAULT_CLEAR = UiConstants.Util.getString("StructuredViewerTextFilterer.defaultClearButton"); //$NON-NLS-1$

    private final String promptText;
    private final String clearText;
    private final Image clearIcon;
    private ILabelProvider lProvider;
    Text text;
    private Button clrBtn;
    Pattern filter;

    public StructuredViewerTextFilterer( String promptText,
                                         String clearText ) {
        this(promptText, clearText, null, null);
    }

    public StructuredViewerTextFilterer( String promptText,
                                         String clearText,
                                         ILabelProvider labelProvider ) {
        this(promptText, clearText, null, labelProvider);
    }

    public StructuredViewerTextFilterer( String promptText,
                                         String clearText,
                                         Image clearIcon ) {
        this(promptText, clearText, clearIcon, null);
    }

    public StructuredViewerTextFilterer( String promptText,
                                         String clearText,
                                         Image clearIcon,
                                         ILabelProvider labelProvider ) {
        this.promptText = promptText;
        this.clearText = clearText;
        this.clearIcon = clearIcon;
        lProvider = labelProvider;
    }

    public void setLabelProvider( ILabelProvider provider ) {
        lProvider = provider;
    }

    @Override
    public Control addControl( Composite parent,
                               FormToolkit ftk ) {
        Composite grp;
        if (ftk != null) {
            grp = ftk.createComposite(parent);
        } else {
            grp = new Composite(parent, SWT.NONE);
        } // endif

        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        grp.setLayout(gl);

        if (ftk != null) {
            text = ftk.createText(grp, promptText, SWT.BORDER);
        } else {
            text = new Text(grp, SWT.BORDER);
            text.setText(promptText);
        } // endif
        text.addModifyListener(new MyModifyListener());
        text.addFocusListener(new FocusListener() {
            public void focusGained( FocusEvent e ) {
                // do an invokeLater so that the select all will happen after the focus stuff completes:
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        if (!text.isDisposed()) {
                            text.selectAll();
                        } // endif
                    }
                }); // endanon runnable
            }

            public void focusLost( FocusEvent e ) {
                text.clearSelection();
            }
        }); // endanon FocusListener
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text.selectAll();

        if (ftk != null) {
            clrBtn = ftk.createButton(grp, clearText, SWT.NONE);
        } else {
            clrBtn = new Button(grp, SWT.FLAT);
            clrBtn.setText(clearText);
        } // endif

        if (clearIcon != null) {
            clrBtn.setImage(clearIcon);
        } // endif

        clrBtn.addSelectionListener(new MyButtonListener());

        return grp;
    }

    @Override
    protected ViewerFilter createViewerFilter() {
        return new ViewerFilter() {
            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                return StructuredViewerTextFilterer.this.select(element);
            }
        }; // endanon ViewerFilter
    }

    @Override
    protected IFilter createVirtualFilter() {
        return this;
    }

    public synchronized boolean select( Object toTest ) {
        if (filter != null) {
            // valid filter, use it:
            String itemValue = lProvider.getText(toTest);
            return filter.matcher(itemValue).matches();
        } // endif

        // default, allow:
        return true;
    }

    final class MyButtonListener implements SelectionListener {
        public void widgetSelected( SelectionEvent e ) {
            text.setText(""); //$NON-NLS-1$
            // TODO this should be scheduled after a delay:
            updateFilter();
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
        }
    }

    final class MyModifyListener implements ModifyListener {
        public void modifyText( ModifyEvent e ) {
            String filterText = text.getText();
            synchronized (StructuredViewerTextFilterer.this) {
                if (filterText.length() > 0) {
                    filter = StringUtil.createPattern(filterText + "*", false); //$NON-NLS-1$;
                } else {
                    filter = null; // no filter
                } // endif
            } // endsync

            // filter update will be scheduled after a delay:
            scheduleUpdate();
        }
    }

}
