package tv.kiekko.eqoa.file;

import java.io.IOException;

import tv.kiekko.eqoa.geom.Box;
import tv.kiekko.eqoa.geom.Point;

/*
 * Contains bounding boxes of Zones etc.
 */

public class WorldZoneProxies extends Obj {

	public class ZoneProxy {
		public long baseOffset;
		public long zoneOffset;
		public Box box;
		public Point center;
	}

	ZoneProxy[] zones;

	public WorldZoneProxies(ObjInfo info) {
		super(info);
	}

	public ZoneProxy getZoneProxy(long offset) {
		for (int i = 0; i < zones.length; i++) {
			if (zones[i].zoneOffset == offset)
				return zones[i];
		}
		return null;
	}

	@Override
	public void load() throws IOException {
		seek();
		int numZones = readInt();
		zones = new ZoneProxy[numZones];
		for (int i = 0; i < numZones; i++) {
			ZoneProxy z = new ZoneProxy();
			zones[i] = z;
			z.zoneOffset = readLong();
			z.baseOffset = readLong();
			int field_18 = readInt();
			Point field_20_28 = readPoint();
			z.center = field_20_28;
			Box box = readBox();
			z.box = box;
			String str = "";
			int type2 = info.getObjectVersion();
			if (type2 == 1)
				str = readString();
			// z.str=str;
			debug(String.format("zoneOffset=%x baseOffset=%x field_18=%x field_20_28=%s box=%s str=%s", z.zoneOffset,
					z.baseOffset, field_18, field_20_28.toString(), box.toString(), str));

		}

	}

	public ZoneProxy[] getZoneProxies() {
		return zones;
	}

}
