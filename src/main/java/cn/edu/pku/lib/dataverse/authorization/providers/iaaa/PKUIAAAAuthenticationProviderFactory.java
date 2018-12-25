package cn.edu.pku.lib.dataverse.authorization.providers.iaaa;

import edu.harvard.iq.dataverse.authorization.AuthenticationProvider;
import edu.harvard.iq.dataverse.authorization.exceptions.AuthorizationSetupException;
import edu.harvard.iq.dataverse.authorization.providers.AuthenticationProviderFactory;
import edu.harvard.iq.dataverse.authorization.providers.AuthenticationProviderRow;

/**
 * 
 * @author luopc
 * @version 1.0
 */
public class PKUIAAAAuthenticationProviderFactory implements AuthenticationProviderFactory {
    
    private final PKUIAAAAuthenticationProvider provider;

    public PKUIAAAAuthenticationProviderFactory() {
        provider = new PKUIAAAAuthenticationProvider();
    }
    
    @Override
    public String getAlias() {
        return "PKUIAAAAuthenticationProvider";
    }

    @Override
    public String getInfo() {
        return "PKUIAAAAuthenticationProvider - the provider bundled with Peking University";
    }

    @Override
    public AuthenticationProvider buildProvider(AuthenticationProviderRow aRow) throws AuthorizationSetupException {
        return provider;
    }
    
}
