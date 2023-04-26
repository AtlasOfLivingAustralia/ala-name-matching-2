package au.org.ala.bayesian;

import lombok.Builder;
import lombok.Value;

import java.util.LinkedHashMap;
import java.util.Map;

@TraceDescriptor(identify = true, identifier = "getId", description = "getDescription", summary = "getSummary")
@Value
@Builder
public class TestTracable {
    private String id;
    private String value1;
    private String value2;

    public Map<String, String> getDescription(NetworkFactory factory) {
        Map<String, String> desc = new LinkedHashMap<>();
        desc.put("id", this.id);
        desc.put("value1", this.value1);
        desc.put("value2", this.value2);
        desc.put("factory", factory.getNetworkId());
        return desc;
    }

    public String getSummary() {
        return this.id + ": [" + this.value1 + ", " + this.value2 + "]";
    }
}
