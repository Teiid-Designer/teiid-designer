/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.extension;

import java.util.Set;
import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import org.teiid.designer.core.extension.ModelTypeMetaclassNameFactory;
import org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalPackage;

/**
 * Provides extendable metaclass names for the Web Service metamodel.
 * 
 * @since 8.0
 */
public class RelationalExtendableClassnameProvider extends AbstractMetaclassNameProvider implements ModelTypeMetaclassNameFactory {

    private static final String VIEW = "org.teiid.designer.metamodels.relational.impl.ViewImpl"; //$NON-NLS-1$

    private VirtualModelProvider virtualModelProvider;

    /**
     * Constructs a provider.
     */
    public RelationalExtendableClassnameProvider() {
        super(RelationalPackage.eNS_URI);

        final String column = "org.teiid.designer.metamodels.relational.impl.ColumnImpl"; //$NON-NLS-1$
        final String primaryKey = "org.teiid.designer.metamodels.relational.impl.PrimaryKeyImpl"; //$NON-NLS-1$
        final String foreignKey = "org.teiid.designer.metamodels.relational.impl.ForeignKeyImpl"; //$NON-NLS-1$
        final String procedure = "org.teiid.designer.metamodels.relational.impl.ProcedureImpl"; //$NON-NLS-1$
        final String index = "org.teiid.designer.metamodels.relational.impl.IndexImpl"; //$NON-NLS-1$
        final String procedureParameter = "org.teiid.designer.metamodels.relational.impl.ProcedureParameterImpl"; //$NON-NLS-1$
        final String uniqueConstraint = "org.teiid.designer.metamodels.relational.impl.UniqueConstraintImpl"; //$NON-NLS-1$
        final String accessPattern = "org.teiid.designer.metamodels.relational.impl.AccessPatternImpl"; //$NON-NLS-1$
        final String baseTable = "org.teiid.designer.metamodels.relational.impl.BaseTableImpl"; //$NON-NLS-1$
        final String procedureResult = "org.teiid.designer.metamodels.relational.impl.ProcedureResultImpl"; //$NON-NLS-1$

        addMetaclass(baseTable, NO_PARENTS);
        addMetaclass(VIEW, NO_PARENTS);
        addMetaclass(procedure, NO_PARENTS);
        addMetaclass(index, NO_PARENTS);

        addMetaclass(column, baseTable, VIEW, procedureResult);
        addMetaclass(primaryKey, baseTable);
        addMetaclass(foreignKey, baseTable);
        addMetaclass(accessPattern, baseTable, VIEW);
        addMetaclass(uniqueConstraint, baseTable);

        addMetaclass(procedureParameter, procedure);
        addMetaclass(procedureResult, procedure);
    }

    /**
     * @see org.teiid.designer.core.extension.ModelTypeMetaclassNameFactory#getProvider(java.util.Set)
     */
    @Override
    public ExtendableMetaclassNameProvider getProvider(final Set<String> modelTypes) {
        if ((modelTypes != null) && (modelTypes.size() == 1)
            && ModelType.VIRTUAL_LITERAL.getName().equals(modelTypes.iterator().next())) {
            if (this.virtualModelProvider == null) {
                this.virtualModelProvider = new VirtualModelProvider(this);
            }

            return this.virtualModelProvider;
        }

        return this;
    }

    class VirtualModelProvider extends AbstractMetaclassNameProvider {

        private final RelationalExtendableClassnameProvider baseProvider;

        /**
         * @param baseProvider the base relational metamodel metaclass name provider (never <code>null</code>)
         */
        VirtualModelProvider(final RelationalExtendableClassnameProvider baseProvider) {
            super(baseProvider.getMetamodelUri());
            this.baseProvider = baseProvider;
        }

        /**
         * @see org.teiid.designer.core.extension.AbstractMetaclassNameProvider#getExtendableMetaclassChildren(java.lang.String)
         */
        @Override
        public String[] getExtendableMetaclassChildren(String parentMetaclassName) {
            return this.baseProvider.getExtendableMetaclassChildren(parentMetaclassName);
        }

        /**
         * @see org.teiid.designer.core.extension.AbstractMetaclassNameProvider#getExtendableMetaclassRoots()
         */
        @Override
        public String[] getExtendableMetaclassRoots() {
            final String[] allRoots = this.baseProvider.getExtendableMetaclassRoots();
            final String[] rootsMinusView = new String[allRoots.length - 1];
            int i = 0;

            // need to filter out view
           for (final String root : allRoots) {
                if (VIEW.equals(root)) {
                    continue;
                }

                rootsMinusView[i++] = root;
            }

            return rootsMinusView;
        }

    }

}
