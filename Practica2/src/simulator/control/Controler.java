package simulator.control;

import org.json.JSONObject;
import java.io.OutputStream;
import org.json.JSONArray;
import java.io.PrintStream;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;
import java.util.ArrayList;
import java.util.List;

public class Controler {

	private Simulator _sim;

	public Controler(Simulator sim) {
		_sim = sim;
	}

	public void load_data(JSONObject data) {
		// Asumimos que data tiene las dos clavs animals y regions (regions es opcinal)
		// Duda si la regiones son opcionales y hay que añadir las regiones antes que
		// las regiones?
		// Si no hay reg no se agregan animales?
		JSONArray jsonArrayanimals;
		if (!data.has("animals"))
			throw new IllegalArgumentException("Los json deben tener campo animals");// asumimos que no pasara nunca?
		else {
			jsonArrayanimals = data.getJSONArray("animals");
		}

		// regions es Opcional
		if (data.has("regions")) 
		set_regions(data);
		
		// Entiendo que en el jsoanArray Animals tenemos un Json por cada tipo de
		// especie con
		// el numero (amount) n de animales de esa especie y el json que describe al
		// animal
		// para cada especie (primer for) hay amount animales de esa especie
		for (int i = 0; i < jsonArrayanimals.length(); i++) {
			JSONObject animalSpecie = jsonArrayanimals.getJSONObject(i);
			for (int j = 0; j < (int) animalSpecie.get("amount"); j++)
				_sim.add_animal(animalSpecie.getJSONObject("spec"));
		}
	}
	
	   //rs tiene la clave regions, ya esta comprobado en load data y si alguien lo usa tiene que asegurarse
	public void set_regions(JSONObject rs) {
			JSONArray jsonArrayregions = rs.getJSONArray("regions");

			// Mejor hacer for each de object?
			for (int i = 0; i < jsonArrayregions.length(); i++) {
				JSONObject region = jsonArrayregions.getJSONObject(i);

				// segun lo que entiendo rf y rt son los limites de la region por si queremos
				// generar una region que ocupe mas filas y columnas ?? no me queda claro como
				// va el mapa

				// rs es un json con jsonarrays (regions,animals) que dentro tienen json (cada
				// region/animal)
				// que dentro tienen un json array de enteros(row,col) u otro json (spec)
				int rf = region.getJSONArray("row").getInt(0);
				int rt = region.getJSONArray("row").getInt(1);
				int cf = region.getJSONArray("col").getInt(0);
				int ct = region.getJSONArray("col").getInt(1);
				JSONObject spec = region.getJSONObject("spec");
				// lamamos a set region por cada celda de la matriz que ocupe?
				for (int r = rf; r <= rt; r++)
					for (int c = cf; c <= ct; c++)
						_sim.set_region(r, c, spec);
			}
		}
	

	public void run(double t, double dt, boolean sv, OutputStream out) {
		// bucle de la simulacion guardamos el estado de antes y de despues

		PrintStream p = new PrintStream(out);

		// estado inicial de la simulacion
		JSONObject init_state = _sim.as_JSON();

		// Creamos la vista
		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(), m.get_cols(), m.get_rows());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		// Simulamos dt veces/segundos
		while (_sim.get_time() <= t) {
			_sim.advance(dt);

			// actualizamos la vista para ver cada iteracion de la simulacion
			if (sv)
				view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		// estado al final de simular dt veces
		JSONObject final_state = _sim.as_JSON();

		// json con los estados de antes y despues de simular
		JSONObject simulationStates = new JSONObject();
		simulationStates.put("in", init_state);
		simulationStates.put("out", final_state);

		// hace falta que pinte llaves y comas de estructura json?
		p.println(simulationStates.toString());

		// cerramos la vista al finalizar la simulacion
		if (sv)
			view.close();

	}

	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.get_genetic_code(), (int) a.get_position().getX(), (int) a.get_position().getY(),
					(int) Math.round(a.get_age()) + 2));// se puede sustituir por el 8 (int)Math.round(a.get_age())+2.
		return ol;
	}
	
	public void reset(int cols, int rows, int width, int height){
		this._sim.reset(cols, rows, width, height);
	}
	public void advance(double dt) {
		_sim.advance(dt);
	}
	//por que queremos tenerlo aqui ara llamarlo desde e main>???
	 public void addObserver(EcoSysObserver o) {
		 _sim.addObserver(o);
	 }
	 public void removeObserver(EcoSysObserver o) {
		 _sim.removeObserver(o);
	 }
	 //Se borra??
	 public double getDeltaTime() {
		 return _sim.get_time();
	 }
}
