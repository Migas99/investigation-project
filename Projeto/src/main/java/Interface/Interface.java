package Interface;

import Database.Neo4j;
import Parsers.StAX;
import org.neo4j.driver.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Interface extends JFrame {

    private Neo4j db;
    private StAX stax;
    private Driver driver;

    private JLabel container;

    private JPanel outputJPanel;
    private JScrollPane outputScroll;
    private JTextArea displayOutput;

    private FirstMenu firstMenuJPanel;
    private SecondMenu secondMenuJPanel;
    private ThirdMenu thirdMenuJPanel;

    private JComboBox parsers;

    public static void main(String[] args) {
        Interface menu = new Interface();
    }

    public Interface() {
        super("Projeto Final");
        this.setLocationByPlatform(true);
        this.setSize(700, 700);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.setContainer();
        this.setOutput();
        this.firstMenu();

        this.setVisible(true);
    }

    private void setContainer() {
        this.container = new JLabel();
        this.container.setLayout(new BorderLayout());
        this.add(this.container);
    }

    private void setOutput() {
        this.outputJPanel = new JPanel();
        this.outputJPanel.setBorder(new TitledBorder(new EtchedBorder(), "Output Messages"));
        this.outputJPanel.setLayout(new BorderLayout());
        this.outputJPanel.setPreferredSize(new Dimension(700, 200));
        this.outputJPanel.setOpaque(false);

        this.displayOutput = new JTextArea(2, 25);
        this.displayOutput.setFont(this.displayOutput.getFont().deriveFont(20f));
        this.displayOutput.setEditable(false);
        this.displayOutput.setLineWrap(true);
        this.displayOutput.setText("Not connected");

        this.outputScroll = new JScrollPane(this.displayOutput);
        this.outputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.outputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.outputJPanel.add(this.outputScroll);
        this.container.add(this.outputJPanel, BorderLayout.CENTER);
    }

    private void firstMenu() {
        this.firstMenuJPanel = new FirstMenu(this.container);
        this.container.add(this.firstMenuJPanel, BorderLayout.NORTH);
    }

    class FirstMenu extends JPanel {

        private JLabel container;

        private GridBagConstraints gbc;

        private JButton connectToDatabase;

        private JTextField URL;
        private JTextField user;
        private JPasswordField password;

        FirstMenu(JLabel container) {
            this.container = container;
            this.setLayout(new GridBagLayout());
            this.setPreferredSize(new Dimension(700, 500));
            this.setOpaque(false);

            this.gbc = new GridBagConstraints();
            this.gbc.gridx = 1;
            this.gbc.gridy = 0;

            /*---- LABEL FOR URL INPUT ----*/
            JLabel urlLabel = new JLabel("<html><div style='text-align: center;'>" + "Insert here the URL:" + "</div></html>");
            urlLabel.setForeground(Color.BLACK);
            urlLabel.setPreferredSize(new Dimension(150, 30));
            this.gbc.gridy = 0;
            this.gbc.gridwidth = 3;
            this.gbc.insets = new Insets(10, 0, 0, 0);
            this.add(urlLabel, this.gbc);
            /*---- LABEL FOR URL INPUT ----*/

            /*---- Insert URL ----*/
            this.URL = new JTextField("neo4j://localhost:7687");
            this.URL.setPreferredSize(new Dimension(300, 30));

            this.gbc.gridy = 1;
            this.gbc.insets = new Insets(0, 0, 0, 0);
            this.add(this.URL, this.gbc);
            /*---- Insert URL ----*/

            /*---- LABEL FOR USER INPUT ----*/
            JLabel userLabel = new JLabel("<html><div style='text-align: center;'>" + "Insert here the user:" + "</div></html>");
            userLabel.setForeground(Color.BLACK);
            userLabel.setPreferredSize(new Dimension(150, 30));
            this.gbc.gridy = 2;
            this.gbc.gridwidth = 3;
            this.gbc.insets = new Insets(10, 0, 0, 0);
            this.add(userLabel, this.gbc);
            /*---- LABEL FOR USER INPUT ----*/

            /*---- Insert user ----*/
            this.user = new JTextField("neo4j");
            this.user.setPreferredSize(new Dimension(300, 30));

            this.gbc.gridy = 3;
            this.gbc.insets = new Insets(0, 0, 0, 0);
            this.add(this.user, this.gbc);
            /*---- Insert user ----*/

            /*---- LABEL FOR PASSWORD INPUT ----*/
            JLabel passwordLabel = new JLabel("<html><div style='text-align: center;'>" + "Insert here the password:" + "</div></html>");
            passwordLabel.setForeground(Color.BLACK);
            passwordLabel.setPreferredSize(new Dimension(150, 30));
            this.gbc.gridy = 4;
            this.gbc.gridwidth = 3;
            this.gbc.insets = new Insets(10, 0, 0, 0);
            this.add(passwordLabel, this.gbc);
            /*---- LABEL FOR PASSWORD INPUT ----*/

            /*---- Insert user ----*/
            this.password = new JPasswordField("");
            this.password.setPreferredSize(new Dimension(300, 30));

            this.gbc.gridy = 5;
            this.gbc.insets = new Insets(0, 0, 0, 0);
            this.add(this.password, this.gbc);
            /*---- Insert user ----*/

            /*---- Connect to database Button ----*/
            this.connectToDatabase = new JButton("Connect to Database");
            this.connectToDatabase.setPreferredSize(new Dimension(300, 50));
            this.connectToDatabase.setFont(new Font("Arial", Font.BOLD, 20));

            this.connectToDatabase.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    SwingWorker<Void, Void> process = new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() {

                            driver = GraphDatabase.driver(URL.getText(), AuthTokens.basic(user.getText(), password.getText()));

                            try {
                                driver.session().run("RETURN 1");
                                displayOutput.setText("Connected successfully");
                                db = new Neo4j(driver);
                                stax = new StAX(db, displayOutput);

                                toSecondMenu();

                            } catch (Exception e) {
                                driver.close();
                                displayOutput.setText("Connection failed");
                            }

                            connectToDatabase.setEnabled(true);
                            return null;
                        }
                    };

                    connectToDatabase.setEnabled(false);
                    displayOutput.setText("CustomObjectTest connection to database ...");
                    process.execute();
                }
            });

            this.gbc.gridy = 6;
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.add(this.connectToDatabase, this.gbc);
            /*---- Connect to database Button ----*/
        }

        private void toSecondMenu() {
            this.removeAll();
            this.container.remove(this);
            secondMenu();
            SwingUtilities.updateComponentTreeUI(this.container);
            this.container.setVisible(true);
        }

    }

    private void secondMenu() {
        this.secondMenuJPanel = new SecondMenu(this.container);
        this.container.add(this.secondMenuJPanel, BorderLayout.NORTH);
    }

    class SecondMenu extends JPanel {

        private JLabel container;

        private File SAFT;

        private GridBagConstraints gbc;

        private JButton chooseFile;
        private JButton validateSAFT;
        private JButton importSAFT = new JButton();
        private JButton queryDatabase = new JButton();

        private JButton back;

        private JPanel displayFilePanel;
        private JTextArea displayFile;
        private JFileChooser fileChooser;

        SecondMenu(JLabel container) {
            this.container = container;
            this.setLayout(new GridBagLayout());
            this.setPreferredSize(new Dimension(700, 500));
            this.setOpaque(false);

            this.gbc = new GridBagConstraints();
            this.gbc.gridx = 1;
            this.gbc.gridy = 0;

            /*---- Choose SAF-T Button ----*/
            this.chooseFile = new JButton("Choose SAF-T File");
            this.chooseFile.setPreferredSize(new Dimension(300, 50));
            this.chooseFile.setFont(new Font("Arial", Font.BOLD, 20));

            this.chooseFile.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    SwingWorker<Void, Void> process = new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() {
                            fileChooser = new JFileChooser();
                            fileChooser.setDialogTitle("Choose the file");
                            FileFilter filter = new FileNameExtensionFilter("XML File", "xml", "XML");
                            fileChooser.setFileFilter(filter);
                            int returnVal = fileChooser.showOpenDialog(SecondMenu.this);

                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                validateSAFT.setEnabled(true);
                                importSAFT.setEnabled(false);

                                SAFT = fileChooser.getSelectedFile();
                                displayFile.setText(SAFT.getName());
                                displayOutput.setText("Selected file: " + SAFT.getName());
                            } else {
                                validateSAFT.setEnabled(false);
                                importSAFT.setEnabled(false);

                                SAFT = null;
                                displayFile.setText("No file was chosen yet");
                                displayOutput.setText("The user didn't choose any file");
                            }

                            chooseFile.setEnabled(true);
                            queryDatabase.setEnabled(true);
                            back.setEnabled(true);

                            return null;
                        }
                    };

                    disableAllButtons();
                    displayOutput.setText("Choosing SAF-T File ...");
                    process.execute();
                }

            });

            this.gbc.gridy = 0;
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.add(this.chooseFile, this.gbc);
            /*---- Choose SAF-T Button ----*/

            /*---- File Display ----*/
            this.displayFilePanel = new JPanel();
            this.displayFilePanel.setBorder(new TitledBorder(new EtchedBorder(), "Chosen file"));
            this.displayFilePanel.setLayout(new BorderLayout());
            this.displayFilePanel.setPreferredSize(new Dimension(300, 50));
            this.displayFilePanel.setOpaque(false);

            this.displayFile = new JTextArea(16, 58);
            this.displayFile.setFont(this.displayFile.getFont().deriveFont(12f));
            this.displayFile.setEditable(false);
            this.displayFile.setText("No file was chosen yet");
            this.displayFilePanel.add(this.displayFile);

            this.gbc.gridy = 1;
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.add(this.displayFilePanel, this.gbc);
            /*---- File Display ----*/

            /*---- Validate SAFT Button ----*/
            this.validateSAFT = new JButton("Validate SAF-T with XSD");
            this.validateSAFT.setPreferredSize(new Dimension(300, 50));
            this.validateSAFT.setFont(new Font("Arial", Font.BOLD, 20));
            this.validateSAFT.setEnabled(false);
            this.validateSAFT.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    SwingWorker<Void, Void> process = new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() {
                            if (validateXML(SAFT.getAbsolutePath())) {
                                importSAFT.setEnabled(true);
                            } else {
                                importSAFT.setEnabled(false);
                            }

                            chooseFile.setEnabled(true);
                            validateSAFT.setEnabled(true);
                            queryDatabase.setEnabled(true);
                            back.setEnabled(true);

                            return null;
                        }

                    };

                    disableAllButtons();
                    displayOutput.setText("Validating SAF-T ...");
                    process.execute();

                }

            });

            this.gbc.gridy = 2;
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.add(this.validateSAFT, this.gbc);
            /*---- Validate SAFT Button ----*/

            /*---- Import SAFT Button ----*/
            this.importSAFT = new JButton("Import SAF-T to Database");
            this.importSAFT.setPreferredSize(new Dimension(300, 50));
            this.importSAFT.setFont(new Font("Arial", Font.BOLD, 20));
            this.importSAFT.setEnabled(false);

            this.importSAFT.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    SwingWorker<Void, Void> process = new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() {
                            stax.processXMLToNeo4j(SAFT.getAbsolutePath());
                            enableAllButtons();
                            displayOutput.setText("The import was completed with success");
                            return null;
                        }

                    };

                    disableAllButtons();
                    displayOutput.setText("Starting import of SAF-T to database ...");
                    process.execute();

                }

            });

            this.gbc.gridy = 3;
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.add(this.importSAFT, this.gbc);
            /*---- Import SAFT Button ----*/

            /*---- Query the database Button ----*/
            this.queryDatabase = new JButton("Query the Database");
            this.queryDatabase.setPreferredSize(new Dimension(300, 50));
            this.queryDatabase.setFont(new Font("Arial", Font.BOLD, 20));

            this.queryDatabase.addActionListener((ActionEvent event) -> {
                displayOutput.setText("The user chose to go to the Query menu");
                this.toThirdMenu();
            });

            this.gbc.gridy = 4;
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.add(this.queryDatabase, this.gbc);
            /*---- Query the database Button ----*/

            /*---- Go to previous Menu Button ----*/
            this.back = new JButton("Back");
            this.back.setPreferredSize(new Dimension(300, 50));
            this.back.setFont(new Font("Arial", Font.BOLD, 20));

            this.back.addActionListener((ActionEvent event) -> {
                db = null;
                stax = null;
                driver = null;

                displayOutput.setText("Not connected");
                this.toFirstMenu();
            });

            this.gbc.gridy = 5;
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.add(this.back, this.gbc);
            /*---- Go to previous Menu Button ----*/
        }

        private void enableAllButtons() {
            this.chooseFile.setEnabled(true);
            this.validateSAFT.setEnabled(true);
            this.importSAFT.setEnabled(true);
            this.queryDatabase.setEnabled(true);
            this.back.setEnabled(true);
        }

        private void disableAllButtons() {
            this.chooseFile.setEnabled(false);
            this.validateSAFT.setEnabled(false);
            this.importSAFT.setEnabled(false);
            this.queryDatabase.setEnabled(false);
            this.back.setEnabled(false);
        }

        private void toFirstMenu() {
            this.removeAll();
            this.container.remove(this);
            firstMenu();
            SwingUtilities.updateComponentTreeUI(this.container);
            this.container.setVisible(true);
        }

        private void toThirdMenu() {
            this.removeAll();
            this.container.remove(this);
            thirdMenu();
            SwingUtilities.updateComponentTreeUI(this.container);
            this.container.setVisible(true);
        }

        private boolean validateXML(String XMLFile) {
            try {
                XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(XMLFile));
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(new File("database/SAFTP.XSD"));

                Validator validator = schema.newValidator();
                validator.validate(new StAXSource(reader));

                displayOutput.setText("The XML File is valid");
            } catch (IOException | SAXException | XMLStreamException e) {
                displayOutput.setText("The XML File is invalid. \nReason: " + e.getMessage());
                System.out.println(e.getMessage());
                return false;
            }

            return true;
        }

    }

    private void thirdMenu() {
        this.thirdMenuJPanel = new ThirdMenu(this.container);
        this.container.add(this.thirdMenuJPanel, BorderLayout.NORTH);
    }

    class ThirdMenu extends JPanel {

        private JLabel container;

        private GridBagConstraints gbc;

        private JButton areAllIdentitiesIdentified;

        ThirdMenu(JLabel container) {
            this.container = container;
            this.setLayout(new GridBagLayout());
            this.setPreferredSize(new Dimension(700, 500));
            this.setOpaque(false);

            this.gbc = new GridBagConstraints();
            this.gbc.gridx = 1;
            this.gbc.gridy = 0;

            /*---- Are all identities identified Button ----*/
            this.areAllIdentitiesIdentified = new JButton("Check if all identities are identified");
            this.areAllIdentitiesIdentified.setPreferredSize(new Dimension(300, 50));
            this.areAllIdentitiesIdentified.setFont(new Font("Arial", Font.BOLD, 20));

            this.areAllIdentitiesIdentified.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    SwingWorker<Void, Void> process = new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() {


                            return null;
                        }
                    };

                    disableAllButtons();
                    displayOutput.setText("Choosing SAF-T File ...");
                    process.execute();
                }

            });

            this.gbc.gridy = 0;
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.add(this.areAllIdentitiesIdentified, this.gbc);
            /*---- Are all identities identified Button ----*/
        }

        private void enableAllButtons(){

        }

        private void disableAllButtons(){

        }
    }
}
