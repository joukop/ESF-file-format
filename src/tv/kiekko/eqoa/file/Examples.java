package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.List;

import tv.kiekko.eqoa.file.WorldZoneProxies.ZoneProxy;
import tv.kiekko.eqoa.geom.Point;

public class Examples {

	// isle of dread? =6
	// permafrost = 9
	// neriak = 45
	// jared's blight = 78
	// highpass hold = 80
	// freeport = 84 ?
	// whale hill = 74 ?

	static Point darvarManor = new Point(13555.957031f, 56.376099f, 14759.808594f);
	static Point neriak = new Point(25239.556641f, 0.500977f, 8891.435547f);

	static void loadZoneProxies(ObjFile file) throws IOException {
		WorldZoneProxies proxies = (WorldZoneProxies) file.getRoot().getChild(ObjType.WorldBase)
				.getChild(ObjType.WorldZoneProxies).getObj();
		for (int i = 0; i < proxies.zones.length; i++) {
			ZoneProxy z = proxies.zones[i];
			System.out.println("zone " + i + " at " + z.box);
			if (z.box.containsXZ(darvarManor))
				System.out.println("darvar manor is in zone #" + i);
			if (z.box.containsXZ(neriak))
				System.out.println("neriak is in zone #" + i);
		}

	}

	static void exportZones(ObjFile file) throws Exception {
		ObjInfo root = file.getRoot();
		List<ObjInfo> zones = root.getChild(ObjType.World).getChildren(ObjType.Zone);
		ObjExport[] e = new ObjExport[84 - 74 + 1];
		for (int i = 74; i <= 84; i++) {
			int j = i - 74;
			e[j] = new ObjExport();
			e[j].setSizeCutoff(20);
			LODSprite.setLowlevel(true);
			Zone z = (Zone) zones.get(i).getObj();
			e[j].addAll(z.getSpritePlacements(), file);
		}
		ObjExport.centerArray(e);
		for (int i = 74; i <= 84; i++) {
			int j = i - 74;
			e[j].write("test-" + j + ".obj");
		}
	}

	static void lavaTest(ObjFile file) throws IOException {
		file.seek(8438314);
		Zone z = (Zone) file.getRoot().getObj();
		ObjExport e = new ObjExport();
		e.addAll(z.getSpritePlacements(), file);
		e.center();
		e.write("test.obj");
	}

	static void exportZone(ObjFile file) throws Exception {
		ObjExport e = new ObjExport();
		ObjInfo root = file.getRoot();
		List<ObjInfo> zones = root.getChild(ObjType.World).getChildren(ObjType.Zone);
		Zone z = (Zone) zones.get(78).getObj();
		e.addAll(z.getSpritePlacements(), file);
		e.center();
		e.write("test.obj");
	}

	static void hspriteTest(ObjFile file) throws Exception {
		file.seek(237109178 - 12);
		file.parse();
		HSprite h = (HSprite) file.getRoot().getObj();
		ObjExport e = new ObjExport();
		for (SpritePlacement sp : h.getSprites()) {
			e.add(sp, file);
		}
		e.center();
		e.write("test.obj");
	}

	public static void main(String[] a) throws Exception {
		String tunariaPath = "Tunaria.esf";
		if (a.length > 0)
			tunariaPath = a[0];
		ObjFile file = new ObjFile(tunariaPath);
		// ObjFile.setLogFile("parse.log");
		exportZone(file);
		// loadZoneProxies(file);
		// exportZones(file);
		// hspriteTest(file);
	}

}
