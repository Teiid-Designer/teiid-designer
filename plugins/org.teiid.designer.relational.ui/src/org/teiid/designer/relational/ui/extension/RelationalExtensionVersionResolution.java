package org.teiid.designer.relational.ui.extension;

import java.io.InputStream;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.ModelExtensionAssistantAggregator;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class RelationalExtensionVersionResolution  implements IMarkerResolution {

	private String medID;

    public RelationalExtensionVersionResolution(String medID) {
		super();
		this.medID = medID;
	}

	/* (non-Javadoc)
     * @see org.eclipse.ui.IMarkerResolution#getLabel()
     */
    @Override
    public String getLabel() {
        return Messages.upgradeRelationalExtensionsLabel;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
     */
    @Override
    public void run(IMarker marker) {
        IResource resource = marker.getResource();

        // Fix the Marked Model Resource
        if(ModelUtilities.isModelFile(resource)) {
            final IFile theFile = (IFile)resource;
            
            final ModelEditor editor = ModelEditorManager.getModelEditorForFile(theFile, false);
            // If editor is open and dirty, ask user whether to save
            if ((editor != null) && editor.isDirty()) {
                boolean saveEditor = MessageDialog.openQuestion(getShell(), Messages.quickFixModelDirtyTitle,
                                                                Messages.quickFixModelDirtyMsg);
                if(!saveEditor) {
                    return;
                } else {
                    // Add the selected Med
                    UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                        @Override
                        public void run() {
                            editor.doSave(new NullProgressMonitor());
                            fixModelMed(theFile);
                        }
                    });   
                    return;
                }
            }
            // Add the selected Med
            UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                  fixModelMed(theFile);
                }
            });

            // Fix the Marked mxd File
        }
