/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;

public class FileContentsDialog  extends Dialog {
    
    //============================================================================================================================
    // Variables
    
    private StyledTextEditor textEditor;
    
    private String fileText;
    
    private String fileName;


    //============================================================================================================================
    // Constructors
        
    /**<p>
     * </p>
     * @param shell the shell
     * @param theXmlText the xml text
     * @since 4.0
     */
    public FileContentsDialog(final Shell shell, final String title, File file, String displayedFileName) {
        super(shell, title);
        
        this.fileName = file.getName();
        
        loadText(file);
    }
    
    //============================================================================================================================
    // Overridden Methods

    /**<p>
     * </p>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        final Composite dlgPanel = (Composite)super.createDialogArea(parent);
        
        Group descGroup = WidgetFactory.createGroup(dlgPanel, "Contents", SWT.NONE);
        descGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Composite innerPanel = new Composite(descGroup, SWT.NONE);
        innerPanel.setLayout(new GridLayout());
        GridData pgd = new GridData(GridData.FILL_BOTH);
        pgd.minimumWidth = 400;
        pgd.minimumHeight = 400;
        pgd.grabExcessVerticalSpace = true;
        pgd.grabExcessHorizontalSpace = true;
        innerPanel.setLayoutData(pgd);
        
        this.textEditor = new StyledTextEditor(innerPanel, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        GridData gdt = new GridData(GridData.FILL_BOTH);
        gdt.widthHint = 400;
        gdt.heightHint = 400;
        this.textEditor.setLayoutData(gdt);
        this.textEditor.setEditable(false);
        this.textEditor.setAllowFind(false);
        this.textEditor.getTextWidget().setWordWrap(true);
        
        this.textEditor.setText(fileText);
        
        return dlgPanel;
    }

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Control superControl =  super.createContents(parent);
		
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		
		return superControl;
	}
	
	private void loadText(File file) {
		FileInputStream fin = null;
		try {
			int ch;
			StringBuffer strContent = new StringBuffer("");
			fin = new FileInputStream(file);
			
			  while( (ch = fin.read()) != -1)
			      strContent.append((char)ch);
			  
			  fileText = strContent.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if( fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}