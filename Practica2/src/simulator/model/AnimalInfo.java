package simulator.model;

import simulator.misc.Vector2D;

//ENTIENDO QUE ESTO ES NECESARIO AQUI para state y diet
import simulator.model.Animal.State;
import simulator.model.Animal.Diet;

//import simulator.misc.Vector2D;

//Para pasar una estancia de la clase Animal asegurandonos de que no va a ser modificada 
public interface AnimalInfo extends JSONable {
	public State get_state();

	public Vector2D get_position();

	public String get_genetic_code();

	public Diet get_diet();

	public double get_speed();

	public double get_sight_range();

	public double get_energy();

	public double get_age();

	public Vector2D get_destination();

	public boolean is_pregnant();
}
