/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.reconciler;

import javax.lang.model.type.NullType;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.util.RuntimeTypeConverter;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.TransformationMappingHelper;
import org.teiid.designer.transformation.util.TransformationSqlHelper;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 * Binding Business Object A Binding has the following properties: (1) Virtual Attribute - can be MetaObject or String(if creating
 * new) (2) SQL Symbol
 * 
 * @author Mark Drilling
 *
 * @since 8.0
 */
public class Binding {

    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String AS = "AS"; //$NON-NLS-1$
    private static final String FROM = "from"; //$NON-NLS-1$
    private static final String TO = "to"; //$NON-NLS-1$
    private static final String CR = "\n"; //$NON-NLS-1$
    private static final String ATTRIBUTE_TYPE_NULL_TEXT = UiConstants.Util.getString("Binding.attributeTypeNull.text"); //$NON-NLS-1$
    private static final String BINDING_SYMBOL_NULL_TEXT = UiConstants.Util.getString("Binding.bindingSymbolNull.text"); //$NON-NLS-1$
    private static final String USE_SQL_SYMBOL_TEXT = UiConstants.Util.getString("Binding.useSqlSymbol.text"); //$NON-NLS-1$
    private static final String USE_SQL_EXPRESSION_TEXT = UiConstants.Util.getString("Binding.useSqlExpression.text"); //$NON-NLS-1$
    private static final String CONVERT_SQL_TEXT = UiConstants.Util.getString("Binding.convertSqlSymbol.text"); //$NON-NLS-1$
    private static final String LOSS_OF_PRECISION_WARNING = UiConstants.Util.getString("Binding.lossOfPrecisionWarning.text"); //$NON-NLS-1$
    private static final String RUNTIME_TYPES_EQUAL_TEXT = UiConstants.Util.getString("Binding.runtimeTypesEqual.text"); //$NON-NLS-1$
    private static final String NO_CONVERSION_REQD_TEXT = UiConstants.Util.getString("Binding.noConversionReqd.text"); //$NON-NLS-1$
    private static final String NO_CONVERSION_AVAIL_TEXT = UiConstants.Util.getString("Binding.noConversionAvail.text"); //$NON-NLS-1$
    private static final String CANNOT_CONVERT_AGGREGATE_TEXT = UiConstants.Util.getString("Binding.cannotConvertAggregate.text"); //$NON-NLS-1$

    // Binding attribute can be EObject or String (when creating new)
    private Object attribute;
    // Binding symbol is SingleElementSymbol
    // private SingleElementSymbol sqlSymbol;

    private EObject originalAttrDatatype;
    private EObject newAttrDatatype;
    private String newAttrName;

    private IExpression originalSymbol;
    private IExpression newSymbol;
    private IExpression availableSymbolConversion;

    // private boolean canConvertSql = false;
    private boolean sqlWasConverted = false;
    private boolean isInputParamBinding = false;
    private String sqlConversionText;

    /**
     * Create a Binding given only the attribute
     * 
     * @param attribute the target attribute
     */
    public Binding( Object attribute ) {
        setAttribute(attribute);
    }

    /**
     * Create a Binding given both the attribute and the bound symbol
     * 
     * @param attribute the target attribute
     * @param symbol the symbol to be bound to the attribute
     */
    public Binding( Object attribute,
    		IExpression symbol ) {
        setAttribute(attribute);
        setOriginalSymbol(symbol);
    }

    /**
     * @return true if bound, false otherwise
     */
    public boolean isBound() {
        return (originalSymbol != null) ? true : false;
    }

    public void setInputParamBinding( boolean isInputParam ) {
        this.isInputParamBinding = isInputParam;
    }

    public boolean isInputParamBinding() {
        return this.isInputParamBinding;
    }

    /**
     * @return true if there is a type conflict, false otherwise
     */
    public boolean hasTypeConflict() {
        boolean hasConflict = false;
        EObject currentAttrDatatype = getCurrentAttrDatatype();
        if (isBound()) {
            // If the current attribute type is null, isConflict
            if (currentAttrDatatype == null) {
                hasConflict = true;
                // attribute type not null, check
            } else {
                hasConflict = !TransformationMappingHelper.typesMatch(getCurrentSymbol(), currentAttrDatatype);
            }
        }
        return hasConflict;
    }

