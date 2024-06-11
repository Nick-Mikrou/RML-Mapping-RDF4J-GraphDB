import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.NamedNode;
import enums.Paths;
import enums.Queries;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>The <b>Project</b> class is the base class of the task</p>
 *
 * @author Nikolaos Mikrou 143
 * @since 4/6/2024
 */

public class Project {

    // Initialize logger to log messages and errors
    private static final Logger logger = Logger.getLogger(Project.class.getName());

    public static void main(String[] args) {

        Project project = new Project();

        // Create File for the output file
        File mappingFile = new File(Paths.MAPPING_FILE_PATH.getPath());

        try (Writer output = new FileWriter(Paths.OUTPUT_FILE_PATH.getPath())) {
            project.runMapper(mappingFile, output);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating file writer", e);
        }

        // Save results to the repository
        project.saveGraphDB();

        // Execution of queries
        for (Queries query : Queries.values()) {
            project.executeQuery(query.getQuery(), query);
        }

    }

    /**
     * <p>The <b>runMapper</b> function executes the process of creating an RDF graph using
     * RML rules. Utilizing the RML rules file, the dataset is converted to RDF</p>
     *
     * @param mappingFile File
     * @param outputFile Writer
     */
    void runMapper(File mappingFile, Writer outputFile) {
        try (InputStream mappingStream = new FileInputStream(mappingFile)) {
            QuadStore rmlStore = QuadStoreFactory.read(mappingStream);

            // Get and record the parent directory of the mapping file
            String parentDir = mappingFile.getParent();
            logger.log(Level.INFO, "Parent directory: {0}", parentDir);

            // Create RecordsFactory based on the parent directory
            RecordsFactory factory = new RecordsFactory(parentDir);

            // Map
            Map<String, Class> libraryMap = new HashMap<>();
            libraryMap.put("IDLabFunctions", IDLabFunctions.class);

            FunctionLoader functionLoader = new FunctionLoader(null, libraryMap);

            // Create QuadStore to store the results
            QuadStore outputStore = new RDF4JStore();

            // Execute mapping
            Executor executor = new Executor(rmlStore, factory, functionLoader, outputStore, Utils.getBaseDirectiveTurtle(mappingStream));
            QuadStore result = executor.executeV5(null).get(new NamedNode("rmlmapper://default.store"));

            // Export the results to the output file
            result.write(outputFile, "turtle");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error running mapper", e);
        } finally {
            try {
                outputFile.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing output file", e);
            }
        }
    }

    /**
     * <p>The <b>saveGraphDB</b> function is responsible for saving data to GraphDB</p>
     */
    private void saveGraphDB() {
        // Create an HTTPRepository object with the GraphDB address
        HTTPRepository repository = new HTTPRepository(Paths.HTTP_REPOSITORY_PATH.getPath());

        // Open a connection and clear its contents
        RepositoryConnection connection = repository.getConnection();
        connection.clear();
        connection.begin();

        try {
            repository.getConnection().add(Project.class.getResourceAsStream("/rml/ontology/ontology.owl"), "urn:base", RDFFormat.TURTLE);
            repository.getConnection().add(Project.class.getResourceAsStream("/rml/output/output.ttl"), "urn:base", RDFFormat.TURTLE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error adding data to repository", e);
        }

        // Completing the transaction and committing the changes to GraphDB
        connection.commit();
        repository.shutDown();
    }

    /**
     * <p>The <b>executeQuery</b> function is responsible for executing the queries</p>
     *
     * @param queryString String
     */
    private void executeQuery(String queryString, Queries queryEnum) {
        // Create an HTTPRepository object with the GraphDB address
        HTTPRepository repository = new HTTPRepository(Paths.HTTP_REPOSITORY_PATH.getPath());

        // Begin connection
        RepositoryConnection connection = repository.getConnection();
        connection.begin();

        try {
            connection.add(Project.class.getResourceAsStream("/rml/ontology/ontology.owl"), "urn:base", RDFFormat.TURTLE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error adding data to repository", e);
        }

        // Committing the transaction persists the data
        connection.commit();

        // SPARQL Query
        TupleQuery query = connection.prepareTupleQuery(queryString);

        // Export results
        try (TupleQueryResult result = query.evaluate()) {
            exportResults(result, queryEnum);
        }

        // Print results in console
        try (TupleQueryResult result = query.evaluate()) {
            printResults(result, queryEnum);
        }

        // Close connection and shutdown repository
        connection.close();
        repository.shutDown();
    }

    /**
     * <p>The function <b>exportResults</b> export results in files</p>
     *
     * @param result TupleQueryResult
     * @param queryEnum Queries
     */
    private void exportResults(TupleQueryResult result, Queries queryEnum) {

        try (FileWriter writer = new FileWriter(Paths.RESULTS_PATH.getPath().concat(queryEnum.name()))) {

            writer.write("Results for the " + queryEnum.name() + "\n\n");
            writer.write("Description : " + queryEnum.getDescription() + "\n\n");

            int count = 0;

            while (result.hasNext()) {
                count++;

                BindingSet solution = result.next();

                writer.write("\n-------------------- Result ".concat(String.valueOf(count)) + " --------------------------\n");
                for (String name : solution.getBindingNames()) {
                    writer.write(name.concat(" : ").concat(String.valueOf(solution.getValue(name))).concat("\n"));
                }
                writer.write("\n");
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while exporting results", e);
        }
    }

    /**
     * <p>The function <b>printResults</b> print results in console</p>
     *
     * @param result TupleQueryResult
     * @param queryEnum Queries
     */
    private void printResults(TupleQueryResult result, Queries queryEnum) {
        System.out.printf("======================== %s ========================\n", queryEnum.name());
        System.out.println("Query description : " + queryEnum.getDescription());

        int count = 0;

        for (BindingSet solution : result) {
            count++;

            System.out.printf("\n-------------------- Result %s --------------------------%n", count);
            for (String name : solution.getBindingNames()) {
                System.out.println((name.concat(" : ").concat(String.valueOf(solution.getValue(name)))));
            }
            System.out.print("\n");
        }
    }

}
