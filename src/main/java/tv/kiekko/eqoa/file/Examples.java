package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.List;

import tv.kiekko.eqoa.file.WorldZoneProxies.ZoneProxy;
import tv.kiekko.eqoa.geom.Box;
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
	

	/*
	Loading NPC models from Chars.ESF (change tunariaPath or argument).
	This code was used to produce this video: https://www.youtube.com/watch?v=hZ9TE_lVc_g
	Exporting a model is quite simple, most of this code is to place them in neat rows.

	static void mobTest(ObjFile file) throws Exception {
		file.parse();
		ObjExport e=new ObjExport();
		float offx=0;
		float offz=0;
		Box columnBox=new Box();
		for (ObjInfo c : file.getRoot().getChild(ObjType.ResourceDir2).getChildren()) {
			Box spriteBox=null;
			Obj o=c.getObj();
			if (o instanceof CSprite) {
				CSprite cs=(CSprite)o;
				for (SpritePlacement sp : cs.getSprites()) {
					sp.pos.x=offx;
					sp.pos.z=offz;
					e.add(sp, file);
					spriteBox=sp.getSprite(file).getPrimBuffer().box;
				}
			} else if (o instanceof SimpleSprite) {
				SimpleSprite ss=(SimpleSprite)o;
				SpritePlacement sp=new SpritePlacement(ss);
				sp.pos.x=offx;
				sp.pos.z=offz;
				e.add(sp, file);
				spriteBox=sp.getSprite(file).getPrimBuffer().box;
			}
			if (spriteBox!=null && !spriteBox.isEmpty()) {
				columnBox.add(spriteBox);
				offx+=spriteBox.getDimensions().x*1.5f;
				if (offx > 30) {
					offx=0;
					offz+=columnBox.getDimensions().z*1.5f;
					columnBox.clear();
				}
			}
		}
		e.center();
		e.write("test.obj");
	}
		*/

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
