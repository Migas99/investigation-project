package RunApplication.SpringBoot.Service;

import Application.DatabaseConnections.CypherQueries;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Service
public class Neo4jDataFetcher {

    @Autowired
    private Environment env;

    private Driver driver;

    @PostConstruct
    private void init() {
        this.driver = GraphDatabase.driver(
                Objects.requireNonNull(env.getProperty("NEO4J_URL")),
                AuthTokens.basic(
                        Objects.requireNonNull(env.getProperty("NEO4J_USERNAME")),
                        Objects.requireNonNull(env.getProperty("NEO4J_PASSWORD"))
                )
        );
    }

    public DataFetcher getListOfCustomersNotIdentified() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfCustomersNotIdentified(this.driver);
    }

    public DataFetcher getListOfSuppliersNotIdentified() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfSuppliersNotIdentified(this.driver);
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
}