    /**
     * @return binding attribute
     */
    public Object getAttribute() {
        return attribute;
    }

    /**
     * @return SQL Symbol binding
     */
    public IExpression getOriginalSymbol() {
        return originalSymbol;
    }

    /**
     * Get the current SingleElementSymbol. If the symbol has been modified, the modified symbol is returned. If not, the original
     * symbol is returned;
     * 
     * @return SQL Symbol binding
     */
    public IExpression getCurrentSymbol() {
    	IExpression result = originalSymbol;
        if (newSymbol != null) {
            result = newSymbol;
        }
        return result;
    }

    /**
     * Get the current symbol's runtime type.
     * 
     * @return the current symbols runtime type
     */
    public String getCurrentSymbolRuntimeType() {
        String runtimeType = null;
        IExpression currentSymbol = getCurrentSymbol();
        if (currentSymbol != null) {
            runtimeType = RuntimeTypeConverter.getRuntimeType(currentSymbol);
        }
        return runtimeType;
    }

    /**
     * Get the current symbol's length
     * 
     * @return the current symbols length
     */
    public int getCurrentSymbolLength() {
        int length = 0;
        IExpression currentSymbol = getCurrentSymbol();
        if (currentSymbol != null) {
            length = TransformationSqlHelper.getElementSymbolLength(currentSymbol);
        }
        return length;
    }

    /**
     * Set the attribute
     * 
     * @param attribute
     */
    public void setAttribute( Object attribute ) {
        this.attribute = attribute;
        if (attribute != null && TransformationHelper.isSqlColumn(attribute)) {
            originalAttrDatatype = TransformationHelper.getSqlColumnDatatype((EObject)attribute);
        }
    }

    /**
     * Set the SQL Symbol
     * 
     * @param the SQL Symbol
     */
    public void setOriginalSymbol( IExpression seSymbol ) {
        this.originalSymbol = seSymbol;
        this.newSymbol = null;
        // Update SQL Conversion Data - based on symbol and attr type
        if (seSymbol == null) {
            availableSymbolConversion = null;
            sqlConversionText = null;
            // canConvertSql=false;
            sqlWasConverted = false;
        } else {
            // If the attribute is "String", means this is new attribute. Takes on symbol type.
            if (getAttribute() instanceof String) {
                String symbolRuntimeType = RuntimeTypeConverter.getRuntimeType(seSymbol);
                EObject datatype = TransformationMappingHelper.getDefaultDatatypeForRuntimeTypeName(symbolRuntimeType);
                setNewAttrDatatype(datatype);
            } else {
                updateSqlConversionData();
            }
        }
    }

    /**
     * Set the New SQL Symbol - this happens on bind
     * 
     * @param the SQL Symbol
     */
    public void setNewSymbol( IExpression seSymbol ) {
        if (this.originalSymbol == null) {
            this.originalSymbol = seSymbol;
            this.newSymbol = seSymbol;
        } else {
            // Already a new symbol, set it as original first
            if (this.newSymbol != null) {
                this.originalSymbol = this.newSymbol;
            }
            this.newSymbol = seSymbol;
        }
        // Update SQL Conversion Data - based on symbol and attr type
        updateSqlConversionData();
    }

    /**
     * set new target attribute type
     * 
     * @param typeStr the new type String
     */
    public void setNewAttrDatatype( EObject datatype ) {
        newAttrDatatype = datatype;

        // Update the SQL Conversion Data - based on new attribute runtime type
        updateSqlConversionData();
    }

    /**
     * set new target attribute name
     * 
     * @param name the new name String
     */
    public void setNewAttrName( String name ) {
        newAttrName = name;
    }

    /**
     * get new target attribute type
     * 
     * @return the new type String
     */
    public EObject getOriginalAttrDatatype() {
        return newAttrDatatype;
    }

    /**
     * get current target attribute type
     * 
     * @return the new type String
     */
    public EObject getCurrentAttrDatatype() {
        EObject result = originalAttrDatatype;
        if (hasAttrTypeModification()) {
            result = newAttrDatatype;
        }
        return result;
    }

    /**
     * get current target attribute name
     * 
     * @return the attribute name
     */
    public String getCurrentAttrName() {
        if (hasAttrNameModification()) {
            return newAttrName;
        }
        return getAttributeName();
    }

