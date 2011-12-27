/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.views;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredList;
import org.teiid.language.SQLConstants;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * SQLReservedWordsView is the View to display all available SQL ReservedWords in Designer.
 */
public class SQLReservedWordsView extends ModelerView {

    private static final String TEXT_ENTRY_LABEL_TEXT = UiConstants.Util.getString("SQLReservedWordsPanel.textEntryLabel.text"); //$NON-NLS-1$
    private static final String UPPER_TABLE_LABEL_TEXT = UiConstants.Util.getString("SQLReservedWordsPanel.upperTableLabel.text"); //$NON-NLS-1$

    private Object[] fElements = null; // List to contain all SQL Reserved Words
    private FilteredList fFilteredList; // Filtered List based on filter selection
    private ILabelProvider fFilterRenderer; // List renderer for SQL Words

    Text fFilterText; // entry for filter string
    // Filter properties
    private boolean fIgnoreCase = true;
    private boolean fAllowDuplicates = false;
    private boolean fMatchEmptyString = true;

    /**
     * Construct SQLReservedWords View for viewing all available SQL Reserved words.
     */
    public SQLReservedWordsView() {
        super();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( final Composite parent ) {

        super.createPartControl(parent);

        GridLayout gridLayout = new GridLayout();
        parent.setLayout(gridLayout);
        GridData gridData1 = new GridData(GridData.FILL_BOTH);
        parent.setLayoutData(gridData1);

        createLabel(parent, TEXT_ENTRY_LABEL_TEXT);
        createFilterText(parent);
        createLabel(parent, UPPER_TABLE_LABEL_TEXT);
        createFilteredList(parent);

        fElements = getAllSQLReservedWords();
        setListElements(fElements);
    }

    private Object[] getAllSQLReservedWords() {
        return SQLConstants.getReservedWords().toArray();
    }

    /**
     * Sets the elements of the list (widget). To be called within open().
     * 
     * @param elements the elements of the list.
     */
    protected void setListElements( Object[] elements ) {
        Assert.isNotNull(fFilteredList);
        fFilteredList.setElements(elements);
    }

    /**
     * Creates a label if name was not <code>null</code>.
     * 
     * @param parent the parent composite.
     * @param name the name of the label.
     * @return returns a label if a name was given, <code>null</code> otherwise.
     */
    protected Label createLabel( Composite parent,
                                 String name ) {
        if (name == null) return null;

        Label label = new Label(parent, SWT.NONE);
        label.setText(name);
        label.setFont(parent.getFont());

        return label;
    }

    /**
     * Creates Text Widget for Filter string entry
     * 
     * @param parent the parent composite.
     * @return returns Text Widget.
     */
    protected Text createFilterText( Composite parent ) {
        Text text = new Text(parent, SWT.BORDER);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        text.setLayoutData(data);
        text.setFont(parent.getFont());

        // Initial filter text is empty
        text.setText(""); //$NON-NLS-1$

        Listener listener = new Listener() {
            @Override
            public void handleEvent( Event e ) {
                fFilteredList.setFilter(fFilterText.getText());
            }
        };
        text.addListener(SWT.Modify, listener);

        fFilterText = text;

        return text;
    }

    /**
     * Creates a filtered list for the Filtered SQL Reserved Words.
     * 
     * @param parent the parent composite.
     * @return returns the filtered list widget.
     */
    private FilteredList createFilteredList( Composite parent ) {
        int flags = SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE;

        fFilterRenderer = new LabelProvider();
        FilteredList list = new FilteredList(parent, flags, fFilterRenderer, fIgnoreCase, fAllowDuplicates, fMatchEmptyString);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        list.setLayoutData(data);
        list.setFont(parent.getFont());
        list.setFilter(""); //$NON-NLS-1$

        fFilteredList = list;

        return list;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
    }

}
