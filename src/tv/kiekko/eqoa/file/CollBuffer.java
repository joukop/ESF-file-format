package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.kiekko.eqoa.file.PrimBuffer.Vertex;
import tv.kiekko.eqoa.geom.Box;
import tv.kiekko.eqoa.geom.Point;

/* CollBuffer is a simplified 3D model without textures, a Sprite sub-object */

public class CollBuffer extends Obj {

	ArrayList<VertexList> lists;
	VertexList currentList;

	public class VertexList {
		public List<Vertex> vertices;
		public Box box;

		VertexList() {
			vertices = new ArrayList<Vertex>();
		}

		public int getNumTriangles() {
			return vertices.size() - 2;
		}

		public Vertex[] getTriangle(int i) {
			boolean odd = (i & 1) == 1;
			if (!odd)
				return new Vertex[] { vertices.get(i), vertices.get(i + 1), vertices.get(i + 2) };
			else
				return new Vertex[] { vertices.get(i), vertices.get(i + 2), vertices.get(i + 1) };
		}

	}

	public class Vertex {
		float x, y, z;
		int vertexGroup;
		int floraType;

		Vertex(Point p, int vg, int ft) {
			this.x = p.x;
			this.y = p.y;
			this.z = p.z;
			this.vertexGroup = vg;
			this.floraType = ft;
		}

		public Point getXYZ() {
			return new Point(x, y, z);
		}

	}

	public void calculateBoxes() {
		for (VertexList vl : lists) {
			vl.box = new Box();
			for (Vertex v : vl.vertices) {
				vl.box.add(v.getXYZ());
			}
		}
	}

	public CollBuffer(ObjInfo info) {
		super(info);
		lists = new ArrayList<VertexList>();
	}

	public List<VertexList> getVertexLists() {
		return lists;
	}

	void begin(int i) {
		lists.add(currentList = new VertexList());
	}

	public void addVertex(Point p, int vg, int floraType) {
		currentList.vertices.add(new Vertex(p, vg, floraType));
	}

	public void end() {
	}

	public void cancel() {
		currentList.vertices.remove(currentList.vertices.size() - 1);
	}

	@Override
	public void load() throws IOException {
		seek();
		int ver = info.getObjectVersion();
		int cbtype = 0;
		debug("objversion=" + ver);
		if (ver > 1) {
			cbtype = readInt();
		}
		int numPrimgroups_maybe = readInt();
		int numVertices = readInt();
		int numSomething = readInt();
		int packing;
		if (ver < 2) {
			packing = 0;
		} else {
			packing = readInt();
		}
		Point[] preTranslations = null;
		Zone zone = null;
		// "packing" tells the conversion factor between the 16-bit integer coordinates
		// and floating point world coordinates
		float p = (float) (1f / Math.pow(2, packing));
		debug(String.format("cbtype=%d primgroups=%d max=%d something=%d packing=%d -> %f", cbtype, numPrimgroups_maybe,
				numVertices, numSomething, packing, p));
		if (info.getParent().type == ObjType.SimpleSubSprite)
			zone = (Zone) info.getParent(ObjType.Zone).getObj();
		if (zone != null)
			preTranslations = zone.getZoneBase().getPreTranslations();
		for (int i = 0; i < numVertices; i++) {
			int num = readInt();
			int primg = readInt();
			int list = readInt();
			if (cbtype == 3) {
				begin(list);
				for (int j = 0; j < num; j++) {
					int x = readShort();
					int y = readShort();
					int z = readShort();
					int vgroup = readByte();
					int flora = readByte();
					Point v = new Point(x * p, y * p, z * p);
					if (vgroup >= preTranslations.length) {
						debug("weird vg=" + vgroup);
						vgroup = 0;
					}
					v.addTo(preTranslations[vgroup]);
					addVertex(v, vgroup, flora);
				}
				end();
			} else {
				debug("cbtype " + cbtype + " not implemented");
			}
		}
	}

}