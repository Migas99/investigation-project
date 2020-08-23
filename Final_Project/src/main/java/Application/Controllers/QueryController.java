package Application.Controllers;

import Application.Services.Neo4jDataFetcher;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.neo4j.driver.Driver;
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
    private Driver driver;
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

                        //Querys relativas aos algoritmos
                        .dataFetcher("louvain", this.dataFetcher.louvain(this.driver))
                        .dataFetcher("localClustering", this.dataFetcher.localClustering(this.driver))
                        .dataFetcher("pageRank", this.dataFetcher.pageRank(this.driver))
                        .dataFetcher("betweennessCentrality", this.dataFetcher.betweennessCentrality(this.driver))
                        .dataFetcher("nodeSimilarity", this.dataFetcher.nodeSimilarity(this.driver))

                        //Querys relativas as vistas e restrições, segundo a OCDE
                        .dataFetcher("getListOfInvoicesNotAssociatedWithCustomers", this.dataFetcher.getListOfInvoicesNotAssociatedWithCustomers(this.driver))
                        .dataFetcher("getListOfNegativeAmountsInGeneralLedger", this.dataFetcher.getListOfNegativeAmountsInGeneralLedger(this.driver))
                        .dataFetcher("getListOfDaysWithoutSales", this.dataFetcher.getListOfDaysWithoutSales(this.driver))
                        .dataFetcher("getListOfNetTotalAndTaxPayableByTaxCode", this.dataFetcher.getListOfNetTotalAndTaxPayableByTaxCode(this.driver))
                        .dataFetcher("getListOfSalesByPeriod", this.dataFetcher.getListOfSalesByPeriod(this.driver))

                        //Vistas normais
                        .dataFetcher("getListOfFilesByCompany", this.dataFetcher.getListOfFilesByCompany(this.driver))
                        .dataFetcher("getFileByName", this.dataFetcher.getFileByName(this.driver))
                )

                .build();
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

}
