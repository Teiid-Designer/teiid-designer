/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.ui.search;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * The <code>MetadataSearchLabelProvider</code> is a label provider for use in metadata searching.
 * 
 * @since 6.0.0
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
