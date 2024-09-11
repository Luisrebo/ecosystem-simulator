package simulator.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders;
	private List<JSONObject> _builders_info;

	public BuilderBasedFactory() {
		// Create a HashMap for _builders, and a LinkedList _builders_info
		_builders = new HashMap<>();
		_builders_info = new ArrayList<>();// linked list?

	}

	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
		// call add_builder(b) for each builder b in builder
		for (Builder<T> b : builders)
			add_builder(b);
	}

	public void add_builder(Builder<T> b) {
		// add an entry â€œb.getTag() |âˆ’> bâ€� to _builders.
		_builders.put(b.get_type_tag(), b);
		// add b.get_info() to _buildersInfo
		_builders_info.add(b.get_info());
	}

	@Override
	public T create_instance(JSONObject info) {
		if (info == null)
			throw new IllegalArgumentException("â€™infoâ€™ cannot be null");

		// si el String no existe en el json devolvera null
		Builder<T> b = _builders.get(info.getString("type"));
		if (b != null) {
			// Si el json viene con campo data lo usamos para createinstance sino creamos
			// uno vacio (data opcional)
			JSONObject data = info.has("data") ? info.getJSONObject("data") : new JSONObject();
			return b.create_instance(data);
		}

		throw new IllegalArgumentException("Unrecognized â€˜infoâ€™:" + info.toString());

	}

	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	}
}