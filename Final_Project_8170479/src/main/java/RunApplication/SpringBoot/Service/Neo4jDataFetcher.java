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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
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

    public DataFetcher getListOfAllAccounts() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfAllAccounts(this.driver);
    }

    public DataFetcher getAccountById() {
        return dataFetchingEnvironment -> {
            String accountId = dataFetchingEnvironment.getArgument("id");
            LinkedList<Map<String, Object>> mapList = CypherQueries.obtainListOfAllAccounts(this.driver);

            Iterator<Map<String, Object>> iterator = mapList.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> map = iterator.next();
                if (map.get("AccountID").equals(accountId)) {
                    return map;
                }
            }

            return null;
        };
    }

    public DataFetcher getListOfAllCustomers() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfAllCustomers(this.driver);
    }

    public DataFetcher getCustomerById() {
        return dataFetchingEnvironment -> {
            String customerId = dataFetchingEnvironment.getArgument("id");
            LinkedList<Map<String, Object>> mapList = CypherQueries.obtainListOfAllCustomers(this.driver);

            Iterator<Map<String, Object>> iterator = mapList.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> map = iterator.next();
                if (map.get("CustomerID").equals(customerId)) {
                    return map;
                }
            }

            return null;
        };
    }

    public DataFetcher getListOfAllSuppliers() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfAllSuppliers(this.driver);
    }

    public DataFetcher getSupplierById() {
        return dataFetchingEnvironment -> {
            String supplierId = dataFetchingEnvironment.getArgument("id");
            LinkedList<Map<String, Object>> mapList = CypherQueries.obtainListOfAllSuppliers(this.driver);

            Iterator<Map<String, Object>> iterator = mapList.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> map = iterator.next();
                if (map.get("SupplierID").equals(supplierId)) {
                    return map;
                }
            }

            return null;
        };
    }

    public DataFetcher getListOfAllProducts() {
        return dataFetchingEnvironment -> CypherQueries.obtainListOfAllProducts(this.driver);
    }

    public DataFetcher getProductById() {
        return dataFetchingEnvironment -> {
            String productCode = dataFetchingEnvironment.getArgument("id");
            LinkedList<Map<String, Object>> mapList = CypherQueries.obtainListOfAllProducts(this.driver);

            Iterator<Map<String, Object>> iterator = mapList.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> map = iterator.next();
                if (map.get("ProductCode").equals(productCode)) {
                    return map;
                }
            }

            return null;
        };
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
