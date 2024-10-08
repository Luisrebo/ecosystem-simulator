package simulator.model;

import java.util.List;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONObject;

public class RegionManager implements AnimalMapView {
	protected int _cols;
	protected int _rows;
	protected int _width;
	protected int _height;
	protected int _region_width;
	protected int _region_height;
	// private List<List<Region>> _regions; //Matriz de regiones
	public Region[][] _regions; // Matriz de regiones
	private Map<Animal, Region> _animal_region;

	public RegionManager(int cols, int rows, int width, int height) {
		_cols = cols;
		_rows = rows;
		_width = width;
		_height = height;
		_region_width = _width / _cols;
		_region_height = _height / _rows;
		this._animal_region = new HashMap<>();
		this._regions = new Region[rows][cols];
		iniMatriz();
	}

	public void iniMatriz() {
		// Inicializamos la matriz a defaultregion
		for (int i = 0; i < _rows; i++)
			for (int j = 0; j < _cols; j++)
				_regions[i][j] = new DefaultRegion();
	}

	@Override
	public int get_cols() {
		// TODO Auto-generated method stub
		return _cols;
	}

	@Override
	public int get_rows() {
		return _rows;
	}

	@Override
	public int get_width() {
		return _width;
	}

	@Override
	public int get_height() {
		return _height;
	}

	@Override
	public int get_region_width() {
		return _region_width;
	}

	@Override
	public int get_region_height() {
		// TODO Auto-generated method stub
		return _region_height;
	}

	@Override
	public double get_food(Animal a, double dt) {
		// comprobar si es null?
		return _animal_region.get(a).get_food(a, dt);
	}

	void set_region(int row, int col, Region r) {
		// modificamos la region en las cordenadas dadas que pasa a ser r
		List<Animal> swapAnimals = _regions[row][col].getAnimals();
		for (Animal a : swapAnimals) {
			_animal_region.put(a, r);
			r.add_animal(a);
		}
		_regions[row][col] = r;
	}

	void register_animal(Animal a) {
		// encuentra la regiÃ³n a la que tiene que pertenecer el animal (apartir de su
		// posiciÃ³n) y lo aÃ±ade
		a.init(this);

		int fila = (int) (a.get_position().getY() / _region_height); // me obliga a casting
		int columna = (int) (a.get_position().getX() / _region_width); // me obliga a casting

		_regions[fila][columna].add_animal(a);
		_animal_region.put(a, _regions[fila][columna]);

	}

	void unregister_animal(Animal a) {
		// quita el animal de la regiÃ³n a la que pertenece y actualiza _animal_region.
		_animal_region.get(a).remove_animal(a);
		_animal_region.remove(a);
	}

	void update_animal_region(Animal a) {

		// encuentra la regiÃ³n a la que tiene que pertenecer el animal
		// si es distinta de su regiÃ³n actual lo aÃ±ade a la nueva regiÃ³n
		int fila = (int) (a.get_position().getY() / _region_height); // me obliga a casting
		int columna = (int) (a.get_position().getX() / _region_width); // me obliga a casting
		if (_animal_region.get(a) != _regions[fila][columna]) {
			_animal_region.remove(a); // hace falta el delete?
			_animal_region.put(a, _regions[fila][columna]);
		}
	}

	void update_all_regions(double dt) {
		// llama a update de todas la regiones en la matriz de regiones.
		for (int i = 0; i < _rows; i++)
			for (int j = 0; j < _cols; j++)
				_regions[i][j].update(dt);
	}

	@Override
	public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter) {
		List<Animal> animalesEnRango = new ArrayList<>();
		Iterator<Animal> it = _animal_region.keySet().iterator();// me da una lista de las keys(animals totales)
		while (it.hasNext()) {
			Animal animal = it.next();
			// si el animal esta en rango y cumple la condicion filter (carnivoro,cod
			// genetico...)
			if (a.get_position().distanceTo(animal.get_position()) <= a.get_sight_range() && filter.test(animal)
					&& a != animal)
				animalesEnRango.add(animal);
		}

		return animalesEnRango;
	}

	public JSONObject as_JSON() {
		// devuelve una estructura JSON de la siguiente forma
		/*
		 * "regiones":[ , ,...],ð�‘œ1 ð�‘œ2 } donde es una estructura JSON que
		 * corresponde a una regiÃ³n y tiene la siguiente formað�‘œð�‘– { "row": i,
		 * "col": j, 13 "data": r
		 */return null;
	}

	//mapInfo extends iterator, animalmapview extends mapInfo, this extends animalmapview
	//este metodo nos data un iterador a la matriz de regiones para recorrerla desde fuera
	//utulizaremos la clase regiondata para asegurarnos que la region que mandamos no es modificable
	//region data interfaz de region no modificable parecido a animalinfo
	@Override
	public Iterator<RegionData> iterator() {
		
		return new RegionIterator();
	}
	//iterador para reccorer la matriz de regiones de izq a dcha
	public class RegionIterator implements Iterator<RegionData>{
		int filaActual=0;
		int colActual=0;
		
		@Override
        public boolean hasNext() {
            // Comprobar si aún quedan elementos en la matriz
            return filaActual < _regions.length && colActual < _regions[filaActual].length;
        }
		
		//Avanzamos de izq a dcha, cuando hemos leido toda la fila col=0 y fila+1
		//devolvemos la region actual y avanzamos a la siguiente
		@Override
        public RegionData next() {
            if (!hasNext()) {
            	//que se hace aqui? return null;????
            	throw new IllegalArgumentException("No hay elementos en la matriz");
            }
            Region currentRegion = _regions[filaActual][colActual];
        //    RegionInfo regionInfo = currentRegion; //se hace asi?
            RegionData data = new RegionData(filaActual, colActual, currentRegion);

            // Avanzar a la siguiente posición
            colActual++;
            if (colActual >= _regions[filaActual].length) {
            	colActual = 0;
                filaActual++;
            }

            return data; 
        }
	}
}

/*
 * Para evitar esto, lo mejor seria comprobar que la anchura es divisible por el
 * numero de columnas y la altura por el numero de filas:
 * 
 * if ( _width % _cols != 0 || _height % _rows != 0) throw ....
 * 
 * Otra posibilidad (aunque me gusta más la primera) es añadir 1 a _region_width
 * y _region_height si es necesario
 * 
 * _region_width = _width / _cols + (width % cols != 0 ? 1 :0 ); _region_height
 * = _height / _rows + (height % rows != 0 ? 1 :0 );
 */
