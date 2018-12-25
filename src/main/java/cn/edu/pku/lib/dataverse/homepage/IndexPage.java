/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.pku.lib.dataverse.homepage;

import cn.edu.pku.lib.dataverse.usage.EventBuilder;
import cn.edu.pku.lib.dataverse.usage.UsageIndexServiceBean;
import cn.edu.pku.lib.dataverse.usage.EventBuilder;
import cn.edu.pku.lib.dataverse.DataverseLocale;
import edu.harvard.iq.dataverse.Dataverse;
import edu.harvard.iq.dataverse.DataverseServiceBean;
import edu.harvard.iq.dataverse.DataverseSession;
import edu.harvard.iq.dataverse.search.SearchException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author luopc
 */
@ViewScoped
@Named
public class IndexPage implements Serializable{
    
    private static final long serialVersionUID = -8477826130973083209L;
    private static final Logger logger = Logger.getLogger(IndexPage.class.getCanonicalName());
    
    @EJB
    HighQualityDataverseServiceBean highQualityDataverseService;
//    @EJB
//    SolrSearchServiceBean solrSearchService;
    @EJB
    DataverseServiceBean dataverseService;
    @EJB
    UsageIndexServiceBean usageIndexService;
    @EJB
    EventBuilder eventBuilder;
    @Inject
    DataverseSession session;
    @Inject
    DataverseLocale dataverseLocale;
    
    private List<HighQualityDataverse> highQualityDataverses;
    private List<List<HighQualityDataverse>> hqDataverse;
    private long datasetCount;
    private long dataverseCount;
    private Dataverse rootDataverse;
    private String language;
    
    public void init(){
        if (StringUtils.isNotBlank(language)){
            if(language.toLowerCase().startsWith("zh")){
                this.dataverseLocale.setLocaleZh();
            }else if(language.toLowerCase().startsWith("en")){
                this.dataverseLocale.setLocaleEn();
            }
        }
        
        highQualityDataverses =  highQualityDataverseService.findAll();
        hqDataverse = new ArrayList<>();
        for(int i= 0; i < Math.ceil((double)highQualityDataverses.size()/4); i++){
            List<HighQualityDataverse> dataverses = new ArrayList(4);
            for(int j=0;j<4&&(i*4+j)<highQualityDataverses.size();j++){
                dataverses.add(highQualityDataverses.get(i*4+j));
            }
            hqDataverse.add(dataverses);
        }
//        try {
//            datasetCount = solrSearchService.getDatasetCount();
//        } catch (SearchException ex) {
//            logger.log(Level.SEVERE, null, ex);
//        }
//        try {
//            dataverseCount = solrSearchService.getDataverseCount();
//        } catch (SearchException ex) {
//                logger.log(Level.SEVERE, null, ex);
//        }
//        EventLog eventLog = usageLogIndexService.buildEventLog(EventLog.EventType.VIEW_DATAVERSE,
//                    session.getUser(),
//                    (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
//        eventLog.setDataverseId(0L);
//        usageLogIndexService.index(eventLog);
    }

    public List<HighQualityDataverse> getHighQualityDataverses() {
        return highQualityDataverses;
    }
    

    public void setHighQualityDataverses(List<HighQualityDataverse> highQualityDataverses) {
        this.highQualityDataverses = highQualityDataverses;
    }

    public long getDatasetCount() {
        return datasetCount;
    }

    public void setDatasetCount(long datasetCount) {
        this.datasetCount = datasetCount;
    }

    public Dataverse getRootDataverse() {
        return rootDataverse;
    }

    public void setRootDataverse(Dataverse rootDataverse) {
        this.rootDataverse = rootDataverse;
    }

    public List<List<HighQualityDataverse>> getHqDataverse() {
        return hqDataverse;
    }

    public void setHqDataverse(List<List<HighQualityDataverse>> hqDataverse) {
        this.hqDataverse = hqDataverse;
    }

    public long getDataverseCount() {
        return dataverseCount;
    }

    public void setDataverseCount(long dataverseCount) {
        this.dataverseCount = dataverseCount;
    }
    
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
