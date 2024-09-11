package simulator.model;

import java.util.List;

//Estartegia que para el animal a elige el animal mas cercano que no sea el mismo
//EN region manager me encargo de que no se devuelva a en as
public class SelectClosest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as == null || as.isEmpty())
			return null;

		Animal closestAnimal = null;
		double minDistance = Double.MAX_VALUE;

		for (Animal animal : as) {
			double distance = a.get_position().distanceTo(animal.get_position());
			if (distance < minDistance) {
				minDistance = distance;
				closestAnimal = animal;
			}
		}

		return closestAnimal;
	}

}
