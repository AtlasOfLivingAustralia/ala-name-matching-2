package au.org.ala.names;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import au.org.ala.bayesian.StoreException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.text.similarity.CosineSimilarity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An autocomplete result.
 * <p>
 * JSON names for some features have been renamed to make them compatible with
 * existing names.
 * </p>
 */
@Value
@Builder
public class Autocomplete {
    /** The autocomplete score */
    private float score;
    /** The result name */
    private String name;
    /** The identifier of the autcomplete match */
    private String taxonId;
    /** The accepted taxon id */
    private String acceptedNameUsageId;
    /** The left value for the tree index */
    private Integer left;
    /** The right value */
    private Integer right;
    /** The taxon rank */
    private String rank;
    /** The rank ID */
    private Integer rankId;
    /** The preferred vernacular name */
    private String vernacularName;
    /** The common names */
    private List<String> vernacularNames;
    /** The classification */
    private AlaLinnaeanClassification classification;
    /** Any synonyms */
    private List<Autocomplete> synonyms;

    /**
     * Convert into a map for web service delivery
     *
     * @return The map.
     */
    public Map asMap() {
        Map map = new LinkedHashMap();
        map.put("score", this.score);
        map.put("name", this.name);
        map.put("lsid", this.taxonId);
        if (this.acceptedNameUsageId != null)
            map.put("acceptedLsid", this.acceptedNameUsageId);
        if (this.left != null)
            map.put("left", this.left.toString());
        if (this.right != null)
            map.put("right", this.right.toString());
        if (this.rank != null)
            map.put("rank", this.rank);
        if (this.rankId != null)
            map.put("rankId", this.rank);
        if (this.vernacularName != null)
            map.put("commonname", this.vernacularName);
        if (this.vernacularNames != null)
            map.put("commonnames", this.vernacularNames);
        if (this.classification != null) {
            Map cl = new LinkedHashMap();
            if (this.classification.getIssues() != null && !this.classification.getIssues().isEmpty()) {
                cl.put("issues", this.classification.getIssues().stream().map(Objects::toString).collect(Collectors.toList()));
            }
            for (Observation observation: this.classification.toObservations()) {
               if (!observation.isBlank()) {
                   if (observation.isSingleton()) {
                       cl.put(observation.getObservable().getId(), observation.getStoreValue());
                   } else {
                       cl.put(observation.getObservable().getId(), observation.getStoreValues());
                   }
                }
            }
            map.put("cl", cl);
        }
        if (this.synonyms != null && !this.synonyms.isEmpty())
            map.put("synonymMatch", this.synonyms.stream().map(Autocomplete::asMap).collect(Collectors.toList()));
        return map;
    }

}
