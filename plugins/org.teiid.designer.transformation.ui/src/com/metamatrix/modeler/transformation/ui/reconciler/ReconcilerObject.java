/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.SingleElementSymbol;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;

/**
 * Reconciler Business Object that the Reconciler Panel works with
 */
public class ReconcilerObject {

    private final String BIND_ATTRIBUTES_MESSAGE = UiConstants.Util.getString("ReconcilerObject.statusMessage.bindTheAttributes"); //$NON-NLS-1$
    private final String HAS_UNMATCHED_SQL_MESSAGE = UiConstants.Util.getString("ReconcilerObject.statusMessage.hasUnmatchedSQL"); //$NON-NLS-1$
    private final String HAS_UNBOUND_ATTRIBUTES_MESSAGE = UiConstants.Util.getString("ReconcilerObject.statusMessage.hasUnboundAttributes"); //$NON-NLS-1$
    private final String HAS_TYPE_CONFLICTS_MESSAGE = UiConstants.Util.getString("ReconcilerObject.statusMessage.hasTypeConflicts"); //$NON-NLS-1$
    private final String NO_SQL_PROJECTED_SYMBOLS_MESSAGE = UiConstants.Util.getString("ReconcilerObject.statusMessage.noSqlProjectedSymbols"); //$NON-NLS-1$
    private final String IS_RECONCILED_MESSAGE = UiConstants.Util.getString("ReconcilerObject.statusMessage.isReconciled"); //$NON-NLS-1$
    private final String NEW_COLUMN_DIALOG_TITLE = UiConstants.Util.getString("ReconcilerObject.newColumnDialog.title"); //$NON-NLS-1$
    // ============================================================
    // Instance variables
    // ============================================================
    private EObject targetGroup; // the transformation target group
    private boolean isTargetLocked = false; // lock state of the transformation target

    private BindingList bindingList = new BindingList();
    private SqlList sqlList = new SqlList();

    private Command originalCommand = null;
    private List originalSymbols = new ArrayList();
    private List originalInputParamSymbols = new ArrayList();
    private QueryCommand modifiedCommand = null;

    private boolean sqlModifiable = true; // mdTODO: should be false when not a QueryCommand
    private boolean isSelectDistinct = false;

    // ============================================================
    // Constructors
    // ============================================================
    /**
     * Constructor.
     * 
     * @param transformationObj the TransformationMappingRoot this is based on.
     */
    public ReconcilerObject( EObject targetGroup,
                             Command originalCommand,
                             boolean targetLocked ) {
        this.originalCommand = originalCommand;
        this.targetGroup = targetGroup;
        this.isTargetLocked = targetLocked;
        init();
    }

    // ============================================================
    // Instance methods
    // ============================================================

    /**
     * Initialize the reconciler business object. This method creates attribute / Symbol bindings for each attribute. If a symbol
     * whose name matches the attribute is found, it is bound to the attribute. Any remaining unbound symbols are used to create
     * the sqlList.
     * 
     * @param mappingRoot the TransformationMappingRoot object
     */
    private void init() {
        // Get projected symbols from the SELECT, retain for future use
        if (originalCommand != null && originalCommand instanceof QueryCommand) {
            this.sqlModifiable = true;
            modifiedCommand = (QueryCommand)originalCommand.clone();
            originalSymbols.addAll(modifiedCommand.getProjectedSymbols());
            // Remember if the the SELECT is select distinct
            this.isSelectDistinct = isSelectDistinct((QueryCommand)originalCommand);
        }
        // Get the procedure InputParameters from the SQL command
        List originalInputParams = TransformationSqlHelper.getProcedureInputParams(originalCommand);
        Iterator paramIter = originalInputParams.iterator();
        while (paramIter.hasNext()) {
            SPParameter param = (SPParameter)paramIter.next();
            ElementSymbol eSymbol = param.getParameterSymbol();
            this.originalInputParamSymbols.add(eSymbol);
        }

        // Get the target Attributes
        List targetAttributes = TransformationHelper.getTargetAttributes(targetGroup);

        // Working symbols list - will be modified as symbols are bound
        List workingSymbolList = new ArrayList(originalSymbols.size());
        workingSymbolList.addAll(originalSymbols);

        // Create a Binding for each targetAttribute
        Iterator attrIter = targetAttributes.iterator();
        while (attrIter.hasNext()) {
            EObject attribute = (EObject)attrIter.next();
            String attrShortName = TransformationHelper.getSqlEObjectName(attribute);
            // If a matching symbol is found, use it in the binding. And remove from working list
            SingleElementSymbol seSymbol = getSymbolWithShortName(attrShortName, workingSymbolList);
            if (seSymbol != null) {
                bindingList.add(new Binding(attribute, seSymbol));
            } else {
                bindingList.add(new Binding(attribute));
            }
        }

        // Check the remaining symbols for Procedure InputParameters
        if (!workingSymbolList.isEmpty()) {
            // Create the SqlList using the left-over (unbound) Sql Symbols
            sqlList.addAll(workingSymbolList);
        }

    }

