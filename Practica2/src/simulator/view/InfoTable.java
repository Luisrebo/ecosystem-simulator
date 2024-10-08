package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import javax.swing.JTable;
import javax.swing.border.Border;

public class InfoTable extends JPanel {
	String _title;
	TableModel _tableModel;

	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();
	}

	private void initGUI() {
		//Border para que la tabla ocupe todo el espacio disponible
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder(_title));
		JTable tabla = new JTable(_tableModel);
     	//tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // establece el ancho fijo de la tabla
		
		//Borde a Infotable
		Border borde = BorderFactory.createLineBorder(Color.BLACK, 3);
		this.setBorder(BorderFactory.createTitledBorder(borde, this._title));
		
		//Panel con scroll
		this.add(new JScrollPane(tabla, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
  
	}
}