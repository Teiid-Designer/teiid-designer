/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiPlugin;


/**
 * A viewer for displaying the System models in ModelerCore's external container.
 * 
 * @since 8.0
 */
public class SystemModelTreeViewer extends TreeViewer implements StringConstants {
	
	private Model[] models;
	
	/**
     * @param parent
     * @since 4.3
     */
    public SystemModelTreeViewer( Composite parent ) {
        this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    }

    /**
     * @param parent
     * @param style
     * @since 4.3
     */
    public SystemModelTreeViewer( Composite parent,
                                  int style ) {
        super(parent, style);

        setContentProvider(new RelationalModelContentProvider());
        setLabelProvider(new SystemModelLabelProvider());
        
        models = new Model[2];
        models[0] = createSYSModel();
        models[1] = createSYSADMINModel();
        
        super.setInput(models);
    }

    class SystemModelLabelProvider implements ILabelProvider {

        ILabelProvider delegate = ModelUtilities.getModelObjectLabelProvider();

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         * @since 4.3
         */
        @Override
		public Image getImage( Object element ) {
            if (element instanceof Model) {
            	return UiPlugin.getDefault().getImage(PluginConstants.Images.MODEL);
            } else if( element instanceof Table ) {
            	return UiPlugin.getDefault().getImage(PluginConstants.Images.TABLE_IMAGE);
            } else if( element instanceof Procedure ) {
            	return UiPlugin.getDefault().getImage(PluginConstants.Images.PROCEDURE_IMAGE);
            } else if( element instanceof Column ) {
            	return UiPlugin.getDefault().getImage(PluginConstants.Images.COLUMN_IMAGE);
            } else if( element instanceof Parameter ) {
            	return UiPlugin.getDefault().getImage(PluginConstants.Images.PARAMETER_IMAGE);
            } else if( element instanceof PK ) {
            	return UiPlugin.getDefault().getImage(PluginConstants.Images.PRIMARY_KEY_IMAGE);
            } else if( element instanceof FK ) {
            	return UiPlugin.getDefault().getImage(PluginConstants.Images.FOREIGN_KEY_IMAGE);
            } else if( element instanceof UC ) {
            	return UiPlugin.getDefault().getImage(PluginConstants.Images.UNIQUE_CONSTRAINT_IMAGE);
            }
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         * @since 4.3
         */
        @Override
		public String getText( Object element ) {
            if (element instanceof RelationalObject) {
                return ((RelationalObject)element).getText();
            }
            return "<unknown>";
        }

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {

		}

    }
    
    class RelationalModelContentProvider implements ITreeContentProvider {