    /**
     * Get Symbol from the supplied workingSymbolList, which matches the supplied name. If found, it is removed from the working
     * list. If not found, returns null.
     * 
     * @param name the symbolName to find in the supplied symbol list.
     * @param workingSymbolList the workingList of symbols to search
     * @return the matching symbol from the list, if found. If not found, returns null.
     */
    private SingleElementSymbol getSymbolWithShortName( String name,
                                                        List workingSymbolList ) {
        SingleElementSymbol result = null;
        Iterator iter = workingSymbolList.iterator();
        while (iter.hasNext()) {
            SingleElementSymbol seSymbol = (SingleElementSymbol)iter.next();
            String symbolName = TransformationSqlHelper.getSingleElementSymbolShortName(seSymbol, false);
            if (symbolName != null && symbolName.equalsIgnoreCase(name)) {
                result = seSymbol;
                iter.remove();
                break;
            }
        }
        return result;
    }

    /**
     * Get the status string for the current state of the object
     * 
     * @return the current reconciled status
     */
    public String getStatus() {
        boolean hasUnboundAttributes = hasUnboundBindings();
        boolean hasUnmatchedSQL = hasUnmatchedSQL();
        boolean hasTypeConflicts = hasTypeConflicts();
        String message = IS_RECONCILED_MESSAGE;
        if (!modifiedSqlHasProjectedSymbols()) {
            message = NO_SQL_PROJECTED_SYMBOLS_MESSAGE;
            // There are unmatched SQL Sybmols
        } else if (hasUnmatchedSQL) {
            // If attributes are unbound, message to bind unbound symbols
            if (hasUnboundAttributes) {
                message = BIND_ATTRIBUTES_MESSAGE;
                // All attributes bound, message to eliminate extra symbols
            } else {
                message = HAS_UNMATCHED_SQL_MESSAGE;
            }
            // No unmatched SQL Symbols
        } else {
            // Unbound attributes, can eliminate them message
            if (hasUnboundAttributes) {
                message = HAS_UNBOUND_ATTRIBUTES_MESSAGE;
                // All attributes bound, check for type conflicts
            } else if (hasTypeConflicts) {
                message = HAS_TYPE_CONFLICTS_MESSAGE;
            }
        }
        return message;
    }

    /**
     * Get the status type for the current state of the object
     * 
     * @return the current reconciled status type (INFO, WARNING, ERROR, ..)
     */
    public int getStatusType() {
        int statusType = IMessageProvider.NONE;
        boolean hasUnboundAttributes = hasUnboundBindings();
        boolean hasUnmatchedSQL = hasUnmatchedSQL();
        boolean hasTypeConflicts = hasTypeConflicts();
        boolean sqlHasProjectedSymbols = modifiedSqlHasProjectedSymbols();
        if (hasUnboundAttributes || hasUnmatchedSQL || hasTypeConflicts || !sqlHasProjectedSymbols) {
            statusType = IMessageProvider.ERROR;
        }
        return statusType;
    }

    /**
     * Get Binding List
     * 
     * @return the list of Bindings for this object
     */
    public BindingList getBindingList() {
        return bindingList;
    }

    /**
     * Get SQL List
     * 
     * @return the list of SQL symbols for this object
     */
    public SqlList getSqlList() {
        return sqlList;
    }

