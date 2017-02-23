/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.summary;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.views.EditDescriptionDialog;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class ModelDescriptionWrapper {
    private static final String NO_DESCRIPTION_AVAILABLE = "No Description Available";   //$NON-NLS-1$
    private static final String DESCRIPTION = "Description";   //$NON-NLS-1$
    
    private Section theSection;
    boolean descriptionChanged = false;
    
    ModelResource modelResource;
    Hyperlink editDescriptionHL;
    Text descriptionText;
    
    /** 
     * 
     * @since 5.0
     */
    public ModelDescriptionWrapper(Composite theParent, ModelResource model) {
        this(theParent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED, model);
    }

    /** 
     * 
     * @since 5.0
     */
    public ModelDescriptionWrapper(Composite theParent, int sectionStyle, ModelResource model) {
        super();
        this.modelResource = model;
        initializeSection(theParent, sectionStyle);
    }
    
    public Section getSection() {
        return theSection;
    }
    
    private void initializeSection(Composite theParent, int sectionStyle) {
        FormToolkit formToolkit = UiPlugin.getDefault().getFormToolkit(theParent.getDisplay());
        theSection = formToolkit.createSection(theParent, sectionStyle );
        
        theSection.setText(DESCRIPTION);

        //theSection.getDescriptionControl().setForeground(formToolkit.getColors().getColor(FormColors.TITLE));
        theSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Composite sectionBody = new Composite(theSection, SWT.NONE);
        sectionBody.setLayoutData(new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL));
        sectionBody.setLayout(new GridLayout());
        theSection.setClient(sectionBody);
        theSection.setBackground(theParent.getBackground());
        
        editDescriptionHL = formToolkit.createHyperlink(sectionBody, "Edit...", SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(editDescriptionHL);
        editDescriptionHL.addHyperlinkListener(new HyperlinkAdapter() {
            
            @Override
            public void linkActivated(HyperlinkEvent e) {
                // Launch Edit Description Dialog
            	editDescription();
            }
        });
        
        // Create Description
        descriptionText = formToolkit.createText( sectionBody,"", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL); //$NON-NLS-1$ 
        descriptionText.setEditable(true);
        descriptionText.setBackground(sectionBody.getBackground());
        GridData gd_0 = new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL);
        gd_0.grabExcessHorizontalSpace = true;
        gd_0.horizontalAlignment = SWT.FILL;
        descriptionText.setLayoutData(gd_0);
        descriptionText.addFocusListener(new FocusAdapter() {
            @Override
			public void focusLost(FocusEvent fe) {
                if( descriptionChanged ) {
                    descriptionChanged = false;
                     ModelUtilities.setModelDescription(modelResource, descriptionText.getText());
                }
            }
            @Override
			public void focusGained(FocusEvent fe) {
                
            }
        });
        
        descriptionText.addKeyListener( new KeyAdapter() {
            @Override
			public void keyPressed(KeyEvent e) {
                descriptionChanged = true;
            }
            @Override
			public void keyReleased(KeyEvent e) {
                descriptionChanged = true;
            }
        });
        
        descriptionText.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                descriptionChanged = true;
            }
        });
    }
    
    public String getText() {
        return descriptionText.getText();
    }
    
    public void setText(String text) {
        descriptionText.setText(text);
    }
    
    
    public void reset() {
        String description = null;

        try {
            description = modelResource.getDescription();
        } catch (ModelerCoreException theException) {
            UiConstants.Util.log(IStatus.ERROR, theException.getMessage());
        }
        
        if( description == null ) {
            description = NO_DESCRIPTION_AVAILABLE;
        }
        if( !descriptionText.getText().equals(description)) {
            descriptionText.setText(description);
        }
    }
    
    private void editDescription() {
    	
    	Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    	EditDescriptionDialog dialog = new EditDescriptionDialog(shell, modelResource.getItemName(), descriptionText.getText());

        if (dialog.open() == Window.OK) {
        	String newDescription = dialog.getChangedDescription();
            ModelUtilities.setModelDescription(modelResource, newDescription);
            reset();
        }
    }

}