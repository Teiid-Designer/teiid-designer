/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class OptionsPanel {

	Button overwriteButton;

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

	}

	public void notifyOperationChanged(Operation operation) {
		this.overwriteButton.setSelection(this.detailsPage.getProcedureGenerator().doOverwriteExistingProcedures());

		updateUi();

		validate();
	}

	private void updateUi() {
		if (this.detailsPage.getProcedureGenerator() != null) {
			// TODO: Set check-box state?
		}
	}

	private void validate() {
		// TODO:
	}

	public IStatus getStatus() {
		return this.status;
	}

}
