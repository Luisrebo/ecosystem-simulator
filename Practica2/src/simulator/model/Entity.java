package simulator.model;

//La implementan los objetos que tienen que ser actualizados en cada iteracion de la simulacion
public interface Entity {
	public void update(double dt);
}
