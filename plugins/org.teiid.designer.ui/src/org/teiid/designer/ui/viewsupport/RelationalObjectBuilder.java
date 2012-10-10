/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.metamodels.core.CoreFactory;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMapping;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMappingImpl;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.UiConstants;


/**
 * @since 8.0
 */
public class RelationalObjectBuilder implements UiConstants {
    // ============================================================================================================================
    // Static Constants
    private static final String SLASH = "/"; //$NON-NLS-1$

    public static final String RELATIONAL_PACKAGE_URI = RelationalPackage.eNS_URI;
    public static final RelationalFactory factory = RelationalFactory.eINSTANCE;

    public static final String BASE_TABLE_STRING = "TABLE"; //$NON-NLS-1$
    public static final String VIEW_STRING = "VIEW"; //$NON-NLS-1$
    public static final String INDEX_STRING = "INDEX"; //$NON-NLS-1$
    public static final String COLUMN_STRING = "COLUMN"; //$NON-NLS-1$
    public static final String UNIQUE_KEY_STRING = "UNIQUEKEY"; //$NON-NLS-1$
    public static final String PRIMARY_KEY_STRING = "PRIMARYKEY"; //$NON-NLS-1$
    public static final String SCHEMA_STRING = "SCHEMA"; //$NON-NLS-1$
    public static final String CATALOG_STRING = "CATALOG"; //$NON-NLS-1$

    private final Resource resource;
    private final MyRelationalModelProcessor processor;

    public static boolean HEADLESS = false;// Flag to use for JUnit testing

    // ============================================================================================================================
    private Object[] datatypesArray = null;