    /**
     * Check whether the target attribute type has been modified
     * 
     * @return true if there is a pending modification, false if not.
     */
    public boolean hasAttrTypeModification() {
        return (newAttrDatatype != null);
    }

    /**
     * Check whether the target attribute name has been modified
     * 
     * @return true if there is a pending modification, false if not.
     */
    public boolean hasAttrNameModification() {
        return (newAttrName != null);
    }

    public void applyAttrNameModification( Object txnSource ) {
        if (hasAttrNameModification()) {
            String newName = getCurrentAttrName();
            Object attribute = getAttribute();
            if (attribute instanceof EObject) {
                TransformationHelper.setSqlColumnName((EObject)getAttribute(), newName, txnSource);
            }
        }
    }

    public void applyAttrTypeModification( Object txnSource ) {
        if (hasAttrTypeModification()) {
            EObject newType = getCurrentAttrDatatype();
            Object attribute = getAttribute();
            if (attribute instanceof EObject) {
                TransformationHelper.setSqlColumnDatatype((EObject)attribute, newType, txnSource);
            }
        }
    }

    /**
     * Check whether the SqlSymbol has been modified
     * 
     * @return true if there is a pending modification, false if not.
     */
    public boolean hasSymbolModification() { // NO_UCD
        return (newSymbol != null);
    }

    /**
     * Check whether the SqlSymbol has been modified
     * 
     * @return true if there is a pending modification, false if not.
     */
    public boolean hasAvailableSymbolConversion() {
        return (availableSymbolConversion != null);
    }

    /**
     * accept the sql Conversion. This resets the sqlSymbol to the new Symbol
     * 
     * @param seSymbol the new Sql Symbol
     */
    public void acceptSqlConversion() {
        if (hasAvailableSymbolConversion()) {
            newSymbol = availableSymbolConversion;
            // Update the SQL Conversion Data - based on attribute runtime type and new symbol
            updateSqlConversionData();
            // track that the original symbol was modified
            sqlWasConverted = true;
        }
    }

    /**
     * undo the sql Conversion after it has been accepted. This resets the new Symbolto null.
     */
    public void undoSqlConversion() {
        if (sqlWasConverted) {
            newSymbol = null;
            // Update the SQL Conversion Data - based on attribute runtime type and new symbol
            updateSqlConversionData();
            // track that the original symbol was modified
            sqlWasConverted = false;
        }
    }

    /**
     * Check whether there are any modifications to the SQL Symbols
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean sqlSymbolWasConverted() {
        return sqlWasConverted;
    }

    /**
     * accept the attribute type conversion. This resets the sqlSymbol to the new Symbol
     * 
     * @param seSymbol the new Sql Symbol
     */
    public void acceptAttributeConversion() {
        if (hasAttributeConversion()) {
            // Get current symbols runtime type
            String runtimeType = getCurrentSymbolRuntimeType();
            // Get default datatype for it
            EObject datatype = TransformationMappingHelper.getDefaultDatatypeForRuntimeTypeName(runtimeType);
            // set the attribute datatype
            setNewAttrDatatype(datatype);
        }
    }

    /**
     * Get attribute Name
     * 
     * @return the attribute name
     */
    public String getAttributeName() {
        String result = PluginConstants.EMPTY_STRING;
        Object attr = getAttribute();
        if (TransformationHelper.isSqlColumn(attr)) {
            result = TransformationHelper.getSqlEObjectName((EObject)attr);
        } else if (attr instanceof String) {
            result = (String)attr;
        }
        return result;
    }

    /**
     * Get attribute Full Name
     * 
     * @return the attribute full name
     */
    public String getAttributeFullName() {
        String result = PluginConstants.EMPTY_STRING;
        Object attr = getAttribute();
        if (TransformationHelper.isSqlColumn(attr)) {
            result = TransformationHelper.getSqlEObjectFullName((EObject)attr);
        } else if (attr instanceof String) {
            result = (String)attr;
        }
        return result;
    }

