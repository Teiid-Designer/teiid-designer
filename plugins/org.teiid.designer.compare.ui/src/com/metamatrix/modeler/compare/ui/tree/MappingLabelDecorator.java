/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.ui.tree;

import java.util.Iterator;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.ui.PluginConstants;
import com.metamatrix.modeler.compare.ui.UiPlugin;
import com.metamatrix.modeler.ui.OverlayImageIcon;

/**
 * MappingLabelDecorator
 */
class MappingLabelDecorator implements
                           ILabelDecorator,
                           PluginConstants {

    // ===========================================================================================================================
    // Constants

    static final Image CHG_AND_CHG_BELOW_IMG = UiPlugin.getDefault().getImage(Images.DIFF_CHANGED_AND_CHANGED_BELOW_DECORATOR);
    static final Image CHG_BELOW_IMG = UiPlugin.getDefault().getImage(Images.DIFF_CHANGED_BELOW_DECORATOR);
    static final Image CHG_IMG = UiPlugin.getDefault().getImage(Images.DIFF_CHANGED_DECORATOR);
    static final Image OLD_IMG = UiPlugin.getDefault().getImage(Images.DIFF_OLD_DECORATOR);
    static final Image NEW_IMG = UiPlugin.getDefault().getImage(Images.DIFF_NEW_DECORATOR);
    static final Image FIRST_IMG = UiPlugin.getDefault().getImage(Images.DIFF_FIRST_DECORATOR);
    static final Image SECOND_IMG = UiPlugin.getDefault().getImage(Images.DIFF_SECOND_DECORATOR);

    // ===========================================================================================================================
    // Variables

    private int terminology;

    /**
     * The content provider contains information about EObject description differences. This decorator needs to have access to
     * this information when decorating.
     */
    private MappingTreeContentProvider contentProvider;

    // ===========================================================================================================================
    // Constructors

    MappingLabelDecorator(int terminology) {
        setTerminology(terminology);
    }

    // ===========================================================================================================================
    // Methods

    /**
     * The <code>MappingTreeContentProvider</code> has knowledge of descriptions (found in Annotations) differences.
     * 
     * @param theProvider
     *            the content provider
     * @since 4.2
     */
    public void setMappingTreeContentProvider(MappingTreeContentProvider theProvider) {
        this.contentProvider = theProvider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
     */
    public Image decorateImage(Image baseImage,
                               Object object) {
        if (!(object instanceof Mapping)) {
            return baseImage;
        }
        Mapping mapping = (Mapping)object;
        Image icon = null;
        if (DifferenceAnalysis.isAdd(mapping)) {
            if (this.terminology == DifferenceReportsPanel.USE_FIRST_SECOND_TERMINOLOGY) {
                icon = SECOND_IMG;
            } else {
                icon = NEW_IMG;
            }
        } else if (DifferenceAnalysis.isDelete(mapping)) {
            if (this.terminology == DifferenceReportsPanel.USE_FIRST_SECOND_TERMINOLOGY) {
                icon = FIRST_IMG;
            } else {
                icon = OLD_IMG;
            }
        } else if (DifferenceAnalysis.isChange(mapping)) {
            icon = CHG_IMG;
            if (mappingIsChangedBelow(mapping)) {
                icon = CHG_AND_CHG_BELOW_IMG;
            }
        } else if (DifferenceAnalysis.isChangeBelow(mapping)) {
            icon = CHG_BELOW_IMG;
        } else if (this.contentProvider != null) {
            // check for additional properties (like descriptions) that have differences.
            // these are properties differences that do not show up in the Mapping.
            // mark as changed if additional properties are found.
            if (this.contentProvider.hasAdditionalPropertyDifferences((Mapping)object)) {
                icon = CHG_IMG;
            }
        }
        if (icon != null) {
            // ImageData overlayData = icon.getImageData();
            // Overlay custom image over base image
            OverlayImageIcon overlayIcon = new OverlayImageIcon(baseImage, icon,
                                                                icon == CHG_BELOW_IMG ? OverlayImageIcon.BOTTOM_LEFT
                                                                                : OverlayImageIcon.BOTTOM_RIGHT);
            return overlayIcon.getImage();
        }
        return baseImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
     */
    public String decorateText(String text,
                               Object element) {
        return text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    public boolean isLabelProperty(Object element,
                                   String property) {
        return false;
    }

    private boolean mappingIsChangedBelow(Mapping mapping) {
        for (Iterator<Mapping> iter = mapping.getNested().iterator(); iter.hasNext();) {
            Mapping child = iter.next();
            if (((DifferenceDescriptor)child.getHelper()).getType() != DifferenceType.NO_CHANGE_LITERAL) {
                return true;
            }
        } // for
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {

    }

    public void setTerminology(int terminology) {
        switch (terminology) {
            case DifferenceReportsPanel.USE_FIRST_SECOND_TERMINOLOGY:
            case DifferenceReportsPanel.USE_OLD_NEW_TERMINOLOGY: {
                this.terminology = terminology;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
}
