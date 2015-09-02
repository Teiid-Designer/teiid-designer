/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 *
 */
public class RowBasedSecurityDialog extends AbstractAddOrEditTitleDialog {
	
    private String targetName;
    private Text targetNameText;
    
    private StyledTextEditor conditionTextEditor;
    private String conditionString;
    private boolean constraint = true;
    private Button constraintButton;
    
    private boolean isEdit;


    /**
     * 
     * @param parentShell
     * @param title
     * @param message
     * @param permission
     * @param okEnabled
     */
    public RowBasedSecurityDialog( Shell parentShell, String title, String message, Permission permission, boolean existingSecurity) {
        super(parentShell, Messages.rowFilterDialogTitle, Messages.setConditionForTargetsMessage, existingSecurity);

    	this.targetName = permission.getTargetName();
    	isEdit = true;
    	if( permission.getCondition() != null ) {
        	this.conditionString = permission.getCondition();
        	this.constraint = permission.isConstraint();
    	}
    }

    
    /**
     * 
     * @param outerPanel
     */
    @Override
	public void createCustomArea( Composite outerPanel ) {
    	final Composite innerPanel = new Composite(outerPanel, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(innerPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(innerPanel);
        
		{
	        WidgetFactory.createLabel(innerPanel, Messages.target);

	        this.targetNameText = WidgetFactory.createTextField(innerPanel, GridData.FILL_HORIZONTAL, 1, StringConstants.EMPTY_STRING);
	        if( isEdit ) {
	        	this.targetNameText.setText(this.targetName);
	        }
	        
	        this.targetNameText.addModifyListener(new ModifyListener() {
	            @Override
	            public void modifyText( ModifyEvent e ) {
	            	handleInputChanged();
	            }
	        });
	        this.targetNameText.setEditable(false);
	        this.targetNameText.setBackground(innerPanel.getBackground());

	        this.constraintButton = new Button(innerPanel, SWT.CHECK);
	        this.constraintButton.setText(Messages.constraint_with_tooltip);
	        GridData gd = new GridData();
	        gd.horizontalSpan = 3;
	        this.constraintButton.setLayoutData(gd);
	        if( isEdit ) {
	        	this.constraintButton.setSelection(this.constraint);
	        } else {
	        	this.constraintButton.setSelection(true);
	        }
	        this.constraintButton.setToolTipText(Messages.constraintButtonTooltip);
	        
	        if( isEdit ) {
	        	this.constraintButton.setSelection(this.constraint);
	        } else {
	        	this.constraintButton.setSelection(true);
	        }
	        
	        this.constraintButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					// Open dialog to display models tree so user can select a column object
					handleInputChanged();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
	        
	        final Group group = WidgetFactory.createGroup(innerPanel, Messages.condition, GridData.FILL_HORIZONTAL, 2);
	        {
	        	this.conditionTextEditor = new StyledTextEditor(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
    			GridDataFactory.fillDefaults().grab(true,  true).span(2,  1).applyTo(this.conditionTextEditor.getTextWidget());
    			((GridData)this.conditionTextEditor.getTextWidget().getLayoutData()).heightHint = 50;
    			
    			if( isEdit ) {
    				this.conditionTextEditor.setText(this.conditionString);
    			} else {
    				this.conditionTextEditor.setText(""); //$NON-NLS-1$
    			}
    			this.conditionTextEditor.getDocument().addDocumentListener(new IDocumentListener() {

    	            @Override
    	            public void documentChanged( DocumentEvent event ) {
    	            	handleInputChanged();
    	            }

    	            @Override
    	            public void documentAboutToBeChanged( DocumentEvent event ) {
    	                // NO OP
    	            }
    	        });
	        }

		}
		

    }
    
    /**
     * @return the new targetColumn value (never <code>null</code>)
     * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
     */
    public String getTargetName() {
        CoreArgCheck.isEqual(getReturnCode(), Window.OK);
        return this.targetName;
    }

    /**
     * @return the new condition value (never <code>null</code>)
     * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
     */
    public String getCondition() {
        CoreArgCheck.isEqual(getReturnCode(), Window.OK);
        return this.conditionString;
    }
    
    /**
     * @return the new constraint (never <code>null</code>)
     * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
     */
    public boolean getConstraintValue() {
        CoreArgCheck.isEqual(getReturnCode(), Window.OK);
        return constraint;
    }
    

    @Override
    protected void handleInputChanged() {
    	validate();
    }
    
    /**
     * @return true if condition is not null and not empty
     */
    public boolean hasCondition() {
    	return getCondition() != null && !getCondition().isEmpty();
    }
    
    @Override
    protected void validate() {
    	boolean enable = true;
    	setErrorMessage(null);
    	setMessage(Messages.clickOkToFinish);
    	
    	this.conditionString = conditionTextEditor.getText();
        this.constraint = constraintButton.getSelection();
    	boolean conditionEmpty = (this.conditionString == null || this.conditionString.trim().isEmpty());
        
        if( conditionEmpty) {
            enable = false;
        	setErrorMessage(Messages.conditionIsUndefined);
        }
    	
    	getButton(IDialogConstants.OK_ID).setEnabled(enable);
    }
}