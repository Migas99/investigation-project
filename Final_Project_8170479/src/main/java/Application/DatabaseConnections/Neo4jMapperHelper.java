package Application.DatabaseConnections;

import Application.Enums.EnumsOfEntities;
import org.neo4j.driver.*;

import java.util.Map;

/**
 * @author José Miguel Ribeiro Cunha
 */
public class Neo4jMapperHelper {

    private Driver driver;

    public Neo4jMapperHelper(Driver driver) {
        this.driver = driver;
    }

    public boolean isFileUnique(String fileName) {
        try (Session session = this.driver.session()) {
            final String name = fileName.substring(0, fileName.length()-4);

            boolean isUnique = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (file)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.File + ") "
                    + "WHERE file.FileName = '" + name + "' "
                    + "RETURN (file)"
            ).list().isEmpty());

            if (isUnique) {
                return true;
            }

            return false;
        }
    }

    /**
     * Verifica se a estrutura do XML já se encontra carregada neste grafo
     *
     * @return retorna se já foi carregada ou não
     */
    public boolean areIdentityNodesLoaded() {
        try (Session session = this.driver.session()) {
            boolean answer = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (any:Identity) "
                    + "RETURN (any)"
            ).list().isEmpty());

            if (answer) {
                return false;
            }

            return true;
        }
    }

    /**
     * Método responsável por adicionar à base de dados um identity node
     *
     * @param XMLElement tipo do nó
     */
    public void addIdentityNode(String XMLElement) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n:" + XMLElement + ":Identity)"
            ));
        }
    }

    /**
     * Adiciona uma relação entre dois nós do typeof
     *
     * @param id     id do nó que vai ser do tipo de
     * @param target nó com o label com o tipo
     */
    public void addRelationshipTypeOf(long id, String target) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b:" + target + ") "
                    + "WHERE ID(a) = " + id + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(b)"
            ));
        }
    }

    /**
     * Método que adiciona um novo nó à base de dados
     *
     * @return o id do novo nó
     */
    public long addNode() {
        try (Session session = this.driver.session()) {
            return session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n)"
                    + "RETURN (n)"
            ).single().get(0).asNode().id());
        }
    }

    /**
     * Método que adiciona um novo nó à base de dados, com a propriedade passado como argumento
     *
     * @param attribute propriedade
     * @param value     valor da propriedade
     * @return o id do novo nó
     */
    public long addNode(String attribute, int value) {
        try (Session session = this.driver.session()) {
            return session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n { " + attribute + ": " + value + " } )"
                    + "RETURN (n)"
            ).single().get(0).asNode().id());
        }
    }

    /**
     * Método que adiciona um novo nó à base de dados, com a propriedade passado como argumento
     *
     * @param attribute propriedade
     * @param value     valor da propriedade
     * @return o id do novo nó
     */
    public long addNode(String attribute, double value) {
        try (Session session = this.driver.session()) {
            return session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n { " + attribute + ": " + value + " } )"
                    + "RETURN (n)"
            ).single().get(0).asNode().id());
        }
    }

    /**
     * Método que adiciona um novo nó à base de dados, com a propriedade passado como argumento
     *
     * @param attribute propriedade
     * @param value     valor da propriedade
     * @return o id do novo nó
     */
    public long addNode(String attribute, String value) {
        try (Session session = this.driver.session()) {
            return session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n { " + attribute + ": '" + value + "' } )"
                    + "RETURN (n)"
            ).single().get(0).asNode().id());
        }
    }

    /**
     * Método responsável por adicionar um novo atributo a um nó
     *
     * @param id        do nó
     * @param attribute atributo
     * @param value     valor do atributo
     */
    public void addPropertyToNode(long id, String attribute, int value) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n) "
                    + "WHERE ID(n) = " + id + " "
                    + "SET n." + attribute + " = " + value + " "
            ));
        }
    }

    /**
     * Método responsável por adicionar um novo atributo a um nó
     *
     * @param id        do nó
     * @param attribute atributo
     * @param value     valor do atributo
     */
    public void addPropertyToNode(long id, String attribute, double value) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n) "
                    + "WHERE ID(n) = " + id + " "
                    + "SET n." + attribute + " = " + value + " "
            ));
        }
    }

    /**
     * Método responsável por adicionar um novo atributo a um nó
     *
     * @param id        do nó
     * @param attribute atributo
     * @param value     valor do atributo
     */
    public void addPropertyToNode(long id, String attribute, String value) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n) "
                    + "WHERE ID(n) = " + id + " "
                    + "SET n." + attribute + " = '" + value + "'"
            ));
        }
    }

    /**
     * Método responsável pela criação de uma relação entre dois nós
     *
     * @param id1      id do primeiro nó
     * @param id2      id do segundo nó
     * @param relation relação entre os nós
     */
    public void addRelationship(long id1, long id2, String relation) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n),(p) "
                    + "WHERE ID(n) = " + id1 + " and ID(p) = " + id2 + " "
                    + "CREATE (n)-[:" + relation + "]->(p)"
            ));
        }
    }

    public void addRelationshipToCompany(long nodeId, long companyNodeId) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Company + ") "
                    + "WHERE ID(a) = " + nodeId + " and ID(b) = " + companyNodeId + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_COMPANY + "]->(b)"
            ));
        }
    }

    public void addRelationshipToAccount(long nodeId, long accountNodeId) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Account + ") "
                    + "WHERE ID(a) = " + nodeId + " and ID(b) = " + accountNodeId + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_ACCOUNT + "]->(b)"
            ));
        }
    }

    public void addRelationshipToCustomer(long nodeId, long customerNodeId) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Customer + ") "
                    + "WHERE ID(a) = " + nodeId + " and ID(b) = " + customerNodeId + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_CUSTOMER + "]->(b)"
            ));
        }
    }

    public void addRelationshipToSupplier(long nodeId, long supplierNodeId) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Supplier + ") "
                    + "WHERE ID(a) = " + nodeId + " and b.SupplierID = " + supplierNodeId + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_SUPPLIER + "]->(b)"
            ));
        }
    }

    public void addRelationshipToProduct(long nodeId, long productNodeId) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Product + ") "
                    + "WHERE ID(a) = " + nodeId + " and ID(b) = " + productNodeId + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_PRODUCT + "]->(b)"
            ));
        }
    }

    public void addRelationshipToTaxTable(long nodeId, Map<String, Object> taxTable) {
        try (Session session = this.driver.session()) {

            if (!taxTable.containsKey("TaxPercentage")) {
                taxTable.put("TaxPercentage", (double) -99);
            }

            if (!taxTable.containsKey("TaxAmount")) {
                taxTable.put("TaxAmount", (double) -99);
            }

            Record result = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(z:" + EnumsOfEntities.Entities.TaxTable + "), "
                    + "(a)-[:" + EnumsOfEntities.TaxTableRelationships.HAS_TAX_TYPE + "]->(b), "
                    + "(a)-[:" + EnumsOfEntities.TaxTableRelationships.HAS_TAX_COUNTRY_REGION + "]->(c) "
                    + "WHERE a.TaxCode = '" + taxTable.get("TaxCode") + "' and "
                    + "(a.TaxPercentage = " + taxTable.get("TaxPercentage") + " or a.TaxAmount = " + taxTable.get("TaxAmount") + ") and "
                    + "b.TaxType = '" + taxTable.get("TaxType") + "' and "
                    + "c.TaxCountryRegion = '" + taxTable.get("TaxCountryRegion") + "' "
                    + "RETURN (a)"
            ).single());

            long tableId = result.get("a").asNode().id();

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.TaxTable + ")"
                    + " "
                    + "WHERE ID(a) = " + nodeId + " AND ID(b) = " + tableId
                    + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_TAX_TABLE + "]->(b)"
            ));
        }
    }

    public void addRelationshipToTransaction(long nodeId, long transactionNodeId) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Transaction + ")  "
                    + "WHERE ID(a) = " + nodeId + " and ID(b) = " + transactionNodeId + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_TRANSACTION + "]->(b)"
            ));
        }
    }

    public void addRelationshipToInvoice(long nodeId, long invoiceNodeId) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Invoice + ")  "
                    + "WHERE ID(a) = " + nodeId + " and ID(b) = " + invoiceNodeId + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_INVOICE + "]->(b)"
            ));
        }
    }

    public void addRelationshipToSourceID(long nodeId, long sourceNodeId) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b) "
                    + "WHERE ID(a) = " + nodeId + " and ID(b) = " + sourceNodeId + " "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_SOURCE + "]->(b)"
            ));
        }
    }

    /**
     * Método responsável por encerrar a conexão com a base de dados
     */
    public void close() {
        this.driver.close();
    }
}
