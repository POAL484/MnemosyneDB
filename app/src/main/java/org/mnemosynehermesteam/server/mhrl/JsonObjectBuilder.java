package org.mnemosynehermesteam.server.mhrl;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class JsonObjectBuilder {
    
    private final JsonObject jsonObject = new JsonObject();

    public JsonObjectBuilder add(String key, Object val) {
        jsonObject.add(key, DataManipulationUtils.serialiseObject(val));
        return this;
    }

    public JsonObject build() {
        return jsonObject;
    }
}
