package simulator.model;

public interface FoodSupplier {
	// Pedimos comida para el animal a durante dt segundos
	double get_food(Animal a, double dt);
}
