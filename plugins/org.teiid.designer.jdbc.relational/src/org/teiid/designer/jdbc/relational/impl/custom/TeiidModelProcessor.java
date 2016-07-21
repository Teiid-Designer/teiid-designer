package org.teiid.designer.jdbc.relational.impl.custom;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Processors;
import org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMapping;

public class TeiidModelProcessor extends RelationalModelProcessorImpl {

    private static final String INTEGER_TYPE = "INTEGER"; //$NON-NLS-1$
    
    /**
     * Construct an instance of OracleModelProcessor.
     */
    public TeiidModelProcessor() {
        super();
    }

    /**
     * Construct an instance of OracleModelProcessor.
     * 
     * @param factory
     */
    public TeiidModelProcessor( final RelationalFactory factory ) {
        super(factory);
    }

    /**
     * Construct an instance of OracleModelProcessor.
     * 
     * @param factory
     */
    public TeiidModelProcessor( final RelationalFactory factory,
                                 final RelationalTypeMapping mapping ) {
        super(factory, mapping);
    }
    
    /**
     * Find the type given the supplied information. This method is called by the various <code>create*</code> methods, and is
     * currently implemented to use {@link #findType(int, int, List)} when a numeric type and {@link #findType(String, List)} (by
     * name) for other types.
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

        // Teiid may return an integer type for JDBC. We need to assume it's mapped to INT
        if (typeName.toUpperCase().startsWith(INTEGER_TYPE)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.INT, problems);
        }
        if (result != null) {
            return result;
        }

        return super.findType(jdbcType, typeName, length, precision, scale, problems);
    }
    
	@Override
	public String getType() {
		return Processors.TEIID;
	}
}
