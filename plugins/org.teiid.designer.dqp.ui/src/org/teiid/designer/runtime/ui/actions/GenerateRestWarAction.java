/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.actions;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import static org.teiid.designer.runtime.extension.rest.RestModelExtensionConstants.NAMESPACE_PROVIDER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.dqp.webservice.war.objects.RestProcedure;
import org.teiid.designer.dqp.webservice.war.ui.wizards.RestWarDeploymentInfoDialog;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.runtime.extension.rest.RestModelExtensionConstants;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;

import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.impl.ProcedureImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelObjectAnnotationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class GenerateRestWarAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(GenerateRestWarAction.class);
    private static final String VDB_EXTENSION = "vdb"; //$NON-NLS-1$
    private static final ModelObjectAnnotationHelper ANNOTATION_HELPER = new ModelObjectAnnotationHelper();

    private IFile selectedVDB;
    // Map of models containing restful procedures
    private Map<String, List<RestProcedure>> restfulProcedureMap = new HashMap<String, List<RestProcedure>>();
    
    private Properties designerProperties;

    public GenerateRestWarAction() {
        this.setText(UTIL.getString(I18N_PREFIX + "text")); //$NON-NLS-1$
        this.setToolTipText(UTIL.getString(I18N_PREFIX + "tooltip")); //$NON-NLS-1$
        this.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.CREATE_WAR));
        setDisabledImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.CREATE_WAR));
        setEnabled(false);
        
    }
    
    public void setDesingerProperties(Properties properties) {
    	this.designerProperties = properties;
    }

    @Override
    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    /**
     * @param selection
     * @return
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {

        final IWorkbenchWindow window = DqpUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        boolean cont = true;
        if (compiler == null) {
            cont = MessageDialog.openConfirm(window.getShell(), UTIL.getString(I18N_PREFIX + "javaWarningTitle"), //$NON-NLS-1$
                                             UTIL.getString(I18N_PREFIX + "invalidJDKMessage")); //$NON-NLS-1$
        }

        if (!cont) {
            notifyResult(false);
            return;
        }

        RestWarDeploymentInfoDialog dialog = null;
        dialog = new RestWarDeploymentInfoDialog(window.getShell(), this.selectedVDB, this.restfulProcedureMap, null, this.designerProperties);

        int rc = dialog.open();

        // Retrieve the file name for the confirmation dialog
        String warFileName = dialog.getWarFileName();

        final String successMessage = UTIL.getString(I18N_PREFIX + "warFileCreated", warFileName); //$NON-NLS-1$

        boolean wasSuccessful = (rc == Window.OK);
        if (wasSuccessful) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    MessageDialog.openInformation(window.getShell(), UTIL.getString(I18N_PREFIX + "creationCompleteTitle"),//$NON-NLS-1$ 
                                                  successMessage);
                }
            });
        } else {
            if (rc != Window.CANCEL) {

                MessageDialog.openError(window.getShell(), UTIL.getString(I18N_PREFIX + "creationFailedTitle"),//$NON-NLS-1$ 
                                        dialog.getMessage());
            }
        }
        notifyResult(rc == Window.OK);
    }
    
    public boolean setSelection(ISelection selection) {
    	boolean result = false;
        List restfulProcedureArray = null;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            // If a VDB is selected and it contains a web service model then
            // enable
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals(VDB_EXTENSION)) {
                    this.selectedVDB = (IFile)obj;
                    Vdb vdb = new Vdb(this.selectedVDB, new NullProgressMonitor());
                    Set<VdbModelEntry> modelEntrySet = vdb.getModelEntries();
                    for (VdbModelEntry vdbModelEntry : modelEntrySet) {
                        final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(vdbModelEntry.getName());
                        if (ModelIdentifier.isVirtualModelType(modelResource)) {
                            restfulProcedureArray = new ArrayList<RestProcedure>();
                            String modelName = FileUtils.getFilenameWithoutExtension(vdbModelEntry.getName().lastSegment());
                            Collection<EObject> eObjectList = null;
                            try {
                                eObjectList = modelResource.getEObjects();
                                for (EObject eObject : eObjectList) {
                                    if (SqlAspectHelper.isProcedure(eObject)) {
                                        IPath path = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(eObject);
                                        final StringBuffer sb = new StringBuffer();
                                        final String[] segments = path.segments();
                                        for (int i = 0; i < segments.length; i++) {
                                            if (i != 0) {
                                                sb.append('.');
                                            }
                                            final String segment = segments[i];
                                            sb.append(segment);
                                        }
                                        String fullName = sb.toString();
                                        String name = ((ProcedureImpl)eObject).getName();
                                        createRestProcedureCollection((Procedure)eObject, name, fullName, restfulProcedureArray);
                                    }
                                }

                                if (restfulProcedureArray.size() > 0) {
                                    restfulProcedureMap.put(modelName, restfulProcedureArray);
                                }

                            } catch (ModelWorkspaceException e) {
                                throw new RuntimeException(e);
                            }

                            if (restfulProcedureArray.size() > 0) {
                            	result = true;
                            }

                        }
                    }

                }
            }
        }
        
        return result;
    }

    @Override
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        boolean enable = setSelection(selection);

        setEnabled(enable);
    }
    
    public static boolean isRestWarVdb(IFile vdbFile) {
    	boolean result = false;
        String extension = vdbFile.getFileExtension();
        if (extension != null && extension.equals(VDB_EXTENSION)) {
        	List restfulProcedureArray = null;
        	
            Vdb vdb = new Vdb(vdbFile, new NullProgressMonitor());
            Set<VdbModelEntry> modelEntrySet = vdb.getModelEntries();
            for (VdbModelEntry vdbModelEntry : modelEntrySet) {
                final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(vdbModelEntry.getName());
                if (ModelIdentifier.isVirtualModelType(modelResource)) {
                	restfulProcedureArray = new ArrayList<RestProcedure>();

                    Collection<EObject> eObjectList = null;
                    try {
                        eObjectList = modelResource.getEObjects();
                        for (EObject eObject : eObjectList) {
                            if (SqlAspectHelper.isProcedure(eObject)) {
                                IPath path = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(eObject);
                                final StringBuffer sb = new StringBuffer();
                                final String[] segments = path.segments();
                                for (int i = 0; i < segments.length; i++) {
                                    if (i != 0) {
                                        sb.append('.');
                                    }
                                    final String segment = segments[i];
                                    sb.append(segment);
                                }
                                String fullName = sb.toString();
                                String name = ((ProcedureImpl)eObject).getName();
                                createRestProcedureCollection((Procedure)eObject, name, fullName, restfulProcedureArray);
                            }
                        }

                        if (restfulProcedureArray.size() > 0) {
                        	result = true;
                        }
                    } catch (ModelWorkspaceException e) {
                        throw new RuntimeException(e);
                    }
                    if( result ) {
                    	break;
                    }
                }
            }

        }
        
        return result;
    }

    private static String getRestMethod( Procedure procedure ) {
        String restMethod = null;

        try {
            // try new way first
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
            restMethod = assistant.getPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.REST_METHOD);

            if (CoreStringUtil.isEmpty(restMethod.trim())) {
                // try old way
                restMethod = (String)ANNOTATION_HELPER.getPropertyValueAnyCase(procedure,
                                                                               ModelObjectAnnotationHelper.EXTENDED_PROPERTY_NAMESPACE
                                                                                       + "REST-METHOD"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            UTIL.log(e);
        }

        return restMethod;
    }

    private static String getUri( Procedure procedure ) {
        String uri = null;

        try {
            // try new way first
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
            uri = assistant.getPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.URI);

            if (CoreStringUtil.isEmpty(uri)) {
                uri = (String)ANNOTATION_HELPER.getPropertyValueAnyCase(procedure,
                                                                        ModelObjectAnnotationHelper.EXTENDED_PROPERTY_NAMESPACE
                                                                                + "URI"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            UTIL.log(e);
        }

        return uri;
    }

    /**
     * @param eObject
     * @return
     * @throws ModelerCoreException
     */
    private static void createRestProcedureCollection( Procedure procedure,
                                                String name,
                                                String fullName,
                                                List restfulProcedureArray ) {
        String restMethod = getRestMethod(procedure);

        if (restMethod != null) {
            String uri = getUri(procedure);

            // the procedure is not eligible for REST exposure with a URI defined
            if (uri != null) {
                boolean hasReturn = false;
                int parameterCount = procedure.getParameters().size();
                int uriParameterCount = 0;
                RestProcedure restProcedure = new RestProcedure();

                // Get all child EObjects for this procedure
                EList<EObject> contents = procedure.eContents();
                for (EObject eobject : contents) {
                    // If this is a result set, set hasReturn true and we will
                    // add the produces annotation to the RestProcedure instance
                    if (SqlAspectHelper.isProcedureResultSet(eobject)) {
                        hasReturn = true;
                        continue;
                    }

                }

                String uriString = uri;
                for (int i = 0; i < uriString.length(); i++) {
                    String character = uriString.substring(i, i + 1);
                    if (character.equals("{")) { //$NON-NLS-1$
                        uriParameterCount++;
                    }
                }
                restProcedure.setRestMethod(restMethod);
                restProcedure.setUri(uri);
                restProcedure.setProcedureName(name);
                restProcedure.setFullyQualifiedProcedureName(fullName);

                // Create JSON version
                RestProcedure jsonRestProcedure = new RestProcedure();
                jsonRestProcedure.setFullyQualifiedProcedureName(restProcedure.getFullyQualifiedProcedureName());
                jsonRestProcedure.setModelName(restProcedure.getModelName());
                jsonRestProcedure.setProcedureName(restProcedure.getProcedureName());
                jsonRestProcedure.setRestMethod(restProcedure.getRestMethod());
                jsonRestProcedure.setUri(restProcedure.getUri());

                // If the parameterCount is greater than the number of parameters passed
                // on the URI, we can expect more parameters via an input stream
                // so the consumes annotation will need to be set. We will set for XML and JSON methods.

                boolean hasInputStream = false;
                if (uriParameterCount < parameterCount) {
                    hasInputStream = true;
                    restProcedure.setConsumesAnnotation("@Consumes( MediaType.APPLICATION_XML )"); //$NON-NLS-1$
                    jsonRestProcedure.setConsumesAnnotation("@Consumes( MediaType.APPLICATION_JSON )"); //$NON-NLS-1$
                }

                if (hasReturn) {
                    restProcedure.setProducesAnnotation("@Produces( MediaType.APPLICATION_XML )"); //$NON-NLS-1$
                    jsonRestProcedure.setProducesAnnotation("@Produces( MediaType.APPLICATION_JSON )"); //$NON-NLS-1$
                }

                restfulProcedureArray.add(restProcedure);

                // Only add JSON operation if there is a return or InputStream. Otherwise, one method will do.
                if (hasReturn || hasInputStream) {
                    restfulProcedureArray.add(jsonRestProcedure);
                }
            }
        }
    }
}
