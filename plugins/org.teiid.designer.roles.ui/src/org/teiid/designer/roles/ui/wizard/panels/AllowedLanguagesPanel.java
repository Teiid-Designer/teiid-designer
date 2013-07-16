/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard.panels;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.roles.ui.wizard.dialogs.AbstractAddOrEditTitleDialog;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 *
 */
public class AllowedLanguagesPanel extends DataRolePanel {
	private ListViewer allowedLanguagesViewer;
	Button addButton;
	Button removeButton;
	
	/**
     * @param parent
     * @param wizard
     */
    public AllowedLanguagesPanel(Composite parent, DataRoleWizard wizard) {
    	super(parent, wizard);
    }
    

	/* (non-Javadoc)
	 * @see org.teiid.designer.roles.ui.wizard.panels.DataRolePanel#createControl()
	 */
	@Override
	void createControl() {
		// Set the layout 
		
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(getPrimaryPanel());
		GridDataFactory.fillDefaults().applyTo(getPrimaryPanel());
		
		{
	    	this.allowedLanguagesViewer = new ListViewer(getPrimaryPanel(), SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	    	GridDataFactory.fillDefaults().grab(true,  true).span(2, 1).applyTo(allowedLanguagesViewer.getControl());
//	        GridData data = new GridData(GridData.FILL_BOTH);
//	        data.horizontalSpan=2;
//	        this.allowedLanguagesViewer.getControl().setLayoutData(data);
		}
        
        Composite toolbarPanel = WidgetFactory.createPanel(getPrimaryPanel(), SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 2);
        
        this.addButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.addButton.setText(Messages.add);
        this.addButton.setToolTipText(Messages.addAllowedLanguageTooltip);
        this.addButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAdd();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        this.removeButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.removeButton.setText(Messages.remove);
        this.removeButton.setToolTipText(Messages.removeAllowedLanguageTooltip);
        this.removeButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemove();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
	}
	
    void handleAdd() {
    	AddLanguageDialog dialog = new AddLanguageDialog(getPrimaryPanel().getShell());

        if (dialog.open() == Window.OK) {
            // update model
            String language = dialog.getLanguage();

            getWizard().getTreeProvider().addAllowedLanguage(language);

            // update UI from model
            //this.allowedLanguagesViewer.add(language);
            
            refresh();

            // select the new language
            this.allowedLanguagesViewer.setSelection(new StructuredSelection(language), true);
        }
    }
    
    void handleRemove() {
        String selectedLanguage = getSelectedLanguage();
        assert (selectedLanguage != null);

        // update model
        getWizard().getTreeProvider().removeAllowedLanguage(selectedLanguage);
        
        this.allowedLanguagesViewer.remove(selectedLanguage);
        // update UI
        this.allowedLanguagesViewer.refresh();
    }
    
    private String getSelectedLanguage() {
        IStructuredSelection selection = (IStructuredSelection)this.allowedLanguagesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (String)selection.getFirstElement();
    }
    
    @Override
	public void refresh() {
        this.allowedLanguagesViewer.getList().removeAll();
        for( String value : getWizard().getTreeProvider().getAllowedLanguages() ) {
        	this.allowedLanguagesViewer.add(value);
        }
    }
    
    /**
     * This inner class provides for selecting existing language to be allowed for the specified data role
     * The class contains a simple 
     */
    class AddLanguageDialog  extends AbstractAddOrEditTitleDialog {

        private String selectedLanguage;
        
        private ListViewer languageViewer;

        /**
         * @param parentShell the parent shell (may be <code>null</code>)
         * @param existingPropertyNames the existing property names (can be <code>null</code>)
         */
        public AddLanguageDialog( Shell parentShell ) {
            super(parentShell, Messages.selectAllowedLanguage, 
                    Messages.selectAllowedLanguageMessage, false);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected void createCustomArea( Composite parent ) {
        	
        	final Group group = WidgetFactory.createGroup(parent, Messages.allowedLanguages, GridData.FILL_HORIZONTAL, 2);
    		{
    	    	languageViewer = new ListViewer(group, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    	    	GridDataFactory.fillDefaults().grab(true,  true).span(1,  1).applyTo(languageViewer.getControl());
//    	        GridData data = new GridData(GridData.FILL_BOTH);
//    	        data.horizontalSpan=1;
//    	        languageViewer.getControl().setLayoutData(data);
    	        
    	        for( String lang : getWizard().getAllowedLanguages()) {
    	        	languageViewer.add(lang);
    	        }
    		}
            languageViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection)languageViewer.getSelection();

			        if (selection.isEmpty()) {
			        	selectedLanguage = null;
			        	getButton(IDialogConstants.OK_ID).setEnabled(false);
			        }
			        selectedLanguage = (String)selection.getFirstElement();
			        getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			});
        }

        /**
         * @return the new language (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public String getLanguage() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return selectedLanguage;
        }

		/* (non-Javadoc)
		 * @see org.teiid.designer.roles.ui.wizard.dialogs.AbstractAddOrEditTitleDialog#handleInputChanged()
		 */
		@Override
		protected void handleInputChanged() {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.teiid.designer.roles.ui.wizard.dialogs.AbstractAddOrEditTitleDialog#validate()
		 */
		@Override
		protected void validate() {
			// TODO Auto-generated method stub
			
		}


    }
}
