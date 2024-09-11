package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {

	public DefaultRegionBuilder() {
		super("default", "Default region builder");
	}

	@Override
	protected DefaultRegion create_instance(JSONObject data) {
		return new DefaultRegion();
	}
	protected void fill_in_data(JSONObject o) {
		//o.put("type", "default");
		//o.put("desc", "Infinite food supply");
		//JSONObject data=new JSONObject();
		//o.put("data", data);
	}
}
