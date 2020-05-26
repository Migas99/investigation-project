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

    /**
     * Método responsável pela criação das relações entre nós relativos à estrutura do XML
     *
     * @param XMLElementOne Um dos nós
     * @param XMLElementTwo Um dos nós
     */
    public void addXMLStructureRelationShip(String XMLElementOne, String XMLElementTwo) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a:" + XMLElementOne + "),(b:" + XMLElementTwo + ")"
                    + "CREATE (a)-[:CONTAINS]->(b)"
                    + "CREATE (b)-[:PART_OF]->(a)"
            ));
        }
    }

    /**
     * Adiciona uma relação entre dois nós do typeof
     *
     * @param id id do nó que vai ser do tipo de
     * @param target nó com o label com o tipo
     */
    public void addRelationshipTypeOf(long id, String target) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a),(b:" + target + ")"
                    + "WHERE ID(a) = " + id
                    + "CREATE (a)-[:TYPE_OF]->(b)"
            ));
        }
    }

    /**
     * Método responsável por criar um novo nó no grafo, e por devolver o seu
     * unique ID
     *
     * @param label label do node
     * @return o id único do node criado
     */
    public long addNode(String label) {
        try (Session session = this.driver.session()) {
            Value answer = session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n:" + label + ":XMLStructure) "
                    + "RETURN (n)"
            ).single().get(0));

            return answer.asNode().id();
        }
    }

    public long addNode() {
        try (Session session = this.driver.session()) {
            Value answer = session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n:BasicNode)"
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
     * Método responsável por adicionar um novo atributo a um nó
     *
     * @param targetNode
     * @param parameter
     * @param value
     */
    public void addAttributesToNode(String targetNode, String parameter, String value) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n) "
                    + "WHERE ID(n) = " + targetNode + " "
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

    /**
     * Método responsável pela criação de uma relação entre dois nós
     *
     * @param relation relação entre os nós
     */
    public void addRelationship(String node1, String node2, String relation) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "CREATE (" + node1 + ")-[:" + relation + "]->(" + node2 + ")"
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
