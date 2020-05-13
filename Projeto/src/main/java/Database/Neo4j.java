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

    /**
     * Método responsável por criar um novo nó no grafo, e por devolver o seu
     * unique ID
     *
     * @param node nome do node
     * @return o id único do node criado
     */
    public long addNode(String node) {
        try (Session session = this.driver.session()) {
            Value answer = session.writeTransaction(tx -> tx.run(""
                    + "CREATE (" + node + ":" + node + ") "
                    + "RETURN " + node
            ).single().get(node));

            Node createdNode = answer.asNode();
            return createdNode.id();
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
            session.writeTransaction(tx -> tx.run(
                    "MATCH (n) "
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
            session.writeTransaction(tx -> tx.run(
                    "MATCH (n),(p) "
                            + "WHERE ID(n) = " + id1 + " and ID(p) = " + id2 + " "
                            + "CREATE (n)-[:" + relation + "]->(p)"
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