//        else if(ModelUtilities.isMedFile(resource)) {
//            final IFile theFile = (IFile)resource;
//            // Check whether there is currently an open editor for the selected Med
//            final IEditorPart editor = UiUtil.getEditorForFile(theFile, false);
//
//            // If editor is open and dirty, ask user whether to save
//            if ((editor != null) && editor.isDirty()) {
//                boolean saveEditor = MessageDialog.openQuestion(getShell(), Messages.quickFixMedFileDirtyTitle,
//                                                                Messages.quickFixMedFileDirtyMsg);
//                if (!saveEditor) {
//                    return;
//                } else {
//                    // Add the selected Med
//                    UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
//                        @Override
//                        public void run() {
//                          editor.doSave(new NullProgressMonitor());
//                          fixMedFile(theFile);
//                        }
//                    });   
//                    return;
//                }
//
//            }
//            // Add the selected Med
//            UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
//                @Override
//                public void run() {
//                  fixMedFile(theFile);
//                }
//            });
//        }
    }

    /*
     * Fix Legacy Names in the supplied Model File by replacing the legacy 'com.metamatrix' names with 'org.teiid.designer'
     * @param modelResource the supplied model resource
     */
    private void fixModelMed( IFile modelFile ) {
        // Get the namespaces which are currently persisted on the model
        final ModelExtensionAssistantAggregator aggregator = ExtensionPlugin.getInstance().getModelExtensionAssistantAggregator();
        Collection<String> supportedNamespaces = null; 

        try {
            supportedNamespaces = aggregator.getSupportedNamespacePrefixes(modelFile);
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, NLS.bind(Messages.getSupportedPrefixesErrorMsg, modelFile.getName()));
            return;
        }

        boolean modelNeedsSave = false;

        ModelExtensionAssistant assistant = null;
        
        if( medID.equalsIgnoreCase("relational") ) {
	        String nsPrefix = RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
	        
	        assistant = ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(nsPrefix);
        } else if( medID.equalsIgnoreCase("rest") ) {
        	String nsPrefix = RestModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
	        
	        assistant = ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(nsPrefix);
        }
        // if assistant is null, it couldn't find in registry. Create a default assistant.
        if (assistant == null) {
        	UiConstants.Util.log(IStatus.ERROR, medID + " MED not found in model" + modelFile.getName()); //NLS.bind(Messages.relationalExtensionNotFoundInModel, modelFile.getName()));
        	return;
        }

        ModelObjectExtensionAssistant mAssistant = (ModelObjectExtensionAssistant)assistant;
        ModelExtensionDefinition modelMed = null;
        try {
            modelMed = mAssistant.getModelExtensionDefinition(modelFile);
        } catch (Exception e) {
        	UiConstants.Util.log(IStatus.ERROR, e, NLS.bind(Messages.getModelMedErrorMsg, modelFile.getName()));
            return;
        }
        
        boolean medChanged = true; //modelMed.getVersion() < assistant.getModelExtensionDefinition().getVersion();

        if(medChanged) {
            // Save the ModelResource MED
            try {
                mAssistant.saveModelExtensionDefinition(modelFile);
            } catch (Exception e) {
            	UiConstants.Util.log(IStatus.ERROR, e, NLS.bind(Messages.saveModelMedErrorMsg, modelFile.getName()));
            }
            modelNeedsSave = true;
        }
        
        if(modelNeedsSave) {
            try {
                ModelResource mdlResc = ModelUtilities.getModelResourceForIFile(modelFile, false);
                if(mdlResc!=null) {
                    mdlResc.save(new NullProgressMonitor(), true);
                }
                modelFile.deleteMarkers(UiConstants.ExtensionIds.MED_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
                modelFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            } catch (CoreException e) {
            	UiConstants.Util.log(IStatus.ERROR, e, NLS.bind(Messages.saveModelErrorMsg, modelFile.getName()));
            }
        }

    }

    /*
     * Fix Legacy Names in the supplied MED File by replacing the legacy 'com.metamatrix' names with 'org.teiid.designer'
     * @param medFile the supplied MED File
     */
    private void fixMedFile( IFile medFile ) {

//    System.out.println(" RelationalExtensionVersionResolution.fixMedFile() called");
//        try {
//            ModelExtensionDefinition med = parse(medFile.getContents());
//            if(med!=null) {
//                // Update the legacy classnames in the MED
//                updateMed(med);
//
//                // Re-write the Med File
//                final ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
//                final InputStream medInputStream = medWriter.writeAsStream(med);
//                medFile.setContents(medInputStream, false, false, new NullProgressMonitor());
//
//                // Delete Error Markers and Refresh
//                medFile.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
//                medFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
//            }
//        } catch (Exception e) {
//        	UiConstants.Util.log(IStatus.ERROR, e, NLS.bind(Messages.fixMedFileClassnamesFailedMsg, medFile.getName()));
//        }
    }

    /*
     * Update the the supplied MED by updating any extended legacy classnames to the new classnames
     * @param med the ModelExtensionDefinition
     * @return 'true' if any metaclass names were updated, 'false' if not
     */
    private boolean updateMed(ModelExtensionDefinition med) {
        boolean wasChanged = false;
//        if(med!=null) {
//            String[] extendedMCs = med.getExtendedMetaclasses();
//            if(extendedMCs!=null) {
//                for(int i=0; i<extendedMCs.length; i++) {
//                    String metaclass = extendedMCs[i];
//                    if(metaclass!=null && metaclass.startsWith(LEGACY_METACLASS_PREFIX)) {
//                        String newMetaclass = metaclass.replaceAll(LEGACY_METACLASS_PREFIX, NEW_METACLASS_PREFIX);
//                        med.updateMetaclass(metaclass, newMetaclass);
//                        wasChanged = true;
//                    }
//                }
//            }
//        }
        return wasChanged;
    }

    /*
     * Parse the supplied InputStream and return the MED
     * @param mxdContents the supplied InputStream
     * @return the ModelExtensionDefinition
     */
    private ModelExtensionDefinition parse(InputStream mxdContents) {
        ModelExtensionDefinition med = null;
        try {
            med = ExtensionPlugin.getInstance().parse(mxdContents);
        } catch (Exception e) {
        	UiConstants.Util.log(Messages.medFileParseErrorMsg);
        }
        return med;
    }

    private static Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

}
