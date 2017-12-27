/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.pku.lib.dataverse.search;

import cn.edu.pku.lib.dataverse.util.StringUtils;
import edu.harvard.iq.dataverse.DataCitation;
import edu.harvard.iq.dataverse.DataFile;
import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.DatasetField;
import edu.harvard.iq.dataverse.DatasetFieldType;
import edu.harvard.iq.dataverse.DatasetVersion;
import edu.harvard.iq.dataverse.Dataverse;
import edu.harvard.iq.dataverse.FileMetadata;
import static edu.harvard.iq.dataverse.search.IndexServiceBean.PUBLISHED_STRING;
import static edu.harvard.iq.dataverse.search.IndexServiceBean.solrDocIdentifierFile;
import edu.harvard.iq.dataverse.search.IndexableDataset;
import edu.harvard.iq.dataverse.search.SearchFields;
import edu.harvard.iq.dataverse.search.SolrField;
import edu.harvard.iq.dataverse.util.StringUtil;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author luopc
 */
public class IndexServiceBeanAuxiliary {
    
    public static final String PUBLISHED_STRING_ZH = "已发布";
    private static final String UNPUBLISHED_STRING_ZH = "未发布";
    private static final String DRAFT_STRING_ZH = "草稿";
    private static final String IN_REVIEW_STRING_ZH = "审核中";
    private static final String DEACCESSIONED_STRING_ZH = "已注销";
    public static final String HARVESTED_ZH = "收割";
    
