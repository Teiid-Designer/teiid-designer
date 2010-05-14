/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import com.metamatrix.core.selection.TreeSelection;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.metamodels.uml2.util.PrimitiveTypeManager;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder;
import com.metamatrix.modeler.modelgenerator.processor.RelationTracker;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2ModelGeneratorPlugin;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;
import com.metamatrix.modeler.modelgenerator.util.AnnotationHelper;
import com.metamatrix.modeler.modelgenerator.util.AnnotationHelperException;
import com.metamatrix.modeler.modelgenerator.util.EObjectUtil;
import com.metamatrix.modeler.modelgenerator.util.SimpleDatatypeUtil;

/**
 * RelationalObjectGeneratorImpl
 */
public class RelationalObjectGeneratorImpl implements RelationalObjectGenerator {

    public static final int CANNOT_FIND_SIMPLE_DATATYPE_ERROR_CODE = 90001;
    public static final int UNABLE_TO_CLONE_OBJECTS = 90002;
    public static final int UNABLE_TO_CREATE_ANNOTATION = 90003;
    public static final int UNABLE_TO_PROCESS_ANNOTATION = 90004;
    public static final int UNSUPPORTED_PROPERTY_TYPE_ERROR_CODE = 90005;
    private final static String STRING_TYPE_NAME = "string"; //$NON-NLS-1$

    private final RelationTracker tracker;
    private final DatatypeFinder datatypeFinder;
    private final Uml2RelationalOptions options;
    private final RelationalObjectNamingStrategy namingStrategy;
    private final EObjectUtil eObjectUtil;
    private final RelationalFactory factory;
    private final TreeSelection inputSelection;
    private final SimpleDatatypeUtil datatypeUtil;
    private final AnnotationHelper annotationHelper;
    private final Map columnCustomPropsMap;
    private final Map tableCustomPropsMap;

    /**
     * Construct an instance of RelationalObjectGeneratorImpl.
     */
    public RelationalObjectGeneratorImpl( final RelationTracker tracker,
                                          final DatatypeFinder finder,
                                          final Uml2RelationalOptions options,
                                          final RelationalObjectNamingStrategy namingStrategy,
                                          final EObjectUtil eObjectUtil,
                                          final RelationalFactory factory,
                                          final TreeSelection inputSelection,
                                          final SimpleDatatypeUtil datatypeUtil,
                                          final AnnotationHelper annotationHelper ) {

        super();

        this.tracker = tracker;
        this.datatypeFinder = finder;
        this.options = options;
        this.columnCustomPropsMap = options.getColumnCustomPropsMap();
        this.tableCustomPropsMap = options.getTableCustomPropsMap();

        this.namingStrategy = namingStrategy;
        this.inputSelection = inputSelection;
        this.eObjectUtil = eObjectUtil;
        this.factory = factory == null ? RelationalFactory.eINSTANCE : factory;
        this.datatypeUtil = datatypeUtil;
        this.annotationHelper = annotationHelper;
    }

    public List createBaseTablesForClass( final Classifier klass,
                                          final List problems,
                                          final Set associationsToBeProcessed ) {
        final List tables = new LinkedList();

        if (shouldClassifierBeProcessed(klass)) {

            /*
             * if this class has no Properties to process, then we do not
             * process it at all. There is no corresponding Relational entity
             * that we can create to represent a Class with no Properties.
             */
            if (hasOwnedInheritedOrNestedProperties(klass)) {

                final String name = namingStrategy.getNameForClassBaseTable(klass);
                final BaseTable table = factory.createBaseTable();

                // Apply custom properties, if any are defined
                applyCustomPropertiesToTable(table);

                table.setName(name);

                tables.add(table);
                /*
                 * we create the Primary Key first so that we can relate this
                 * table via the Primary Key to any tables that must be spawned
                 * as a result of 'relationships' to other tables, or Properties
                 * with multplicity >1 that are owned by this table.
                 */
                createPrimaryKeyForTable(table, klass, problems);

                /*
                 * we must record this 'additional table' immediately due to the
                 * fact that UML models may contain 'circular' type references.
                 * For example: Class A has a Property of type Class B, Class B
                 * has a property of Class A. If we dont record the fact that
                 * this table has been processed immediately after a Primary key
                 * has been created for it, we will get a recursive situation
                 * where Class A will be processed, which will cause Class B to
                 * be processed, etc.
                 */
                tracker.recordGeneratedFrom(klass, tables, problems);

                final List ancillaryTables = processPropertiesForClassifier(table,
                                                                            klass,
                                                                            associationsToBeProcessed,
                                                                            CoreStringUtil.Constants.EMPTY_STRING,
                                                                            problems);

                tables.addAll(ancillaryTables);

                addAnnotationForElement(klass, table, problems);

            }
        }
        return tables;
    }

    protected List createBaseTablesFromDatatype( final Property referringProperty,
                                                 final DataType datatype,
                                                 final List problems,
                                                 final Set associationsToBeProcessed ) {

        final List tables = new LinkedList();

        /*
         * we still process a 'null' datatype instance so that we will have a
         * place to hold values of the property. We just wont have any
         * information about the 'type' of the column necessary to hold the
         * values of the property.
         *
         *
         * We must also check to see if this datatype is considered to
         * be a primitive 'built-in' Datatype.  If so, then it is by default 'processed'.  This
         * is a fix for defect 14928.
         */
        if (datatype == null || PrimitiveTypeManager.INSTANCE.getAllPrimitiveTypes().contains(datatype)
            || shouldClassifierBeProcessed(datatype)) {

            final String name = namingStrategy.getNameForDatatypeTable(referringProperty, datatype);
            final BaseTable table = factory.createBaseTable();

            // Apply custom properties, if any are defined
            applyCustomPropertiesToTable(table);

            table.setName(name);

            tables.add(table);

            final Column datatypeValueColumn = factory.createColumn();
            datatypeValueColumn.setName(namingStrategy.getNameForDatatypeValueColumn(referringProperty,
                                                                                     datatype,
                                                                                     CoreStringUtil.Constants.EMPTY_STRING));

            // Apply column custom properties, if any defined
            applyCustomPropertiesToColumn(datatypeValueColumn);

            table.getColumns().add(datatypeValueColumn);

            /*
             * try to find a simple datatype that represents this Datatype
             * construct in the input UML model by its name. If we dont find
             * one, we move up its generalization hierarchy and see if any of
             * its ancestors have a matching simple datatype.
             */
            final EObject type = getSimpleDatatypeForDataType(datatype, problems);

            datatypeValueColumn.setType(type);

            /*
             * this will take care of setting up the remaining property values
             * for the column based on the property it represents. This can only
             * be called AFTER the type is set on the column.
             */
            setupColumnRepresentingProperty(datatypeValueColumn, referringProperty, problems);

            if (datatype != null) {
                /*
                 * this means that the datatype that we are to process of the
                 * referring Property is not null. If the datatype is Null, then
                 * there is no reason to try to process any properties of a null
                 * Datatype instance.
                 */

                tables.addAll(processPropertiesForClassifier(table,
                                                             datatype,
                                                             associationsToBeProcessed,
                                                             CoreStringUtil.Constants.EMPTY_STRING,
                                                             problems));
            }
            addAnnotationForElement(referringProperty, table, problems);

        }
        return tables;

    }

