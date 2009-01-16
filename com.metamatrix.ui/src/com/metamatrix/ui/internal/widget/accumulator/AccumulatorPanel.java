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

package com.metamatrix.ui.internal.widget.accumulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.OrderableViewerSorter;
import com.metamatrix.ui.internal.widget.MessageLabel;

/**
 * AccumulatorPanel The panel contains two display columns with a column of buttons between them. The lefthand, or "available"
 * column is displayed through an interface so that a List, Table, or Tree can be accomodated. The righthand, or "selected" column
 * is displayed by this panel through a one-column Table. The buttons are move-right, move-all-right, move-left, and
 * move-all-left. In addition up and down buttons apply to the contents of the right side table.
 */
public class AccumulatorPanel extends Composite implements InternalUiConstants {
    // ============================================================
    // Static variables
    // ============================================================
    final public static int DEFAULT_RESET_BUTTON_MARGIN_HEIGHT = 10;
    final public static int DEFAULT_LEFT_SIDE_WIDTH_HINT = (int)(Display.getDefault().getBounds().width * .25);
    final public static int DEFAULT_RIGHT_SIDE_WIDTH_HINT = DEFAULT_LEFT_SIDE_WIDTH_HINT;
    final public static int DEFAULT_LEFT_AND_RIGHT_SIDE_HEIGHT_HINT = (int)(Display.getDefault().getBounds().height * .10);

    // ============================================================
    // Instance variables
    // ============================================================
    private IAccumulatorSource source;
    private ILabelProvider labelProvider = null;
    private Table rightTable;
    private TableViewer tableViewer;
    private Collection /*<Object>*/initialRightSideItems;
    private String leftSideLabelText = null;
    private String rightSideLabelText = null;
    private Button rightButton;
    private Button leftButton;
    private Button allRightButton;
    private Button allLeftButton;
    private Button upButton;
    private Button downButton;
    private Button resetButton;
    private Button sortButton;
    private int resetButtonMarginHeight;
    private int leftSideWidthHint;
    private int rightSideWidthHint;
    private int leftAndRightSideHeightHint;
    private boolean isSorting = false;
    private boolean ignoreLeftSideSelectionChanges = false;
    private boolean ignoreRightSideSelectionChanges = false;
    private java.util.List /*<IAccumulatedValuesChangeListener>*/changeListeners;
    private MessageLabel messageLabel;

    // ============================================================
    // Constructors
    // ============================================================
    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     * @param source Provider of data for "available" column, and provider of control to display the column
     * @param initialRightSideItemsColl Items initially displayed in the "selected" column
     * @param labelProvider Optional ILabelProvider; if this argument is non-null, then this label provider will be used to
     *        display the right-hand side list (actually a one-column table); if null, the toString() of each object will be used,
     *        with no image.
     * @param leftSideLabelText Optional left side column label. Default if null is "Available".
     * @param rightSideLabelText Optional right side column label. Default if null is "Selected".
     * @param resetButtonMarginHeight Display margin height in pixels for the Reset button. If < 1, default value is used.
     * @param leftSideWidthHint Width hint for left side control. If < 1, default value is used.
     * @param rightSideWidthHint Width hint for right side control. If < 1, default value is used.
     * @param leftAndRightSideHeightHint Height hint for both the left and right side controls. If < 1, default value is used.
     */
    public AccumulatorPanel( Composite parent,
                             IAccumulatorSource source,
                             Collection /*<Object>*/initialRightSideItemsColl,
                             ILabelProvider labelProvider,
                             String leftSideLabelText,
                             String rightSideLabelText,
                             int resetButtonMarginHeight,
                             int leftSideWidthHint,
                             int rightSideWidthHint,
                             int leftAndRightSideHeightHint ) {
        super(parent, SWT.NONE);
        this.source = source;
        this.labelProvider = labelProvider;
        this.initialRightSideItems = initialRightSideItemsColl;
        this.leftSideLabelText = leftSideLabelText;
        this.rightSideLabelText = rightSideLabelText;
        if (resetButtonMarginHeight < 0) {
            this.resetButtonMarginHeight = DEFAULT_RESET_BUTTON_MARGIN_HEIGHT;
        } else {
            this.resetButtonMarginHeight = resetButtonMarginHeight;
        }
        if (leftSideWidthHint < 1) {
            this.leftSideWidthHint = DEFAULT_LEFT_SIDE_WIDTH_HINT;
        } else {
            this.leftSideWidthHint = leftSideWidthHint;
        }
        if (rightSideWidthHint < 1) {
            this.rightSideWidthHint = DEFAULT_RIGHT_SIDE_WIDTH_HINT;
        } else {
            this.rightSideWidthHint = rightSideWidthHint;
        }
        if (leftAndRightSideHeightHint < 1) {
            this.leftAndRightSideHeightHint = DEFAULT_LEFT_AND_RIGHT_SIDE_HEIGHT_HINT;
        } else {
            this.leftAndRightSideHeightHint = leftAndRightSideHeightHint;
        }
        init();
    }

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     * @param source Provider of data for "available" column, and provider of control to display the column
     * @param initialRightSideItemsColl items initially displayed in the "selected" column
     * @param labelProvider optional ILabelProvider; if this argument is non-null, then this label provider will be used to
     *        display the right-hand side list (actually a one-column table); if null, the toString() of each object will be used,
     *        with no image.
     * @param leftSideLabelText optional left side column label. Default if null is "Available".
     * @param rightSideLabelText optional right side column label. Default if null is "Selected".
     */
    public AccumulatorPanel( Composite parent,
                             IAccumulatorSource source,
                             Collection /*<Object>*/initialRightSideItemsColl,
                             ILabelProvider labelProvider,
                             String leftSideLabelText,
                             String rightSideLabelText ) {
        this(parent, source, initialRightSideItemsColl, labelProvider, leftSideLabelText, rightSideLabelText, -1, -1, -1, -1);
    }

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     * @param source Provider of data for "available" column, and provider of control to display the column
     * @initialRightSideItemsColl items initially displayed in the "selected" column
     */
    public AccumulatorPanel( Composite parent,
                             IAccumulatorSource source,
                             Collection /*<Object>*/initialRightSideItemsColl ) {
        this(parent, source, initialRightSideItemsColl, null, null, null);
    }

