package tv.kiekko.eqoa.file;

import java.io.IOException;

import tv.kiekko.eqoa.geom.Point;

/*
 * ZoneBase contains information about a zone.
 * We only get preTranslations which are used to translate
 * PrimBuffer and CollBuffer vertices to world coordinates.
 * 
 */

public class ZoneBase extends Obj {

	ZonePreTranslations preTranslations;

	public ZoneBase(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() throws IOException {
		preTranslations = (ZonePreTranslations) info.getChild(ObjType.ZonePreTranslations).getObj();
	}

	public Point[] getPreTranslations() {
		return preTranslations.values;
	}

}
