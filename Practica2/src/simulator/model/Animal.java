package simulator.model;

import java.util.List;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements AnimalInfo, Entity {

	public enum State {
		NORMAL, MATE, HUNGER, DANGER, DEAD
	}
    //antes protected
	public enum Diet {
		HERBIVORE, CARNIVORE
	}

	// codigo genetico para saber si 2animales se pueden emparejar
	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _energy;
	protected double _speed;
	protected double _age; // si la edad llega a un maximo muere
	protected double _desire; // deso del animal se usa para saber si un animal entra o sale de una pareja
	protected double _sight_range; // radio visual
	protected Animal _mate_target; // Animal con quien quiere emparejarse
	protected Animal _baby; // referencia que indica si el animal lleva unbebe que no ha nacido aun
	protected AnimalMapView _region_mngr; // gestor de regiones (null hasta que el propio AMV lo cambie
	protected SelectionStrategy _mate_strategy; // estrategia para buscar pareja
	protected boolean _dead; // por defecto a falso

	// constructora para objetos iniciales
	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed,
			SelectionStrategy mate_strategy, Vector2D pos) {
		if (genetic_code == null || genetic_code.trim().isEmpty())
			throw new IllegalArgumentException("El codigo genetico no pede ser un valor nulo o una cadena vacia");
		if (sight_range < 0)
			throw new IllegalArgumentException("El radio visual debe ser un numero positivo");
		if (init_speed < 0)
			throw new IllegalArgumentException("La velocidad inicial debe ser un valor positivo");
		if (mate_strategy == null)
			throw new IllegalArgumentException("La estrategia de apareamiento no puede ser un valor null");
		_genetic_code = genetic_code;
		_diet = diet;
		_sight_range = sight_range;
		_speed = Utils.get_randomized_parameter(init_speed, 0.1);
		_mate_strategy = mate_strategy;
		_pos = pos; // puede ser null y si lo fuera se inicializaria posteriormente en init
		_state = State.NORMAL;
		_energy = 100.0;
		_desire = 0.0;
		_dest = null;
		_mate_target = null;
		_region_mngr = null;
	}

	// constructor para animal nacido de otros dos
	protected Animal(Animal p1, Animal p2) {
		_dest = null;
		_baby = null;
		_mate_target = null;
		_region_mngr = null;
		_state = State.NORMAL;
		_desire = 0.0;
		_genetic_code = p1.get_genetic_code();
		_diet = p1.get_diet();
		_energy = (p1._energy + p2._energy) / 2.0;
		// posicion aleatoria cercana a p1.pos
		_pos = p1.get_position().plus(Vector2D.get_random_vector(-1, 1).scale(60.0 * (Utils._rand.nextGaussian() + 1)));
		// _pos=p1.get_position();
		// mutacion de las velocidades de sus padres
		_sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2, 0.2);
		// mutacion de las velocidades de los padres
		_speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, 0.2);
		// esta linea es inventada
		_mate_strategy = p1._mate_strategy;

	}

	// el gestor de regiones llama a este metodo al añadir un animal a la simulacion
	public void init(AnimalMapView reg_mngr) {
		_region_mngr = reg_mngr;
		if (_pos == null) {
			_pos = new Vector2D(Utils._rand.nextDouble(0, _region_mngr.get_width()),
					Utils._rand.nextDouble(0, _region_mngr.get_height()));
			
		}
		// vale con mandarle el maximo valor a nextdouble porque va a coger valores
		// entre cero y el maximo aunque podria hacerse como arriba
		_dest = new Vector2D(Utils._rand.nextDouble(0, _region_mngr.get_width()),
				Utils._rand.nextDouble(0, _region_mngr.get_height()));
	
		// Ajustar posiciones
		this.ajustar_posicion();
	}

	// el animal se mueve hacia dest con velocidad speed
	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}

	public Animal deliver_baby() {
		Animal aux = _baby;
		_baby = null;
		return aux;
	}

	public JSONObject as_JSON() {
		JSONObject json_animal = new JSONObject();
		json_animal.put("pos", _pos);
		json_animal.put("gcode", _genetic_code);
		json_animal.put("diet", _diet);
		json_animal.put("state", _state);
		return json_animal;
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public State get_state() {
		// TODO Auto-generated method stub
		return _state;
	}

	@Override
	public Vector2D get_position() {
		// TODO Auto-generated method stub
		return _pos;
	}

	@Override
	public String get_genetic_code() {
		// TODO Auto-generated method stub
		return _genetic_code;
	}

	@Override
	public Diet get_diet() {
		// TODO Auto-generated method stub
		return _diet;
	}

	@Override
	public double get_speed() {
		// TODO Auto-generated method stub
		return _speed;
	}

	@Override
	public double get_sight_range() {
		// TODO Auto-generated method stub
		return _sight_range;
	}

	@Override
	public double get_energy() {
		// TODO Auto-generated method stub
		return _energy;
	}

	@Override
	public double get_age() {
		// TODO Auto-generated method stub
		return _age;
	}

	@Override
	public Vector2D get_destination() {
		// TODO Auto-generated method stub
		return _dest;
	}

	@Override
	public boolean is_pregnant() {
		// TODO Auto-generated method stub
		return _baby != null;
	}

	public void setLife(boolean willDead) {
		_dead = willDead;
	}

	protected double suma_resta_entre_limites(double atributo, double cantidad, double limite_superior,
			double limite_inferior) {
		double atributoActualizado = atributo + cantidad;
		if (atributoActualizado < limite_inferior)
			atributoActualizado = limite_inferior;
		else if (atributoActualizado > limite_superior)
			atributoActualizado = limite_superior;
		return atributoActualizado;
	}

	protected void ajustar_posicion() {
		double x = this._pos.getX();
		double y = this._pos.getY();
		int width = this._region_mngr.get_width();
		int height = this._region_mngr.get_height();

		while (x >= width)
			x = (x - width);
		while (x < 0)
			x = (x + width);
		while (y >= height)
			y = (y - height);
		while (y < 0)
			y = (y + height);

		_pos = new Vector2D(x, y);
	}

	// devuelve true si el animal esta fuera del mapa
	protected boolean animal_fuera_del_mapa() {
		return (this._pos.getX() >= _region_mngr.get_width()) || (this._pos.getX() < 0)
				|| (this._pos.getY() >= _region_mngr.get_height()) || (this._pos.getY() < 0);
	}
}
