package org.teiid.designer.datatools.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.ICategory;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.ui.actions.AddProfileViewAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.MessageLabel;

public class SelectConnectionProfileDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {
    private static final String DEFAULT_TITLE = DatatoolsUiConstants.UTIL.getString("SelectConnectionProfileDialog.title"); //$NON-NLS-1$ 
    private static final String NEW_BUTTON_TEXT = DatatoolsUiConstants.UTIL.getString("SelectConnectionProfileDialog.newButton"); //$NON-NLS-1$
    	
    private Text fileNameText;
    private MessageLabel statusMessageLabel;
    private Button newCPButton;
    private IConnectionProfile selectedCP;
    
    private static final String UNKNOWN_FILE = DatatoolsUiConstants.UTIL.getString("SelectConnectionProfileDialog.unknownFileName");  //$NON-NLS-1$

	/**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     */
    public SelectConnectionProfileDialog( Shell parent ) {
    	this(parent, DEFAULT_TITLE, new ConnectionProfileTreeProvider(), new ConnectionProfileTreeProvider());
    }
    
    public SelectConnectionProfileDialog( Shell parent,
                                 ILabelProvider labelProvider,
                                 ITreeContentProvider contentProvider ) {
        this(parent, null, labelProvider, contentProvider);
    }
    
    public SelectConnectionProfileDialog( Shell parent,
            String title,
            ILabelProvider labelProvider,
            ITreeContentProvider contentProvider  ) {
    	 super(parent, labelProvider, contentProvider);
    	 setTitle(title);
    	 setMessage(DatatoolsUiConstants.UTIL.getString("SelectConnectionProfileDialog.defaultMessage")); //$NON-NLS-1$
    	 setInput(ProfileManager.getInstance());//.getRootCategories());
    	 setAllowMultiple(false);
    }
    

	@Override
	protected Control createDialogArea(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout());
        GridData panelData = new GridData(GridData.FILL_BOTH);
        panel.setLayoutData(panelData);
        
//        createMessageArea(panel);
//        setMessage(DatatoolsUiConstants.UTIL.getString("SelectConnectionProfileDialog.defaultMessage")); //$NON-NLS-1$
        
        Group selectedGroup = WidgetFactory.createGroup(panel, DatatoolsUiConstants.UTIL.getString("SelectConnectionProfileDialog.selectedGroupTitle"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        //selectedGroup.setText(null);
        selectedGroup.setLayout(new GridLayout(2, false));

        this.fileNameText = WidgetFactory.createTextField(selectedGroup, GridData.FILL_HORIZONTAL, UNKNOWN_FILE);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = convertHeightInCharsToPixels(1);
        this.fileNameText.setLayoutData(data);
        this.fileNameText.setEditable(false);
        this.fileNameText.setBackground(panel.getBackground());
        this.fileNameText.setText(UNKNOWN_FILE);

        newCPButton = WidgetFactory.createButton(selectedGroup, NEW_BUTTON_TEXT);
        newCPButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                AddProfileViewAction action = new AddProfileViewAction();
                action.run();
                selectedCP = action.getAddedProfile();
                if( selectedCP != null ) {
                	fileNameText.setText(selectedCP.getName());
                }
            }
        });
        
        super.createDialogArea(panel);
        
        this.statusMessageLabel =  new MessageLabel(panel);
        GridData statusData = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = convertHeightInCharsToPixels(1);
        this.statusMessageLabel.setLayoutData(statusData);
        this.statusMessageLabel.setEnabled(false);
        this.statusMessageLabel.setText(UNKNOWN_FILE);
        
        // listen to selection in the tree
        getTreeViewer().addSelectionChangedListener(this);
        
		return panel;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection)event.getSelection();
		if( selection.isEmpty() ) {
			this.selectedCP = null;
			this.fileNameText.setText(UNKNOWN_FILE);
			updateOnSelection(null);
			return;
		}
		
		Object firstElement = selection.getFirstElement();
		if(firstElement instanceof ICategory ) {
			this.selectedCP = null;
			this.fileNameText.setText(UNKNOWN_FILE);
		} else {
			this.selectedCP = (IConnectionProfile)selection.getFirstElement();
			this.fileNameText.setText(selectedCP.getName());
		}
		updateOnSelection(firstElement);
	}
	
	private void updateOnSelection(Object selectedObject) {
		IStatus status = new Status(IStatus.INFO, DatatoolsUiPlugin.PLUGIN_ID, DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.okSelectionMessage")); //$NON-NLS-1$
		if( selectedObject != null ) {
			if( selectedObject instanceof ICategory ) {
				status = new Status(IStatus.ERROR, DatatoolsUiPlugin.PLUGIN_ID, DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.categorySelectionMessage")); //$NON-NLS-1$
				getOkButton().setEnabled(false);
			} else {
				getOkButton().setEnabled(true);
			}
		} else {
			status = new Status(IStatus.ERROR, DatatoolsUiPlugin.PLUGIN_ID, DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.invalidSelectionMessage")); //$NON-NLS-1$
			getOkButton().setEnabled(false);
		}
		
		this.statusMessageLabel.setErrorStatus(status);
	}

	public IConnectionProfile getSelectedCP() {
		return this.selectedCP;
	}

    
}
