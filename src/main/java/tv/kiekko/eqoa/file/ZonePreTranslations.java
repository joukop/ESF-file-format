package tv.kiekko.eqoa.file;

import tv.kiekko.eqoa.geom.Point;

/*
 * PreTranslations are just a list of vectors that
 * tell the x, y, z offsets of a vertex group
 * in PrimBuffers and CollBuffers.
 * 
 */

public class ZonePreTranslations extends Obj {
	Point[] values;

	public ZonePreTranslations(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() {
		seek();
		int num = readInt();
		values = new Point[num];
		for (int i = 0; i < num; i++) {
			values[i] = readPoint();
		}
	}

}
