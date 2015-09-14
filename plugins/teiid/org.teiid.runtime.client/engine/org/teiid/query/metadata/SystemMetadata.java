/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.teiid.query.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.PropertiesUtils;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeAliases;
import org.teiid.metadata.Datatype;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.MetadataStore;
import org.teiid.metadata.Table;
import org.teiid.query.function.SystemFunctionManager;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.validator.ValidatorReport;
import org.teiid.runtime.client.Messages;

public class SystemMetadata {
	
	private static String JAVA_PATH_SEPARATOR = "/"; //$NON-NLS-1$
	
	private static Map<ITeiidServerVersion, SystemMetadata> instances = new HashMap<ITeiidServerVersion, SystemMetadata>();
	
	/**
	 * @param teiidVersion
	 * @return get singleton instance keyed on given teiid version
	 */
	public static SystemMetadata getInstance(ITeiidServerVersion teiidVersion) {
		SystemMetadata instance = instances.get(teiidVersion);
		if (instance == null) {
		    instance = new SystemMetadata(teiidVersion);
		    instances.put(teiidVersion, instance);
		}

		return instance;
	}

	private final ITeiidServerVersion teiidVersion;
	private final DataTypeManagerService dataTypeManager;
	private List<Datatype> dataTypes = new ArrayList<Datatype>();
	private Map<String, Datatype> typeMap = new TreeMap<String, Datatype>(String.CASE_INSENSITIVE_ORDER);
	private MetadataStore systemStore;

