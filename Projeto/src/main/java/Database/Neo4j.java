package Database;

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
    public boolean isXMLStructureLoaded() {
        try (Session session = this.driver.session()) {
            boolean answer = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n:XMLStructure) "
                    + "RETURN (n)"
            ).list().isEmpty());

            if (answer) {
                return false;
            }

            return true;
        }
    }

    /**
     * Adiciona um nó do tipo estrutura, ou seja, que vai ser único
     *
     * @param XMLElement label do nó
     */
    public void addXMLStructureNode(String XMLElement) {
        try (Session session = this.driver.session()) {
            boolean isUnique = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n:" + XMLElement + ") "
                    + "RETURN n"
            ).list().isEmpty());

            if (isUnique) {
                session.writeTransaction(tx -> tx.run(""
                        + "CREATE (" + XMLElement + ":" + XMLElement + ":XMLStructure)"
                ));
            }
        }
    }

    public void addIdentityNode(String XMLElement){
        try (Session session = this.driver.session()) {
            boolean isUnique = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n:" + XMLElement + ") "
                    + "RETURN n"
            ).list().isEmpty());

            if (isUnique) {
                session.writeTransaction(tx -> tx.run(""
                        + "CREATE (" + XMLElement + ":" + XMLElement + ":XMLStructure)"
                ));
            }
        }
    }

    /**
     * Método responsável pela criação das relações entre nós relativos à estrutura do XML
     *
     * @param XMLElementOne Um dos nós
     * @param XMLElementTwo Um dos nós
     */
    public void addXMLStructureRelationShip(String XMLElementOne, String XMLElementTwo) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a:" + XMLElementOne + "),(b:" + XMLElementTwo + ") "
                    + "CREATE (a)-[:CONTAINS]->(b) "
                    + "CREATE (b)-[:PART_OF]->(a)"
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
                    + "MATCH (a),(b:" + target + ") "
                    + "WHERE ID(a) = " + id + " "
                    + "CREATE (a)-[:TYPE_OF]->(b)"
            ));
        }
    }

    public long addNode(String XMLElement) {
        try (Session session = this.driver.session()) {
            Value answer = session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n:BasicNode { XMLElement: '" + XMLElement + "' } )"
                    + "RETURN (n)"
            ).single().get(0));

            return answer.asNode().id();
        }
    }

    /**
     * Método responsável por adicionar um novo atributo a um nó
     *
     * @param id
     * @param parameter
     * @param value
     */
    public void addAttributesToNode(long id, String parameter, String value) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n) "
                    + "WHERE ID(n) = " + id + " "
                    + "SET n." + parameter + " = '" + value + "'"
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

    public void addRelationshipToCompany(long id, String Company) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.CompanyName = '" + Company + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:Company)"
                        + "CREATE (b { CompanyName: '" + Company + "' })"
                        + "CREATE (b)-[:TYPE_OF]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b) "
                    + "WHERE ID(a) = " + id + " and b.CompanyName = '" + Company + "' "
                    + "CREATE (a)-[:HAS_COMPANY]->(b)"
            ));
        }
    }

    public void addRelationshipToAccount(long id, String Account) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.AccountID = '" + Account + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:Account)"
                        + "CREATE (b { AccountID: '" + Account + "' })"
                        + "CREATE (b)-[:TYPE_OF]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b) "
                    + "WHERE ID(a) = " + id + " and b.AccountID = '" + Account + "' "
                    + "CREATE (a)-[:HAS_ACCOUNT]->(b)"
            ));
        }
    }

    public void addRelationshipToCustomer(long id, String Customer) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.CustomerID = '" + Customer + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:Company)"
                        + "CREATE (b { CustomerID: '" + Customer + "' })"
                        + "CREATE (b)-[:TYPE_OF]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b) "
                    + "WHERE ID(a) = " + id + " and b.CustomerID = '" + Customer + "' "
                    + "CREATE (a)-[:HAS_CUSTOMER]->(b)"
            ));
        }
    }

    /**
     * INCOMPLETO
     *
     * @param id
     * @param Company
     */
    public void addRelationshipToSupplier(long id, String Company) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.CompanyName = " + Company + " "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:Company)"
                        + "CREATE (b { CompanyName: '" + Company + "' })"
                        + "CREATE (b)-[:TYPE_OF]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b) "
                    + "WHERE ID(a) = " + id + " and b.CompanyName = " + Company + " "
                    + "CREATE (a)-[:HAS_COMPANY]->(b)"
            ));
        }
    }

    public void addRelationshipToProduct(long id, String Product) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.ProductCode = '" + Product + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:Company)"
                        + "CREATE (b { ProductCode: '" + Product + "' })"
                        + "CREATE (b)-[:TYPE_OF]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b) "
                    + "WHERE ID(a) = " + id + " and b.ProductCode = '" + Product + "' "
                    + "CREATE (a)-[:HAS_PRODUCT]->(b)"
            ));
        }
    }

    /**
     * INCOMPLETO
     *
     * @param id
     * @param Company
     */
    public void addRelationshipToTaxTable(long id, String Company) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.CompanyName = '" + Company + "' "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:Company)"
                        + "CREATE (b { CompanyName: '" + Company + "' })"
                        + "CREATE (b)-[:TYPE_OF]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b) "
                    + "WHERE ID(a) = " + id + " and b.CompanyName = '" + Company + "' "
                    + "CREATE (a)-[:HAS_COMPANY]->(b)"
            ));
        }
    }

    public void addRelationshipToTransaction(long id, String Transaction) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.TransactionID = " + Transaction + " "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:Company)"
                        + "CREATE (b { TransactionID: '" + Transaction + "' })"
                        + "CREATE (b)-[:TYPE_OF]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b) "
                    + "WHERE ID(a) = " + id + " and b.TransactionID = " + Transaction + " "
                    + "CREATE (a)-[:HAS_TRANSACTION]->(b)"
            ));
        }
    }

    /**
     * INCOMPLETO
     *
     * @param id
     * @param Company
     */
    public void addRelationshipToSourceID(long id, String Company) {
        try (Session session = this.driver.session()) {
            boolean isEmpty = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a) "
                    + "WHERE a.CompanyName = " + Company + " "
                    + "RETURN (a)"
            ).list().isEmpty());

            if (isEmpty) {
                session.writeTransaction(tx -> tx.run(""
                        + "MATCH (a:Company)"
                        + "CREATE (b { CompanyName: '" + Company + "' })"
                        + "CREATE (b)-[:TYPE_OF]->(a)"
                ));
            }

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b) "
                    + "WHERE ID(a) = " + id + " and b.CompanyName = " + Company + " "
                    + "CREATE (a)-[:HAS_COMPANY]->(b)"
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
