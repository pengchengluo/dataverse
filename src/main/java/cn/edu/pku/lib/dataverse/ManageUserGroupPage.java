/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.pku.lib.dataverse;

import cn.edu.pku.lib.dataverse.authorization.providers.iaaa.PKUIAAAUser;
import cn.edu.pku.lib.dataverse.util.UserUtils;
import edu.harvard.iq.dataverse.Dataverse;
import edu.harvard.iq.dataverse.DataverseHeaderFragment;
import edu.harvard.iq.dataverse.DataverseRequestServiceBean;
import edu.harvard.iq.dataverse.DataverseSession;
import edu.harvard.iq.dataverse.DvObject;
import edu.harvard.iq.dataverse.DvObjectServiceBean;
import edu.harvard.iq.dataverse.EjbDataverseEngine;
import edu.harvard.iq.dataverse.NavigationWrapper;
import edu.harvard.iq.dataverse.PermissionServiceBean;
import edu.harvard.iq.dataverse.UserNotification;
import edu.harvard.iq.dataverse.UserNotificationServiceBean;
import static edu.harvard.iq.dataverse.actionlogging.ActionLogRecord.ActionType.BuiltinUser;
import edu.harvard.iq.dataverse.authorization.AuthenticationServiceBean;
import edu.harvard.iq.dataverse.authorization.Permission;
import edu.harvard.iq.dataverse.authorization.RoleAssignee;
import edu.harvard.iq.dataverse.authorization.groups.Group;
import edu.harvard.iq.dataverse.authorization.groups.GroupException;
import edu.harvard.iq.dataverse.authorization.groups.GroupServiceBean;
import edu.harvard.iq.dataverse.authorization.groups.impl.explicit.ExplicitGroup;
import edu.harvard.iq.dataverse.authorization.groups.impl.explicit.ExplicitGroupServiceBean;
import edu.harvard.iq.dataverse.authorization.providers.builtin.BuiltinUser;
import edu.harvard.iq.dataverse.authorization.providers.builtin.BuiltinUserServiceBean;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser.UserType;
import edu.harvard.iq.dataverse.engine.command.exception.CommandException;
import edu.harvard.iq.dataverse.engine.command.impl.UpdateExplicitGroupCommand;
import edu.harvard.iq.dataverse.util.JsfHelper;
import static edu.harvard.iq.dataverse.util.JsfHelper.JH;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author luopc
 */
@ViewScoped
@Named
public class ManageUserGroupPage implements Serializable {

    private static final Logger logger = Logger.getLogger(ManageUserGroupPage.class.getCanonicalName());

    @EJB
    DvObjectServiceBean dvObjectService;
    @EJB
    PermissionServiceBean permissionService;
    @EJB
    GroupServiceBean groupService;
    @EJB
    ExplicitGroupServiceBean explicitGroupService;
    @EJB
    AuthenticationServiceBean authenticationServiceBean;
    @EJB
    EjbDataverseEngine commandEngine;
    @EJB
    GroupServiceBean groupServiceBean;
    @EJB
    UserNotificationServiceBean userNotificationService;
    @Inject
    DataverseSession session;
    @EJB
    BuiltinUserServiceBean builtinUserService;
    @EJB
    cn.edu.pku.lib.dataverse.usage.EventBuilder eventBuilder;
    @EJB
    cn.edu.pku.lib.dataverse.usage.UsageIndexServiceBean usageIndexService;
    @Inject
    NavigationWrapper navigationWrapper;
    @Inject
    DataverseRequestServiceBean dvRequestService;

    private ExplicitGroup explicitGroup;
    private Long groupId;

    private DvObject dvObject = new Dataverse();

    private String searchIdentifier = "";
    private List<AuthenticatedUser> searchUsers = Collections.EMPTY_LIST;
    private AuthenticatedUser viewedUser;
    
    private String rejectReason;
    private String otherRejectReason;
    private AuthenticatedUser rejectedUser;

    public String init() {
        if (groupId != null) {
            explicitGroup = explicitGroupService.findById(groupId);
            if (explicitGroup != null) {
                dvObject = (Dataverse) explicitGroup.getOwner();
                if (permissionService.on(dvObject).has(Permission.ManageDataversePermissions)) {
                    rejectReason = "other";
                    return "";
                } else {
                    return "/loginpage.xhtml" + navigationWrapper.getRedirectPage();
                }
            }
        }
        return "/404.xhtml";
    }

    public DvObject getDvObject() {
        return dvObject;
    }

