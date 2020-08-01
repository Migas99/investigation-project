package Run.SpringBoot.Controllers;

import Run.SpringBoot.Service.Neo4jDataFetcher;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@RestController
public class QueryController {

    @Autowired
    private Neo4jDataFetcher dataFetcher;

    private GraphQL graphQL;

    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("graphql/schema.graphql");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")

                        .dataFetcher("getAllAccounts", this.dataFetcher.getListOfAllAccounts())
                        .dataFetcher("getAccountById", this.dataFetcher.getAccountById())

                        .dataFetcher("getAllCustomers", this.dataFetcher.getListOfAllCustomers())
                        .dataFetcher("getCustomerById", this.dataFetcher.getCustomerById())

                        .dataFetcher("getAllSuppliers", this.dataFetcher.getListOfAllSuppliers())
                        .dataFetcher("getSupplierById", this.dataFetcher.getSupplierById())

                        .dataFetcher("getAllProducts", this.dataFetcher.getListOfAllProducts())
                        .dataFetcher("getProductById", this.dataFetcher.getProductById())

                        .dataFetcher("getGeneralLedgerEntries", this.dataFetcher.getGeneralLedgerEntries())

                        .dataFetcher("getAllJournals", this.dataFetcher.getListOfAllJournals())
                        .dataFetcher("getJournalById", this.dataFetcher.getJournalById())

                        .dataFetcher("getAllTransactions", this.dataFetcher.getListOfAllTransactions())
                        .dataFetcher("getTransactionById", this.dataFetcher.getTransactionById())

                        .dataFetcher("getSalesInvoices", this.dataFetcher.getSalesInvoices())

                        .dataFetcher("getAllInvoices", this.dataFetcher.getListOfAllInvoices())
                        .dataFetcher("getInvoiceById", this.dataFetcher.getInvoiceById())

                        .dataFetcher("getListOfInvoicesNotAssociatedWithCustomers", this.dataFetcher.getListOfInvoicesNotAssociatedWithCustomers())
                        .dataFetcher("getListOfNegativeAmountsInGeneralLedger", this.dataFetcher.getListOfNegativeAmountsInGeneralLedger())
                        .dataFetcher("getListOfDaysWithoutSales", this.dataFetcher.getListOfDaysWithoutSales())
                        .dataFetcher("getListOfNetTotalAndTaxPayableByTaxCode", this.dataFetcher.getListOfNetTotalAndTaxPayableByTaxCode())
                        .dataFetcher("getListOfSalesByPeriod", this.dataFetcher.getListOfSalesByPeriod()))

                .type(newTypeWiring("GeneralLedgerEntries")
                        .dataFetcher("Journal", this.dataFetcher.getListOfAllJournals()))

                .type(newTypeWiring("Journal")
                        .dataFetcher("Transaction", this.dataFetcher.getListOfTransactionsByJournal()))

                .type(newTypeWiring("Transaction")
                        .dataFetcher("Lines", this.dataFetcher.getLinesByTransaction()))

                .type(newTypeWiring("SalesInvoices")
                        .dataFetcher("Invoice", this.dataFetcher.getListOfAllInvoices()))

                .type(newTypeWiring("Invoice")
                        .dataFetcher("DocumentStatus", this.dataFetcher.getDocumentStatusByInvoice())
                        .dataFetcher("SpecialRegimes", this.dataFetcher.getSpecialRegimesByInvoice())
                        .dataFetcher("ShipTo", this.dataFetcher.getShipToByInvoice())
                        .dataFetcher("ShipFrom", this.dataFetcher.getShipFromByInvoice())
                        .dataFetcher("Line", this.dataFetcher.getListOfLineByInvoice())
                        .dataFetcher("DocumentTotals", this.dataFetcher.getDocumentTotalsByInvoice())
                        .dataFetcher("WithholdingTax", this.dataFetcher.getListOfWithholdingTaxByInvoice()))

                .build();
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

}
