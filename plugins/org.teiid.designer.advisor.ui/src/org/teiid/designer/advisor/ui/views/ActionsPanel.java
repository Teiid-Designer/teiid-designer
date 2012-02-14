/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.views;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.Messages;

import com.metamatrix.modeler.internal.ui.forms.FormUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.Label;

public class ActionsPanel extends ManagedForm implements AdvisorUiConstants {
    FormToolkit toolkit;

    private ScrolledForm parentForm;
    private ActionsSection aspectSection;
    
    private Combo actionGroupCombo;

    /**
     * @since 4.3
     */
    public ActionsPanel( Composite parent ) {
        super(parent);

        this.parentForm = this.getForm();

        initGUI();
    }

    private void initGUI() {
        this.parentForm.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(GridData.FILL_BOTH);
        this.parentForm.setLayoutData(gd);

        this.toolkit = getToolkit();
        Color bkgdColor = toolkit.getColors().getBackground();
        parentForm.setBackground(bkgdColor);

        this.parentForm.setText(Messages.TeiidActionsManager);

        this.parentForm.setLayout(new GridLayout());

        this.parentForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        FormUtil.tweakColors(toolkit, parentForm.getDisplay());
        this.parentForm.setBackground(bkgdColor);

        Composite body = parentForm.getBody();
		//int nColumns = 2;
		GridLayout gl = new GridLayout(2, false);
		body.setLayout(gl);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		body.setLayoutData(gd);
		
		Label label = WidgetFactory.createLabel(body, Messages.SelectActionsGroup);
		label.setBackground(bkgdColor);
		gd = new GridData();
		gd.verticalAlignment = GridData.CENTER;
		label.setLayoutData(gd);
		
		actionGroupCombo = new Combo(body, SWT.NONE | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.CENTER;
		actionGroupCombo.setLayoutData(gd);
		
		WidgetUtil.setComboItems(actionGroupCombo, Arrays.asList(AdvisorUiConstants.MODELING_ASPECT_LABELS_LIST), null, true);
		actionGroupCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
            	selectComboItem(actionGroupCombo.getSelectionIndex());
            }
        });

        aspectSection = new ActionsSection(toolkit, body);
        
//        Button tempButton1 = new Button(body, SWT.PUSH);
//        tempButton1.setText("TEST 1");
//        Button tempButton2 = new Button(body, SWT.PUSH);
//        tempButton2.setText("TEST 2");
        
        new ActionsCheatSheetSection(toolkit, body);
        
        actionGroupCombo.select(getInitialComboSelectionIndex());
        selectComboItem(getInitialComboSelectionIndex());
    }

    @Override
    public FormToolkit getToolkit() {
        if (this.toolkit == null) {
            Display display = parentForm.getDisplay();
            if (AdvisorUiPlugin.getDefault() != null) {
                this.toolkit = AdvisorUiPlugin.getDefault().getFormToolkit(display);
            } else {
                this.toolkit = new FormToolkit(display);
            }
        }

        return this.toolkit;
    }
    
    private void selectComboItem(int selectionIndex) {
    	if( selectionIndex >=0 ) {
    		String aspectId = actionGroupCombo.getItem(selectionIndex);
    		aspectSection.aspectChanged(aspectId);
    	}
    }
    
    private int getInitialComboSelectionIndex() {
    	int index = 0;
    	for( String item : actionGroupCombo.getItems()) {
    		if( AdvisorUiConstants.MODELING_ASPECT_LABELS.MODEL_PROJECT_MANAGEMENT.equalsIgnoreCase(item)) {
    			return index; 
    		}
    		index++;
    	}
    	
    	return -1;
    }
}
