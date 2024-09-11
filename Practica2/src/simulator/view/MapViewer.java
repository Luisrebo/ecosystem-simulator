package simulator.view;

import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura/ de la simulación -- se supone que siempre van a ser iguales
	// al tamaño del componente
	private int _width;
	private int _height;

	// Número de filas/columnas de la simulación
	private int _rows;
	private int _cols;

	// Anchura/altura de una región
	int _rwidth;
	int _rheight;

	// Mostramos sólo animales con este estado. Los posibles valores de _currState
	// son null, y los valores deAnimal.State.values(). Si es null mostramos todo.
	Animal.State _currState;
	//private int _idxState = Animal.State.values().length;
	private int _idxState = 0;

	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.
	volatile private Collection<AnimalInfo> _objs;
	volatile private Double _time;

	// Una clase auxilar para almacenar información sobre una especie
	private static class SpeciesInfo {
		private Integer _count;
		private Color _color;

		SpeciesInfo(Color color) {
			_count = 0;
			_color = color;
		}
	}

	// Un mapa para la información sobre las especies
	Map<String, SpeciesInfo> _kindsInfo = new HashMap<>();

	// El font que usamos para dibujar texto
	private Font _font = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no
	private boolean _showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'h':
					_showHelp = !_showHelp;
					repaint();
					break;
				case 's':
					// TODO Cambiar _currState al siguiente (de manera circular). Después de null
					// viene el primero de Animal.State.values() y después del último viene null.

					if (_idxState == Animal.State.values().length &&(_currState!=null)) {
						_currState = null;
						_idxState = -1;}
					else 
						_currState = Animal.State.values()[_idxState];

					_idxState += 1;

					repaint();

				default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus(); // Esto es necesario para capturar las teclas cuando el ratón está sobre este
								// componente.
			}
		});

		// Por defecto mostramos todos los animales
		_currState = null;

		// Por defecto mostramos el texto de ayuda
		_showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Cambiar el font para dibujar texto
		g.setFont(_font);

		// Dibujar fondo blanco
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, _width, _height);

		// Dibujar los animales, el tiempo, etc.
		if (_objs != null)
			drawObjects(gr, _objs, _time);

		// TODO Mostrar el texto de ayuda si _showHelp es true. El texto a mostrar es el
		// siguiente (en 2 líneas):
		//
		// h: toggle help
		// s: show animals of a specific state
		if (_showHelp) {
			g.setColor(Color.red);
			g.drawString("h: togglehelp", 5, 15);
			g.drawString("s:show animals of an specific state", 5, 30);
		}

	}

	private boolean visible(AnimalInfo a) {
		// TODO Devolver true si el animal es visible, es decir si _currState es null o
		// su estado es igual a _currState.
		return _currState==null||Objects.equals(a.get_state(), _currState);
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {
		// TODO Dibujar el grid de regiones
		g.setColor(Color.DARK_GRAY);
		for (int i = 0; i < _rows; i++) {
			for (int j = 0; j < _cols; j++) {
				int x = j * _rwidth; // Calcular la coordenada x de la esquina superior izquierda de la celda
				int y = i * _rheight; // Calcular la coordenada y de la esquina superior izquierda de la celda
				g.drawRect(x, y, _rwidth, _rheight); // Dibujar el borde de la celda

			}
		}

		// Dibujar los animales
		for (AnimalInfo a : animals) {
		

			// Si no es visible saltamos la iteración
			/*
			 * if (!visible(a)) continue;
			 */

			if (!visible(a)) continue;
			
			// La información sobre la especie de 'a'
			SpeciesInfo esp_info = _kindsInfo.get(a.get_genetic_code());

			// Si esp_info es null, añade una entrada correspondiente al mapa. Para el
			// color usa ViewUtils.get_color(a.get_genetic_code())
			// SI NO EXISTIA
			if (esp_info == null) {

				Color color = ViewUtils.get_color(a.get_genetic_code());
				// Color color=ViewUtils.get_color(Color.BLACK);
				esp_info = new SpeciesInfo(color);
				_kindsInfo.put(a.get_genetic_code(), esp_info);
			}
			// contador antes o despues?
			esp_info._count += 1;

			// TODO Incrementar el contador de la especie (es decir el contador dentro de
			// tag_info)

			// TODO Dibijar el animal en la posicion correspondiente, usando el color
			// tag_info._color. Su tamaño tiene que ser relativo a su edad, por ejemplo
			// edad/2+2. Se puede dibujar usando fillRoundRect, fillRect o fillOval.
			g.setColor(esp_info._color);
			int tamanio = (int) (a.get_age() / 2 + 2);
			int cellX = (int) (a.get_position().getX());
			int cellY = (int) (a.get_position().getY());

			g.fillOval(cellX, cellY, tamanio, tamanio);

		}

		// TODO Dibujar la etiqueta del estado visible, sin no es null.

		// TODO Dibujar la etiqueta del tiempo. Para escribir solo 3 decimales puede
		// usar String.format("%.3f", time)

		// TODO Dibujar la información de todas la especies. Al final de cada iteración
		// poner el contador de la especie correspondiente a 0 (para resetear el cuento)
		int i = -23;
		for (Entry<String, SpeciesInfo> e : _kindsInfo.entrySet()) {
			g.setColor(e.getValue()._color);
			drawStringWithRect(g, 5, 550 + i, e.getKey() + ": " + e.getValue()._count);
			e.getValue()._count = 0;
			i += 23;
		}
		g.setColor(Color.BLACK);
		// drawStringWithRect(g,5,550+i,"Time :"+ time);
		drawStringWithRect(g, 5, 550 + i, "Time :" + String.format("%.3f", time));
		
		if(_currState!=null) {
		g.setColor(Color.BLUE);
		drawStringWithRect(g, 5, 550 + i+23, "STATE : " + _currState);
		}
	}

	// Un método que dibujar un texto con un rectángulo
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		// TODO Almacenar objs y time en los atributos correspondientes y llamar a
		// repaint() para redibujar el componente.
		_objs = objs;
		_time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Actualizar los atributos _width, _height, _cols, _rows, etc.
		_cols = map.get_cols();
		_rows = map.get_rows();
		_height = map.get_height();
		_width = map.get_width();
		_rwidth = _width / _cols;
		_rheight = _height / _rows;

		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset
		setPreferredSize(new Dimension(map.get_width(), map.get_height()));

		// Dibuja el estado
		update(animals, time);
	}

}
