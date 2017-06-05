/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */package org.teiid.designer.transformation.ui.builder.view;


import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.wizard.AbstractWizard;

public class CreateViewWizard extends AbstractWizard {

		private static final String TITLE = "Create Virtual Table";

	    private ViewBuilderManager builder;

	    private CreateViewPage1 page1;
	    private CreateViewPage2 page2;
	    
	public CreateViewWizard() {
		super(UiPlugin.getDefault(), TITLE, null);
	}

	@Override
	public boolean finish() {
		// TODO Auto-generated method stub
		return false;
	}


    @Override
	public void addPages() {
		super.addPages();
		this.page1 = new CreateViewPage1(builder);
		addPage(page1);
		this.page2 = new CreateViewPage2(builder);
		addPage(page2);
	}

}
