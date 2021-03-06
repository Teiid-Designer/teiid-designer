/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.edit;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.teiid.designer.relational.RelationalConstants.DIRECTION;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 *
 */
public class EditParameterDialog extends TitleAreaDialog {
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	// =============================================================
	// Instance variables
	// =============================================================
	RelationalParameter origParameter;
	RelationalParameter tempParameter;
	
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
	public EditParameterDialog(Shell parent, RelationalParameter parameter) {
		super(parent);
		this.origParameter = parameter;
		tempParameter = new RelationalParameter();
		tempParameter.setDatatype(origParameter.getDatatype());
		tempParameter.setName(origParameter.getName());
		tempParameter.setLength(origParameter.getLength());
		tempParameter.setDirection(origParameter.getDirection());
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.EditParameterTitle);
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
		setTitle(Messages.EditParameterTitle);
		setMessage(NLS.bind(Messages.EditingParameterInformation, origParameter.getName()), IMessageProvider.INFORMATION);

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
		columnNameText.setText(origParameter.getName());
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
				tempParameter.setName(value);
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
			dTypes.add(dType);
		}
		
		String[] datatypes = dTypes.toArray(new String[dTypes.size()]);
		datatypeCombo.setItems(datatypes);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(datatypeCombo);
		datatypeCombo.setText(origParameter.getDatatype());
		datatypeCombo.redraw();
		datatypeCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				tempParameter.setDatatype(datatypeCombo.getText());
				validate();
			}
		});
		
		// ------------------------------
		// Length value
		// ------------------------------
		Label label1 = new Label(composite, SWT.NONE | SWT.SINGLE);
		label1.setText(Messages.lengthLabel);
		label1.setLayoutData(new GridData());

		final Text lengthValueText = new Text(composite, SWT.BORDER | SWT.NONE);
		lengthValueText.setText(String.valueOf(origParameter.getLength()));
		lengthValueText.setForeground(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_BLUE));
		lengthValueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lengthValueText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				String value = lengthValueText.getText();
				if (value == null) {
					value = EMPTY_STRING;
				}
				IStatus lengthCheck = validateLength(value);
				if( lengthCheck.isOK() ) {
					tempParameter.setLength(Integer.parseInt(value));
					validate();
				} else {
					setErrorMessage(lengthCheck.getMessage());
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
			}
		});
		
		// ------------------------------
		// Direction value
		// ------------------------------
		Label label2 = new Label(composite, SWT.NONE | SWT.NONE);
		label2.setText(Messages.directionLabel);
		label2.setLayoutData(new GridData());

		final Combo directionCombo = new Combo(composite, SWT.READ_ONLY);
		directionCombo.setForeground(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_BLUE));
		directionCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		directionCombo.setItems(DIRECTION.AS_ARRAY);
		directionCombo.setText(origParameter.getDirection());
		directionCombo.redraw();
		directionCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				tempParameter.setDirection(directionCombo.getText());
				validate();
			}
		});

		return composite;
	}
	
	private void validate() {
		this.tempParameter.validate();
		IStatus status = this.tempParameter.getStatus();
		if( status.getSeverity() == IStatus.ERROR ) {
			setErrorMessage(status.getMessage());
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else {
			setErrorMessage(null);
			setMessage(Messages.ClickOkToAcceptChanges);
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
	}
	
	private IStatus validateLength(String lengthStr) {
		try {
			Integer.parseInt(lengthStr);
		} catch (NumberFormatException e) {
			return new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, NLS.bind(Messages.ColumnLengthError, lengthStr));
		}
		return Status.OK_STATUS;
	}

	@Override
	public void create() {
		super.create();
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	@Override
	protected void okPressed() {
		origParameter.setDatatype(tempParameter.getDatatype());
		origParameter.setName(tempParameter.getName());
		origParameter.setLength(tempParameter.getLength());
		origParameter.setDirection(tempParameter.getDirection());
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
				case 3: {
					return (((ITeiidXmlColumnInfo) element).getRelativePath());
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
						UiConstants.Images.PARAMETER_ICON);
			}
			return null;
		}

	}

}