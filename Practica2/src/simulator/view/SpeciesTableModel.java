package simulator.view;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controler;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.Animal.State;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	// TODO definir atributos necesarios
	private Controler _ctrl;
	private String[] _headers;
	//private List<List<Integer>> _matrizDatos;
	private Map<String,List<Integer>> _mapa;
	private List<String> _species;
	public SpeciesTableModel(Controler ctrl) {
		_species=new ArrayList<>();
		_mapa=new HashMap<>();
		_headers = new String[State.values().length+1];
		fillHeaders();
		_ctrl=ctrl;
		_ctrl.addObserver(this);
		
	}
	// TODO el resto de métodos van aquí …
	public void fillHeaders() {
		
		//Nos guardamos todos los valores de los State que existen
		State[] stateValues =State.values();
		_headers[0] = "Species";

		for (int i = 1; i < State.values().length + 1; i++) 
				_headers[i] = stateValues[i - 1].toString();

	}
	@Override
	public String getColumnName(int column) {
		
		return _headers[column];
	}
	
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return _species.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return _headers.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		// TODO Auto-generated method stub
		if(columnIndex==0)
			return _species.get(rowIndex);
		else 
			return _mapa.get(_species.get(rowIndex)).get(columnIndex-1);
	
		//else return null;
		
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		//Un array, una fila por cada especie nueva.
				_mapa.clear();
				_species.clear();
				//Aqui estoy actualizando las especies si en esta iteracion no hay una especie desaparecera la palabra especies de la columna especies 
				//aunque haya existido en otro paso de la smulacion
				for (AnimalInfo animalInfo:animals) {
					if(!(_species.stream().anyMatch((specie)->specie==animalInfo.get_genetic_code()))) {
						_species.add(animalInfo.get_genetic_code());
						//_mapa.put(animalInfo.get_genetic_code(), new ArrayList<>(_headers.length-1));
						List<Integer> list = new ArrayList<>(_headers.length - 1);
						for (int i = 0; i < _headers.length - 1; i++) {
						    list.add(0); // Agregar el valor 0 a cada posición del ArrayList
						}
						_mapa.put(animalInfo.get_genetic_code(), list);
						
					}
					agregarAnimal(animalInfo);
				}
					
				
				fireTableDataChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		_mapa.clear();
		_species.clear();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		//si el animal que acabo de añadir es de una especie/genetic code nuevo hasta ahora
		//lo agrego al array de especies para ponerlo en la tabla
		//Y añado al HashMap un array vacio de tamaño numero d states para
		//representar el # de animales que hay en ese estado 
		if(!(_species.stream().anyMatch((specie)->specie==a.get_genetic_code()))) {
			_species.add(a.get_genetic_code());
			//_mapa.put(a.get_genetic_code(), new ArrayList<>(_headers.length-1));
			List<Integer> list = new ArrayList<>(_headers.length - 1);
			for (int i = 0; i < _headers.length - 1; i++) {
			    list.add(0); // Agregar el valor 0 a cada posición del ArrayList
			}
			
			_mapa.put(a.get_genetic_code(), list);

			
		}
		
		//Añado e animal al mapa
		//Dentro de su especie sumo 1 en la posicion del estado que tenga
		agregarAnimal(a);
			
		fireTableDataChanged();
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		//Un array, una fila por cada especie nueva.
		_mapa.clear();
		_species.clear();
		//Aqui estoy actualizando las especies si en esta iteracion no hay una especie desaparecera la palabra especies de la columna especies 
		//aunque haya existido en otro paso de la smulacion
		for (AnimalInfo animalInfo:animals) {
			if(!(_species.stream().anyMatch((specie)->specie==animalInfo.get_genetic_code()))) {
				_species.add(animalInfo.get_genetic_code());
				//_mapa.put(animalInfo.get_genetic_code(), new ArrayList<>(_headers.length-1));
				List<Integer> list = new ArrayList<>(_headers.length - 1);
				for (int i = 0; i < _headers.length - 1; i++) {
				    list.add(0); // Agregar el valor 0 a cada posición del ArrayList
				}
				_mapa.put(animalInfo.get_genetic_code(), list);
				
			}
			agregarAnimal(animalInfo);
		}
			
		
		fireTableDataChanged();
	}
	//Sumamos 1 al array de la especie del animal a en la posicion que represente el estado del animal a
	//Si era null ponemos un 1
	public void agregarAnimal(AnimalInfo animal) {
		if(_mapa.get(animal.get_genetic_code()).get(animal.get_state().ordinal())==0)
		_mapa.get(animal.get_genetic_code()).set(animal.get_state().ordinal(),1);
		else {
			
			int valorAnterior = _mapa.get(animal.get_genetic_code()).get(animal.get_state().ordinal());
			valorAnterior += 1; // Sumar 1 al valor anterior
			_mapa.get(animal.get_genetic_code()).set(animal.get_state().ordinal(), valorAnterior);

			
			
		}	
	}	
	}
	