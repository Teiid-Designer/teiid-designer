/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.core.association.AssociationProvider;

/**
 * ForeignKeyAssociationProvider
 */
public class Uml2AssociationProvider implements AssociationProvider {

    public static final Class[] VALID_CLASSES_TYPES = new Class[] {Classifier.class, Property.class};

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
        final List classifiers = Uml2AssociationProvider.getClassifiers(eObjects);
        if (classifiers.size() != 2) {
            return Collections.EMPTY_LIST;
        }

        // ---------------------------------------------------------------------------------------
        // Create a Uml2AssociationDescriptor for any situation that is considered ambiguous
        // ---------------------------------------------------------------------------------------
        final Classifier type1 = (Classifier)classifiers.get(0);
        final Classifier type2 = (classifiers.size() == 2 ? (Classifier)classifiers.get(1) : type1);
        final List tmp = new ArrayList(11);

        Uml2AssociationDescriptor tmpDescr = null;
        boolean end1IsNavigable = false;
        AggregationKind end1Aggregation = AggregationKind.NONE_LITERAL;
        String end1Name = "End1"; //$NON-NLS-1$
        int end1LowerBound = 0;
        int end1UpperBound = 1;
        boolean end2IsNavigable = false;
        AggregationKind end2Aggregation = AggregationKind.NONE_LITERAL;
        String end2Name = "End2"; //$NON-NLS-1$
        int end2LowerBound = 0;
        int end2UpperBound = 1;

        if (type1 != type2) {
            // Association [type1]x---x[type2]
            end1IsNavigable = false;
            end2IsNavigable = false;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            Object[] params = new Object[] {type1.getName(), type2.getName()};
            String statusMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.Ambiguous_UML_association_status_between_{0}_and_{1}_1", params); //$NON-NLS-1$
            String textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_----_{1}_2", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]<---x[type2]
            end1IsNavigable = false;
            end2IsNavigable = true;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_<---_{1}_3", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]x--->[type2]
            end1IsNavigable = true;
            end2IsNavigable = false;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_--->_{1}_4", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]<--->[type2]
            end1IsNavigable = true;
            end2IsNavigable = true;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_<-->_{1}_5", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]<>---x[type2] shared
            end1IsNavigable = false;
            end2IsNavigable = false;
            end1Aggregation = AggregationKind.SHARED_LITERAL;
            end2Aggregation = AggregationKind.NONE_LITERAL;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_<s>---_{1}_6", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]<>--->[type2] shared
            end1IsNavigable = true;
            end2IsNavigable = false;
            end1Aggregation = AggregationKind.SHARED_LITERAL;
            end2Aggregation = AggregationKind.NONE_LITERAL;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_<s>-->_{1}_7", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]x---<>[type2] shared
            end1IsNavigable = false;
            end2IsNavigable = false;
            end1Aggregation = AggregationKind.NONE_LITERAL;
            end2Aggregation = AggregationKind.SHARED_LITERAL;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_---<s>_{1}_8", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]<---<>[type2] shared
            end1IsNavigable = false;
            end2IsNavigable = true;
            end1Aggregation = AggregationKind.NONE_LITERAL;
            end2Aggregation = AggregationKind.SHARED_LITERAL;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_<--<s>_{1}_9", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]<>---x[type2] composed
            end1IsNavigable = false;
            end2IsNavigable = false;
            end1Aggregation = AggregationKind.COMPOSITE_LITERAL;
            end2Aggregation = AggregationKind.NONE_LITERAL;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_<c>---_{1}_10", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]<>--->[type2] composed
            end1IsNavigable = true;
            end2IsNavigable = false;
            end1Aggregation = AggregationKind.COMPOSITE_LITERAL;
            end2Aggregation = AggregationKind.NONE_LITERAL;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_<c>-->_{1}_11", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]x---<>[type2] composed
            end1IsNavigable = false;
            end2IsNavigable = false;
            end1Aggregation = AggregationKind.NONE_LITERAL;
            end2Aggregation = AggregationKind.COMPOSITE_LITERAL;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_---<c>_{1}_12", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

            // Association [type1]<---<>[type2] composed
            end1IsNavigable = false;
            end2IsNavigable = true;
            end1Aggregation = AggregationKind.NONE_LITERAL;
            end2Aggregation = AggregationKind.COMPOSITE_LITERAL;
            tmpDescr = new Uml2AssociationDescriptor(type1, end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
                                                     end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name,
                                                     end2LowerBound, end2UpperBound);
            textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.{0}_<--<c>_{1}_13", params); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, statusMsg, null);
            tmpDescr.setText(textMsg);
            tmp.add(tmpDescr);

        }

        // Since all the possibilities are considered ambiguous ...
        final List result = new ArrayList(1);

        // Create a Uml2AssociationDescriptor composed of all the possible
        // ways to create the association
        Uml2AssociationDescriptor descriptor = new Uml2AssociationDescriptor(eObjects);
        Object[] params = new Object[] {type1.getName(), type2.getName()};
        String statusMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.Ambiguous_UML_association_status__14", params); //$NON-NLS-1$
        String textMsg = Uml2Plugin.Util.getString("Uml2AssociationProvider.Ambiguous_association_between_{0}_and_{1}__15", params); //$NON-NLS-1$
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
            } else if (obj instanceof Property) {
                Object container = ((Property)obj).eContainer();
                if (container instanceof Classifier) {
                    classifier = (Classifier)container;
                }
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
