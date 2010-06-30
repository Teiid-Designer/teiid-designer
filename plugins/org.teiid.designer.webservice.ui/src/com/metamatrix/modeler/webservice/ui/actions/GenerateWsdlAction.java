package com.metamatrix.modeler.webservice.ui.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.webservice.gen.BasicWsdlGenerator;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.webservice.ui.IUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class GenerateWsdlAction extends SortableSelectionAction {

    public List<ModelResource> wsResources = new ArrayList();

    public GenerateWsdlAction() {
        super();
    }

    @Override
    public void run() {

        BasicWsdlGenerator wsdlGenerator = new BasicWsdlGenerator();
        ModelResource wsModel = null;
        // This will be overwritten by the web service model name
        String webServiceName = "TeiidWS"; //$NON-NLS-1$
        for (ModelResource webServiceModel : wsResources) {
            try {
                wsModel = webServiceModel;
                wsdlGenerator.addWebServiceModel(webServiceModel.getEmfResource());
                webServiceName = webServiceModel.getItemName();
                IResource[] iResources = WorkspaceResourceFinderUtil.getDependentResources(webServiceModel.getResource());
                for (IResource iResource : iResources) {
                    if (ModelIdentifier.isSchemaModel(iResource)) {
                        wsdlGenerator.addXsdModel(importSchema(iResource.getLocation().toOSString()), iResource.getLocation());
                    }
                }
            } catch (ModelWorkspaceException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // TODO: Create wizard to override these default values as part of the soap war generator in 7.1
        webServiceName = webServiceName.substring(0, webServiceName.lastIndexOf(".")); //$NON-NLS-1$
        wsdlGenerator.setName(webServiceName);
        wsdlGenerator.setTargetNamespace("http://teiid.org"); //$NON-NLS-1$
        wsdlGenerator.setUrlRootForReferences(""); //$NON-NLS-1$
        wsdlGenerator.setUrlSuffixForReferences(""); //$NON-NLS-1$
        wsdlGenerator.setUrlForWsdlService("http://serverName:port/warName/"); //$NON-NLS-1$
        final IStatus status = wsdlGenerator.generate(new NullProgressMonitor());

        // nothing more to do if an error is expected
        if (status.getSeverity() == IStatus.ERROR) {
            throw new RuntimeException("Unable to generate WSDL"); //$NON-NLS-1$
        }

        try {
            // create our wsdl file and write to it
            String fileName = webServiceName + "." + IUiConstants.WSDL_FILE_EXTENSION; //$NON-NLS-1$
            String path = wsModel.getResource().getLocation().toOSString();
            OutputStream stream = new FileOutputStream(new File(path.substring(0, path.lastIndexOf("/")), fileName)); //$NON-NLS-1$
            wsdlGenerator.write(stream);
            // Get an iFile instance to refresh our workspace
            IFile iFile = wsModel.getModelProject().getProject().getFile(fileName);
            iFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());

        } catch (CoreException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public XSDSchema importSchema( String path ) {
        XSDParser parser = new XSDParser(null);
        parser.parse(path);
        XSDSchema schema = parser.getSchema();
        schema.setSchemaLocation(path);
        return schema;
    }

    @Override
    protected boolean isValidSelection( ISelection selection ) {
        boolean isValid = true;
        wsResources = new ArrayList();
        if (SelectionUtilities.isEmptySelection(selection)) {
            isValid = false;
        }

        if (isValid) {
            final Collection objs = SelectionUtilities.getSelectedObjects(selection);
            final Iterator selections = objs.iterator();
            while (selections.hasNext() && isValid) {
                final Object next = selections.next();
                if (next instanceof Table) {
                    isValid = true;
                } else if (next instanceof Procedure) {
                    isValid = true;
                } else if (next instanceof IFile) {
                    final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)next);
                    if (modelResource != null) {
                        isValid = ModelIdentifier.isWebServicesViewModel(modelResource);
                        wsResources.add(modelResource);
                    } else {
                        isValid = false;
                    }
                } else {
                    isValid = false;
                }

                // stop processing if no longer valid:
                if (!isValid) {
                    break;
                } // endif -- valid
            } // endwhile -- all selected
        } // endif -- is empty sel

        return isValid;
    }

    @Override
    public boolean isApplicable( ISelection selection ) {
        return isValidSelection(selection);
    }
}
