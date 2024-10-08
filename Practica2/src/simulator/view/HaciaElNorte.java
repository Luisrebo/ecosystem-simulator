package simulator.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simulator.control.Controler;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class HaciaElNorte implements EcoSysObserver {
	
	private Controler _ctrl;
	private Map<AnimalInfo,Double> _animales;
	private List<Integer> _haciaElNorte;
	
	
	public HaciaElNorte(Controler ctrl) {
		
		
		_animales=new HashMap<>();
		_haciaElNorte=new ArrayList<>();
		_ctrl=ctrl;
		_ctrl.addObserver(this);
	}
	
	public void Imprimir() {
		for(int i=0;i<_haciaElNorte.size();i++) {
			
			System.out.println("en la iteracion "+i+ " fuerom al norte : "+_haciaElNorte.get(i)+" Animales");
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		_animales.clear();
		_haciaElNorte.clear();
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
		// TODO Auto-generated method stub
		
		Integer haciaElNorteEnEstaIteracion=0;
		for(AnimalInfo a:animals) {
			
			if(_animales.containsKey(a)) {
				
				if(a.get_position().getY()>_animales.get(a))
					haciaElNorteEnEstaIteracion+=1;
				
			}
			_animales.put(a, a.get_position().getY());
			
		}
		_haciaElNorte.add(haciaElNorteEnEstaIteracion);
			//_haciaElNorte.add(animals.stream().filter((a)->a.get_position().getY()>_animales.get(a)).count));
			
	}


}
