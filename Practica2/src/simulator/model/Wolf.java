package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {

	private static final double SPEED_INICIAL = 60.0;
	private static final double CAMPO_VISUAL_INICIAL = 50.0;
	// edad a la que muere la oveja
	private static final double DEATH_AGE = 14.0;

	protected SelectionStrategy _hunting_strategy;
	protected Animal _hunt_target;

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("Wolf", Diet.CARNIVORE, CAMPO_VISUAL_INICIAL, SPEED_INICIAL, mate_strategy, pos);
		this._hunting_strategy = hunting_strategy;
		_hunt_target = null;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		_hunting_strategy = p1._hunting_strategy;
		_hunt_target = null;
	}

	@Override
	public void update(double dt) {
		// 1 si el animal no esta muerto hacemos update
		if (_state != State.DEAD) {
			// 2 Actualizar segun el estado del animal
			switch (_state) {
			case NORMAL:
				// 1Avanzar el animal segun lo pasos
				// 1.1
				if (_pos.distanceTo(_dest) < 8.0)
					_dest = new Vector2D(Utils._rand.nextDouble(_region_mngr.get_region_width()),
							Utils._rand.nextDouble(_region_mngr.get_height()));
				// 1.2
				move(_speed * dt * Math.exp((_energy - 100.0) * 0.007));
				// 1.3
				_age += dt;
				// 1.4
				_energy = suma_resta_entre_limites(_energy, -18 * dt, 100.0, 0.0);
				// 1.5
				_desire = suma_resta_entre_limites(_desire, +30 * dt, 100.0, 0.0);
				// 2 Cambio de estado
				if (_energy < 50.0) {
					_state = State.HUNGER;
					_mate_target = null;
				} else if (_desire > 65.0) {
					_state = State.MATE;
					_hunt_target = null;
				}

				break;

			case HUNGER:

				// 1.0
				if (_hunt_target == null || _hunt_target._state == State.DEAD
						|| _pos.distanceTo(_dest) > this._sight_range)
					this._hunt_target = this._hunting_strategy.select(this,
							this._region_mngr.get_animals_in_range(this, (a) -> a.get_diet() == Diet.HERBIVORE));
				// 2.0
				if (_hunt_target == null) {
					// Avanza normal como arriba
					move(_speed * dt * Math.exp((_energy - 100.0) * 0.007));
					_age += dt;
					_energy = suma_resta_entre_limites(_energy, -18 * dt, 100.0, 0.0);
					_desire = suma_resta_entre_limites(_desire, +30 * dt, 100.0, 0.0);
				} else {
					// 2.1
					_dest = _hunt_target.get_position();
					// 2.2
					move(3.0 * _speed * dt * Math.exp((_energy - 100.0) * 0.007));
					// 2.3
					_age += dt;
					// 2.4
					_energy = suma_resta_entre_limites(_energy, -18 * 1.2 * dt, 100.0, 0.0);
					// 2.5
					_desire = suma_resta_entre_limites(_desire, +30 * dt, 100.0, 0.0);
					// 2.6 Si estamos cerca cazamos
					if (this._pos.distanceTo(_dest) < 0.8) {
						// 2.6.1
						_hunt_target._state = State.DEAD;
						// 2.6.2
						_hunt_target = null;
						// 2.6.3
						_energy = suma_resta_entre_limites(_energy, +50, 100.0, 0.0);
					}
				}
				// 3Cambio de estado
				// 3.1
				if (this._energy > 50.0)
					// 3.1.1
					if (_desire < 65.0) {
						_state = State.NORMAL;
						_hunt_target = null;
						_mate_target = null;
					}
					// 3.1.2
					else {
						_state = State.MATE;
						_hunt_target = null;
					}

				break;

			case MATE:
				// 1
				if (this._mate_target == null || this._mate_target._state == State.DEAD
						|| this._pos.distanceTo(_mate_target.get_position()) > this._sight_range)
					_mate_target = null;
				// 2
				if (_mate_target == null)
					_mate_target = _mate_strategy.select(this,
							_region_mngr.get_animals_in_range(this, (a) -> a.get_genetic_code() == this._genetic_code));

				if (_mate_target == null) {
					// Avanza normal como arriba
					move(_speed * dt * Math.exp((_energy - 100.0) * 0.007));
					_age += dt;
					_energy = suma_resta_entre_limites(_energy, -18 * dt, 100.0, 0.0);
					_desire = suma_resta_entre_limites(_desire, +30 * dt, 100.0, 0.0);
				}

				else {
					// 2.1
					_dest = _mate_target.get_position();
					// 2.2
					move(3.0 * _speed * dt * Math.exp((_energy - 100.0) * 0.007));
					// 2.3
					_age += dt;
					// 2.4
					_energy = this.suma_resta_entre_limites(_energy, -18 * 1.2 * dt, 100.0, 0.0);
					// 2.5
					_desire = this.suma_resta_entre_limites(_desire, +30 * dt, 100.0, 0.0);
					// 2.6
					if (this._pos.distanceTo(this._mate_target._pos) < 0.8) {
						// 2.6.1
						_desire = 0.0;
						_mate_target._desire = 0.0;
						// 2.6.2
						if (this._baby == null && Utils._rand.nextDouble() < 0.9)
							_baby = new Wolf(this, this._mate_target);
						// 2.6.3
						_energy = this.suma_resta_entre_limites(_energy, 10.0, 100.0, 0.0);
						// 2.6.4
						_mate_target = null;
					}
				}
				// 3.0
				if (_energy < 50.0) {
					_state = State.HUNGER;
					_mate_target = null;
				} else if (_desire < 65.0) {
					_state = State.NORMAL;
					_hunt_target = null;
					_mate_target = null;
				}
			}
			// 3
			if (animal_fuera_del_mapa()) {
				this.ajustar_posicion();
				_state = State.NORMAL;
				_hunt_target = null;
				_mate_target = null;
			}
			// 4
			if (_energy <= 0.0 || _age > 14.0)
				_state = State.DEAD;
			else
				_energy = this.suma_resta_entre_limites(_energy, this._region_mngr.get_food(this, dt), 100.0, 0.0);
		}
	}
}
