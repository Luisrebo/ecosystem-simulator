package simulator.model;

import simulator.model.Animal.Diet;

public class DefaultRegion extends Region {

	@Override
	public double get_food(Animal a, double dt) {
		if (a._diet == Diet.CARNIVORE)
			return 0.0;
		else {
			int animalesHerbivoros=0;
			for(Animal herbivoro : getAnimals())
				if(herbivoro.get_diet()==Diet.HERBIVORE)
					animalesHerbivoros+=1;
			
			return 60.0 * Math.exp(-Math.max(0, animalesHerbivoros - 5.0) * 2.0) * dt;
		}
	}
	public String toString() {return "Default region";}
}
