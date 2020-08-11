package Run.SpringBoot.Service;

import Database.CypherAlgorithms;
import Database.CypherQueries;
import Database.CypherViews;
import Database.Neo4jConnector;
import graphql.schema.DataFetcher;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class Neo4jDataFetcher {

    @Autowired
    private Environment env;

    private Driver driver;

    @PostConstruct
    private void init() {
        this.driver = Neo4jConnector.getDriver();
    }

    public DataFetcher louvain() {
        return dataFetchingEnvironment -> CypherAlgorithms.louvainAlgorithm(this.driver);
    }

    public DataFetcher localClustering() {
        return dataFetchingEnvironment -> CypherAlgorithms.localClusteringAlgorithm(this.driver);
    }

    public DataFetcher pageRank() {
        return dataFetchingEnvironment -> CypherAlgorithms.pageRankAlgorithm(this.driver);
    }

    public DataFetcher nodeSimilarity() {
        return dataFetchingEnvironment -> CypherAlgorithms.nodeSimilarityAlgorithm(this.driver);
    }

    public DataFetcher getListOfInvoicesNotAssociatedWithCustomers() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfInvoicesNotAssociatedWithCustomers(this.driver);
    }

    public DataFetcher getListOfNegativeAmountsInGeneralLedger() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfNegativeAmountsInGeneralLedger(this.driver);
    }

    public DataFetcher getListOfDaysWithoutSales() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfDaysWithoutSales(this.driver);
    }

    public DataFetcher getListOfNetTotalAndTaxPayableByTaxCode() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfNetTotalAndTaxPayableByTaxCode(this.driver);
    }

    public DataFetcher getListOfSalesByPeriod() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfSalesByPeriod(this.driver);
    }

    public DataFetcher getListOfFilesByCompany() {
        return dataFetchingEnvironment -> CypherViews.getListOfFilesByCompany(this.driver);
    }

    public DataFetcher getFileByName() {
        return dataFetchingEnvironment -> CypherViews.getFileByName(this.driver, dataFetchingEnvironment.getArgument("fileName"));
    }
}
