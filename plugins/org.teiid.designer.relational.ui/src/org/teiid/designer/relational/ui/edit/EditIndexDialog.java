/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.edit;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 *
 */
public class EditIndexDialog extends TitleAreaDialog {
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private final String CREATE_TITLE = Messages.createIndexTitle;
	private final String EDIT_TITLE = Messages.editIndexTitle;

	// =============================================================
	// Instance variables
	// =============================================================
	RelationalIndex originalIndex;
	RelationalIndex editedIndex;
	RelationalTable theTable;

	String selectedTableName;
	String selectedIndexName;
	
    private Button autoUpdateCB;
    private Text  filterConditionText;
    private Button nullableCB;
    private Button uniqueCB;
    StyledTextEditor descriptionTextEditor;

	TableViewer theColumnDataViewer;

	Set<RelationalColumn> selectedColumns = new HashSet<RelationalColumn>();

	boolean isEdit;

	boolean creatingContents = false;
	boolean processingChecks = false;

	// =============================================================
	// Constructors
	// =============================================================

	/**
	 * 
	 * @param parent the parent shell
	 * @param theTable the relational table object
	 * @param index the index being edited
	 * @param isEdit edit mode
	 */
	public EditIndexDialog(Shell parent, RelationalTable theTable, RelationalIndex index, boolean isEdit) {
		super(parent);
		this.theTable = theTable;
		this.isEdit = isEdit;
		boolean reallyIsEdit = isEdit;
		this.originalIndex = index;
		if (reallyIsEdit) {
			this.editedIndex = this.originalIndex.clone();
		}
		if (!reallyIsEdit) {
			this.editedIndex = index;
		}
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (isEdit) {
			shell.setText(EDIT_TITLE);
		} else {
			shell.setText(CREATE_TITLE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#setShellStyle(int)
	 */
	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

	}

	// =============================================================
	// Instance methods
	// =============================================================

	@SuppressWarnings("unused")
	@Override
	protected Control createDialogArea(Composite parent) {
		creatingContents = true;
		if (isEdit) {
			setTitle(EDIT_TITLE);
		} else {
			setTitle(CREATE_TITLE);
		}

		Composite dialogComposite = (Composite) super.createDialogArea(parent);

		Composite composite = WidgetFactory.createPanel(dialogComposite);
		// ------------------------------
		// Set layout for the Composite
		// ------------------------------
		GridLayout gridLayout = new GridLayout();
		composite.setLayout(gridLayout);
		gridLayout.numColumns = 2;
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 500;
		composite.setLayoutData(gridData);

		Label label = new Label(composite, SWT.NONE | SWT.RIGHT);
		label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
		label.setLayoutData(new GridData());

		final Text indexNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		indexNameText.setForeground(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_BLUE));
		indexNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		indexNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				String value = indexNameText.getText();
				if (value == null) {
					value = EMPTY_STRING;
				}
				editedIndex.setName(value);
				validate();
			}
		});

		label = new Label(composite, SWT.NONE | SWT.RIGHT);
		label.setText(Messages.nameInSourceLabel);
		label.setLayoutData(new GridData());

		final Text indexNISText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		indexNISText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		indexNISText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		indexNISText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				String value = indexNISText.getText();
				if (value == null) {
					value = EMPTY_STRING;
				}
				editedIndex.setNameInSource(value);
				validate();
			}
		});

		// Group to present properties widgets
		PROPERTIES_GROUP : {
	        this.autoUpdateCB = new Button(composite, SWT.CHECK | SWT.RIGHT);
	        this.autoUpdateCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	        this.autoUpdateCB.setText(Messages.autoUpdateLabel);
	        this.autoUpdateCB.addSelectionListener(new SelectionAdapter() {
	            /**            		
	             * {@inheritDoc}
	             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	             */
	            @Override
	            public void widgetSelected( SelectionEvent e ) {
	            	editedIndex.setAutoUpdate(autoUpdateCB.getSelection());
	            	validate();
	            }
	        });
	        new Label(composite, SWT.NONE);
	        
	        this.nullableCB = new Button(composite, SWT.CHECK | SWT.RIGHT);
	        this.nullableCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	        this.nullableCB.setText(Messages.nullableLabel);
	        this.nullableCB.addSelectionListener(new SelectionAdapter() {
	            /**            		
	             * {@inheritDoc}
	             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	             */
	            @Override
	            public void widgetSelected( SelectionEvent e ) {
	            	editedIndex.setNullable(nullableCB.getSelection());
	            	validate();
	            }
	        });
	        new Label(composite, SWT.NONE);
	        
	        this.uniqueCB = new Button(composite, SWT.CHECK | SWT.RIGHT);
	        this.uniqueCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	        this.uniqueCB.setText(Messages.uniqueLabel);
	        this.uniqueCB.addSelectionListener(new SelectionAdapter() {
	            /**            		
	             * {@inheritDoc}
	             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	             */
	            @Override
	            public void widgetSelected( SelectionEvent e ) {
	            	editedIndex.setUnique(uniqueCB.getSelection());
	            	validate();
	            }
	        });
	        new Label(composite, SWT.NONE);
	        
			label = new Label(composite, SWT.NONE | SWT.RIGHT);
			label.setText(Messages.filterConditionLabel);
			label.setLayoutData(new GridData());

			filterConditionText = new Text(composite, SWT.BORDER | SWT.SINGLE);
			filterConditionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
			filterConditionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			filterConditionText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent event) {
					String value = filterConditionText.getText();
					if (value == null) {
						value = EMPTY_STRING;
					}
					editedIndex.setFilterCondition(value);
					validate();
				}
			});
		}

		Group theColumnsGroup = WidgetFactory.createGroup(dialogComposite, Messages.selectColumnReferencesForIndex, SWT.NONE, 1, 1);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 120;
		gd.widthHint = 500;
		theColumnsGroup.setLayoutData(gd);

		Table tableWidget = new Table(theColumnsGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
		tableWidget.setHeaderVisible(false);
		tableWidget.setLinesVisible(true);
		tableWidget.setLayout(new TableLayout());
		tableWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		theColumnDataViewer = new TableViewer(tableWidget);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 160;
		gd.horizontalSpan = 2;
		theColumnDataViewer.getControl().setLayoutData(gd);
		theColumnDataViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// TODO Auto-generated method stub
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub
			}

			@Override
			public boolean hasChildren(Object element) {
				return !theTable.getColumns().isEmpty();
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof RelationalTable) {
					return theTable.getColumns().toArray(new Object[0]);
				}
				return new Object[0];
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				// TODO Auto-generated method stub
				return new Object[0];
			}
		});

		this.theColumnDataViewer.getTable().addSelectionListener(
				new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						editedIndex.getColumns().clear();
						for (TableItem item : theColumnDataViewer.getTable()
								.getItems()) {

							if (item.getChecked()) {
								editedIndex.addColumn((RelationalColumn) item
										.getData());
							}
						}
						validate();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

		theColumnDataViewer.setLabelProvider(new ColumnDataLabelProvider(0));

		theColumnDataViewer.setInput(this.theTable);

		for (RelationalColumn col : this.editedIndex.getColumns()) {
			for (TableItem item : theColumnDataViewer.getTable().getItems()) {
				if (item.getData() == col) {
					item.setChecked(true);
				}
			}
		}
		
        DESCRIPTION_GROUP: {
            final Group descGroup = WidgetFactory.createGroup(dialogComposite, UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION), GridData.FILL_BOTH, 3);
            descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
            final GridData descGridData = new GridData(GridData.FILL_BOTH);
            descGridData.horizontalSpan = 1;
            descGridData.heightHint = 120;
//            descGridData.minimumHeight = 30;
            descGridData.grabExcessVerticalSpace = true;
            descriptionTextEditor.setLayoutData(descGridData);
            descriptionTextEditor.setText(""); //$NON-NLS-1$
            descriptionTextEditor.getTextWidget().addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					editedIndex.setDescription(descriptionTextEditor.getText());
				}
			});
        }

		setMessage(Messages.newIndexMessage);
		if (editedIndex.getName() != null) {
			indexNameText.setText(editedIndex.getName());
		}
		if (editedIndex.getNameInSource() != null) {
			indexNISText.setText(editedIndex.getNameInSource());
		}
		this.autoUpdateCB.setSelection(editedIndex.isAutoUpdate());
		this.nullableCB.setSelection(editedIndex.isNullable());
		this.uniqueCB.setSelection(editedIndex.isUnique());
		if (editedIndex.getFilterCondition() != null) {
			filterConditionText.setText(editedIndex.getFilterCondition());
		}
		if( editedIndex.getDescription() != null ) {
			descriptionTextEditor.setText(editedIndex.getDescription());
		}
		
		creatingContents = false;

		return composite;
	}

	private void validate() {
		if (creatingContents)
			return;

		editedIndex.validate();

		boolean enable = true;
		setMessage(Messages.newIndexMessage);
		// ONLY DISABLE if NAME == null
		if (editedIndex.getName() == null || editedIndex.getName().trim().length() == 0) {
			enable = false;
			setErrorMessage(editedIndex.getStatus().getMessage());
		} else {
			if (editedIndex.getStatus().getSeverity() < IStatus.ERROR) {
				setErrorMessage(null);
			} else if (editedIndex.getStatus().getSeverity() == IStatus.WARNING) {
				setMessage(editedIndex.getStatus().getMessage(), IMessageProvider.WARNING);
			} else if (editedIndex.getStatus().getSeverity() == IStatus.ERROR) {
				setErrorMessage(editedIndex.getStatus().getMessage());
			}
		}

		getButton(IDialogConstants.OK_ID).setEnabled(enable);
	}

	@Override
	public void create() {
		super.create();
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	@Override
	protected void okPressed() {
		if (isEdit) {
			this.originalIndex.inject(editedIndex);
		}

		super.okPressed();
	}

	/**
	 * @return the table name
	 */
	public String getTableName() {
		return this.selectedTableName;
	}

	/**
	 * @return the index name
	 */
	public String getIndexName() {
		return this.selectedIndexName;
	}

	class ColumnDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ColumnDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof RelationalColumn) {
				switch (this.columnNumber) {
					case 0: {
						return ((RelationalColumn) element).getName();
					}
					case 1: {
						return ((RelationalColumn) element).getDatatype();
					}
					case 2: {
						return Integer.toString(((RelationalColumn) element).getLength());
					}
				}
			}
			return EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			switch (this.columnNumber) {
			case 0: {
				return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
			}
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
			}
			return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if (this.columnNumber == 0) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
			}
			return null;
		}

	}

}
