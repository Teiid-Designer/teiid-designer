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

package com.metamatrix.metamodels.relational.aspects.validation;

import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.aspects.validation.rules.AccessPatternColumnsRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.ColumnDatatypeRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.ColumnIntegerDatatypeRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.ColumnNativeTypeRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.ForeignKeyColumnsRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.IndexReferenceOneTableRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.MissingColumnLengthRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.MissingColumnPrecisionRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.ProcedureParametersRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.RelationalStringNameRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.SiblingNameInSourceRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.TableChildrenUpdatabilityRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.TableMaterializedRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.TableUniqueKeysRule;
import com.metamatrix.metamodels.relational.aspects.validation.rules.UniqueKeyColumnsRule;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.core.validation.rules.StringLengthRule;

/**
 * RelationalEntityAspect
 */
public abstract class RelationalEntityAspect extends AbstractValidationAspect {

	public static final RelationalStringNameRule NAME_RULE = new RelationalStringNameRule(RelationalPackage.RELATIONAL_ENTITY__NAME);
	public static final ValidationRule LENGTH_RULE = new StringLengthRule(RelationalPackage.RELATIONAL_ENTITY__NAME);
	public static final ValidationRule TABLE_UPDATABILITY_RULE = new TableChildrenUpdatabilityRule();
	public static final ValidationRule MISSING_COLUMN_LENGTH_RULE = new MissingColumnLengthRule();
    public static final ValidationRule MISSING_COLUMN_PRECISION_RULE = new MissingColumnPrecisionRule();
	public static final ValidationRule SIBLING_NAME_IN_SOURCE_RULE = new SiblingNameInSourceRule();
	public static final ValidationRule COLUMN_INTEGER_DATATYPE_RULE = new ColumnIntegerDatatypeRule();
	public static final ValidationRule FOREIGN_KEY_COLUMNS_RULE = new ForeignKeyColumnsRule();
	public static final ValidationRule UNIQUE_KEY_COLUMNS_RULE = new UniqueKeyColumnsRule();
	public static final ValidationRule ACCESS_PTTN_COLUMNS_RULE = new AccessPatternColumnsRule();
	public static final ValidationRule TABLE_UNIQUE_KEYS_RULE = new TableUniqueKeysRule();
    public static final ValidationRule COLUMN_DATATYPE_RULE = new ColumnDatatypeRule(RelationalPackage.COLUMN__TYPE);
    public static final ValidationRule TABLE_MATERIALIZED_RULE = new TableMaterializedRule(RelationalPackage.TABLE__MATERIALIZED);
	public static final ValidationRule PROC_PARAM_RULE = new ProcedureParametersRule();
    public static final ValidationRule INDEX_REF_MULTIPLE_TABLES = new IndexReferenceOneTableRule();
    public static final ValidationRule EMPTY_COLUMN_NATIVE_TYPE_RULE = new ColumnNativeTypeRule();

    protected RelationalEntityAspect(final MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for relational entity.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NAME_RULE);
		addRule(LENGTH_RULE);
        addRule(SIBLING_NAME_IN_SOURCE_RULE);
		return super.getValidationRules();		
	}
}
