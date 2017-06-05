/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.model;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.transformation.TransformationPlugin;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalPrimaryKey;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalUniqueConstraint;
import org.teiid.designer.relational.model.RelationalViewTable;

/**
 * This class provides a means to convert from either EMF to RelationalReference objects or visa-versa
 * @author blafond
 *
 */
public class RelationalModelConverter {
	ModelResource modelResource;
	RelationalModel model;
	
	public RelationalModelConverter() {
		super();
	}
	
	public RelationalModelConverter(ModelResource modelResource) {
		super();
		this.modelResource = modelResource;
	}
	
	public RelationalModelConverter(ModelResource modelResource, RelationalModel model) {
		this(modelResource);
		this.model = model;
	}

	public RelationalReference convert(EObject eObject, boolean toVirtual) {
		
		if( eObject instanceof BaseTable ) {
			return convertTable((BaseTable)eObject, toVirtual);
		}
		
		return null;
	}
	
	public RelationalModel convertModel(ModelResource modelResource) {
		RelationalModel model = new RelationalModel(ModelUtil.getName(modelResource));
		
		
		return model;
	}
	
	public RelationalTable convertTable(BaseTable table, boolean toVirtual) {
		RelationalTable ref;
		
		if( toVirtual ) {
			ref = new RelationalViewTable(table.getName());
		} else {
			ref = new RelationalTable(table.getName());
		}
        // Create and Set Name
        
        ref.setNameInSource(table.getNameInSource());
        ref.setSupportsUpdate(table.isSupportsUpdate());
        ref.setMaterialized(table.isMaterialized());
        ref.setSystem(table.isSystem());
        ref.setCardinality(table.getCardinality());
        setDescription(ref, table);
        
		List<Column> columns = table.getColumns();
		for( Column col : columns ) {
			RelationalColumn columnRef = convertColumn(col);
			ref.addColumn(columnRef);
		}
		
		setPrimaryKey(ref, table);
		
		setUniqueConstraints(ref, table);
        		
        return ref;
	}
	
	public RelationalColumn convertColumn(Column column) {
		RelationalColumn ref = new RelationalColumn(column.getName());
		
		ref.setNameInSource(column.getNameInSource());
		ref.setUpdateable(column.isUpdateable());
		ref.setAutoIncremented(column.isAutoIncremented());
		ref.setCaseSensitive(column.isCaseSensitive());
		ref.setCharacterSetName(column.getCharacterSetName());
		ref.setCollationName(column.getCollationName());
		ref.setCurrency(column.isCurrency());
		ref.setDefaultValue(column.getDefaultValue());
        ref.setDistinctValueCount(column.getDistinctValueCount());
        ref.setLengthFixed(column.isFixedLength());
        ref.setFormat(column.getFormat());
        ref.setMaximumValue(column.getMaximumValue());
        ref.setMinimumValue(column.getMinimumValue());
        ref.setNativeType(column.getNativeType());
        ref.setNullable(column.getNullable().getLiteral().toString());
        ref.setNullValueCount(column.getNullValueCount());
        ref.setPrecision(column.getPrecision());
        ref.setRadix(column.getRadix());
        ref.setScale(column.getScale());
        ref.setSearchability(column.getSearchability().getLiteral().toString());
        ref.setSelectable(column.isSelectable());
        ref.setSigned(column.isSigned());
        
        String dTypeName = ModelerCore.getModelEditor().getName(column.getType());
        ref.setDatatype(dTypeName);
        
        ref.setLength(column.getLength());
        
        setDescription(ref, column);
        
        return ref;
	}
	
	private String getDescription(EObject eObject) {
		String description = null;
		
		try {
			description = ModelerCore.getModelEditor().getDescription(eObject);
		} catch (ModelerCoreException ex) {
            String message = "Error finding description for object = "; //$NON-NLS-1$
            TransformationPlugin.Util.log(IStatus.ERROR, ex, message);
        }
		
		return description;
	}
	
	private String getName(EObject eObj) {
		return ModelerCore.getModelEditor().getName(eObj);
	}
	
	private void setDescription(RelationalReference ref, EObject eObj) {
        String description = getDescription(eObj);
        if( description != null ) {
        	ref.setDescription(description);
        }
	}
	
	private RelationalColumn findColumn(RelationalTable table, String name) {
		for( RelationalColumn col : table.getColumns() ) {
			if( col.getName().equals(name) ) {
				return col;
			}
		}
		
		return null;
	}
	
	private void setPrimaryKey(RelationalTable ref, BaseTable table) {
		PrimaryKey pk = table.getPrimaryKey();
		if( pk != null ) {
			RelationalPrimaryKey rpk = new RelationalPrimaryKey(getName(pk));
			for( Object col : pk.getColumns() ) {
				String cName = getName((EObject)col);
				RelationalColumn relCol = findColumn(ref, cName);
				if( relCol != null ) {
					rpk.addColumn(relCol);
				}
			}
		}
	}
	
	private void setUniqueConstraints(RelationalTable ref, BaseTable table) {
		for( UniqueConstraint uc : table.getUniqueConstraints() ) {
			RelationalUniqueConstraint ruc = new RelationalUniqueConstraint(getName(uc));
			for( Object col : uc.getColumns() ) {
				String cName = getName((EObject)col);
				RelationalColumn relCol = findColumn(ref, cName);
				if( relCol != null ) {
					ruc.addColumn(relCol);
				}
			}
		}
	}
}
