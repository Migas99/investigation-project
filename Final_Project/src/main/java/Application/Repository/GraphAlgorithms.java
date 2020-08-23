package Application.Repository;

import Application.Enumerations.Entities;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.util.*;

import static Application.Repository.GraphProjections.*;

/**
 * Classe que contêm as métodos que utilizam algoritmos de grafos
 */
public class GraphAlgorithms {

    /**
     * Método responsável por correr o algoritmo de Louvain que irá nos permitir identificar comunidades
     * dentro do grafo. O grafo no qual este algoritmo corre, é constítuido pelos nós e relações entre as
     * empresas e as transações e entre estas mesmas.
     *
     * @param driver instância do driver para comunicar com a base de dados
     * @return Lista de comunidades, contendo dentro de cada o identificador único da comunidade e uma outra
     * lista contendo o nome das empresas que constituem essa comunidade
     */
    public static LinkedList<Map<String, Object>> louvainAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.louvain.stream(" + getAnonymousGraphProjectionWithCompaniesRelatedToOthersAndTransactions() + ")\n"
                    + "YIELD nodeId, communityId\n"
                    + "MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c) = nodeId\n"
                    + "WITH DISTINCT(communityId) AS CommunityNumber, collect(c.CompanyName) AS Companies\n"
                    + "RETURN CommunityNumber, Companies\n"
                    + "ORDER BY CommunityNumber\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    /**
     * Método responsável por correr o algoritmo local clustering. Este algoritmo é capaz de atribuir um coeficiente,
     * que representa a probabilidade dos vizinhos de um dado nó se conhecerem entre si.
     * O grafo no qual este incide, é um grafo no qual apenas existem nós que representam empresas e as
     * relações entre estas.
     *
     * @param driver instância do driver para comunicar com a base de dados
     * @return retorna uma lista que contêm o coeficiente, por empresa
     */
    public static LinkedList<Map<String, Object>> localClusteringAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.localClusteringCoefficient.stream(" + getAnonymousGraphProjectionWithCompaniesRelatedToOthers() + ")\n"
                    + "YIELD nodeId, localClusteringCoefficient\n"
                    + "MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c) = nodeId\n"
                    + "WITH c.CompanyName AS Company, localClusteringCoefficient AS Coefficient\n"
                    + "RETURN Company, Coefficient\n"
                    + "ORDER BY Coefficient DESC\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    /**
     * Método responsável por correr o algoritmo PageRank, que irá ser capaz de identificar quais as empresas
     * mais influentes.
     * O grafo no qual este algoritmo corre, é constítuido pelos nós que representam as empresas
     * e as relações entre estas.
     *
     * @param driver instância do driver para comunicar com a base de dados
     * @return Uma lista que irá conter a empresa e a pontuação desta, que serve como medidor de influência da mesma
     */
    public static LinkedList<Map<String, Object>> pageRankAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.pageRank.stream(" + getAnonymousGraphProjectionWithCompaniesRelatedToOthers() + ")\n"
                    + "YIELD nodeId, score\n"
                    + "MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c) = nodeId\n"
                    + "WITH c.CompanyName AS Company, score AS Score\n"
                    + "RETURN Company, Score\n"
                    + "ORDER BY Score DESC\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    /**
     * Método responsável por correr o algoritmo Betweenness Centrality. Este algoritmo é capaz de atribuir uma
     * pontuação a cada nó, e esta pontuação indica-nos o número de vezes que este é usado no cálculo
     * do shortest-path entre os diferentes nós.
     *
     * @param driver instância do driver para comunicar com a base de dados
     * @return lista que contêm a empresa e a pontuação dada a esta
     */
    public static LinkedList<Map<String, Object>> betweennessCentralityAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.betweenness.stream(" + getAnonymousGraphProjectionWithCompaniesRelatedToOthers() + ")\n"
                    + "YIELD nodeId, score\n"
                    + "MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c) = nodeId\n"
                    + "RETURN c.CompanyName AS Company, score AS Score\n"
                    + "ORDER BY Score DESC\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    /**
     * INCOMPLETO - O algoritmo em si, não apresenta qualquer utilidade, penso.
     * <p>
     * Método responsável por correr o algoritmo node similarity. Este algoritmo atribuí uma pontuação
     * entre 0 e 1, que indica o quão semelhantes dois nós são.
     * O grafo no qual este algoritmo corre, é um grafo constituído apenas pelas empresas e as informações
     * associadas a esta.
     *
     * @param driver instância do driver para comunicar com a base de dados
     * @return uma lista que irá conter o o grau de semelhança entre duas empresas
     */
    public static LinkedList<Map<String, Object>> nodeSimilarityAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.nodeSimilarity.stream(" + getAnonymousGraphProjectionWithCompaniesInformation() + ")\n"
                    + "YIELD node1, node2, similarity\n"
                    + "MATCH (c1:" + Entities.Labels.Company + "),(c2:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c1) = node1 AND ID(c2) = node2\n"
                    + "RETURN c1.CompanyName AS CompanyOne, c2.CompanyName AS CompanyTwo, similarity AS Similarity\n"
                    + "ORDER BY Similarity DESC\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

}
