package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.List;

/*
 * This sprite contains several versions of the model of varying levels of detail.
 * By default, this implementation returns the most detailed one,
 * or if lowLevel is set, the least detailed one.
 * 
 */

public class LODSprite extends SimpleSprite {
	static boolean lowLevel = false;

	public LODSprite(ObjInfo info) {
		super(info);
	}

	ObjInfo level1;

	@Override
	public void load() {
		seek();
		skipBytes(4); // dict_id
		box = readBox();
		ObjInfo array = info.getNextSibling();
		List<ObjInfo> c = array.getChildren();
		if (lowLevel)
			level1 = c.get(c.size() - 1);
		else
			level1 = c.get(0);
	}

	public SimpleSprite getSprite() throws IOException {
		return (SimpleSprite) level1.getObj();
	}

	public static void setLowlevel(boolean b) {
		lowLevel = b;
	}

}
