/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.view;

import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

/**
 * 
 * This page provides management of widgets to assist in inputting various values for defining 
 * @author blafond
 *
 */
public class CreateViewPage1  extends AbstractWizardPage implements StringConstants {
    private final ViewBuilderManager builder;
    
	public CreateViewPage1(ViewBuilderManager builder) {
        super(CreateViewPage1.class.getSimpleName(), EMPTY_STRING);
        this.builder = builder;
        setTitle("Create View Wizard Page 1");
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}

}
