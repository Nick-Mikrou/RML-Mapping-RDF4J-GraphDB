package enums;

/**
 * <p>The enum <b>Paths</b> maintains the paths of the documents needed for the task</p>
 *
 * @author Nikolaos Mikrou 143
 * @since 4/6/2024
 */

public enum Paths {

    MAPPING_FILE_PATH("./src/main/resources/rml/mapping/mapping.ttl"),
    OUTPUT_FILE_PATH("./src/main/resources/rml/output/output.ttl"),
    HTTP_REPOSITORY_PATH("http://DESKTOP-70MRQVG:7200/repositories/Project"),
    RESULTS_PATH("./src/main/resources/results/")

    ;

    private final String path;

    /**
     * <p>Constructor</p>
     *
     * @param path String
     */
    Paths(String path) {
        this.path = path;
    }

    /**
     * <p>Function that returns the path of the enum</p>
     *
     * @return path
     */
    public String getPath() {
        return this.path;
    }

}
