/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.MarkerUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.graphics.GlobalUiColorManager;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * XmlDocumentModelObjectLabelProvider is a wrapper around EMF's ILabelProvider that colors EObject icons with virtual or physical
 * colors. It also supplies a specialized icon for Built-in Datatypes. jh Lyra enh: Creating this new subclass of
 * ModelObjectLabelProvider so that we can add custom behavior for the Xml Document Tree. Basically, we need to make this provider
 * aware of the details of the doc model so we can annotate certain nodes, for example anchor points.
 */
public class XmlDocumentModelObjectLabelProvider extends ModelExplorerLabelProvider {

    private static final String SUFFIX_EXCLUDED = Util.getString("XmlDocumentModelObjectLabelProvider.suffixExcluded"); //$NON-NLS-1$

    /** The color being replaced. */
    private static final Color TEMP_COLOR = GlobalUiColorManager.getColor(new RGB(247, 247, 247));

    private static final Color PHYSICAL_COLOR = GlobalUiColorManager.getColor(new RGB(0, 220, 225));

    private static final Color VIRTUAL_COLOR = GlobalUiColorManager.getColor(new RGB(255, 204, 102));

    static Color gray = null;

    private ILabelProvider delegate = null;
    private TreeMappingAdapter tmaMappingAdapter;
    private DocumentTreeController docTreeController;

    public XmlDocumentModelObjectLabelProvider() {
        super();
        delegate = ModelUtilities.getAdapterFactoryLabelProvider();
    }

    public XmlDocumentModelObjectLabelProvider( DocumentTreeController docTreeController ) {
        super();
        delegate = ModelUtilities.getAdapterFactoryLabelProvider();

        // jh Defect 21277: The state of backing maps was getting out of sync which appeared as
        // errors in the text labels produced by this provider. The fix is to
        // give this class a reference to DocumentTreeController so that he can
        // always get the latest copy of MappingAdapterFilter/TreeMappingAdapter.
        this.docTreeController = docTreeController;
    }

    /**
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( Object theElement ) {

        Image result = null;

        if (theElement instanceof XSDSimpleTypeDefinition) {
            try {
                // Only care about built-in types, so just use the workspace DT Mgr ...
                if (ModelerCore.getWorkspaceDatatypeManager().isBuiltInDatatype((XSDSimpleTypeDefinition)theElement)) {
                    result = UiPlugin.getDefault().getImage(PluginConstants.Images.BUILTIN_DATATYPE);
                } else {
                    result = delegate.getImage(theElement);
                }
            } catch (Exception e) {
                result = delegate.getImage(theElement);
            }

        } else if (theElement instanceof EObject) {
            EObject eObj = (EObject)theElement;
            Image temp = delegate.getImage(theElement);

            boolean virtual = ModelObjectUtilities.isVirtual(eObj);

            String prefix = (virtual) ? "virtual." : "physical."; //$NON-NLS-1$ //$NON-NLS-2$
            // image registry uses the base image hashCode as key
            String imageId = prefix + temp.hashCode();

            UiPlugin plugin = UiPlugin.getDefault();
            if (plugin.isImageRegistered(imageId)) {
                result = plugin.getImage(imageId);
            } else {
                result = UiUtil.createImage(temp, TEMP_COLOR, (virtual) ? VIRTUAL_COLOR : PHYSICAL_COLOR);
                plugin.registerPluginImage(imageId, result);
            }
        } else {
            result = delegate.getImage(theElement);
        }

        return result;
    }

    /**
     * Overloaded getImage necessary for finding icons for EObjects that are not inside a Model. Specifically, this method was
     * written for the New Child/Sibling menu items.
     * 
     * @param theElement
     * @param modelType
     * @return
     * @since 4.2
     */
    public Image getImage( final EObject theElement,
                           boolean isVirtual ) {
        Image result = null;

        if (theElement instanceof XSDSimpleTypeDefinition) {
            try {
                // Only care about built-in types, so just use the workspace DT Mgr ...
                if (ModelerCore.getWorkspaceDatatypeManager().isBuiltInDatatype(theElement)) {
                    result = UiPlugin.getDefault().getImage(PluginConstants.Images.BUILTIN_DATATYPE);
                } else {
                    result = delegate.getImage(theElement);
                }
            } catch (Exception e) {
                result = delegate.getImage(theElement);
            }

        } else {
            Image temp = delegate.getImage(theElement);

            String prefix = (isVirtual) ? "virtual." : "physical."; //$NON-NLS-1$ //$NON-NLS-2$
            // image registry uses the base image hashCode as key
            String imageId = prefix + temp.hashCode();

            UiPlugin plugin = UiPlugin.getDefault();
            if (plugin.isImageRegistered(imageId)) {
                result = plugin.getImage(imageId);
            } else {
                result = UiUtil.createImage(temp, TEMP_COLOR, (isVirtual) ? VIRTUAL_COLOR : PHYSICAL_COLOR);
                plugin.registerPluginImage(imageId, result);
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( Object theElement ) {
        // System.out.println("\n[XmlDocumentModelObjectLabelProvider.getText] TOP; theElement:  " + theElement );
        // Thread.dumpStack();
        /*
         * jh Lyra enh: for M1.3, we'll need to determine if the element is the 
         *              "anchor point" or "Mapping Root" of a MC; if so we tack
         *              the MC name to the end of the text.  Example:
         *                  () sequence - MC: book
         *              Can we do the same for getImage()???  If we wish to tack
         *              and MC icon onto the EMF icon ( '{}' for sequence, for example),
         *              can we do that?  Or can we create a second set of EMF icons that
         *              combine the EMF designation with the MC icon and substitute the
         *              appropriate one whenever a node is an MC root????
         *              Ask Dan F.
         *              
         *              Also consider these issues for M1.2: "Change icons in tree to 
         *              indicate multiplicity (use XSD icons).    
         * 
         */
        String sText = StringUtilities.replaceWhitespace(delegate.getText(theElement), true);
        String sFinalText = sText;
        // System.out.println("[XmlDocumentModelObjectLabelProvider.getText] About to process: " + sText );

        /*
         * jh Lyra enh: Determine if this node is the anchor of a Mapping Class.
         *              If so, add the MC's name to the text of this node.
         */
        if (theElement instanceof EObject) {
            // jh Defect 21277: Get MappingAdapterFilter from DocumentTreeController.
            tmaMappingAdapter = docTreeController.getMappingAdapterFilter().getMappingAdapter();

            MappingClass mc = tmaMappingAdapter.getMappingClass((EObject)theElement);
            // if ( mc != null ) {
            // System.out.println("[XmlDocumentModelObjectLabelProvider.getText] we have an MC, will we use it?: " + mc.getName()
            // );
            // }

            // we'll find a mapping class ONLY if this treenode is the mapping root element
            if (mc != null && mc.getName() != null && mc.getName().trim() != "") { //$NON-NLS-1$
                String sMappingClassName = mc.getName();
                sFinalText = sText + " [MC: " + sMappingClassName + "]"; //$NON-NLS-1$  //$NON-NLS-2$
            } else {
                // System.out.println("[XmlDocumentModelObjectLabelProvider.getText] NO MC:");
            }

            // jh Lyra enh: TODO do the same for Staging Tables...
            StagingTable st = tmaMappingAdapter.getStagingTableForRootTreeNode((EObject)theElement);
            // if ( st != null ) {
            // System.out.println("[XmlDocumentModelObjectLabelProvider.getText] we have an ST, will we use it?: " + st.getName()
            // );
            // }
            // we'll find a staging table ONLY if this treenode is mapped to a staging table
            // (a staging table can only be mapped to one element)
            if (st != null && st.getName() != null && st.getName().trim() != "") { //$NON-NLS-1$
                String sStagingTableName = st.getName();
                sFinalText = sText + " [ST: " + sStagingTableName + "]"; //$NON-NLS-1$  //$NON-NLS-2$
            } else {
                // System.out.println("[XmlDocumentModelObjectLabelProvider.getText] NO ST:");
            }

        }
        // System.out.println("[XmlDocumentModelObjectLabelProvider.getText] About to return: " + sFinalText );

        return sFinalText;
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
        IMarker[] mrkrs = null;
        boolean errorOccurred = false;
        try {
            mrkrs = resrc.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
        } catch (CoreException ex) {
            Util.log(ex);
            errorOccurred = true;
        }
        if (!errorOccurred) {
            final IMarker[] markers = mrkrs;
            ImageDescriptor decorationIcon = getDecorationIcon((EObject)element, resrc, markers);
            if (decorationIcon != null) {
                decoration.addOverlay(decorationIcon);
            }
        }

        // defect 19275 - fiddle with colors for certain nodes:

        // first, obtain gray from the system if we don't have it yet:
        if (gray == null) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    gray = UiUtil.getSystemColor(SWT.COLOR_GRAY);
                }
            }); // endanon
        } // endif

