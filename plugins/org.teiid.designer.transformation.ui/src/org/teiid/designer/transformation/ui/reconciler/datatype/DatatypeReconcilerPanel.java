/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.reconciler.datatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.reconciler.Binding;
import org.teiid.designer.transformation.ui.reconciler.BindingList;
import org.teiid.designer.transformation.ui.reconciler.ColorManager;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.TransformationMappingHelper;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.table.CheckBoxEditingSupport;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.viewsupport.DatatypeSelectionDialog;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 * Panel used by the DatatypeReconcilerDialog to assist in displaying and editing/fixing datatype conflicts for mapped or bound
 * attributes and SQL symbols
 * 
 * @since 8.0
 */
public class DatatypeReconcilerPanel extends SashForm implements ISelectionChangedListener, PluginConstants.Images {

    private DatatypeReconcilerDialog datatypeReconcilerDialog = null;

    // Original BindingList that was passed in
    private BindingList originalBindingList;

    // Working Binding List - includes type info
    /**
	 * 
	 */
	private BindingTableInput bindingListInput;

    private boolean targetLocked;

    private ColorManager colorManager;
    private Table table;
    private TableViewer bindingTableViewer;

    // Push Buttons
    Button changeAllColumnDatatypesButton, convertAllSqlSymbolsButton;
    Text helpText;
    
