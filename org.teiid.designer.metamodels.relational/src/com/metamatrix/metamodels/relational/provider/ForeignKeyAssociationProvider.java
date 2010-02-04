/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.core.association.AssociationProvider;

/**
 * ForeignKeyAssociationProvider
 */
public class ForeignKeyAssociationProvider implements AssociationProvider {

    public static final Class[] VALID_CLASSES_TYPES = new Class[] {Table.class, Column.class, PrimaryKey.class, ForeignKey.class};

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationProvider#getNewAssociationDescriptors(java.util.List)
     */
    public Collection getNewAssociationDescriptors( List eObjects ) {

        // If the list of selected objects contains invalid entities then return
        if (!containsValidObjects(eObjects, VALID_CLASSES_TYPES)) {
            return Collections.EMPTY_LIST;
        }

        // If the list of selected objects contains a insufficient number
        // of tables either explicitly or implicitly referenced then return
        final List tables = ForeignKeyAssociationProvider.getTables(eObjects);
        if (tables.size() != 2) {
            return Collections.EMPTY_LIST;
        }

        // ---------------------------------------------------------------------------------------
        // Create a ForeignKeyAssociationDescriptor for any situation that is considered ambiguous
        // ---------------------------------------------------------------------------------------
        final Table tableA = (Table)tables.get(0);
        final Table tableB = (Table)tables.get(1);
        final PrimaryKey pkA = ForeignKeyAssociationProvider.getPrimaryKey(eObjects, tableA);
        final PrimaryKey pkB = ForeignKeyAssociationProvider.getPrimaryKey(eObjects, tableB);
        final ForeignKey fkA = ForeignKeyAssociationProvider.getForeignKey(eObjects, tableA);
        final ForeignKey fkB = ForeignKeyAssociationProvider.getForeignKey(eObjects, tableB);
        final List columnsA = ForeignKeyAssociationProvider.getColumns(eObjects, tableA);
        final List columnsB = ForeignKeyAssociationProvider.getColumns(eObjects, tableB);

        ForeignKeyAssociationDescriptor tmpDescr = null;
        List tmp = new ArrayList(11);

        // Ignore selected columns when both a PK and FK are selected
        boolean columnsSelected = (!columnsA.isEmpty() && !columnsB.isEmpty());
        if ((fkA != null && pkB != null && columnsSelected) || (fkB != null && pkA != null && columnsSelected)) {
            List modifiedList = new ArrayList(eObjects);
            modifiedList.remove(columnsA);
            modifiedList.remove(columnsB);
            tmpDescr = new ForeignKeyAssociationDescriptor(modifiedList);
            final String msg = RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_status_5"); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, msg, null);
            tmpDescr.setOverwritePkRef(false);
            tmpDescr.setText(RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_text_5")); //$NON-NLS-1$
            tmp.add(tmpDescr);
        }

        // Reset PK reference on selected FK
        if ((pkA != null && fkB != null && fkB.getUniqueKey() != null && fkB.getUniqueKey() != pkA)
            || (pkB != null && fkA != null && fkA.getUniqueKey() != null && fkA.getUniqueKey() != pkB)) {
            tmpDescr = new ForeignKeyAssociationDescriptor(eObjects);
            final String msg = RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_status_1"); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, msg, null);
            tmpDescr.setOverwritePkRef(true);
            tmpDescr.setText(RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_text_1")); //$NON-NLS-1$
            tmp.add(tmpDescr);
        }

        // Ignore selected columns in table containing PK
        if ((pkA != null && !columnsA.isEmpty()) || (pkB != null && !columnsB.isEmpty())) {
            List modifiedList = new ArrayList(eObjects);
            if (pkA != null && !columnsA.isEmpty()) {
                modifiedList.removeAll(columnsA);
            } else {
                modifiedList.removeAll(columnsB);
            }
            tmpDescr = new ForeignKeyAssociationDescriptor(modifiedList);
            final String msg = RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_status_2"); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, msg, null);
            tmpDescr.setOverwritePkRef(false);
            tmpDescr.setText(RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_text_2")); //$NON-NLS-1$
            tmp.add(tmpDescr);
        }

        // Ignore selected columns when is also FK selected
        if ((fkA != null && !columnsA.isEmpty()) || (fkB != null && !columnsB.isEmpty())) {
            List modifiedList = new ArrayList(eObjects);
            if (fkA != null && !columnsA.isEmpty()) {
                modifiedList.removeAll(columnsA);
            } else {
                modifiedList.removeAll(columnsB);
            }
            tmpDescr = new ForeignKeyAssociationDescriptor(modifiedList);
            final String msg = RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_status_3"); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, msg, null);
            tmpDescr.setOverwritePkRef(false);
            tmpDescr.setText(RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_text_3")); //$NON-NLS-1$
            tmp.add(tmpDescr);
        }

        // Ignore selected FK when columns are also selected
        if ((fkA != null && !columnsA.isEmpty()) || (fkB != null && !columnsB.isEmpty())) {
            List modifiedList = new ArrayList(eObjects);
            if (fkA != null && !columnsA.isEmpty()) {
                modifiedList.remove(fkA);
            } else {
                modifiedList.remove(fkB);
            }
            tmpDescr = new ForeignKeyAssociationDescriptor(modifiedList);
            final String msg = RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_status_4"); //$NON-NLS-1$
            tmpDescr.updateStatus(IStatus.OK, -1, msg, null);
            tmpDescr.setOverwritePkRef(false);
            tmpDescr.setText(RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Ambiguous_foreign_key_relationship_text_4")); //$NON-NLS-1$
            tmp.add(tmpDescr);
        }

        // If any situations were found that would be considered ambiguous ...
        final List result = new ArrayList(1);
        if (!tmp.isEmpty()) {
            // Create a ForeignKeyAssociationDescriptor composed of all the possible
            // ways to create the association
            ForeignKeyAssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(eObjects);
            ForeignKeyAssociationDescriptor firstDescr = (ForeignKeyAssociationDescriptor)tmp.get(0);
            final String msg = RelationalPlugin.Util.getString("ForeignKeyAssociationProvider.Default_foreign_key_relationship_constructor_1") + firstDescr.getText(); //$NON-NLS-1$
            descriptor.updateStatus(IStatus.WARNING, -1, msg, null);
            descriptor.setAmbiguous(true);

            // Add all descriptors
            for (Iterator iter = tmp.iterator(); iter.hasNext();) {
                AssociationDescriptor ad = (AssociationDescriptor)iter.next();
                descriptor.addDescriptor(ad);
            }
            result.add(descriptor);
        }
        // Else create the one ForeignKeyAssociationDescriptor that can create
        // the association from the clearly defined list of selected objects
        else {
            ForeignKeyAssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(eObjects);
            descriptor.setAmbiguous(false);
            result.add(descriptor);
        }

        return result;
    }

    /**
     * MyDefect : 13663 added logic to eliminate data access associations
     * 
     * @param obj
     * @return
     * @since 4.3
     */
    private static boolean isDataAccessAssociation( Object obj ) {

        if (!(obj instanceof EObject)) {
            return true;
        }

        EClass eClass = ((EObject)obj).eClass();
        RelationalPackage relationalPackage = RelationalPackage.eINSTANCE;
        if (eClass != relationalPackage.getBaseTable() && eClass != relationalPackage.getPrimaryKey()
            && eClass != relationalPackage.getForeignKey() && eClass != relationalPackage.getColumn()) {
            return true;
        }

        return false;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * Return true if the list of objects are allowable RelationalEntity instances that represent valid EObjects to be used in the
     * creation of a foreign key relationship.
     */
    static boolean containsValidObjects( final List eObjects,
                                         final Class[] validClasses ) {
        ArgCheck.isNotNull(eObjects);
        ArgCheck.isNotNull(validClasses);

        for (Iterator iter = eObjects.iterator(); iter.hasNext();) {

            final Object obj = iter.next();

            // Return false if the object is null
            if (obj == null) {
                return false;
            }

            // MyDefect : 13663 added logic to eliminate data access associations
            if (isDataAccessAssociation(obj)) {
                return false;
            }

            // Return false if the object is not in the relational model
            if (!(obj instanceof RelationalEntity)) {
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

    /**
     * Gather all Table instances either implicitly or explicitly defined in the list of objects. The list may contain instances
     * of Table, Column, PrimaryKey, and ForeignKey.
     * 
     * @return
     */
    static List getTables( final List eObjects ) {
        ArgCheck.isNotNull(eObjects);
        final List result = new ArrayList(eObjects.size());
        for (Iterator iter = eObjects.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            Table table = null;
            if (obj instanceof Table) {
                table = (Table)obj;
            } else if (obj instanceof Column && ((Column)obj).eContainer() != null) {
                Object container = ((Column)obj).eContainer();
                if (container instanceof Table) {
                    table = (Table)container;
                }
            } else if (obj instanceof PrimaryKey && ((PrimaryKey)obj).eContainer() != null) {
                table = (Table)((PrimaryKey)obj).eContainer();
            } else if (obj instanceof ForeignKey && ((ForeignKey)obj).eContainer() != null) {
                table = (Table)((ForeignKey)obj).eContainer();
            }
            if (table != null && table instanceof BaseTable && !result.contains(table)) {
                result.add(table);
            }
        }
        return result;
    }

    /**
     * Gather all Column instances from the selection list of objects that are contained by the specified Table
     * 
     * @return
     */
    static List getColumns( final List eObjects,
                            final Table container ) {
        ArgCheck.isNotNull(eObjects);
        ArgCheck.isNotNull(container);
        final List result = new ArrayList(eObjects.size());
        for (Iterator iter = eObjects.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            if (obj instanceof Column && ((Column)obj).eContainer() == container) {
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * Gather all ForeignKey instances from the selection list of objects that are contained by the specified Table
     * 
     * @return
     */
    static List getForeignKeys( final List eObjects,
                                final Table container ) {
        ArgCheck.isNotNull(eObjects);
        ArgCheck.isNotNull(container);
        final List result = new ArrayList(eObjects.size());
        for (Iterator iter = eObjects.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            if (obj instanceof ForeignKey && ((ForeignKey)obj).eContainer() == container) {
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * Return the first ForeignKey instance from the selection list of objects that is contained by the specified Table
     * 
     * @return
     */
    static ForeignKey getForeignKey( final List eObjects,
                                     final Table container ) {
        ArgCheck.isNotNull(eObjects);
        ArgCheck.isNotNull(container);
        for (Iterator iter = eObjects.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            if (obj instanceof ForeignKey && ((ForeignKey)obj).eContainer() == container) {
                return (ForeignKey)obj;
            }
        }
        return null;
    }

    /**
     * Return the PrimaryKey instance from the selection list of objects that is contained by the specified Table
     * 
     * @return
     */
    static PrimaryKey getPrimaryKey( final List eObjects,
                                     final Table container ) {
        ArgCheck.isNotNull(eObjects);
        ArgCheck.isNotNull(container);
        for (Iterator iter = eObjects.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            if (obj instanceof PrimaryKey && ((PrimaryKey)obj).eContainer() == container) {
                return (PrimaryKey)obj;
            }
        }
        return null;
    }

    static boolean isAmbiguous( final List eObjects ) {
        final List tables = ForeignKeyAssociationProvider.getTables(eObjects);
        final Table tableA = (Table)tables.get(0);
        final Table tableB = (Table)tables.get(1);
        final PrimaryKey pkA = ForeignKeyAssociationProvider.getPrimaryKey(eObjects, tableA);
        final PrimaryKey pkB = ForeignKeyAssociationProvider.getPrimaryKey(eObjects, tableB);
        final ForeignKey fkA = ForeignKeyAssociationProvider.getForeignKey(eObjects, tableA);
        final ForeignKey fkB = ForeignKeyAssociationProvider.getForeignKey(eObjects, tableB);
        final List columnsA = ForeignKeyAssociationProvider.getColumns(eObjects, tableA);
        final List columnsB = ForeignKeyAssociationProvider.getColumns(eObjects, tableB);

        if (pkA != null && fkB != null && fkB.getUniqueKey() != pkA) {
            return true;
        }
        if (pkB != null && fkA != null && fkA.getUniqueKey() != pkB) {
            return true;
        }
        if (pkA != null && !columnsA.isEmpty()) {
            return true;
        }
        if (pkB != null && !columnsB.isEmpty()) {
            return true;
        }

        return false;
    }

}
