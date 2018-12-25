/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.pku.lib.dataverse.homepage;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author luopc
 */
@Stateless
@Named
public class HighQualityDataverseServiceBean implements java.io.Serializable{
    
    private static final long serialVersionUID = -5992942858287091944L;
    
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;
    
    public void save(HighQualityDataverse highQualityDataverse){
        if(highQualityDataverse.getId() == null){
            em.persist(highQualityDataverse);
        }else{
            em.merge(highQualityDataverse);
        }
    }
    
    public void remove(HighQualityDataverse highQualityDataverse){
        em.remove(em.merge(highQualityDataverse));
    }
    
    public List<HighQualityDataverse> findAll(){ 
        List<HighQualityDataverse> hqDvs = em.createQuery("select object(o) from HighQualityDataverse as o order by o.id").getResultList();
        return hqDvs;
    }
}

