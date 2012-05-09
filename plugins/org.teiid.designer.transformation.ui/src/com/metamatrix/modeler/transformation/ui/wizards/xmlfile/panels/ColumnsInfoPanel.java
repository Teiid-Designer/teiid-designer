/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile.panels;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.Messages;
import com.metamatrix.modeler.transformation.ui.wizards.xmlfile.TeiidXmlColumnInfo;
import com.metamatrix.modeler.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import com.metamatrix.modeler.transformation.ui.wizards.xmlfile.TeiidXmlImportXmlConfigurationPage;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class ColumnsInfoPanel {
	
	private Button addButton, deleteButton, upButton, downButton;
	private Text rootPathText;

	TreeViewer schemaTreeViewer;

	EditColumnsPanel editColumnsPanel;

	final TeiidXmlImportXmlConfigurationPage configPage;

	boolean refreshing = false;

	public ColumnsInfoPanel(Composite parent,
			TeiidXmlImportXmlConfigurationPage configPage) {
		super();
		this.configPage = configPage;
		init(parent);
	}

	public TeiidXmlFileInfo getFileInfo() {
		return this.configPage.getFileInfo();
	}

	public void refresh() {
		refreshing = true;
		this.editColumnsPanel.refresh();
		this.rootPathText.setText(this.getFileInfo().getRootPath());
		refreshing = false;
	}

	public Text getRootPathText() {
		return rootPathText;
	}

	private void init(Composite parent) {
		Group columnInfoGroup = WidgetFactory.createGroup(parent, Messages.ColumnInfo, SWT.NONE, 2);
		columnInfoGroup.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		columnInfoGroup.setLayoutData(gd);

		Label prefixLabel = new Label(columnInfoGroup, SWT.NONE);
		prefixLabel.setText(Messages.RootPath);
		prefixLabel.setToolTipText(Messages.RootPathTooltip);

		rootPathText = WidgetFactory.createTextField(columnInfoGroup, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 50;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		rootPathText.setLayoutData(gd);
		rootPathText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				handleRootPathChanged();
			}
		});
		rootPathText.setToolTipText(Messages.RootPathTooltip);

		Composite leftToolbarPanel = new Composite(columnInfoGroup, SWT.NONE);
		GridLayout tbGL = new GridLayout();
		tbGL.marginHeight = 0;
		tbGL.marginWidth = 0;
		tbGL.verticalSpacing = 2;
		leftToolbarPanel.setLayout(tbGL);
		GridData ltpGD = new GridData(GridData.FILL_VERTICAL);
		ltpGD.heightHint = 120;
		leftToolbarPanel.setLayoutData(ltpGD);

		addButton = new Button(leftToolbarPanel, SWT.PUSH);
		addButton.setText(Messages.addLabel);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setEnabled(true);
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				configPage.createColumn();

				editColumnsPanel.refresh();
				notifyColumnDataChanged();
			}

		});

		deleteButton = new Button(leftToolbarPanel, SWT.PUSH);
		deleteButton.setText(Messages.deleteLabel);
		deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		deleteButton.setEnabled(false);
		deleteButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidXmlColumnInfo info = editColumnsPanel.getSelectedColumn();
				if( info != null ) {
					getFileInfo().removeColumn(info);
					deleteButton.setEnabled(false);
					editColumnsPanel.selectRow(-1);
					notifyColumnDataChanged();
				}
			}

		});

		upButton = new Button(leftToolbarPanel, SWT.PUSH);
		upButton.setText(Messages.moveUpLabel);
		upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		upButton.setEnabled(false);
		upButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidXmlColumnInfo info = editColumnsPanel.getSelectedColumn();
				if (info != null) {
					int selectedIndex = editColumnsPanel.getSelectedIndex();
					getFileInfo().moveColumnUp(info);
					downButton.setEnabled(getFileInfo().canMoveDown(info));
					upButton.setEnabled(getFileInfo().canMoveUp(info));
					editColumnsPanel.refresh();
					notifyColumnDataChanged();

					editColumnsPanel.selectRow(selectedIndex - 1);
				}
			}

		});

		downButton = new Button(leftToolbarPanel, SWT.PUSH);
		downButton.setText(Messages.moveDownLabel);
		downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		downButton.setEnabled(false);
		downButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidXmlColumnInfo info = editColumnsPanel.getSelectedColumn();
				if (info != null) {
					int selectedIndex = editColumnsPanel.getSelectedIndex();
					getFileInfo().moveColumnDown(info);
					downButton.setEnabled(getFileInfo().canMoveDown(info));
					upButton.setEnabled(getFileInfo().canMoveUp(info));
					editColumnsPanel.refresh();
					notifyColumnDataChanged();

					editColumnsPanel.selectRow(selectedIndex + 1);
				}
			}

		});

		editColumnsPanel = new EditColumnsPanel(columnInfoGroup, this.configPage);

		editColumnsPanel.addSelectionListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteButton.setEnabled(false);
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					TeiidXmlColumnInfo columnInfo = null;
					for( Object obj : objs) {
						if(  !(obj instanceof TeiidXmlColumnInfo)) {
							enable = false;
							break;
						} else {
							columnInfo = (TeiidXmlColumnInfo)obj;
						}
					} 
					if( objs.length == 0 ) {
						enable = false;
					}
					deleteButton.setEnabled(enable);
					if( enable ) {
						upButton.setEnabled(getFileInfo().canMoveUp(columnInfo));
						downButton.setEnabled(getFileInfo().canMoveDown(columnInfo));
					}
					
				}

			}
		});
	}
	
	private void handleRootPathChanged() {
		String currentRootPath = this.getFileInfo().getRootPath();
		if( !StringUtilities.equals(currentRootPath, this.rootPathText.getText())) {
			this.getFileInfo().setRootPath(this.rootPathText.getText());
			notifyColumnDataChanged();
		}
	}

	private void notifyColumnDataChanged() {
		if (!refreshing) {
			this.configPage.handleInfoChanged(false);
		}
	}

	public void disableButtons() {
		deleteButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
	}

	public void setEnabled(boolean enable) {
		addButton.setEnabled(enable);
		disableButtons();
		rootPathText.setEnabled(enable);
		refresh();
		//editColumnsPanel.setEnabled(enable);
	}
	
	public void notifySelection(boolean selected) {
		addButton.setEnabled(selected);
	}

}