package simulator.view;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import simulator.control.Controler;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import javax.swing.JSeparator;
class StatusBar extends JPanel implements EcoSysObserver {
	// TODO Añadir los atributos necesarios.
	JLabel _time;
	JLabel _numerOfAnimals;
	JLabel _dimension;
	Controler _ctrl;
	StatusBar(Controler ctrl) {
	initGUI();
	// TODO registrar this como observador
	_ctrl=ctrl;
	_ctrl.addObserver(this);
	}
	private void initGUI() {
	this.setLayout(new FlowLayout(FlowLayout.LEFT));
	this.setBorder(BorderFactory.createBevelBorder(1));
	// TODO Crear varios JLabel para el tiempo, el número de animales, y la
	// dimensión y añadirlos al panel. Puedes utilizar el siguiente código
	// para añadir un separador vertical:
	//
	// JSeparator s = new JSeparator(JSeparator.VERTICAL);
	// s.setPreferredSize(new Dimension(10, 20));
	// this.add(s);
	_time=new JLabel("Time: ");
	_numerOfAnimals=new JLabel("Total Animals: ");
	_dimension=new JLabel("Dimension: ");
	
	//Creamos separadores
	JSeparator separador = new JSeparator(JSeparator.VERTICAL);
	 separador.setPreferredSize(new Dimension(10, 20)); 
	 JSeparator separador2=new JSeparator(JSeparator.VERTICAL);
	 separador2.setPreferredSize(new Dimension(10, 20));
	 
	 //añadimos los componentes al status bar
	 this.add(_time);
	 this.add(separador);
	 this.add(_numerOfAnimals);
	 this.add(separador2);
	 this.add(_dimension);
	
	}
	// TODO el resto de métodos van aquí…
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		updateTimeLabel(time);
		updateAnimalsLabel(animals.size());
		updateDimensionLabel(map.get_height(), map.get_width(), map.get_rows(), map.get_cols());
		
	}
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		updateAnimalsLabel(animals.size());
	}
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		updateDimensionLabel(map.get_height(), map.get_width(), map.get_rows(), map.get_cols());
	}
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		updateTimeLabel(time);
		updateAnimalsLabel(animals.size());
	}
	public void updateTimeLabel(double time) {
		_time.setText("Time: "+time);
	}
	public void updateAnimalsLabel(int NumberOfAnimals) {
		_numerOfAnimals.setText("Total Animals: "+NumberOfAnimals);
	}
	public void updateDimensionLabel(int w,int h,int w1,int h1) {
		_dimension.setText("Dimension: "+w +"x"+h+" "+w1+"x"+h1);
	}
	}