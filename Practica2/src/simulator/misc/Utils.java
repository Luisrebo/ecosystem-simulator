package simulator.misc;

import java.util.Random;

public class Utils {
	public static final Random _rand = new Random();

	// nos aseguamos que value esta dentro del rango min-max y si no fuera asi se le
	// asigna el limite (max o min) segun por donde se exceda
	public static double constrain_value_in_range(double value, double min, double max) {
		value = value > max ? max : value;
		value = value < min ? min : value;
		return value;
	}

//randomizamos el valor value
	public static double get_randomized_parameter(double value, double tolerance) {
		assert (tolerance > 0 && tolerance <= 1);
		// nexdouble devuelve un numero cualquiera entre 0 y 1
		double t = (_rand.nextDouble() - 0.5) * 2 * tolerance; // valores entre -1 y 1 * tolerance (tolerance entre 0 y
																// 1 tamb)
		return value * (1 + t);
	}

}
