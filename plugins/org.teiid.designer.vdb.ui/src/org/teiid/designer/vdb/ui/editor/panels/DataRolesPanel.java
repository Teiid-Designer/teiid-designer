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
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.teiid.designer.core.ModelEditorImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ContainerImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.ui.common.table.DefaultTableProvider;
import org.teiid.designer.ui.common.table.TableAndToolBar;
import org.teiid.designer.ui.common.table.TextColumnProvider;
import org.teiid.designer.ui.common.widget.ButtonProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.vdb.VdbDataRole;
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
    TableAndToolBar<VdbDataRole> dataRolesGroup;
    Action cloneDataRoleAction;
    VdbDataRole selectedDataRole;
    
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
                VdbDataRole vdbDataRole = (VdbDataRole)selection.getFirstElement();
                if (vdbDataRole == null) {
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

                DataRole dataPolicy = new DataRole(vdbDataRole.getName(), vdbDataRole.getDescription(),
                                                   vdbDataRole.isAnyAuthenticated(), vdbDataRole.allowCreateTempTables(),
                                                   vdbDataRole.getMappedRoleNames(), vdbDataRole.getPermissions());

                final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
                //final NewDataRoleWizard wizard = new NewDataRoleWizard(tempContainer, dataPolicy);
                final DataRoleWizard wizard = new DataRoleWizard(tempContainer, dataPolicy, vdbEditor.getVdb().getAllowedLanguages());

                wizard.init(iww.getWorkbench(), new StructuredSelection(vdbEditor.getVdb().getModelEntries()));
                final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
                final int rc = dialog.open();
                if (rc == Window.OK) {
                    // Get the Data Policy
                    DataRole dp = wizard.getFinalDataRole();
                    if (dp != null) {
                        vdbEditor.getVdb().removeDataPolicy(vdbDataRole);
                        vdbEditor.getVdb().addDataPolicy(dp, new NullProgressMonitor());
                    }

                }
            }
        };
        
        dataRolesGroup = new TableAndToolBar(parent, 1, new DefaultTableProvider<VdbDataRole>() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultTableProvider#doubleClicked(java.lang.Object)
             */
            @Override
            public void doubleClicked( VdbDataRole element ) {
                editProvider.selected(new StructuredSelection(element));
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.TableProvider#getElements()
             */
            @Override
            public VdbDataRole[] getElements() {
                final Set<VdbDataRole> entries = vdbEditor.getVdb().getDataPolicyEntries();
                return entries.toArray(new VdbDataRole[entries.size()]);
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
        }, new TextColumnProvider<VdbDataRole>() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getImage(java.lang.Object)
             */
            @Override
            public Image getImage( final VdbDataRole element ) {
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
            public String getValue( final VdbDataRole element ) {
                return element.getName();
            }
        }, new TextColumnProvider<VdbDataRole>() {
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
            public String getValue( final VdbDataRole element ) {
                return element.getDescription();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#isEditable(java.lang.Object)
             */
            @Override
            public boolean isEditable( final VdbDataRole element ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#setValue(java.lang.Object, java.lang.Object)
             */
            @Override
            public void setValue( final VdbDataRole element,
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
                //final NewDataRoleWizard wizard = new NewDataRoleWizard(tempContainer, null);
                final DataRoleWizard wizard = new DataRoleWizard(tempContainer, null, vdbEditor.getVdb().getAllowedLanguages());

                wizard.init(iww.getWorkbench(), new StructuredSelection(vdbEditor.getVdb().getModelEntries()));
                final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
                final int rc = dialog.open();
                if (rc == Window.OK) {
                    // Get the Data Policy
                    DataRole dp = wizard.getFinalDataRole();
                    if (dp != null) {
                    	vdbEditor.getVdb().addDataPolicy(dp, new NullProgressMonitor());
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
                        if (element instanceof VdbDataRole) {
                        	vdbEditor.getVdb().removeDataPolicy((VdbDataRole)element);
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
                                                  selectedDataRole.allowCreateTempTables(),
                                                  selectedDataRole.getMappedRoleNames(), selectedDataRole.getPermissions());
                    vdbEditor.getVdb().addDataPolicy(newDR, new NullProgressMonitor());
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
                    selectedDataRole = (VdbDataRole)sel.getFirstElement();
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