    public List createBaseTablesForAssociation( final Association association,
                                                final List problems ) {
        List returnTables = new LinkedList();

        /*
         * first we check to make sure that relational entities for both ends of
         * the Assocation are available in the output model so that we can
         * relate them. we know that either both members are not owned by the
         * association or both members are owned by the Association (otherwise
         * it would have already been processed), so we simply get the Member
         * Properties.
         */
        final List members = association.getMemberEnds();

        if (members.size() != 2) {
            final String message = Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectGeneratorImpl.Unable_to_process_the_Association_1"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.WARNING, Uml2ModelGeneratorPlugin.PLUGIN_ID, UNABLE_TO_PROCESS_ANNOTATION,
                                              message, null);
            problems.add(status);
        } else {

            final EObject end1 = (EObject)members.get(0);
            final EObject end2 = (EObject)members.get(1);

            if (end1.eClass().getClassifierID() == UMLPackage.PROPERTY && end2.eClass().getClassifierID() == UMLPackage.PROPERTY) {
                final Property property1 = (Property)end1;
                final Property property2 = (Property)end2;

                final Type type1 = property1.getType();
                final Type type2 = property2.getType();

                // boolean isCompAggr1 = false;
                // boolean isCompAggr2 = false;
                // AggregationKind aKind1 = property1.getAggregation();
                // AggregationKind aKind2 = property2.getAggregation();
                // if (aKind1.equals(AggregationKind.COMPOSITE_LITERAL)) {
                // isCompAggr1 = true;
                // }
                // if (aKind2.equals(AggregationKind.COMPOSITE_LITERAL)) {
                // isCompAggr2 = true;
                // }

                /*
                 * this will check to see that the 'type' of the ends of the
                 * Association have been created in the output already. If they
                 * have not, then they may have not been in the selection to be
                 * generated from or may not have been generated for other
                 * reasons. In that case, we ignore this Association.
                 */
                final BaseTable outputTable1 = (BaseTable)tracker.getGeneratedTo(type1);
                final BaseTable outputTable2 = (BaseTable)tracker.getGeneratedTo(type2);

                if (outputTable1 != null && outputTable2 != null) {
                    String fkName1 = namingStrategy.getNameForForeignKey(property1, outputTable1);
                    String fkName2 = namingStrategy.getNameForForeignKey(property2, outputTable2);

                    returnTables.add(createIntersectTable(fkName1,
                                                          fkName2,
                                                          outputTable1,
                                                          outputTable2,
                                                          namingStrategy.getNameForIntersectTableRepresentingAssociation(association),
                                                          problems));

                }
            }

        }

