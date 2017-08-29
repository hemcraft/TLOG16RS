package com.HuszarAndras.tlog16rs;

import com.HuszarAndras.tlog16rs.core.tlog16java.CreateDatabase;
import com.HuszarAndras.tlog16rs.resources.TLOG16RSResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.io.IOException;
import liquibase.exception.LiquibaseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TLOG16RSApplication extends Application<TLOG16RSConfiguration> {

    public static void main(final String[] args) throws Exception {
        try{
            new TLOG16RSApplication().run(args);
        }catch(Exception e){
            log.error("error");
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "TLOG16RS";
    }

    @Override
    public void initialize(final Bootstrap<TLOG16RSConfiguration> bootstrap) {
    }

    @Override
    public void run(final TLOG16RSConfiguration configuration,
                    final Environment environment) throws LiquibaseException, IOException {
        CreateDatabase createDatabase = new CreateDatabase(configuration);
        environment.jersey().register(new TLOG16RSResource());
    }

}
