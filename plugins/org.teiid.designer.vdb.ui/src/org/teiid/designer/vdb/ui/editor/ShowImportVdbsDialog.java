/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.editor;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbImportVdbEntry;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

/**
 * @since 8.0
 *
 */
public class ShowImportVdbsDialog extends Dialog {
    
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ShowImportVdbsDialog.class);
    private static final String TITLE =getString("title"); //$NON-NLS-1$
    
	private static String getString(final String id) {
		return VdbUiConstants.Util.getString(I18N_PREFIX + id);
	}
    private Vdb vdb;
    
    TableViewer importVdbViewer;
    /**
     * 
     * @param parent
     * @param vdb the VDB
     */
    public ShowImportVdbsDialog( Shell parent, Vdb vdb) {
        super(parent, TITLE);
        this.vdb = vdb;
    }
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
    	Composite mainPanel = (Composite)super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        mainPanel.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        
    	Group theGroup = WidgetFactory.createGroup(mainPanel, getString("vdbNames"), SWT.NONE, 1, 4); //$NON-NLS-1$
    	GridData groupGD = new GridData(GridData.FILL_BOTH);
    	groupGD.heightHint = 200;
    	groupGD.widthHint = 400;
    	theGroup.setLayoutData(groupGD);
    	
    	this.importVdbViewer = new TableViewer(theGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    	this.importVdbViewer.setLabelProvider(new ILabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getText(Object element) {
				if( element instanceof VdbImportVdbEntry) {
					return ((VdbImportVdbEntry)element).getName();
				}
				return null;
			}
			
			@Override
			public Image getImage(Object element) {
				if( element instanceof VdbImportVdbEntry) {
					return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.VDB_ICON);
				}
				return null;
			}
		});
    	
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.importVdbViewer.getControl().setFont(JFaceResources.getTextFont());
        this.importVdbViewer.getControl().setLayoutData(data);
        
        if( this.vdb != null ) {
	        for( VdbImportVdbEntry row : this.vdb.getImportVdbEntries() ) {
	        	if( row != null ) {
//	        		this.importVdbViewer.add(row.getName());
	        		this.importVdbViewer.add(row);
	        	}
	        }
        }
        
        return mainPanel;
    }
}
