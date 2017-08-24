package com.HuszarAndras.tlog16rs;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

@lombok.Getter
@lombok.Setter
public class TLOG16RSConfiguration extends Configuration {
    @NotEmpty
    protected String driver;
    @NotEmpty
    protected String url;
    @NotEmpty
    protected String username;
    @NotEmpty
    protected String password;
    @NotEmpty
    protected String serverconfig;
}
