package au.org.ala.bayesian;

import lombok.Builder;
import lombok.Value;

import java.util.LinkedHashMap;
import java.util.Map;

@TraceDescriptor(description = "getDescription", summary = "getSummary")
@Value
@Builder
public class TestTracable2 {
    private String id;
    private String value1;
    private String value2;

    public Map<String, String> getDescription(NetworkFactory factory) {
        Map<String, String> desc = new LinkedHashMap<>();
        desc.put("id", this.id);
        if (this.value1 != null)
            desc.put("value1", this.value1);
        if (this.value2 != null)
            desc.put("value2", this.value2);
        return desc;
    }

    public String getSummary() {
        return this.id + ": [" + this.value1 + ", " + this.value2 + "]";
    }
}
