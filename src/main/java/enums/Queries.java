package enums;

/**
 * <p>The <b>Queries</b> enum maintains SPARQL queries</p>
 *
 * @author Nikolaos Mikrou 143
 * @since 4/6/2024
 */

public enum Queries {

    QUERY1("PREFIX ex: <http://www.example.com/movieontology#>\n" +
            "SELECT (?actorName AS ?Actor_Name) (?actedInd AS ?Acted_In) (?title AS ?Title) (?type AS ?Type) (?startYear AS ?Start_Year) (?averageRating AS ?Average_Rating)\n" +
            "WHERE {\n" +
            "    ?actor a ex:Actor .\n" +
            "\t?actor ex:hasName ?actorName .\n" +
            "    ?actor ex:id \"nm0000001\" .\n" +
            "    ?actor ex:actedIn ?actedInd .\n" +
            "    \n" +
            "    BIND(REPLACE(STR(?actedInd), \"http://movie.example.com/data/movie/\", \"\") AS ?movieId)\n" +
            "    \n" +
            "    ?movie a ex:Movie .\n" +
            "    ?movie ex:tconst ?movieId .\n" +
            "    ?movie ex:hasPrimaryTitle ?title .\n" +
            "    ?movie ex:hasTitleType ?type .\n" +
            "    ?movie ex:hasStartYear ?startYear .\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?rating a ex:Rating .\n" +
            "        ?rating ex:rates ?actedInd .\n" +
            "        ?rating ex:hasAverageRating ?averageRating .\n" +
            "    }\n" +
            "}"
            , "This SPARQL query retrieves detailed information about the movies or series in which a specific actor (with ID \"nm0000001\") has acted."),

    QUERY2("PREFIX ex: <http://www.example.com/movieontology#>\n" +
            "SELECT (?rates AS ?Rates) (?movieTitle AS ?Movie_Title) (?averageRating AS ?Average_Rating) (?adultContent AS ?Adult_Content) (?genre AS ?Genre)\n" +
            "WHERE {\n" +
            "    ?rating a ex:Rating .\n" +
            "    ?rating ex:rates ?rates .\n" +
            "    ?rating ex:hasAverageRating ?averageRating .\n" +
            "    \n" +
            "    BIND (REPLACE(STR(?rates), \"http://movie.example.com/data/movie/\", \"\") AS ?tconst)\n" +
            "    \n" +
            "    ?movie a ex:Movie .\n" +
            "    ?movie ex:tconst ?tconst .\n" +
            "    ?movie ex:hasPrimaryTitle ?movieTitle .\n" +
            "    ?movie ex:isAdultContent ?adultContent .\n" +
            "    ?movie ex:hasGenres ?genre\n" +
            "    FILTER (?averageRating > 8.0)\n" +
            "}"
            , "This SPARQL query retrieves details of movies that have an average rating higher than 8.0"),

    QUERY3("PREFIX ex: <http://www.example.com/movieontology#>\n" +
            "SELECT (?genre AS ?Genre) (COUNT(?movie) AS ?Count)\n" +
            "WHERE {\n" +
            "    ?movie a ex:Movie .\n" +
            "    ?movie ex:hasGenres ?genre .\n" +
            "}\n" +
            "GROUP BY ?genre"
            , "This SPARQL query retrieves the count of movies for each genre."),

    QUERY4("PREFIX ex: <http://www.example.com/movieontology#>\n" +
            "SELECT (?startYear AS ?Start_Year) (AVG(?averageRating) AS ?Average_Rating) (AVG(?runtimeMinutes) AS ?Average_Runtime)\n" +
            "WHERE {\n" +
            "    ?movie a ex:Movie ;\n" +
            "           ex:hasStartYear ?startYear .\n" +
            "    ?rating a ex:Rating ;\n" +
            "            ex:rates ?movie ;\n" +
            "            ex:hasAverageRating ?averageRating .\n" +
            "    OPTIONAL { ?movie ex:hasRuntimeMinutes ?runtimeMinutes }\n" +
            "}\n" +
            "GROUP BY ?startYear"
            , "This SPARQL query calculates the average rating and average runtime for movies released in each year"),

    QUERY5("PREFIX ex: <http://www.example.com/movieontology#>\n" +
            "SELECT (?actor AS ?Actor) (?actorName AS ?Actor_Name) (COUNT(?movie) AS ?Movies_Count)\n" +
            "WHERE {\n" +
            "    ?actor a ex:Actor .\n" +
            "    ?actor ex:hasName ?actorName .\n" +
            "    ?actor ex:actedIn ?movie .\n" +
            "}\n" +
            "GROUP BY ?actor ?actorName\n" +
            "ORDER BY DESC(?Movies_Count)\n" +
            "LIMIT 1"
            , "This SPARQL query retrieves the actor who has appeared in the most movies."),

    QUERY6("PREFIX ex: <http://www.example.com/movieontology#>\n" +
            "SELECT (?genre AS ?Genre) (AVG(?averageRating) AS ?Average_Rating)\n" +
            "WHERE {\n" +
            "    ?movie a ex:Movie .\n" +
            "    ?movie ex:hasGenres ?genre .\n" +
            "    \n" +
            "    ?rating a ex:Rating .\n" +
            "    ?rating ex:rates ?movie .\n" +
            "    ?rating ex:hasAverageRating ?averageRating .\n" +
            "}\n" +
            "GROUP BY ?genre"
            , "This SPARQL query retrieves the average rating for each movie genre"),

    QUERY7("PREFIX ex: <http://www.example.com/movieontology#>\n" +
            "SELECT (?movieTitle AS ?Movie_Title) (?totalRatings AS ?Total_Ratings) (?averageRating AS ?Average_Ratings)\n" +
            "WHERE {\n" +
            "    ?movie a ex:Movie ;\n" +
            "           ex:hasGenres \"Drama\" .\n" +
            "    ?movie ex:hasPrimaryTitle ?movieTitle .\n" +
            "    \n" +
            "    ?rating a ex:Rating ;\n" +
            "            ex:rates ?movie ;\n" +
            "            ex:hasTotal ?totalRatings ;\n" +
            "            ex:hasAverageRating ?averageRating .\n" +
            "    \n" +
            "    FILTER (?averageRating > 7.5 && ?totalRatings > 5000)\n" +
            "}\n" +
            "ORDER BY DESC(?averageRating)\n" +
            "LIMIT 10"
            , "This SPARQL query retrieves movie titles, total ratings, and average ratings for movies categorized as Drama with an average rating higher than 7.5" +
            " and total ratings greater than 5000. It orders the results by average rating in descending order and limits the output to the top 10 movies.")

    ;

    private final String query;
    private final String description;

    /**
     * <p>Constructor</p>
     *
     * @param query String
     * @param description String
     */
    Queries(String query, String description) {
        this.query = query;
        this.description = description;
    }

    /**
     * <p>Function that return the query of the enum</p>
     *
     * @return query
     */
    public String getQuery() {
        return this.query;
    }

    /**
     * <p>Function that return the description of the enum</p>
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

}