    public void setDvObject(DvObject dvObject) {
        this.dvObject = dvObject;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<AuthenticatedUser> getAuthenticatedUsers() {
        Set<AuthenticatedUser> authUsersSet = explicitGroup.getContainedAuthenticatedUsers();
        List<AuthenticatedUser> authUsers = new ArrayList<AuthenticatedUser>();
        for (AuthenticatedUser user : authUsersSet) {
            authUsers.add(user);
        }
        return authUsers;
    }
    
    public void downloadAuthenticatedUsers(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
        File file = generateExcelRequestJoinGroupLogFile();
        if(file == null){
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            return ;
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=\"user_member.xlsx\"");
        response.setContentLength((int)file.length());
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                ){
            byte[] buffer = new byte[1024*4];
            int length;
            while((length = in.read(buffer))>0){
                out.write(buffer,0,length);
            }
        }catch(IOException ioe){
            logger.log(Level.SEVERE, null, ioe);
        }
        context.responseComplete();
        if(file.exists())file.delete();
    }
    
    private File generateExcelRequestJoinGroupLogFile(){
        //excel workbook
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("Groups' user member"));
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        
        //generate header
        String heads = ResourceBundle.getBundle("Bundle", locale).getString("dataverse.permissions.groups.member.header");
        String[] array = heads.split(",");
        Row row = sheet.createRow(0);
        for(int k=0; k<array.length; k++){
            Cell cell = row.createCell(k);
            cell.setCellValue(array[k]);
        }
        
        //generate logs
        Set<AuthenticatedUser> authUsersSet = explicitGroup.getContainedAuthenticatedUsers();
        int j=1;
        Cell cell;
        for(AuthenticatedUser user : authUsersSet){
            row = sheet.createRow(j);
            cell = row.createCell(0);cell.setCellValue(user.getUserIdentifier());
            cell = row.createCell(1);cell.setCellValue(user.getLastName());
            cell = row.createCell(2);cell.setCellValue(user.getFirstName());
            cell = row.createCell(3);
            if(user.getUserType()==UserType.ORDINARY)cell.setCellValue("ORDINARY");
            else if(user.getUserType()==UserType.ADVANCE)cell.setCellValue("ADVANCE");
            else cell.setCellValue("");
            cell = row.createCell(4);cell.setCellValue(user.getAffiliation());
            cell = row.createCell(5);cell.setCellValue(user.getPosition());               
            cell = row.createCell(6);cell.setCellValue(user.getDepartment());
            cell = row.createCell(7);cell.setCellValue(user.getEmail());
            cell = row.createCell(8);cell.setCellValue(user.getSpeciality());
            cell = row.createCell(9);cell.setCellValue(user.getResearchInterest());
            cell = row.createCell(10);cell.setCellValue(user.getGender());
            cell = row.createCell(11);cell.setCellValue(user.getEducation());
            cell = row.createCell(12);cell.setCellValue(user.getProfessionalTitle());
            cell = row.createCell(13);cell.setCellValue(user.getSupervisor());
            cell = row.createCell(14);cell.setCellValue(user.getCertificateType());
            cell = row.createCell(15);cell.setCellValue(user.getCertificateNumber());
            cell = row.createCell(16);cell.setCellValue(user.getOfficePhone());
            cell = row.createCell(17);cell.setCellValue(user.getCellphone());
            cell = row.createCell(18);cell.setCellValue(user.getOtherEmail());
            cell = row.createCell(19);cell.setCellValue(user.getCountry());
            cell = row.createCell(20);cell.setCellValue(user.getProvince());
            cell = row.createCell(21);cell.setCellValue(user.getCity());
            cell = row.createCell(22);cell.setCellValue(user.getAddress());
            cell = row.createCell(23);cell.setCellValue(user.getZipCode());
            if(UserUtils.isBuiltInUser(user)){
                cell = row.createCell(24);cell.setCellValue("Built In");
            }else if(UserUtils.isPKUIAAAUser(user)){
                cell = row.createCell(24);cell.setCellValue("PKU IAAA");
            }
            j++;
        }
        
