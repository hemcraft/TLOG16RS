/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.core.tlog16java;

import com.HuszarAndras.tlog16rs.entities.TestEntity;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

/**
 *
 * @author Andris
 */
public class CreateDatabase {
    private DataSourceConfig dataSourceConfig;
    private ServerConfig serverConfig;
    private EbeanServer ebeanServer;
    
    public CreateDatabase() throws LiquibaseException, FileNotFoundException, IOException{
        FileInputStream propFile =
            new FileInputStream("C:/Users/andris/Documents/NetBeansProjects/TLOG16RS/myProperties.txt");
        Properties p =
            new Properties(System.getProperties());
        p.load(propFile);

        System.setProperties(p);
        
        serverConfig = new ServerConfig();
        serverConfig.setName(System.getProperty("database.serverconfig"));
        
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(System.getProperty("database.driver"));
        dataSourceConfig.setUrl(System.getProperty("database.url"));
        dataSourceConfig.setUsername(System.getProperty("database.username"));
        dataSourceConfig.setPassword(System.getProperty("database.password"));
        
        //serverConfig.setDdlGenerate(true);
        //serverConfig.setDdlRun(true); 
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false); 
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TestEntity.class); 
        serverConfig.setDefaultServer(true);
        
        ebeanServer = EbeanServerFactory.create(serverConfig);
        
        updateSchema();
    }
    
    private void updateSchema() throws DatabaseException, LiquibaseException{
       
        dataSourceConfig.setDriver("org.mariadb.jdbc.Driver");
        java.sql.Connection c = Ebean.beginTransaction().getConnection();
        Liquibase liquibase = null;
        
        try{
           System.out.println("itt vagyok");
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
        liquibase = new Liquibase("C:/Users/andris/Documents/NetBeansProjects/TLOG16RS/src/main/resources/migration.xml", new FileSystemResourceAccessor(), database);
        liquibase.update("");
        }catch(LiquibaseException e){
            
        }
        
        Ebean.endTransaction();
    }
}
