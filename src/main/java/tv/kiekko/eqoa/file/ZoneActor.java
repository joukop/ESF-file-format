package tv.kiekko.eqoa.file;

import tv.kiekko.eqoa.geom.Point;

/* 
 * ZoneActor contains a resource ID or a Sprite that will be looked up in the global dictionary,
 * and a placement for it.
 */

public class ZoneActor extends Obj {
	public SpritePlacement placement;

	public ZoneActor(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() {
		seek();
		int actor_id = readInt();
		Point pos = readPoint();
		Point rot = readPoint();
		float a = readFloat();
		byte[] color = readColor();
		placement = new SpritePlacement(actor_id, pos, rot, a, color);
	}

}