        String filesRootDirectory = System.getProperty("dataverse.files.directory");
        if (filesRootDirectory == null || filesRootDirectory.equals("")) {
            filesRootDirectory = "/tmp/files";
        }
        File file = new File(filesRootDirectory + "/temp/" + UUID.randomUUID());
        try(FileOutputStream out = new FileOutputStream(file)){
            wb.write(out);
            return file;
        }catch(IOException ioe){
            logger.log(Level.SEVERE, null ,ioe);
        }
        if(file.exists()){
            file.delete();
        }
        return null;
    }

    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<>();
        Set<String> groupIDs = explicitGroup.getContainedRoleAssignees();
        for (String groupID : groupIDs) {
            Group group = groupServiceBean.getNoneExplicitGroupByIdentifier(groupID);
            if (group != null) {
                groups.add(group);
            }
        }
        for (ExplicitGroup expGroup : explicitGroup.getContainedExplicitGroups()) {
            expGroup.setProvider(explicitGroupService.getProvider());
            groups.add(expGroup);
        }
        return groups;
    }

    public List<Group> getAvaliableGroups() {
        List<Group> groups = new ArrayList<>();
        groups.addAll(groupService.findGlobalGroups());
        for (ExplicitGroup group : explicitGroupService.findAvailableFor(dvObject)) {
            if (group.getId() != this.groupId) {
                groups.add(group);
            }
        }
        return groups;
    }

    public ExplicitGroup getExplicitGroup() {
        return explicitGroup;
    }

    public void setExplicitGroup(ExplicitGroup explicitGroup) {
        this.explicitGroup = explicitGroup;
    }

    public String getSearchIdentifier() {
        return searchIdentifier;
    }

    public void setSearchIdentifier(String searchIdentifier) {
        this.searchIdentifier = searchIdentifier;
    }

    public List<AuthenticatedUser> getSearchUsers() {
        return searchUsers;
    }

    public AuthenticatedUser getViewedUser() {
        return viewedUser;
    }

    public void setViewedUser(AuthenticatedUser user) {
        this.viewedUser = user;
    }

    public void setSearchUsers(List<AuthenticatedUser> searchUsers) {
        this.searchUsers = searchUsers;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getOtherRejectReason() {
        return otherRejectReason;
    }

    public AuthenticatedUser getRejectedUser() {
        return rejectedUser;
    }

    public void setRejectedUser(AuthenticatedUser rejectedUser) {
        this.rejectedUser = rejectedUser;
    }

    public void setOtherRejectReason(String otherRejectReason) {
        this.otherRejectReason = otherRejectReason;
    }
    
    public void search() {
        AuthenticatedUser user = authenticationServiceBean.getAuthenticatedUser(this.searchIdentifier);
        if (user == null) {
            searchUsers = Collections.EMPTY_LIST;
        } else {
            searchUsers = new ArrayList<AuthenticatedUser>();
            searchUsers.add(user);
        }
    }

    private boolean addMember(RoleAssignee roleAssignee) {
        boolean isAdded = false;
        if (!explicitGroup.structuralContains(roleAssignee)) {
            try {
                explicitGroup.add(roleAssignee);
                logger.info("Attempting to add user " + roleAssignee.getIdentifier() + " for group" + explicitGroup.getGroupAliasInOwner()); // TODO MBS remove
                explicitGroup = commandEngine.submit(new UpdateExplicitGroupCommand(dvRequestService.getDataverseRequest(), explicitGroup));
                explicitGroup.setProvider(explicitGroupService.getProvider());
                JsfHelper.addSuccessMessage("Succesfully add user " + roleAssignee.getIdentifier());
                isAdded = true;
            } catch (GroupException ge) {
                JH.addMessage(FacesMessage.SEVERITY_FATAL, ge.getMessage());
                logger.log(Level.SEVERE, ge.getMessage());
            } catch (CommandException ex) {
                logger.log(Level.WARNING, "User add failed", ex);
                JsfHelper.JH.addMessage(FacesMessage.SEVERITY_ERROR,
                        "User add failed.",
                        ex.getMessage());
            } catch (Exception ex) {
                JH.addMessage(FacesMessage.SEVERITY_FATAL, "The group was not able to be updated.");
                logger.log(Level.SEVERE, "Error add user: " + ex.getMessage(), ex);
            }
        } else {
            JsfHelper.addInfoMessage("Group " + explicitGroup.getDisplayName() + " has already contained user " + roleAssignee.getIdentifier());
            isAdded = true;
        }
        return isAdded;
    }
    
    public void grantJoinGroupToRequests(AuthenticatedUser user) {
        if (addMember(user)) {
            try {
                explicitGroup.getJoinGroupRequesters().remove(user);
                explicitGroup = commandEngine.submit(new UpdateExplicitGroupCommand(dvRequestService.getDataverseRequest(), explicitGroup));
                explicitGroup.setProvider(explicitGroupService.getProvider());
                JsfHelper.addSuccessMessage("Joining group requestd by " + user.getName() + " was granted.");
                Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
                userNotificationService.sendNotification(user, new Timestamp(new Date().getTime()), UserNotification.Type.GRANTJOINGROUP, explicitGroup.getId());
                usageIndexService.index(eventBuilder.acceptJoinGroup(
                        (javax.servlet.http.HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(),
                        user, explicitGroup.getId()));
            } catch (CommandException ex) {
                logger.log(Level.WARNING, "Request user remove failed", ex);
                JsfHelper.JH.addMessage(FacesMessage.SEVERITY_ERROR,
                        "Request user remove failed.",
                        ex.getMessage());
            } catch (Exception ex) {
                JH.addMessage(FacesMessage.SEVERITY_FATAL, "The group was not able to be updated.");
                logger.log(Level.SEVERE, "Error request user remove: " + ex.getMessage(), ex);
            }
        }
    }
    
    public void rejectJoinGroupToRequests() {
        if(this.rejectedUser != null){
            rejectJoinGroupToRequests(this.rejectedUser);
            this.rejectedUser = null;
        }
    }

    public void rejectJoinGroupToRequests(AuthenticatedUser user) {
        try {
            explicitGroup.getJoinGroupRequesters().remove(user);
            explicitGroup = commandEngine.submit(new UpdateExplicitGroupCommand(dvRequestService.getDataverseRequest(), explicitGroup));
            explicitGroup.setProvider(explicitGroupService.getProvider());
            JsfHelper.addSuccessMessage("Joining group requested by " + user.getName() + " was rejected.");
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            if(rejectReason.equals("other")){
                rejectReason = otherRejectReason;
            }
            userNotificationService.sendNotification(user, new Timestamp(new Date().getTime()), UserNotification.Type.REJECTJOINGROUP, explicitGroup.getId(),rejectReason);
            usageIndexService.index(eventBuilder.rejectJoinGroup(
                        (javax.servlet.http.HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(),
                        user, explicitGroup.getId()));
        } catch (CommandException ex) {
            logger.log(Level.WARNING, "Request user remove failed", ex);
            JsfHelper.JH.addMessage(FacesMessage.SEVERITY_ERROR,
                    "Request user remove failed.",
                    ex.getMessage());
        } catch (Exception ex) {
            JH.addMessage(FacesMessage.SEVERITY_FATAL, "The group was not able to be updated.");
            logger.log(Level.SEVERE, "Error request user remove: " + ex.getMessage(), ex);
        }finally{
            rejectReason = "other";
        }
    }
    
    public void addMemberDirectly(RoleAssignee roleAssignee){
        if (addMember(roleAssignee)) {
            if(roleAssignee.getIdentifier().startsWith(AuthenticatedUser.IDENTIFIER_PREFIX)){
                AuthenticatedUser user = authenticationServiceBean.getAuthenticatedUser(roleAssignee.getIdentifier().substring(1));
                if(user != null){
                    usageIndexService.index(eventBuilder.addGroupMemberDirectly(
                        (javax.servlet.http.HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(),
                        user, explicitGroup.getId()));
                }
            }
        }
    }
    
    public void deleteMemberDirectly(RoleAssignee roleAssignee) {
        if (explicitGroup.structuralContains(roleAssignee)) {
            explicitGroup.remove(roleAssignee);
            try {
                logger.info("Attempting to delete user " + roleAssignee.getIdentifier() + " for group" + explicitGroup.getGroupAliasInOwner()); // TODO MBS remove
                explicitGroup = commandEngine.submit(new UpdateExplicitGroupCommand(dvRequestService.getDataverseRequest(), explicitGroup));
                explicitGroup.setProvider(explicitGroupService.getProvider());
                JsfHelper.addSuccessMessage("Succesfully delete user " + roleAssignee.getIdentifier());
                if(roleAssignee.getIdentifier().startsWith(AuthenticatedUser.IDENTIFIER_PREFIX)){
                    AuthenticatedUser user = authenticationServiceBean.getAuthenticatedUser(roleAssignee.getIdentifier().substring(1));
                    if(user != null){
                        usageIndexService.index(eventBuilder.deleteGroupMemberDirectly(
                        (javax.servlet.http.HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(),
                        user, explicitGroup.getId()));
                    }
                }
            } catch (CommandException ex) {
                logger.log(Level.WARNING, "User delete failed", ex);
                JsfHelper.JH.addMessage(FacesMessage.SEVERITY_ERROR,
                        "User delete failed.",
                        ex.getMessage());
            } catch (Exception ex) {
                JH.addMessage(FacesMessage.SEVERITY_FATAL, "The group was not able to be updated.");
                logger.log(Level.SEVERE, "Error delete user: " + ex.getMessage(), ex);
            }
        } else {
            JsfHelper.addInfoMessage("Group " + explicitGroup.getDisplayName() + " doesn't contain user " + roleAssignee.getIdentifier());
        }
    }
    
    public void save(){
        explicitGroupService.persist(explicitGroup);
        JsfHelper.addSuccessMessage("Succesfully update group information.");
    }
}
