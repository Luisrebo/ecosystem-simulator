package simulator.model;

import java.util.List;

public interface EcoSysObserver {
	//map gestor de regiones //animals lista de animales //time tiempo actual de la simulacion
	//dt tiempo del paso dela simulacion
	
	void onRegister(double time, MapInfo map, List<AnimalInfo> animals);

	void onReset(double time, MapInfo map, List<AnimalInfo> animals);

	void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a);

	void onRegionSet(int row, int col, MapInfo map, RegionInfo r);

	void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt);
}