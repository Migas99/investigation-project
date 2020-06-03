package Database;

import Enums.EnumsOfEntities;
import Models.TaxTable;
import org.neo4j.driver.*;

/**
 * @author José Miguel Ribeiro Cunha
 */
public class Neo4j {

    private Driver driver;

    /**
     * Método construtor
     *
     * @param url      onde o servidor se encontra à escuta
     * @param user     o username
     * @param password a password da base de dados
     */
    public Neo4j(String url, String user, String password) {
        this.driver = GraphDatabase.driver(url, AuthTokens.basic(user, password));
    }

    /**
     * Verifica se a estrutura do XML já se encontra carregada neste grafo
     *
     * @return retorna se já foi carregada ou não
     */
    public boolean areIdentityNodesLoaded() {
        try (Session session = this.driver.session()) {
            boolean answer = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n:Identity) "
                    + "RETURN (n)"
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
            boolean isUnique = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n:" + XMLElement + ") "
                    + "RETURN (n)"
            ).list().isEmpty());

            if (isUnique) {
                session.writeTransaction(tx -> tx.run(""
                        + "CREATE (n:" + XMLElement + ":Identity)"
                ));
            }
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
                    + "MATCH (a),(b:" + target + ") "
                    + "WHERE ID(a) = " + id + " "
                    + "CREATE (a)-[:TYPE_OF]->(b)"
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

    public void addRelationshipToCompany(long id, String CompanyName) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(b:" + EnumsOfEntities.Entities.Company + ") "
                    + "WHERE a.CompanyName = '" + CompanyName + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:" + EnumsOfEntities.Entities.Company + ")"
                        + "CREATE (b { CompanyName: '" + CompanyName + "' })-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Company + ") "
                    + "WHERE ID(a) = " + id + " and b.CompanyName = '" + CompanyName + "' "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_COMPANY + "]->(b)"
            ));
        }
    }

    public void addRelationshipToAccount(long id, String AccountID) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(b:" + EnumsOfEntities.Entities.Account + ") "
                    + "WHERE a.AccountID = '" + AccountID + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:" + EnumsOfEntities.Entities.Account + ")"
                        + "CREATE (b { AccountID: '" + AccountID + "' })-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Account + ") "
                    + "WHERE ID(a) = " + id + " and b.AccountID = '" + AccountID + "' "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_ACCOUNT + "]->(b)"
            ));
        }
    }

    public void addRelationshipToCustomer(long id, String CustomerID) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(b:" + EnumsOfEntities.Entities.Customer + ") "
                    + "WHERE a.CustomerID = '" + CustomerID + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:" + EnumsOfEntities.Entities.Customer + ") "
                        + "CREATE (b { CustomerID: '" + CustomerID + "' })-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Customer + ") "
                    + "WHERE ID(a) = " + id + " and b.CustomerID = '" + CustomerID + "' "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_CUSTOMER + "]->(b)"
            ));
        }
    }

    public void addRelationshipToSupplier(long id, String SupplierID) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(b:" + EnumsOfEntities.Entities.Supplier + ") "
                    + "WHERE a.SupplierID = '" + SupplierID + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:" + EnumsOfEntities.Entities.Supplier + ") "
                        + "CREATE (b { SupplierID: '" + SupplierID + "' })-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Supplier + ") "
                    + "WHERE ID(a) = " + id + " and b.SupplierID = '" + SupplierID + "' "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_SUPPLIER + "]->(b)"
            ));
        }
    }

    public void addRelationshipToProduct(long id, String ProductCode) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(b:" + EnumsOfEntities.Entities.Product + ") "
                    + "WHERE a.ProductCode = '" + ProductCode + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:" + EnumsOfEntities.Entities.Product + ") "
                        + "CREATE (b { ProductCode: '" + ProductCode + "' })-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Product + ") "
                    + "WHERE ID(a) = " + id + " and b.ProductCode = '" + ProductCode + "' "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_PRODUCT + "]->(b)"
            ));
        }
    }

    public void addRelationshipToTaxTable(long id, TaxTable table) {
        try (Session session = this.driver.session()) {
            long tableID = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(z:" + EnumsOfEntities.Entities.TaxTable + "), "
                    + "(a)-[:" + EnumsOfEntities.TaxTableRelationships.HAS_TAX_TYPE + "]->(b), "
                    + "(a)-[:" + EnumsOfEntities.TaxTableRelationships.HAS_TAX_COUNTRY_REGION + "]->(c) "
                    + "WHERE a.TaxCode = '" + table.getTaxCode() + "' and "
                    + "(a.TaxPercentage = " + table.getTaxPercentage() + " or a.TaxAmount = " + table.getTaxAmount() + ") and "
                    + "b.TaxType = '" + table.getTaxType() + "' and "
                    + "c.TaxCountryRegion = '" + table.getTaxCountryRegion() + "' "
                    + "RETURN (a)"
            ).single().get(0).asNode().id());

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.TaxTable + ") "
                    + "WHERE ID(a) = " + id + " and ID(a) = '" + tableID + "' "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_TAX_TABLE + "]->(b)"
            ));
        }
    }

    public void addRelationshipToTransaction(long id, String TransactionID) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(c:" + EnumsOfEntities.Entities.Transaction + ")  "
                    + "WHERE ID(a) = " + id + " and b.TransactionID = '" + TransactionID + "' "
                    + "CREATE (a)-[:" + EnumsOfEntities.OtherRelationships.HAS_TRANSACTION + "]->(b)"
            ));
        }
    }

    public void addRelationshipToSourceID(long id, String SourceID) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.SourceID = '" + SourceID + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "CREATE (a { SourceID: '" + SourceID + "' })"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a), (b) "
                    + "WHERE ID(a) = " + id + " and b.SourceID = '" + SourceID + "' "
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
