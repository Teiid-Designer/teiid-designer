/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import java.util.Properties;
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
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlTextViewer;
import org.teiid.designer.ui.common.graphics.ColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;


/**
 * @since 8.0
 */
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
				@Override
				public void modifyText(ModifyEvent e) {
					handleWrapperProcedureNameChanged();
				}
			});
			wrapperProcedureText.setEnabled(true);
		}

		SQL_TEXT_VIEWER: {
			Group group = WidgetFactory.createGroup(parent,Messages.GeneratedSQLStatement, SWT.NONE, 2);
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

		validate();

		updateUi();
	}

	private void updateUi() {
		this.wrapperProcedureText.setText(detailsPage.getProcedureGenerator().getWrapperProcedureName());
		this.sqlTextViewer.getDocument().set(detailsPage.getProcedureGenerator().getWrapperSqlString());
	}

	private void handleWrapperProcedureNameChanged() {
		this.detailsPage.getProcedureGenerator().setWrapperProcedureName(this.wrapperProcedureText.getText());
		this.sqlTextViewer.getDocument().set(detailsPage.getProcedureGenerator().getWrapperSqlString());
		validate();
	}

	private void validate() {
		this.detailsPage.updateStatus();
		updateDesignerProperties();
	}

	public IStatus getStatus() {
		return this.status;
	}
	
	private void updateDesignerProperties() {
    	
		if( this.detailsPage.getProcedureGenerator().getWrapperProcedureName() != null ) {
            Properties designerProperties = this.detailsPage.getImportManager().getDesignerProperties();
            if (designerProperties != null) {
                DesignerPropertiesUtil.setPreviewTargetObjectName(designerProperties,
                                                                  this.detailsPage.getProcedureGenerator().getWrapperProcedureName());
                DesignerPropertiesUtil.setPreviewTargetModelName(designerProperties,
                                                                 this.detailsPage.getImportManager().getViewModelName());
            }
		}
	}
	
	public void setVisible() {
		validate();
	}
}