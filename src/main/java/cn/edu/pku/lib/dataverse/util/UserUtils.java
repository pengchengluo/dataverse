/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.pku.lib.dataverse.util;

import cn.edu.pku.lib.dataverse.authorization.providers.iaaa.PKUIAAAAuthenticationProvider;
import edu.harvard.iq.dataverse.authorization.AuthenticatedUserDisplayInfo;
import edu.harvard.iq.dataverse.authorization.AuthenticationProvider;
import edu.harvard.iq.dataverse.authorization.AuthenticationServiceBean;
import edu.harvard.iq.dataverse.authorization.UserRecordIdentifier;
import edu.harvard.iq.dataverse.authorization.providers.builtin.BuiltinAuthenticationProvider;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;

/**
 *
 * @author luopc
 */
public class UserUtils {
    
    public static boolean isBuiltInUser(AuthenticatedUser user) {
        String authProviderString = user.getAuthenticatedUserLookup().getAuthenticationProviderId();
        if (authProviderString != null && authProviderString.equals(BuiltinAuthenticationProvider.PROVIDER_ID)) {
            return true;
        }
        return false;
    }
    
    public static boolean isPKUIAAAUser(AuthenticatedUser user){
        String authProviderString = user.getAuthenticatedUserLookup().getAuthenticationProviderId();
        if (authProviderString != null && authProviderString.equals(PKUIAAAAuthenticationProvider.PROVIDER_ID)) {
            return true;
        }
        return false;
    }
        
    public static AuthenticatedUser createAuthUserForPKUIAAAUser(String username,
            AuthenticatedUserDisplayInfo udi,
            AuthenticationServiceBean authenticationService,
            AuthenticationProvider prv,
            AuthenticatedUser user){
        if(user != null || !prv.getId().equals(PKUIAAAAuthenticationProvider.PROVIDER_ID))
            return user;
        udi.setUserType(AuthenticatedUser.UserType.ORDINARY);
        return authenticationService.createAuthenticatedUser(
                new UserRecordIdentifier(PKUIAAAAuthenticationProvider.PROVIDER_ID, username),
                username, udi, false);
    }
    
}
