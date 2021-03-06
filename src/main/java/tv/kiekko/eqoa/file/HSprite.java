package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.kiekko.eqoa.geom.Point;

/* A complex sprite type. Basic cases work, but many things unimplemented. */

public class HSprite extends GroupSprite {
	List<ObjInfo> sprites;
	HSpriteHierarchy hierarchy;

	static class HSpriteHierarchy extends Obj {
		float[] floats1;

		public HSpriteHierarchy(ObjInfo info) {
			super(info);
		}

		@Override
		public void load() {
			seek();
			int ver = info.getObjectVersion();
			debug("ver=" + ver);
			if (ver != 0) {
				int num = readInt();
				debug("has " + num + " floats");
				floats1 = new float[num];
				for (int i = 0; i < num; i++) {
					floats1[i] = readFloat();
					debug("float[" + i + "]=" + floats1[i]);
				}
			}
			int numNodes = readInt();
			debug("numNodes=" + numNodes);
			// TODO: Neriak skull flags are wrong, probably because the following is
			// unimplemented
			float[] bindPose = new float[8];
			for (int i = 0; i < numNodes; i++) {
				int node = readInt(); // -1 = root
				debug("node=" + node);
				for (int j = 0; j < 8; j++) {
					bindPose[j] = readFloat();
					// debug("bindPose["+j+"]="+bindPose[j]);
				}
				// setnodebindpose
				if (ver != 0) {
					int k = readInt();
					debug("k=" + k);
				}
				Point vec;
				if (ver < 2) {
					vec = new Point(1, 1, 1);
				} else {
					vec = readPoint();
					debug("fixscale=" + vec);
				}
				// setnodefixscale
			}
			debug("end available=" + info.getAvailable());
		}

	}

	public HSprite(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() throws IOException {
		seek();
		skipBytes(4); // id
		box = readBox();
		debug("box=" + box);
		debug("hsprite children=" + info.getChildren());
		ObjInfo array = info.getChild(ObjType.HSpriteArray);
		sprites = array.getChildren();
		debug("sprites=" + sprites);
		hierarchy = (HSpriteHierarchy) info.getChild(ObjType.HSpriteHierarchy).getObj();
	}

	@Override
	public List<SpritePlacement> getSprites() throws IOException {
		if (placements == null) {
			placements = new ArrayList<SpritePlacement>();
			for (ObjInfo i : sprites) {
				SimpleSubSprite s = (SimpleSubSprite) i.getObj();
				s.usePretrans = false;
				placements.add(new SpritePlacement(s));
			}
		}
		return placements;
	}

}
