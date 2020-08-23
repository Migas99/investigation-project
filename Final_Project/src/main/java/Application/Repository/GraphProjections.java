package Application.Repository;

import Application.Enumerations.Entities;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

/**
 * Classe que contêm métodos que possibilitam a construção de subgrafos
 */
public class GraphProjections {

    /**
     * Método que permite adicionar um grafo ao catálogo
     *
     * @param driver            instância do driver para conectar com a base de dados
     * @param graphName         o nome no qual o grafo fica associado no catálogo
     * @param nodeQuery         a query que nos indica quais os nós constituintes do grafo
     * @param relationshipQuery a query que nos indica quais as relações constituintes do grafo
     */
    public static void createGraphInCatalog(Driver driver, String graphName, String nodeQuery, String relationshipQuery) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.graph.create.cypher(\n"
                    + "'" + graphName + "',\n"
                    + "'" + nodeQuery + "',\n"
                    + "'" + relationshipQuery + "'\n"
                    + ")\n"
                    + "YIELD graphName\n"
            ));
        }
    }

    /**
     * Método que remove um grafo do catálogo, dado o seu nome.
     *
     * @param driver    instância do driver para conectar com a base de dados
     * @param graphName nome do grafo a ser removido
     */
    public static void removeGraphInCatalog(Driver driver, String graphName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("CALL gds.graph.drop('" + graphName + "') YIELD graphName"));
        }
    }

    /**
     * Método responsável por retornar uma Query capaz de definir um grafo anónimo. Neste caso, este será
     * um grafo que irá conter os nós e as relações respetivos à informação das empresas.
     *
     * @return a query de construção do grafo
     */
    public static String getAnonymousGraphProjectionWithCompaniesInformation() {
        return "\n{\n"
                + "nodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id\n"
                + "UNION\n"
                + "MATCH (ci:" + Entities.Labels.CompanyInfo + ")\n"
                + "RETURN ID(ci) AS id\n"
                + "UNION\n"
                + "MATCH (ca:" + Entities.Labels.CompanyAddress + ")\n"
                + "RETURN ID(ca) AS id\n"
                + "UNION\n"
                + "MATCH (cc:" + Entities.Labels.CompanyContact + ")\n"
                + "RETURN ID(cc) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")-[r:"
                + Entities.CompanyRelationships.HAS_COMPANY_ID + "|"
                + Entities.CompanyRelationships.HAS_TAX_REGISTRATION_NUMBER + "|"
                + Entities.CompanyRelationships.HAS_BUSINESS_NAME
                + "]->(ci:" + Entities.Labels.CompanyInfo + ")\n"
                + "RETURN ID(c) AS source, ID(ci) AS target, type(r) AS type\n"
                + "UNION\n"
                + "MATCH (c:" + Entities.Labels.Company + ")-[r:"
                + Entities.CompanyRelationships.HAS_BUILDING_NUMBER + "|"
                + Entities.CompanyRelationships.HAS_STREET_NAME + "|"
                + Entities.CompanyRelationships.HAS_ADDRESS_DETAIL + "|"
                + Entities.CompanyRelationships.HAS_CITY + "|"
                + Entities.CompanyRelationships.HAS_POSTAL_CODE + "|"
                + Entities.CompanyRelationships.HAS_REGION + "|"
                + Entities.CompanyRelationships.HAS_COUNTRY
                + "]->(ca:" + Entities.Labels.CompanyAddress + ")\n"
                + "RETURN ID(c) AS source, ID(ca) AS target, type(r) AS type\n"
                + "UNION\n"
                + "MATCH (c:" + Entities.Labels.Company + ")-[r:"
                + Entities.CompanyRelationships.HAS_TELEPHONE + "|"
                + Entities.CompanyRelationships.HAS_FAX + "|"
                + Entities.CompanyRelationships.HAS_EMAIL + "|"
                + Entities.CompanyRelationships.HAS_WEBSITE
                + "]->(cc:" + Entities.Labels.CompanyContact + ")\n"
                + "RETURN ID(c) AS source, ID(cc) AS target, type(r) AS type'\n"
                + "}\n";
    }

    /**
     * Método responsável por retornar uma Query capaz de definir um grafo anónimo. Neste caso,
     * este grafo irá conter todas as empresas existentes e as relações entre estas.
     *
     * @return a query de construção do grafo
     */
    public static String getAnonymousGraphProjectionWithCompaniesRelatedToOthers() {
        return "\n{\n"
                + "nodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (c1:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.IS_CUSTOMER_OF + "]->(c2:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c1) AS source, ID(c2) AS target\n"
                + "UNION\n"
                + "MATCH (c1:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.IS_SUPPLIER_OF + "]->(c2:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c1) AS source, ID(c2) AS target'\n"
                + "}\n";
    }

    /**
     * Método responsáve lpor retornar uma Query capaz de definir um grafo anónimo. Neste caso,
     * este grafo irá retornar todas as empresas e transações existentes, e as relações entre estes
     * dois tipos.
     *
     * @return a query de construção do grafo
     */
    public static String getAnonymousGraphProjectionWithCompaniesAndTransactions() {
        return "\n{\n"
                + "nodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id\n"
                + "UNION\n"
                + "MATCH (t:" + Entities.Labels.Transaction + ")\n"
                + "RETURN ID(t) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (t:" + Entities.Labels.Transaction + ")-[:" + Entities.TransactionRelationships.HAS_BUYER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(t) AS source, ID(c) AS target\n"
                + "UNION\n"
                + "MATCH (t:" + Entities.Labels.Transaction + ")-[:" + Entities.TransactionRelationships.HAS_SELLER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(t) AS source, ID(c) AS target'\n"
                + "}\n";
    }

    /**
     * Método responsável por retornar uma Query capaz de definir um grafo anónimo. Neste caso,
     * este grafo irá conter todos os nós correspondentes às empresas e às faturas, e as relações
     * entre estes.
     *
     * @return a query de construção do grafo
     */
    public static String getAnonymousGraphProjectionWithCompaniesAndInvoiceSales() {
        return "\n{\n"
                + "nodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id\n"
                + "UNION\n"
                + "MATCH (i:" + Entities.Labels.Invoice + ")\n"
                + "RETURN ID(i) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (i:" + Entities.Labels.Invoice + ")-[:" + Entities.InvoiceRelationships.HAS_SELLER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(i) AS source, ID(c) AS target\n"
                + "UNION\n"
                + "MATCH (i:" + Entities.Labels.Invoice + ")-[:" + Entities.InvoiceRelationships.HAS_BUYER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(i) AS source, ID(c) AS target'\n"
                + "}\n";
    }

    /**
     * Método responsável por retornar uma Query capaz de definir um grafo anónimo. Neste caso, este grafo
     * irá conter os nós relativos às empresas e transações e as relações entre as empresas e as empresas
     * com as transações.
     *
     * @return a query de construção do grafo
     */
    public static String getAnonymousGraphProjectionWithCompaniesRelatedToOthersAndTransactions() {
        return "\n{\n"
                + "nodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id\n"
                + "UNION\n"
                + "MATCH (t:" + Entities.Labels.Transaction + ")\n"
                + "RETURN ID(t) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (c1:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.IS_CUSTOMER_OF + "]->(c2:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c1) AS source, ID(c2) AS target\n"
                + "UNION\n"
                + "MATCH (c1:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.IS_SUPPLIER_OF + "]->(c2:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c1) AS source, ID(c2) AS target\n"
                + "UNION\n"
                + "MATCH (t:" + Entities.Labels.Transaction + ")-[:" + Entities.TransactionRelationships.HAS_BUYER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(t) AS source, ID(c) AS target\n"
                + "UNION\n"
                + "MATCH (t:" + Entities.Labels.Transaction + ")-[:" + Entities.TransactionRelationships.HAS_SELLER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(t) AS source, ID(c) AS target'\n"
                + "}\n";
    }
}
