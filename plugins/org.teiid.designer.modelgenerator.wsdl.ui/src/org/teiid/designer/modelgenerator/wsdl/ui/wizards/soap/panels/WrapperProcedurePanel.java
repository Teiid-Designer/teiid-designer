/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.ui.graphics.ColorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class WrapperProcedurePanel {

	Text wrapperProcedureText;
	SqlTextViewer sqlTextViewer;
	IDocument sqlDocument;

	IStatus status;

	final OperationsDetailsPage detailsPage;

	public WrapperProcedurePanel(Composite parent, OperationsDetailsPage detailsPage) {
		super();
		this.detailsPage = detailsPage;
		init(parent);
	}

	@SuppressWarnings("unused")
	private void init(Composite parent) {

		WRAPPER_GROUP: {

			Label procedureNameLabel = new Label(parent, SWT.NONE);
			procedureNameLabel.setText(Messages.GeneratedProcedureName);
			
			this.wrapperProcedureText = new Text(parent, SWT.BORDER | SWT.SINGLE);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			wrapperProcedureText.setLayoutData(gridData);
			wrapperProcedureText.setForeground(WidgetUtil.getDarkBlueColor());
			wrapperProcedureText.setEditable(true);
			wrapperProcedureText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					handleWrapperProcedureNameChanged();
				}
			});
			wrapperProcedureText.setEnabled(true);
		}

		SQL_TEXT_VIEWER: {
			Group group = WidgetFactory.createGroup(parent,Messages.GeneratedSQLStatement, SWT.NONE, 2);
			group.setLayout(new GridLayout(1, false));
			GridData gd = new GridData(GridData.FILL_BOTH);
			group.setLayoutData(gd);
			
			ColorManager colorManager = new ColorManager();
			int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

			sqlTextViewer = new SqlTextViewer(group, new VerticalRuler(0), styles, colorManager);
			sqlDocument = new Document();
			sqlTextViewer.setInput(sqlDocument);
			sqlTextViewer.setEditable(false);
			sqlTextViewer.getTextWidget().setEnabled(true);
			sqlTextViewer.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			sqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
			sqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		}
	}

	public void notifyOperationChanged(Operation operation) {

		updateUi();

		validate();
	}

	private void updateUi() {
		this.wrapperProcedureText.setText(detailsPage.getProcedureGenerator().getWrappedProcedureName());
		this.sqlTextViewer.getDocument().set(detailsPage.getProcedureGenerator().getWrapperSqlString());
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