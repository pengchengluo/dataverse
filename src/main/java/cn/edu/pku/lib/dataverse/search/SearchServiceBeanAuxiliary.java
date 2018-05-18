/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.pku.lib.dataverse.search;

import cn.edu.pku.lib.dataverse.DatasetFieldZhConstant;
import edu.harvard.iq.dataverse.DatasetFieldServiceBean;
import edu.harvard.iq.dataverse.DatasetFieldType;
import edu.harvard.iq.dataverse.Dataverse;
import edu.harvard.iq.dataverse.DataverseFacet;
import edu.harvard.iq.dataverse.search.FacetCategory;
import edu.harvard.iq.dataverse.search.SearchFields;
import edu.harvard.iq.dataverse.search.SolrQueryResponse;
import edu.harvard.iq.dataverse.search.SolrSearchResult;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.TransactionRolledbackLocalException;
import javax.persistence.NoResultException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

/**
 *
 * @author luopc
 */
public class SearchServiceBeanAuxiliary {
    
    public static void processHightlightMap(Map<String, String> solrFieldsToHightlightOnMap,
            List<DatasetFieldType> datasetFields, Locale locale){
        boolean isZh = locale.getLanguage().equals("zh");
        if(isZh){
            solrFieldsToHightlightOnMap.put(SearchFieldsZh.NAME_ZH, "名称");
            solrFieldsToHightlightOnMap.put(SearchFieldsZh.AFFILIATION_ZH, "单位");
            solrFieldsToHightlightOnMap.put(SearchFieldsZh.DESCRIPTION_ZH, "描述");
            
            solrFieldsToHightlightOnMap.remove(SearchFields.NAME);
            solrFieldsToHightlightOnMap.remove(SearchFields.AFFILIATION);
            solrFieldsToHightlightOnMap.remove(SearchFields.DESCRIPTION);
            
            solrFieldsToHightlightOnMap.put(SearchFields.FILE_TYPE_FRIENDLY, "文件类型");
            solrFieldsToHightlightOnMap.put(SearchFields.VARIABLE_NAME, "变量名称");
            solrFieldsToHightlightOnMap.put(SearchFields.VARIABLE_LABEL, "变量标签");
            solrFieldsToHightlightOnMap.put(SearchFields.FILE_TYPE_SEARCHABLE, "文件类型");
            solrFieldsToHightlightOnMap.put(SearchFields.DATASET_PUBLICATION_DATE, "发布日期");
            solrFieldsToHightlightOnMap.put(SearchFields.FILENAME_WITHOUT_EXTENSION, "文件名（不带扩展名）");
            solrFieldsToHightlightOnMap.put(SearchFields.FILE_TAG_SEARCHABLE, "文件标签");
        }
        for (DatasetFieldType datasetFieldType : datasetFields) {
            String solrField = datasetFieldType.getSolrField().getNameSearchable();
            String displayName = datasetFieldType.getDisplayName();
            if(isZh && !solrField.endsWith("_zh"))
                solrFieldsToHightlightOnMap.remove(solrField);
            if(!isZh && solrField.endsWith("_zh"))
                solrFieldsToHightlightOnMap.remove(solrField);
        }
    }
    
    public static void processDataverseCategoryFacet(SolrQuery solrQuery, Locale locale){
        if(locale.getLanguage().equals("zh")){
            solrQuery.removeFacetField(SearchFields.DATAVERSE_CATEGORY);
            solrQuery.addFacetField(SearchFieldsZh.DATAVERSE_CATEGORY_ZH);
        }
    }
    public static void processFacet(SolrQuery solrQuery, Dataverse dataverse, Locale locale){
        boolean isZh = locale.getLanguage().equals("zh");
        if (dataverse != null) {
            for (DataverseFacet dataverseFacet : dataverse.getDataverseFacets()) {
                DatasetFieldType datasetField = dataverseFacet.getDatasetFieldType();
                if(isZh && !datasetField.getName().endsWith("_zh"))
                    solrQuery.removeFacetField(datasetField.getSolrField().getNameFacetable());
                if(!isZh && datasetField.getName().endsWith("_zh"))
                    solrQuery.removeFacetField(datasetField.getSolrField().getNameFacetable());
            }
        }
    }
    
