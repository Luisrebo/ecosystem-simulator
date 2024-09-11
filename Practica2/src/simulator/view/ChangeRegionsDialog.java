package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controler;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	
	private JComboBox<String> rowFromCombo;
	private JComboBox <String>rowToCombo;
	private JComboBox <String> colFromCombo;
	private JComboBox <String> colToCombo;
	private JComboBox<String> regionsCombo;
	
	private DefaultTableModel _dataTableModel;
	private Controler _ctrl;
	private List<JSONObject> _regionsInfo;
	private String[] _headers = { "Key", "Value", "Description" };
	private int _status;

// TODO en caso de ser necesario, añadir los atributos aquí…
	ChangeRegionsDialog(Controler ctrl) {
		super((Frame) null, true);
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
		
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		// PANEL LABELAYUDA
		JPanel panelMensajeAyuda = new JPanel(new BorderLayout());
		JLabel mensajeAyuda = new JLabel(
				"<html><p>Select a region type, the rows/cols interval, and provide values for the parameters in the value column (default values \n are used for parameters with no value).</p><br></html>");
		panelMensajeAyuda.add(mensajeAyuda);
		mainPanel.add(panelMensajeAyuda);

		// TABLA DE DATOS
        // _dataTableModel modelo de tabla que incluye los parámetros dela region
		_dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};
		
		_dataTableModel.setColumnIdentifiers(_headers);
		JTable dataTable = new JTable(_dataTableModel);
		JScrollPane tabelScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 mainPanel.add(tabelScroll);
		
		 //FACTORIA DE REGIONES
		 _regionsInfo = Main._region_factory.get_info();
			
		//COMBO REGION TYPE
		JLabel regionTypeLabel=new JLabel("Region type:");		
		_regionsModel = new DefaultComboBoxModel<>();
		//añado los tipos de regiones al combo
		for(int i=0; i<_regionsInfo.size();i++) 
			_regionsModel.addElement(_regionsInfo.get(i).getString("type"));
		
		regionsCombo=new JComboBox<String>(_regionsModel);
		regionsCombo.addActionListener((e) -> {
			updateTableModel(regionsCombo.getSelectedIndex());
		});
		
		//RELLENO LA TABLA CON EL TIPO DE LA REGION 0
				updateTableModel(0);
					
		//COMBOS ROWS
		JLabel rowFromToLabel=new JLabel("Row from/to:");
		
		_fromRowModel = new DefaultComboBoxModel<>();
		 rowFromCombo=new JComboBox<String>(_fromRowModel);
		_toRowModel = new DefaultComboBoxModel<>();
		rowToCombo=new JComboBox<String>(_toRowModel);
		
		//COMBOS COLS
		JLabel colFromToLabel=new JLabel("Column from/to:");
		
		_fromColModel= new DefaultComboBoxModel<>();
		 colFromCombo=new JComboBox<String>(_fromColModel);
		_toColModel=new DefaultComboBoxModel<>();
		 colToCombo=new JComboBox<String>(_toColModel);
		
		//PANEL COMBOS BOX
		JPanel panelCombosBox=new JPanel();
		panelCombosBox.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(panelCombosBox);
		
		panelCombosBox.add(regionTypeLabel);
		panelCombosBox.add(regionsCombo);
		
		panelCombosBox.add(rowFromToLabel);
		panelCombosBox.add(rowFromCombo);
		panelCombosBox.add(rowToCombo);
		
		panelCombosBox.add(colFromToLabel);
		panelCombosBox.add(colFromCombo);
		panelCombosBox.add(colToCombo);
		
		//PANEL BOTONES OK Y CANCEL
		JPanel panelBotones=new JPanel();
		panelBotones.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(panelBotones);
		
		//BOTON OK
		JButton okButton=new JButton("ok");
		okButton.addActionListener((e) -> {
			actualizarRegionEnModelo();
			_status=1;
			setVisible(false);
		});
		
		//BOTON CANCEL
		JButton cancelButton=new JButton("cancel");
		cancelButton.addActionListener((e) -> {
			_status=0;
			setVisible(false);
		});
		
		panelBotones.add(cancelButton);
		panelBotones.add(okButton);
		
		
		setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
		pack();
		setResizable(false);
		setVisible(false);
	}

	public void open(Frame parent) {
		setLocation(//
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}
	private void updateTableModel(int _dataIdx) {
		
		//info={"data":{"factor":"food increase factor (optional, default 2.0)","food":"initial amount of food (optional, default 100.0)"},"type":"dynamic","desc":"dynamic region builder"}
		JSONObject info=_regionsInfo.get(_dataIdx);
				
		//data={"factor":"food increase factor (optional, default 2.0)","food":"initial amount of food (optional, default 100.0)"}
		JSONObject data=info.getJSONObject("data");

		//setNum rows se usa con setValueAt
		_dataTableModel.setNumRows(data.keySet().size());
		
		int currentRow=0;
		for (String key:data.keySet()) {
			//Object [] newRow= {key,"",datos.getString(key)}; 
			//_dataTableModel.addRow(newRow);duplica las filas si vuelves a dynamic
			//setValueAt(dato, row, column);
			_dataTableModel.setValueAt(key, currentRow, 0);
			_dataTableModel.setValueAt("", currentRow, 1);
			_dataTableModel.setValueAt(data.getString(key), currentRow, 2);
			currentRow+=1;
		}		
	}
	//Llamamos al controlador para que actualice la region que se ha modificado en la tabla
	private void actualizarRegionEnModelo() {
		JSONArray arrayRegion=new JSONArray();
		JSONObject region =new JSONObject();
		JSONObject regionTableToModel=new JSONObject();
		JSONArray jsonArray=new JSONArray();
		
		JSONArray row=new JSONArray();
		row.put(rowFromCombo.getSelectedIndex());
		row.put(rowToCombo.getSelectedIndex());
		regionTableToModel.put("row", row);
		
		JSONArray col=new JSONArray();
		col.put(colFromCombo.getSelectedIndex());
		col.put(colToCombo.getSelectedIndex());
		regionTableToModel.put("col", col);
		
		JSONObject spec=new JSONObject();
		spec.put("type",_regionsModel.getSelectedItem());
		
		JSONObject data=new JSONObject();
		
		//Problema en default no hay filas
		//factor y food podrian cambiar de orden (cuidado)
		for(int i=0; i<_dataTableModel.getRowCount();i++) {
			
			String valor1Tabla=_dataTableModel.getValueAt(i, 1).toString();
			if(!valor1Tabla.isEmpty()) 
				data.put(_dataTableModel.getValueAt(i, 0).toString(), valor1Tabla);
		/*String valorTablafactor=_dataTableModel.getValueAt(0, 1).toString();
		if(!valorTablafactor.isEmpty()) 
			data.put("factor", valorTablafactor);*/
		
			String valor2Tabla=_dataTableModel.getValueAt(1, 1).toString();
			if(!valor2Tabla.isEmpty()) 
				data.put(_dataTableModel.getValueAt(1, 0).toString(), valor2Tabla);
		/*String valorTablafood=_dataTableModel.getValueAt(1, 1).toString();	
		if(!valorTablafood.isEmpty()) 
			data.put("food", valorTablafood);*/
		
		}
		
		spec.put("data",data);
		
		regionTableToModel.put("spec", spec);
		arrayRegion.put(regionTableToModel);
		region.put("regions",arrayRegion);
		
		_ctrl.set_regions(region);
	
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		//ACTUALIZAMOS LOS COMBOS CON NUMERO DE FILAS Y COLUMNAS
		_fromRowModel.removeAllElements();
		_toRowModel.removeAllElements();
		_fromColModel.removeAllElements();
		_toColModel.removeAllElements();
	
		for(Integer i=0;i<map.get_rows();i++) {
			_fromRowModel.addElement(i.toString());
			_toRowModel.addElement(i.toString());
		}
		for(Integer i=0;i<map.get_cols();i++) {
			_fromColModel.addElement(i.toString());
			_toColModel.addElement(i.toString());
		}
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		//ACTUALIZAMOS LOS COMBOS CON NUMERO DE FILAS Y COLUMNAS
				_fromRowModel.removeAllElements();
				_toRowModel.removeAllElements();
				_fromColModel.removeAllElements();
				_toColModel.removeAllElements();
			
				for(Integer i=0;i<map.get_rows();i++) {
					_fromRowModel.addElement(i.toString());
					_toRowModel.addElement(i.toString());
				}
				
				for(Integer i=0;i<map.get_cols();i++) {
					_fromColModel.addElement(i.toString());
					_toColModel.addElement(i.toString());
				}

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

	}
}
//TODO añadir la descripción de todas las regiones a _regionsModel, para eso
//usa la clave “desc” o “type” de los JSONObject en _regionsInfo,
//ya que estos nos dan información sobre lo que puede crear la factoría.
//TODO crear un combobox que use _regionsModel y añadirlo al diálogo.
//TODO crear 4 modelos de combobox para _fromRowModel, _toRowModel,
//_fromColModel y _toColModel.
//TODO crear 4 combobox que usen estos modelos y añadirlos al diálogo.
//TODO crear los botones OK y Cancel y añadirlos al diálogo.
