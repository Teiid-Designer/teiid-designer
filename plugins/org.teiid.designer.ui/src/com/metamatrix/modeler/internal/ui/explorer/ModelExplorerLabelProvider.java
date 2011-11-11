/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.explorer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.DiagramLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ExtendedModelObjectLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ImportContainer;
import com.metamatrix.modeler.internal.ui.viewsupport.MarkerUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;

/**
 * ModelExplorerLabelProvider
 * 
 * @since 4.0
 */
public class ModelExplorerLabelProvider extends LabelProvider
    implements ILightweightLabelDecorator, UiConstants, PluginConstants.Images {

    private static final String DOT_XMI = '.' + ModelUtil.EXTENSION_XMI;

    public static boolean debug = false;
    private static int instanceCounter = 0;

    private ILabelProvider defaultProvider;
    private ExtendedModelObjectLabelProvider extendedModelObjectLabelProvider;
    private IResourceChangeListener resrcChgListener;
    private DiagramLabelProvider diagramLabelProvider;
    private IBaseLabelProvider eventSource;

    // Defect 23509 - caching our own listener list so we can prevent unnecessary event processing.
    ListenerList myListeners = new ListenerList(ListenerList.IDENTITY);

    /**
     * @since 4.0
     */
    public ModelExplorerLabelProvider() {
        if (debug) {
            System.err.println("ModelExplorerLabelProvider instantiated: " + ++instanceCounter + " instance(s) in memory."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        this.eventSource = this;

        // Add listener for model validation completion events
        this.resrcChgListener = new IResourceChangeListener() {
            @Override
            public void resourceChanged( final IResourceChangeEvent event ) {
                final Display display = Display.getDefault();
                if (display.isDisposed()) {
                    return;
                }

                // Defect 23509 - preventing unnecessary event processing.
                // Don't do any work if ther are no listeners.... :)
                if (myListeners == null || myListeners.isEmpty()) {
                    return;
                }

                final IMarkerDelta[] deltas = event.findMarkerDeltas(null, true);

                if ((deltas != null) && (deltas.length > 0)) {
                    Set resources = new HashSet();

                    for (int i = 0; i < deltas.length; i++) {
                        resources.add(deltas[i].getResource());
                    }

                    final Object[] resourcesToUpdate = resources.toArray();

                    display.asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            changeLabel(resourcesToUpdate);
                        }
                    });
                }
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this.resrcChgListener);
        diagramLabelProvider = new DiagramLabelProvider();
        extendedModelObjectLabelProvider = new ExtendedModelObjectLabelProvider();
    }

    void changeLabel( Object[] resourcesToUpdate ) {
        fireLabelProviderChanged(new LabelProviderChangedEvent(getLabelProviderChangedEventSource(), resourcesToUpdate));
    }

    public void setLabelProviderChangedEventSource( IBaseLabelProvider theSource ) {
        this.eventSource = theSource;
    }

    public IBaseLabelProvider getLabelProviderChangedEventSource() {
        return this.eventSource;
    }

    /**
     * Method declared on IBaseLabelProvider. Defect 23509 - we need to keep track of # of listeners in this class because super
     * does not have a getter
     */
    @Override
    public void addListener( ILabelProviderListener listener ) {
        super.addListener(listener);
        myListeners.add(listener);
    }

    /**
     * Method declared on IBaseLabelProvider. Defect 23509 - we need to keep track of # of listeners in this class because super
     * does not have a getter
     */
    @Override
    public void removeListener( ILabelProviderListener listener ) {
        super.removeListener(listener);
        myListeners.remove(listener);
    }

    /**
     * @since 4.0
     */
    IResourceChangeListener getResourceChangeListener() {
        return this.resrcChgListener;
    }

    /**
     * @since 4.0
     */
    private ILabelProvider getDefaultProvider() {
        if (defaultProvider == null) {
            defaultProvider = new WorkbenchLabelProvider();
        }
        return defaultProvider;
    }

    /**
     * Removes listeners registered by this instance.
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        if (debug) {
            System.err.println("ModelExplorerLabelProvider disposed: " + --instanceCounter + " instance(s) in memory."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(getResourceChangeListener());
    }

    /**
     * Figures out which provider to delegate to.
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.0
     */
    @Override
    public Image getImage( Object element ) {
        final boolean startedTxn = ModelerCore.startTxn(false, false, null, this);
        try {

            if (element instanceof Diagram) {
                Image result = diagramLabelProvider.getImage(element);
                if (result == null) {
                    return UiPlugin.getDefault().getImage("icons/full/obj16/Diagram.gif"); //$NON-NLS-1$
                }
                return result;
            }

            if (element instanceof ImportContainer) {
                return UiPlugin.getDefault().getImage(IMPORT_CONTAINER);
            }

            if (element instanceof EObject) {
                if (element instanceof TransformationMappingRoot) {
                    return UiPlugin.getDefault().getImage("icons/full/obj16/Transform.gif"); //$NON-NLS-1$
                }
                return ModelUtilities.getEMFLabelProvider().getImage(element);
            } else if (element instanceof IFile && ModelUtilities.isModelFile((IFile)element) && ((IFile)element).exists()) {
                return ModelIdentifier.getModelImage((IResource)element);
            }
            Image result = extendedModelObjectLabelProvider.getImage(element);
            if (result != null) {
                return result;
            }

            return getDefaultProvider().getImage(element);
        } catch (final Exception err) {
            Util.log(err);
        } finally {
            if (startedTxn) {
                ModelerCore.commitTxn();
            }
        }

        return super.getImage(element);
    }

    /**
     * Figures out which provider to delegate to.
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.0
     */
    @Override
    public String getText( Object element ) {
        final boolean startedTxn = ModelerCore.startTxn(false, false, null, this);
        try {
            if (element instanceof Diagram) {
                String result = diagramLabelProvider.getText(element);
                if (result == null) {
                    return UiConstants.Util.getString("ModelExplorerLabelProvider.genericDiagramLabel"); //$NON-NLS-1$
                }
                return result;
            }

            if (element instanceof ImportContainer) {
                return element.toString();
            }

            if (element instanceof EObject) {
                if (element instanceof TransformationMappingRoot) {
                    return UiConstants.Util.getString("ModelExplorerLabelProvider.genericTransformationLabel"); //$NON-NLS-1$
                }
                ILabelProvider p = ModelUtilities.getEMFLabelProvider();
                return p.getText(element);
            }

            String result = extendedModelObjectLabelProvider.getText(element);
            if (result != null) {
                return result;
            }

            // ------------------------------------------------------------
            // Defect 22319 - Hide the .xmi file extension in Dimension
            // Utilizing the hidden-project centric characteristics
            // That way Enterprise doesn't hide the extension
            // ------------------------------------------------------------
            String defaultText = getDefaultProvider().getText(element);
            if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
                if (defaultText.endsWith(DOT_XMI)) {
                    int len = defaultText.lastIndexOf(DOT_XMI);
                    defaultText = defaultText.substring(0, len);
                }
            }
            return defaultText;
        } finally {
            if (startedTxn) {
                ModelerCore.commitTxn();
            }
        }

    }

    /**
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     * @since 4.0
     */
    @Override
    public void decorate( final Object element,
                          final IDecoration decoration ) {
        final Display display = Display.getDefault();
        if (display.isDisposed()) {
            return;
        }

        final IResource resrc = getResource(element);
        if (resrc == null || !resrc.exists() || ((resrc instanceof IProject) && !((IProject)resrc).isOpen())) {
            return;
        }
        IMarker[] markers = null;
        boolean errorOccurred = false;
        try {
            markers = resrc.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
        } catch (CoreException ex) {
            Util.log(ex);
            errorOccurred = true;
        }
        if (!errorOccurred) {
            ImageDescriptor decorationIcon = getDecorationIcon(markers);
            if (decorationIcon != null) {
                decoration.addOverlay(decorationIcon);
            }
        }

        try {
            // add suffix for special warnings:
            IMarker[] problems = resrc.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
            for (int i = 0; i < problems.length; i++) {
                IMarker marker = problems[i];
                String value = marker.getAttribute(ModelerCore.MARKER_PROBLEM_DECORATOR_TEXT, null);

                if (value != null) {
                    String message = UiConstants.Util.getString("ModelExplorerLabelProvider.problemMarkerBrackets", //$NON-NLS-1$
                                                                value);
                    decoration.addSuffix(message);
                    break;
                } // endif
            } // endfor
        } catch (CoreException ex) {
            Util.log(ex);
        } // endtry
        
        // Lastly, decorate with Extension if applicable
        
        if( element instanceof IFile ) {
            File file = ((IFile)element).getLocation().toFile();
            ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();

            try {
                for (String namespacePrefix : registry.getAllNamespacePrefixes()) {
                    ModelExtensionAssistant assistant = registry.getModelExtensionAssistant(namespacePrefix);

                    if ((assistant instanceof ModelObjectExtensionAssistant)
                            && ((ModelObjectExtensionAssistant)assistant).hasExtensionProperties(file)) {
                        decoration.addOverlay(UiPlugin.getDefault().getExtensionDecoratorImage(), IDecoration.TOP_LEFT);
                        break;
                    }
                }
            } catch (Exception e) {
                Util.log(IStatus.INFO, e, Util.getString("ModelExplorerLabelProvider.modelExtensionError", file.getName())); //$NON-NLS-1$
            }
        }

    }

    private ImageDescriptor getDecorationIcon( IMarker[] markers ) {
        final boolean startedTxn = ModelerCore.startTxn(false, false, null, this);
        ImageDescriptor icon = null;

        for (int ndx = markers.length; --ndx >= 0;) {
            final Object attr = MarkerUtilities.getMarkerAttribute(markers[ndx], IMarker.SEVERITY); // markers[ndx].getAttribute(IMarker.SEVERITY);
            if (attr == null) {
                continue;
            }
            // Asserting attr is an Integer...
            final int severity = ((Integer)attr).intValue();
            if (severity == IMarker.SEVERITY_ERROR) {
                icon = UiPlugin.getDefault().getErrorDecoratorImage();
                break;
            }
            if (icon == null && severity == IMarker.SEVERITY_WARNING) {
                icon = UiPlugin.getDefault().getWarningDecoratorImage();
            }
        }

        if (startedTxn) {
            ModelerCore.commitTxn();
        }

        return icon;
    }

    /**
     * Returns the resource for the specified element, or null if there is no resource associated with it.
     * 
     * @param element The element for which to find an associated resource
     * @return The resource for the specified element; may be null.
     * @since 4.0
     */
    private IResource getResource( final Object element ) {
        if (element instanceof IResource) {
            return (IResource)element;
        }
        if (element instanceof IAdaptable) {
            return (IResource)((IAdaptable)element).getAdapter(IResource.class);
        }
        return null;
    }
}
