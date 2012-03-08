/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class OptionsPanel {

    Button generateButton;

    Button overwriteButton;

    Label wrapperLabel;
    Text wrapperProcedureText;

    IStatus status;

    final OperationsDetailsPage detailsPage;

    public OptionsPanel(Composite parent, OperationsDetailsPage detailsPage) {
	super();
	this.detailsPage = detailsPage;
	init(parent);
    }

    @SuppressWarnings("unused")
    private void init(Composite parent) {
	Group optionsGroup = WidgetFactory.createGroup(parent, Messages.Options, GridData.FILL_BOTH, 1, 2);

	this.overwriteButton = WidgetFactory.createCheckBox(optionsGroup, Messages.OverwriteExistingProcedures);
	this.overwriteButton.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		detailsPage.getProcedureGenerator().setOverwriteExistingProcedures(overwriteButton.getSelection());
		validate();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	    }
	});

	GridData gd = new GridData();
	gd.horizontalSpan = 2;
	overwriteButton.setLayoutData(gd);

	WRAPPER_GROUP: {
	    Group wrapperGroup = WidgetFactory.createGroup(optionsGroup, Messages.WrapperProcedure, GridData.FILL_HORIZONTAL,
		1, 2);

	    this.generateButton = WidgetFactory.createCheckBox(wrapperGroup, Messages.GenerateWrapperProcedure);
	    this.generateButton.addSelectionListener(new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent e) {
		    detailsPage.getProcedureGenerator().setGenerateWrapperProcedure(generateButton.getSelection());
		    updateUi();
		    validate();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	    });

	    gd = new GridData();
	    gd.horizontalSpan = 2;
	    generateButton.setLayoutData(gd);

	    wrapperLabel = new Label(wrapperGroup, SWT.NONE);
	    wrapperLabel.setText(Messages.Name);
	    this.wrapperProcedureText = new Text(wrapperGroup, SWT.BORDER | SWT.SINGLE);
	    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	    wrapperProcedureText.setLayoutData(gridData);
	    wrapperProcedureText.setForeground(WidgetUtil.getDarkBlueColor());
	    wrapperProcedureText.setEditable(true);
	    wrapperProcedureText.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
		    handleWrapperProcedureNameChanged();
		}
	    });
	    wrapperLabel.setEnabled(false);
	    wrapperProcedureText.setEnabled(false);
	}
    }

    public void notifyOperationChanged(Operation operation) {
	this.generateButton.setSelection(this.detailsPage.getProcedureGenerator().doGenerateWrapperProcedure());
	this.overwriteButton.setSelection(this.detailsPage.getProcedureGenerator().doOverwriteExistingProcedures());

	updateUi();

	validate();
    }

    private void updateUi() {
	if (this.detailsPage.getProcedureGenerator() != null) {

	}
	if (this.generateButton.getSelection()) {
	    this.wrapperProcedureText.setText(detailsPage.getProcedureGenerator().getWrappedProcedureName());
	}
	this.wrapperLabel.setEnabled(this.generateButton.getSelection());
	this.wrapperProcedureText.setEnabled(this.generateButton.getSelection());
    }

    private void handleWrapperProcedureNameChanged() {
	this.detailsPage.getProcedureGenerator().setWrapperProcedureName(this.wrapperProcedureText.getText());
	validate();
    }

    private void validate() {
	// TODO:
    }

    public IStatus getStatus() {
	return this.status;
    }

}