    /**
     * @since 4.2
     */
    public RelationalObjectBuilder( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);
        this.resource = resource;
        if (!HEADLESS) {
            // Can't utilize this entity when UnitTesting as it utilizes the DTMgr
            processor = new MyRelationalModelProcessor(RelationalFactory.eINSTANCE, RelationalTypeMappingImpl.getInstance());
        } else {
            processor = null;
        }
    }

    public String getRelationalPackageURI() {
        return RELATIONAL_PACKAGE_URI;
    }

    public EObject createColumn( final String name,
                                 final Object location,
                                 final String description,
                                 final XSDSimpleTypeDefinition builtInType,
                                 final XSDSimpleTypeDefinition actualType ) {
        CoreArgCheck.isNotNull(name);
        CoreArgCheck.isNotNull(location);

        Column col = factory.createColumn();
        col.setName(name);

        int actualLength = 255;

        if (builtInType != null && actualType != null) {
            EList validFacets = actualType.getValidFacets();
            boolean lengthDone = false;
            if (validFacets.contains("length") && actualType.getLengthFacet() != null) {//$NON-NLS-1$
                actualLength = actualType.getLengthFacet().getValue();
                lengthDone = true;
            }

            if (!lengthDone && validFacets.contains("maxLength") && actualType.getMaxLengthFacet() != null) {//$NON-NLS-1$
                actualLength = actualType.getMaxLengthFacet().getValue();
            }

            if (validFacets.contains("fractionDigits") && actualType.getFractionDigitsFacet() != null) {//$NON-NLS-1$
                col.setPrecision(actualType.getFractionDigitsFacet().getValue());
            }

            col.setType(builtInType);
            col.setLength(actualLength);
        }

        // Set Description
        createAnnotation(col, description);

        return col;
    }

    public void createTransformation( EObject baseTable,
                                      String selectSql ) {
        if (HEADLESS) {
            // Don't do anything with the transformations when Unit Testing
            return;
        }

        TransformationHelper.createTransformation(baseTable, selectSql);
        // TransformationDiagramUtil.createTransformationDiagram(baseTable,resource, true);
    }

    public String getTypeName( EObject column ) {
        if (column instanceof Column) {
            EObject type = ((Column)column).getType();
            if (type instanceof XSDSimpleTypeDefinition) {
                return ((XSDSimpleTypeDefinition)type).getName();
            }
        }

        return new String();
    }

    public EObject createBaseTable( final String name,
                                    final Object location,
                                    final boolean supportsUpdate,
                                    final String description ) {
        CoreArgCheck.isNotNull(name);
        CoreArgCheck.isNotNull(location);

        // Create and Set Name
        BaseTable bt = factory.createBaseTable();
        bt.setName(name);
        bt.setSupportsUpdate(supportsUpdate);

        // add table to container
        if (location instanceof Resource) {
            ((Resource)location).getContents().add(bt);
        } else if (location instanceof Schema) {
            ((Schema)location).getTables().add(bt);
        } else if (location instanceof Catalog) {
            ((Catalog)location).getTables().add(bt);
        }

        // Set Description

        createAnnotation(bt, description);

        return bt;
    }

    public void addColumns( final Object table,
                            final Collection columns ) {
        if (table instanceof Table) {
            ((Table)table).getColumns().addAll(columns);
        }
    }

    public void createXPathNIS( final EObject table,
                                final XSDConcreteComponent xsdComp ) {
        CoreArgCheck.isNotNull(xsdComp);
        CoreArgCheck.isNotNull(table);

        if (!(table instanceof BaseTable)) {
            return;
        }

        XSDElementDeclaration element = null;
        EObject owner = xsdComp;
        boolean done = false;
        while (!done && owner != null) {
            if (owner instanceof XSDElementDeclaration) {
                element = (XSDElementDeclaration)xsdComp;
                done = true;
            } else {
                owner = owner.eContainer();
            }
        }

        if (element == null) {
            return;
        }

        final Stack elements = new Stack();
        owner = element;
        while (owner != null) {
            if (owner instanceof XSDElementDeclaration) {
                elements.push(owner);
            }

            owner = owner.eContainer();
        }

        final StringBuffer xpath = new StringBuffer();
        while (!elements.isEmpty()) {
            xpath.append(SLASH);
            final XSDElementDeclaration next = (XSDElementDeclaration)elements.pop();
            xpath.append(next.getName());
        }

        ((BaseTable)table).setNameInSource(xpath.toString());
    }

    public void createColXPathNIS( final EObject col,
                                   final Stack elementStack ) {
        CoreArgCheck.isNotNull(col);
        CoreArgCheck.isNotNull(elementStack);

        if (!(col instanceof Column)) {
            return;
        }

        final Stack copy = (Stack)elementStack.clone();
        final Stack topDownStack = new Stack();
        while (!copy.isEmpty()) {
            topDownStack.push(copy.pop());
        }

        final StringBuffer nis = new StringBuffer();
        while (!topDownStack.isEmpty()) {
            nis.append(SLASH);
            final XSDElementDeclaration next = (XSDElementDeclaration)topDownStack.pop();
            nis.append(next.getName());
        }

        ((Column)col).setNameInSource(nis.toString());
    }

    public EObject createView( final String name,
                               final Object location,
                               final boolean supportsUpdate,
                               final String description ) {
        CoreArgCheck.isNotNull(name);
        CoreArgCheck.isNotNull(location);

        // Create and Set Name
        View view = factory.createView();
        view.setName(name);
        view.setSupportsUpdate(supportsUpdate);

        // add table to container
        if (location instanceof Resource) {
            ((Resource)location).getContents().add(view);
        } else if (location instanceof Schema) {
            ((Schema)location).getTables().add(view);
        } else if (location instanceof Catalog) {
            ((Catalog)location).getTables().add(view);
        }

        // Set Description

        createAnnotation(view, description);

        return view;
    }

    private void createAnnotation( EObject eObject,
                                   String description ) {
        if (description != null && description.trim().length() > 0) {
            AnnotationContainer annotations = null;
            final Iterator contents = this.resource.getContents().iterator();
            while (contents.hasNext()) {
                final Object next = contents.next();
                if (next instanceof AnnotationContainer) {
                    annotations = (AnnotationContainer)next;
                }
            } // while

            if (annotations == null) {
                annotations = CoreFactory.eINSTANCE.createAnnotationContainer();
                this.resource.getContents().add(annotations);
            }

            Annotation annotation = annotations.findAnnotation(eObject);
            if (annotation == null) {
                annotation = CoreFactory.eINSTANCE.createAnnotation();
                annotations.getAnnotations().add(annotation);
                annotation.setAnnotatedObject(eObject);
            }

            annotation.setDescription(description);

        }
    }

    public EObject getDatatype( String datatype ) {
        if (HEADLESS) {
            // Can't utilize DTMgr when running headless
            return null;
        }

        if (datatypesArray == null) {
            try {
                datatypesArray = ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes();
            } catch (ModelerCoreException e) {
                Util.log(e);
            }
        }
        if (datatypesArray != null) {
            String dtName = null;
            for (int i = 0; i < datatypesArray.length; i++) {
                dtName = ModelerCore.getWorkspaceDatatypeManager().getName((EObject)datatypesArray[i]);
                if (dtName != null && dtName.equals(datatype)) return (EObject)datatypesArray[i];
            }
        }
        List problems = new ArrayList();

        EObject dType = processor.findType(datatype, problems);
        if (dType != null) {
            return dType;
        }
        return null;
    }

    class MyRelationalModelProcessor extends RelationalModelProcessorImpl {
        private static final String VARCHAR2_TYPE_NAME = "VARCHAR2"; //$NON-NLS-1$
        private static final String NVARCHAR2_TYPE_NAME = "NVARCHAR2"; //$NON-NLS-1$
        private static final String TIMESTAMP_TYPE_NAME = "TIMESTAMP("; //$NON-NLS-1$

        /**
         * Construct an instance of OracleModelProcessor.
         */
        public MyRelationalModelProcessor() {
            super();
        }

        /**
         * Construct an instance of OracleModelProcessor.
         * 
         * @param factory
         */
        public MyRelationalModelProcessor( final RelationalFactory factory ) {
            super(factory);
        }

        /**
         * Construct an instance of OracleModelProcessor.
         * 
         * @param factory
         */
        public MyRelationalModelProcessor( final RelationalFactory factory,
                                           final RelationalTypeMapping mapping ) {
            super(factory, mapping);
            setDatatypeManager(ModelerCore.getWorkspaceDatatypeManager());
        }

        /**
         * Find the type given the supplied information. This method is called by the various <code>create*</code> methods, and is
         * currently implemented to use {@link #findType(int, int, List)} when a numeric type and {@link #findType(String, List)}
         * (by name) for other types.
         * 
         * @param type
         * @param typeName
         * @return
         */
        @Override
        protected EObject findType( final int jdbcType,
                                    final String typeName,
                                    final int length,
                                    final int precision,
                                    final int scale,
                                    final List problems ) {

            EObject result = null;

            // Oracle 9i introduced the "timestamp" type name (with type=1111, or OTHER)
            if (typeName.startsWith(TIMESTAMP_TYPE_NAME)) {
                result = findBuiltinType(DatatypeConstants.BuiltInNames.TIMESTAMP, problems);
            }
            if (result != null) {
                return result;
            }

            return super.findType(jdbcType, typeName, length, precision, scale, problems);
        }

        /**
         * Overrides the method to find a type simply by name. This method converts some Oracle-specific (non-numeric) types to
         * standard names, and then simply delegates to the superclass. Find the datatype by name.
         * 
         * @param jdbcTypeName the name of the JDBC (or DBMS) type
         * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
         * @return the datatype that is able to represent data with the supplied criteria, or null if no datatype could be found
         * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#findType(java.lang.String,
         *      java.util.List)
         */
        @Override
        protected EObject findType( final String jdbcTypeName,
                                    final List problems ) {
            String standardName = jdbcTypeName;
            if (VARCHAR2_TYPE_NAME.equalsIgnoreCase(jdbcTypeName) || NVARCHAR2_TYPE_NAME.equalsIgnoreCase(jdbcTypeName)) {
                standardName = RelationalTypeMapping.SQL_TYPE_NAMES.VARCHAR;
            }
            return super.findType(standardName, problems);
        }

        @Override
        protected boolean isFixedLength( final int type,
                                         final String typeName ) {
            if (NVARCHAR2_TYPE_NAME.equalsIgnoreCase(typeName)) {
                return false;
            }
            return super.isFixedLength(type, typeName);
        }
    }

}