	/**
	 * @param teiidVersion
	 */
	public SystemMetadata(ITeiidServerVersion teiidVersion) {
	    if (teiidVersion.isLessThan(Version.TEIID_8_0.get()))
	        throw new UnsupportedOperationException(Messages.getString(Messages.Misc.TeiidVersionFailure, this.getClass().getSimpleName(), teiidVersion));

		this.teiidVersion = teiidVersion;
		this.dataTypeManager = DataTypeManagerService.getInstance(teiidVersion);

		String baseResourceLocation = this.getClass().getPackage().getName();
        baseResourceLocation = StringUtil.replaceAll(baseResourceLocation, StringUtil.Constants.DOT, JAVA_PATH_SEPARATOR);
        baseResourceLocation = baseResourceLocation + JAVA_PATH_SEPARATOR;

        InputStream is = getResource(baseResourceLocation, "types.dat", teiidVersion); //$NON-NLS-1$
		try {
			InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8")); //$NON-NLS-1$
			BufferedReader br = new BufferedReader(isr);
			String s = br.readLine();
			String[] props = s.split("\\|"); //$NON-NLS-1$
			while ((s = br.readLine()) != null) {
				Datatype dt = new Datatype();
				String[] vals = s.split("\\|"); //$NON-NLS-1$
				Properties p = new Properties();
				for (int i = 0; i < props.length; i++) {
					if (vals[i].length() != 0) {
						p.setProperty(props[i], new String(vals[i]));
					}
				}
				PropertiesUtils.setBeanProperties(dt, p, null);
				if ("string".equals(dt.getName())) { //$NON-NLS-1$
					dt.setLength(DataTypeManagerService.MAX_STRING_LENGTH);
				} else if ("varbinary".equals(dt.getName())) { //$NON-NLS-1$
					dt.setLength(DataTypeManagerService.MAX_LOB_MEMORY_BYTES);
				}
				dataTypes.add(dt);
				if (dt.isBuiltin()) {
					typeMap.put(dt.getRuntimeTypeName(), dt);
				}
			}
			is.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		addAliasType(DataTypeManagerService.DataTypeAliases.BIGINT);
		addAliasType(DataTypeManagerService.DataTypeAliases.DECIMAL);
		addAliasType(DataTypeManagerService.DataTypeAliases.REAL);
		addAliasType(DataTypeManagerService.DataTypeAliases.SMALLINT);
		addAliasType(DataTypeManagerService.DataTypeAliases.TINYINT);
		addAliasType(DataTypeManagerService.DataTypeAliases.VARCHAR);
		for (String name : dataTypeManager.getAllDataTypeNames()) {
			if (!name.equals(DefaultDataTypes.NULL.getId())) {
				ArgCheck.isNotNull(typeMap.get(name), name);
			}
		}
		
		VDBMetaData vdb = new VDBMetaData();
		vdb.setName("System");  //$NON-NLS-1$
		vdb.setVersion(1);
		Properties p = new Properties();
		QueryParser parser = new QueryParser(teiidVersion);
		systemStore = loadSchema(vdb, p, baseResourceLocation, "SYS", parser).asMetadataStore(); //$NON-NLS-1$
		systemStore.addDataTypes(dataTypes);
		loadSchema(vdb, p, baseResourceLocation, "SYSADMIN", parser).mergeInto(systemStore); //$NON-NLS-1$
		SystemFunctionManager systemFunctionManager = new SystemFunctionManager(teiidVersion, getClass().getClassLoader(), typeMap);
        TransformationMetadata tm = new TransformationMetadata(parser.getTeiidParser(), vdb, new CompositeMetadataStore(systemStore), null, systemFunctionManager.getSystemFunctions(), null);
        vdb.addAttchment(IQueryMetadataInterface.class, tm);
		MetadataValidator validator = new MetadataValidator(this.teiidVersion, this.typeMap);
		ValidatorReport report = validator.validate(vdb, systemStore);
		if (report.hasItems()) {
			throw new RuntimeException(report.getFailureMessage());
		}
	}

    private InputStream getResource(String location, String name, ITeiidServerVersion teiidVersion) {
        

        int major = 8;
        int minor = 0;
        try {
            major = Integer.parseInt(teiidVersion.getMajor());
            minor = Integer.parseInt(teiidVersion.getMinor());
        } catch (NumberFormatException ex) {
            // Nothing required
        }

        InputStream is = null;
        for (int i = major; i > 7; --i) {
            if (is != null)
                break;

            for (int j = minor; j >= 0; --j) {
                if (is != null)
                    break;

                String versionPkg = "v" + i + j + JAVA_PATH_SEPARATOR; //$NON-NLS-1$
                String resource = location + versionPkg + name;
                is = SystemMetadata.class.getClassLoader().getResourceAsStream(resource);
            }            
        }
        
        if (is == null) {
            // default to 8.0.0
            String resource = location + "v800" + JAVA_PATH_SEPARATOR + name; //$NON-NLS-1$
            is = SystemMetadata.class.getClassLoader().getResourceAsStream(resource);
        }

        return is;
    }

    private MetadataFactory loadSchema(VDBMetaData vdb, Properties p, String resourceLocation, String name, QueryParser parser) {
		ModelMetaData mmd = new ModelMetaData();
		mmd.setName(name);
		vdb.addModel(mmd);
		InputStream is = getResource(resourceLocation, name + ".sql", teiidVersion); //$NON-NLS-1$
		try {
			MetadataFactory factory = new MetadataFactory(teiidVersion, vdb.getName(), vdb.getVersion(), name, typeMap, p, null);
			parser.parseDDL(factory, new InputStreamReader(is, Charset.forName("UTF-8"))); //$NON-NLS-1$
			for (Table t : factory.getSchema().getTables().values()) {
				t.setSystem(true);
			}
			return factory;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void addAliasType(DataTypeAliases alias) {
	    DefaultDataTypes dataType = dataTypeManager.getDataType(alias);
		String primaryType = dataType.getId();
		Datatype dt = typeMap.get(primaryType);
		ArgCheck.isNotNull(dt, alias.getId());
		typeMap.put(alias.getId(), dt);
	}

	/**
	 * List of all "built-in" datatypes.  Note that the datatype names do not necessarily match the corresponding runtime type names i.e. int vs. integer
	 * @return
	 */
	public List<Datatype> getDataTypes() {
		return dataTypes;
	}
	
	/**
	 * Map of runtime types and aliases to runtime datatypes
	 * @return
	 */
	public Map<String, Datatype> getRuntimeTypeMap() {
		return typeMap;
	}
	
	public MetadataStore getSystemStore() {
		return systemStore;
	}
}
