/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.editor.panels;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelEditorImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ContainerImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;
import org.teiid.designer.roles.Crud.Type;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.Vdb.Event;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.Messages;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiPlugin;
import org.teiid.designer.vdb.ui.VdbUiConstants.Images;
import org.teiid.designer.vdb.ui.editor.ConfirmationDialog;
import org.teiid.designer.vdb.ui.editor.VdbEditor;

/**
 * @author blafond
 *
 */
public class DataRolesPanel  {
    static final String CONFIRM_OVERWRITE_UDFJAR_MESSAGE = i18n("confirmOverwriteUdfJarMessage"); //$NON-NLS-1$
    static final String INFORM_DATA_ROLES_ON_ADD_MESSAGE = i18n("informDataRolesExistOnAddMessage"); //$NON-NLS-1$
    static final String WEB_SERVICES_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$
    static final String DESCRIPTION_COLUMN_NAME = i18n("descriptionColumnName"); //$NON-NLS-1$;
    static final String CONFIRM_REMOVE_MESSAGE = i18n("confirmRemoveMessage"); //$NON-NLS-1$
    
    static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }

    VdbEditor vdbEditor;
    Action cloneDataRoleAction;
    DataRole selectedDataRole;
    TableViewerBuilder dataRolesViewer;
    
    Button newButton, editButton, deleteButton;
    
    /**
     * @param parent
     * @param editor
     */
    public DataRolesPanel(Composite parent, VdbEditor editor) {
    	super();
    	this.vdbEditor = editor;
    	
    	createPanel(parent);
    }
    
	private void createPanel(Composite parent) {

        // Need a top Table Viewer containing Name and Description strings
        
    	this.dataRolesViewer = new TableViewerBuilder(parent, (SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER));  	
    	this.dataRolesViewer.setContentProvider( new DataRolesContentProvider());
    	this.dataRolesViewer.setInput(vdbEditor);
    	
        // create columns
        TableViewerColumn column = this.dataRolesViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
        column.setLabelProvider(new DataRoleLabelProvider(0));

        column = this.dataRolesViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION));
        column.setLabelProvider(new DataRoleLabelProvider(1));

        this.dataRolesViewer.setInput(this.vdbEditor.getVdb());
        
        final MenuManager dataRolesMenuManager = new MenuManager();
        dataRolesViewer.getControl().setMenu(dataRolesMenuManager.createContextMenu(parent));
        
        this.dataRolesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteButton.setEnabled(false);
					editButton.setEnabled(false);
				} else {
					Object[] objs = sel.toArray();

					deleteButton.setEnabled(true);
					editButton.setEnabled(objs.length == 1);
				}
				
                dataRolesMenuManager.removeAll();
                if (sel.size() == 1) {
                    selectedDataRole = (DataRole)sel.getFirstElement();
                    dataRolesMenuManager.add(cloneDataRoleAction);
                }
			}
		});
        
        this.dataRolesViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				Object[] objs = sel.toArray();
				if( objs.length == 1 && objs[0] instanceof DataRole) {
					handleEdit((DataRole)objs[0]);
				}
			}
		});

        this.cloneDataRoleAction = new Action(i18n("cloneDataRoleActionLabel")) { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {

                if (selectedDataRole != null) {
                    DataRole newDR = new DataRole(
                                                  selectedDataRole.getName() + i18n("cloneDataRoleAction.copySuffix"), //$NON-NLS-1$
                                                  selectedDataRole.getDescription(), selectedDataRole.isAnyAuthenticated(),
                                                  selectedDataRole.isAllowCreateTempTables(), selectedDataRole.isGrantAll(),
                                                  selectedDataRole.getRoleNames(), selectedDataRole.getPermissions());
                    vdbEditor.getVdb().addDataRole(newDR);
                    dataRolesViewer.refresh();
                }

            }
        };

        this.cloneDataRoleAction.setEnabled(true);

        // Add selection changed listener so if a Physical Source model is selected, the applicable menu actions are
        // retrieved via the SourceHandler extension point and interface.
        // This allows changing Translator and JNDI names via existing deployed objects on Teiid Instances that are
        // connected in the user's workspace.


        dataRolesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {


            }
        });
        
        Composite buttonPanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 5);
        GridLayoutFactory.fillDefaults().numColumns(5).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    	newButton = new Button(buttonPanel, SWT.PUSH);
    	newButton.setImage(VdbUiPlugin.singleton.getImage(Images.ADD_ROLE));
