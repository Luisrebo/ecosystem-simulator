package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.model.Animal.State;

public class Simulator implements JSONable,Observable<EcoSysObserver> {
	private Factory<Animal> _animals_factory;
	private Factory<Region> _region_factory;
	private RegionManager _regionManager;
	private double _tiempoActual;
	private List<Animal> _listaAnimales;// todos los animales de la simulacion
	private List<EcoSysObserver> _listaObservadores;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {
		_regionManager = new RegionManager(cols, rows, width, height);
		_animals_factory = animals_factory;
		_region_factory = regions_factory;
		_tiempoActual = 0.0;
		_listaAnimales = new ArrayList<>();
		_listaObservadores=new ArrayList<>();
	}

	//Funciones de modelo :
	
	// Creo que estoestaba mal en la plantilla
	private void set_region(int row, int col, Region r) {
		// añade la región r al gestor de regiones en la posición (row,cols)
		_regionManager.set_region(row, col, r);

		// Notificacion a todos los observadores
		notifyObserverSetRegion(row,col,r);
			
	}

	public void set_region(int row, int col, JSONObject r_json) {
		// crea una región R a partir de r_json y llama a set_region (row,col,r)
		// Region r=_region_factory.create_instance(r_json);
		set_region(row, col, _region_factory.create_instance(r_json));
	}

	private void add_animal(Animal a) {
		// añade el animal a la lista de animales y lo registra en el gestor de
		// regiones.
		_listaAnimales.add(a);
		_regionManager.register_animal(a);
		
		//Notificacion a todos los observadores
		notifyObserverAddAnimal(a);	
	}

	public void add_animal(JSONObject a_json) {
		// crea un animal A a partir de a_json y llama a add_animal(A)
		add_animal(_animals_factory.create_instance(a_json));
	}

	public MapInfo get_map_info() {
		// devuelve el gestor de regiones.
		return _regionManager;
	}

	public List<? extends AnimalInfo> get_animals() {
		// lista que no se puede modificar desde fuera de los animales
		return Collections.unmodifiableList(_listaAnimales);
	}

	public double get_time() {
		// devuelve el tiempo actual.
		return _tiempoActual;
	}

	public void advance(double dt) {
		// avanza la simulación un paso (hay que tener cuidado de no modificar
		// la lista de animales mientras la estamos recorriendo)

		_tiempoActual += dt;
		// No puedo hacer remove mientras recorro?
		List<Animal> onlyAliveAnimals = new ArrayList<>();
		// quitamos los animales muertos de la lista y region
		// Lo hago con state o con atrb dead?
		// Si el animal esta muerto lo elimino de la region, de la lista aun no?

		for (Animal animal : _listaAnimales)
			// si esta muerto lo saco de la region
			// if (animal._dead)
			if (animal._state == State.DEAD)
				_regionManager.unregister_animal(animal);
			// si esta vivo lo guardo en lista vivos para mantenerlo
			else
				onlyAliveAnimals.add(animal);

		// Ahora solo quedan animales vivos en la lista
		_listaAnimales = onlyAliveAnimals;

		// actualizamos todos los animales vivos
		// actualizamos la region de los animales vivos
		for (Animal animal : _listaAnimales) {
			animal.update(dt);
			_regionManager.update_animal_region(animal);
		}
		// actualizamos todas las regiones
		_regionManager.update_all_regions(dt);

		// Dan a luz los animales embarazados
		// deveria volver a comrpobar si esta vivo?
		// lista de animales nacidos para no añadirles a la lista que estamos
		// recorriendo
		List<Animal> justBorn = new ArrayList<>();
		for (Animal animal : _listaAnimales) {
			if (animal.is_pregnant()) {

				justBorn.add(animal.deliver_baby()); // devuelve el hijo y pone el atr baby a null en animal
			}
		}
		// recorremos los animales que acaban de nacer y los añadimos a la lista
		// No les metemos en la region ya?
		for (Animal animal : justBorn)
			// funcion que registra al animal y lo añade a la lista
			add_animal(animal);
		
		notify_on_advanced( dt);
		
	}

	public JSONObject as_JSON() {
		return null;
	}
	public void reset(int cols, int rows, int width, int height) {
		// vaciamos/creamos una lista nueva
		this._listaAnimales=new ArrayList<>();
		this._tiempoActual=0.0;
		this._regionManager=new RegionManager(cols, rows, width, height);

		//Notificacion de reset a todos los observer
		notifyObserverReset();
	}

	//Funciones de observador:
	@Override
	public void addObserver(EcoSysObserver o) {
		// TODO Auto-generated method stub
		_listaObservadores.add(o);
		
		//Notificacion a todos los observadores
		//get_map_info y get_animals devuelven estructuras que no comprometen la integridad de los datos
		List<AnimalInfo> animals=new ArrayList<>(_listaAnimales);
		o.onRegister(get_time(), get_map_info(), animals);
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		// TODO Auto-generated method stub
		_listaObservadores.remove(o);
	}
	
	public void notifyObserverReset() {
		List<AnimalInfo> animals=new ArrayList<>(_listaAnimales);
		
		for(EcoSysObserver o:_listaObservadores)
			o.onReset(get_time(), get_map_info(),animals);
	}
	
	public void notifyObserverSetRegion(int row,int col,Region r){
		List<AnimalInfo> animals=new ArrayList<>(_listaAnimales);
		RegionInfo rInfo=r; //duda esta bien esto?
		
		for(EcoSysObserver o:_listaObservadores)
			o.onRegionSet(row, col, get_map_info(),rInfo );
			
	}
	
	public void notifyObserverAddAnimal(Animal a){
		List<AnimalInfo> animals=new ArrayList<>(_listaAnimales);
		AnimalInfo aInfo=a; //duda esta bien esto?
		
		for(EcoSysObserver o:_listaObservadores)
			o.onAnimalAdded(get_time(), get_map_info(), animals, aInfo);
	}
	private void notify_on_advanced(double dt) {
		List<AnimalInfo> animals = new ArrayList<>(_listaAnimales);
		// para cada observador o, invocar o.onAvanced(_time, _region_mngr, animals, dt)
		for(EcoSysObserver o:_listaObservadores)
			o.onAvanced(get_time(), get_map_info(), animals, dt);
		}

}
