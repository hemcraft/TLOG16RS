/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.core.tlog16java;

import com.HuszarAndras.tlog16rs.TLOG16RSConfiguration;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import java.io.FileNotFoundException;
import java.io.IOException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import com.HuszarAndras.tlog16rs.entities.TimeLogger;
import com.HuszarAndras.tlog16rs.entities.WorkDay;
import com.HuszarAndras.tlog16rs.entities.WorkMonth;
import com.HuszarAndras.tlog16rs.entities.Task;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 *
 * @author Andris
 */
public class CreateDatabase {
    private DataSourceConfig dataSourceConfig;
    private ServerConfig serverConfig;
    private EbeanServer ebeanServer;
    
    public CreateDatabase(TLOG16RSConfiguration config) throws LiquibaseException, FileNotFoundException, IOException{
        
        serverConfig = new ServerConfig();
        serverConfig.setName(config.getServerconfig());
        
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(config.getDriver());
        dataSourceConfig.setUrl(config.getUrl());
        dataSourceConfig.setUsername(config.getUsername());     
        dataSourceConfig.setPassword(config.getPassword());
        
         
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false); 
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TimeLogger.class); 
        serverConfig.addClass(WorkMonth.class); 
        serverConfig.addClass(WorkDay.class); 
        serverConfig.addClass(Task.class); 
        serverConfig.setDefaultServer(true);
        
        ebeanServer = EbeanServerFactory.create(serverConfig);
        
        updateSchema();
    }
    
    private void updateSchema() throws  LiquibaseException{
       
        dataSourceConfig.setDriver("org.mariadb.jdbc.Driver");
        java.sql.Connection c = Ebean.beginTransaction().getConnection();
        Liquibase liquibase = null;
        
        try{
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
        liquibase = new Liquibase("migration.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update("");
        }catch(LiquibaseException e){           
        }
        
        Ebean.endTransaction();
        
    }
}
