/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.sql;

import java.util.Collection;

import org.eclipse.datatools.connectivity.sqm.core.containment.AbstractContainmentProvider;
import org.eclipse.datatools.connectivity.sqm.internal.core.containment.GroupID;
import org.eclipse.datatools.modelbase.sql.schema.Catalog;
import org.eclipse.datatools.modelbase.sql.schema.SQLSchemaPackage;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class TeiidSchemaContainmentProvider extends AbstractContainmentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.sqm.core.containment.
	 * AbstractContainmentProvider
	 * #getContainedElements(org.eclipse.emf.ecore.EObject)
	 */
	@SuppressWarnings({ "unchecked" })
	public Collection getContainedElements(EObject obj) {
		Collection children = super.getContainedElements(obj);
		TeiidCatalogSchema schema = (TeiidCatalogSchema) obj;
		children.addAll(schema.getTables());
		children.addAll(schema.getRoutines());
		children.addAll(schema.getSequences());
		children.addAll(schema.getUserDefinedTypes());
		children.addAll(schema.getAssertions());
		children.addAll(schema.getCharSets());
		children.addAll(schema.getDocuments());
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.sqm.core.containment.
	 * AbstractContainmentProvider#getContainer(org.eclipse.emf.ecore.EObject)
	 */
	public EObject getContainer(EObject obj) {
		Catalog catalog = ((Schema) obj).getCatalog();
		if (catalog != null) {
			return catalog;
		} else {
			return ((Schema) obj).getDatabase();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.sqm.core.containment.
	 * AbstractContainmentProvider
	 * #getContainmentFeature(org.eclipse.emf.ecore.EObject)
	 */
	public EStructuralFeature getContainmentFeature(EObject obj) {
		return SQLSchemaPackage.eINSTANCE.getCatalog_Schemas();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.sqm.internal.core.containment.
	 * ContainmentProvider#getGroupId(org.eclipse.emf.ecore.EObject)
	 */
	public String getGroupId(EObject obj) {
		return GroupID.SCHEMA;
	}

}