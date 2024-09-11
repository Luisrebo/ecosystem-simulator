package simulator.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import simulator.control.Controler;
import simulator.model.Animal.Diet;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.RegionInfo;

public class Car implements EcoSysObserver{
	private Controler _ctrl;
	private Map<RegionData,Integer> _mapaRegiones;
	
	public Car(Controler ctrl) {
		
		_mapaRegiones=new HashMap<>();
		
		
		_ctrl=ctrl;
		ctrl.addObserver(this);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		_mapaRegiones.clear();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		Iterator <MapInfo.RegionData> it=map.iterator();
		MapInfo.RegionData rdt;
		while(it.hasNext()) {
			rdt=it.next();
			if(!_mapaRegiones.containsKey(rdt))
				_mapaRegiones.put(rdt, 0);
			else if(rdt.r().getAnimalsInfo().stream().filter((a)->a.get_diet()==Diet.CARNIVORE).count()>3) {
				Integer pasosConMasDeTres=_mapaRegiones.get(rdt);
				_mapaRegiones.put(rdt, pasosConMasDeTres+1);
			}
				
		}
		
		
		
	}
public void Imprimir() {
	System.out.println("Car solucion");
	for( RegionData ri:_mapaRegiones.keySet())
		System.out.println("Region fila: "+ri.row()+"columna: "+ri.col()+"tiene mas de tres carnivoros en "+ _mapaRegiones.get(ri));
}
}
