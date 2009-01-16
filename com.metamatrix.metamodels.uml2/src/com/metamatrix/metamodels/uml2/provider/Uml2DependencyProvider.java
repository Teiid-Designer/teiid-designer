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
package com.metamatrix.metamodels.uml2.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.uml2.uml.Classifier;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.core.association.AssociationProvider;

/**
 * ForeignKeyAssociationProvider
 */
public class Uml2DependencyProvider implements AssociationProvider {

    public static final Class[] VALID_CLASSES_TYPES = new Class[] {Classifier.class};

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.modeler.core.association.AssociationProvider#getNewAssociationDescriptors(java.util.List)
     */
    public Collection getNewAssociationDescriptors( final List eObjects ) {

        // If the list of selected objects contains invalid entities then return
        if (!containsValidObjects(eObjects, VALID_CLASSES_TYPES)) {
            return Collections.EMPTY_LIST;
        }

        // If the list of selected objects contains a insufficient number
        // of tables either explicitly or implicitly referenced then return
        final List classifiers = Uml2DependencyProvider.getClassifiers(eObjects);
        if (classifiers.size() != 2) {
            return Collections.EMPTY_LIST;
        }

        // ---------------------------------------------------------------------------------------
        // Create a Uml2AssociationDescriptor for any situation that is considered ambiguous
        // ---------------------------------------------------------------------------------------
        final Classifier type1 = (Classifier)classifiers.get(0);
        final Classifier type2 = (classifiers.size() == 2 ? (Classifier)classifiers.get(1) : type1);
        final List tmp = new ArrayList(3);

        Uml2DependencyDescriptor tmpDescr = null;

        if (type1 != type2) {
            // Realization [type1]<-- realizes --[type2]
            tmpDescr = new Uml2RealizationDescriptor(type2, type1);
            Object[] params = new Object[] {type1.getName(), type2.getName()};
            String statusMsg = Uml2Plugin.Util.getString("Uml2DependencyProvider.Ambiguous_UML_realization_status_between_{0}_and_{1}_1_1", params); //$NON-NLS-1$
            String textMsg = Uml2Plugin.Util.getString("Uml2DependencyProvider.{0}_<--_realizes_---_{1}_2", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Realization [type1]---->[type2]
            tmpDescr = new Uml2RealizationDescriptor(type1, type2);
            textMsg = Uml2Plugin.Util.getString("Uml2DependencyProvider.{0}_---_realizes_-->_{1}_3", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);
        }

        // Since all the possibilities are considered ambiguous ...
        final List result = new ArrayList(1);

        // Create a Uml2DependencyDescriptor composed of all the possible
        // ways to create the dependency
        Uml2DependencyDescriptor descriptor = new Uml2DependencyDescriptor(eObjects);
        Object[] params = new Object[] {type1.getName(), type2.getName()};
        String statusMsg = Uml2Plugin.Util.getString("Uml2DependencyProvider.Ambiguous_UML_realization_status_4", params); //$NON-NLS-1$
        String textMsg = Uml2Plugin.Util.getString("Uml2DependencyProvider.Ambiguous_realization_between_{0}_and_{1}_5", params); //$NON-NLS-1$
        descriptor.updateStatus(IStatus.WARNING, -1, statusMsg, null);
        descriptor.setText(textMsg);
        descriptor.setAmbiguous(true);

        // Add all descriptors
        for (Iterator iter = tmp.iterator(); iter.hasNext();) {
            AssociationDescriptor ad = (AssociationDescriptor)iter.next();
            descriptor.addDescriptor(ad);
        }
        result.add(descriptor);

        return result;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * Gather all Classifier instances either implicitly or explicitly defined in the list of objects. The list may contain
     * instances of Uml element types.
     * 
     * @return
     */
    public static List getClassifiers( final List eObjects ) {
        ArgCheck.isNotNull(eObjects);
        final List result = new ArrayList(eObjects.size());
        for (Iterator iter = eObjects.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            Classifier classifier = null;
            if (obj instanceof Classifier) {
                classifier = (Classifier)obj;
            }
            if (classifier != null && !result.contains(classifier)) {
                result.add(classifier);
            }
        }
        return result;
    }

    /**
     * Return true if the list of objects are allowable RelationalEntity instances that represent valid EObjects to be used in the
     * creation of a foreign key relationship.
     */
    public static boolean containsValidObjects( final List eObjects,
                                                final Class[] validClasses ) {
        ArgCheck.isNotNull(eObjects);
        ArgCheck.isNotNull(validClasses);
        for (Iterator iter = eObjects.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            // Return false if the object is null
            if (obj == null) {
                return false;
            }
            // Return false if the object is not a valid class instance
            boolean validClassInstance = false;
            for (int i = 0; i < validClasses.length; i++) {
                if (validClasses[i].isInstance(obj)) {
                    validClassInstance = true;
                    break;
                }
            }
            if (!validClassInstance) {
                return false;
            }
        }
        return true;
    }

}
