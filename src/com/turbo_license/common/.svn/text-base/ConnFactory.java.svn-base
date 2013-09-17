/*
 * ConnFactory.java
 *
 * Created on August 14, 2003, 2:24 PM
 */

package com.turbo_license.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 
 * @author Administrator
 */
public class ConnFactory {

	static final String DB_FILE = "db.xml";

	public static String SERIAL_CALL;

	static Map dbMap = new Hashtable(10);

	static Logger logger = Logger.getLogger(ConnFactory.class);

	static int cleanCounter = 0;

	static {

		Class[] constrTypeArray = new Class[0];
		Object[] constrValueArray = new Object[0];
		Class[] setMethArray = new Class[] { String.class };

		try {
			logger.debug("Building DataSources...");
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(ConnFactory.class.getClassLoader().getResourceAsStream(DB_FILE));
			SERIAL_CALL = doc.getRootElement().getAttributeValue("serial_call");

			Iterator dbs = doc.getRootElement().getChildren().iterator();
			while (dbs.hasNext()) {

				// RETRIEVE THE VALUES OF THIS JDBC CONNECTION FROM
				// THE XML FILE
				Element el = (Element) dbs.next();
				String id = el.getAttributeValue("id");
				String url = el.getAttributeValue("url");
				String user = el.getAttributeValue("user");
				String pass = el.getAttributeValue("pass");
				String serverName = el.getAttributeValue("server_name");
				String dbName = el.getAttributeValue("db_name");
				String dataSourceClass = el.getAttributeValue("data_source_class");

				// CREATE AND INVOKE THE CONSTRUCTOR OF THIS JDBC'S
				// DATASOURCE CLASS
				Class dsClass = Class.forName(dataSourceClass);
				Constructor constr = dsClass.getConstructor(constrTypeArray);
				DataSource dataSource = (DataSource) constr.newInstance(constrValueArray);

				// SET THE ATTRIBUTES OF THIS CONNECTION
				// USE REFLECTION BECAUSE WE DON'T KNOW WHICH
				// DATA SOURCE CLASS WE'RE DEALING WITH.
				// AND NO, CURRENT VERSION OF JAVAX.SQL.DATASOURCE
				// DOES NOT CONTAIN THESE METHODS...				
				if (dataSourceClass.contains("postgresql")) {
					Method setServerNameMeth = dsClass.getMethod("setServerName", setMethArray);
					Method setDatabaseNameMeth = dsClass.getMethod("setDatabaseName", setMethArray);
					Method setUserMeth = dsClass.getMethod("setUser", setMethArray);
					Method setPassMeth = dsClass.getMethod("setPassword", setMethArray);
					setServerNameMeth.invoke(dataSource, serverName);
					setDatabaseNameMeth.invoke(dataSource, dbName);
					setUserMeth.invoke(dataSource, user);
					setPassMeth.invoke(dataSource, pass);
				} else {
					Method setUrlMeth = dsClass.getMethod("setURL", setMethArray);
					Method setUserMeth = dsClass.getMethod("setUser", setMethArray);
					Method setPassMeth = dsClass.getMethod("setPassword", setMethArray);
					setUrlMeth.invoke(dataSource, url);
					setUserMeth.invoke(dataSource, user);
					setPassMeth.invoke(dataSource, pass);
				}

				// PLACE THE DATASOURCE IN THE MAP FOR RE-USE
				dbMap.put(id, dataSource);
				logger.debug("DB '" + id + "' created to " + (url == null ? serverName + " " + dbName : url));
			}
		} catch (Exception e) {
			logger.error("Error building datasources: " + e.getMessage(), e);
		}
	}

	public static synchronized SqlConn getConnection(String db) throws SQLException {

		// if (cleanCounter++ > 100) {
		//	ConMap.clean(System.currentTimeMillis() - (1000l * 60l * 3l));
		//	logger.debug("cleaning connections");
		//	cleanCounter = 0;
		// }

		SqlConn con = new SqlConn((DataSource) dbMap.get(db));
		// ConMap.map.put(System.currentTimeMillis(), con);
		return con;
	}

	public static DataSource getDataSource(String db) {
		return (DataSource) dbMap.get(db);
	}

}
