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
                    + "CALL gds.louvain.stream(" + getAnonymousGraphProjectionWithCompaniesAndInvoiceSales() + ")\n"
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
     * ***************************
     * **NÃO APRESENTA UTILIDADE**
     * ***************************
     * Devido à maneira como se encontra estruturada a informação no grafo (cada atributo divido em nós
     * com relações entre si) este algoritmo não irá apresentar resultados satisfatórios. O algoritmo
     * Common Neighbors irá então ser um bom substituto a este.
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

    /**
     * Método responsável por correr o algoritmo Common Neighbors.
     * Este algoritmo irá calcular um score entre 0 e 1, que nos dirá o quão semelhante
     * uma empresa é com outra, baseando-se nos atributos idênticos entre estas.
     *
     * @param driver instância do driver para comunicar com a base de dados
     * @return uma lista que irá indicar a pontuação entre duas empresas
     */
    public static LinkedList<Map<String, Object>> commonNeighborsAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            String allRelations = "'HAS_BUILDING_NUMBER', 'HAS_STREET_NAME', 'HAS_ADDRESS_DETAIL', 'HAS_CITY', 'HAS_POSTAL_CODE', 'HAS_REGION', 'HAS_COUNTRY'," +
                    " 'HAS_TELEPHONE', 'HAS_FAX', 'HAS_EMAIL', 'HAS_WEBSITE'";

            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (c1:" + Entities.Labels.Company + "),(c2:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c1) < ID(c2)\n"
                    + "WITH c1, c2\n"
                    + "UNWIND [" + allRelations + "] AS relation\n"
                    + "WITH c1, c2, gds.alpha.linkprediction.commonNeighbors(c1, c2, {relationshipQuery: relation}) AS score\n"
                    + "WITH c1, c2, sum(score) AS scores\n"
                    + "RETURN c1.CompanyName AS CompanyOne, c2.CompanyName AS CompanyTwo, scores / 11 AS Score\n"
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

}
