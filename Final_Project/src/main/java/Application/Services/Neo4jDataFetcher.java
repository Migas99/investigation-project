package Application.Services;

import Application.Repository.GraphAlgorithms;
import Application.Repository.Queries;
import Application.Repository.Views;
import graphql.schema.DataFetcher;
import org.neo4j.driver.Driver;
import org.springframework.stereotype.Service;

@Service
public class Neo4jDataFetcher {

    public DataFetcher louvain(Driver driver) {
        return dataFetchingEnvironment -> GraphAlgorithms.louvainAlgorithm(driver);
    }

    public DataFetcher localClustering(Driver driver) {
        return dataFetchingEnvironment -> GraphAlgorithms.localClusteringAlgorithm(driver);
    }

    public DataFetcher pageRank(Driver driver) {
        return dataFetchingEnvironment -> GraphAlgorithms.pageRankAlgorithm(driver);
    }

    public DataFetcher betweennessCentrality(Driver driver) {
        return dataFetchingEnvironment -> GraphAlgorithms.betweennessCentralityAlgorithm(driver);
    }

    public DataFetcher nodeSimilarity(Driver driver) {
        return dataFetchingEnvironment -> GraphAlgorithms.nodeSimilarityAlgorithm(driver);
    }

    public DataFetcher getListOfInvoicesNotAssociatedWithCustomers(Driver driver) {
        return dataFetchingEnvironment -> Queries.obtainListOfInvoicesNotAssociatedWithCustomers(driver);
    }

    public DataFetcher getListOfNegativeAmountsInGeneralLedger(Driver driver) {
        return dataFetchingEnvironment -> Queries.obtainListOfNegativeAmountsInGeneralLedger(driver);
    }

    public DataFetcher getListOfDaysWithoutSales(Driver driver) {
        return dataFetchingEnvironment -> Queries.obtainListOfDaysWithoutSales(driver);
    }

    public DataFetcher getListOfNetTotalAndTaxPayableByTaxCode(Driver driver) {
        return dataFetchingEnvironment -> Queries.obtainListOfNetTotalAndTaxPayableByTaxCode(driver);
    }

    public DataFetcher getListOfSalesByPeriod(Driver driver) {
        return dataFetchingEnvironment -> Queries.obtainListOfSalesByPeriod(driver);
    }

    public DataFetcher getListOfFilesByCompany(Driver driver) {
        return dataFetchingEnvironment -> Views.getListOfFilesByCompany(driver);
    }

    public DataFetcher getFileByName(Driver driver) {
        return dataFetchingEnvironment -> Views.getFileByName(driver, dataFetchingEnvironment.getArgument("fileName"));
    }
}
