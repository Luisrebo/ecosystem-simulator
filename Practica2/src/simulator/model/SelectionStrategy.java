package simulator.model;

import java.util.List;

//Estrategia por la cual un animal elige a otro cercano de la lista de animales para interactuar (emparejarse,cazar...)
public interface SelectionStrategy {
	Animal select(Animal a, List<Animal> as);
}