//    	newButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(newButton);
    	newButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleNew();
			}
    		
		});
    	
    	editButton = new Button(buttonPanel, SWT.PUSH);
    	editButton.setImage(VdbUiPlugin.singleton.getImage(Images.EDIT_ROLE));
//    	editButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.EDIT));
    	GridDataFactory.fillDefaults().applyTo(newButton);
    	editButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEdit((DataRole)(((IStructuredSelection)dataRolesViewer.getSelection()).getFirstElement()));
			}
    		
		});
    	
    	deleteButton = new Button(buttonPanel, SWT.PUSH);
    	deleteButton.setImage(VdbUiPlugin.singleton.getImage(Images.REMOVE_ROLE));
//    	deleteButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	GridDataFactory.fillDefaults().applyTo(newButton);
    	deleteButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
                if (ConfirmationDialog.confirm(CONFIRM_REMOVE_MESSAGE)) {
                	DataRole role = (DataRole)(((IStructuredSelection)dataRolesViewer.getSelection()).getFirstElement());
                    vdbEditor.getVdb().removeDataRole(role.getName());
                    refresh();
                }
			}
    		
		});
    }
    
    /**
     * convenience method to refresh the viewer
     */
    public void refresh() {
    	dataRolesViewer.refresh();
    }
    
    void handleNew() {
        ContainerImpl tempContainer = null;
        try {
            Collection<File> modelFiles = vdbEditor.getVdb().getModelFiles();

            tempContainer = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
            ModelEditorImpl.setContainer(tempContainer);
            for (File modelFile : modelFiles) {
                boolean isVisible = true;

                Resource r = tempContainer.getResource(URI.createFileURI(modelFile.getPath()), true);
                if (isVisible && ModelUtil.isModelFile(r) && !ModelUtil.isXsdFile(r)) {
                    EObject firstEObj = r.getContents().get(0);
                    ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                    String mmURI = ma.getPrimaryMetamodelUri();
                    if (RelationalPackage.eNS_URI.equalsIgnoreCase(mmURI)
                        || XmlDocumentPackage.eNS_URI.equalsIgnoreCase(mmURI)
                        || WEB_SERVICES_VIEW_MODEL_URI.equalsIgnoreCase(mmURI)
                        || ModelIdentifier.FUNCTION_MODEL_URI.equals(mmURI)) {
                        // DO NOTHING. This leaves the resource in the temp container
                    } else {
                        tempContainer.getResources().remove(r);
                    }
                } else {
                    tempContainer.getResources().remove(r);
                }
            }
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            ModelEditorImpl.setContainer(null);
        }

        final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
        
        boolean grantNoneAsDefault = MessageDialog.openQuestion(iww.getShell(), 
        		Messages.dataRolesPanel_confirm_default_role_access_title, 
        		Messages.dataRolesPanel_confirm_default_role_access_message);
        
        Set<String> roleNames = VdbUtil.getDataRoleNames(vdbEditor.getVdb(), null);
        final DataRoleWizard wizard = new DataRoleWizard(tempContainer, null, vdbEditor.getVdb().getAllowedLanguages(), roleNames, grantNoneAsDefault);

        wizard.init(iww.getWorkbench(), new StructuredSelection(vdbEditor.getVdb().getModelEntries()));
        final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
        final int rc = dialog.open();
        if (rc == Window.OK) {
            // Get the Data Policy
            DataRole dp = wizard.getFinalDataRole();
            if (dp != null) {
            	vdbEditor.getVdb().addDataRole(dp);
            	refresh();
            }

        }
    }
    
    void handleEdit(DataRole dataRole) {
        if (dataRole == null) {
            return;
        }
        ContainerImpl tempContainer = null;
        try {
            Collection<File> modelFiles = vdbEditor.getVdb().getModelFiles();

            tempContainer = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
            ModelEditorImpl.setContainer(tempContainer);
            for (File modelFile : modelFiles) {
                boolean isVisible = true;

                Resource r = tempContainer.getResource(URI.createFileURI(modelFile.getPath()), true);
                if (isVisible && ModelUtil.isModelFile(r) && !ModelUtil.isXsdFile(r)) {
                    EObject firstEObj = r.getContents().get(0);
                    ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                    String mmURI = ma.getPrimaryMetamodelUri();
                    if (RelationalPackage.eNS_URI.equalsIgnoreCase(mmURI)
                        || XmlDocumentPackage.eNS_URI.equalsIgnoreCase(mmURI)
                        || WEB_SERVICES_VIEW_MODEL_URI.equalsIgnoreCase(mmURI)
                        || ModelIdentifier.FUNCTION_MODEL_URI.equals(mmURI)) {
                        // DO NOTHING. This leaves the resource in the temp container
                    } else {
                        tempContainer.getResources().remove(r);
                    }
                } else {
                    tempContainer.getResources().remove(r);
                }
            }
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            ModelEditorImpl.setContainer(null);
        }

        final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
        Set<String> roleNames = VdbUtil.getDataRoleNames(vdbEditor.getVdb(), dataRole.getName());
        final DataRoleWizard wizard = new DataRoleWizard(tempContainer, dataRole, vdbEditor.getVdb().getAllowedLanguages(), roleNames, false);

        wizard.init(iww.getWorkbench(), new StructuredSelection(vdbEditor.getVdb().getModelEntries()));
        final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
        final int rc = dialog.open();
        if (rc == Window.OK) {
            // Get the Data Policy
            DataRole dp = wizard.getFinalDataRole();
            // 1) Check to see if the name changed.. if changed, we need to remove the OLD 
            if (dp != null) {
            	if( vdbEditor.getVdb().getDataRoles().contains(dataRole) ) {
                	dataRole.setName(dp.getName());
                	dataRole.setAnyAuthenticated(dp.isAnyAuthenticated());
                	dataRole.setAllowCreateTempTables(dp.isAllowCreateTempTables());
                	dataRole.setGrantAll(dp.isGrantAll());
                	dataRole.setDescription(dp.getDescription());
                	dataRole.setPermissions(dp.getPermissions());
                	
                	Permission systemPerm = dataRole.getPermission(DataRole.SYS_ADMIN_TABLE_TARGET);
                	if( systemPerm != null ) {
                		dataRole.addPermission(new Permission(DataRole.SYS_ADMIN_TABLE_TARGET,
                				false, systemPerm.getCRUDValue(Type.READ).booleanValue(), false, 
                				false, systemPerm.getCRUDValue(Type.EXECUTE).booleanValue(), false));
                	}
                    if (!dataRole.isAnyAuthenticated() && !dp.getRoleNames().isEmpty()) {
                    	dataRole.setRoleNames(dp.getRoleNames());
                    }
                    
                    vdbEditor.getVdb().setModified(this, Event.DATA_POLICY_REMOVED, dataRole, dataRole);
                    refresh();
            	}
            }

        }
    }
    
	class DataRoleLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public DataRoleLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof DataRole ) {
				switch (this.columnNumber) {
					case 0: {
						return ((DataRole)element).getName();
					}
					case 1: {
						return ((DataRole)element).getDescription();
					}
				}
			}
			return StringConstants.EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			switch (this.columnNumber) {
			case 0: {
				return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
			}
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
		}
		return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == 0 ) {
				return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.DATA_ROLE);
			}
			return null;
		}
		
		
	}
	
	class DataRolesContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if( inputElement instanceof Vdb ) {
				Collection<DataRole> roles = ((Vdb)inputElement).getDataRoles();
				return roles.toArray();
			}

			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if( element instanceof Vdb ) {
				Collection<DataRole> roles = ((Vdb)element).getDataRoles();
				
				return !roles.isEmpty();
			}
			
			return false;
		}

	}
}