    // ============================================================
    // Instance methods
    // ============================================================

    /**
     * Initialize the panel.
     */
    private void init() {
        // Set overall grid layout
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 4;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(gridData);
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;

        // Set label above the Available side
        Label leftSideLabel = new Label(this, SWT.NONE);
        if (leftSideLabelText == null) {
            leftSideLabelText = Util.getString("AccumulatorColumnLabel.available"); //$NON-NLS-1$
        }
        leftSideLabel.setText(leftSideLabelText);
        GridData leftSideLabelGridData = new GridData();
        leftSideLabelGridData.horizontalSpan = 2;
        leftSideLabelGridData.horizontalAlignment = GridData.FILL;
        leftSideLabelGridData.verticalAlignment = GridData.FILL;
        leftSideLabel.setLayoutData(leftSideLabelGridData);

        // Set label above the Selected side
        Label rightSideLabel = new Label(this, SWT.NONE);
        if (rightSideLabelText == null) {
            rightSideLabelText = Util.getString("AccumulatorColumnLabel.selected"); //$NON-NLS-1$
        }
        rightSideLabel.setText(rightSideLabelText);
        GridData rightSideLabelGridData = new GridData();
        rightSideLabelGridData.horizontalSpan = 2;
        rightSideLabelGridData.verticalAlignment = GridData.FILL;
        rightSideLabelGridData.horizontalAlignment = GridData.FILL;
        rightSideLabel.setLayoutData(rightSideLabelGridData);

        // Add the control which displays the Available side list
        Control leftSideControl = source.createControl(this);
        source.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent ev ) {
                leftSideControlSelectionChanged();
            }

