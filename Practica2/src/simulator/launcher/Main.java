package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controler;
import simulator.factories.Factory;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SelectYoungestBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.view.MainWindow;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.DefaultRegionBuilder;
import simulator.factories.DynamicSupplyRegionBuilder;
import simulator.view.Car;
import simulator.view.HaciaElNorte;
//-i resources/examples/ex1.json -o resources/tmp/myout.jso -t 60.0 -dt 0.03 -sv

public class Main {

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	private final static Double _default_time = 10.0; // in seconds
	private final static Double _default_dt = 0.03;

	// some attributes to stores values corresponding to command-line parameters
	private static Double _time = null;
	private static String _in_file = null;
	private static ExecMode _mode = ExecMode.GUI;
	private static String _outFile = null;
	public static Double _dt = null;
	private static boolean _sv; // false default
	private static boolean _car;
	private static boolean _no;
	

	// factorias
	public static Factory<Animal> _animal_factory;
	// private static Factory<SelectionStrategy> _strategyFactory; No necesria para
	// la simulacion solo para crear animales
	public static Factory<Region> _region_factory;

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_mode(line);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_dt_option(line);
			parse_outFile_option(line);
			parse_sv_option(line);
			parse_car_option(line);
			parse_no_option(line);
			

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();
		
	     // m
			cmdLineOptions.addOption(
					Option.builder("m").longOpt("mode").hasArgs().desc("Execution Mode. Possible values: 'batch' (Batch\n"
							+ "mode), 'gui' (Graphical User Interface mode).\n"
							+ "Default value: 'gui'.").build());

		// help (ejemplo sin arumentos de entrada)
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file (ejemplo con argumento de entrada)
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());

		// outfile
		cmdLineOptions.addOption(
				Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());

		// dt
		cmdLineOptions.addOption(
				Option.builder("dt").longOpt("delta-time").hasArg().desc("A double representing actual time, in\n"
						+ "seconds, per simulation step. Default value: " + _default_dt + ".").build());
		// sv
		cmdLineOptions.addOption(
				Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());
		
		// car
				cmdLineOptions.addOption(
						Option.builder("car").longOpt("car-option").desc("Show the the number of carnivores en batch mode.").build());
				
				cmdLineOptions.addOption(
						Option.builder("no").desc("Show the number of animals traveled to north per iteration.").build());
				
				
		

		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}
	
	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}
	private static void parse_mode(CommandLine line) throws ParseException {
		String mode=line.getOptionValue("m");
		
		if(mode!=null && mode.equalsIgnoreCase(ExecMode.BATCH.toString()))
			_mode=ExecMode.BATCH;
			
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}

	private static void parse_dt_option(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _default_dt.toString()); // hay valor default para dt?
		try {
			_dt = Double.parseDouble(dt);
			assert (_dt >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for dt: " + dt);
		}
	}

	private static void parse_sv_option(CommandLine line) {
		_sv = line.hasOption("sv");
		/*
		 * if(line.hasOption("sv")) _sv=true;
		 */
	}
	
	private static void parse_no_option(CommandLine line) {
		if(_mode==ExecMode.BATCH)
		_no = line.hasOption("no");
		
	}
	
	private static void parse_car_option(CommandLine line) {
		if(_mode==ExecMode.BATCH)
			_car=line.hasOption("car");
	}

	private static void parse_outFile_option(CommandLine line) throws ParseException {
		if(_mode==ExecMode.BATCH) {
		if (line.hasOption("o"))
			_outFile = line.getOptionValue("o");
		else
			throw new ParseException("Invalid value for outfile: ");
	}
		}

	private static void init_factories() {
		// Factoria de estrategias
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
		selection_strategy_builders.add(new SelectYoungestBuilder());
		Factory<SelectionStrategy> selection_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(
				selection_strategy_builders);

		// Factoria de animales (recibe la de estrategias en constructor)
		List<Builder<Animal>> animal_builders = new ArrayList<>();
		animal_builders.add(new SheepBuilder(selection_strategy_factory));
		animal_builders.add(new WolfBuilder(selection_strategy_factory));
		/* Factory<Animal> */ _animal_factory = new BuilderBasedFactory<Animal>(animal_builders);

		// Factoria de regiones
		List<Builder<Region>> region_builders = new ArrayList<>();
		region_builders.add(new DynamicSupplyRegionBuilder());
		region_builders.add(new DefaultRegionBuilder());
		/* Factory<Region> */ _region_factory = new BuilderBasedFactory<Region>(region_builders);
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	private static void start_batch_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		JSONObject data = load_JSON_file(is);

		OutputStream out = new FileOutputStream(new File(_outFile));

		int width, height, rows, cols;
		width = data.getInt("width");
		height = data.getInt("height");
		rows = data.getInt("rows");
		cols = data.getInt("cols");

		// Cuidado en el json los parametros vienen en otro orden que en el constructor
		// de simulator
		// public Simulator(int cols, int rows, int width, int height,Factory<Animal>...
		Simulator sim = new Simulator(cols, rows, width, height, _animal_factory, _region_factory);
		Controler ctrl = new Controler(sim);
		
        Car car=new Car(ctrl);
		HaciaElNorte hn=new HaciaElNorte(ctrl);
		
		ctrl.load_data(data);
		ctrl.run(_time, _dt, _sv, out);
		//SwingUtilities.invokeAndWait(() -> new MainWindow(ctrl));
		out.close();
	
		//	if(_car) car.Imprimir();
		//if(_no) hn.Imprimir();
	}

	private static void start_GUI_mode() throws Exception {
		Simulator sim;
		Controler ctrl;
		InputStream is =null;
		JSONObject data=null;
		
		if(_in_file!=null) {
		 is = new FileInputStream(new File(_in_file));
		 data = load_JSON_file(is);

		int width, height, rows, cols;
		width = data.getInt("width");
		height = data.getInt("height");
		rows = data.getInt("rows");
		cols = data.getInt("cols");

		// Cuidado en el json los parametros vienen en otro orden que en el constructor
		// de simulator
		// public Simulator(int cols, int rows, int width, int height,Factory<Animal>...
		 sim = new Simulator(cols, rows, width, height, _animal_factory, _region_factory);
		 ctrl = new Controler(sim);
		ctrl.load_data(data);}
		else {
			 sim= new Simulator(20, 15, 800, 600,_animal_factory, _region_factory);
			 ctrl=new Controler(sim);
		}
		SwingUtilities.invokeAndWait(() -> new MainWindow(ctrl));
		if(is!=null)
		ctrl.load_data(data);
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l); // long
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
