/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf.ui;

import com.metamatrix.modeler.internal.ui.util.IModelerPerspectiveContributor;
import com.metamatrix.modeler.internal.ui.util.PerspectiveObject;

/**
 * @since 5.0
 */
public class UdfViewPerspectiveContributor implements IModelerPerspectiveContributor {

    PerspectiveObject[] contributions;

    /**
     * @since 5.0
     */
    public UdfViewPerspectiveContributor() {
        super();
        createContributions();
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.util.IModelerPerspectiveContributor#getContributions()
     * @since 4.3
     */
    public PerspectiveObject[] getContributions() {
        return contributions;
    }

    private void createContributions() {
        PerspectiveObject udfView = new PerspectiveObject(UdfUiPlugin.UDF_MODEL_VIEW, false, PerspectiveObject.TOP_LEFT);

        contributions = new PerspectiveObject[1];
        contributions[0] = udfView;
    }
}
