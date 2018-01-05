package cn.edu.pku.lib.dataverse.authorization.providers.iaaa;

import edu.harvard.iq.dataverse.authorization.AuthenticationProviderDisplayInfo;
import edu.harvard.iq.dataverse.authorization.AuthenticationRequest;
import edu.harvard.iq.dataverse.authorization.AuthenticationResponse;
import edu.harvard.iq.dataverse.authorization.CredentialsAuthenticationProvider;
import java.util.Arrays;
import java.util.List;
import static edu.harvard.iq.dataverse.authorization.CredentialsAuthenticationProvider.Credential;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import java.util.logging.Logger;

/**
 * 
 * @author luopc
 * @version 1.0
 */
public class PKUIAAAAuthenticationProvider implements CredentialsAuthenticationProvider{
    
    private static final Logger logger = Logger.getLogger(PKUIAAAAuthenticationProvider.class.getCanonicalName());
    
    public static final String PROVIDER_ID = "pkuiaaa";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_RAND = "rand";
    private static final List<Credential> CREDENTIALS_LIST = 
            Arrays.asList(new Credential(KEY_TOKEN),
                    new Credential(KEY_RAND));
      
    final PKUIAAAUserServiceBean bean;

    public PKUIAAAAuthenticationProvider( PKUIAAAUserServiceBean bean ) {
        this.bean = bean;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public AuthenticationProviderDisplayInfo getInfo() {
        return new AuthenticationProviderDisplayInfo(getId(), "PKU IAAA Login Provider", "PKU University internal user repository");
    }

    @Override
    public AuthenticationResponse authenticate( AuthenticationRequest authReq ) {
        String remoteAddr = authReq.getIpAddress().toString();
        String token = authReq.getCredential(KEY_TOKEN);
        PKUIAAAUser user = new PKUIAAAUser();
        try{
            PKUIAAAResult iaaaResult = PKUIAAAValidation.validateToken(remoteAddr, token);
            user.setUserName(iaaaResult.getLogonID());
            if(iaaaResult.getUserType().equals(PKUIAAAResult.USER_TYPE_STUDENT)){
                PKUTpubResult tpubResult = PKUTpub.getSinglePerson(user.getUserName());
                user.setSpeciality(tpubResult.getSpeciality());
            }
            String name = iaaaResult.getName();
            user.setLastName(name.substring(0,1));
            user.setFirstName(name.substring(1));
            user.setEmail(user.getUserName()+"@pku.edu.cn");
            user.setAffiliation("北京大学");
            user.setDepartment(iaaaResult.getDept());
            if(iaaaResult.getUserType().equals("职工"))
                user.setPosition("faculty");
            else if(iaaaResult.getUserType().equals("学生"))
                user.setPosition("student");
            else 
                user.setPosition("other");
            PKUIAAAUser foundUser = bean.findByUserName(user.getUserName());
            if(foundUser == null){
                user.setUserType(AuthenticatedUser.UserType.ORDINARY);
                bean.save(user);
            }else{
                user = foundUser;
            }
        }catch(PKUIAAAException ex){
            return AuthenticationResponse.makeFail("PKU IAAA authentication error");
        }
        return AuthenticationResponse.makeSuccess(user.getUserName(), user.getDisplayInfo());
   }

    @Override
    public List<Credential> getRequiredCredentials() {
        return CREDENTIALS_LIST;
    }
}
