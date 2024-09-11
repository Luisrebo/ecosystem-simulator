package simulator.model;

import simulator.misc.Utils;
import simulator.model.Animal.Diet;

public class DynamicSupplyRegion extends Region {

	protected double _cantidadComida;
	protected double _factorCrecimiento;

	public DynamicSupplyRegion(double cantidadComida, double factorCrecimiento) {
		if (factorCrecimiento < 0)
			throw new IllegalArgumentException("El factor de crecimiento debe ser positivo");
		_cantidadComida = cantidadComida;
		_factorCrecimiento = factorCrecimiento;
	}

	@Override
	public double get_food(Animal a, double dt) {
		if (a._diet == Diet.CARNIVORE)
			return 0.0;
		else {
			int animalesHerbivoros=0;
			for(Animal herbivoro : getAnimals())
				if(herbivoro.get_diet()==Diet.HERBIVORE)
					animalesHerbivoros+=1;
			// cantidadASuministrar es la comida qe se suministra y se resta a la cantidad
			// de comida de la region
			double cantidadASuministrar = Math.min(_cantidadComida,
					60.0 * Math.exp(-Math.max(0, animalesHerbivoros - 5.0) * 2.0) * dt);
			_cantidadComida -= cantidadASuministrar;
			return cantidadASuministrar;
		}
		
	}

	@Override
	public void update(double dt) {
		// con prob 0.5 genero mas comida
		if (Utils._rand.nextDouble(1) >= 0.5)
			_cantidadComida += dt * _factorCrecimiento;

	}
	public String toString() {return "Dynamic region";}
}
