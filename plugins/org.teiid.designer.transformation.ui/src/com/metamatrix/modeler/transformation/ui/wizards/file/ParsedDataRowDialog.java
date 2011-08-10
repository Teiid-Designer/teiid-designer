package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.UiConstants.Images;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * Simple dialog to display parsed data column values from selected flat file data row
 */
public class ParsedDataRowDialog extends TitleAreaDialog {
	private final String TITLE = UiConstants.Util.getString("ParsedDataRowDialog.title"); //$NON-NLS-1$

    //=============================================================
    // Instance variables
    //=============================================================
    private TeiidMetadataFileInfo fileInfo;
    private String stringToParse;

    ListViewer columnDataViewer;
        
    //=============================================================
    // Constructors
    //=============================================================
    /**
     * ParsedDataRowDialog constructor.
     * 
     * @param parent   parent of this dialog
     * @param fileInfo the flat file business object
     * @param stringToParse the data string to parse
     */
    public ParsedDataRowDialog(Shell parent, TeiidMetadataFileInfo fileInfo, String stringToParse) {
        super(parent);
        this.fileInfo = fileInfo;
        this.stringToParse = stringToParse;
    }
    
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }
    
    /* (non-Javadoc)
    * @see org.eclipse.jface.window.Window#setShellStyle(int)
    */
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

    }
        
    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
    	setTitleImage(UiPlugin.getDefault().getImage(Images.IMPORT_TEIID_METADATA));
    	
        Composite composite = (Composite)super.createDialogArea(parent);
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        GridLayout gridLayout = new GridLayout();
        composite.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 500;
        composite.setLayoutData(gridData);
        
    	Group columnsGroup = WidgetFactory.createGroup(parent, UiConstants.Util.getString("ParsedDataRowDialog.columnsGroup"), SWT.NONE, 1, 1); //$NON-NLS-1$
    	columnsGroup.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 280;
    	gd.widthHint = 500;
    	columnsGroup.setLayoutData(gd);
    	
    	this.columnDataViewer = new ListViewer(columnsGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=1;
        this.columnDataViewer.getControl().setLayoutData(data);
        
        String[] parsedColumns = fileInfo.parseRow(stringToParse);
        TeiidColumnInfo[] infos = fileInfo.getColumnInfoList();
        int i=0;
        for( String row : parsedColumns ) {
        	if( row != null ) {
        		String value = infos[i++].getName() + "  :  " + row;  //$NON-NLS-1$
        		this.columnDataViewer.add(value);
        	}
        }
        
        setMessage(UiConstants.Util.getString("ParsedDataRowDialog.initialMessage")); //$NON-NLS-1$
        return composite;
    }
    
    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }
    @Override
    protected void okPressed() {
        super.okPressed();
    }

}