            public void widgetSelected( SelectionEvent ev ) {
                leftSideControlSelectionChanged();
            }
        });
        GridData leftSideControlGridData = new GridData();
        leftSideControlGridData.widthHint = leftSideWidthHint;
        leftSideControlGridData.heightHint = leftAndRightSideHeightHint;
        leftSideControlGridData.verticalAlignment = GridData.FILL;
        leftSideControlGridData.horizontalAlignment = GridData.FILL;
        leftSideControlGridData.grabExcessHorizontalSpace = true;
        leftSideControlGridData.grabExcessVerticalSpace = true;
        leftSideControl.setLayoutData(leftSideControlGridData);

        // Add the navigation buttons
        Composite controlButtons = new Composite(this, SWT.NONE);
        GridData controlButtonsGridData = new GridData();
        controlButtonsGridData.verticalAlignment = GridData.FILL;
        controlButtonsGridData.horizontalAlignment = GridData.FILL;
        controlButtons.setLayoutData(controlButtonsGridData);

        GridLayout controlButtonsGridLayout = new GridLayout();
        controlButtonsGridLayout.verticalSpacing = 2;
        controlButtons.setLayout(controlButtonsGridLayout);

        rightButton = new Button(controlButtons, SWT.PUSH);
        rightButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                rightButtonPressed();
            }
        });
        Image rightButtonImage = UiPlugin.getDefault().getImage(UiConstants.Images.RIGHT);
        rightButton.setImage(rightButtonImage);
        GridData rightButtonGridData = new GridData();
        rightButtonGridData.verticalAlignment = GridData.FILL;
        rightButtonGridData.horizontalAlignment = GridData.FILL;
        rightButton.setLayoutData(rightButtonGridData);

        leftButton = new Button(controlButtons, SWT.PUSH);
        leftButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                leftButtonPressed();
            }
        });
        Image leftButtonImage = UiPlugin.getDefault().getImage(UiConstants.Images.LEFT);
        leftButton.setImage(leftButtonImage);
        GridData leftButtonGridData = new GridData();
        leftButtonGridData.verticalAlignment = GridData.FILL;
        leftButtonGridData.horizontalAlignment = GridData.FILL;
        leftButton.setLayoutData(leftButtonGridData);

        allRightButton = new Button(controlButtons, SWT.PUSH);
        allRightButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                allRightButtonPressed();
            }
        });
        Image allRightButtonImage = UiPlugin.getDefault().getImage(UiConstants.Images.ALL_RIGHT);
        allRightButton.setImage(allRightButtonImage);
        GridData allRightButtonGridData = new GridData();
        allRightButtonGridData.verticalAlignment = GridData.FILL;
        allRightButtonGridData.horizontalAlignment = GridData.FILL;
        allRightButton.setLayoutData(allRightButtonGridData);
        allRightButton.setVisible(this.source.supportsAddAll());

        allLeftButton = new Button(controlButtons, SWT.PUSH);
        allLeftButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                allLeftButtonPressed();
            }
        });
        Image allLeftButtonImage = UiPlugin.getDefault().getImage(UiConstants.Images.ALL_LEFT);
        allLeftButton.setImage(allLeftButtonImage);
        GridData allLeftButtonGridData = new GridData();
        allLeftButtonGridData.verticalAlignment = GridData.FILL;
        allLeftButtonGridData.horizontalAlignment = GridData.FILL;
        allLeftButton.setLayoutData(allLeftButtonGridData);

        // Add the Selected list
        rightTable = new Table(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tableViewer = new TableViewer(rightTable);
        tableViewer.setSorter(new OrderableViewerSorter());
        if (labelProvider != null) {
            tableViewer.setLabelProvider(labelProvider);
        }
        rightTable.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent ev ) {
                rightSideControlSelectionChanged();
            }

            public void widgetSelected( SelectionEvent ev ) {
                rightSideControlSelectionChanged();
            }
        });

        GridData rightTableGridData = new GridData();
        rightTableGridData.widthHint = rightSideWidthHint;
        rightTableGridData.heightHint = leftAndRightSideHeightHint;
        rightTableGridData.verticalAlignment = GridData.FILL;
        rightTableGridData.horizontalAlignment = GridData.FILL;
        rightTableGridData.grabExcessHorizontalSpace = true;
        rightTableGridData.grabExcessVerticalSpace = true;
        rightTable.setLayoutData(rightTableGridData);

        // Add the up and down buttons
        Composite upDownButtons = new Composite(this, SWT.NONE);
        GridData upDownButtonsGridData = new GridData();
        upDownButtonsGridData.verticalAlignment = GridData.FILL;
        upDownButtonsGridData.horizontalAlignment = GridData.FILL;
        upDownButtons.setLayoutData(upDownButtonsGridData);

        GridLayout upDownButtonsGridLayout = new GridLayout();
        upDownButtonsGridLayout.verticalSpacing = 2;
        upDownButtons.setLayout(upDownButtonsGridLayout);

        upButton = new Button(upDownButtons, SWT.PUSH);
        upButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                upButtonPressed();
            }
        });
        Image upButtonImage = UiPlugin.getDefault().getImage(UiConstants.Images.UP);
        upButton.setImage(upButtonImage);
        GridData upButtonGridData = new GridData();
        upButtonGridData.verticalAlignment = GridData.FILL;
        upButtonGridData.horizontalAlignment = GridData.FILL;
        upButton.setLayoutData(upButtonGridData);

        downButton = new Button(upDownButtons, SWT.PUSH);
        downButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                downButtonPressed();
            }
        });
        Image downButtonImage = UiPlugin.getDefault().getImage(UiConstants.Images.DOWN);
        downButton.setImage(downButtonImage);
        GridData downButtonGridData = new GridData();
        downButtonGridData.verticalAlignment = GridData.FILL;
        downButtonGridData.horizontalAlignment = GridData.FILL;
        downButton.setLayoutData(downButtonGridData);

        // Add the Reset button
        Composite resetButtonComposite = new Composite(this, SWT.NONE);
        GridLayout resetButtonGridLayout = new GridLayout();
        resetButtonGridLayout.marginHeight = this.resetButtonMarginHeight;
        resetButtonComposite.setLayout(resetButtonGridLayout);
        GridData resetButtonCompositeGridData = new GridData(GridData.FILL_BOTH);
        resetButtonCompositeGridData.horizontalSpan = 2;
        resetButtonComposite.setLayoutData(resetButtonCompositeGridData);
        resetButton = new Button(resetButtonComposite, SWT.PUSH);
        resetButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                resetButtonPressed();
            }
        });
        String resetButtonText = Util.getString("ButtonText.reset"); //$NON-NLS-1$
        resetButton.setText(resetButtonText);
        GridData resetButtonGridData = new GridData(GridData.FILL_BOTH);
        resetButtonGridData.verticalAlignment = GridData.VERTICAL_ALIGN_END;
        resetButtonGridData.horizontalAlignment = GridData.END;
        resetButton.setLayoutData(resetButtonGridData);

        // Add the Sort button (checkbox)
        sortButton = new Button(this, SWT.CHECK);
        sortButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                sortButtonChanged();
            }
        });
        String sortButtonText = Util.getString("ButtonText.keepSortedAlphabetically"); //$NON-NLS-1$
        sortButton.setText(sortButtonText);
        GridData sortButtonGridData = new GridData();
        sortButtonGridData.verticalAlignment = GridData.FILL;
        sortButtonGridData.horizontalAlignment = GridData.END;
        sortButtonGridData.horizontalIndent = 4;
        sortButton.setLayoutData(sortButtonGridData);

        messageLabel = new MessageLabel(this);
        messageLabel.setAlignment(SWT.LEFT);
        messageLabel.setErrorStatus(null);
        messageLabel.setFont(this.getFont());
        GridData messageLabelGridData = new GridData();
        messageLabelGridData.horizontalSpan = 4;
        messageLabelGridData.horizontalAlignment = GridData.FILL;
        messageLabelGridData.verticalAlignment = GridData.FILL;
        messageLabel.setLayoutData(messageLabelGridData);

        // Populate the list
        initiallyPopulateRightTable();

        // Initially enable/disable the buttons
        setButtonStates();

        // Initialize changeListeners to empty
        changeListeners = new ArrayList();
    }

    public void addAccumulatedValuesChangeListener( IAccumulatedValuesChangeListener listener ) {
        changeListeners.add(listener);
    }

    private void initiallyPopulateRightTable() {
        rightTable.removeAll();
        Object[] items = initialRightSideItems.toArray();
        insertItemsIntoRightTable(items);
        notifyListeners();
    }

    // private void insertItemIntoRightTable(Object item) {
    // if (!isSorting) {
    // java.util.List /*<String>*/ order = rightTableItemsListWithItemAppended(item);
    // OrderableViewerSorter sorter = (OrderableViewerSorter)tableViewer.getSorter();
    // sorter.setStringsOrder(order);
    // }
    // tableViewer.add(item);
    // //Do not call notifyListeners() here. Call it in the methods calling this method.
    // }

    private void insertItemsIntoRightTable( Object[] items ) {
        if (!isSorting) {
            java.util.List /*<String>*/order = rightTableItemsListWithItemsAppended(items);
            OrderableViewerSorter sorter = (OrderableViewerSorter)tableViewer.getSorter();
            sorter.setStringsOrder(order);
        }
        tableViewer.add(items);
        // Do not call notifyListeners() here. Call it in the methods calling this method.
    }

    void leftSideControlSelectionChanged() {
        if (!ignoreLeftSideSelectionChanges) {
            setButtonStates();
        }
    }

    void rightSideControlSelectionChanged() {
        if (!ignoreRightSideSelectionChanges) {
            setButtonStates();
        }
    }

    void rightButtonPressed() {
        // Get selected left side items
        Collection /*<Object>*/itemsToMoveToSelected = source.getSelectedAvailableValues();
        // Insert each onto right side
        Object[] items = itemsToMoveToSelected.toArray();
        insertItemsIntoRightTable(items);

        // Tell left side to remove them
        ignoreLeftSideSelectionChanges = true;
        source.accumulatedValuesAdded(itemsToMoveToSelected);
        ignoreLeftSideSelectionChanges = false;
        // Set button states
        setButtonStates();
        notifyListeners();
    }

    void leftButtonPressed() {
        // Get selected right side items
        int[] selectedItemIndices = rightTable.getSelectionIndices();
        Object[] selectedObjects = new Object[selectedItemIndices.length];
        for (int i = 0; i < selectedObjects.length; i++) {
            selectedObjects[i] = tableViewer.getElementAt(selectedItemIndices[i]);
        }
        ignoreRightSideSelectionChanges = true;
        // Remove them from the right side
        tableViewer.remove(selectedObjects);
        ignoreRightSideSelectionChanges = false;
        // Tell left side to add them
        source.accumulatedValuesRemoved(Arrays.asList(selectedObjects));
        // Reset button states
        setButtonStates();
        notifyListeners();
    }

    void allRightButtonPressed() {
        // Get all left side items
        Collection /*<Object>*/itemsToMoveToSelected = source.getAvailableValues();
        // Add each to right side
        Object[] items = itemsToMoveToSelected.toArray();
        insertItemsIntoRightTable(items);
        // Tell left side to remove them
        ignoreLeftSideSelectionChanges = true;
        source.accumulatedValuesAdded(itemsToMoveToSelected);
        ignoreLeftSideSelectionChanges = false;
        // Set button states
        setButtonStates();
        notifyListeners();
    }

    void allLeftButtonPressed() {
        // Get all right side items
        Object[] obj = getRightTableItems();
        ignoreRightSideSelectionChanges = true;
        // Remove them from right side
        tableViewer.remove(obj);
        // Tell left side to add them
        ignoreRightSideSelectionChanges = false;
        Collection itemsColl = Arrays.asList(obj);
        source.accumulatedValuesRemoved(itemsColl);
        // Set button states
        setButtonStates();
        notifyListeners();
    }

    void upButtonPressed() {
        // Have to compute what the new item order should be, tell it to the sorter and save
        // selections. Then remove all items and re-add them, which the sorter
        // will then put in the desired order. Then reset the selection.

        // Get all items
        Object[] items = getRightTableItems();
        // Get selected items
        int[] selectedIndices = rightTable.getSelectionIndices();
        Object[] selectedItems = new Object[selectedIndices.length];
        for (int i = 0; i < selectedItems.length; i++) {
            selectedItems[i] = items[selectedIndices[i]];
        }
        // Form into new order
        Object[] itemsInNewOrder = new Object[items.length];
        int firstSelectionIndex = selectedIndices[0];
        int lastSelectionIndex = selectedIndices[selectedIndices.length - 1];
        for (int i = 0; i < firstSelectionIndex - 1; i++) {
            itemsInNewOrder[i] = items[i];
        }
        Object itemMovingInBackOfSelections = items[firstSelectionIndex - 1];
        for (int i = firstSelectionIndex; i <= lastSelectionIndex; i++) {
            itemsInNewOrder[i - 1] = items[i];
        }
        itemsInNewOrder[lastSelectionIndex] = itemMovingInBackOfSelections;
        for (int i = lastSelectionIndex + 1; i < items.length; i++) {
            itemsInNewOrder[i] = items[i];
        }

        // BWP- code to save and remove scroll bar positions removed, not working. Was attempting
        // to use getSelection() and setSelection(), but was not restoring scroll bar position.

        // Remove all items
        tableViewer.remove(items);
        // Set item order in the sorter
        java.util.List /*<String>*/order = new ArrayList(itemsInNewOrder.length);
        for (int i = 0; i < itemsInNewOrder.length; i++) {
            String text;
            if (labelProvider != null) {
                text = labelProvider.getText(itemsInNewOrder[i]);
            } else {
                text = itemsInNewOrder[i].toString();
            }
            order.add(text);
        }
        OrderableViewerSorter sorter = (OrderableViewerSorter)tableViewer.getSorter();
        sorter.setStringsOrder(order);
        // Re-add the items
        tableViewer.add(items);
        // Restore the selection
        java.util.List itemsList = Arrays.asList(getRightTableItems());
        int[] newSelectedIndices = new int[selectedIndices.length];
        for (int i = 0; i < newSelectedIndices.length; i++) {
            newSelectedIndices[i] = itemsList.indexOf(items[selectedIndices[i]]);
        }
        rightTable.setSelection(newSelectedIndices);

        setButtonStates();
    }

    void downButtonPressed() {
        // Have to compute what the new item order should be, tell it to the sorter, and save
        // selections. Then remove all items and re-add them, which the sorter
        // will then put in the desired order. Then reset the selection.

        // Get all items
        Object[] items = getRightTableItems();
        // Get selected items
        int[] selectedIndices = rightTable.getSelectionIndices();
        Object[] selectedItems = new Object[selectedIndices.length];
        for (int i = 0; i < selectedItems.length; i++) {
            selectedItems[i] = items[selectedIndices[i]];
        }
        // Form into new order
        Object[] itemsInNewOrder = new Object[items.length];
        int firstSelectionIndex = selectedIndices[0];
        int lastSelectionIndex = selectedIndices[selectedIndices.length - 1];
        for (int i = 0; i < firstSelectionIndex; i++) {
            itemsInNewOrder[i] = items[i];
        }
        Object itemMovingInFrontOfSelections = items[lastSelectionIndex + 1];
        for (int i = firstSelectionIndex; i <= lastSelectionIndex; i++) {
            itemsInNewOrder[i + 1] = items[i];
        }
        itemsInNewOrder[firstSelectionIndex] = itemMovingInFrontOfSelections;
        for (int i = lastSelectionIndex + 2; i < items.length; i++) {
            itemsInNewOrder[i] = items[i];
        }

        // BWP- code to save and remove scroll bar positions removed, not working. Was attempting
        // to use getSelection() and setSelection(), but was not restoring scroll bar position.

        // Remove all items
        tableViewer.remove(items);
        // Set item order in the sorter
        java.util.List /*<String>*/order = new ArrayList(itemsInNewOrder.length);
        for (int i = 0; i < itemsInNewOrder.length; i++) {
            String text;
            if (labelProvider != null) {
                text = labelProvider.getText(itemsInNewOrder[i]);
            } else {
                text = itemsInNewOrder[i].toString();
            }
            order.add(text);
        }
        OrderableViewerSorter sorter = (OrderableViewerSorter)tableViewer.getSorter();
        sorter.setStringsOrder(order);
        // Re-add the items
        tableViewer.add(items);
        // Restore the selection
        java.util.List itemsList = Arrays.asList(getRightTableItems());
        int[] newSelectedIndices = new int[selectedIndices.length];
        for (int i = 0; i < newSelectedIndices.length; i++) {
            newSelectedIndices[i] = itemsList.indexOf(items[selectedIndices[i]]);
        }
        rightTable.setSelection(newSelectedIndices);

        // Set button states
        setButtonStates();
    }

    void sortButtonChanged() {
        if (sortButton.getSelection()) {
            // Now sorting. To get the items sorted, have to remove them then re-add them. First
            // save the selection so that we can restore it.
            isSorting = true;
            OrderableViewerSorter sorter = (OrderableViewerSorter)tableViewer.getSorter();
            sorter.setStringsOrder(null);
            Object[] items = getRightTableItems();
            int[] selectedItemIndices = rightTable.getSelectionIndices();
            Object[] selectedItems = new Object[selectedItemIndices.length];
            for (int i = 0; i < selectedItems.length; i++) {
                selectedItems[i] = items[selectedItemIndices[i]];
            }
            // Save scroll bar positions
            int vertScrollBarPosit = -1;
            if (rightTable.getVerticalBar().isVisible()) {
                vertScrollBarPosit = rightTable.getVerticalBar().getSelection();
            }
            int horizScrollBarPosit = -1;
            if (rightTable.getHorizontalBar().isVisible()) {
                horizScrollBarPosit = rightTable.getHorizontalBar().getSelection();
            }
            // Remove all items
            tableViewer.remove(items);
            // Re-add the items. Will now be put in alphabetical order.
            tableViewer.add(items);
            // Restore the selection
            java.util.List itemsList = Arrays.asList(getRightTableItems());
            int[] newSelectedItemIndices = new int[selectedItemIndices.length];
            for (int i = 0; i < newSelectedItemIndices.length; i++) {
                newSelectedItemIndices[i] = itemsList.indexOf(items[selectedItemIndices[i]]);
            }
            rightTable.setSelection(newSelectedItemIndices);
            // Restore the scroll bar positions
            if (vertScrollBarPosit >= 0) {
                rightTable.getVerticalBar().setSelection(vertScrollBarPosit);
            }
            if (horizScrollBarPosit >= 0) {
                rightTable.getHorizontalBar().setSelection(horizScrollBarPosit);
            }
        } else {
            // Now not sorting. We don't change the already sorted table. Merely set a flag so
            // that future inserts will be at the end of the table.
            isSorting = false;
        }
        // Set the button states
        setButtonStates();
    }

    private void setButtonStates() {
        int numLeftSideItems = source.getAvailableValuesCount();
        if (numLeftSideItems == 0 || !source.getSelectionStatus().isOK()) {
            // No left side items. Disable both move-right buttons.
            rightButton.setEnabled(false);
            allRightButton.setEnabled(false);
        } else {
            allRightButton.setEnabled(true);
            // Enable move-selected-right button iff. anything selected.
            int numLeftSideSelectedItems = source.getSelectedAvailableValuesCount();
            rightButton.setEnabled((numLeftSideSelectedItems > 0));
        }
        int numRightSideItems = rightTable.getItemCount();
        if (numRightSideItems == 0) {
            // No right side items. Disable both move-left buttons, also up and down buttons.
            leftButton.setEnabled(false);
            allLeftButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        } else {
            allLeftButton.setEnabled(true);
            // Enable move-selected-left button iff. anything selected.
            int numRightSideSelectedItems = rightTable.getSelectionCount();
            boolean anyRightSideItemsSelected = (numRightSideSelectedItems > 0);
            leftButton.setEnabled(anyRightSideItemsSelected);
            if (sortButton.getSelection() || (!anyRightSideItemsSelected)) {
                // If sorting alphabetically or no right side items selected, disable up and down
                // buttons.
                upButton.setEnabled(false);
                downButton.setEnabled(false);
            } else {
                boolean contiguous = isRightSideSelectionContiguous();
                if (contiguous) {
                    // Contiguous right side selection. Enable up button iff. selection does not
                    // include topmost row. Enable down button iff. selection does not include
                    // bottommost row.
                    upButton.setEnabled((!isFirstRightSideItemSelected()));
                    downButton.setEnabled((!isLastRightSideItemSelected()));
                } else {
                    // Right side selection is discontiguous. Disable up and down buttons.
                    upButton.setEnabled(false);
                    downButton.setEnabled(false);
                }
            }
        }

        setStatus(source.getSelectionStatus());
    }

    private void setStatus( IStatus status ) {
        messageLabel.setErrorStatus(status);
    }

    private boolean isRightSideSelectionContiguous() {
        int[] indices = rightTable.getSelectionIndices();
        boolean contiguous;
        if (indices.length == 0) {
            contiguous = false;
        } else {
            boolean gapFound = false;
            int i = 0;
            while ((i < indices.length - 1) && (!gapFound)) {
                if (indices[i + 1] - indices[i] > 1) {
                    gapFound = true;
                } else {
                    i++;
                }
            }
            contiguous = (!gapFound);
        }
        return contiguous;
    }

    private boolean isFirstRightSideItemSelected() {
        int[] indices = rightTable.getSelectionIndices();
        boolean firstItemSelected;
        if (indices.length == 0) {
            firstItemSelected = false;
        } else {
            firstItemSelected = (indices[0] == 0);
        }
        return firstItemSelected;
    }

    private boolean isLastRightSideItemSelected() {
        int[] indices = rightTable.getSelectionIndices();
        boolean lastItemSelected;
        if (indices.length == 0) {
            lastItemSelected = false;
        } else {
            lastItemSelected = (indices[indices.length - 1] == rightTable.getItemCount() - 1);
        }
        return lastItemSelected;
    }

    void resetButtonPressed() {
        // First, figure out which items currently on right side did not start out on right
        // side.
        Collection /*<Object>*/itemsThatWereAddedToRight = getItemsMovedToSelected();
        // Next, figure out which items started out on the right side but are not there now.
        Collection /*<String>*/itemsThatWereRemovedFromRight = getItemsRemovedFromSelected();
        // Re-populate the righthand list to its initial state.
        initiallyPopulateRightTable();
        // Inform the source of values removed from the right side and values added to the
        // right side.
        source.accumulatedValuesRemoved(itemsThatWereAddedToRight);
        source.accumulatedValuesAdded(itemsThatWereRemovedFromRight);
        // Set the button states
        setButtonStates();
    }

    private Object[] getRightTableItems() {
        // Return all items on the right side.
        int numItems = rightTable.getItemCount();
        Object[] items = new Object[numItems];
        for (int i = 0; i < numItems; i++) {
            items[i] = tableViewer.getElementAt(i);
        }
        return items;
    }

    // private java.util.List /*<String>*/ rightTableItemsListWithItemAppended(Object item) {
    // Object[] items = getRightTableItems();
    // java.util.List itemsList = new ArrayList(items.length + 1);
    // for (int i = 0; i <= items.length; i++) {
    // Object curObj;
    // if (i < items.length) {
    // curObj = tableViewer.getElementAt(i);
    // } else {
    // curObj = item;
    // }
    // String text;
    // if (labelProvider != null) {
    // text = labelProvider.getText(curObj);
    // } else {
    // text = curObj.toString();
    // }
    // itemsList.add(text);
    // }
    // return itemsList;
    // }

    private java.util.List /*<String>*/rightTableItemsListWithItemsAppended( Object[] newItems ) {
        Object[] items = getRightTableItems();
        java.util.List itemsList = new ArrayList(items.length + newItems.length);
        for (int i = 0; i < items.length; i++) {
            final Object curObj = tableViewer.getElementAt(i);
            String text;
            if (labelProvider != null) {
                text = labelProvider.getText(curObj);
            } else {
                text = curObj.toString();
            }
            itemsList.add(text);
        }

        for (int i = 0; i < newItems.length; i++) {
            final Object curObj = newItems[i];
            String text;
            if (labelProvider != null) {
                text = labelProvider.getText(curObj);
            } else {
                text = curObj.toString();
            }
            itemsList.add(text);

        }

        return itemsList;
    }

    /**
     * Return those items which started out as available but were moved to selected.
     * 
     * @return Collection (Object) of those items moved from available to selected.
     */
    public Collection /*<Object>*/getItemsMovedToSelected() {
        Object[] obj = getRightTableItems();
        Collection /*<Object>*/itemsThatWereAddedToRight = new ArrayList(obj.length);
        for (int i = 0; i < obj.length; i++) {
            if (!initialRightSideItems.contains(obj[i])) {
                itemsThatWereAddedToRight.add(obj[i]);
            }
        }
        return itemsThatWereAddedToRight;
    }

    /**
     * Return those items which started out as selected but were moved to available.
     * 
     * @return Collection (Object) of those items moved from selected to available.
     */
    public Collection /*<Object>*/getItemsRemovedFromSelected() {
        Collection /*<Object>*/itemsThatWereRemovedFromRight = new ArrayList(initialRightSideItems.size());
        Collection /*<Object>*/rightSideItems = Arrays.asList(getRightTableItems());
        Iterator it = initialRightSideItems.iterator();
        while (it.hasNext()) {
            Object curItem = it.next();
            boolean hasBeenRemoved = (!rightSideItems.contains(curItem));
            if (hasBeenRemoved) {
                itemsThatWereRemovedFromRight.add(curItem);
            }
        }
        return itemsThatWereRemovedFromRight;
    }

    /**
     * Return Collection (Object) of selected items.
     * 
     * @return Collection (Object) of the selected items
     */
    public Collection /*<Object>*/getSelectedItems() {
        Object[] obj = getRightTableItems();
        Collection /*<Object>*/coll = Arrays.asList(obj);
        return coll;
    }

    /**
     * Repopulate the selected items. No IAccumulatorSource methods are called. Method intended for use when the contents of both
     * the available and selected sides have changed while being displayed, e.g. a selection elsewhere has changed, causing the
     * accumulator to be completely repopulated.
     * 
     * @param selectedItems new Collection of selected items
     */
    public void repopulateSelectedItems( Collection newSelectedItems ) {
        int numRightSideItems = rightTable.getItemCount();
        Object[] rightSideItems = new Object[numRightSideItems];
        for (int i = 0; i < numRightSideItems; i++) {
            rightSideItems[i] = tableViewer.getElementAt(i);
        }
        tableViewer.remove(rightSideItems);
        Iterator it = newSelectedItems.iterator();
        while (it.hasNext()) {
            Object item = it.next();
            tableViewer.add(item);
        }
        source.accumulatedValuesAdded(newSelectedItems);
        notifyListeners();
    }

    /**
     * Inform the accumulator that the population of the available side has changed. The accumulator will then reset button states
     * accordingly.
     */
    public void availableItemsHaveChanged() {
        setButtonStates();
    }

    private void notifyListeners() {
        if (changeListeners != null) {
            int numListeners = changeListeners.size();
            for (int i = numListeners - 1; i >= 0; i--) {
                IAccumulatedValuesChangeListener listener = (IAccumulatedValuesChangeListener)changeListeners.get(i);
                listener.accumulatedValuesChanged(this);
            }
        }
    }
}// end AccumulatorPanel
