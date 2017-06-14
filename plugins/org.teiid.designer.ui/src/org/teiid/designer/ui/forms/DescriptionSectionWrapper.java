/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.forms;

import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;


/** 
 * @since 5.0
 */
public class DescriptionSectionWrapper {
    private static final String NO_DESCRIPTION_AVAILABLE = "No Description Available";   //$NON-NLS-1$
    private static final String DESCRIPTION = "Description";   //$NON-NLS-1$
    
    private Section theSection;
    boolean descriptionChanged = false;
    EObject target;
    ModelResource modelResource;
    Text descriptionText;
    
    /** 
     * 
     * @since 5.0
     */
    public DescriptionSectionWrapper(Composite theParent) {
        super();
        initializeSection(theParent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED );
    }

    /** 
     * 
     * @since 5.0
     */
    public DescriptionSectionWrapper(Composite theParent, int sectionStyle) {
        super();
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
        
        // Create Description
        descriptionText = formToolkit.createText( sectionBody,"", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL); //$NON-NLS-1$ 
        descriptionText.setEditable(true);
        GridData gd_0 = new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL);
        gd_0.grabExcessHorizontalSpace = true;
        gd_0.horizontalAlignment = SWT.FILL;
        descriptionText.setLayoutData(gd_0);
        descriptionText.addFocusListener(new FocusAdapter() {
            @Override
			public void focusLost(FocusEvent fe) {
                if( descriptionChanged ) {
                    descriptionChanged = false;
                    if( target != null ) {
                        if( target instanceof ModelAnnotation ) {
                            ModelUtilities.setModelDescription(modelResource, descriptionText.getText());
                        } else {
                            ModelObjectUtilities.setDescription(target, descriptionText.getText(),this);
                        }
                    } else {
                        ModelUtilities.setModelDescription(modelResource, descriptionText.getText());
                    }
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
    
    public void setTarget(EObject target, ModelResource modelResource) {
        this.target = target;
        this.modelResource = modelResource;

        reset();
    }
    
    public void reset() {
        String description = null;
        if( this.target instanceof ModelAnnotation ) {
            description = ModelUtilities.getModelDescription(this.modelResource);
        } else {
            try {
                description = ModelerCore.getModelEditor().getDescription(this.target);
            } catch (ModelerCoreException theException) {
                UiConstants.Util.log(IStatus.ERROR, theException.getMessage());
            }
        }
        if( description == null ) {
            description = NO_DESCRIPTION_AVAILABLE;
        }
        if( !descriptionText.getText().equals(description)) {
            descriptionText.setText(description);
        }
    }

}