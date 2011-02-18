/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.xsd.ui;

import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelInitializer;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.util.INewModelObjectHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelInitializerSelectionDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;


/** 
 * @since 4.3
 */
public class XsdSchemaUiNewModelObjectHelper implements
                                                 INewModelObjectHelper {

    /** 
     * 
     * @since 4.3
     */
    public XsdSchemaUiNewModelObjectHelper() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     * @since 4.3
     */
    public boolean canHelpCreate(Object newObject) {
        CoreArgCheck.isNotNull(newObject);
        if(newObject instanceof XSDSchemaImpl) {
            return true;
        }
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#helpCreate(java.lang.Object, Map)
     * @since 4.3
     */
    public boolean helpCreate(Object newObject, Map properties) throws ModelerCoreException {
        XSDSchemaImpl xsdSchemaImpl = (XSDSchemaImpl) newObject;
        MetamodelDescriptor descriptor = ModelUtilities.getModelResourceForModelObject(xsdSchemaImpl).getPrimaryMetamodelDescriptor();
        
        if (descriptor != null) {
            final List initializerNames = descriptor.getModelInitializerNames();
            final int numInitializers = initializerNames.size();
            String initializerName = null;
            if ( numInitializers == 1 ) {
                // Only one, so choose it ...
                initializerName = (String) initializerNames.get(0);
            } else if ( numInitializers > 1 ) {
                // More than one, so give choice to users ...
                ModelInitializerSelectionDialog dialog = new ModelInitializerSelectionDialog(Display.getCurrent().getActiveShell(), descriptor);
                dialog.setInitialSelection();
                dialog.open();
                if ( dialog.getReturnCode() == Window.OK && dialog.getResult() != null && dialog.getResult().length > 0 ) {
                    initializerName = (String) dialog.getResult()[0];
                } else {
                    // can't cancel, so just use the first one
                    initializerName = (String) initializerNames.get(0);
                }
            }
            
            if ( initializerName != null ) {
                // Run the initializer ...
                final ModelInitializer initializer = descriptor.getModelInitializer(initializerName);
                if ( initializer != null ) {
                    try {
                        initializer.execute(xsdSchemaImpl.eResource());
                    } catch (Exception e) {
                        UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                    }
                }
            }
        }
        return true;
    }

}
