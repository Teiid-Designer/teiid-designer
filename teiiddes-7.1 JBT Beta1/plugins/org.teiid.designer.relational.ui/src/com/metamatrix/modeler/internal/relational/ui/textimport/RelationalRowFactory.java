/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relational.ui.textimport;

import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.util.RelationalTypeMappingImpl;
import com.metamatrix.modeler.tools.textimport.ui.wizards.IRowObject;

/**
 * @since 4.2
 */
public class RelationalRowFactory {
    private static final String NUMBER = "NUMBER"; //$NON-NLS-1$
    public static final int UNKNOWN = -1;
    
    public static final int CATALOG = 2;
    public static final int SCHEMA = 3;
    public static final int BASE_TABLE = 10;
    public static final int VIEW = 11;
    public static final int INDEX = 12;
    public static final int COLUMN = 13;
    public static final int UNIQUE_KEY = 14;
    public static final int PRIMARY_KEY = 15;

    public static final String BASE_TABLE_STRING = "TABLE"; //$NON-NLS-1$
    public static final String VIEW_STRING = "VIEW"; //$NON-NLS-1$
    public static final String INDEX_STRING = "INDEX"; //$NON-NLS-1$
    public static final String COLUMN_STRING = "COLUMN"; //$NON-NLS-1$
    public static final String UNIQUE_KEY_STRING = "UNIQUEKEY"; //$NON-NLS-1$
    public static final String PRIMARY_KEY_STRING = "PRIMARYKEY"; //$NON-NLS-1$
    public static final String SCHEMA_STRING = "SCHEMA"; //$NON-NLS-1$
    public static final String CATALOG_STRING = "CATALOG"; //$NON-NLS-1$

    private static final char COMMA = IRowObject.COMMA;
    private static final AllRelationalModelProcessor processor = new AllRelationalModelProcessor(
                                                                                                 RelationalFactory.eINSTANCE,
                                                                                                 RelationalTypeMappingImpl.getInstance());

    /**
     * @since 4.2
     */
    public RelationalRowFactory() {
        super();
    }

    public IRowObject createRowObject( String rawString ) {
        IRowObject newRowObject = null;

        // Given a row object, let's create a row for it.
        // Let's parse the initial string and check object type.
        int commaIndex = rawString.indexOf(COMMA); // Name should not have double quotes

        if (commaIndex == -1 || commaIndex >= rawString.length()) return null;

        String typeString = rawString.substring(0, commaIndex).trim();
        String restOfRow = rawString.substring(commaIndex + 1);

        int objectType = getObjectType(typeString);

        switch (objectType) {
            case CATALOG: {
                newRowObject = new CatalogRowObject(restOfRow);
            }
                break;
            case SCHEMA: {
                newRowObject = new SchemaRowObject(restOfRow);
            }
                break;
            case BASE_TABLE: {
                newRowObject = new BaseTableRowObject(restOfRow);
            }
                break;
            case INDEX: {
                newRowObject = new IndexRowObject(restOfRow);
            }
                break;
            case COLUMN: {
                newRowObject = new ColumnRowObject(restOfRow);
            }
        }

        if (newRowObject != null) {
            newRowObject.setRawString(rawString);
            newRowObject.parseRow();
        }

        return newRowObject;
    }

    private int getObjectType( String type ) {
        if (type.equals(BASE_TABLE_STRING)) {
            return BASE_TABLE;
        } else if (type.equals(VIEW_STRING)) {
            return VIEW;
        } else if (type.equals(COLUMN_STRING)) {
            return COLUMN;
        } else if (type.equals(INDEX_STRING)) {
            return INDEX;
        } else if (type.equals(UNIQUE_KEY_STRING)) {
            return UNIQUE_KEY;
        } else if (type.equals(PRIMARY_KEY_STRING)) {
            return PRIMARY_KEY;
        } else if (type.equals(SCHEMA_STRING)) {
            return SCHEMA;
        } else if (type.equals(CATALOG_STRING)) {
            return CATALOG;
        }

        return UNKNOWN;
    }

    public static EObject getDataType( final String someType,
                                       final int length,
                                       final int precision,
                                       final int scale ) {
        EObject bidt = null;
        final List problems = new LinkedList();
        if (someType.equalsIgnoreCase(NUMBER)) bidt = processor.findType(Types.NUMERIC,
                                                                         someType,
                                                                         length,
                                                                         precision,
                                                                         scale,
                                                                         problems);
        else bidt = processor.findType(someType, problems);

        return bidt;

    }

}