        // check if this node is excluded or is part of a subtree that is:
        if (XmlDocumentUtil.isExcluded(element, true)) {
            decoration.setForegroundColor(gray);
            // also show [excluded] when actually set to this node:
            if (XmlDocumentUtil.isExcluded(element, false)) decoration.addSuffix(SUFFIX_EXCLUDED);
        } // endif
    }

    private ImageDescriptor getDecorationIcon( EObject element,
                                               IResource resrc,
                                               IMarker[] markers ) {
        ImageDescriptor icon = null;

        for (int ndx = markers.length; --ndx >= 0;) {
            IMarker marker = markers[ndx];

            EObject targetEObject = ModelWorkspaceManager.getModelWorkspaceManager().getMarkerManager().getMarkedEObject(resrc,
                                                                                                                         marker);

            boolean usable = ((element == targetEObject) || ModelObjectUtilities.isDescendant(element, targetEObject));
            if (!usable) {
                continue;
            }
            final Object attr = MarkerUtilities.getMarkerAttribute(marker, IMarker.SEVERITY);
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
        if (element instanceof EObject) {
            IResource ir = null;
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject((EObject)element);
            if (modelResource != null) {
                ir = modelResource.getResource();
            }
            return ir;
        }
        return null;
    }

    /**
     * Method to provide a way to get an image with the URL instead of EObject and to colorize it based on virtual vs physical.
     * 
     * @param theElement
     * @param url
     * @return
     * @since 4.2
     */
    public Image getImage( EObject eObj,
                           Object url ) {
        Image result = null;

        Image temp = ModelObjectUtilities.getImageFromObject(url);
        if (temp != null) {
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObj);
            boolean virtual = (ModelUtilities.isVirtual(modelResource));

            String prefix = (virtual) ? "virtual." : "physical."; //$NON-NLS-1$ //$NON-NLS-2$
            // image registry uses the base image hashCode as key
            String imageId = prefix + temp.hashCode();

            UiPlugin plugin = UiPlugin.getDefault();
            if (plugin.isImageRegistered(imageId)) {
                result = plugin.getImage(imageId);
            } else {
                result = UiUtil.createImage(temp, TEMP_COLOR, (virtual) ? VIRTUAL_COLOR : PHYSICAL_COLOR);
                plugin.registerPluginImage(imageId, result);
            }
        }

        return result;
    }

    public void setTreeMappingAdapter( TreeMappingAdapter tmaMappingAdapter ) {
        this.tmaMappingAdapter = tmaMappingAdapter;
    }
}