    /**
     * Refresh lists
     */
    public void refresh() { // NO_UCD
        bindingList.refresh(true);
        sqlList.refresh(true);
        updateCommandFromBindings();
    }

    /**
     * get the current SQL string to display for this object
     * 
     * @return the SQL String to display
     */
    public String getOriginalSql() {
        return originalCommand.toString();
    }

    /**
     * Add a new binding to the end of the bindings list
     * 
     * @param binding the binding to add
     */
    public void addBinding( Binding binding ) {
        bindingList.add(binding);
        updateCommandFromBindings();
    }

    /**
     * remove the supplied symbol from the symbols list
     * 
     * @param symbol the symbol to remove
     */
    public void removeSymbol( SingleElementSymbol symbol ) {
        sqlList.remove(symbol);
        updateCommandFromBindings();
    }

    /**
     * add the supplied List of symbols to the symbols list
     * 
     * @param symbols the list of symbols to add
     */
    public void addSymbols( List symbols ) {
        sqlList.addAll(symbols);
        updateCommandFromBindings();
    }

    /**
     * remove the supplied List of symbols from the symbols list
     * 
     * @param symbols the list of symbols to remove
     */
    public void removeSymbols( List symbols ) {
        sqlList.removeAll(symbols);
        updateCommandFromBindings();
    }

    /**
     * determine if any of the current bindings are unbound
     * 
     * @return 'true' if any of the bindings are unbound
     */
    public boolean hasUnboundBindings() {
        boolean hasUnbound = false;
        List bindings = bindingList.getAll();
        Iterator iter = bindings.iterator();
        while (iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            if (!binding.isBound()) {
                hasUnbound = true;
                break;
            }
        }
        return hasUnbound;
    }

    /**
     * determine if there are any unmatched SQL symbols
     * 
     * @return 'true' if any unmatched sql symbols
     */
    public boolean hasUnmatchedSQL() {
        return sqlList.size() > 0;
    }

    /**
     * determine if there are any type conflicts
     * 
     * @return 'true' if any type conflicts
     */
    public boolean hasTypeConflicts() {
        return bindingList.hasTypeConflict();
    }

    /**
     * determine if there are any type conflicts
     * 
     * @return 'true' if any type conflicts
     */
    public boolean hasTypeConflict( int index ) { // NO_UCD
        boolean hasConflict = false;
        if (index >= 0 && index < bindingList.size()) {
            Binding binding = bindingList.get(index);
            hasConflict = binding.hasTypeConflict();
        }
        return hasConflict;
    }

    public void unbind( List bindings ) {
        // Save the unbound SQL symbols
        List unboundSQL = new ArrayList(bindings.size());

        // Iterate and unbind the supplied bindings
        Iterator iter = bindings.iterator();
        Binding binding = null;
        while (iter.hasNext()) {
            binding = (Binding)iter.next();
            SingleElementSymbol symbol = binding.getCurrentSymbol();
            // Add the current symbol to the list, if not null
            if (symbol != null) {
                unboundSQL.add(symbol);
            }
            // Set the binding symbol null
            binding.setOriginalSymbol(null);
        }
        // refresh the binding list
        bindingList.refresh(true);
        // add the symbols back into the symbols list
        addSymbols(unboundSQL);
    }

    public void bind( List bindings,
                      List sqlSymbols ) {
        Binding binding = null;
        SingleElementSymbol sqlSymbol = null;
        if (bindings.size() == sqlSymbols.size()) {
            for (int i = 0; i < bindings.size(); i++) {
                binding = (Binding)bindings.get(i);
                sqlSymbol = (SingleElementSymbol)sqlSymbols.get(i);
                if (binding.getCurrentSymbol() == null) {
                    binding.setNewSymbol(sqlSymbol);
                }
            }
        }

        bindingList.refresh(true);
        removeSymbols(sqlSymbols);
    }

