package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	public SelectFirstBuilder() {
		super("first", "SelectFirstStrategy");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		// solo entra data del json aceptado (llamos este metodo desde buldbasedfac)
		return new SelectFirst();
	}

}
