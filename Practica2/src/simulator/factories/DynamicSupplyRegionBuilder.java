package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;
import simulator.model.SelectionStrategy;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	private static final double _default_factor = 2.0;
	private static final double _default_food = 1000.0;

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "dynamic region builder");
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		double food, factor;
		
		if (data.has("factor"))
			factor = data.getDouble("factor");
		else
			factor = _default_factor;

		if (data.has("food"))
			food = data.getDouble("food");
		else
			food = _default_food;

		return new DynamicSupplyRegion(food, factor);
	}
	protected void fill_in_data(JSONObject o) {
		o.put( "factor", "food increase factor (optional, default 2.0)");
		o.put( "food", "initial amount of food (optional, default 100.0)");
	
	}

}
