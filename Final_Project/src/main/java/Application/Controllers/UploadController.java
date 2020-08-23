package Application.Controllers;

import Application.Mappers.QueryConstructor;
import Application.Parsers.StAX;
import Application.Repository.Constraints;
import Application.Repository.Upload;
import org.neo4j.driver.Driver;
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
    @Autowired
    private Driver driver;

    @PostConstruct
    public void init() {
        Constraints.initializeDatabase(this.driver);
    }

    /**
     * Valida e processa o ficheiro recebido.
     *
     * @param file o ficheiro enviado pelo utilizador
     * @return resposta se o upload do ficheiro foi ou não bem sucedido
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        Map<String, String> map = new HashMap<>();

        System.out.println("[SERVER] Received a new file: " + file.getOriginalFilename());

        File fileCopy = new File("database/XML/" + file.getOriginalFilename());
        FileInputStream inputStream = null;
        XMLStreamReader reader = null;

        try {
            /*Reescrevemos a informação para um novo ficheiro*/
            FileOutputStream outputStream = new FileOutputStream(fileCopy, false);
            outputStream.write(file.getBytes());
            outputStream.close();

            /*Validamos o ficheiro vs o XSD*/
            inputStream = new FileInputStream(fileCopy.getAbsolutePath());
            reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(Objects.requireNonNull(env.getProperty("XSD"))));

            Validator validator = schema.newValidator();
            validator.validate(new StAXSource(reader));

            if (Constraints.isFileNameUnique(this.driver, fileCopy.getName())) {
                System.out.println("[SERVER] Starting to map the file: " + file.getOriginalFilename());
                QueryConstructor query = StAX.processXMLToNeo4j(fileCopy);
                System.out.println("[SERVER] Importing to database the file " + fileCopy.getName() + " ... ");
                Upload.uploadToDatabase(this.driver, query.getUploadQuery(), query.getParameters());
                System.out.println("[SERVER] Done mapping the file: " + file.getOriginalFilename());
                map.put("Message", "The file " + fileCopy.getName() + " was uploaded and mapped into the database with success.");
            } else {
                map.put("Message", "This file or another one with the same name has already been uploaded.");
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

            if (fileCopy.delete()) {
                System.out.println("[SERVER] The file " + fileCopy.getName() + " was deleted from the XML folder.");
            } else {
                System.out.println("[SERVER] Could not delete the file " + fileCopy.getName() + " from XML folder.");
            }

        }

        return map;
    }

}
