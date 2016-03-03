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
import org.eclipse.swt.layout.GridData;
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
import org.teiid.designer.ui.common.widget.Label;

public class ColumnMaskingDialog  extends AbstractAddOrEditTitleDialog {
	
    private String targetName;
    private Text targetNameText;
    
    private StyledTextEditor conditionTextEditor;
    private String conditionString;
    
    private StyledTextEditor maskTextEditor;
    private String maskString;
    private int order = 0;
    private Text orderText;
    
    private boolean isEdit;


    /**
     * 
     * @param parentShell
     * @param title
     * @param message
     * @param permission
     * @param okEnabled
     */
    public ColumnMaskingDialog( Shell parentShell, String title, String message, Permission permission, boolean existingMask) {
        super(parentShell, Messages.columnMaskingDialogTitle, Messages.setConditionOrMaskingMessage, existingMask);

    	this.targetName = permission.getTargetName();
    	isEdit = true;
    	if( permission.getCondition() != null ) {
        	this.conditionString = permission.getCondition();
    	}
    	
        if(permission.getMask() != null ) {
        	this.maskString = permission.getMask();
        	this.order = permission.getOrder();
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
	        Label columnLabel = WidgetFactory.createLabel(innerPanel, Messages.column);
	        GridDataFactory.swtDefaults().align(GridData.CENTER, GridData.CENTER).applyTo(columnLabel);

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

	        
	        WidgetFactory.createLabel(innerPanel, Messages.order);
	        
	        this.orderText = WidgetFactory.createTextField(innerPanel, GridData.FILL_HORIZONTAL, 1, StringConstants.EMPTY_STRING);
	        if( isEdit ) {
	        	this.orderText.setText(Integer.toString(this.order));
	        } else {
	        	this.orderText.setText("0"); //$NON-NLS-1$
	        }
	        
	        this.orderText.addModifyListener(new ModifyListener() {
	            @Override
	            public void modifyText( ModifyEvent e ) {
	            	handleInputChanged();
	            }
	        });
	        
	        final Group maskGroup = WidgetFactory.createGroup(innerPanel, Messages.columnMasking, GridData.FILL_HORIZONTAL, 2);
	        {
	        	this.maskTextEditor = new StyledTextEditor(maskGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
    			GridDataFactory.fillDefaults().grab(true,  true).span(2,  1).applyTo(this.maskTextEditor.getTextWidget());
    			((GridData)this.maskTextEditor.getTextWidget().getLayoutData()).heightHint = 50;
    			
    			if( isEdit ) {
    				this.maskTextEditor.setText(this.maskString);
    			} else {
    				this.maskTextEditor.setText(""); //$NON-NLS-1$
    			}
    			this.maskTextEditor.getDocument().addDocumentListener(new IDocumentListener() {

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

	        this.orderText.setEnabled(true);
	        this.maskTextEditor.getTextWidget().setEnabled(true);
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
     * @return the new mask value (never <code>null</code>)
     * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
     */
    public String getMask() {
        CoreArgCheck.isEqual(getReturnCode(), Window.OK);
        return this.maskString;
    }
    
    /**
     * @return the new order (never <code>null</code>)
     * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
     */
    public int getOrder() {
        CoreArgCheck.isEqual(getReturnCode(), Window.OK);
        return order;
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
    
    /**
     * @return true if condition is not null and not empty
     */
    public boolean hasMask() {
    	return getMask() != null && !getMask().isEmpty();
    }
    
    @Override
    protected void validate() {
    	getButton(IDialogConstants.OK_ID).setEnabled(true);
    	setErrorMessage(null);
    	setMessage(Messages.clickOkToFinish); //Messages.clickOKToFinish);
    	
    	this.conditionString = conditionTextEditor.getText();
        this.maskString = maskTextEditor.getText();

    	boolean conditionEmpty = (this.conditionString == null || this.conditionString.trim().isEmpty());

        boolean maskEmpty = (maskString == null || maskString.trim().isEmpty());
        
        if( maskEmpty && conditionEmpty) {
    		setErrorMessage(Messages.noMaskOrConditionDefined);
    		getButton(IDialogConstants.OK_ID).setEnabled(false);
    		return;
        }
        
    	if( this.orderText.getText() != null ) {
    		try {
				order = Integer.parseInt(orderText.getText());
			} catch (NumberFormatException ex) {
        		setErrorMessage(Messages.orderMustBeAnInteger);
        		getButton(IDialogConstants.OK_ID).setEnabled(false);
        		return;
			}
    	} else {
    		setErrorMessage(Messages.orderMustNotBeNull);
    		getButton(IDialogConstants.OK_ID).setEnabled(false);
    		return;
    	}
    	
    	// Assume that if order is > 0 (non-default), then the mask cannot be empty
    	if( maskEmpty ) {
    		getButton(IDialogConstants.OK_ID).setEnabled(false);
    		return;
    	}
    }
}