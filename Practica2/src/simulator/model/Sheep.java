package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal.Diet;
import simulator.model.Animal.State;

public class Sheep extends Animal {

	private static final double SPEED_INICIAL = 35.0;
	private static final double CAMPO_VISUAL_INICIAL = 40.0;
	private static final double DEATH_AGE = 8.0;

	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, CAMPO_VISUAL_INICIAL, SPEED_INICIAL, mate_strategy, pos);
		_danger_strategy = danger_strategy;
		_danger_source = null;// esto no se

	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		_danger_source = null;
		_danger_strategy = p1._danger_strategy;
	}

	protected SelectionStrategy get_dangerStrategy() {
		return _danger_strategy;
	}

	@Override
	public void update(double dt) {
		// 1
		if (_state != State.DEAD) {
			// 2
			switch (_state) {

			case NORMAL:
				// 1.1
				if (_pos.distanceTo(_dest) < 8.0)
					_dest = new Vector2D(Utils._rand.nextDouble(_region_mngr.get_region_width()),
							Utils._rand.nextDouble(_region_mngr.get_height()));
				// 1.2
				move(_speed * dt * Math.exp((_energy - 100.0) * 0.007));
				// 1.3
				_age += dt;
				// 1.4
				_energy = suma_resta_entre_limites(_energy, -20 * dt, 100.0, 0.0);
				// 1.5
				_desire = suma_resta_entre_limites(_desire, +40 * dt, 100.0, 0.0);
				// 2.0
				// 2.1
				if (_danger_source == null)
					_danger_source = _danger_strategy.select(this,
							this._region_mngr.get_animals_in_range(this, (a) -> a.get_diet() == Diet.CARNIVORE));
				// 2.2
				if (_danger_source != null) {
					_state = State.DANGER;
					_mate_target = null;
				} else if (_desire > 65.0) {
					_state = State.MATE;
					_danger_source = null;
				}
				break;

			case DANGER:
				// 1 -¿y si el peligro esta fuera de vision?
				if (_danger_source == null || _danger_source._state == State.DEAD)
					_danger_source = null;
				// 2
				if (_danger_source == null) {
					move(_speed * dt * Math.exp((_energy - 100.0) * 0.007));
					_age += dt;
					_energy = suma_resta_entre_limites(_energy, -20 * dt, 100.0, 0.0);
					_desire = suma_resta_entre_limites(_desire, +40 * dt, 100.0, 0.0);
				}
				// 2.1
				else {
					_dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction());
					// 2.2
					move(2.0 * _speed * dt * Math.exp((_energy - 100.0) * 0.007));
					// 2.3
					_age += dt;
					// 2.4
					_energy = suma_resta_entre_limites(_energy, -20 * 1.2 * dt, 100.0, 0.0);
					// 2.5
					_desire = suma_resta_entre_limites(_desire, +40 * dt, 100.0, 0.0);
				}
				// 3
				// 3.1
				if (_danger_source == null || _pos.distanceTo(_danger_source._pos) > this._sight_range) {
					// 3.1.1
					_danger_source = _danger_strategy.select(this,
							this._region_mngr.get_animals_in_range(this, (a) -> a.get_diet() == Diet.CARNIVORE));
					// 3.1.2
					if (_danger_source == null) {
						// 3.1.2.1
						if (_desire < 65.0) {
							_state = State.NORMAL;
							_danger_source = null;
							_mate_target = null;
						} else {
							_state = State.MATE;
							_danger_source = null;
							_mate_target = null;
						}
					}
				}
				break;
			case MATE:
				// 1
				if (this._mate_target == null || this._mate_target._state == State.DEAD
						|| _pos.distanceTo(this._mate_target._pos) > this._sight_range)
					_mate_target = null;
				// 2
				if (this._mate_target == null)
					_mate_target = this._mate_strategy.select(this, _region_mngr.get_animals_in_range(this,
							(a) -> a.get_genetic_code() == this.get_genetic_code()));

				// avanzamos normal
				if (_mate_target == null) {
					move(_speed * dt * Math.exp((_energy - 100.0) * 0.007));
					_age += dt;
					_energy = suma_resta_entre_limites(_energy, -20 * dt, 100.0, 0.0);
					_desire = suma_resta_entre_limites(_desire, +40 * dt, 100.0, 0.0);
				} else {
					// 2.1
					_dest = _mate_target.get_position();
					// 2.2
					move(2.0 * _speed * dt * Math.exp((_energy - 100.0) * 0.007));
					// 2.3
					_age += dt;
					// 2.4
					_energy = suma_resta_entre_limites(_energy, -20 * 1.2 * dt, 100.0, 0.0);
					// 2.5
					_desire = suma_resta_entre_limites(_desire, +40 * dt, 100.0, 0.0);
					// 2.6
					if (_pos.distanceTo(_mate_target._pos) < 0.8) {
						// 2.6.1
						_desire = 0.0;
						_mate_target._desire = 0.0;
						// 2.6.2
						if (this._baby == null && Utils._rand.nextDouble() < 0.9)
							_baby = new Sheep(this, this._mate_target);
						// 2.6.3
						_mate_target = null;
					}
				}
				// 3
				if (_danger_source == null)
					_danger_source = _danger_strategy.select(this,
							_region_mngr.get_animals_in_range(this, (a) -> a.get_diet() == Diet.CARNIVORE));
				// 4
				if (this._danger_source != null) {
					_state = State.DANGER;
					_mate_target = null;
				} else if (_desire < 65.0) {
					_state = State.NORMAL;
					_danger_source = null;
					_mate_target = null;
				}
				break;
			}
			// 3
			if (this.animal_fuera_del_mapa()) {
				this.ajustar_posicion();
				_state = State.NORMAL;
				_danger_source = null;
				_mate_target = null;
			}
			// 4
			if (this._energy <= 0.0 || _age > 8.0)
				_state = State.DEAD;
			else
				_energy = this.suma_resta_entre_limites(_energy, this._region_mngr.get_food(this, dt), 100.0, 0.0);
		}
	}
}