    public void createNewBindings( List sqlSymbols ) {
        if (sqlSymbols == null || !sqlSymbols.isEmpty()) {
            Iterator iter = sqlSymbols.iterator();
            while (iter.hasNext()) {
                SingleElementSymbol symbol = (SingleElementSymbol)iter.next();
                String symbolShortName = TransformationSqlHelper.getSingleElementSymbolShortName(symbol, false);
                if (bindingList.hasRemovedBindingMatch(symbolShortName)) {
                    Binding removedBinding = bindingList.getRemovedBindingMatch(symbolShortName);
                    removedBinding.setNewSymbol(symbol);
                    addBinding(removedBinding);
                } else {
                    Binding newBinding = new Binding(symbolShortName, symbol);
                    addBinding(newBinding);
                }
                removeSymbol(symbol);
            }
        } else {
            Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
            ColumnEntryDialog dialog = new ColumnEntryDialog(shell, NEW_COLUMN_DIALOG_TITLE);

            dialog.open();

            String newColumnName = dialog.getColumnName();
            EObject datatype = dialog.getDatatype();

            if (newColumnName != null) {
                // no symbol provided, so assume "New"
                Binding newBinding = new Binding(newColumnName);
                newBinding.setNewAttrDatatype(datatype);
                addBinding(newBinding);
            }
        }

    }

    public boolean shouldEnableBind( List selectedBindings,
                                     List selectedSymbols ) {
        boolean bindEnabled = false;
        if (sqlModifiable) {
            int nBindings = selectedBindings.size();
            int nSymbols = selectedSymbols.size();

            if (nBindings > 0 && nSymbols > 0 && nBindings == nSymbols) {
                bindEnabled = true;
                // All rows must have null symbol values
                Iterator iter = selectedBindings.iterator();
                while (iter.hasNext()) {
                    Binding binding = (Binding)iter.next();
                    if (binding.getCurrentSymbol() != null) {
                        bindEnabled = false;
                        break;
                    }
                }
            }
        }
        return bindEnabled;
    }

    public boolean shouldEnableUnbind( List selectedBindings ) {
        boolean unbindEnabled = false;
        if (sqlModifiable) {
            if (selectedBindings.size() > 0) {
                // At least one Binding must have non-null symbol value to enable
                Iterator iter = selectedBindings.iterator();
                while (iter.hasNext()) {
                    Binding binding = (Binding)iter.next();
                    if (binding.getCurrentSymbol() != null) {
                        unbindEnabled = true;
                        break;
                    }
                }
            }
        }
        return unbindEnabled;
    }

    public boolean shouldEnableCreateNew( List selectedSymbols ) { // NO_UCD
        boolean enableCreate = false;

        if (selectedSymbols.size() > 0) {
            if (!isTargetLocked()) {
                enableCreate = true;
                // If any of the selected row names are already in attributes, disable
                Iterator iter = selectedSymbols.iterator();
                while (iter.hasNext()) {
                    iter.next();
                    // SingleElementSymbol symbol = (SingleElementSymbol)iter.next();
                    // TableItem item = sqlListPanel.getTableViewer().getTable().getItem(rowIndex);
                    // Do Not allow creation of duplicate Attribute Names
                    // if( containsIgnoreCase(getTargetAttributeNamesOrdered(),sqlName) ) {
                    // enabled=false;
                    // break;
                    // }
                }
            }
        }
        return enableCreate;
    }

    /**
     * Check whether there are any required mods to the SQL or targetGroup.
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasValidModifications() {
        boolean hasMods = false;
        // If any of the following have changed, set hasMods to true
        if (hasValidSqlModifications() || hasTargetAttributesToDelete() || hasTargetAttributesToCreate()
            || hasTargetAttributesToRename() || hasTargetAttributeTypeMods()) {
            hasMods = true;
        }
        return hasMods;
    }

    /**
     * Check whether there are valid mods to the SQL. The SQL must be different than the original SQL and the modified SQL must
     * have at least one projected symbol.
     * 
     * @return true if there are valid modifications, false if not.
     */
    public boolean hasValidSqlModifications() {
        boolean hasValidSqlMods = false;
        if (hasModifiedSql() && modifiedSqlHasProjectedSymbols()) {
            hasValidSqlMods = true;
        }
        return hasValidSqlMods;
    }

