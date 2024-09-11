package simulator.model;

import java.util.List;

//Estrategia que para el animal a selecciona el primero (que no sea el mismo) de la lista as
public class SelectFirst implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		return as.isEmpty() ? null : as.get(0);
	}

}
