package Database;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

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

    public void addXMLStructureNode(String XMLElement) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "CREATE (" + XMLElement + ":" + XMLElement + ")"
            ));
        }
    }

    public void addXMLStructureRelationShip(String XMLElementOne, String XMLElementTwo) {
        try (Session session = this.driver.session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "CREATE (" + XMLElementOne + ")-[:CHILD_OF]->(" + XMLElementTwo + ")"
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

    public long addNode(){
        try (Session session = this.driver.session()) {
            Value answer = session.writeTransaction(tx -> tx.run(""
                    + "CREATE (n:BasicNode)"
                    + "RETURN (n)"
            ).single().get(0));

            return answer.asNode().id();
        }
    }

    /**
     * Método que cria um novo nó, mas apenas se este for único (ou seja, se não estiver já criado
     *
     * @param node o nó
     * @return o id do nó encontrado / criado
     */
    public long addUniqueNode(String node) {
        try (Session session = this.driver.session()) {
            Value answer = null;

            answer = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (n:" + node + ") "
                    + "RETURN n"
            ).single().get('n'));

            /**
             * Se o nó não existir, criamos o nó
             */
            if (answer.isEmpty()) {
                answer = session.writeTransaction(tx -> tx.run(""
                        + "CREATE (n:" + node + ") "
                        + "RETURN n"
                ).single().get('n'));
            }

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
