/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.teiid.designer.query.sql.lang.ITableFunctionReference;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.util.SymbolMap;

/**
 *
 */
public abstract class TableFunctionReference extends FromClause
    implements ITableFunctionReference<LanguageVisitor> {

    private GroupSymbol symbol;

    private SymbolMap correlatedReferences;

    /**
     * @param p
     * @param id
     */
    public TableFunctionReference(TeiidParser p, int id) {
        super(p, id);
    }

    /**
     * @return correlated references
     */
    public SymbolMap getCorrelatedReferences() {
        return correlatedReferences;
    }
    
    /**
     * @param correlatedReferences
     */
    public void setCorrelatedReferences(SymbolMap correlatedReferences) {
        this.correlatedReferences = correlatedReferences;
    }
    
    /**
     * @param copy
     */
    public void copy(TableFunctionReference copy) {
        copy.symbol = this.symbol.clone();
        if (correlatedReferences != null) {
            copy.correlatedReferences = correlatedReferences.clone();
        }
    }

    @Override
    public void collectGroups(Collection<GroupSymbol> groups) {
        groups.add(getGroupSymbol());
    }

    /**
     * Get name of this clause.
     * @return Name of clause
     */
    public String getName() {
        return this.symbol.getName();   
    }
    
    /**
     * @return output name
     */
    public String getOutputName() {
        return this.symbol.getOutputName();
    }

    /**
     * Get GroupSymbol representing the named subquery 
     * @return GroupSymbol representing the subquery
     */
    public GroupSymbol getGroupSymbol() {
        return this.symbol;    
    }
    
    /** 
     * Reset the alias for this subquery from clause and it's pseudo-GroupSymbol.  
     * WARNING: this will modify the hashCode and equals semantics and will cause this object
     * to be lost if currently in a HashMap or HashSet.
     * @param name New name
     * @since 4.3
     */
    public void setName(String name) {
        this.symbol = this.parser.createASTNode(ASTNodes.GROUP_SYMBOL);
        this.symbol.setName(name);
    }

    /**
     * @return projected columns
     */
    public abstract List<? extends ProjectedColumn> getColumns();

    /**
     * @return calculated projected symbols
     */
    public List<ElementSymbol> getProjectedSymbols() {
        ArrayList<ElementSymbol> symbols = new ArrayList<ElementSymbol>(getColumns().size());
        for (ProjectedColumn col : getColumns()) {
            symbols.add(col.getSymbol());
        }
        return symbols;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.correlatedReferences == null) ? 0 : this.correlatedReferences.hashCode());
        result = prime * result + ((this.symbol == null) ? 0 : this.symbol.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TableFunctionReference other = (TableFunctionReference)obj;
        if (this.correlatedReferences == null) {
            if (other.correlatedReferences != null)
                return false;
        } else if (!this.correlatedReferences.equals(other.correlatedReferences))
            return false;
        if (this.symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!this.symbol.equals(other.symbol))
            return false;
        return true;
    }
}
