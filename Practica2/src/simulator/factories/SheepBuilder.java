package simulator.factories;

import java.util.Vector;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {

	private Factory<SelectionStrategy> _strategyFactory;

	public SheepBuilder(Factory<SelectionStrategy> factoriaEstrategias) {
		super("sheep", "Sheep anmial");
		_strategyFactory = factoriaEstrategias;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Animal create_instance(JSONObject data) {

		SelectionStrategy mateStrategy;
		SelectionStrategy dangerStrategy;
		Vector2D pos = null;

		// Si existe uso la mate strategy que entra
		if (data.has("mate_strategy"))
			mateStrategy = _strategyFactory.create_instance(data.getJSONObject("mate_strategy"));
		// si no trae mate strategy meto por defecto selectfisrts
		else {
			JSONObject jsonEstrategia = new JSONObject();
			// no hace falta llamar a la factoria puedo crear directamente el select fisrt
			// strategy
			jsonEstrategia.put("type", "first");
			mateStrategy = _strategyFactory.create_instance(jsonEstrategia);
		}

		// danger strategy tamb opcional, si trae la uso sino sfirst por defect
		if (data.has("danger_strategy"))
			dangerStrategy = _strategyFactory.create_instance(data.getJSONObject("danger_strategy"));
		// si no trae mate strategy meto por defecto selectfisrts
		else {
			JSONObject jsonEstrategia = new JSONObject();
			jsonEstrategia.put("type", "first");
			// dangerStrategy=_strategyFactory.create_instance(data.getJSONObject("jsonEstrategia"));
			dangerStrategy = _strategyFactory.create_instance(jsonEstrategia);
		}

		if (data.has("pos")) {
			double x_ini = data.getJSONObject("pos").getJSONArray("x_range").getDouble(0);
			double x_fin = data.getJSONObject("pos").getJSONArray("x_range").getDouble(1);

			double y_ini = data.getJSONObject("pos").getJSONArray("y_range").getDouble(0);
			double y_fin = data.getJSONObject("pos").getJSONArray("y_range").getDouble(1);

			pos = new Vector2D(Utils._rand.nextDouble(x_ini, x_fin), Utils._rand.nextDouble(y_ini, y_fin));
		}

		return new Sheep(mateStrategy, dangerStrategy, pos);
	}

}
