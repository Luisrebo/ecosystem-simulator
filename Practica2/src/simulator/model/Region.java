package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {

	protected List<Animal> _animalsInRegionList;

	public Region() {
		_animalsInRegionList = new ArrayList<>();
	}

	@Override
	public double get_food(Animal a, double dt) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub

	}

	final void add_animal(Animal a) {
		_animalsInRegionList.add(a);
	}

	final void remove_animal(Animal a) {
		_animalsInRegionList.remove(a);
	}

	final List<Animal> getAnimals() {
		return _animalsInRegionList;
	}

	public int numberOfAnimals() {
		return _animalsInRegionList.size();
	}

	public JSONObject as_JSON() {
		// TO Do
		return null;
	}
	@Override
	public List<AnimalInfo> getAnimalsInfo(){
		return new ArrayList<>(_animalsInRegionList);
		// se puede usar Collections.unmodifiableList(_animals)
		//Las dos opciones en la línea del return son válidas, la primera es segura para programación concurrente
		//mientras la segunda no
	}
}
