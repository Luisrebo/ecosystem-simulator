package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import simulator.control.Controler;

public class MainWindow extends JFrame {
	 private Controler _ctrl;
	 private InfoTable tablaRegiones;
	 private InfoTable tablaEspecies;
	 private InfoTable tableexamen;
	 public MainWindow(Controler ctrl) {
	 super("[ECOSYSTEM SIMULATOR]");
	 _ctrl = ctrl;
	 initGUI();
	 }
	 private void initGUI() {
	 JPanel mainPanel = new JPanel(new BorderLayout());
	 setContentPane(mainPanel);
	 
	 // CONTROLPANEL
	  ControlPanel controlPanel =new ControlPanel(_ctrl);
	 mainPanel.add(controlPanel,BorderLayout.PAGE_START);
	 
	 // STATUSBAR
	 StatusBar statusBar=new StatusBar(_ctrl);
	 mainPanel.add(statusBar,BorderLayout.PAGE_END);
	 
	 
	 // CONTENT PANEL panel con las tablas (usa un BoxLayout vertical)
	 JPanel contentPanel = new JPanel();
	 contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); //duda uso mejor page_axis?
	 mainPanel.add(contentPanel, BorderLayout.CENTER);

	 
	 //Tablas
	 tablaEspecies=new InfoTable("Species", new SpeciesTableModel(_ctrl));
	 tablaEspecies.setPreferredSize(new Dimension(500, 250));
	 //tablaEspecies.setMinimumSize(new Dimension(500, 250));
	 // tablaEspecies.setMaximumSize(new Dimension(500, 250));
	 //tablaEspecies.setSize(new Dimension(500, 250));
	 
	 tablaRegiones=new InfoTable("Regions", new RegionsTableModel(_ctrl));
	 tablaRegiones.setPreferredSize(new Dimension(500, 250));
	 //tablaRegiones.setMinimumSize(new Dimension(500, 250));
	 //tablaRegiones.setMaximumSize(new Dimension(500, 250));
	 
	 tableexamen=new InfoTable("Examen", new ExamenTableModel(_ctrl));
	 tablaRegiones.setPreferredSize(new Dimension(500, 250));
	 
//	 JScrollPane panelTablaEspecies=new JScrollPane(tablaEspecies);
//	 JScrollPane panelTablaRegiones=new JScrollPane(tablaRegiones);
	 
	 //contentPanel.add(panelTablaEspecies);
	 //contentPanel.add(panelTablaRegiones);
	 
	 contentPanel.add(tablaEspecies);
	 contentPanel.add(tablaRegiones);
	 contentPanel.add(tableexamen);
	
	
	 
	//DEFINE LOS METODOS DE LA VENTANA (ej. cerrar con el aspa)
		addWindowListener(new WindowListener() {
		    @Override
		    public void windowOpened(WindowEvent e) {}
		    @Override
		    public void windowClosing(WindowEvent e) {
		    //Comento esto para depurar mas rapido sin msj de confirmacion (quitarSystem.exit(0))
			//ViewUtils.quit(MainWindow.this);
		    	System.exit(0);
		    }
		    @Override
		    public void windowClosed(WindowEvent e) {}
		    @Override
		    public void windowIconified(WindowEvent e) {}
		    @Override
		    public void windowDeiconified(WindowEvent e) {}
		    @Override
		    public void windowActivated(WindowEvent e) {}
		    @Override
		    public void windowDeactivated(WindowEvent e) {}
		    });
		
	 setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	pack();
	//Borrar esto al final 
	 setBounds(100, 100, 1214, 637);
	 setLocationRelativeTo(null);
	 setVisible(true);
	
	 }
	 }
