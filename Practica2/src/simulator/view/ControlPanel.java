package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controler;
//import simulator.misc.Utils;

class ControlPanel extends JPanel {
	private Controler _ctrl;
	
	
	//JComponents
	private JToolBar _toolBar;
	private JFileChooser _fc;
	private ChangeRegionsDialog _changeRegionsDialog;
	private MapWindow _mapWindow;
	
	// utilizado en los botones de run/stop
	private boolean _stopped = true; 
	
	//Botones
	private JButton _quitButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JButton _changeRegionButton;
	private JButton _viewButton ;
	private JButton _openFileButton;
	private JSpinner _spinnerSteps;
	private JTextField _textFieldDt;
	
	//Lista de botones para deshabilitarlos rapido
	private List<JButton> botones;

// TODO añade más atributos aquí …
	ControlPanel(Controler ctrl) {
		_ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		
		//Lista de botones para deshabilitarlos rapido
		botones=new ArrayList<>();
		
		// _toolaBar.addSeparator() para añadir la línea de separación vertical
		
		//Tool Bar
		_toolBar = new JToolBar("tool bar");
		_toolBar.setFloatable(false);
		add(_toolBar, BorderLayout.PAGE_START);
				
		//File Chooser
		_fc=new JFileChooser();
		//_fc.setCurrentDirectory(new File("/desktop"));
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir")+"/resources/examples"));
		
		//Dialogo _changeRegionsDialog
		_changeRegionsDialog=new ChangeRegionsDialog(_ctrl);
						
		//BOTONES
		//Open File Button
		_openFileButton=iniJButton("Run","resources/icons/open.png",true);
		_openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				int cargafichero = _fc.showOpenDialog(ViewUtils.getWindow(ControlPanel.this));	
				if(cargafichero==JFileChooser.APPROVE_OPTION) {
					try {
		                File selectedFile = _fc.getSelectedFile();
		                InputStream in = new FileInputStream(selectedFile);
		                JSONObject json = new JSONObject(new JSONTokener(in));
		                int width,height,rows,cols;
		                if(json.has("width")&&json.has("height")&&json.has("rows")&&json.has("cols")) {
		                	width=json.getInt("width");
		                	height=json.getInt("height");
		                	rows=json.getInt("rows");
		                	cols=json.getInt("cols");
		                	_ctrl.reset(cols, rows, width, height);
		                	_ctrl.load_data(json);
		                }else {throw new IllegalArgumentException("La region del archivo" + selectedFile+" tiene datos de entrada erroneos"); }
		                // Procesar el JSON aquí
		            } catch (FileNotFoundException ex) {
		                ViewUtils.showErrorMsg(ex.getMessage());
		                // Manejar el error de archivo no encontrado aquí
		            }
					_runButton.setEnabled(true);
				}
			}});
		
		//Run Button
		_runButton = iniJButton("Run", "resources/icons/run.png", false);
		_runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_stopped=false;
				_stopButton.setEnabled(true);
				eneableButtons(false);
				run_sim( (int)_spinnerSteps.getValue(),Double.parseDouble(_textFieldDt.getText()));
				//run_sim(10000, 0.03);
				eneableButtons(true);
			}
		});
		
		// Quit Button
		_quitButton=iniJButton("Quit","resources/icons/exit.png",true);
		_quitButton.addActionListener((e) -> ViewUtils.quit(this));
		
		//Change Region Button
		_changeRegionButton=iniJButton("Change Region","resources/icons/regions.png",true);
		_changeRegionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_changeRegionsDialog.open(ViewUtils.getWindow(ControlPanel.this));
			}
		});
		
		//View Button
		_viewButton=iniJButton("View Simulation","resources/icons/viewer.png",true);
		_viewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//VENTANA CON EL MAPA SIMULADO
				_mapWindow=new MapWindow( ViewUtils.getWindow(ControlPanel.this), _ctrl);
			}
		});
		
		//Stop Button
		_stopButton=iniJButton("Stop","resources/icons/stop.png",true);
		_stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_stopped=true;
				eneableButtons(true);
				_stopButton.setEnabled(false);
			}
		});
		
		//Spinner Steps Y su JLabel
		JLabel stepsLabel=new JLabel("Steps:");
		_spinnerSteps= new JSpinner(new SpinnerNumberModel(10000, 1, 10000, 100));
		// initial value = 10000 (first parameter)
		// initial range value = 1 (second parameter)
		// final range value = 10000 (third parameter)
		// increment = 100 (fourth parameter)
		_spinnerSteps.setToolTipText("simulation steps");
		_spinnerSteps.setMaximumSize(new Dimension(80, 40));
		_spinnerSteps.setPreferredSize(new Dimension(80, 40));
		_spinnerSteps.setMinimumSize(new Dimension(80, 40));
				
		// JTextField Delta Time y su JLabel
		_textFieldDt = new JTextField();
		_textFieldDt.setToolTipText("delta time");
		// inicializamos el valor
		_textFieldDt.setText(Double.toString(_ctrl.getDeltaTime()));//Duda
		_textFieldDt.setPreferredSize(new Dimension(80, 40));
		_textFieldDt.setMaximumSize(new Dimension(80, 40));
		JLabel dtLabel = new JLabel("Delta-Time:");
		
		//Aqui añadimos los componentes/botones en orden a toolBar
		_toolBar.add(_openFileButton);
		_toolBar.addSeparator();
		_toolBar.add(_changeRegionButton);
		_toolBar.add(_viewButton);
		_toolBar.addSeparator();
		_toolBar.add(_runButton);
		_toolBar.add(_stopButton);
		_toolBar.addSeparator();
		_toolBar.add(stepsLabel);
		_toolBar.add(_spinnerSteps);
		_toolBar.add(dtLabel);
		_toolBar.add(_textFieldDt);
		_toolBar.add(Box.createGlue()); // this aligns the button to the right
		_toolBar.addSeparator();
		_toolBar.add(_quitButton);
		
		//dt con el main 
		
		// TODO Inicializar _changeRegionsDialog con instancias del diálogo de cambio
		// de regiones
		this.setVisible(true);
	}
		// TODO el resto de métodos van aquí…
	
	// INICIALIZAMOS LOS JBUTTON
		public JButton iniJButton(String toolTip, String imageIconPath, boolean eneabled) {
			JButton b = new JButton();
			b.setToolTipText(toolTip);
			b.setIcon(new ImageIcon(imageIconPath));
			b.setEnabled(eneabled);
			b.setVisible(true);
			botones.add(b);
			return b;
		}
	
		private void run_sim(int n, double dt) {
			if (n > 0 && !_stopped) {
				//_runButton.setEnabled(false);
			try {
			_ctrl.advance(dt);
			
			} catch (Exception e) {
			// TODO llamar a ViewUtils.showErrorMsg con el mensaje de error
			// que corresponda
			// TODO activar todos los botones
				eneableButtons(true);
			_stopped = true;
			}
			SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			} else {
			// TODO activar todos los botones
				eneableButtons(true);
				_stopButton.setEnabled(false);
			_stopped = true;
			}
			}
		
		//Activamos o desactivamos todos los botones
		public void eneableButtons(boolean eneabled) {
			for(JButton b: botones)
				b.setEnabled(eneabled);
		}
}