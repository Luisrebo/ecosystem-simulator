package simulator.model;

import org.json.JSONObject;

//Esta interfaz la implementan los objetos que necesitamos que proporcionen su estado en formato json

public interface JSONable {
	default public JSONObject as_JSON() {
		return new JSONObject();
	}
}
