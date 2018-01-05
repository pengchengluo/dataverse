package cn.edu.pku.lib.dataverse.authorization.providers.iaaa;

import edu.harvard.iq.dataverse.DataverseRequestServiceBean;
import edu.harvard.iq.dataverse.DataverseSession;
import edu.harvard.iq.dataverse.authorization.AuthenticationRequest;
import edu.harvard.iq.dataverse.authorization.AuthenticationServiceBean;
import edu.harvard.iq.dataverse.authorization.exceptions.AuthenticationFailedException;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import edu.harvard.iq.dataverse.engine.command.DataverseRequest;
import static edu.harvard.iq.dataverse.util.JsfHelper.JH;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author luopc
 * @author Michael Bar-Sinai
 */
@ViewScoped
@Named("PKUIAAALoginPage")
public class PKUIAAALoginPage implements java.io.Serializable {
    private static final Logger logger = Logger.getLogger(PKUIAAALoginPage.class.getName());
    
    private String token;
    private String rand;
    
    @Inject
    DataverseSession session; 
    
    @Inject
    DataverseRequestServiceBean dvRequestService;
    
    @EJB
    AuthenticationServiceBean authSvc;
    
    private final String credentialsAuthProviderId = "pkuiaaa";
    
    private String redirectPage = "dataverse.xhtml";

    public String login() {
        AuthenticationRequest authReq = new AuthenticationRequest();
        DataverseRequest dvRequest = dvRequestService.getDataverseRequest();
        authReq.putCredential("token", token);
        authReq.putCredential("rand", rand);
        authReq.setIpAddress(dvRequest.getSourceAddress());
        try {
            AuthenticatedUser r = authSvc.getCreateAuthenticatedUser(credentialsAuthProviderId, authReq);
            logger.log(Level.INFO, "User authenticated: {0}", r.getEmail());
            session.setUser(r);
            try {
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                Object redirectUrl = request.getSession().getAttribute("loginRedirectURL");
                if(redirectUrl != null && !((String)redirectUrl).contains("loginpage.xhtml")){
                    redirectPage = (String)redirectUrl;
                }
                redirectPage = URLDecoder.decode(redirectPage, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.log(Level.SEVERE, null, ex);
                redirectPage = "dataverse.xhtml";
            }
            logger.log(Level.INFO, () -> "Sending user to = " + redirectPage);
            return redirectPage + (redirectPage.indexOf("?") == -1 ? "?" : "&") + "faces-redirect=true";
        } catch (AuthenticationFailedException ex) {
            JH.addMessage(FacesMessage.SEVERITY_ERROR, "The username and/or password you entered is invalid. Contact support@dataverse.org if you need assistance accessing your account.", ex.getResponse().getMessage());
            return null;
        }
        
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRand() {
        return rand;
    }

    public void setRand(String rand) {
        this.rand = rand;
    }    
}
