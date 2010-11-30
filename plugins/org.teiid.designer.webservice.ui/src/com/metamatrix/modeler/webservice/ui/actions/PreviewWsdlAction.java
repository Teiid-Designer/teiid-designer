package com.metamatrix.modeler.webservice.ui.actions;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.webservice.gen.BasicWsdlGenerator;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.text.ScaledFontManager;

public class PreviewWsdlAction extends SortableSelectionAction {

    public List<ModelResource> wsResources = new ArrayList<ModelResource>();
    public static String WSDL_GENERATION = "WSDL Preview"; //$NON-NLS-1$
    public static String WSDL_GENERATION_SUCCESS = "Successfully generated WSDL file: "; //$NON-NLS-1$
    public static String WSDL_GENERATION_ERROR = "There was an error generating the WSDL..."; //$NON-NLS-1$
    public static String WSDL_DIALOG_TITLE = "WSDL Preview"; //$NON-NLS-1$
        

    public PreviewWsdlAction() {
        super();
    }

    @Override
    public void run() {

        BasicWsdlGenerator wsdlGenerator = new BasicWsdlGenerator();
        // This will be overwritten by the web service model name
        String webServiceName = "TeiidWS"; //$NON-NLS-1$
        for (ModelResource webServiceModel : wsResources) {
            try {
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

        webServiceName = webServiceName.substring(0, webServiceName.lastIndexOf(".")); //$NON-NLS-1$
        wsdlGenerator.setName(webServiceName);
        wsdlGenerator.setTargetNamespace("http://teiid.org"); //$NON-NLS-1$
        wsdlGenerator.setUrlRootForReferences(""); //$NON-NLS-1$
        wsdlGenerator.setUrlSuffixForReferences(""); //$NON-NLS-1$
        wsdlGenerator.setUrlForWsdlService("http://serverName:port/warName/"); //$NON-NLS-1$
        final IStatus status = wsdlGenerator.generate(new NullProgressMonitor());
        
        // Create a StringBuffer into which the WSDL can be written ...
        final ByteArrayOutputStream bas = new ByteArrayOutputStream();
        final BufferedOutputStream stream = new BufferedOutputStream(bas);
        try {
        	wsdlGenerator.write(stream);
        } catch (IOException e) {
            MessageDialog.openInformation(null, WSDL_GENERATION, WSDL_GENERATION_ERROR + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            if ( stream != null ) {
                try {
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}
            }
        }
        
        // nothing more to do if an error is expected
        if (status.getSeverity() == IStatus.ERROR) {
            ErrorDialog.openError(null, WSDL_GENERATION, WSDL_GENERATION_ERROR, status);
            throw new RuntimeException("Unable to generate WSDL"); //$NON-NLS-1$
        }
        
        new WsdlDialog(Display.getDefault().getActiveShell(), bas.toString()).open();
        
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
                ModelResource modelResource = null;
                if (next instanceof IFile) {
                	modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)next);
                	if (modelResource != null) {
                    	isValid = ModelIdentifier.isWebServicesViewModel(modelResource);
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
    
    class WsdlDialog extends Dialog {

        private StyledText text;
        private String wsdlString;

        /**
         * Construct an instance of WsdlDialog.
         */
        public WsdlDialog(Shell shell, String wsdlString) {
            super(shell, WSDL_DIALOG_TITLE);
            this.wsdlString = wsdlString;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite)super.createDialogArea(parent);
            // add controls to composite as necessary

            text = new StyledText(composite, SWT.V_SCROLL);
            GridData gd = new GridData(GridData.FILL_BOTH);
            text.setLayoutData(gd);

            text.setEditable(false);
            text.setWordWrap(true);
            text.setTabs(4);

            StyleRange bodyRange = new StyleRange();
            bodyRange.start = 0;
            bodyRange.length = wsdlString.length();
            ScaledFontManager fontManager = new ScaledFontManager();
            text.setFont(fontManager.createFontOfSize(10));

            text.setText(wsdlString);
            text.setStyleRange(bodyRange);

            super.setSizeRelativeToScreen(75, 70);

            return composite;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.window.Window#create()
         */
        public void create() {
            setShellStyle(getShellStyle() | SWT.RESIZE);
            super.create();
            super.getShell().setText(WSDL_DIALOG_TITLE);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
         */
        protected void createButtonsForButtonBar(Composite parent) {
            Button okButton = createButton(
                parent,
                IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL,
                true);
            okButton.setFocus();
        }
    }
}
