/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.search;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;


/**
 * The <code>MetadataSearchLabelProvider</code> is a label provider for use in metadata searching.
 * 
 * @since 8.0
 */
public class MetadataSearchLabelProvider extends LabelProvider implements UiConstants {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * Delegate label provider for images.
     * 
     * @since 6.0.0
     */
    private ModelExplorerLabelProvider delegate;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @since 6.0.0
     */
    public MetadataSearchLabelProvider() {
        this.delegate = new ModelExplorerLabelProvider();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     * @since 6.0.0
     */
    @Override
    public String getText( Object element ) {
        // for MetadataMatchInfo objects return path and number of matches contained in that resource
        if (element instanceof MetadataMatchInfo) {
            MetadataMatchInfo matchInfo = (MetadataMatchInfo)element;
            int count = matchInfo.getMatchCount();

            if (count == 1) {
                return Util.getString(I18nUtil.getPropertyPrefix(MetadataSearchLabelProvider.class) + "resourceOneMatch.msg", //$NON-NLS-1$
                                      matchInfo.getResourcePath());
            }

            return Util.getString(I18nUtil.getPropertyPrefix(MetadataSearchLabelProvider.class) + "resourceMatches.msg", //$NON-NLS-1$
                                  matchInfo.getResourcePath(),
                                  count);
        }

        // must be IModelObjectMatch
        IModelObjectMatch match = (IModelObjectMatch)element;
        return match.getMatchDescription();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     * @since 6.0.0
     */
    @Override
    public Image getImage( Object element ) {
        if (element instanceof IModelObjectMatch) {
            IModelObjectMatch match = (IModelObjectMatch)element;
            EObject eObj = match.getEObject();

            if (eObj != null) {
                return this.delegate.getImage(eObj);
            }
        } else if (element instanceof MetadataMatchInfo) {
            IResource model = ((MetadataMatchInfo)element).getResource();
            return ModelIdentifier.getModelImage(model);
        }

        return super.getImage(element);
    }
}
