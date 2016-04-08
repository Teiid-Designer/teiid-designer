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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
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
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.Crud.Type;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.ui.common.table.DefaultTableProvider;
import org.teiid.designer.ui.common.table.TableAndToolBar;
import org.teiid.designer.ui.common.table.TextColumnProvider;
import org.teiid.designer.ui.common.widget.ButtonProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiConstants.Images;
import org.teiid.designer.vdb.ui.VdbUiPlugin;
import org.teiid.designer.vdb.ui.editor.ConfirmationDialog;
import org.teiid.designer.vdb.ui.editor.VdbEditor;

/**
 *
 */
public class DataRolesPanel {
    static final String CONFIRM_OVERWRITE_UDFJAR_MESSAGE = i18n("confirmOverwriteUdfJarMessage"); //$NON-NLS-1$
    static final String INFORM_DATA_ROLES_ON_ADD_MESSAGE = i18n("informDataRolesExistOnAddMessage"); //$NON-NLS-1$
    static final String WEB_SERVICES_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$
    static final String DESCRIPTION_COLUMN_NAME = i18n("descriptionColumnName"); //$NON-NLS-1$;
    static final String CONFIRM_REMOVE_MESSAGE = i18n("confirmRemoveMessage"); //$NON-NLS-1$
    
    static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }

    VdbEditor vdbEditor;
    TableAndToolBar<DataRole> dataRolesGroup;
    Action cloneDataRoleAction;
    DataRole selectedDataRole;
    
    /**
     * @param parent
     * @param editor
     */
    public DataRolesPanel(Composite parent, VdbEditor editor) {
    	super();
    	this.vdbEditor = editor;
    	
    	createPanel(parent);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createPanel(Composite parent) {
        final String DATA_POLICY_COLUMN_NAME = i18n("dataPolicyName"); //$NON-NLS-1$

        final ButtonProvider editProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.EDIT_ROLE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("editRoleToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return (selection.size() == 1);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                DataRole dataRole = (DataRole)selection.getFirstElement();
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
                final DataRoleWizard wizard = new DataRoleWizard(tempContainer, dataRole, vdbEditor.getVdb().getAllowedLanguages(), roleNames);

                wizard.init(iww.getWorkbench(), new StructuredSelection(vdbEditor.getVdb().getModelEntries()));
                final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
                final int rc = dialog.open();
                if (rc == Window.OK) {
                    // Get the Data Policy
                    DataRole dp = wizard.getFinalDataRole();
                    if (dp != null) {
                    	if( !vdbEditor.getVdb().getDataRoles().contains(dp) ) {
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
                            
                            refresh();
                    	}
                    }

                }
            }
        };
        
        dataRolesGroup = new TableAndToolBar(parent, 1, new DefaultTableProvider<DataRole>() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultTableProvider#doubleClicked(java.lang.Object)
             */
            @Override
            public void doubleClicked( DataRole element ) {
                editProvider.selected(new StructuredSelection(element));
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.TableProvider#getElements()
             */
            @Override
            public DataRole[] getElements() {
                final Collection<DataRole> entries = vdbEditor.getVdb().getDataRoles();
                return entries.toArray(new DataRole[entries.size()]);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultTableProvider#isDoubleClickSupported()
             */
            @Override
            public boolean isDoubleClickSupported() {
                return true;
            }
        }, new TextColumnProvider<DataRole>() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getImage(java.lang.Object)
             */
            @Override
            public Image getImage( final DataRole element ) {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
             */
            @Override
            public String getName() {
                return DATA_POLICY_COLUMN_NAME;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
             */
            @Override
            public Image getImage() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
             */
            @Override
            public String getValue( final DataRole element ) {
                return element.getName();
            }
        }, new TextColumnProvider<DataRole>() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
             */
            @Override
            public String getName() {
                return DESCRIPTION_COLUMN_NAME;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
             */
            @Override
            public Image getImage() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
             */
            @Override
            public String getValue( final DataRole element ) {
            	if( element.getDescription() == null ) return StringConstants.EMPTY_STRING;
                return element.getDescription();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#isEditable(java.lang.Object)
             */
            @Override
            public boolean isEditable( final DataRole element ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#setValue(java.lang.Object, java.lang.Object)
             */
            @Override
            public void setValue( final DataRole element,
                                  final String value ) {
                element.setDescription(value);
            }
        });

        ButtonProvider newProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.ADD_ROLE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("addRoleToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
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
                Set<String> roleNames = VdbUtil.getDataRoleNames(vdbEditor.getVdb(), null);
                final DataRoleWizard wizard = new DataRoleWizard(tempContainer, null, vdbEditor.getVdb().getAllowedLanguages(), roleNames);

                wizard.init(iww.getWorkbench(), new StructuredSelection(vdbEditor.getVdb().getModelEntries()));
                final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
                final int rc = dialog.open();
                if (rc == Window.OK) {
                    // Get the Data Policy
                    DataRole dp = wizard.getFinalDataRole();
                    if (dp != null) {
                    	vdbEditor.getVdb().addDataRole(dp);
                    }

                }
            }
        };

        dataRolesGroup.add(newProvider);
        dataRolesGroup.add(editProvider);

        ButtonProvider removeProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.REMOVE_ROLE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("removeRoleToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return !selection.isEmpty();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                if (ConfirmationDialog.confirm(CONFIRM_REMOVE_MESSAGE)) {
                    for (final Object element : selection.toList()) {
                        if (element instanceof DataRole) {
                        	vdbEditor.getVdb().removeDataRole(((DataRole)element).getName());
                        }

                    }
                }
            }
        };

        dataRolesGroup.add(removeProvider);
        dataRolesGroup.setInput(vdbEditor.getVdb());

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
                    dataRolesGroup.getTable().getViewer().refresh();
                }

            }
        };

        this.cloneDataRoleAction.setEnabled(true);

        // Add selection changed listener so if a Physical Source model is selected, the applicable menu actions are
        // retrieved via the SourceHandler extension point and interface.
        // This allows changing Translator and JNDI names via existing deployed objects on Teiid Instances that are
        // connected in the user's workspace.
        final TableViewer dataRolesViewer = dataRolesGroup.getTable().getViewer();
        final MenuManager dataRolesMenuManager = new MenuManager();
        dataRolesViewer.getControl().setMenu(dataRolesMenuManager.createContextMenu(parent));
        dataRolesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
                dataRolesMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)dataRolesViewer.getSelection();
                if (sel.size() == 1) {
                    selectedDataRole = (DataRole)sel.getFirstElement();
                    dataRolesMenuManager.add(cloneDataRoleAction);
                }

            }
        });
    }
    
    /**
     * convenience method to refresh the viewer
     */
    public void refresh() {
    	dataRolesGroup.getTable().getViewer().refresh();
    }
}