        return returnTables;

    }

    /**
     * <p>
     * This method will create a Primary Key instance given the input Class and BaseTable instances. The Class instance must be
     * the source object that was used ot create the passed in BaseTable instance. The Class instance will be interrogated for
     * patterns indicating whether or not the columns for a PK in a relational situation can be created from existing attributes
     * on the Table.
     * </p>
     * <p>
     * There are several options that affect the creation of a Primary Key by this method.
     * </p>
     * <p>
     * The 'artificial key' options affect the generation of an artificial key. The boolean 'createArtificialKeys' option is the
     * one that will control whether or not artificial keys will be generated at all. An artificial key is a key made up of
     * columns that are generated and put on the target table. The other artificial key options affect the type, name and number
     * of columns that will be created in creating the artificial key.
     * </p>
     * <p>
     * The algorithm for creating a Primary key is as follows:
     * </p>
     * <p>
     * If there are 'Properties' of the passed in Class which have the stereotype name that equalsIgnoreCase() the passsed in
     * pkStereoTypeName, then the Columns that were generated from those Properties will be the members of the generated Primary
     * Key.
     * </p>
     * <p>
     * If no Properties are found that have a stereotype name that matches the passed in stereotype name, and the
     * createArtificialKeys parameter is 'true' then numberOfArtificialKeyColumns number of columns will be created in the target
     * table, the artificialKeyType will be set as the type of those columns and the artificialKeyColumnBasename will be postfixed
     * with an integer starting at 1 and incremented for each Column generated for the artificial Primary Key and this will be the
     * name of the columns.
     * </p>
     * <p>
     * If no Properties are found that have a stereotype name that matches the passed in stereotype name, and the
     * createArtificialKeys parameter is 'false' then no Primary Key will be generated for the table.
     * </p>
     * <p>
     * If a Primary Key is generated for the passed in table, it will be 'set' on that table so that no further manipulation of
     * the input BaseTable or return PrimaryKey is necessary. Also, if artificial key columns are generated, then they will be
     * added to the input BaseTable's Columns Collection.
     * </p>
     * 
     * @param table the input BaseTable to create the Primary Key for
     * @param klass The input Class instance from which the 'table' parameter was created.
     * @param tracker This is an instance of RelationTracker that has been used to keep track of all of the relationships between
     *        the UML and Relational objects for the entire Model Generation process so far. There must exist mappings in this
     *        RelationTracker object between the Source Properties(Features) of the passed in Class and the Output Columns in the
     *        passed in Relational Base Table.
     * @return the Primary Key that was generated from this process or null if no Primary Key was generated.
     */
    protected PrimaryKey createPrimaryKeyForTable( final BaseTable table,
                                                   final Classifier klass,
                                                   final List problems ) {
        PrimaryKey key = null;
        if (table != null && klass != null) {
            final List pkFeatures = getPrimaryKeyProperties(klass);
            if (pkFeatures.size() > 0) {
                key = factory.createPrimaryKey();
                key.setName(namingStrategy.getNameForPrimaryKey(klass, table));
                final Iterator iter = pkFeatures.iterator();
                while (iter.hasNext()) {
                    final Property property = (Property)iter.next();
                    final Column column = factory.createColumn();
                    column.setName(namingStrategy.getNameForColumn(property));
                    /*
                     * we know that the type for the Property is an instance of
                     * a DataType because the getPrimaryKeyProperties() method
                     * checks to be sure that it is.
                     */
                    column.setType(getSimpleDatatypeForDataType((DataType)property.getType(), problems));

                    /*
                     * this will take care of setting up the remaining property
                     * values for the column based on the property it
                     * represents. This can only be called AFTER the type is set
                     * on the column.
                     */
                    setupColumnRepresentingProperty(column, property, problems);

                    // Apply column custom properties, if any defined
                    applyCustomPropertiesToColumn(column);

                    key.getColumns().add(column);
                    table.getColumns().add(column);
                    tracker.recordGeneratedFrom(property, column, problems);

                }
                key.setTable(table);
            } else {

                createArtificialPrimaryKeyInTable(table);
            }
        }
        return key;
    }

    /**
     * <p>
     * This method can be used to determine if there are features of the input klass parameter which have been 'earmarked' as
     * primary key candidates for relational mapping. This is done through specifying a stereotype for the feature that has a name
     * that equals (ignoring case) the input parameter pkStereotypeName. Note that the returned features will be in the order that
     * they were specified in the klass.
     * </p>
     * <p>
     * If a feature of the class has the correct stereotype name and has a multiplicity with an upper bound >1, it will be ignored
     * and will not be included in the List of primary key candidates.
     * </p>
     * <p>
     * An empty List will be returned if no features are found on the klass that have a matching stereotype moniker.
     * </p>
     * 
     * @param klass the class whose features will be looked at for Primary Key candidates
     * @param pkStereotypeName the string (case insensitive) name of the stereotype that indicates that this feature is a Primary
     *        Key candidate.
     * @return A List of Feature Objects that are determined to be Primary Key candidates. May be empty if no matching Features
     *         are found on the klass input parameter. Will not be null.
     */
    protected List getPrimaryKeyProperties( final Classifier klass ) {
        final List properties = getPropertyEObjectsForClass(klass);
        final Iterator iter = properties.iterator();
        final List pkProperties = new ArrayList();
        while (iter.hasNext()) {
            final Property property = (Property)iter.next();
            /*
             * we cant include a Property whose multiplicity upper bound is >1.
             * This is because such a Property will be 'normalized' out of the
             * BaseTable and another table will be created that will hold the
             * values of that property. Thus that property cannot be involved in
             * the Primary Key of this BaseTable. In addition, the determined
             * 'type' of a Primary key column cannot be anything other than a
             * Simple Datatype (primitive or derived from primitive)
             */
            if (property.getUpper() == 1) {
                final Type type = property.getType();
                if (type != null && property.getType() instanceof PrimitiveType) {
                    final EList stereotypes = property.getApplicableStereotypes();
                    if (checkStereotypesAgainstStrings(stereotypes, options.getPrimaryKeyStereotypeNames())) {
                        pkProperties.add(property);
                    }

                }
            }

        }
        return pkProperties;
    }

    protected PrimaryKey createArtificialPrimaryKeyInTable( final BaseTable table ) {
        final PrimaryKey key = factory.createPrimaryKey();
        key.setName(namingStrategy.getNameForPrimaryKey(null, table));
        for (int i = 0; i < options.getNumberOfKeyColumns(); ++i) {
            final Column column = factory.createColumn();
            column.setName(namingStrategy.getNameForArtificialPKColumn(i + 1));
            EObject keyColType = options.getTypeOfKeyColumns();
            column.setType(keyColType);
            // If type is string, set the length
            if (keyColType != null) {
                String typeName = ModelerCore.getDatatypeManager(table, true).getName(keyColType);
                if (typeName != null && typeName.equals(STRING_TYPE_NAME)) {
                    int keyLength = options.getKeyColumnLength();
                    column.setLength(keyLength);
                }
            }
            column.setNullable(NullableType.NO_NULLS_LITERAL);
            table.getColumns().add(column);
            key.getColumns().add(column);
        }
        key.setTable(table);
        return key;
    }

    protected List processPropertiesForClassifier( final BaseTable table,
                                                   final Classifier classifier,
                                                   final Set associationsToBeProcessed,
                                                   final String nameSuffix,
                                                   final List problems ) {

        final List returnTables = new LinkedList();

        final Set membersToProcess = getMembersToProcessForClassifier(classifier);
        /*
         * iterate through the members of the class and process them into the
         * appropriate Relational Objects.
         */
        final Iterator iter = membersToProcess.iterator();
        while (iter.hasNext()) {
            final EObject object = (EObject)iter.next();
            if (object.eClass().getClassifierID() == UMLPackage.PROPERTY) {
                final Property property = (Property)object;

                /*
                 * check to see if there are special reasons why this property should not be processed for this
                 * class
                 */

                if (shouldPropertyBeProcessedForClass(classifier, property)) {

                    /*
                     * if a property has an association that has no owned
                     * members that means that the navigability of the
                     * association is bi-directional. In our case this must be
                     * handled in a special manner to avoid 'double processing'
                     * the association. This is because we process 'owned'
                     * members of the Classes we process. In the case where
                     * navigability is bi-directional the class on both ends of
                     * the Association 'owns' the Property that represents the
                     * assocation end. If we processed each class from each end,
                     * then we would process the relationship between the two
                     * classes twice, resulting in incorrect Relational Object
                     * output.
                     */
                    final Association association = property.getAssociation();
                    if (association != null && association.getOwnedMembers().size() == 0) {
                        associationsToBeProcessed.add(association);
                        /*
                         * no further processing is required for this property
                         * we will process this type of Association after all of
                         * the output relational objects have been created.
                         */
                    } else {
                        final Type type = property.getType();

                        /*
                         * we use instanceof here instead of using the
                         * Classifier ID in order to catch the subclasses of
                         * these UML construct types.
                         */
                        if (type instanceof Class || type instanceof Interface) {

                            final Classifier typeClass = (Classifier)type;
                            if (property.getUpper() <= 1 && property.getUpper() != LiteralUnlimitedNatural.UNLIMITED) {
                                processClassTypePropertyWMultOf1(table,
                                                                 property,
                                                                 typeClass,
                                                                 associationsToBeProcessed,
                                                                 returnTables,
                                                                 nameSuffix,
                                                                 problems);
                            } else {
                                processClassTypePropertyWMultStar(table,
                                                                  property,
                                                                  typeClass,
                                                                  associationsToBeProcessed,
                                                                  returnTables,
                                                                  nameSuffix,
                                                                  problems);
                            }
                        } else if (type == null || type instanceof DataType) {

                            final DataType typeDatatype = (DataType)type;
                            if (property.getUpper() <= 1 && property.getUpper() != LiteralUnlimitedNatural.UNLIMITED) {
                                processDatatypeTypePropertyWMultOf1(table,
                                                                    property,
                                                                    typeDatatype,
                                                                    associationsToBeProcessed,
                                                                    returnTables,
                                                                    nameSuffix,
                                                                    problems);
                            } else {
                                processDatatypeTypePropertyWMultStar(table,
                                                                     property,
                                                                     typeDatatype,
                                                                     associationsToBeProcessed,
                                                                     returnTables,
                                                                     nameSuffix,
                                                                     problems);
                            }

                        } else {

                            // add the unsupported type to the problems list
                            String className = CoreStringUtil.getLastToken(type.getClass().getName(), "."); //$NON-NLS-1$
                            String[] params = new String[] {className,
                                property.getNamespace().getName() + '.' + property.getName()};
                            final String message = Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectGeneratorImpl.UnsupportedPropertyType", (Object[])params); //$NON-NLS-1$
                            final IStatus status = new Status(IStatus.WARNING, Uml2ModelGeneratorPlugin.PLUGIN_ID,
                                                              UNSUPPORTED_PROPERTY_TYPE_ERROR_CODE, message, null);
                            problems.add(status);
                        }

                    }

                }
            }
        }
        return returnTables;
    }

    private void processDatatypeTypePropertyWMultStar( final BaseTable table,
                                                       final Property property,
                                                       final DataType typeDatatype,
                                                       final Set associationsToBeProcessed,
                                                       final List returnTables,
                                                       final String nameSuffix,
                                                       final List problems ) {

        final List tables = createBaseTablesFromDatatype(property, typeDatatype, problems, associationsToBeProcessed);
        returnTables.addAll(tables);

        if (tables != null && tables.size() > 0) {
            /*
             * the first table in the return List is always the table that is
             * most directly related to the input Datatype.
             */
            final BaseTable datatypeTable = (BaseTable)tables.get(0);
            // boolean isCompAggr = false;
            // AggregationKind aKind = property.getAggregation();
            // if (aKind.equals(AggregationKind.COMPOSITE_LITERAL)) {
            // isCompAggr = true;
            // }
            createForeignKeyToTable(namingStrategy.getNameForForeignKey(table), table, datatypeTable, problems);
            tracker.recordGeneratedFrom(property, datatypeTable, problems);
        }

    }

    private void processDatatypeTypePropertyWMultOf1( final BaseTable table,
                                                      final Property property,
                                                      final DataType typeDatatype,
                                                      final Set associationsToBeProcessed,
                                                      final List returnTables,
                                                      String nameSuffix,
                                                      final List problems ) {

        final Column typeColumn = factory.createColumn();
        typeColumn.setName(namingStrategy.getNameForDatatypeValueColumn(property, typeDatatype, nameSuffix));

        final EObject colType = getSimpleDatatypeForDataType(typeDatatype, problems);
        typeColumn.setType(colType);

        /*
         * this will take care of setting up the remaining property values for
         * the column based on the property it represents. This can only be
         * called AFTER the type is set on the column.
         */
        setupColumnRepresentingProperty(typeColumn, property, problems);

        // Apply column custom properties, if any defined
        applyCustomPropertiesToColumn(typeColumn);

        table.getColumns().add(typeColumn);

        tracker.recordGeneratedFrom(property, typeColumn, problems);

        /*
         * In this case we take the parent table and we process the type
         * Datatype instance for the property as if the parent table 'owns' the
         * properties of the type Datatype itself. Any ancillary tables that are
         * spawned from this process are also added to the List of tables that
         * represent this Class.
         *
         * We also add a name suffix to indicate that any
         * columns that are added to this table in support of this property that are related
         * to the datatype of the property are suffixed with the property name.  If the property does
         * not have a name, but is the end of an association, then we use the name of the association
         * as a differentiator.
         */
        String differentiatorSuffix = property.getName();

        if (CoreStringUtil.isEmpty(differentiatorSuffix)) {
            final Association association = property.getAssociation();
            if (association != null) {
                differentiatorSuffix = association.getName();
                if (CoreStringUtil.isEmpty(differentiatorSuffix)) {
                    differentiatorSuffix = property.getType().getName();
                }
            }
        }

        nameSuffix = nameSuffix + "_" + differentiatorSuffix; //$NON-NLS-1$

        returnTables.addAll(processPropertiesForClassifier(table, typeDatatype, associationsToBeProcessed, nameSuffix, problems));

    }

    private void processClassTypePropertyWMultStar( final BaseTable table,
                                                    final Property property,
                                                    final Classifier typeClass,
                                                    final Set associationsToBeProcessed,
                                                    final List returnTables,
                                                    final String nameSuffix,
                                                    final List problems ) {

        BaseTable typeClassTable = null;

        // boolean isCompAggr1 = false;
        // boolean isCompAggr2 = false;
        // AggregationKind aKind = property.getAggregation();
        // if (aKind.equals(AggregationKind.COMPOSITE_LITERAL)) {
        // isCompAggr1 = true;
        // isCompAggr2 = true;
        //
        // }
        /*
         * here we must check to see if the type of this Property instance is
         * recursive. Meaning that its type is the same Class as the Class that
         * owns the property. If so, then we just go ahead and copy the PK again
         * and add a recursive FK to the table.
         */
        if (!typeClass.equals(property.getOwner())) {

            /*
             * here we check to see if the type Class of the Property we are
             * processing has already been created in the Relational ouput, if
             * so, we use that output object (will be a BaseTable). If not, we
             * process the Class.
             */

            final EObject output = tracker.getGeneratedFrom(typeClass);
            if (output != null) {
                typeClassTable = (BaseTable)output;
            } else {
                final List tables = createBaseTablesForClass(typeClass, problems, associationsToBeProcessed);
                /*
                 * ( The first Object in the List will be the table that
                 * represents the type Class.
                 */
                if (tables.size() > 0) {
                    returnTables.addAll(tables);
                    typeClassTable = (BaseTable)tables.get(0);
                }
            }
        } else {
            typeClassTable = table;
        }

        /*
         * if we tried to look up a relational table for the type Class AND we
         * tried to process it to get a Relational table representign this type
         * class, and got nothing, then we give up trying to relate the parent
         * table of this Property to the type Class for the Property.
         */
        if (typeClassTable != null) {
            final Association association = property.getAssociation();
            BaseTable intersectTable = null;
            if (property.getAssociation() == null) {

                intersectTable = createIntersectTable(namingStrategy.getNameForForeignKey(table),
                                                      namingStrategy.getNameForForeignKey(typeClassTable),
                                                      table,
                                                      typeClassTable,
                                                      namingStrategy.getNameForUnidirectionalIntersectTable(table,
                                                                                                            typeClassTable,
                                                                                                            property),
                                                      problems);

            } else {

                List propEnds = association.getMemberEnds();
                /*
                 * this should 'ALWAYS' be the case, unless the Association is
                 * not a valid association between two ends.
                 */
                if (propEnds.size() == 2) {
                    final Property end1 = (Property)propEnds.get(0);
                    final Property end2 = (Property)propEnds.get(1);

                    /*
                     * we must determine which end of the association is the
                     * navigable one.
                     */
                    if (end1 == property) {
                        intersectTable = createIntersectTable(namingStrategy.getNameForForeignKey(end1, table),
                                                              namingStrategy.getNameForForeignKey(end2, typeClassTable),
                                                              table,
                                                              typeClassTable,
                                                              namingStrategy.getNameForUnidirectionalIntersectTable(table,
                                                                                                                    typeClassTable,
                                                                                                                    property),
                                                              problems);
                    } else {
                        intersectTable = createIntersectTable(namingStrategy.getNameForForeignKey(end2, table),
                                                              namingStrategy.getNameForForeignKey(end1, typeClassTable),
                                                              table,
                                                              typeClassTable,
                                                              namingStrategy.getNameForUnidirectionalIntersectTable(table,
                                                                                                                    typeClassTable,
                                                                                                                    property),
                                                              problems);
                    }

                } else {
                    /*
                     * this association is not a valid association anyway, so we
                     * name the FK's like there is no association involved.
                     */
                    intersectTable = createIntersectTable(namingStrategy.getNameForForeignKey(table),
                                                          namingStrategy.getNameForForeignKey(typeClassTable),
                                                          table,
                                                          typeClassTable,
                                                          namingStrategy.getNameForUnidirectionalIntersectTable(table,
                                                                                                                typeClassTable,
                                                                                                                property),
                                                          problems);

                }

                returnTables.add(intersectTable);
            }
        } else {

            /*
             * In this case we just create a new table with a column with the default relational datatype
             * as specified in the generator wizard as a place to put the property value.  We then create a foreign key
             * to that table.  This is a fix for defect 14928 whereby no relational output at all was being generated
             * for the property in this situation.
             */
            BaseTable propertyTable = createBaseTableForProperty(property, problems);
            createForeignKeyToTable(namingStrategy.getNameForForeignKey(property, table), propertyTable, table, problems);
            returnTables.add(propertyTable);
        }

    }

    private BaseTable createBaseTableForProperty( final Property property,
                                                  final List problems ) {

        BaseTable table = factory.createBaseTable();
        table.setName(property.getName());
        createArtificialPrimaryKeyInTable(table);
        addDefaultTypeColumnToTableForProperty(table, property, problems);
        return table;

    }

    private void processClassTypePropertyWMultOf1( final BaseTable table,
                                                   final Property property,
                                                   final Classifier typeClass,
                                                   final Set associationsToBeProcessed,
                                                   final List returnTables,
                                                   final String nameSuffix,
                                                   final List problems ) {

        BaseTable typeClassTable = null;

        // boolean isCompAggr = false;
        // AggregationKind aKind = property.getAggregation();
        // if (aKind.equals(AggregationKind.COMPOSITE_LITERAL)) {
        // isCompAggr = true;
        // }
        /*
         * here we must check to see if the type of this Property instance is
         * recursive. Meaning that its type is the same Class as the Class that
         * owns the property. If so, then we just go ahead and copy the PK again
         * and add a recursive FK to the table.
         */
        if (!typeClass.equals(property.getOwner())) {

            /*
             * here we check to see if the type Class of the Property we are
             * processing has already been created in the Relational ouput, if
             * so, we use that output object (will be a BaseTable). If not, we
             * process the Class.
             */

            final EObject output = tracker.getGeneratedTo(typeClass);
            if (output != null) {
                typeClassTable = (BaseTable)output;
            } else {
                final List tables = createBaseTablesForClass(typeClass, problems, associationsToBeProcessed);
                /*
                 * ( The first Object in the List will be the table that
                 * represents the type Class.
                 */
                if (tables.size() > 0) {
                    returnTables.addAll(tables);
                    typeClassTable = (BaseTable)tables.get(0);
                }
            }
        } else {
            typeClassTable = table;
        }

        /*
         * if we tried to look up a relational table for the type Class AND we
         * tried to process it to get a Relational table representign this type
         * class, and got nothing, then we give up trying to relate the parent
         * table of this Property to the type Class for the Property.
         */
        if (typeClassTable != null) {

            createForeignKeyToTable(namingStrategy.getNameForForeignKey(property, typeClassTable),
                                    typeClassTable,
                                    table,
                                    problems);

        } else {
            /*
             * In this case we just create a column with the default relational datatype
             * as specified in the generator wizard as a placeholder for the property value. This
             * is a fix for defect 14928 whereby no relational output at all was being generated
             * for the property in this situation.
             */
            addDefaultTypeColumnToTableForProperty(table, property, problems);

        }

    }

    private void addDefaultTypeColumnToTableForProperty( final BaseTable table,
                                                         final Property property,
                                                         final List problems ) {
        Column column = factory.createColumn();
        column.setType(options.getDefaultRelationalColumnType());
        column.setName(property.getName());
        setupColumnRepresentingProperty(column, property, problems);
        table.getColumns().add(column);

    }

    /**
     * This method will set up all of the properties of the input Column instance based on the state of the input Property
     * instance. The input Property instance should be the property that the Column instance is to represent in the output
     * Relational Model.
     * 
     * @param column
     * @param property
     * @return
     */
    private Column setupColumnRepresentingProperty( final Column column,
                                                    final Property property,
                                                    final List problems ) {

        /*
         * if the property name was mangled somehow to allow the name to be used
         * in the output relational model we set the name in source of the
         * column to be the original name.
         */
        if (property.getName() != null && !property.getName().equals(column.getName())) {
            column.setNameInSource(property.getName());
        }

        if (property.getLower() == 0) {
            column.setNullable(NullableType.NULLABLE_LITERAL);
        } else {
            column.setNullable(NullableType.NO_NULLS_LITERAL);
        }

        if (checkStereotypesAgainstStrings(property.getApplicableStereotypes(), options.getAutoincrementStereotypeNames())) {
            column.setAutoIncremented(true);
        }

        final EObject datatype = column.getType();
        if (datatype != null) {

            if (datatypeUtil.isSimpleDatatypeString(datatype)) {
                /*
                 * this means the datatype of the must be a character type
                 * datatype.
                 */
                column.setSearchability(SearchabilityType.SEARCHABLE_LITERAL);
                column.setSigned(false);
                column.setLength(options.getGeneratedStringTypeColumnDefaultLength());

            } else if (datatypeUtil.isSimpleDatatypeNumeric(datatype)) {
                column.setSearchability(SearchabilityType.ALL_EXCEPT_LIKE_LITERAL);
                column.setCaseSensitive(false);
            } else {
                column.setSearchability(SearchabilityType.UNSEARCHABLE_LITERAL);
                column.setCaseSensitive(false);
            }

            column.setDefaultValue(property.getDefault());
        }

        if (checkStereotypesAgainstStrings(property.getAppliedStereotypes(), options.getReadOnlyStereoTypeNames())
            || checkStereotypesAgainstStrings(property.getOwner().getAppliedStereotypes(), options.getReadOnlyStereoTypeNames())) {
            column.setUpdateable(false);
        } else {
            column.setUpdateable(true);
        }

        addAnnotationForElement(property, column, problems);

        return column;
    }

    /**
     * Apply the custom properties to the provided Column. The custom properties for columns are stored in the
     * columnCustomPropsMap.
     * 
     * @param column the Column to apply the custom properties to
     */
    private void applyCustomPropertiesToColumn( Column column ) {
        if (this.columnCustomPropsMap != null) {
            // Get features for the Column
            List features = column.eClass().getEAllStructuralFeatures();
            // Feature names for which there are values to set
            Set customPropKeys = this.columnCustomPropsMap.keySet();
            // Iterate the keyNames and set values for each
            Iterator iter = customPropKeys.iterator();
            while (iter.hasNext()) {
                // Get keyName
                String key = (String)iter.next();
                Object value = this.columnCustomPropsMap.get(key);
                // Look for existing feature
                EStructuralFeature feature = getFeature(features, key);
                // If existing feature, set default value
                if (feature != null) {
                    feature.setDefaultValue(value);
                    // If not existing feature, create new annotation tag
                } else {
                    try {
                        this.annotationHelper.setAnnotation(column, key, value);
                    } catch (AnnotationHelperException err) {
                    }
                }
            }
        }
    }

    /**
     * Apply the custom properties to the provided BaseTable. The custom properties for tables are stored in the
     * tableCustomPropsMap.
     * 
     * @param table the BaseTable to apply the custom properties to
     */
    private void applyCustomPropertiesToTable( BaseTable table ) {
        if (this.tableCustomPropsMap != null) {
            // Get features for the BaseTable
            List features = table.eClass().getEAllStructuralFeatures();
            // Feature names for which there are values to set
            Set customPropKeys = this.tableCustomPropsMap.keySet();
            // Iterate the keyNames and set values for each
            Iterator iter = customPropKeys.iterator();
            while (iter.hasNext()) {
                // Get keyName
                String key = (String)iter.next();
                Object value = this.tableCustomPropsMap.get(key);
                // Look for existing feature
                EStructuralFeature feature = getFeature(features, key);
                // If existing feature, set default value
                if (feature != null) {
                    feature.setDefaultValue(value);
                    // If not existing feature, create new annotation tag
                } else {
                    try {
                        this.annotationHelper.setAnnotation(table, key, value);
                    } catch (AnnotationHelperException err) {
                    }
                }
            }
        }
    }

    /**
     * Get a feature by name from a provided list of features.
     * 
     * @param features a List of features.
     * @param featureName the name of the feature to get from the List.
     * @return the feature with the provided name, 'null' if not found.
     */
    private EStructuralFeature getFeature( List features,
                                           String featureName ) {
        EStructuralFeature result = null;
        Iterator iter = features.iterator();
        while (iter.hasNext()) {
            EStructuralFeature feature = (EStructuralFeature)iter.next();
            if (featureName.equals(feature.getName())) {
                result = feature;
                break;
            }
        }
        return result;
    }

    private BaseTable createIntersectTable( String fkName1,
                                            String fkName2,
                                            final BaseTable table1,
                                            final BaseTable table2,
                                            final String intersectTableName,
                                            final List problems ) {
        final BaseTable intersectTable = factory.createBaseTable();
        intersectTable.setName(intersectTableName);
        if (fkName1 != null && fkName1.equalsIgnoreCase(fkName2)) {
            fkName1 = fkName1 + "_1"; //$NON-NLS-1$
            fkName2 = fkName2 + "_2"; //$NON-NLS-1$
        }
        createForeignKeyToTable(fkName1, table1, intersectTable, problems);

        createForeignKeyToTable(fkName2, table2, intersectTable, problems);
        return intersectTable;
    }

    /**
     * This method will clone the columns in the Primary Key of the toTable parameter and put them into the fromTable. It will
     * then create a Foreign Key in the fromTable that is made up of the copied columns. It will then 'hook up' the Foreign Key to
     * the toTable.
     * 
     * @param toTable
     * @param fromTable
     * @return null if an FK could not be created.
     */
    protected ForeignKey createForeignKeyToTable( final String keyName,
                                                  final BaseTable toTable,
                                                  final BaseTable fromTable,
                                                  final List problems ) {
        final ForeignKey key = factory.createForeignKey();
        final PrimaryKey toTableKey = toTable.getPrimaryKey();
        if (toTableKey != null) {
            final List columns = toTableKey.getColumns();

            final Iterator columniter = columns.iterator();
            for (int i = 1; columniter.hasNext(); ++i) {
                final Column column = (Column)columniter.next();
                Column newCol = null;
                try {
                    newCol = (Column)eObjectUtil.clone(column);
                } catch (ModelerCoreException e) {
                    final String[] params = new String[] {toTable.getName()};
                    final IStatus status = new Status(
                                                      IStatus.WARNING,
                                                      Uml2ModelGeneratorPlugin.PLUGIN_ID,
                                                      UNABLE_TO_CLONE_OBJECTS,
                                                      Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectGeneratorImpl.Unable_to_clone_an_EObject_to_create_an_FK_to_the_table_named__{0}._2", (Object[])params), e); //$NON-NLS-1$
                    problems.add(status);
                    return null;
                }
                newCol.setName(namingStrategy.getNameForCopiedPKColumn(toTable, i));
                key.getColumns().add(newCol);
                fromTable.getColumns().add(newCol);
            }

        } else {
            /*
             * if the toTable does not have a Primary key, then we create one.
             * This will only occur in a situation where the toTable represents
             * a Datatype entity, thus Datatype tables cannot have PK's
             * explicitly specified (doesnt make sense) so we simply create an
             * artificial key for the toTable to make the relationship.
             */
            createArtificialPrimaryKeyInTable(toTable);
            return createForeignKeyToTable(keyName, toTable, fromTable, problems);

        }
        key.setName(keyName);
        key.setTable(fromTable);
        key.setUniqueKey(toTableKey);
        // if (isCompositeAggregation) {
        // key.setCascadeDeletes(CascadeDeletesType.ALWAYS_LITERAL);
        // } else {
        // key.setCascadeDeletes(CascadeDeletesType.UNSPECIFIED_LITERAL);
        // }
        return key;
    }

    /**
     * This method will return ONLY the Property instances that are owned by the Class instance that is passed in.
     * 
     * @param klass the class to interrogate for Property type Features.
     * @return A List of {@link Property}instances that are found to be owned by the passed in Class instance. Will never be null,
     *         but may be empty.
     */
    protected List getPropertyEObjectsForClass( final Classifier klass ) {
        final List properties = new LinkedList();
        final Iterator iter = klass.getOwnedMembers().iterator();
        while (iter.hasNext()) {
            final EObject object = (EObject)iter.next();
            if (object.eClass().getClassifierID() == UMLPackage.PROPERTY) {
                properties.add(object);
            }

        }
        return properties;
    }

    protected boolean shouldClassifierBeProcessed( final Classifier classifier ) {

        if (classifier.isAbstract()) {
            return false;
        }

        /*
         * here we check to see if this Class instance already has a matching
         * object in the output.
         */
        if (classifier instanceof Stereotype) {
            return false;
        }
        if (hasUMLObjectBeenProcessed(classifier)) {
            return false;
        }

        /*
         * here is where we enforce the reachability constraints dicated by the
         * options for the generator If we ask whether or not this class should
         * be generated and it was not selected, and we are told to ignore
         * classifiers that are 'reachable' then we do not process this
         * classifier.
         *
         */
        if (inputSelection.getSelectionMode(classifier) != TreeSelection.SELECTED
            && options.getReachabilityConstraint() == Uml2RelationalOptions.IGNORE_REACHABLE_NOT_IN_SELECTION) {
            return false;
        }

        /*
         * here we check to see if the 'stereotype' of the class matches the
         * stereotypes specified in the generator options for Class sterotypes
         * to be excluded from the output.
         */
        final List stereotypes = options.getClassStereotypesToIgnore();
        final EList appliedStereotypes = classifier.getAppliedStereotypes();
        /*
         * if we find a match, then we return false
         */
        if (checkStereotypesAgainstStrings(appliedStereotypes, stereotypes)) {
            return false;
        }
        return true;
    }

    protected boolean hasUMLObjectBeenProcessed( final EObject object ) {
        if (tracker.getGeneratedTo(object) != null) {
            return true;
        }
        return false;
    }

    protected boolean hasOwnedInheritedOrNestedProperties( final Classifier klass ) {
        if (getMembersToProcessForClassifier(klass).size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * This method will interrogate the passed in Classifier for any members that should be processed. This method will look for
     * nested classes and will include the members of those classes recursively. It will also include inherited members in the
     * returned Collection. The members in the returned collection will include
     * 
     * @param classifier
     * @return
     */
    protected Set getMembersToProcessForClassifier( final Classifier classifier ) {

        if (classifier == null) {
            return Collections.EMPTY_SET;
        }

        final Set allMembers = new HashSet();
        final LinkedList ownedMembers = new LinkedList(classifier.getOwnedMembers());
        final LinkedList inheritedMembers = new LinkedList(classifier.getInheritedMembers());

        /*
         * this call will remove any properties that are redefined by other properties in the list of members
         * to be processed.
         */
        removeRedefinedProperties(ownedMembers, inheritedMembers);

        final Iterator iter = ownedMembers.iterator();
        while (iter.hasNext()) {
            final EObject object = (EObject)iter.next();
            if (object.eClass().getClassifierID() == UMLPackage.CLASS) {
                /*
                 * this means we have found a nested class, in this case, we get
                 * the 'members' for this nested class and add them to the
                 * members to be processed for the owning class.
                 */
                Class nestedClass = (Class)object;
                allMembers.addAll(getMembersToProcessForClassifier(nestedClass));
            } else {

                allMembers.add(object);
            }

        }

        /*
         * if the classifier is abstract and we are asked to provide the members
         * to be processed for it, we must traverse the subtypes of the
         * classifier and 'pull up' all owned members of the subtypes to be
         * processed as members of this root classifier.
         */
        if (classifier.isAbstract()) {
            allMembers.addAll(getOwnedMembersFromAllSubClasses(classifier));
        }

        allMembers.addAll(inheritedMembers);
        return allMembers;
    }

    /**
     * This method will remove all properties from both the owned members and inherited members lists that are passed into the
     * method. It will first remove all inherited properties which redefine
     * 
     * @param ownedMembers
     * @param inheritedMembers
     */
    protected void removeRedefinedProperties( LinkedList ownedMembers,
                                              LinkedList inheritedMembers ) {
        LinkedList allMembers = new LinkedList(ownedMembers);
        allMembers.addAll(inheritedMembers);
        Iterator iter = allMembers.iterator();
        while (iter.hasNext()) {
            Object object = iter.next();

            if (object instanceof Property) {
                Property property = (Property)object;
                List redefinedProperties = property.getRedefinedProperties();
                Iterator iter1 = redefinedProperties.iterator();
                while (iter1.hasNext()) {
                    Object obj = iter1.next();

                    if (inheritedMembers.contains(obj)) {
                        inheritedMembers.remove(obj);
                    } else if (ownedMembers.contains(obj)) {
                        ownedMembers.remove(obj);
                    }
                }
            }
        }
    }

    /**
     * This method will check the 'names' of the input stereotypes to see if they CONTAIN any of the strings in the input list of
     * strings. All comparisons will be done without regard to character case.
     * 
     * @param stereotypes the Stereotype objects whose names are to be compared to the input Strings.
     * @param strings the List of Strings to compare the Stereotype names against.
     * @return true if any matches are found, false if one is not. Also false if either of the input paremeter instance is null or
     *         empty.
     */
    protected boolean checkStereotypesAgainstStrings( final List stereotypes,
                                                      final List strings ) {
        if (stereotypes != null) {
            if (strings != null) {
                final Iterator iter = strings.iterator();
                while (iter.hasNext()) {
                    final String string = (String)iter.next();
                    final Iterator iterator = stereotypes.iterator();
                    while (iterator.hasNext()) {

                        final Object object = iterator.next();
                        if (object instanceof EObject) {
                            final EObject eObject = (EObject)object;
                            if (eObject.eClass().getClassifierID() == UMLPackage.STEREOTYPE) {
                                final Stereotype stereotype = (Stereotype)eObject;
                                /*
                                 * here we 'search' for the string in the
                                 * stereotype name in case there are several
                                 * stereotypes assigned which are delimited in
                                 * some manner.
                                 */
                                if (CoreStringUtil.indexOfIgnoreCase(stereotype.getName(), string) >= 0) {
                                    return true;
                                }
                            }
                        }
                    }
                }

            }

        }
        return false;
    }

    /**
     * This helper method is used to get a Simple Datatype EObject for the given DataType instance. The method will first check to
     * see if it can get a Simple Datatype by the name of the given DataType instance. If no Simple Datatype is found with that
     * name, the method will recursively go up the Generalization hierarchy of the Datatype looking for a Datatype in its hierachy
     * that we can get a Simple Datatype for. If we are unable to find a Simple Datatype EObject match anywhere in the hierarchy
     * of the DataType, we then return the default EObject Simple Datatype instance from the Processor Options.
     * 
     * @param type the DataType instance to look for a matching Simple Datatype instance for.
     * @param problems The List of Status instances indicating any problems that may have occurred durign processing.
     * @return The Simple Datatype EObject instance representing the passed in DataType instance. Will not be null.
     */
    protected EObject getSimpleDatatypeForDataType( final DataType type,
                                                    final List problems ) {

        if (problems == null) {
            throw new IllegalArgumentException(
                                               Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectGeneratorImpl.The_method_getSimpleDatatypeForDataType_cannot_process_a_Datatype_without_a_non-null_List_for_capturing_problems_encountered_during_processing.__Problems_parameter_instance_was_null._2")); //$NON-NLS-1$
        }

        EObject simpleType = null;
        if (type != null) {
            try {
                simpleType = datatypeFinder.findDatatype(type.getName());
            } catch (CoreException e) {
                String[] params = new String[] {};
                final String message = Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectGeneratorImpl.Unable_to_look_up_a_SimpleDatatype_using_the_instance_of_DatatypeFinder._1", (Object[])params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.WARNING, Uml2ModelGeneratorPlugin.PLUGIN_ID,
                                                  CANNOT_FIND_SIMPLE_DATATYPE_ERROR_CODE, message, e);
                problems.add(status);
            }

            if (simpleType == null) {
                final List generalizations = type.getGeneralizations();
                final Iterator iter = generalizations.iterator();
                while (iter.hasNext()) {
                    final Generalization generalization = (Generalization)iter.next();
                    final Classifier generalClassifier = generalization.getGeneral();
                    /*
                     * here we check to make sure that the 'type' is not the
                     * General end of the Generalization relationship
                     */
                    if (generalClassifier instanceof DataType) {
                        final DataType generalDataType = (DataType)generalClassifier;
                        simpleType = getSimpleDatatypeForDataType(generalDataType, problems);
                    }
                }
            }
        }

        if (simpleType == null) {
            simpleType = options.getDefaultRelationalColumnType();
        }

        return simpleType;
    }

    /**
     * This method will be used to add an Annotation to the output model for the given UMl2 Element instance.
     * 
     * @param inputObject the input UML2 Element instance to add an Annotation for.
     * @param outputObject the output Relational EObject to add an Annotation to.
     * @param problems the List of Statuses that indicate problems during processing.
     */
    private void addAnnotationForElement( final Element inputObject,
                                          final EObject outputObject,
                                          final List problems ) {

        final List comments = inputObject.getOwnedComments();
        final Iterator iter = comments.iterator();
        final StringBuffer description = new StringBuffer();
        while (iter.hasNext()) {
            final Comment comment = (Comment)iter.next();
            description.append(comment.getBody());

        }

        try {
            annotationHelper.createAnnotation(outputObject, description.toString());
        } catch (AnnotationHelperException e) {
            Object[] params;
            if (inputObject instanceof NamedElement) {
                final NamedElement namedElement = (NamedElement)inputObject;
                params = new Object[] {namedElement.getName()};
            } else {
                params = new Object[] {};
            }
            final String message = Uml2ModelGeneratorPlugin.Util.getString("RelationalObjectGeneratorImpl.Unable_to_create_an_Anno_1", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.WARNING, Uml2ModelGeneratorPlugin.PLUGIN_ID, UNABLE_TO_CREATE_ANNOTATION,
                                              message, e);
            problems.add(status);
        }

    }

    /**
     * @param classifier
     * @return
     */
    private Collection getOwnedMembersFromAllSubClasses( final Classifier classifier ) {

        // ############################################################################################
        // TODO implement this method to traverse the subtypes of the abstract
        // class and include those as
        // members to be processed.
        // ############################################################################################

        return Collections.EMPTY_LIST;
    }

    /**
     * This method is used to Check to see if a Property instance should be processed for its classifier. It checks to see if its
     * Multiplicity upper is >0, checks to see if there is already an output object created for this Property. If an output object
     * has already been created, it then checks to see if the Property is actually inherited, If it is inherited, then it checks
     * to see if the Property is part of the Primary Key. If it is not part of the Primary key for the input classifier, then the
     * property is processed. If it is not part of the primary key, then it has already been processed for the classifier, as
     * Primary key generation always occurs before processing other properties on the classifier.
     * 
     * @param klassifier the classifier that owns the Property in question
     * @param property The Property instance that needs to be determined whether it should be processed or not
     * @return true if the Property should be processed, false if not.
     */
    private boolean shouldPropertyBeProcessedForClass( Classifier klassifier,
                                                       Property property ) {
        /*
         * We also check to be sure the upper multiplicity of the property is
         * >0. If the multiplicity upper bound is 0, then the property cannot
         * have a value, and the generated table should not have a table to hold
         * a value that is not possible.
         */
        if (property != null && (property.getUpper() > 0 || property.getUpper() == LiteralUnlimitedNatural.UNLIMITED)) {

            /*
             * if the property has not previously been processed, then we go ahead and processed.  This check
             * ensures that there is no corresponding column in the output already that matches this input property.
             */
            if (tracker.getGeneratedTo(property) == null) {
                return true;
            }

            /*
             * here we check to see if this property is inherited or is owned,
             * and if it is inherited, whether or not it is part of the primary
             * key for this Class instance. If the Property is inherited, we
             * cannot check to see if a relational output column has been
             * generated for it in the conventional sense, because this will
             * commonly be the case for an inherited property. When the
             * superclass that owns the property is processed, an output column
             * will be generated for the property for that class. Thus if the
             * property is inherited, we check to see if the property is part of
             * the Primary key properties (in which case a
             */
            if (isPropertyInherited(klassifier, property)) {
                if (!getPrimaryKeyProperties(klassifier).contains(property)) {
                    return true;
                }
            }
        }

        return false;

    }

    /**
     * This method will return a boolean true if the given property is part of the inherited colleciton of properties that are
     * referenced by the given Class object. Will return false if the passed in Class is null.
     * 
     * @param klass The class to check whether the given Property is inherited for.
     * @param property the property to determine whether it is inherited from a superclass of the given Class
     * @return true if the Property is inherited on the given class from a superclass, false if the input Class is null, or if the
     *         property is not inherited, but is owned.
     */
    private boolean isPropertyInherited( Classifier klass,
                                         Property property ) {
        if (klass != null) {
            if (klass.getInheritedMembers().contains(property)) {
                return true;
            }
        }
        return false;
    }

}
