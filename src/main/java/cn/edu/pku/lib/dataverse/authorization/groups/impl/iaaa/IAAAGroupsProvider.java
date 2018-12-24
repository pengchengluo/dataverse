/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.pku.lib.dataverse.authorization.groups.impl.iaaa;

import edu.harvard.iq.dataverse.DvObject;
import edu.harvard.iq.dataverse.authorization.RoleAssignee;
import edu.harvard.iq.dataverse.authorization.groups.Group;
import edu.harvard.iq.dataverse.authorization.groups.GroupProvider;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import edu.harvard.iq.dataverse.engine.command.DataverseRequest;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author luopc
 */
public class IAAAGroupsProvider implements GroupProvider<Group> {
    
    private static final IAAAGroupsProvider instance = new IAAAGroupsProvider();

    private IAAAGroupsProvider(){}
    
    public static IAAAGroupsProvider get() {
        return instance;
    }
    
    @Override
    public String getGroupProviderAlias() {
        return "iaaa";
    }

    @Override
    public String getGroupProviderInfo() {
        return "Holder for groups come from IAAA user.";
    }
    
    @Override
    public Set<Group> groupsFor(DataverseRequest req, DvObject dvo) {
        return groupsFor(req.getUser(), dvo );
    }

    @Override
    public Set<Group> groupsFor(RoleAssignee ra, DvObject dvo) {
        if(ra instanceof AuthenticatedUser){
            if(cn.edu.pku.lib.dataverse.util.UserUtils.isPKUIAAAUser((AuthenticatedUser)ra)){
                return Collections.singleton((Group)IAAAUsers.get());
            }
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public Group get(String groupAlias) {
        return groupAlias.equals(IAAAUsers.get().getAlias())? IAAAUsers.get():null;
    }
    
    public Group getByIdentifier(String identifier){
        return identifier.equals(IAAAUsers.get().getIdentifier())? IAAAUsers.get():null;
    }

    @Override
    public Set<Group> findGlobalGroups() {
        return Collections.singleton((Group)IAAAUsers.get());
    }

    @Override
    public Set<Group> groupsFor(RoleAssignee ra) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Group> groupsFor(DataverseRequest req) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