    private BindingContentProvider bindingContentProvider;

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     * @param dialog the dialog
     * @param bindingList the datatype binding list
     * @param targetLocked boolean defining if target table is locked (read-only)
     * @param colorManager the color manager for error/warning status
     */
    public DatatypeReconcilerPanel( Composite parent,
                                    DatatypeReconcilerDialog dialog,
                                    BindingList bindingList,
                                    boolean targetLocked,
                                    ColorManager colorManager ) {
        super(parent, SWT.VERTICAL);
        this.originalBindingList = bindingList;
        this.targetLocked = targetLocked;
        this.colorManager = colorManager;
        this.datatypeReconcilerDialog = dialog;
        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {
        // ------------------------------
        // Set layout for the SashForm
        // ------------------------------
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        gridLayout.marginLeft = 20;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(gridData);

        // Init the symbol arrays from bindings
        this.bindingListInput = new BindingTableInput(createBindingList(originalBindingList));

        // ----------------------------------
        // Create the Table Viewer Panel
        // ----------------------------------
        createTableViewerPanel(this);

        // Initialize the message area at the top of the dialog
        datatypeReconcilerDialog.setTitle(Messages.datatypeReconciler_statusTitle);
        updateMessageArea();

        // Listen for TableSelection from the Tables
        bindingTableViewer.addSelectionChangedListener(this);
        selectFirstTypeConflict();
    }

    /**
     * Create the Datatype BindingList from the supplied BindingList
     * 
     * @param bindings the bindingList from which to init the arrays
     */
    private BindingList createBindingList( BindingList bindings ) {
        BindingList newBindingList = new BindingList();

        if (bindings != null && bindings.size() > 0) {
            // Set the array values from the supplied bindingList
            for (int i = 0; i < bindings.size(); i++) {
                Binding binding = bindings.get(i);
                newBindingList.add(binding);
            }
        }

        return newBindingList;
    }

    /**
     * Updates the Message Area at the top of the containing dialog, based upon the current status of the panel
     */
    private void updateMessageArea() {
        int statusType = IMessageProvider.NONE;
        String message = Messages.datatypeReconciler_allResolvedMessage;
        if (this.bindingListInput.getBindingList().hasTypeConflict()) {
            message = Messages.datatypeReconciler_someUnresolvedConflicts;
            statusType = IMessageProvider.ERROR;
        }
        datatypeReconcilerDialog.setMessage(message, statusType);
    }

    /**
     * Create the tableViewer Panel
     */
    @SuppressWarnings("unused")
	private void createTableViewerPanel( Composite theParent ) {
        Composite tablePanel = new Composite(theParent, SWT.NONE);

        // Set the layout
        GridLayout gridLayout = new GridLayout();
        tablePanel.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        tablePanel.setLayoutData(gridData);
        
        // Add header panel
        
        HEADER_PANEL: {
	        Composite headerPanel = WidgetFactory.createPanel(tablePanel);
			// ------------------------------
			// Set layout for the Composite
			// ------------------------------
			headerPanel.setLayout(new GridLayout());
			((GridLayout)headerPanel.getLayout()).numColumns = 3;
			headerPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
			((GridData)headerPanel.getLayoutData()).grabExcessHorizontalSpace = true;
	        
			// Add general info/instructions text box
			{
		    	helpText = new Text(headerPanel, SWT.WRAP | SWT.READ_ONLY);
		    	helpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		    	helpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		    	helpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		    	((GridData)helpText.getLayoutData()).horizontalSpan = 3;
		    	((GridData)helpText.getLayoutData()).heightHint = 40;
		    	((GridData)helpText.getLayoutData()).widthHint = 360;
		    	
		    	helpText.setText(Messages.datatypeReconciler_helpText);
			}
			
			
			
			// Add button bar containing:
			// [CHANGE ALL COLUMN DATATYPES] - or - [CONVERT ALL SQL SYMBOLS]
			this.changeAllColumnDatatypesButton =  
					WidgetFactory.createButton(headerPanel, Messages.datatypeReconciler_convertAllColumnDatatypesLabel);
			this.changeAllColumnDatatypesButton.setToolTipText(Messages.datatypeReconciler_convertAllColumnDatatypesTooltip);
			this.changeAllColumnDatatypesButton.setEnabled(false);
	        this.changeAllColumnDatatypesButton.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected( final SelectionEvent event ) {
	            	changeAllColumnDatatypesPressed();
	            }
	        });
			
			WidgetFactory.createLabel(headerPanel, "   - or -   "); //$NON-NLS-1$
			
			this.convertAllSqlSymbolsButton =  
					WidgetFactory.createButton(headerPanel, Messages.datatypeReconciler_convertAllSqlSymbolsLabel);
			this.convertAllSqlSymbolsButton.setToolTipText(Messages.datatypeReconciler_convertAllSqlSymbolsTooltip);
			
			this.convertAllSqlSymbolsButton.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected( final SelectionEvent event ) {
	            	changeAllColumnDatatypesButtonPressed();
	            }
	        });
			
        }
        
        BINDING_TABLE : {
	    	table = new Table(tablePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);
	        table.setLayout(new TableLayout());
	    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	    	gd.heightHint = 200;
	    	table.setLayoutData(gd);
	
	        this.bindingTableViewer = new TableViewer(table);
	        this.bindingTableViewer.getControl().setLayoutData(gd);
	        
	        this.bindingContentProvider = new BindingContentProvider();
	        
	        // create columns
	        
	        // {"", "Target Column", "Matched Type", "Change Type", "", "Source SQL Symbol"};
	        // COLUMN 0 :  FIX Button
	        TableViewerColumn column = new TableViewerColumn(this.bindingTableViewer, SWT.LEFT);
	        column.getColumn().setText(getSpaces(20));
	        column.setLabelProvider(new TheBindingColumnLabelProvider(0));
	        column.setEditingSupport(new ChangeDatatypeEditingSupport(this.bindingTableViewer));
	        column.getColumn().pack();
	        
	        // COLUMN 1 :  Target Column Definition
	        column = new TableViewerColumn(this.bindingTableViewer, SWT.LEFT);
	        column.getColumn().setText(Messages.datatypeReconciler_targetColumnLabel + getSpaces(25));
	        column.setLabelProvider(new TheBindingColumnLabelProvider(1));
	        column.getColumn().pack();
	        
	        // COLUMN 2 :  Matched Datatype
	        column = new TableViewerColumn(this.bindingTableViewer, SWT.LEFT);
	        column.getColumn().setText(Messages.datatypeReconciler_matchedTypeLabel + getSpaces(60));
	        column.setLabelProvider(new TheBindingColumnLabelProvider(2));
	        column.setEditingSupport(new ChangeProposedDatatypeEditingSupport(this.bindingTableViewer));
	        column.getColumn().pack();
	        
	        // COLUMN 4 :  CONVERT Button
	        column = new TableViewerColumn(this.bindingTableViewer, SWT.LEFT);
	        column.getColumn().setText(getSpaces(20));
	        column.setLabelProvider(new TheBindingColumnLabelProvider(3));
	        column.setEditingSupport(new ConvertSymbolEditingSupport(this.bindingTableViewer));
	        column.getColumn().pack();
	        
	        // COLUMN 5 :  Source SQL Symbol Definition
	        column = new TableViewerColumn(this.bindingTableViewer, SWT.LEFT);
	        column.getColumn().setText(Messages.datatypeReconciler_sourceSqlSymbolLabel + getSpaces(60));
	        column.setLabelProvider(new TheBindingColumnLabelProvider(4));
	        column.getColumn().pack();
	        
	        bindingTableViewer.setUseHashlookup(true);
	        bindingTableViewer.setContentProvider(this.bindingContentProvider);
	        bindingTableViewer.setInput(this.bindingListInput);

	        updateRowColors();
        }
    }

    private String getDatatypeText( Object object ) {
        String result = null;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getText(object);
            }
        }
        return result;
    }

    /**
     * Update the Attribute and Sql Conversion Panels when new table row is selected
     * 
     * @param binding the selected Binding
     */
    private void updateDisplaysOnTableSelection( Binding binding ) {
        // Update the Conversion Panel
        updateAttributeConversionPanel(binding);

        // Update the SqlConversion Panel
        updateSymbolConversionPanel(binding);
    }

    /**
     * Update the Attribute Conversion Panel with the provided binding info
     * 
     * @param binding the selected Binding
     */
    private void updateAttributeConversionPanel( Binding binding ) {
        updateAttributeConversionPanelButtons(binding);
    }

    /**
     * Update the Attribute Conversion Panel button enabled states.
     */
    private void updateAttributeConversionPanelButtons( Binding binding ) {
        // ------------------------------------------
        // Set the ConvertAll Button Enabled State
        // ------------------------------------------
        // Enable ConvertAll Button if any Binding has Conflict and target group not locked
        boolean enableConvertAll = false;

        // Enable ConvertAll Button if any Binding has Conflict
        boolean hasTypeConflict = bindingListInput.getBindingList().hasTypeConflict();
        if (hasTypeConflict && !this.targetLocked) {
            enableConvertAll = true;
        }

        changeAllColumnDatatypesButton.setEnabled(enableConvertAll);
    }

    /**
     * Update the Symbol Conversion Panel with the provided binding info
     * 
     * @param binding the selected Binding
     */
    private void updateSymbolConversionPanel( Binding binding ) {
        updateSymbolConversionPanelButtons(binding);
    }

    /**
     * Update the Symbol Conversion Panel button enabled states.
     */
    private void updateSymbolConversionPanelButtons( Binding binding ) {
        // ------------------------------------------
        // Set the Apply Button Enabled State
        // ------------------------------------------
        boolean enableApply = false;
        if (binding != null) {
            // Enable Apply Button if current Binding has Conflict
            enableApply = binding.canConvertSqlSymbol();
        }

        // ------------------------------------------
        // Set the ConvertAll Button Enabled State
        // ------------------------------------------
        // If any binding has conflict, enable
        boolean enableConvertAll = false;
        if (bindingListInput.getBindingList().hasTypeConflict()) {
            enableConvertAll = true;
        }
        
        if( enableConvertAll && enableApply ) {
        	enableConvertAll = true;
        } else {
        	enableConvertAll = false;
        }
        // Set ConvertAll Enabled State
        convertAllSqlSymbolsButton.setEnabled(enableConvertAll);
    }

    /**
     * update Row background colors, based on binding and type conflict status.
     */
    public void updateRowColors() {
        int rows = table.getItemCount();
        for (int i = 0; i < rows; i++) {
            TableItem item = table.getItem(i);
            Binding binding = bindingListInput.getBindingList().get(i);
            if (!binding.isBound() || binding.hasTypeConflict()) {
                item.setBackground(colorManager.getColor(ColorManager.UNBOUND_BACKGROUND));
            } else {
                item.setBackground(colorManager.getColor(ColorManager.BOUND_BACKGROUND));
            }
        }

    }

    /**
     * handler for convertAll Attributes Button pressed
     */
    void changeAllColumnDatatypesPressed() {
        for (int i = 0; i < bindingListInput.getBindingList().size(); i++) {
            Binding binding = bindingListInput.getBindingList().get(i);
            if (binding.hasTypeConflict() && binding.hasAttributeConversion()) {
                // accept the default attribute type
                binding.acceptAttributeConversion();
            }
        }
        
        bindingListInput.datatypeChanged();

        // Refresh
        bindingTableViewer.refresh(true);
        updateRowColors();
        updateMessageArea();

        selectFirstBinding();
    }

    /**
     * handler for convertAll Sql Button pressed
     */
    void changeAllColumnDatatypesButtonPressed() {
        for (int i = 0; i < bindingListInput.getBindingList().size(); i++) {
            Binding binding = bindingListInput.getBindingList().get(i);
            // If there is a type conflict, and available conversion, use it
            if (binding.hasTypeConflict() && binding.canConvertSqlSymbol()) {
                // accept the available Sql Conversion
                binding.acceptSqlConversion();
            }
        }
        
        bindingListInput.datatypeChanged();

        // Refresh
        bindingTableViewer.refresh(true);
        updateRowColors();
        updateMessageArea();

        selectFirstBinding();
    }

    /**
     * handler for convert Selected Attribute Button pressed
     */
    void convertSelectedAttrPressed() {
        // Get the selected binding - table only allows single select
        Binding binding = getSelectedBinding();
        // Set datatype on the binding
        binding.setNewAttrDatatype(bindingListInput.getTargetDatatype(binding));

        // Update the AttrConversion Panel
        updateAttributeConversionPanel(binding);

        bindingListInput.datatypeChanged();
        
        // Refresh
        bindingTableViewer.refresh(true);
        updateRowColors();
        updateMessageArea();

        selectBinding(binding);
    }

    /**
     * handler for convert Selected Sql Button pressed
     */
    void convertSelectedSqlPressed() {
        // Get the selected binding
        Binding binding = getSelectedBinding();
        // accept the available Sql Conversion
        if (binding.canConvertSqlSymbol()) {
            binding.acceptSqlConversion();
        }
        // Update the SqlConversion Panel
        updateSymbolConversionPanel(binding);

        
        bindingListInput.datatypeChanged();
        // Refresh table and message area
        bindingTableViewer.refresh(true);
        updateRowColors();
        updateMessageArea();

        selectBinding(binding);
    }


    /**
     * Handler for Table Selection changed
     */
    @Override
	public void selectionChanged( SelectionChangedEvent event ) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        Binding binding = null;
        if (SelectionUtilities.isSingleSelection(selection)) {
            Object o = SelectionUtilities.getSelectedObject(selection);
            binding = (Binding)o;
        } else if (SelectionUtilities.isMultiSelection(selection)) {
            List objects = SelectionUtilities.getSelectedObjects(selection);
            if (objects.size() > 0) binding = (Binding)objects.get(0);

        }
        // Update Displays
        updateDisplaysOnTableSelection(binding);
    }

    /**
     * Return the current selected datatype binding
     * 
     * @return the selected Binding
     */
    public Binding getSelectedBinding() {
        Binding selectedBinding = null;
        IStructuredSelection selection = (IStructuredSelection)bindingTableViewer.getSelection();
        if (selection != null) {
            Object elem = selection.getFirstElement();
            if (elem != null && elem instanceof Binding) {
                selectedBinding = (Binding)elem;
            }
        }
        return selectedBinding;
    }

    /**
     * Select the first Binding in the binding list
     */
    private void selectBinding( Binding binding ) {
        if (binding != null) {
            bindingTableViewer.setSelection(new StructuredSelection(binding), true);
        }
    }

    /**
     * Select the first Binding in the binding list
     */
    private void selectFirstBinding() {
        if (bindingListInput.getBindingList().size() > 0) {
            Binding binding = bindingListInput.getBindingList().get(0);
            bindingTableViewer.setSelection(new StructuredSelection(binding), true);
        }
    }

    /**
     * Select the first Binding in the binding list which has a type conflict
     */
    private void selectFirstTypeConflict() {
        Binding nextSelection = bindingListInput.getBindingList().getFirstTypeConflict();
        if (nextSelection != null) {
            bindingTableViewer.setSelection(new StructuredSelection(nextSelection), true);
        } else {
            selectFirstBinding();
        }
    }

    /**
     * Check whether there are any modifications to the SQL Symbols
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasSqlSymbolModifications() {
        boolean result = false;
        // If any of the newSymbols is non-null, there are type modifications
        for (int i = 0; i < bindingListInput.getBindingList().size(); i++) {
            Binding binding = bindingListInput.getBindingList().get(i);
            if (binding.sqlSymbolWasConverted()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Check whether there are any modifications to the target attribute types
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasAttributeTypeModifications() {
        boolean result = false;
        // If any of the newSymbols is non-null, there are type modifications
        for (int i = 0; i < bindingListInput.getBindingList().size(); i++) {
            Binding binding = bindingListInput.getBindingList().get(i);
            if (binding.hasAttrTypeModification()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Check whether there are any modifications to the target attribute types or sql symbols
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasModifications() {
        boolean result = false;
        if (hasSqlSymbolModifications() || hasAttributeTypeModifications()) {
            result = true;
        }
        return result;
    }

    /**
     * Accept all of the binding type modifications. This will go thru the Binding modifications and make them permanent.
     */
    public void applyBindingTypeModifications() {
        // If any of the newSymbols is non-null, there are type modifications
        for (int i = 0; i < bindingListInput.getBindingList().size(); i++) {
            Binding originalBinding = originalBindingList.get(i);
            Binding binding = bindingListInput.getBindingList().get(i);
            // Change the Attribute Types if required
            if (binding.hasAttrTypeModification()) {
                originalBinding.setNewAttrDatatype(binding.getCurrentAttrDatatype());
            }
            // Set the SqlSymbol on the original Binding if required
            if (binding.sqlSymbolWasConverted()) {
                originalBinding.setNewSymbol(binding.getCurrentSymbol());
            }
        }
    }

    /**
     * Clear all of the binding type modifications. This will go thru the Binding modifications and make them permanent.
     */
    public void clearBindingTypeModifications() {
        // If any of the newSymbols is non-null, there are type modifications
        for (int i = 0; i < bindingListInput.getBindingList().size(); i++) {
            Binding binding = bindingListInput.getBindingList().get(i);
            // Change the Attribute Types if required
            if (binding.hasAttrTypeModification()) {
                binding.setNewAttrDatatype(null);
            }
            // Set the SqlSymbol on the original Binding if required
            if (binding.sqlSymbolWasConverted()) {
                binding.undoSqlConversion();
            }
        }
    }
    
    private String getSpaces(int nSpaces) {
    	StringBuffer sb = new StringBuffer(nSpaces);
    	for( int i=0; i<nSpaces; i++ ) {
    		sb.append(StringUtilities.SPACE);
    	}
    	return sb.toString();
    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class BindingContentProvider implements IStructuredContentProvider {

        @Override
		public void inputChanged( Viewer v,
                                  Object oldInput,
                                  Object newInput ) {
        }

        @Override
		public void dispose() {
        }

        // Return the bindings as an array of Objects
        @Override
		public Object[] getElements( Object parent ) {
            return bindingListInput.getBindingList().getAll().toArray();
        }
    }
    
    class BindingTableInput {
    	BindingList bindingList;
    	Map<Binding, EObject> targetDatatypeMap = new HashMap<Binding, EObject>();
    	Map<Binding, Boolean> reconciledList = new HashMap<Binding, Boolean>();
    	
    	public BindingTableInput(BindingList bindingList) {
    		super();
    		this.bindingList = bindingList;
    		
    		for( Object obj : bindingList.getAll().toArray() ) {
    			Binding binding = (Binding)obj;
    			
	            String runtimeType = binding.getCurrentSymbolRuntimeType();
	            // Get default datatype for it
	            EObject datatype = TransformationMappingHelper.getDefaultDatatypeForRuntimeTypeName(runtimeType);
	            targetDatatypeMap.put(binding, datatype);
    		}
    		
    		datatypeChanged();
    	}
    	
    	public BindingList getBindingList() {
    		return this.bindingList;
    	}
    	
    	public void datatypeChanged() {
    		for( Object obj : bindingList.getAll().toArray() ) {
    			Binding binding = (Binding)obj;

	            reconciledList.put(binding, Boolean.valueOf(!binding.hasTypeConflict()));
    		}
    	}
    	
    	public EObject getTargetDatatype(Binding binding) {
    		return targetDatatypeMap.get(binding);
    	}
    	
    	public boolean isReconciled(Binding binding) {
    		return reconciledList.get(binding).booleanValue();
    	}
    	
    	public void setTargetDatatype(Binding binding, EObject dType) {
    		targetDatatypeMap.put(binding, dType);
    		datatypeChanged();
    	}
    	
    	public void setDatatypeOnBinding(Binding binding) {
    		binding.setNewAttrDatatype(getTargetDatatype(binding));
    		datatypeChanged();
    	}
    }
    
	class TheBindingColumnLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public TheBindingColumnLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			
	        String result = PluginConstants.EMPTY_STRING;
	        Binding binding = (Binding) element;
	        switch (this.columnNumber) {
	        	// columnNames = new String[] {"", "Target Column", "Matched Type", "Change Type", "", "Source SQL Symbol"};
	            case 0:  // FIX COLUMN
	                break;
	            case 1 : // TARGET COLUMN
	                result = binding.getAttributeText(true);
	                break;
	            case 2 : // MATCHED TYPE
	                Object attr = binding.getAttribute();
	                // Attribute Datatype Label
	                EObject datatype = null;
	                attr = binding.getAttribute();
	                if (TransformationHelper.isSqlColumn(attr)) {
	                    datatype = bindingListInput.getTargetDatatype(binding);
	                }
	                result = getDatatypeText(datatype);
	                break;
	            case 3 : // CONVERT BUTTON
	                //result = binding.getSqlSymbolText(true);
	                break;
	            case 4 : // SQL Symbol Column
	                result = binding.getSqlSymbolText(true);
	                break;

	            default :
	                break;  
	        }
	        return result;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			return PluginConstants.EMPTY_STRING;
		}

		@Override
		public Image getImage(Object element) {
            Image image = null;
	        Binding binding = (Binding) element;
	        switch (this.columnNumber) {
	        	case 0:  // FIX BUTTON
	            	if( bindingListInput.isReconciled(binding) ) {
	            		image = UiPlugin.getDefault().getImage(PluginConstants.Images.CHANGED_BUTTON_DISABLED_ICON);
	            	} else {
	            		image = UiPlugin.getDefault().getImage(PluginConstants.Images.CHANGED_BUTTON_ICON);
	            	}
	        	break;
	            case 1:  // TARGET COLUMN
	                break;
	            case 2:  // MATCHED TYPE
	            	image = UiPlugin.getDefault().getImage(PluginConstants.Images.ELIPSIS_LONG_BUTTON_ICON);
	                break;
	            case 3:  // Attribute Column
	            	if( bindingListInput.isReconciled(binding) ) {
	            		image = UiPlugin.getDefault().getImage(PluginConstants.Images.CONVERT_BUTTON_DISABLED_ICON);
	            	} else {
	            		image = UiPlugin.getDefault().getImage(PluginConstants.Images.CONVERT_BUTTON_ICON);
	            	}
	        	break;
	            default :
	                break;  
	        }
	        return image;
		}
	}
	
	class ChangeDatatypeEditingSupport extends CheckBoxEditingSupport {

		public ChangeDatatypeEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setElementValue(Object element, Object newValue) {
			if (element instanceof Binding && newValue instanceof Boolean) {
				convertSelectedAttrPressed();
			}
		}

	}
	
	class ConvertSymbolEditingSupport extends CheckBoxEditingSupport {

		public ConvertSymbolEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setElementValue(Object element, Object newValue) {
			if (element instanceof Binding && newValue instanceof Boolean) {
				convertSelectedSqlPressed();
			}
		}

	}
	
	// 
	class ChangeProposedDatatypeEditingSupport extends CheckBoxEditingSupport {

		public ChangeProposedDatatypeEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setElementValue(Object element, Object newValue) {
			if (element instanceof Binding && newValue instanceof Boolean) {
		        Binding binding = (Binding)element;

	            Object attr = binding.getAttribute();
	            
	            if (TransformationHelper.isSqlColumn(attr)) {
	                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
	                DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(shell, (EObject)attr, "string"); //$NON-NLS-1$
	                Object originalValue = bindingListInput.getTargetDatatype(binding);
	                
	                Object[] selection = new Object[] {originalValue};
	                
	                selection[0] = originalValue;
	                dialog.setInitialSelections(selection);

	                int status = dialog.open();
	                EObject newDatatype = (EObject)originalValue;
	                if (status == Window.OK) {
	                    Object[] result = dialog.getResult();
	                    if (result.length == 0) {
	                        // null out the value
	                        newDatatype = null;
	                    } else {
	                        // return the selected value
	                        newDatatype = (EObject)result[0];
	                    }
	                }
	                // If different datatype was chosen, set it on the binding
	                if (newDatatype!=null && !newDatatype.equals(originalValue)) {
	                    bindingListInput.setTargetDatatype(binding, newDatatype);
	                    updateAttributeConversionPanelButtons(binding);
	                }
	            }

	            // chooserPanel.layout();
	            bindingTableViewer.refresh(true);
	            updateRowColors();
	            updateMessageArea();

			}
		}

	}
}