    /**
     * Check whether there are any mods to the original SQL.
     * 
     * @return true if there are SQL modifications, false if not.
     */
    public boolean hasModifiedSql() {
        // Determine if any SQL mods are required
        boolean sqlModified = false;
        // If SQL Modifiable, compare original command with modified command
        if (sqlModifiable && originalCommand != null && modifiedCommand != null) {
            String originalSQL = originalCommand.toString();
            String modifiedSQL = modifiedCommand.toString();
            // If the original Query was SELECT *, expand it for comparison
            if (originalCommand instanceof QueryCommand && isSelectStar((QueryCommand)originalCommand)) {
                originalSQL = expandSelectStar((QueryCommand)originalCommand);
            }
            sqlModified = !modifiedSQL.equals(originalSQL);
        }
        return sqlModified;
    }

    /**
     * Get the modified SQL statement string.
     * 
     * @return the modified Commands SQL text
     */
    public String getModifiedSql() {
        String sql = null;
        if (modifiedCommand != null) {
            sql = modifiedCommand.toString();
        }
        return sql;
    }

    /**
     * Check whether the modified SQL has any projected symbols
     * 
     * @return true if there is one or more projected symbol, false if not.
     */
    public boolean modifiedSqlHasProjectedSymbols() {
        boolean hasSymbols = false;
        if (modifiedCommand != null) {
            List symbolList = modifiedCommand.getProjectedSymbols();
            if (symbolList != null && symbolList.size() > 0) {
                hasSymbols = true;
            }
        }
        return hasSymbols;
    }

    /**
     * Determine whether the Target Virtual Group is Locked (ReadOnly)
     * 
     * @return 'true' if the transformation target group is Locked (Readonly), 'false' if not.
     */
    public boolean isTargetLocked() {
        return this.isTargetLocked;
    }

    /**
     * Determine if the supplied symbol is an inputParameter symbol.
     * 
     * @param symbol the ElementSymbol to test.
     * @return 'true' if supplied symbol is InputParameter, 'false' if not.
     */
    public boolean isInputParameterSymbol( SingleElementSymbol symbol ) {
        boolean isInputParamSymbol = false;
        // See if it's in the InputParameter symbol list.
        if (this.originalInputParamSymbols.contains(symbol)) {
            isInputParamSymbol = true;
        }
        return isInputParamSymbol;
    }

    /**
     * Set the Target Virtual Group Locked state
     * 
     * @param shouldLock 'true' if the transformation target group is to be Locked (Readonly), 'false' if not.
     */
    public void setTargetLocked( boolean shouldLock ) {
        // If different than current state, change it.
        if (this.isTargetLocked != shouldLock) {
            this.isTargetLocked = shouldLock;
        }
    }

    /**
     * Tests whether a QueryCommand is a SELECT *.
     * 
     * @return true if the query is a SELECT *, false if not.
     */
    private boolean isSelectStar( QueryCommand queryCommand ) {
        boolean isSelectStar = false;
        if (queryCommand != null) {
            Select select = queryCommand.getProjectedQuery().getSelect();
            if (select != null) {
                isSelectStar = select.isStar();
            }
        }
        return isSelectStar;
    }

    /**
     * Tests whether a QueryCommand is a SELECT DISTINCT.
     * 
     * @return true if the query is a SELECT DISTINCT, false if not.
     */
    private boolean isSelectDistinct( QueryCommand queryCommand ) {
        boolean isSelectDistinct = false;
        if (queryCommand != null) {
            Select select = queryCommand.getProjectedQuery().getSelect();
            if (select != null) {
                isSelectDistinct = select.isDistinct();
            }
        }
        return isSelectDistinct;
    }

    /**
     * Expands a QueryCommand if it has a SELECT *.
     * 
     * @return the modified query string with the SELECT * expanded.
     */
    private String expandSelectStar( QueryCommand queryCommand ) {
        QueryCommand qComm = (QueryCommand)queryCommand.clone();
        if (qComm != null) {
            Select select = queryCommand.getProjectedQuery().getSelect();
            boolean isDistinct = select.isDistinct();
            if (select.isStar()) {
                // Get the list of SELECT symbols
                List symbols = queryCommand.getProjectedSymbols();
                Select newSelect = new Select(symbols);
                if (isDistinct) {
                    newSelect.setDistinct(isDistinct);
                }
                queryCommand.getProjectedQuery().setSelect(newSelect);
            }
        }
        return qComm.toString();
    }