        public RelationalModelContentProvider() {
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        @Override
		public void dispose() {

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        @Override
		public Object[] getChildren( Object parentElement ) {
        	if( parentElement instanceof RelationalObject ) {
        		return ((RelationalObject)parentElement).getChildren();
        	}
            return new Object[0];
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        @Override
		public Object[] getElements( Object inputElement ) {
            return (Object[])inputElement;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        @Override
		public Object getParent( Object element ) {
        	if( element instanceof RelationalObject ) {
        		return ((RelationalObject)element).getParent();
        	}
            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        @Override
		public boolean hasChildren( Object element ) {
        	if( element instanceof RelationalObject ) {
        		return ((RelationalObject)element).getChildren().length > 0;
        	}
            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        @Override
		public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {

        }

    }
    
    abstract class RelationalObject {
    	String name;
    	List<RelationalObject> children;
    	RelationalObject parent;
    	
    	public RelationalObject(String name) {
    		this.name = name;
    		children = new ArrayList<RelationalObject>();
    	}
    	
    	public void addChild(RelationalObject child) {
    		this.children.add(child);
    	}
    	
    	public RelationalObject getParent() {
    		return parent;
    	}
    	
    	public void setParent(RelationalObject parent) {
    		this.parent = parent;
    	}
    	
    	public RelationalObject[] getChildren() {
    		return children.toArray(new RelationalObject[children.size()]);
    	}
    	
    	abstract String getText();
    }
    
    class Model extends RelationalObject {

		public Model(String name) {
			super(name);
		}
		
		public Table addTable(String name) {
			Table child = new Table(name);
			addChild(child);
			child.setParent(this);
			return child;
		}
		
		public Procedure addProcedure(String name) {
			Procedure child = new Procedure(name);
			addChild(child);
			child.setParent(this);
			return child;
		}

		@Override
		String getText() {
			return name;
		}
    }
    
    class Table extends RelationalObject {

		public Table(String name) {
			super(name);
		}
		
		public Column addColumn(String name, String datatype) {
			Column child = new Column(name, datatype);
			addChild(child);
			child.setParent(this);
			return child;
		}
		
		public PK addPK(String name) {
			PK child = new PK(name);
			addChild(child);
			child.setParent(this);
			return child;
		}
		
		public FK addFK(String name) {
			FK child = new FK(name);
			addChild(child);
			child.setParent(this);
			return child;
		}
		
		public UC addUC(String name) {
			UC child = new UC(name);
			addChild(child);
			child.setParent(this);
			return child;
		}


		@Override
		String getText() {
			return name;
		}
    }
    
    class Procedure extends RelationalObject {

		public Procedure(String name) {
			super(name);
		}
		
		public Parameter addParameter(String name, String datatype) {
			Parameter child = new Parameter(name, datatype);
			addChild(child);
			child.setParent(this);
			return child;
		}
		
		public Table addTable(String name) {
			Table child = new Table(name);
			addChild(child);
			child.setParent(this);
			return child;
		}

		@Override
		String getText() {
			return name;
		}
    }
    
    class Column extends RelationalObject {
    	String datatype;
    	
		public Column(String name, String datatype) {
			super(name);
			this.datatype = datatype;
		}

		@Override
		String getText() {
			return name + SPACE + COLON + SPACE + datatype;
		}
    }
    
    class Parameter extends RelationalObject {
    	String datatype;
    	
		public Parameter(String name, String datatype) {
			super(name);
			this.datatype = datatype;
		}

		@Override
		String getText() {
			return name + SPACE + COLON + SPACE + datatype;
		}
    }
    
    class PK extends RelationalObject {

		public PK(String name) {
			super(name);
		}

		@Override
		String getText() {
			return name;
		} 
    	
    }
    
    class FK extends RelationalObject {

		public FK(String name) {
			super(name);
		}

		@Override
		String getText() {
			return name;
		} 
    	
    }
    
    class UC extends RelationalObject {

		public UC(String name) {
			super(name);
		}

		@Override
		String getText() {
			return name;
		} 
    	
    }
    
    private Model createSYSModel() {
    	Model model = new Model("SYS");
    	
/*
CREATE FOREIGN TABLE FunctionParams (
	VDBName string(255) NOT NULL,
	SchemaName string(255),
	FunctionName string(255) NOT NULL,
	FunctionUID string(50) NOT NULL,
	Name string(255) NOT NULL,
	DataType string(25) NOT NULL,
	Position integer NOT NULL,
	Type string(100) NOT NULL,
	"Precision" integer NOT NULL,
	TypeLength integer NOT NULL,
	Scale integer NOT NULL,
	Radix integer NOT NULL,
	NullType string(10) NOT NULL,
	UID string(50),
	Description string(4000),
	TypeName string(100),
	TypeCode integer,
	ColumnSize integer,
	CONSTRAINT PK_1 PRIMARY KEY(VDBName, SchemaName, FunctionName, Name),
	CONSTRAINT FK_1 FOREIGN KEY(VDBName, SchemaName, FunctionName) REFERENCES Functions(VDBName, SchemaName, Name),
	CONSTRAINT UC_1 UNIQUE(UID)
) OPTIONS(UPDATABLE 'TRUE');
*/
    	Table table = model.addTable("FunctionParams");
    	table.addColumn("VDBName", "string(255)");
    	table.addColumn("SchemaName", "string(255)");
    	table.addColumn("FunctionName", "string(255)");
    	table.addColumn("FunctionUID", "string(50)");
    	table.addColumn("Name", "string(255)");
    	table.addColumn("DataType", "string(25)");
    	table.addColumn("Position", "integer");
    	table.addColumn("Type", "string(100)");
    	table.addColumn("\"Precision\"", "integer");
    	table.addColumn("TypeLength", "integer");
    	table.addColumn("Scale", "integer");
    	table.addColumn("Radix", "integer");
    	table.addColumn("NullType", "string(10)");
    	table.addColumn("UID", "string(50)");
    	table.addColumn("Description", "string(4000)");
    	table.addColumn("TypeName", "string(100)");
    	table.addColumn("TypeCode", "integer");
    	table.addColumn("ColumnSize", "integer");
    	table.addPK("PK_1");
    	table.addFK("FK_1");
    	table.addUC("UC_1");
    	
/*
CREATE FOREIGN TABLE Functions (
	VDBName string(255) NOT NULL,
	SchemaName string(255),
	Name string(255) NOT NULL,
	NameInSource string(255),
	UID string(50) NOT NULL,
	Description string(4000),
	IsVarArgs boolean,
	CONSTRAINT PK_1 PRIMARY KEY(VDBName, SchemaName, Name),
	CONSTRAINT FK_1 FOREIGN KEY(VDBName, SchemaName) REFERENCES Schemas(VDBName, Name),
	CONSTRAINT UC_1 UNIQUE(UID)
) OPTIONS(UPDATABLE 'TRUE');
 */
    	table = model.addTable("Functions");
    	table.addColumn("VDBName", "string(255)");
    	table.addColumn("SchemaName", "string(255)");
    	table.addColumn("Name", "string(255)");
    	table.addColumn("NameInSource", "string(255)");
    	table.addColumn("UID", "string(50)");
    	table.addColumn("Description", "string(4000)");
    	table.addColumn("IsVarArgs", "boolean");
    	table.addPK("PK_1");
    	table.addFK("FK_1");
    	table.addUC("UC_1");
    	
/*
CREATE FOREIGN TABLE spatial_ref_sys (
	srid integer,
	auth_name string(256),
	auth_srid integer,
	srtext string(2048),
	proj4text string(2048),
	CONSTRAINT PRIMARY KEY PRIMARY KEY(srid)
) OPTIONS(MATERIALIZED 'TRUE', UPDATABLE 'TRUE');
 */
    	table = model.addTable("spatial_ref_sys");
    	table.addColumn("srid", "integer");
    	table.addColumn("auth_name", "string(256)");
    	table.addColumn("auth_srid", "integer");
    	table.addColumn("srtext", "string(2048)");
    	table.addColumn("proj4text", "string(2048)");
    	table.addPK("PRIMARY KEY");
/*
    	CREATE FOREIGN TABLE GEOMETRY_COLUMNS (
    			F_TABLE_CATALOG string(256) NOT NULL,
    			F_TABLE_CATALOG string(256) NOT NULL,
    			F_TABLE_NAME string(256) NOT NULL,
    			F_GEOMETRY_COLUMN string(256) NOT NULL,
    			COORD_DIMENSION integer NOT NULL,
    			SRID integer NOT NULL,
    			TYPE string(30) NOT NULL
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("GEOMETRY_COLUMNS");
    	table.addColumn("F_TABLE_CATALOG", "string(256)");
    	table.addColumn("F_TABLE_CATALOG", "string(256)");
    	table.addColumn("F_TABLE_NAME", "string(256)");
    	table.addColumn("F_GEOMETRY_COLUMN", "string(256)");
    	table.addColumn("COORD_DIMENSION", "integer");
    	table.addColumn("SRID", "integer");
    	table.addColumn("TYPE", "string(30)");
/*
    		CREATE FOREIGN PROCEDURE ARRAYITERATE (
    			IN val object
    		) RETURNS
    			TABLE (
    				col object
    		) OPTIONS(UPDATECOUNT '1');
*/
    	Procedure proc = model.addProcedure("ARRAYITERATE");
    	proc.addParameter("val", "object");
    	Table result = proc.addTable("result");
    	result.addColumn("col", "object");

    	/*
    		CREATE FOREIGN TABLE Columns (
    			VDBName string(255) NOT NULL,
    			SchemaName string(255),
    			TableName string(255) NOT NULL,
    			Name string(255) NOT NULL,
    			Position integer NOT NULL,
    			NameInSource string(255),
    			DataType string(100) NOT NULL,
    			Scale integer NOT NULL,
    			Length integer NOT NULL,
    			IsLengthFixed boolean NOT NULL,
    			SupportsSelect boolean NOT NULL,
    			SupportsUpdates boolean NOT NULL,
    			IsCaseSensitive boolean NOT NULL,
    			IsSigned boolean NOT NULL,
    			IsCurrency boolean NOT NULL,
    			IsAutoIncremented boolean NOT NULL,
    			NullType string(20) NOT NULL,
    			MinRange string(50),
    			MaxRange string(50),
    			DistinctCount integer,
    			NullCount integer,
    			SearchType string(20) NOT NULL,
    			Format string(255),
    			DefaultValue string(255),
    			JavaClass string(500) NOT NULL,
    			"Precision" integer NOT NULL,
    			CharOctetLength integer,
    			Radix integer NOT NULL,
    			UID string(50) NOT NULL,
    			Description string(255),
    			TableUID string(50) NOT NULL,
    			TypeName string(100),
    			TypeCode integer,
    			ColumnSize integer,
    			CONSTRAINT PK_1 PRIMARY KEY(VDBName, SchemaName, TableName, Name),
    			CONSTRAINT FK_1 FOREIGN KEY(VDBName, SchemaName, TableName) REFERENCES Tables(VDBName, SchemaName, Name),
    			CONSTRAINT FK_2 FOREIGN KEY(TableUID) REFERENCES Tables(UID),
    			CONSTRAINT UC_1 UNIQUE(UID)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("Columns");
    	table.addColumn("VDBName", "string(255)");
    	table.addColumn("SchemaName", "string(255)");
    	table.addColumn("TableName", "string(255)");
    	table.addColumn("Name", "string(255)");
    	table.addColumn("Position", "integer");
    	table.addColumn("NameInSource", "string(255)");
    	table.addColumn("Datatype", "string(100)");
    	table.addColumn("Scale", "integer");
    	table.addColumn("Length", "integer");
    	table.addColumn("IsLengthFixed", "boolean");
    	table.addColumn("SupportsSelect", "boolean");
    	table.addColumn("SupportsUpdates", "boolean");
    	table.addColumn("IsCaseSensitive", "boolean");
    	table.addColumn("IsSigned", "boolean");
    	table.addColumn("IsCurrency", "boolean");
    	table.addColumn("IsAutoIncremented", "boolean");
    	table.addColumn("NullType", "string(20)");
    	table.addColumn("MinRange", "string(50)");
    	table.addColumn("MaxRange", "string(50)");
    	table.addColumn("DistinctCount", "integer");
    	table.addColumn("NullCount", "integer");
    	table.addColumn("SearchType", "string(20)");
    	table.addColumn("Format", "string(255)");
    	table.addColumn("DefaultValue", "string(255)");
    	table.addColumn("JavaClass", "string(500)");
    	table.addColumn("\"Precision\"", "integer");
    	table.addColumn("CharOctetLength", "integer");
    	table.addColumn("Radix", "integer");
    	table.addColumn("UID", "string(50)");
    	table.addColumn("Description", "string(255)");
    	table.addColumn("TableUID", "string(50");
    	table.addColumn("TypeName", "string(100)");
    	table.addColumn("TypeCode", "integer");
    	table.addColumn("ColumnSize", "integer");
    	table.addPK("PK_1");
    	table.addFK("FK_1");
    	table.addFK("FK_2");
    	table.addUC("UC_1");

    	/*
    		CREATE FOREIGN TABLE DataTypes (
    			Name string(100) NOT NULL,
    			IsStandard boolean,
    			Type string(64),
    			TypeName string(100) NOT NULL,
    			JavaClass string(500) NOT NULL,
    			Scale integer,
    			TypeLength integer NOT NULL,
    			NullType string(20) NOT NULL,
    			IsSigned boolean NOT NULL,
    			IsAutoIncremented boolean NOT NULL,
    			IsCaseSensitive boolean NOT NULL,
    			"Precision" integer NOT NULL,
    			Radix integer,
    			SearchType string(20) NOT NULL,
    			UID string(50) NOT NULL,
    			RuntimeType string(64),
    			BaseType string(64),
    			Description string(255),
    			TypeCode integer,
    			Literal_Prefix string(64),
    			Literal_Suffix string(64),
    			CONSTRAINT PK_1 PRIMARY KEY(Name),
    			CONSTRAINT UC_1 UNIQUE(UID)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("DataTypes");
    	table.addColumn("Name", "string(100)");
    	table.addColumn("IsStandard", "boolean");
    	table.addColumn("Type", "string(64)");
    	table.addColumn("TypeName", "string(100)");
    	table.addColumn("JavaClass", "string(500)");
    	table.addColumn("Scale", "integer");
    	table.addColumn("TypeLength", "integer");
    	table.addColumn("NullType", "string(20)");
    	table.addColumn("IsSigned", "boolean");
    	table.addColumn("IsAutoIncremented", "boolean");
    	table.addColumn("IsCaseSensitive", "boolean");
    	table.addColumn("\"Precision\"", "integer");
    	table.addColumn("Radix", "integer");
    	table.addColumn("SearchType", "string(20)");
    	table.addColumn("UID", "string(50)");
    	table.addColumn("RuntimeType", "string(64)");
    	table.addColumn("BaseType", "string(64)");
    	table.addColumn("Description", "string(255)");
    	table.addColumn("TypeCode", "integer");
    	table.addColumn("Literal_Prefix", "string(64)");
    	table.addColumn("Literal_Suffix", "string(64)");
    	table.addPK("PK_1");
    	table.addUC("UC_1");

    	/*
    		CREATE FOREIGN TABLE KeyColumns (
    			VDBName string(255) NOT NULL,
    			SchemaName string(255),
    			TableName string(2048) NOT NULL,
    			Name string(255) NOT NULL,
    			KeyName string(255),
    			KeyType string(20) NOT NULL,
    			RefKeyUID string(50),
    			UID string(50) NOT NULL,
    			Position integer,
    			TableUID string(50) NOT NULL,
    			CONSTRAINT PK_1 PRIMARY KEY(VDBName, SchemaName, TableName, Name),
    			CONSTRAINT FK_1 FOREIGN KEY(VDBName, SchemaName, TableName) REFERENCES Tables(VDBName, SchemaName, Name),
    			CONSTRAINT FK_2 FOREIGN KEY(TableUID) REFERENCES Tables(UID),
    			CONSTRAINT UC_1 UNIQUE(UID)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("KeyColumns");
    	table.addColumn("VDBName", "string(255)");
		table.addColumn("SchemaName", "string(255)");
		table.addColumn("TableName", "string(2048)");
		table.addColumn("Name", "string(255)");
		table.addColumn("KeyName", "string(255)");
		table.addColumn("KeyType", "string(20)");			
		table.addColumn("RefKeyUID", "string(50)");
		table.addColumn("UID", "string(50)");
		table.addColumn("Position", "integer");
		table.addColumn("TableUID", "string(50)");
    	table.addPK("PK_1");
    	table.addFK("FK_1");
    	table.addFK("FK_2");
    	table.addUC("UC_1");
    	/*
    		CREATE FOREIGN TABLE Keys (
    			VDBName string(255) NOT NULL,
    			SchemaName string(255),
    			TableName string(2048) NOT NULL,
    			Name string(255) NOT NULL,
    			Description string(255),
    			NameInSource string(255),
    			Type string(20) NOT NULL,
    			IsIndexed boolean NOT NULL,
    			RefKeyUID string(50),
    			UID string(50) NOT NULL,
    			TableUID string(50) NOT NULL,
    			RefTableUID string(50) NOT NULL,
    			ColPositions short NOT NULL,
    			CONSTRAINT PK_1 PRIMARY KEY(VDBName, SchemaName, TableName, Name),
    			CONSTRAINT FK_1 FOREIGN KEY(VDBName, SchemaName, TableName) REFERENCES Tables(VDBName, SchemaName, Name),
    			CONSTRAINT UC_1 UNIQUE(UID)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("Keys");
    	table.addColumn("VDBName", "string(255)");
    	table.addColumn("SchemaName", "string(255)");
    	table.addColumn("TableName", "string(2048)");
    	table.addColumn("Name", "string(255)");
    	table.addColumn("Description", "string(255)");
    	table.addColumn("NameInSource", "string(255)");
    	table.addColumn("Type", "string(20)");
    	table.addColumn("IsIndexed", "boolean");
    	table.addColumn("RefKeyUID", "string(50)");
    	table.addColumn("UID", "string(50)");
    	table.addColumn("TableUID", "string(50)");
    	table.addColumn("RefTableUID", "string(50)");
    	table.addColumn("ColPositions", "short");
    	table.addPK("PK_1");
    	table.addFK("FK_1");
    	table.addUC("UC_1");
    	
    	/*
    		CREATE FOREIGN TABLE ProcedureParams (
    			VDBName string(255) NOT NULL,
    			SchemaName string(255),
    			ProcedureName string(255) NOT NULL,
    			Name string(255) NOT NULL,
    			DataType string(25) NOT NULL,
    			Position integer NOT NULL,
    			Type string(100) NOT NULL,
    			"Optional" boolean NOT NULL,
    			"Precision" integer NOT NULL,
    			TypeLength integer NOT NULL,
    			Scale integer NOT NULL,
    			Radix integer NOT NULL,
    			NullType string(10) NOT NULL,
    			UID string(50),
    			Description string(255),
    			TypeName string(100),
    			TypeCode integer,
    			ColumnSize integer,
    			CONSTRAINT PK_1 PRIMARY KEY(VDBName, SchemaName, ProcedureName, Name),
    			CONSTRAINT FK_1 FOREIGN KEY(VDBName, SchemaName, ProcedureName) REFERENCES Procedures(VDBName, SchemaName, Name),
    			CONSTRAINT UC_1 UNIQUE(UID)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("ProcedureParams");
    	table.addColumn("VDBName", "string(255)");
    	table.addColumn("SchemaName", "string(255)");
    	table.addColumn("ProcedureName", "string(255)");
    	table.addColumn("Name", "string(255)");
    	table.addColumn("Datatype", "string(25)");
    	table.addColumn("Position", "integer");
    	table.addColumn("Type", "string(100)");
    	table.addColumn("\"Optional\"", "boolean");
    	table.addColumn("\"Precision\"", "integer");
    	table.addColumn("TypeLength", "integer");
    	table.addColumn("Scale", "integer");
    	table.addColumn("Radix", "integer");
    	table.addColumn("NullType", "string(10)");
    	table.addColumn("UID", "string(50)");
    	table.addColumn("Description", "string(255)");
    	table.addColumn("TypeName", "string(100)");
    	table.addColumn("TypeCode", "integer");
    	table.addColumn("ColumnSize", "integer");
    	table.addPK("PK_1");
    	table.addFK("FK_1");
    	table.addUC("UC_1");
    	
    	/*
    		CREATE FOREIGN TABLE Procedures (
    			VDBName string(255) NOT NULL,
    			SchemaName string(255),
    			Name string(255) NOT NULL,
    			NameInSource string(255),
    			ReturnsResults boolean NOT NULL,
    			UID string(50) NOT NULL,
    			Description string(255),
    			SchemaUID string(50) NOT NULL,
    			CONSTRAINT PK_1 PRIMARY KEY(VDBName, SchemaName, Name),
    			CONSTRAINT FK_2 FOREIGN KEY(SchemaUID) REFERENCES Schemas(UID),
    			CONSTRAINT FK_1 FOREIGN KEY(VDBName, SchemaName) REFERENCES Schemas(VDBName, Name),
    			CONSTRAINT UC_1 UNIQUE(UID)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("Procedures");
    	table.addColumn("VDBName", "string(255)");
		table.addColumn("SchemaName", "string(255)");
		table.addColumn("Name", "string(255)");
		table.addColumn("NameInSource", "string(255)");
		table.addColumn("ReturnsResults", "boolean");
		table.addColumn("UID", "string(50)");
		table.addColumn("Description", "string(255)");
		table.addColumn("SchemaUID", "string(50)");
    	table.addPK("PK_1");
    	table.addFK("FK_1");
    	table.addFK("FK_2");
    	table.addUC("UC_1");
    	/*
    		CREATE FOREIGN TABLE Properties (
    			Name string(4000) NOT NULL,
    			"Value" string(4000) NOT NULL,
    			UID string(50) NOT NULL,
    			ClobValue clob(2097152),
    			CONSTRAINT UC_1 UNIQUE(UID, Name)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("Properties");
    	table.addColumn("Name", "string(4000)");
    	table.addColumn("\"Value\"", "string(4000)");
    	table.addColumn("UID", "string(50)");
    	table.addColumn("ClobValue", "clob(2097152)");
    	table.addUC("UC_1");
    	
    	/*
    		CREATE FOREIGN TABLE ReferenceKeyColumns (
    			PKTABLE_CAT string(255),
    			PKTABLE_SCHEM string(255),
    			PKTABLE_NAME string(255),
    			PKCOLUMN_NAME string(255),
    			FKTABLE_CAT string(255),
    			FKTABLE_SCHEM string(255),
    			FKTABLE_NAME string(255),
    			FKCOLUMN_NAME string(255),
    			KEY_SEQ short,
    			UPDATE_RULE integer,
    			DELETE_RULE integer,
    			FK_NAME string(255),
    			PK_NAME string(255),
    			DEFERRABILITY integer,
    			FK_UID string(50)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	
    	table = model.addTable("ReferenceKeyColumns");
    	table.addColumn("PKTABLE_CAT", "string(255)");
    	table.addColumn("PKTABLE_SCHEM", "string(255)");
    	table.addColumn("PKTABLE_NAME", "string(255)");
    	table.addColumn("PKCOLUMN_NAME", "string(255)");
    	table.addColumn("FKTABLE_CAT", "string(255)");
    	table.addColumn("FKTABLE_SCHEM", "string(255)");
    	table.addColumn("FKTABLE_NAME", "string(255)");
    	table.addColumn("FKCOLUMN_NAME", "string(255)");
    	table.addColumn("KEY_SEQ", "short");
    	table.addColumn("UPDATE_RULE", "integer");
    	table.addColumn("DELETE_RULE", "integer");
    	table.addColumn("FK_NAME", "string(255)");
    	table.addColumn("PK_NAME", "string(255)");
    	table.addColumn("DEFERRABILITY", "integer");
    	table.addColumn("FK_UID", "string(50)");
    	/*
    		CREATE FOREIGN TABLE Schemas (
    			VDBName string(255),
    			Name string(255),
    			IsPhysical boolean NOT NULL,
    			UID string(50) NOT NULL,
    			Description string(255),
    			PrimaryMetamodelURI string(255) NOT NULL,
    			CONSTRAINT PK_1 PRIMARY KEY(VDBName, Name),
    			CONSTRAINT UC_1 UNIQUE(UID)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("Schemas");
    	table.addColumn("VDBName", "string(255)");
    	table.addColumn("Name", "string(255)");
    	table.addColumn("IsPhysical", "boolean");
    	table.addColumn("UID", "string(250)");
    	table.addColumn("Description", "string(255)");
    	table.addColumn("PrimaryMetamodelURI", "string(255)");
    	table.addPK("PK_1");
    	table.addUC("UC_1");
    	/*
    		CREATE FOREIGN TABLE Tables (
    			VDBName string(255),
    			SchemaName string(255),
    			Name string(255) NOT NULL,
    			Type string(20) NOT NULL,
    			NameInSource string(255),
    			IsPhysical boolean NOT NULL,
    			SupportsUpdates boolean NOT NULL,
    			UID string(50) NOT NULL,
    			Cardinality integer NOT NULL,
    			Description string(255),
    			IsSystem boolean,
    			IsMaterialized boolean NOT NULL,
    			SchemaUID string(50) NOT NULL,
    			CONSTRAINT PK_1 PRIMARY KEY(VDBName, SchemaName, Name),
    			CONSTRAINT FK_2 FOREIGN KEY(SchemaUID) REFERENCES Schemas(UID),
    			CONSTRAINT FK_1 FOREIGN KEY(VDBName, SchemaName) REFERENCES Schemas(VDBName, Name),
    			CONSTRAINT UC_1 UNIQUE(UID)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	
    	table = model.addTable("Tables");
    	table.addColumn("VDBName", "string(255)");
    	table.addColumn("SchemaName", "string(255)");
    	table.addColumn("Name", "string(255)");
		table.addColumn("Type", "string(20)");
		table.addColumn("NameInSource", "string(255)");
		table.addColumn("IsPhysical", "boolean");
		table.addColumn("SupportsUpdates", "boolean");
		table.addColumn("UID", "string(50)");
		table.addColumn("Cardinality", "integer");
		table.addColumn("Description", "string(255)");
		table.addColumn("IsSystem", "boolean");
		table.addColumn("IsMaterialized", "boolean");
		table.addColumn("SchemaUID", "string(50)");
    	table.addPK("PK_1");
    	table.addFK("FK_1");
    	table.addFK("FK_2");
    	table.addUC("UC_1");
    	/*
    		CREATE FOREIGN TABLE VirtualDatabases (
    			Name string(255) NOT NULL,
    			Version string(50) NOT NULL,
    			Description string(4000),
    			CONSTRAINT PK_1 PRIMARY KEY(Name, Version)
    		) OPTIONS(UPDATABLE 'TRUE');
*/
    	table = model.addTable("VirtualDatabases");
    	table.addColumn("Name", "string(255)");
    	table.addColumn("Version", "string(50)");
    	table.addColumn("Description", "string(4000)");
    	table.addPK("PK_1");
    	
    	/*
    		CREATE FOREIGN PROCEDURE getXMLSchemas (
    			IN document string NOT NULL
    		) RETURNS
    			TABLE (
    				schema xml
    		) OPTIONS(UPDATECOUNT '1');
*/
    	proc = model.addProcedure("getXMLSchemas");
    	proc.addParameter("document", "string");
    	result = proc.addTable("result");
    	result.addColumn("schema", "xml");
    	
    	return model;
    }
    
    private Model createSYSADMINModel() {
    	Model model = new Model("SYSADMIN");
    	
/*
    	CREATE FOREIGN TABLE Usage (
    		    VDBName string(255) NOT NULL,
    		    UID string(50) NOT NULL,
    		    object_type string(50) NOT NULL,
    		    SchemaName string(255) NOT NULL,
    		    Name string(255) NOT NULL,
    		    ElementName string(255),
    		    Uses_UID string(50) NOT NULL,
    		    Uses_object_type string(50) NOT NULL,
    		    Uses_SchemaName string(255) NOT NULL,
    		    Uses_Name string(255) NOT NULL,
    		    Uses_ElementName string(255),
    		    PRIMARY KEY (UID, Uses_UID)
    		);
 */
    	Table table = model.addTable("Usage");
    	table.addColumn("Name", "string(255)");
    	table.addColumn("VDBName", "string(255)");
    	Column pk1 = table.addColumn("UID", "string(50)");
	    table.addColumn("object_type", "string(50)");
	    table.addColumn("SchemaName", "string(255)");
	    table.addColumn("Name", "string(255)");
	    table.addColumn("ElementName", "string(255)");
	    Column pk2 = table.addColumn("Uses_UID", "string(50)");
	    table.addColumn("Uses_object_type", "string(50)");
	    table.addColumn("Uses_SchemaName", "string(255)");
	    table.addColumn("Uses_Name", "string(255)");
	    table.addColumn("Uses_ElementName", "string(255)");
	    PK pk = table.addPK("Primary Key"); // PRIMARY KEY (UID, Uses_UID)
	    pk.addChild(pk1);
	    pk.addChild(pk2);
    	

/*
    		CREATE FOREIGN TABLE MatViews (
    			VDBName string(255) NOT NULL,
    			SchemaName string(255) NOT NULL,
    			Name string(255) NOT NULL,
    			TargetSchemaName string(255),
    			TargetName string,
    			Valid boolean,
    			LoadState string(255),
    			Updated timestamp,
    			Cardinality integer,
    			PRIMARY KEY (VDBName, SchemaName, Name)
    		);
*/
	    
    		table = model.addTable("MatViews");
    		pk1 = table.addColumn("VDBName", "string(255)");
    		pk2 = table.addColumn("SchemaName", "string(255)");
    		Column pk3 = table.addColumn("Name", "string(255)");
        	table.addColumn("TargetSchemaName", "string(255)");
        	table.addColumn("TargetName", "string");
        	table.addColumn("Valid", "boolean)");
        	table.addColumn("LoadState", "timestamp");
        	table.addColumn("Cardinality", "integer");
        	pk = table.addPK("Primary Key"); // PRIMARY KEY (VDBName, SchemaName, Name)
    	    pk.addChild(pk1);
    	    pk.addChild(pk2);
    	    pk.addChild(pk3);

/*
    		CREATE FOREIGN TABLE VDBResources (
    			resourcePath string(255),
    			contents blob,
    			PRIMARY KEY (resourcePath)
    		);
*/
        	
    		table = model.addTable("VDBResources");
    		pk1 = table.addColumn("resourcePath", "string(255)");
        	table.addColumn("contents", "blob");
        	table.addPK("Primary Key"); // PRIMARY KEY (resourcePath)
        	pk = table.addPK("Primary Key"); // PRIMARY KEY (VDBName, SchemaName, Name)
    	    pk.addChild(pk1);
        	
/*
    		CREATE FOREIGN TABLE Triggers (
    			VDBName string(255) NOT NULL,
    			SchemaName string(255) NOT NULL,
    			TableName string(255) NOT NULL,
    			Name string(255) NOT NULL,
    			TriggerType string(50) NOT NULL,
    			TriggerEvent string(50) NOT NULL,
    			Status string(50) NOT NULL,
    			Body clob(2097152),
    			TableUID string(50) NOT NULL,
    			PRIMARY KEY (VDBName, SchemaName, TableName, Name)
    		);
*/
    		table = model.addTable("Triggers");
    		pk1 = table.addColumn("VDBName", "string(255)");
    		pk2 = table.addColumn("SchemaName", "string(255)");
    		pk3 = table.addColumn("TableName", "string(255)");
    		Column pk4 = table.addColumn("Name", "string(255)");
        	table.addColumn("TriggerType", "string(50)");
        	table.addColumn("TriggerEvent", "string(50))");
        	table.addColumn("Status", "string(50)");
        	table.addColumn("Body", "clob(2097152)");
        	table.addColumn("TableUID", "string(50)");
        	pk = table.addPK("Primary Key"); // PRIMARY KEY (VDBName, SchemaName, TableName, Name)
    	    pk.addChild(pk1);
    	    pk.addChild(pk2);
    	    pk.addChild(pk3);
    	    pk.addChild(pk4);
/*
    		CREATE FOREIGN TABLE Views (
    		    VDBName string(255) NOT NULL,
    		    SchemaName string(255) NOT NULL,
    		    Name string(255) NOT NULL,
    		    Body clob(2097152) NOT NULL,
    		    UID string(50) NOT NULL,
    		    PRIMARY KEY (VDBName, SchemaName, Name),
    		    UNIQUE(UID)
    		);
*/
    		table = model.addTable("Views");
    		pk1 = table.addColumn("VDBName", "string(255)");
    		pk2 = table.addColumn("SchemaName", "string(255)");
    		pk3 = table.addColumn("Name", "string(255)");
    		table.addColumn("Body", "clob(2097152)");
    		Column uc1 = table.addColumn("UID", "string(50)");
        	pk = table.addPK("Primary Key"); // PRIMARY KEY (VDBName, SchemaName, Name)
    	    pk.addChild(pk1);
    	    pk.addChild(pk2);
    	    pk.addChild(pk3);
    	    UC uc = table.addUC("UNIQUE");
    	    uc.addChild(uc1);
/*
    		CREATE FOREIGN TABLE StoredProcedures (
    		    VDBName string(255) NOT NULL,
    		    SchemaName string(255) NOT NULL,
    		    Name string(255) NOT NULL,
    		    Body clob(2097152) NOT NULL,
    		    UID string(50) NOT NULL,
    		    PRIMARY KEY (VDBName, SchemaName, Name),
    		    UNIQUE(UID)
    		);
*/
    		table = model.addTable("StoredProcedures");
    		pk1 = table.addColumn("VDBName", "string(255)");
    		pk2 = table.addColumn("SchemaName", "string(255)");
    		pk3 = table.addColumn("Name", "string(255)");
    		table.addColumn("Body", "clob(2097152)");
    		uc1 = table.addColumn("UID", "string(50)");
        	pk = table.addPK("Primary Key"); // PRIMARY KEY (VDBName, SchemaName, Name)
    	    pk.addChild(pk1);
    	    pk.addChild(pk2);
    	    pk.addChild(pk3);
    	    uc = table.addUC("UNIQUE");
    	    uc.addChild(uc1);
/*
    		CREATE FOREIGN PROCEDURE isLoggable(OUT loggable boolean NOT NULL RESULT, IN level string NOT NULL DEFAULT 'DEBUG', IN context string NOT NULL DEFAULT 'org.teiid.PROCESSOR')
    		OPTIONS (UPDATECOUNT 0);
*/
        	Procedure proc = model.addProcedure("isLoggable");
        	proc.addParameter("level", "string");
        	proc.addParameter("context", "string");
        	Table result = proc.addTable("result");
        	result.addColumn("loggable", "boolean");
        	
/*
    		CREATE FOREIGN PROCEDURE logMsg(OUT logged boolean NOT NULL RESULT, IN level string NOT NULL DEFAULT 'DEBUG', IN context string NOT NULL DEFAULT 'org.teiid.PROCESSOR', IN msg object NOT NULL)
    		OPTIONS (UPDATECOUNT 0);
*/
        	proc = model.addProcedure("logMsg");
        	proc.addParameter("level", "string");
        	proc.addParameter("context", "string");
        	proc.addParameter("msg", "string");
        	result = proc.addTable("result");
        	result.addColumn("logged", "boolean");
/*
    		CREATE FOREIGN PROCEDURE refreshMatView(OUT RowsUpdated integer NOT NULL RESULT, IN ViewName string NOT NULL, IN Invalidate boolean NOT NULL DEFAULT 'false')
    		OPTIONS (UPDATECOUNT 0);
*/
        	proc = model.addProcedure("refreshMatView");
        	proc.addParameter("ViewName", "string");
        	proc.addParameter("Invalidate", "boolean");
        	result = proc.addTable("result");
        	result.addColumn("RowsUpdated", "integer");
/*
    		CREATE FOREIGN PROCEDURE refreshMatViewRow(OUT RowsUpdated integer NOT NULL RESULT, IN ViewName string NOT NULL, IN Key object NOT NULL, VARIADIC KeyOther object)
    		OPTIONS (UPDATECOUNT 1);
*/
        	proc = model.addProcedure("refreshMatViewRow");
        	proc.addParameter("ViewName", "string");
        	proc.addParameter("Key", "boolean");
        	proc.addParameter("KeyOther", "object");
        	result = proc.addTable("result");
        	result.addColumn("RowsUpdated", "integer");
/*
    		CREATE FOREIGN PROCEDURE refreshMatViewRows(OUT RowsUpdated integer NOT NULL RESULT, IN ViewName string NOT NULL, VARIADIC Key object[] NOT NULL)
    		OPTIONS (UPDATECOUNT 1);
*/
        	proc = model.addProcedure("refreshMatViewRows");
        	proc.addParameter("ViewName", "string");
        	proc.addParameter("KeyOther", "object[]");
        	result = proc.addTable("result");
        	result.addColumn("RowsUpdated", "integer");
/*
    		CREATE FOREIGN PROCEDURE setColumnStats(IN tableName string NOT NULL, IN columnName string NOT NULL, IN distinctCount long, IN nullCount long, IN max string, IN min string)
    		OPTIONS (UPDATECOUNT 0);
*/
        	proc = model.addProcedure("setColumnStats");
        	proc.addParameter("tableName", "string");
        	proc.addParameter("columnName", "string");
        	proc.addParameter("distinctCount", "long");
        	proc.addParameter("max", "string");
        	proc.addParameter("min", "string");
        	
/*
    		CREATE FOREIGN PROCEDURE setProperty(OUT OldValue clob(2097152) NOT NULL RESULT, IN UID string(50) NOT NULL, IN Name string NOT NULL, IN "Value" clob(2097152))
    		OPTIONS (UPDATECOUNT 0);
*/
        	proc = model.addProcedure("setProperty");
        	proc.addParameter("UID", "string(50)");
        	proc.addParameter("Name", "string");
        	proc.addParameter("\"Value\"", "clob(2097152)");
        	result = proc.addTable("result");
        	result.addColumn("OldValue", "clob(2097152)");
/*
    		CREATE FOREIGN PROCEDURE setTableStats(IN tableName string NOT NULL, IN cardinality long NOT NULL)
    		OPTIONS (UPDATECOUNT 0);
*/
        	proc = model.addProcedure("setTableStats");
        	proc.addParameter("tableName", "string");
        	proc.addParameter("cardinality", "object");

/*
        	CREATE FOREIGN PROCEDURE matViewStatus (
        			IN schemaName string NOT NULL,
        			IN viewName string NOT NULL
        		) RETURNS
        			TABLE (
        				TargetSchemaName string(50),
        				TargetName string(50),
        				Valid boolean,
        				LoadState string(25),
        				Updated timestamp,
        				Cardinality long,
        				LoadNumber long,
        				OnErrorAction string(25),
        				NodeName string(25)
        		) OPTIONS(UPDATECOUNT '1');
*/
        	proc = model.addProcedure("matViewStatus");
        	proc.addParameter("schemaName", "string");
        	proc.addParameter("viewName", "string");
        	result = proc.addTable("result");
        	result.addColumn("TargetSchemaName", "string(50)");
        	result.addColumn("TargetName", "string(50)");
        	result.addColumn("Valid", "boolean");
        	result.addColumn("LoadState", "string(25)");
        	result.addColumn("Updated", "timestamp");
        	result.addColumn("Cardinality", "long");
        	result.addColumn("LoadNumber", "long");
        	result.addColumn("OnErrorAction", "string(25)");
        	result.addColumn("NodeName", "string(25)");
/*
        		CREATE FOREIGN PROCEDURE loadMatView (
        			IN schemaName string NOT NULL,
        			IN viewName string NOT NULL,
        			IN invalidate boolean NOT NULL
        		) RETURNS
        			TABLE (
        				resultSet integer
        		) OPTIONS(UPDATECOUNT '1');
*/
        	proc = model.addProcedure("loadMatView");
        	proc.addParameter("schemaName", "string");
        	proc.addParameter("viewName", "string");
        	proc.addParameter("invalidate", "boolean");
        	result = proc.addTable("result");
        	result.addColumn("resultSet", "integer");
/*
        		CREATE FOREIGN PROCEDURE updateMatView (
        			IN schemaName string NOT NULL,
        			IN viewName string NOT NULL,
        			IN refreshCriteria string NOT NULL
        		) RETURNS
        			TABLE (
        				resultSet integer
        		) OPTIONS(UPDATECOUNT '1');
*/
        	proc = model.addProcedure("updateMatView");
        	proc.addParameter("schemaName", "string");
        	proc.addParameter("viewName", "string");
        	proc.addParameter("refreshCriteria", "string");
        	result = proc.addTable("result");
        	result.addColumn("resultSet", "integer");
    	return model;
    }

}