    public static void processDataverseIndex(SolrInputDocument solrInputDocument, Dataverse dataverse, Dataverse rootDataverse){        
        solrInputDocument.addField(SearchFieldsZh.NAME_ZH, dataverse.getNameZh());
        solrInputDocument.addField(SearchFieldsZh.NAME_SORT_ZH, 
                cn.edu.pku.lib.dataverse.util.StringUtils.chineseToPinyinAndUpperCase(dataverse.getNameZh()));
        solrInputDocument.addField(SearchFieldsZh.DATAVERSE_CATEGORY_ZH, dataverse.getIndexableCategoryName(Locale.SIMPLIFIED_CHINESE));
        if (dataverse.isReleased()) {
            solrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, PUBLISHED_STRING_ZH);
            solrInputDocument.addField(SearchFieldsZh.RELEASE_OR_CREATE_DATE_SEARCHABLE_TEXT_ZH, 
                    convertToFriendlyDate(dataverse.getPublicationDate(),Locale.SIMPLIFIED_CHINESE));
        }else{
            solrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, UNPUBLISHED_STRING_ZH);
            solrInputDocument.addField(SearchFieldsZh.RELEASE_OR_CREATE_DATE_SEARCHABLE_TEXT_ZH,
                    convertToFriendlyDate(dataverse.getCreateDate(),Locale.SIMPLIFIED_CHINESE));
        }
        solrInputDocument.addField(SearchFieldsZh.DESCRIPTION_ZH, StringUtil.html2text(dataverse.getDescriptionZh()));
        solrInputDocument.addField(SearchFieldsZh.DATAVERSE_DESCRIPTION_ZH, StringUtil.html2text(dataverse.getDescriptionZh()));
        if(dataverse.getAffiliationZh() != null && !dataverse.getAffiliationZh().isEmpty()){
            solrInputDocument.addField(SearchFieldsZh.AFFILIATION_ZH, dataverse.getAffiliationZh());
            solrInputDocument.addField(SearchFieldsZh.DATAVERSE_AFFILIATION_ZH, dataverse.getAffiliationZh());
        }
        if (rootDataverse != null && !dataverse.equals(rootDataverse)) {
            if (dataverse.getOwner() != null) {
                solrInputDocument.addField(SearchFieldsZh.PARENT_NAME_ZH, dataverse.getOwner().getNameZh());
            }
        }
    }
    
    public static void processDatasetIndex(IndexableDataset indexableDataset, IndexableDataset.DatasetState state, Date majorVersionReleaseDate, SolrInputDocument solrInputDocument, Date datasetSortByDate, DatasetVersion datasetVersion, Dataset dataset){
        if (majorVersionReleaseDate != null) {
        } else {
            if (indexableDataset.getDatasetState().equals(IndexableDataset.DatasetState.WORKING_COPY)) {
                solrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, UNPUBLISHED_STRING_ZH);
            } else if (indexableDataset.getDatasetState().equals(IndexableDataset.DatasetState.DEACCESSIONED)) {
                solrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, DEACCESSIONED_STRING_ZH);
            }
        }
        if (state.equals(indexableDataset.getDatasetState().PUBLISHED)) {
            solrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, PUBLISHED_STRING_ZH);
        } else if (state.equals(indexableDataset.getDatasetState().WORKING_COPY)) {
            solrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, DRAFT_STRING_ZH);
        }
        solrInputDocument.addField(SearchFieldsZh.RELEASE_OR_CREATE_DATE_SEARCHABLE_TEXT_ZH, convertToFriendlyDate(datasetSortByDate, Locale.SIMPLIFIED_CHINESE));
        String parentDatasetTitle = "TBD";
        if (datasetVersion != null) {
            solrInputDocument.addField(SearchFieldsZh.DATASET_CITATION_ZH, new DataCitation(datasetVersion, Locale.SIMPLIFIED_CHINESE).toString(true));
            if (datasetVersion.isInReview()) {
                solrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, IN_REVIEW_STRING_ZH);
            }
            for (DatasetField dsf : datasetVersion.getFlatDatasetFields()) {
                DatasetFieldType dsfType = dsf.getDatasetFieldType();
                String solrFieldSearchable = dsfType.getSolrField().getNameSearchable();
                if (dsf.getValues() != null && !dsf.getValues().isEmpty() && dsf.getValues().get(0) != null && solrFieldSearchable != null) {
                    if (dsfType.getSolrField().getSolrType().equals(SolrField.SolrType.EMAIL)) {
                        //no-op. we want to keep email address out of Solr per https://github.com/IQSS/dataverse/issues/759
                    } else if (dsfType.getSolrField().getSolrType().equals(SolrField.SolrType.DATE)) {
                        
                    } else {
                        if (dsf.getDatasetFieldType().getName().equals("authorAffiliation_zh")) {
                            solrInputDocument.addField(SearchFieldsZh.AFFILIATION_ZH, dsf.getValuesWithoutNaValues());
                        }else if (dsf.getDatasetFieldType().getName().equals("title_zh")) {
                            List<String> possibleTitles = dsf.getValues();
                            String firstTitle = possibleTitles.get(0);
                            if (firstTitle != null) {
                                parentDatasetTitle = firstTitle;
                            }
                            solrInputDocument.addField(SearchFieldsZh.NAME_SORT_ZH,
                                    cn.edu.pku.lib.dataverse.util.StringUtils.chineseToPinyinAndUpperCase(dsf.getValue()));
                        }
                    }
                }
            }
        }
        solrInputDocument.addField(SearchFieldsZh.PARENT_NAME_ZH, dataset.getOwner().getNameZh());
    }
    
    public static void processDatafileIndex(IndexableDataset indexableDataset, Date majorVersionReleaseDate, DatasetVersion datasetVersion, DataFile datafile, SolrInputDocument datafileSolrInputDocument, String filenameCompleteFinal, Date fileSortByDate){
        datafileSolrInputDocument.addField(SearchFieldsZh.NAME_SORT_ZH, filenameCompleteFinal);
        datafileSolrInputDocument.addField(SearchFieldsZh.RELEASE_OR_CREATE_DATE_SEARCHABLE_TEXT_ZH,
                convertToFriendlyDate(fileSortByDate,Locale.SIMPLIFIED_CHINESE));
        if (majorVersionReleaseDate == null && !datafile.isHarvested()) {
            datafileSolrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, UNPUBLISHED_STRING_ZH);
        }
        if (datasetVersion.isInReview()) {
            datafileSolrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, IN_REVIEW_STRING_ZH);
        }
        if (indexableDataset.getDatasetState().equals(indexableDataset.getDatasetState().PUBLISHED)) {
            datafileSolrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, PUBLISHED_STRING_ZH);
        } else if (indexableDataset.getDatasetState().equals(indexableDataset.getDatasetState().WORKING_COPY)) {
            datafileSolrInputDocument.addField(SearchFieldsZh.PUBLICATION_STATUS_ZH, DRAFT_STRING_ZH);
        }
    }
    
    public static String convertToFriendlyDate(Date dateAsDate,Locale locale) {
        if (dateAsDate == null) {
            dateAsDate = new Date();
        }
        //  using DateFormat.MEDIUM for May 5, 2014 to match what's in DVN 3.x
        DateFormat format = null;
        if(locale != null){
            format = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        }else{
            format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
        }
        String friendlyDate = format.format(dateAsDate);
        return friendlyDate;
    }
}
