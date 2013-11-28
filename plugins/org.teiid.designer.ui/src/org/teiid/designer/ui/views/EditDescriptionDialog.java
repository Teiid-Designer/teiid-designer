package org.teiid.designer.ui.views;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;



/**
 * Dialog designed to display an editable text box.
 * 
 *
 *
 * @since 8.0
 */
public class EditDescriptionDialog extends Dialog implements KeyListener {
	//============================================================================================================================
    // Constants
    
    private static final String TITLE = UiConstants.Util.getString("EditDescriptionDialog.title.prefix"); //$NON-NLS-1$
    private static final String DESRIPTION_TEXT = ' ' + UiConstants.Util.getString("EditDescriptionDialog.description.text") + ' '; //$NON-NLS-1$
    
    //============================================================================================================================
    // Variables
    
    private StyledTextEditor textEditor;
    
    private String currentDescription = CoreStringUtil.Constants.EMPTY_STRING;
    
    private String changedDescription = CoreStringUtil.Constants.EMPTY_STRING;
    
    private boolean changed;


    //============================================================================================================================
    // Constructors
        
    /**<p>
     * </p>
     * @param parent
     * @param title
     * @since 4.0
     */
    public EditDescriptionDialog(final Shell shell, final String objectName, final String currentDescription) {
        super(shell, TITLE + ' ' + objectName);
        this.currentDescription = currentDescription;
        this.changedDescription = currentDescription;
        this.changed = false;
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
        
        Group descGroup = WidgetFactory.createGroup(dlgPanel, DESRIPTION_TEXT, SWT.NONE);
        descGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Composite innerPanel = new Composite(descGroup, SWT.NONE);
        innerPanel.setLayout(new GridLayout());
        GridData pgd = new GridData(GridData.FILL_BOTH);
        pgd.minimumWidth = 400;
        pgd.minimumHeight = 400;
        pgd.grabExcessVerticalSpace = true;
        pgd.grabExcessHorizontalSpace = true;
        innerPanel.setLayoutData(pgd);
        
        this.textEditor = new StyledTextEditor(innerPanel, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        GridData gdt = new GridData(GridData.FILL_BOTH);
        gdt.widthHint = 400;
        gdt.heightHint = 400;
        this.textEditor.setLayoutData(gdt);
        this.textEditor.setEditable(true);
        this.textEditor.setAllowFind(false);
        this.textEditor.getTextWidget().setWordWrap(true);
        
        this.textEditor.setText(currentDescription);
        
        this.textEditor.getTextWidget().addKeyListener(this);
        
        return dlgPanel;
    }

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Control superControl =  super.createContents(parent);
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		
		return superControl;
	}
	
	/**
	 * @return password
	 */
	public String getChangedDescription() {
		return this.changedDescription;
	}
	
	public boolean descriptionChanged() {
		return changed && (this.changedDescription.equals(currentDescription));
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.changedDescription = this.textEditor.getText();
		this.changed = true;
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
}
