/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.file;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.proc.ITeiidXmlColumnInfo;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 *
 */
public class EditDelimitedColumnDialog extends TitleAreaDialog {
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	// =============================================================
	// Instance variables
	// =============================================================
	TeiidColumnInfo column;
	
	// =============================================================
	// Constructors
	// =============================================================

	/**
	 * 
	 * @param parent
	 *            the parent shell
	 * @param resultSetLabel 
	 * @param editingColumnInformation 
	 * @param relationalViewProcedure
	 *            the columnInfo table object
	 */
	public EditDelimitedColumnDialog(Shell parent, TeiidColumnInfo column) {
		super(parent);
		this.column = column;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.EditColumnTitle);
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

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.EditColumnTitle);
		setMessage(NLS.bind(Messages.EditingColumnInformation, column.getName()), IMessageProvider.INFORMATION);

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

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		// ------------------------------
		// Column Name
		// ------------------------------
		Label label = new Label(composite, SWT.NONE | SWT.SINGLE);
		label.setText(Messages.Name);
		label.setLayoutData(new GridData());

		final Text columnNameText = new Text(composite, SWT.BORDER | SWT.NONE);
		columnNameText.setText(column.getName());
		columnNameText.setForeground(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_BLUE));
		columnNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		columnNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				String value = columnNameText.getText();
				if (value == null) {
					value = EMPTY_STRING;
				}
				column.setName(value);
				validate();
			}
		});

		// ------------------------------
		// Data type dropdown
		// ------------------------------
		Label datatype = new Label(composite, SWT.BORDER | SWT.NONE);
		datatype.setText(Messages.dataTypeLabel);
		datatype.setLayoutData(new GridData());

		final Combo datatypeCombo = new Combo(composite,
				SWT.READ_ONLY);
		datatypeCombo.setForeground(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_BLUE));
		datatypeCombo.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, true, true));
		
		IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
		Set<String> unsortedDatatypes = service.getAllDataTypeNames();
		Collection<String> dTypes = new ArrayList<String>();
		
		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
		Arrays.sort(sortedStrings);
		for( String dType : sortedStrings ) {
			if (dType.equalsIgnoreCase("integer")){
				//skip
			}else{
				dTypes.add(dType);
			}
		}
		
		String[] datatypes = dTypes.toArray(new String[dTypes.size()]);
		datatypeCombo.setItems(datatypes);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(datatypeCombo);
		datatypeCombo.setText(column.getDatatype());
		datatypeCombo.redraw();
		datatypeCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				column.setDatatype(datatypeCombo.getText());
				validate();
			}
		});
		
		return composite;
	}
	
	private void validate() {
		IStatus status = this.column.getStatus();
		if( status.getSeverity() == IStatus.ERROR ) {
			setErrorMessage(status.getMessage());
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else {
			setErrorMessage(null);
			setMessage(Messages.ClickOkToAcceptChanges);
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
	}

	@Override
	public void create() {
		super.create();
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	@Override
	protected void okPressed() {

		super.okPressed();
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
			if (element instanceof ITeiidXmlColumnInfo) {
				switch (this.columnNumber) {
				case 0: {
					return ((ITeiidXmlColumnInfo) element).getName();
				}
				case 1: {
					return Boolean.toString(((ITeiidXmlColumnInfo) element)
							.getOrdinality());
				}
				case 2: {
					return ((ITeiidXmlColumnInfo) element).getDatatype();
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
				return UiPlugin.getDefault().getImage(
						UiConstants.Images.COLUMN_ICON);
			}
			return null;
		}

	}

}