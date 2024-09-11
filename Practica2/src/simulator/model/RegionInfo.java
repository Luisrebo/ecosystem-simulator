package simulator.model;

import java.util.List;

public interface RegionInfo extends JSONable {
	//Nuestro objetivo es dar acceso a las regiones desde fuera del modelo, de tal manera que no se pueda
	//alterar sus estados.
	
	public List<AnimalInfo> getAnimalsInfo();
}
