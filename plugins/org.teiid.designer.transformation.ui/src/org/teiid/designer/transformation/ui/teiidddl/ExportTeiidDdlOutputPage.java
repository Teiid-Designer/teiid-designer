package org.teiid.designer.transformation.ui.teiidddl;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

public class ExportTeiidDdlOutputPage  extends AbstractWizardPage implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExportTeiidDdlOutputPage.class);
	
	private static final String TITLE = "Export Model as Teiid DDL"; //getString("title"); //$NON-NLS-1$
	private static final String FILE_GROUP = "Output File Definition"; //$NON-NLS-1$
	private static final String FILE_LABEL = "Name"; //$NON-NLS-1$
	private static final String FILE_BUTTON = "...";
	private static final String FILE_DIALOG_TITLE = "Select DDL File for Export"; //getString("ExportTeiidDdlWizard_fileDialogTitle"); //$NON-NLS-1$

	private static final String DDL_EXTENSION = FileUtils.Constants.FILE_EXTENSION_SEPARATOR + "ddl"; //$NON-NLS-1$


	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}
	
	private Combo fileCombo;

	private final TeiidDdlExporter exporter;

	public ExportTeiidDdlOutputPage(TeiidDdlExporter exporter) {
		super(ExportTeiidDdlOutputPage.class.getSimpleName(), TITLE);
		this.exporter = exporter;
	}

	@Override
	public void createControl(Composite parent) {
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(2, false));
		mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);

		setMessage("Select DDL File for export");
	}
	
	private void createExportToSection(final Composite pg,
			final IDialogSettings settings) {
		Group exportToGroup = WidgetFactory.createGroup(pg, FILE_GROUP,
				GridData.FILL_HORIZONTAL, 1, 1);

		Composite buttonComposite = new Composite(exportToGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonComposite);
		GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 10).applyTo(buttonComposite);

		final Composite exportToFilePanel = new Composite(exportToGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 50).applyTo(exportToFilePanel);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(exportToFilePanel);

		/* Contents of button composite */

		Button clipboardButton = WidgetFactory.createButton(buttonComposite,
				TeiidDdlExporter.CLIPBOARD_TYPE.getLabel(),
				GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL, 1,
				SWT.RADIO);
		clipboardButton.setToolTipText(getString("ExportTeiidDdlWizard_clipboardTooltip")); //$NON-NLS-1$
		clipboardButton.setSelection(true);
		clipboardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exporter.setExportType(TeiidDdlExporter.CLIPBOARD_TYPE);
				validatePage();
				exportToFilePanel.setVisible(false);
			}
		});

		Button fileButton = WidgetFactory.createButton(buttonComposite,
				TeiidDdlExporter.FILE_TYPE.getLabel(), GridData.HORIZONTAL_ALIGN_CENTER
						| GridData.GRAB_HORIZONTAL, 1, SWT.RADIO);
		fileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exporter.setExportType(TeiidDdlExporter.FILE_TYPE);
				validatePage();
				exportToFilePanel.setVisible(true);
			}
		});

		/* Contents of export file panel */

		WidgetFactory.createLabel(exportToFilePanel, FILE_LABEL);
		this.fileCombo = WidgetFactory.createCombo(exportToFilePanel, SWT.NONE,
				GridData.FILL_HORIZONTAL, settings.getArray(FILE_LABEL));
		this.fileCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				fileModified();
			}
		});

		WidgetFactory.createButton(exportToFilePanel, FILE_BUTTON)
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						fileButtonSelected();
					}
				});

		exportToFilePanel.setVisible(false);
	}
	
	/**
	 * <p>
	 * </p>
	 * 
	 * @since 4.0
	 */
	private void fileButtonSelected() {
		// Display file dialog for user to choose libraries
		final FileDialog dlg = new FileDialog(getShell(), SWT.SAVE | SWT.SINGLE);
		dlg.setFilterExtensions(new String[] { "*.ddl", "*.sql", "*.*" }); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		dlg.setText(FILE_DIALOG_TITLE);
		final String file = dlg.open();
		if (file != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(file);
			if (file.indexOf('.') < 0) {
				buffer.append(DDL_EXTENSION);
			}
			this.fileCombo.setText(buffer.toString());
			this.exporter.setDdlFile(new File(file));
		}
		validatePage();
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @since 4.0
	 */
	private void fileModified() {
		String file = this.fileCombo.getText();
		if (file != null && file.length() > 0) {
			final char lastChr = file.charAt(file.length() - 1);
			if (file.indexOf(FileUtils.Constants.FILE_EXTENSION_SEPARATOR) < 0 && lastChr != ':'
					&& lastChr != '\\' && lastChr != '/') {
				file += DDL_EXTENSION;
			}
			this.exporter.setDdlFile(new File(file));
		}
		validatePage();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		// TODO Auto-generated method stub
		return true;
	}
	
	private boolean validatePage() {
		IStatus status = exporter.validate();
		return true;
	}
	

}