    /**
     * determine if any of the bindings with attributes have pending name changes
     * 
     * @return 'true' if pending attribute name changes, 'false' if not.
     */
    public boolean hasTargetAttributesToRename() {
        boolean hasNameChange = false;
        if (!isTargetLocked()) {
            Iterator iter = bindingList.getAll().iterator();
            while (iter.hasNext()) {
                Binding binding = (Binding)iter.next();
                if (binding.hasAttrNameModification()) {
                    hasNameChange = true;
                    break;
                }
            }
        }
        return hasNameChange;
    }

    /**
     * apply all pending pending name changes
     */
    public void applyTargetAttributeRenames( Object txnSource ) {
        if (!isTargetLocked()) {
            Iterator iter = bindingList.getAll().iterator();
            while (iter.hasNext()) {
                Binding binding = (Binding)iter.next();
                if (binding.hasAttrNameModification()) {
                    binding.applyAttrNameModification(txnSource);
                }
            }
        }
    }

    /**
     * determine if any of the bindings with attributes have pending type mods
     * 
     * @return 'true' if pending attribute type changes, 'false' if not.
     */
    public boolean hasTargetAttributeTypeMods() {
        boolean hasTypeChange = false;
        if (!isTargetLocked()) {
            Iterator iter = bindingList.getAll().iterator();
            while (iter.hasNext()) {
                Binding binding = (Binding)iter.next();
                if (binding.hasAttrTypeModification()) {
                    hasTypeChange = true;
                    break;
                }
            }
        }
        return hasTypeChange;
    }

    /**
     * apply all pending pending type changes
     */
    public void applyTargetAttributeTypeMods( Object txnSource ) {
        if (!isTargetLocked()) {
            Iterator iter = bindingList.getAll().iterator();
            while (iter.hasNext()) {
                Binding binding = (Binding)iter.next();
                if (binding.hasAttrTypeModification()) {
                    binding.applyAttrTypeModification(txnSource);
                }
            }
        }
    }

    /**
     * Check whether there are any new Attributes to Create.
     * 
     * @return true if there are attributes to create, false if not.
     */
    public boolean hasTargetAttributesToCreate() {
        boolean hasNew = false;
        if (!isTargetLocked()) {
            Iterator iter = bindingList.getAll().iterator();
            while (iter.hasNext()) {
                Binding binding = (Binding)iter.next();
                Object attr = binding.getAttribute();
                if (attr instanceof String) {
                    hasNew = true;
                    break;
                }
            }
        }
        return hasNew;
    }

    /**
     * Get the list of Target Attributes to Create.
     * 
     * @return the List of attribute names to create.
     */
    public List getTargetAttributeNamesToCreate() {
        if (isTargetLocked()) {
            return Collections.EMPTY_LIST;
        }
        ArrayList newAttrNames = new ArrayList();
        Iterator iter = bindingList.getAll().iterator();
        while (iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            Object attr = binding.getAttribute();
            if (attr instanceof String) {
                newAttrNames.add(attr);
            }
        }
        return newAttrNames;
    }

    /**
     * Get the Map of attribute name - to - type length for the attributes that are to be created.
     * 
     * @return the Map of attribute names (key) to length (value).
     */
    public Map getCreatedAttrLengthMap() {
        // empty Map returned if target group is locked
        if (isTargetLocked()) {
            return Collections.EMPTY_MAP;
        }
        // Iterate thru the bindings
        Map symbolLengthMap = new HashMap();
        Iterator iter = bindingList.getAll().iterator();
        while (iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            // Get the binding attribute
            Object attr = binding.getAttribute();
            // If binding attribute is a string, then it's to be created new
            if (attr instanceof String) {
                // Get length for the bound symbol & put key-value into the Map
                int length = binding.getCurrentSymbolLength();
                symbolLengthMap.put(attr, new Integer(length));
            }
        }
        return symbolLengthMap;
    }

