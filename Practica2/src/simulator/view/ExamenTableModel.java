package simulator.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controler;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.Animal.Diet;
import simulator.model.Animal.State;
import simulator.model.MapInfo.RegionData;

public class ExamenTableModel extends AbstractTableModel implements EcoSysObserver {

	
	private static class ExamenStruct {
		private long _herviborosDanger;
		private long _carnivorosHunger;
		

		ExamenStruct(long hervibir,long carnivor) {
			_herviborosDanger=hervibir;
			_carnivorosHunger=carnivor;
		}
	}

	
	Controler _ctrl;
	String[] _headers= {"Step","Herbivoros danger","Carnivoros hunger"};
	List<ExamenStruct> _lista;

	public ExamenTableModel(Controler ctrl) {
		//_listaRegiones=new ArrayList<>();
		
		_lista=new ArrayList<>();
		_ctrl=ctrl;
		_ctrl.addObserver(this);
 
	}
public String getColumnName(int column) {
		
		return _headers[column];
	}
	
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return _lista.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return _headers.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		switch (columnIndex) {
		case 0: 
			 return rowIndex;
			
		case 1:
			return _lista.get(rowIndex)._herviborosDanger;
			
		case 2:
			return _lista.get(rowIndex)._carnivorosHunger;
		
			}
			return null;
		
		
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		_lista.clear();
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
		
				_lista.add(new ExamenStruct(animals.stream().filter((a)->a.get_diet()==Diet.HERBIVORE&&a.get_state()==State.DANGER).count(), 
						animals.stream().filter((a)->a.get_diet()==Diet.CARNIVORE && a.get_state()==State.HUNGER).count()));
				fireTableDataChanged();
	}

}
