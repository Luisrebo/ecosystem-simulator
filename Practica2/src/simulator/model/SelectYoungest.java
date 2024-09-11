package simulator.model;

import java.util.List;

//estategia que dado el animal a elige el animal mas jovemn de as que no sea el mismo
//en region manager compruebo que a no este en as
public class SelectYoungest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as == null || as.isEmpty())
			return null;

		Animal youngest = null;
		double minAge = Double.MAX_VALUE;

		for (Animal animal : as)
			if (animal.get_age() < minAge) {
				minAge = animal.get_age();
				youngest = animal;
			}

		return youngest;
	}

}
