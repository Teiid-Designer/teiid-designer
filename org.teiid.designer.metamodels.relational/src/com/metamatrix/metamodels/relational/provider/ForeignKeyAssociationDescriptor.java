/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.provider;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;

/**
 * ForeignKeyAssociationDescriptor
 */
public class ForeignKeyAssociationDescriptor extends AbstractAssociationDescriptor {

    private static final String TYPE = "ForeignKeyAssociation"; //$NON-NLS-1$
    private static final String LABEL = RelationalPlugin.Util.getString("ForeignKeyAssociationDescriptor.Foreign_Key_Association_1"); //$NON-NLS-1$
    private static final String NEW = RelationalPlugin.Util.getString("ForeignKeyAssociationDescriptor.New_1"); //$NON-NLS-1$
    private boolean overwritePkRef;
    private String text;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of ForeignKeyAssociationDescriptor.
     * 
     * @param eObjects
     */
    public ForeignKeyAssociationDescriptor( List eObjects ) {
        super(eObjects);
        this.overwritePkRef = true;
        this.text = LABEL;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getType()
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#isComplete()
     */
    @Override
    public boolean isComplete() {
        final List eObjects = this.getEObjects();
        if (eObjects == null || eObjects.isEmpty()) {
            return false;
        }

        // Return false if the list contains invalid objects
        if (!ForeignKeyAssociationProvider.containsValidObjects(eObjects, ForeignKeyAssociationProvider.VALID_CLASSES_TYPES)) {
            return false;
        }

        // Return false if there are not two tables implicitly or explicitly defined in the list
        final List tables = ForeignKeyAssociationProvider.getTables(eObjects);
        if (tables.size() != 2) {
            return false;
        }

        // ----------------------------------------
        // Check the contents of the selection list
        // ----------------------------------------
        final Table tableA = (Table)tables.get(0);
        final Table tableB = (Table)tables.get(1);

        // Return false if there are two primary keys in the selection list
        final PrimaryKey pkA = ForeignKeyAssociationProvider.getPrimaryKey(eObjects, tableA);
        final PrimaryKey pkB = ForeignKeyAssociationProvider.getPrimaryKey(eObjects, tableB);
        if (pkA != null && pkB != null) {
            return false;
        }

        // Return false if there are two foreign keys in the selection list,
        // either in different tables or within the same table
        final List fKeysA = ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableA);
        final List fKeysB = ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableB);
        if (!fKeysA.isEmpty() && !fKeysB.isEmpty()) {
            return false;
        }
        if (fKeysA.size() > 1 || fKeysB.size() > 1) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getImage()
     */
    @Override
    public Object getImage() {
        return RelationalEditPlugin.INSTANCE.getImage("full/obj16/ForeignKey"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getText()
     */
    @Override
    public String getText() {
        return this.text;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor#canCreate()
     */
    @Override
    public boolean canCreate() { // NO_UCD
        final List eObjects = this.getEObjects();
        return ForeignKeyAssociationProvider.containsValidObjects(eObjects, ForeignKeyAssociationProvider.VALID_CLASSES_TYPES);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor#execute()
     */
    @Override
    public EObject create() {
        if (!isComplete()) {
            return null;
        }

        // Order the list of selected objects into a PrimaryKey -> ForeignKey order
        final List eObjects = this.getEObjects();
        final List selectedObjs = getOrderedObjects(eObjects);

        // Create the ForeignKey relationship ...
        final List tables = ForeignKeyAssociationProvider.getTables(selectedObjs);
        final BaseTable pkTable = (BaseTable)tables.get(0);
        final BaseTable fkTable = (BaseTable)tables.get(1);
        return createAssociation(pkTable, fkTable, selectedObjs, overwritePkRef);

    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /*
     * Return the ordered list of selected objects.  The ordered list is found by first
     * placing all entries associated with each table in their own list.  The next step
     * is to determine which of the two lists contains the primary key to be used as the
     * source for the assocation.  This list is placed at the top of the resulting
     * list while the foreign key portion is added to the bottom of the resulting list.
     */
    protected List getOrderedObjects( final List eObjects ) {

        final List tables = ForeignKeyAssociationProvider.getTables(eObjects);
        final BaseTable tableA = (BaseTable)tables.get(0);
        final BaseTable tableB = (BaseTable)tables.get(1);
        final PrimaryKey pkA = ForeignKeyAssociationProvider.getPrimaryKey(eObjects, tableA);
        final PrimaryKey pkB = ForeignKeyAssociationProvider.getPrimaryKey(eObjects, tableB);
        final List fKeysA = ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableA);
        final List fKeysB = ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableB);
        final List columnsA = ForeignKeyAssociationProvider.getColumns(eObjects, tableA);
        final List columnsB = ForeignKeyAssociationProvider.getColumns(eObjects, tableB);

        // If the selection list contains a PrimaryKey reference ....
        boolean sortFromAtoB = true;
        boolean orderIsSet = false;
        if (pkB != null) {
            sortFromAtoB = false;
            orderIsSet = true;
        }

        // If the selection list contains a ForeignKey reference ....
        if (!fKeysA.isEmpty() && !orderIsSet) {
            sortFromAtoB = false;
            orderIsSet = true;
        }

        // If one table already has a primary key and the other does not ...
        if (tableA.getPrimaryKey() == null && tableB.getPrimaryKey() != null && !orderIsSet) {
            sortFromAtoB = false;
            orderIsSet = true;
        }

        final List result = new ArrayList(eObjects.size() + 2);
        if (sortFromAtoB) {
            result.add(tableA);
            result.add(pkA);
            result.addAll(columnsA);
            result.addAll(fKeysA);
            result.add(tableB);
            result.add(pkB);
            result.addAll(columnsB);
            result.addAll(fKeysB);
        } else {
            result.add(tableB);
            result.add(pkB);
            result.addAll(columnsB);
            result.addAll(fKeysB);
            result.add(tableA);
            result.add(pkA);
            result.addAll(columnsA);
            result.addAll(fKeysA);
        }

        return result;
    }

    /**
     * Create the foreign key relationship between the defined
     * 
     * @param pkTable the table for the primary key end
     * @param fkTable the table for the foreign key end
     * @param selectedObjs the list of selected objects
     * @param resetPkRefOnFk if true, the primary key reference on any existing foreign key that participates in this association
     *        will be reset.
     * @return the ForeignKey instance
     * @throws ModelerCoreException
     */
    protected EObject createAssociation( final BaseTable pkTable,
                                         final BaseTable fkTable,
                                         final List selectedObjs,
                                         boolean resetPkRefOnFk ) {
        CoreArgCheck.isNotNull(pkTable);
        CoreArgCheck.isNotNull(fkTable);

        // -----------------------------------------------
        // Process the primary key part of the association
        // -----------------------------------------------

        // Get the columns from the selection list associated with the source table
        List pkColumns = ForeignKeyAssociationProvider.getColumns(selectedObjs, pkTable);
        PrimaryKey pk = pkTable.getPrimaryKey();

        // If there is no PrimaryKey under the source table then create one
        if (pk == null) {
            pk = RelationalFactory.eINSTANCE.createPrimaryKey();
            pk.setTable(pkTable);
            pk.setName(NEW + pk.eClass().getName());
        }

        // Add any columns for the source to the primary key
        if (!pkColumns.isEmpty() && pk.getColumns().isEmpty()) {
            pk.getColumns().addAll(pkColumns);
        }

        // ------------------------------------------
        // Process the target part of the association
        // ------------------------------------------

        // Get the columns and foreign keys from the selection list associated with the target table
        List fkColumns = ForeignKeyAssociationProvider.getColumns(selectedObjs, fkTable);
        List fkeys = ForeignKeyAssociationProvider.getForeignKeys(selectedObjs, fkTable);
        ForeignKey fk = null;

        // If there is no ForeignKey in the selection list then create one
        if (fkeys.isEmpty()) {
            fk = RelationalFactory.eINSTANCE.createForeignKey();
            fk.setTable(fkTable);
            fk.setUniqueKey(pk);
        }
        // There is a ForeignKey in the selection list
        else {
            fk = (ForeignKey)fkeys.get(0);
            if (resetPkRefOnFk) {
                fk.setUniqueKey(pk);
            }
        }

        // Add any columns for the target to the foreign key
        if (!fkColumns.isEmpty() && fk.getColumns().isEmpty()) {
            fk.getColumns().addAll(fkColumns);
        }

        return fk;

    }

    /**
     * @param string
     */
    protected void setText( String string ) {
        this.text = string;
    }

    /**
     * @param b
     */
    protected void setOverwritePkRef( boolean b ) {
        this.overwritePkRef = b;
    }

}