    /**
     * Check whether there are any Target Attributes to Delete.
     * 
     * @return true if there are attributes to delete, false if not.
     */
    public boolean hasTargetAttributesToDelete() {
        boolean hasDeleteAttr = false;
        if (!isTargetLocked()) {
            List deletedBindings = bindingList.getRemovedList();
            Iterator iter = deletedBindings.iterator();
            while (iter.hasNext()) {
                Binding binding = (Binding)iter.next();
                Object attr = binding.getAttribute();
                if (!(attr instanceof String)) {
                    hasDeleteAttr = true;
                    break;
                }
            }
        }
        return hasDeleteAttr;
    }

    /**
     * Get the List of Target Attributes to Delete.
     * 
     * @return the list of attributes to delete.
     */
    public List getTargetAttributesToDelete() {
        List attrs = new ArrayList();
        if (!isTargetLocked()) {
            List deletedBindings = bindingList.getRemovedList();
            Iterator iter = deletedBindings.iterator();
            while (iter.hasNext()) {
                Binding binding = (Binding)iter.next();
                Object attr = binding.getAttribute();
                if (!(attr instanceof String)) {
                    attrs.add(attr);
                }
            }
        }
        return attrs;
    }

    /**
     * Update the Modified Query using the current state of the Bindings
     */
    public void updateCommandFromBindings() {
        // Working list of unmatched SQL Symbols
        List unmatchedSymbols = new ArrayList();
        unmatchedSymbols.addAll(sqlList.getAll());

        // List to hold the resulting symbols
        ArrayList newSymbols = new ArrayList();

        // -----------------------------
        // Iterate thru the Bindings
        // -----------------------------
        Iterator iter = bindingList.getAll().iterator();
        while (iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            // --------------------------------------------------------------
            // Bound - get the Symbol from the binding
            // --------------------------------------------------------------
            if (binding.isBound() && !binding.isInputParamBinding()) {
                SingleElementSymbol symbol = binding.createBindingSymbol();
                if (!isInputParameterSymbol(symbol)) {
                    newSymbols.add(binding.createBindingSymbol());
                }
                // --------------------------------------------------------------
                // Unbound - use seSymbol from the unmatched Symbol List
                // --------------------------------------------------------------
            } else {
                // current attribute name
                String attrName = binding.getCurrentAttrName();
                // See if there is a matching symbol in the unmatched working list
                SingleElementSymbol seSymbol = getSymbolWithShortName(attrName, unmatchedSymbols);
                if (seSymbol != null && !isInputParameterSymbol(seSymbol)) {
                    newSymbols.add(seSymbol);
                } else {
                    if (!unmatchedSymbols.isEmpty()) {
                        seSymbol = (SingleElementSymbol)unmatchedSymbols.get(0);
                        if (seSymbol != null && !isInputParameterSymbol(seSymbol)) {
                            newSymbols.add(seSymbol);
                        }
                        unmatchedSymbols.remove(0);
                    }
                }
            }
        }

        // Ensure that the "Removed SQL symbols" have been removed from the working list
        // List removedSymbols = sqlList.getRemovedList();
        // unmatchedSymbols.removeAll(removedSymbols);

        // If there are unmatched symbols remaining, add them
        iter = unmatchedSymbols.iterator();
        while (iter.hasNext()) {
            SingleElementSymbol sym = (SingleElementSymbol)iter.next();
            if (sym != null && !isInputParameterSymbol(sym)) {
                newSymbols.add(sym);
            }
        }

        // Use the new list of symbols to update the modified Query
        Select newSelect = new Select(newSymbols);
        if (this.isSelectDistinct) {
            newSelect.setDistinct(true);
        }
        if (modifiedCommand instanceof Query) {
            ((Query)modifiedCommand).setSelect(newSelect);
        } else if (modifiedCommand instanceof SetQuery) {
            ((SetQuery)modifiedCommand).getProjectedQuery().setSelect(newSelect);
        }

    }

    public List getOriginalSymbols() {
        return this.originalSymbols;
    }

    public Command getModifiedCommand() {
        return modifiedCommand;
    }

    public Command getOriginalCommand() {
        return originalCommand;
    }

