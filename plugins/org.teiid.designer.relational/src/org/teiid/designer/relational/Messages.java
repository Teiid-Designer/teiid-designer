/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	public static String datatypeProcessor_error_while_computing_datatype;
	public static String datatypeProcessor_error_finding_datatatype;
	public static String relationalModelFactory_unknown_object_type_0_cannot_be_processed;
	public static String relationalModelFactory_error_finding_table_named;
	public static String relationalModelFactory_error_adding_desciption_to_0;
	public static String relationalModelFactory_creatingModelChildren;
	public static String relationalModelFactory_creatingForeigneKeys;
	public static String relationalModelFactory_creatingIndexes;
	public static String relationalModelFactory_replacingModelObject;
	public static String relationalModelFactory_creatingModelChild;
	
	public static String validationOkCreateObject;
	public static String validate_error_nameCannotBeNullOrEmpty;
	public static String validate_error_pkNoColumnsDefined;
	public static String validate_error_fkNoColumnsDefined;
	public static String validate_error_ucNoColumnsDefined;
	public static String validate_error_materializedTableHasNoTableDefined;
	public static String validate_warning_noColumnsDefined;
	public static String validate_error_fKUniqueKeyNameIsUndefined;
	public static String validate_error_fKReferencedUniqueKeyTableIsUndefined;
	public static String validate_error_duplicateColumnNamesInTable;
	public static String validate_error_duplicateParameterNamesInProcedure;
	public static String validate_warning_noParametersDefined;

    static {
        NLS.initializeMessages("org.teiid.designer.relational.messages", Messages.class); //$NON-NLS-1$
    }
}

