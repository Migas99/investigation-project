package Run.SpringBoot.Controllers;

import Cypher.CypherQueries;
import Parser.StAX;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class UploadController {

    @Autowired
    private Environment env;

    private Driver driver;

    @PostConstruct
    public void init() {
        /*Criamos o driver, que conecta à base de dados*/
        this.driver = GraphDatabase.driver(
                env.getProperty("NEO4J_URL"),
                AuthTokens.basic(
                        Objects.requireNonNull(env.getProperty("NEO4J_USERNAME")),
                        Objects.requireNonNull(env.getProperty("NEO4J_PASSWORD"))
                )
        );

        if (!CypherQueries.areIdentityNodesLoaded(this.driver)) {
            CypherQueries.loadEntities(this.driver);
        }

    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        Map<String, String> map = new HashMap<>();

        System.out.println("[SERVER] Received a new file: " + file.getOriginalFilename());

        File saftp = new File("database/XML/" + file.getOriginalFilename());
        FileInputStream inputStream = null;
        XMLStreamReader reader = null;

        try {
            /*Reescrevemos a informação para um novo ficheiro*/
            FileOutputStream outputStream = new FileOutputStream(saftp, false);
            outputStream.write(file.getBytes());
            outputStream.close();

            /*Validamos o ficheiro vs o XSD*/
            inputStream = new FileInputStream(saftp.getAbsolutePath());
            reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(Objects.requireNonNull(env.getProperty("SAFTP_XSD"))));

            Validator validator = schema.newValidator();
            validator.validate(new StAXSource(reader));

            /*Criamos o driver, que conecta à base de dados*/
            /*Driver driver = GraphDatabase.driver(
                    env.getProperty("NEO4J_URL"),
                    AuthTokens.basic(
                            Objects.requireNonNull(env.getProperty("NEO4J_USERNAME")),
                            Objects.requireNonNull(env.getProperty("NEO4J_PASSWORD"))
                    )
            );*/

            if (CypherQueries.isFileUnique(driver, saftp.getName())) {
                System.out.println("[SERVER] Starting to map the file: " + file.getOriginalFilename());

                StAX.processXMLToNeo4j(driver, saftp);

                System.out.println("[SERVER] Done mapping the file: " + file.getOriginalFilename());
                map.put("Message", "The file " + saftp.getName() + " was uploaded and mapped into the database with success.");
            } else {
                map.put("Message", "The file was already uploaded. If that is not the case, change its name.");
            }

        } catch (Exception e) {

            e.printStackTrace();
            map.put("Error", e.getMessage());

        } finally {

            /*Apagamos o ficheiro no fim de tudo*/
            if (inputStream != null && reader != null) {
                try {
                    reader.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (saftp.delete()) {
                System.out.println("[SERVER] The file " + saftp.getName() + " was deleted from the XML folder.");
            } else {
                System.out.println("[SERVER] Could not delete the file " + saftp.getName() + " from XML folder.");
            }

        }

        return map;
    }

}