    public static void processFacetField(FacetField facetField, FacetCategory facetCategory, Map<String, String> datasetfieldFriendlyNamesBySolrField, Locale locale){
        boolean isZh = locale.getLanguage().equals("zh");
        if(facetField.getName().equals(SearchFields.PUBLICATION_DATE) && isZh){
            facetCategory.setFriendlyName("发布时间");
            datasetfieldFriendlyNamesBySolrField.put(SearchFields.PUBLICATION_DATE, "发布时间");
        }else if(facetField.getName().equals(SearchFieldsZh.PUBLICATION_STATUS_ZH) && isZh){
            facetCategory.setFriendlyName("发布状态");
            datasetfieldFriendlyNamesBySolrField.put(SearchFields.PUBLICATION_STATUS, "发布状态");
        }else if(facetField.getName().equals(SearchFieldsZh.DATAVERSE_CATEGORY_ZH) && isZh){
            facetCategory.setFriendlyName("数据空间类型");
            datasetfieldFriendlyNamesBySolrField.put(SearchFields.DATAVERSE_CATEGORY, "数据空间类型");
        }
    }
    
    public static void processPublicationStatusCounts(SolrQueryResponse solrQueryResponse,QueryResponse queryResponse, Locale locale){
        boolean isZh = locale.getLanguage().equals("zh");
        if(isZh){
            solrQueryResponse.setPublicationStatusCounts(queryResponse.getFacetField(SearchFieldsZh.PUBLICATION_STATUS_ZH));
        }
    }
    
    public static void processSolrSearchResult(SolrSearchResult solrSearchResult,
            SolrDocument solrDocument, String type, Map<String, String> parent,
            DatasetFieldServiceBean datasetFieldService, Logger logger,
            Locale locale){
        boolean isZh = locale.getLanguage().equals("zh");
        if(isZh){
            String description = (String) solrDocument.getFieldValue(SearchFieldsZh.DESCRIPTION_ZH);
            solrSearchResult.setDescriptionNoSnippet(description);
            
            if (type.equals("datasets")) {
                String titleZhSolrField = null;
                try {
                    DatasetFieldType titleDatasetField = datasetFieldService.findByName(DatasetFieldZhConstant.titleZh);
                    titleZhSolrField = titleDatasetField.getSolrField().getNameSearchable();
                } catch (EJBTransactionRolledbackException ex) {
                    logger.info("Couldn't find " + DatasetFieldZhConstant.titleZh);
                    if (ex.getCause() instanceof TransactionRolledbackLocalException) {
                        if (ex.getCause().getCause() instanceof NoResultException) {
                            logger.info("Caught NoResultException");
                        }
                    }
                }
                String title = (String) solrDocument.getFieldValue(titleZhSolrField);
                        
                if (title != null) {
                    solrSearchResult.setTitle(title);
                } else {
                    logger.fine("No title indexed. Setting to empty string to prevent NPE. Dataset id ");
                    solrSearchResult.setTitle("");
                }
                
                List<String> datasetDescriptions = (List<String>) solrDocument.getFieldValue(SearchFieldsZh.DATASET_DESCRIPTION_ZH);
                if (datasetDescriptions != null) {
                    String firstDatasetDescription = datasetDescriptions.get(0);
                    if (firstDatasetDescription != null) {
                        solrSearchResult.setDescriptionNoSnippet(firstDatasetDescription);
                    }
                }
                
                String citation = (String) solrDocument.getFieldValue(SearchFieldsZh.DATASET_CITATION_ZH);
                String citationPlainHtml = (String) solrDocument.getFieldValue(SearchFieldsZh.DATASET_CITATION_HTML_ZH);
                
                solrSearchResult.setCitation(citation);
                solrSearchResult.setCitationHtml(citationPlainHtml);
            }
            String dateToDisplayOnCard = (String) solrDocument.getFirstValue(SearchFieldsZh.RELEASE_OR_CREATE_DATE_SEARCHABLE_TEXT_ZH);
            solrSearchResult.setDateToDisplayOnCard(dateToDisplayOnCard);
            parent.put("name", (String) solrDocument.getFieldValue(SearchFieldsZh.PARENT_NAME_ZH));
//            parent.put("citation", (String) solrDocument.getFieldValue(SearchFieldsZh.PARENT_CITATION_ZH));
            solrSearchResult.setName((String)solrDocument.getFieldValue(SearchFieldsZh.NAME_ZH));
        }
        
    }
    public static void processPermissionFilterQuery(SolrQuery solrQuery, Locale locale){
        if(locale.getLanguage().equals("zh")){
            solrQuery.removeFacetField(SearchFields.PUBLICATION_STATUS);
            solrQuery.addFacetField(SearchFieldsZh.PUBLICATION_STATUS_ZH);
        }
    }
}
