/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.ast;

import org.teiid.runtime.client.lang.parser.TeiidParser;
import org.teiid.runtime.client.types.DataTypeManagerService;

public class ExceptionExpression extends SimpleNode implements Expression {
    
    /**
     * @param i
     */
    public ExceptionExpression(int i) {
        super(i);
    }

    /**
     * @param teiidParser 
     * @param i
     */
    public ExceptionExpression(TeiidParser teiidParser, int i) {
        super(teiidParser, i);
    }

    private Expression message;
    private Expression sqlState;
    private Expression errorCode;
    private Expression parent;
    
    @Override
    public Class<?> getType() {
        return DataTypeManagerService.DefaultDataTypes.OBJECT.getClass();
    }

    public Expression getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(Expression errCode) {
        this.errorCode = errCode;
    }
    
    public Expression getSqlState() {
        return sqlState;
    }
    
    public void setSqlState(Expression sqlState) {
        this.sqlState = sqlState;
    }
    
    public Expression getMessage() {
        return message;
    }
    
    public void setMessage(Expression message) {
        this.message = message;
    }
    
    public Expression getParent() {
        return parent;
    }
    
    public void setParent(Expression parent) {
        this.parent = parent;
    }

    public String getDefaultSQLState() {
        return "50001";
    }

    @Override
    public String toString() {
        return "ExceptionExpression [message=" + this.message + ", sqlState=" + this.sqlState + ", errorCode=" + this.errorCode
               + ", parent=" + this.parent + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ExceptionExpression other = (ExceptionExpression)obj;
        if (this.errorCode == null) {
            if (other.errorCode != null) return false;
        } else if (!this.errorCode.equals(other.errorCode)) return false;
        if (this.message == null) {
            if (other.message != null) return false;
        } else if (!this.message.equals(other.message)) return false;
        if (this.parent == null) {
            if (other.parent != null) return false;
        } else if (!this.parent.equals(other.parent)) return false;
        if (this.sqlState == null) {
            if (other.sqlState != null) return false;
        } else if (!this.sqlState.equals(other.sqlState)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.errorCode == null) ? 0 : this.errorCode.hashCode());
        result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
        result = prime * result + ((this.parent == null) ? 0 : this.parent.hashCode());
        result = prime * result + ((this.sqlState == null) ? 0 : this.sqlState.hashCode());
        return result;
    }
}
