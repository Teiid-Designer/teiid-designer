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

public class TasksPanel extends ManagedForm implements AdvisorUiConstants {
    FormToolkit toolkit;

    private ScrolledForm parentForm;
    private AspectsSection aspectSection;

    /**
     * @since 4.3
     */
    public TasksPanel( Composite parent ) {
        super(parent);

        this.parentForm = this.getForm();

        initGUI();
    }

    private void initGUI() {
        this.parentForm.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(GridData.FILL_BOTH);
        this.parentForm.setLayoutData(gd);

        this.toolkit = getToolkit();

        parentForm.setBackground(toolkit.getColors().getBackground());

        this.parentForm.setText(Messages.TeiidActionsManager);

        this.parentForm.setLayout(new GridLayout());

        this.parentForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        FormUtil.tweakColors(toolkit, parentForm.getDisplay());
        this.parentForm.setBackground(toolkit.getColors().getBackground());

        Composite body = parentForm.getBody();
		//int nColumns = 2;
		GridLayout gl2 = new GridLayout(2, false);
		body.setLayout(gl2);
		GridData gd2 = new GridData(GridData.FILL_BOTH);
		//gd2.horizontalSpan = 2;
		body.setLayoutData(gd2);
		
		WidgetFactory.createLabel(body, Messages.SelectActionsGroup);
		
		final Combo combo = new Combo(body, SWT.NONE | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		WidgetUtil.setComboItems(combo, Arrays.asList(AdvisorUiConstants.MODELING_ASPECT_LABELS_LIST), null, true);
		combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
            	int selectionIndex = combo.getSelectionIndex();
            	String aspectId = null;
            	if( selectionIndex >=0 ) {
            		aspectId = combo.getItem(selectionIndex);
            		aspectSection.aspectChanged(aspectId);
            	}
            }
        });

        aspectSection = new AspectsSection(toolkit, parentForm.getBody());
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
}
