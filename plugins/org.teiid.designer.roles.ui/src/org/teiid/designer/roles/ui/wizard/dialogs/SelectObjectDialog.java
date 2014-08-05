/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.MessageLabel;

/**
 *
 */
public class SelectObjectDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {
    private static final char DELIM = CoreStringUtil.Constants.DOT_CHAR;
    private static final char B_SLASH = '/';
    
    private Text targetNameText;
    private String targetName;
    private MessageLabel statusMessageLabel;
    private Object input;
    private ILabelProvider labelProvider;
    private ITreeContentProvider contentProvider;

    public SelectObjectDialog( Shell parent, 
    		ILabelProvider labelProvider, 
    		ITreeContentProvider contentProvider, 
    		Object input) {
        super(parent, labelProvider, contentProvider);
        this.labelProvider = labelProvider;
        this.contentProvider = contentProvider;
        this.input = input;
        setTitle(Messages.targetSelection);
        setMessage(Messages.selectTargetForCondition);
        setInput(input);
        setAllowMultiple(false);
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout());
        GridData panelData = new GridData(GridData.FILL_BOTH);
        panel.setLayoutData(panelData);

        Group selectedGroup = WidgetFactory.createGroup(panel, "Selected Target", GridData.FILL_HORIZONTAL, 1, 2); //$NON-NLS-1$

        this.targetNameText = WidgetFactory.createTextField(selectedGroup, GridData.FILL_HORIZONTAL, Messages.undefined);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = convertHeightInCharsToPixels(1);
        data.verticalAlignment=GridData.CENTER;
        this.targetNameText.setLayoutData(data);
        this.targetNameText.setEditable(false);
        this.targetNameText.setBackground(panel.getBackground());
        this.targetNameText.setText(Messages.undefined);

        super.createDialogArea(panel);

        this.statusMessageLabel = new MessageLabel(panel);
        GridData statusData = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = convertHeightInCharsToPixels(1);
        this.statusMessageLabel.setLayoutData(statusData);
        this.statusMessageLabel.setEnabled(false);
        this.statusMessageLabel.setText(Messages.undefined);

        getTreeViewer().expandToLevel(2);

        return panel;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected TreeViewer createTreeViewer( Composite parent ) {
        TreeViewer viewer = super.createTreeViewer(parent);
        viewer.addSelectionChangedListener(this);
        viewer.getTree().setEnabled(true);
        viewer.setSorter(new ViewerSorter());
        viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                if (element instanceof EObject || element instanceof Resource) {
                    return true;
                }

                return false;
            }
        } });
        
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);

        viewer.setInput(input);

        return viewer;
    }

    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        TreeSelection selection = (TreeSelection)event.getSelection();
        if (selection.isEmpty()) {
            this.targetNameText.setText(Messages.undefined);
            this.targetName = null;
            updateOnSelection(null);
            return;
        }

        Object firstElement = selection.getFirstElement();
        
        if (!(firstElement instanceof Table || firstElement instanceof View || firstElement instanceof Procedure )) {
            this.targetNameText.setText(Messages.undefined);
            this.targetName = null;
        } else {
            EObject target = (EObject)selection.getFirstElement();
            targetName = getFullName(target);
            this.targetNameText.setText(ModelerCore.getModelEditor().getName(target));
        }

        updateOnSelection(firstElement);
    }
    
    private String getFullName(EObject target) {
    	String targetName = getResourceName(target.eResource()) + '/' + ModelerCore.getModelEditor().getModelRelativePath(target);

        targetName = targetName.replace(B_SLASH, DELIM);
        
        return targetName;
    }
    
    /*
     * Returns the file name only minus the xmi file extension
     */
    private String getResourceName( Resource res ) {

        if (res.getURI().path().endsWith(".xmi")) { //$NON-NLS-1$
            Path path = new Path(res.getURI().path());
            return path.removeFileExtension().lastSegment();
        }
        return res.getURI().path();
    }

    private void updateOnSelection( Object selectedObject ) {
        IStatus status = new Status(IStatus.INFO,
        		RolesUiPlugin.PLUGIN_ID,
                                    "Valid column selected. Click OK to finish."); //$NON-NLS-1$
        if (selectedObject != null) {
            if (!(selectedObject instanceof Column)) {
                status = new Status(IStatus.ERROR,
                		RolesUiPlugin.PLUGIN_ID,
                                    "Selected object is not a column"); //$NON-NLS-1$
                getOkButton().setEnabled(false);
            } else {
                getOkButton().setEnabled(true);
            }
        } else {
            status = new Status(IStatus.ERROR,
            		RolesUiPlugin.PLUGIN_ID,
                                "No column selected"); //$NON-NLS-1$
            getOkButton().setEnabled(false);
        }

        this.statusMessageLabel.setErrorStatus(status);
    }
    
    /**
     * Returns the current TeiidTranslator
     * 
     * @return the TeiidTranslator. may return null
     */
    public String getColumnName() {
        return this.targetName;
    }

}