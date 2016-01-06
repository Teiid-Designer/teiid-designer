/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.actions;

import static org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants.NAMESPACE_PROVIDER;
import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
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
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper;
import org.teiid.designer.core.workspace.ModelObjectAnnotationHelper;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.metamodels.relational.impl.ProcedureImpl;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.wizards.webservices.RestWarDeploymentInfoDialog;
import org.teiid.designer.runtime.ui.wizards.webservices.util.RestProcedure;
import org.teiid.designer.runtime.ui.wizards.webservices.util.WarArchiveUtil;
import org.teiid.designer.ui.actions.ISelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.XmiVdb;


/**
 * @since 8.0
 */
public class GenerateRestWarAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(GenerateRestWarAction.class);
    private static final String VDB_EXTENSION = "vdb"; //$NON-NLS-1$
    private static final ModelObjectAnnotationHelper ANNOTATION_HELPER = new ModelObjectAnnotationHelper();

    private IFile selectedVDB;
    // Map of models containing restful procedures
    private Map<String, List<RestProcedure>> restfulProcedureMap = new HashMap<String, List<RestProcedure>>();
    
    private Properties designerProperties;

    /**
     * 
     */
    public GenerateRestWarAction() {
        this.setText(UTIL.getString(I18N_PREFIX + "text")); //$NON-NLS-1$
        this.setToolTipText(UTIL.getString(I18N_PREFIX + "tooltip")); //$NON-NLS-1$
        this.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.CREATE_WAR));
        setDisabledImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.CREATE_WAR));
        setEnabled(false);
        
    }
    
    /**
     * @param properties Designer properties for WAR generation
     */
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
     * @return whether selection is applicable
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

    /**
     * @param result
     * @param obj
     */
    private static boolean isVdb(Object obj) {
        if (obj == null)
            return false;

        if (! (obj instanceof IFile))
            return false;

        return VDB_EXTENSION.equals(((IFile) obj).getFileExtension());
    }

    /**
     * @param eObjectList
     * @return list of rest procedures
     */
    private static List<RestProcedure> findRestProcedures(ModelResource modelResource) throws Exception {
        List<RestProcedure> restfulProcedureArray = new ArrayList<RestProcedure>();
        Collection<EObject> eObjectList = modelResource.getEObjects();
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

        return restfulProcedureArray;
    }

    /**
     * @param selection
     * @return if selection was set
     */
    public boolean setSelection(ISelection selection) {
        if (SelectionUtilities.isMultiSelection(selection))
            return false;

        Object obj = SelectionUtilities.getSelectedObject(selection);
        // If a VDB is selected and it contains a web service model then
        // enable
        if (! (obj instanceof IFile))
            return false;

        if (! isVdb(obj))
            return false;

        this.selectedVDB = (IFile)obj;

        boolean result = false;
        try {
            Vdb vdb = new XmiVdb(this.selectedVDB);
            Set<VdbModelEntry> modelEntrySet = vdb.getModelEntries();
            restfulProcedureMap.clear();
            for (VdbModelEntry vdbModelEntry : modelEntrySet) {
                final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(vdbModelEntry.getPath());
                if (! ModelIdentifier.isVirtualModelType(modelResource))
                    continue;

                List<RestProcedure> restfulProcedureArray = findRestProcedures(modelResource);
                if (restfulProcedureArray.size() > 0) {
                    String modelName = vdbModelEntry.getName();
                    restfulProcedureMap.put(modelName, restfulProcedureArray);
                    result = true;
                }
            }
        } catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            return false;
        }

        return result;
    }

    @Override
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        boolean enable = setSelection(selection);

        setEnabled(enable);
    }
    
    private static String getHeaders( Procedure procedure ) {
        Object headers = null;

        try {
            // try new way first
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
            headers = assistant.getPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.HEADERS);

            if (headers==null || CoreStringUtil.isEmpty((String)headers)) {
                headers = ANNOTATION_HELPER.getPropertyValueAnyCase(procedure,
                                                                        ModelObjectAnnotationHelper.EXTENDED_PROPERTY_NAMESPACE
                                                                                + "headers"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            UTIL.log(e);
        }

        return headers==null?StringConstants.EMPTY_STRING:(String)headers;
    }
    
    private static String getCharset( Procedure procedure ) {
        String charset = null;

        try {
            // try new way first
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
            charset = assistant.getPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.CHARSET);

            if (CoreStringUtil.isEmpty(charset)) {
                charset = (String)ANNOTATION_HELPER.getPropertyValueAnyCase(procedure,
                                                                        ModelObjectAnnotationHelper.EXTENDED_PROPERTY_NAMESPACE
                                                                                + "CHARSET"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            UTIL.log(e);
        }

        return charset;
    }
    
    /**
     * @param eObject
     * @throws ModelerCoreException
     */
    private static void createRestProcedureCollection( Procedure procedure,
                                                String name,
                                                String fullName,
                                                List restfulProcedureArray ) {
        String restMethod = WarArchiveUtil.getRestMethod(procedure);
        LinkedList<String> queryParameterList = new LinkedList<String>();
        LinkedList<String> headerParameterList = new LinkedList<String>();
        

        if (restMethod != null) {
            String uri = WarArchiveUtil.getUri(procedure);
            String charSet = getCharset(procedure);
            if (charSet==null){
            	charSet=Charset.defaultCharset().name();
            }
            
            // the procedure is not eligible for REST exposure without a URI defined
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

                //Check for HTTP Header parameters
                String headers = getHeaders(procedure);
                if (headers.length()>0){ 
                	String[] headerParameterArray = headers.split(";"); //$NON-NLS-1$
                	for (String param : headerParameterArray) {
	                    headerParameterList.addLast(param);
	                }
                }
                
                //Check for URI parameters
                String uriString = uri;
                for (int i = 0; i < uriString.length(); i++) {
                    String character = uriString.substring(i, i + 1);
                    if (character.equals("{")) { //$NON-NLS-1$
                        uriParameterCount++;
                    }
                }
                
                //Check for query parameters
                if (uriString.indexOf("&")>-1){ //$NON-NLS-1$
                	String[] queryParameterArray = uriString.split("&"); //$NON-NLS-1$
	                int i = 0;
                	for (String param : queryParameterArray) {
	                	i++;
                		if (i==1) {
                			uri= param; //Set the first token as our URI and continue
                		    continue; 
                		}
	                    queryParameterList.addLast(param);
	                }
                }
                
                restProcedure.setDescription(WarArchiveUtil.getRestDescription(procedure));
                restProcedure.setCharSet(charSet);
                restProcedure.setRestMethod(restMethod);
                restProcedure.setUri(uri);
                restProcedure.setProcedureName(name);
                restProcedure.setFullyQualifiedProcedureName(fullName);
                restProcedure.setQueryParameterList(queryParameterList);
                restProcedure.setHeaderParameterList(headerParameterList);

                // Create JSON version
                RestProcedure jsonRestProcedure = new RestProcedure();
                jsonRestProcedure.setDescription(WarArchiveUtil.getRestDescription(procedure));
                jsonRestProcedure.setCharSet(charSet);
                jsonRestProcedure.setFullyQualifiedProcedureName(restProcedure.getFullyQualifiedProcedureName());
                jsonRestProcedure.setModelName(restProcedure.getModelName());
                jsonRestProcedure.setProcedureName(restProcedure.getProcedureName());
                jsonRestProcedure.setRestMethod(restProcedure.getRestMethod());
                jsonRestProcedure.setUri(restProcedure.getUri());
                jsonRestProcedure.setQueryParameterList(queryParameterList);
                jsonRestProcedure.setHeaderParameterList(headerParameterList);          
                
                // If the parameterCount is greater than the number of parameters passed
                // on the URI, we can expect more parameters via an input stream
                // so the consumes annotation will need to be set. We will set for XML and JSON methods.
                boolean hasInputStream = false;
                if (uriParameterCount + headerParameterList.size() < parameterCount &&
                	queryParameterList.size() + headerParameterList.size() < parameterCount) {
                    hasInputStream = true;
                    restProcedure.setConsumesAnnotation("@Consumes( MediaType.APPLICATION_XML )"); //$NON-NLS-1$
                    jsonRestProcedure.setConsumesAnnotation("@Consumes( MediaType.APPLICATION_JSON )"); //$NON-NLS-1$
                }

                if (hasReturn) {
                    restProcedure.setProducesAnnotation("@Produces( MediaType.APPLICATION_XML+"+"\"; charset="+charSet+"\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    jsonRestProcedure.setProducesAnnotation("@Produces( MediaType.APPLICATION_JSON+"+"\"; charset="+charSet+"\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