    /**
     * Returns a Collection of Group symbols that are NOT referenced by ElementSymbols within a Query command
     * 
     * @return
     * @since 5.0
     */
    public Collection getUnreferencedGroupSymbols() {
        Collection allGroupSymbols = TransformationSqlHelper.getGroupSymbols(originalCommand);
        Collection unreferencedGroupSymbols = new ArrayList(allGroupSymbols);
        // Let's get list of all Element symbols...

        Collection referencedElementSymbols = ElementCollectorVisitor.getElements(originalCommand, true, true);

        Iterator iter = referencedElementSymbols.iterator();
        while (iter.hasNext()) {
            SingleElementSymbol nextSymbol = (SingleElementSymbol)iter.next();
            if (nextSymbol instanceof AliasSymbol) {
                SingleElementSymbol theSymbol = ((AliasSymbol)nextSymbol).getSymbol();
                if (theSymbol instanceof ElementSymbol) {
                    GroupSymbol nextGS = ((ElementSymbol)theSymbol).getGroupSymbol();
                    if (TransformationSqlHelper.containsGroupSymbol(allGroupSymbols, nextGS)) {
                        unreferencedGroupSymbols.remove(nextGS);
                    }
                }
            } else if (nextSymbol instanceof ElementSymbol) {
                GroupSymbol nextGS = ((ElementSymbol)nextSymbol).getGroupSymbol();
                if (TransformationSqlHelper.containsGroupSymbol(allGroupSymbols, nextGS)) {
                    unreferencedGroupSymbols.remove(nextGS);
                }
            }
        }
        return unreferencedGroupSymbols;
    }

    /**
     * Returns a Collection of Group symbols that are actually referenced by ElementSymbols within a Query command
     * 
     * @return
     * @since 5.0
     */
    public Collection getReferencedGroupSymbols() {
        Collection allGroupSymbols = TransformationSqlHelper.getGroupSymbols(originalCommand);
        Collection referencedGroupSymbols = new ArrayList(allGroupSymbols.size());

        Iterator iter = originalSymbols.iterator();
        while (iter.hasNext()) {
            SingleElementSymbol nextSymbol = (SingleElementSymbol)iter.next();
            if (nextSymbol instanceof AliasSymbol) {
                SingleElementSymbol theSymbol = ((AliasSymbol)nextSymbol).getSymbol();
                if (theSymbol instanceof ElementSymbol) {
                    GroupSymbol nextGS = ((ElementSymbol)theSymbol).getGroupSymbol();
                    if (!TransformationSqlHelper.containsGroupSymbol(referencedGroupSymbols, nextGS)) {
                        referencedGroupSymbols.add(nextGS);
                    }
                }
            } else if (nextSymbol instanceof ElementSymbol) {
                GroupSymbol nextGS = ((ElementSymbol)nextSymbol).getGroupSymbol();
                if (!TransformationSqlHelper.containsGroupSymbol(referencedGroupSymbols, nextGS)) {
                    referencedGroupSymbols.add(nextGS);
                }
            }
        }
        return referencedGroupSymbols;
    }

    /**
     * Returns a list of ALL element symbols available for a Query command based on existing GROUP symbols in the Query
     * 
     * @return
     * @since 5.0
     */
    public List getAvailableElementSymbols() {
        List availableSymbolList = new ArrayList();
        Collection groupSymbols = TransformationSqlHelper.getGroupSymbols(originalCommand);
        if (!groupSymbols.isEmpty()) {
            // Now we need to create Element Symbols for ALL unused group symbols
            List extraGroupEObjects = TransformationSqlHelper.getGroupSymbolEObjects(groupSymbols);

            for (Iterator iter = extraGroupEObjects.iterator(); iter.hasNext();) {
                EObject nextGroup = (EObject)iter.next();
                SqlAlias sqlAlias = TransformationFactory.eINSTANCE.createSqlAlias();
                sqlAlias.setAliasedObject(nextGroup);
                String aliasName = TransformationHelper.getSqlEObjectFullName(nextGroup);
                sqlAlias.setAlias(aliasName);

                List newElementSymbols = TransformationSqlHelper.createElemSymbols(sqlAlias);
                if (!newElementSymbols.isEmpty()) {
                    availableSymbolList.addAll(newElementSymbols);
                }
            }
        }

        return availableSymbolList;
    }
}