    /**
     * Get attribute String description text - "name : currentType"
     * 
     * @return the string descriptive text
     */
    public String getAttributeText( boolean showType ) {
        StringBuffer sb = new StringBuffer(getCurrentAttrName());
        if (showType) {
            final EObject currentDatatype = getCurrentAttrDatatype();
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(currentDatatype, true);
            final String dtName = dtMgr.getRuntimeTypeName(currentDatatype);
            if (dtName != null) {
                sb.append(" : " + dtName); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }

    /**
     * Get SqlSymbol description text for the current symbol
     * 
     * @return the string descriptive text
     */
    public String getSqlSymbolText( boolean showType ) {
        StringBuffer sb = new StringBuffer();
        Object sqlSymbol = getCurrentSymbol();
        if (sqlSymbol != null) {
            // If its an IAliasSymbol, use underlying symbol
            if (sqlSymbol instanceof IAliasSymbol) {
                sqlSymbol = ((IAliasSymbol)sqlSymbol).getSymbol();
            }
            if (sqlSymbol instanceof IExpression) {
                String symbolName = TransformationSqlHelper.getSingleElementSymbolShortName((IExpression)sqlSymbol, true);
                // show aliased if necessary
                if (!isInputParamBinding()) {
                    String attrName = getCurrentAttrName();
                    String symShortName = TransformationSqlHelper.getSingleElementSymbolShortName((IExpression)sqlSymbol,
                                                                                                  false);
                    // If symbol and attribute shortNames are different, show as aliased
                    if (!attrName.equalsIgnoreCase(symShortName)) {
                        sb = new StringBuffer(symbolName + SPACE + AS + SPACE + attrName);
                        // same names, just show symbol
                    } else {
                        sb = new StringBuffer(symbolName);
                    }
                    // InputParam Bindings are never aliased
                } else {
                    sb = new StringBuffer(symbolName);
                }
                if (showType) {
                    sb.append(" : " + getSymbolDatatype((IExpression)sqlSymbol)); //$NON-NLS-1$
                }
            }
        }
        return sb.toString();
    }

    private String getSymbolDatatype( IExpression seSymbol ) {
        String typeName;
        Class objClass = seSymbol.getType();
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        
        if (objClass == null) {
            typeName = service.getDataTypeName(NullType.class);
        } else {
            typeName = service.getDataTypeName(objClass);
        }
        return typeName;
    }

    /**
     * Create a SingleElementSymbol for the current state of the binding Create from the Current Binding symbol and ensure it will
     * match the current name.
     * 
     * @return the SingleElementSymbol for the current state of the binding
     */
    public IExpression createBindingSymbol() {
    	IExpression result = null;
        if (isBound()) {
        	IExpression currentSymbol = getCurrentSymbol();
            String currentAttrName = getCurrentAttrName();
            String symbolShortName = TransformationSqlHelper.getSingleElementSymbolShortName(currentSymbol, false);
            // -----------------------------------------------------------------
            // Name of current Symbol matches the target Attribute
            // -----------------------------------------------------------------
            if (symbolShortName != null && symbolShortName.equalsIgnoreCase(currentAttrName)) {
                result = (IExpression)currentSymbol.clone();

                // -----------------------------------------------------------------
                // Name of sql doesnt match the target Attribute
                // -----------------------------------------------------------------
            } else {
                // If this is already an alias, rename it
                if (currentSymbol instanceof IAliasSymbol) {
                	IExpression uSymbol = ((IAliasSymbol)currentSymbol).getSymbol();
                    // If underlying symbol matches, drop the alias
                	IQueryService queryService = ModelerCore.getTeiidQueryService();
                    if (queryService.getSymbolShortName(uSymbol).equalsIgnoreCase(currentAttrName)) {
                        result = (IExpression)uSymbol.clone();
                    } else {
                        IAliasSymbol aSym = (IAliasSymbol)currentSymbol.clone();
                        aSym.setShortName(currentAttrName);
                        result = aSym;
                    }
                    // If this is not an alias, make it one
                } else {
                    IQueryService queryService = ModelerCore.getTeiidQueryService();
                    IQueryFactory factory = queryService.createQueryFactory();
                    currentSymbol = factory.createAliasSymbol(currentAttrName, currentSymbol);
                    result = currentSymbol;
                }
            }
        }
        return result;
    }

    /**
     * Determine if there is an attribute conversion for this binding. Checks to see if the attribute has a type which is
     * compatible with the current SQL Symbol type.
     * 
     * @return 'true' if the attribute conversion is possible, 'false' if not.
     */
    public boolean hasAttributeConversion() {
        boolean canConvert = false;
        // Get current symbol runtime type
        String symbolRuntimeType = getCurrentSymbolRuntimeType();
        // Get default datatype compatible with symbol runtime type
        if (symbolRuntimeType != null) {
            EObject datatype = TransformationMappingHelper.getDefaultDatatypeForRuntimeTypeName(symbolRuntimeType);
            // If a conversion is available, canConvert = true
            if (datatype != null) {
                canConvert = true;
            }
        }
        return canConvert;
    }

    /**
     * Get the SQL Conversion status for this binding
     * 
     * @return 'true' if the Sql conversion is possible, 'false' if not.
     */
    public boolean canConvertSqlSymbol() {
        return (availableSymbolConversion != null);
    }

    /**
     * Get the SQL Conversion display text for the selected binding
     * 
     * @return the Symbol Conversion text
     */
    public String getSqlConversionText() {
        return sqlConversionText;
    }

    private void updateSqlConversionData() {
        updateCanConvertSqlStatus();
        updateAvailableSymbolConversionAndText();
    }

    /**
     * Updates whether the binding's SQL symbol can be converted so that it's runtime type matches the attribute type.
     */
    private void updateCanConvertSqlStatus() {
        // canConvertSql=false;
        // If the current attribute is a "String", this is binding for new attribute
        // or if the current attribute datatype is null, will take on datatype of symbol
        // Set canConvert true
        if (getAttribute() instanceof String || getCurrentAttrDatatype() == null) {
            // canConvertSql=true;
            return;
        }
    }

    /**
     * update the SQL Conversion display text and symbol for this binding
     */
    public void updateAvailableSymbolConversionAndText() {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        
        sqlConversionText = null;
        availableSymbolConversion = null;
        if (getCurrentSymbol() == null) {
            sqlConversionText = BINDING_SYMBOL_NULL_TEXT;
            return;
        }
        String symbolAlias = "aliasName"; //$NON-NLS-1$
        // ----------------------------------------------------
        // Get the DataType Name for the current SQL Symbol
        // ----------------------------------------------------
        EObject attrDatatype = getCurrentAttrDatatype();
        if (attrDatatype == null) {
            sqlConversionText = NO_CONVERSION_REQD_TEXT + CR + ATTRIBUTE_TYPE_NULL_TEXT;
            return;
        }
        String currentAttrTypeStr = RuntimeTypeConverter.getRuntimeType(attrDatatype);

        Object sqlSymbol = getCurrentSymbol();
        String sqlSymbolTypeStr = RuntimeTypeConverter.getRuntimeType(sqlSymbol);
        IExpression oSymbol = originalSymbol;

        // -------------------------------------------------------
        // DataTypes not equal, update the SQL to do the CONVERT
        // -------------------------------------------------------
        if (!currentAttrTypeStr.equalsIgnoreCase(sqlSymbolTypeStr)) {
            // if the conversion is desired back to the original SQL type, no convert is required
            String originalSQLTypeStr = service.getDataTypeName(oSymbol.getType());
            if (currentAttrTypeStr.equalsIgnoreCase(originalSQLTypeStr)) {
                availableSymbolConversion = null;
                // optimizer.optimize(oSymbol);
                sqlConversionText = CONVERT_SQL_TEXT + CR + oSymbol.toString();
            } else {
                // Check whether there is a conversion
                boolean isExplicit = service.isExplicitConversion(sqlSymbolTypeStr, currentAttrTypeStr);
                boolean isImplicit = service.isImplicitConversion(sqlSymbolTypeStr, currentAttrTypeStr);
                // Explicit conversion, use it
                if (isImplicit || isExplicit) {
                    // If symbol is aliased, get underlying symbol
                    if (oSymbol instanceof IAliasSymbol) {
                        IAliasSymbol aSym = (IAliasSymbol)oSymbol;
                        oSymbol = aSym.getSymbol();
                        // Symbol alias should be target attribute name
                        symbolAlias = getCurrentAttrName();
                    }
                    if (oSymbol instanceof IElementSymbol) {
                        symbolAlias = getCurrentAttrName();
                        IAliasSymbol aSymbol = TransformationSqlHelper.convertElementSymbol((IElementSymbol)oSymbol,
                                                                                           currentAttrTypeStr,
                                                                                           symbolAlias);
                        availableSymbolConversion = aSymbol;
                        sqlConversionText = getSQLLabelText(aSymbol, symbolAlias, isExplicit);
                        return;
                    } else if (oSymbol instanceof IExpressionSymbol) {
                        IExpressionSymbol eSymbol = (IExpressionSymbol)oSymbol.clone();
                        // First check whether the IExpression is a ConvertFunction, and the converted
                        // Symbol type matches the desired type.
                        if (TransformationSqlHelper.isConvertFunction(eSymbol)) {
                            IExpression cExpr = TransformationSqlHelper.getConvertedExpr(eSymbol);
                            if (cExpr != null) {
                            	IExpression seSymbol = cExpr;
                                String seSymbolTypeStr = service.getDataTypeName(seSymbol.getType());
                                if (seSymbolTypeStr != null && seSymbolTypeStr.equalsIgnoreCase(currentAttrTypeStr)) {
                                    availableSymbolConversion = seSymbol;
                                    // optimizer.optimize(availableSymbolConversion);
                                    sqlConversionText = USE_SQL_SYMBOL_TEXT + CR + seSymbol.toString();
                                    // optimizer.deoptimize(availableSymbolConversion);
                                    return;
                                }
                            } else {
                                IExpressionSymbol exprSymbol = factory.createExpressionSymbol("expr", cExpr); //$NON-NLS-1$
                                availableSymbolConversion = exprSymbol;
                                if (symbolAlias != null) {
                                    sqlConversionText = USE_SQL_EXPRESSION_TEXT + CR + availableSymbolConversion.toString()
                                                            + SPACE + AS + SPACE + symbolAlias;
                                } else {
                                    sqlConversionText = USE_SQL_EXPRESSION_TEXT + CR + availableSymbolConversion.toString();
                                }
                                eSymbol = TransformationSqlHelper.convertExpressionSymbol(exprSymbol, currentAttrTypeStr);
                            }
                        } else {
                            eSymbol = TransformationSqlHelper.convertExpressionSymbol((IExpressionSymbol)oSymbol,
                                                                                      currentAttrTypeStr);
                        }
                        sqlConversionText = getSQLLabelText(eSymbol, symbolAlias, isExplicit);
                        if (symbolAlias != null) {
                            availableSymbolConversion = factory.createAliasSymbol(symbolAlias, eSymbol);
                        } else {
                            availableSymbolConversion = eSymbol;
                        }
                    }
                    sqlConversionText = CONVERT_SQL_TEXT + SPACE + FROM + SPACE + sqlSymbolTypeStr + SPACE + TO + SPACE
                                        + currentAttrTypeStr;
                    // No conversion available
                } else {
                    sqlConversionText = CONVERT_SQL_TEXT + CR + NO_CONVERSION_AVAIL_TEXT + SPACE + FROM + SPACE
                                        + sqlSymbolTypeStr + SPACE + TO + SPACE + currentAttrTypeStr;
                }
            }
            // -------------------------------------------------------
            // DataTypes not equal, update the SQL to do the CONVERT
            // -------------------------------------------------------
        } else {
            sqlConversionText = RUNTIME_TYPES_EQUAL_TEXT + CR + NO_CONVERSION_REQD_TEXT;
        }
        return;
    }

    private String getSQLLabelText( IExpression seSymbol,
                                    String symbolAlias,
                                    boolean isExplicit ) {
        StringBuffer sb = new StringBuffer();
        if (seSymbol != null) {
            // optimizer.optimize(seSymbol);
            sb.append(seSymbol.toString());
            // optimizer.deoptimize(seSymbol);
            // If symbolAlias was passed in, use it
            if (symbolAlias != null && !(seSymbol instanceof IAliasSymbol)) {
                sb.append(SPACE + AS + SPACE + symbolAlias);
            }
            // If explicit conversion, warn loss of precision
            if (isExplicit) {
                sb.append(CR + LOSS_OF_PRECISION_WARNING);
            }
        }
        return sb.toString();
    }
}
