package simulator.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controler;
import simulator.model.Animal.Diet;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	// TODO definir atributos necesarios
	Controler _ctrl;
	String[] _headers;
	List<RegionData> _listaRegiones;

	public RegionsTableModel(Controler ctrl) {
		_listaRegiones=new ArrayList<>();
		_ctrl=ctrl;
		_headers = new String[Diet.values().length+3];
		fillHeaders();
		_ctrl.addObserver(this);
 
	}
	public void fillHeaders() {
		
		//Nos guardamos todos los valores de los Diet que existen
		Diet[] dietValues =Diet.values();
		_headers[0] = "Row";
		_headers[1] = "Col";
		_headers[2] = "Desc.";

		for (int i = 3; i < Diet.values().length + 3; i++) 
				_headers[i] = dietValues[i - 3].toString();

	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return _listaRegiones.size();
	}
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return _headers.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return _headers[column];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
     switch (columnIndex) {
	case 0: 
		return _listaRegiones.get(rowIndex).row();
		
	case 1:
		return _listaRegiones.get(rowIndex).col();
		
	case 2:
		return _listaRegiones.get(rowIndex).r().toString();
	
	case 3:
		//Los animales en header estan en el mismo orden que en diet.values
		return _listaRegiones.get(rowIndex).r().getAnimalsInfo().stream().filter((a)->a.get_diet().toString()==_headers[columnIndex]).count();
				
	case 4:
		return _listaRegiones.get(rowIndex).r().getAnimalsInfo().stream().filter((a)->a.get_diet().toString()==_headers[columnIndex]).count();
	
		}
		return null;
	}
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		_listaRegiones.clear();
		Iterator<MapInfo.RegionData> it=map.iterator();
		while(it.hasNext()) {
			RegionData region=it.next();
			_listaRegiones.add(region);
		}
		fireTableDataChanged();
		
	}
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		_listaRegiones.clear();
		Iterator<MapInfo.RegionData> it=map.iterator();
		while(it.hasNext()) {
			RegionData region=it.next();
			_listaRegiones.add(region);
		}
		fireTableDataChanged();
		
	}
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		_listaRegiones.clear();
		Iterator<MapInfo.RegionData> it=map.iterator();
		while(it.hasNext()) {
			RegionData region=it.next();
			_listaRegiones.add(region);
		}
		fireTableDataChanged();
	}
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		_listaRegiones.clear();
		Iterator<MapInfo.RegionData> it=map.iterator();
		while(it.hasNext()) {
			RegionData region=it.next();
			_listaRegiones.add(region);
		}
		fireTableDataChanged();
		
	}
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		_listaRegiones.clear();
		Iterator<MapInfo.RegionData> it=map.iterator();
		while(it.hasNext()) {
			RegionData region=it.next();
			_listaRegiones.add(region);
		}
		fireTableDataChanged();	
	}
	}