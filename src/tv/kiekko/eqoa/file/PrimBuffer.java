package tv.kiekko.eqoa.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import tv.kiekko.eqoa.geom.Box;
import tv.kiekko.eqoa.geom.Point;

/*
 * PrimBuffer contains the vertices, texture U and V, faces (triangle strips), their normals,
 * and information about texturing.
 * 
 * There's also code here for exporting this data into .OBJ and .MTL files (should probably
 * be refactored to another class).
 *
 * Vertex x, y, z, u, and v are stored as 16-bit integers. They are converted to world
 * coordinates by multiplying with a "packing factor" and usually adding certain values
 * from the zone's preTranslation table, based on vertex group, a 16-bit integer stored
 * after the coordinate data. Normals are stored as three bytes.
 * 
 */

public class PrimBuffer extends Obj {

	@Override
	protected void debug(String s) {
	}

	public PrimBuffer(ObjInfo info) throws IOException {
		super(info);
		vertexLists = new ArrayList<VertexList>();
		matpal = new MaterialPalette(null);
	}

	int nMaterials;
	int nFaces;
	float packing1, packing2, packing3;
	int pbtype;
	int material = -1;
	int layer = -1;

	Box box = new Box();

	public int getFaceCountOfMaterial(int materialGroup) {
		int count = 0;
		for (VertexList l : vertexLists) {
			if (materialGroup < 0 || l.material == materialGroup)
				count += l.vertices.size() - 2;
		}
		return count;
	}

	public int getNumberOfVertices() {
		int count = 0;
		for (VertexList l : vertexLists) {
			count += l.vertices.size();
		}
		return count;
	}

	public int getVertexCountOfMaterial(int materialGroup) {
		int count = 0;
		for (VertexList l : vertexLists) {
			if (materialGroup < 0 || l.material == materialGroup)
				count += l.vertices.size();
		}
		return count;
	}

	ArrayList<VertexList> vertexLists;
	MaterialPalette matpal;
	VertexList currentList;

	public class VertexList {
		public List<Vertex> vertices;
		public int material;
		public int layer;
		public int layers;

		VertexList() {
			vertices = new ArrayList<Vertex>();
		}

	}

	public class Vertex {
		public float x, y, z;
		public float u, v;
		public float nx, ny, nz;
		public float r, g, b;
		public short vgroup;
		public String comment;

		// V0
		public Vertex(Point ve, float u2, float v2, Point n, byte[] color) {
			x = ve.x;
			y = ve.y;
			z = ve.z;
			u = u2;
			v = v2;
			nx = n.x;
			ny = n.y;
			nz = n.z;
			this.r = (((int) color[0]) & 255) / 255f;
			this.g = (((int) color[1]) & 255) / 255f;
			this.b = (((int) color[2]) & 255) / 255f;
			box.add(x, y, z);
		}

		public Vertex(Point p, float[] uv, Point normal, float[] color, int vg) {
			x = p.x;
			y = p.y;
			z = p.z;
			u = uv[0];
			v = uv[1];
			r = color[0];
			g = color[1];
			b = color[2];
			vgroup = (short) vg;
		}

		public Vertex(Point p) {
			x = p.x;
			y = p.y;
			z = p.z;
		}

		public float[] getColor() {
			return new float[] { r, g, b };
		}

		public Point getXYZ() {
			return new Point(x, y, z);
		}

		public float[] getUV() {
			return new float[] { u, v };
		}

		public Point getNormal() {
			return new Point(nx, ny, nz);
		}

	}

	// "Packing" tells how the 16-bit coordinates are converted to world
	// coordinates.
	// The first is used for vertex x, y, z
	// The second is used for u, v
	// The third unknown at the moment.

	void setPacking(int p1, int p2, int p3) {
		packing1 = 1f / (float) Math.pow(2, p1);
		packing2 = 1f / (float) Math.pow(2, p2);
		packing3 = 1f / (float) Math.pow(2, p3);
	}

	public void setMaterial(int i, int j) {
		material = i;
		layer = j;
	}

	public void begin() {
		currentList = new VertexList();
		vertexLists.add(currentList);
		currentList.material = material;
		currentList.layer = layer;
	}

	public void end() {

	}

	public void cancel() {
		getVertices().remove(getVertices().size() - 1);
	}

	// comment going in the exported .OBJ file

	public void addComment(String s) {
		getList().vertices.get(getList().vertices.size() - 1).comment = s;
	}

	// V0
	public void addVertex(Point vertex, float u, float v, Point normal, byte[] colorb) {
		currentList.vertices.add(new Vertex(vertex, u, v, normal, colorb));
	}

	public void addVertex(Point p, float u, float v, byte[] n, byte[] color, short vgroup) {
		Point normal = new Point(n[0] / 127f, n[1] / 127f, n[2] / 127f);
		currentList.vertices.add(new Vertex(p, u, v, normal, color));
	}

	public void addVertex(Point p, float u, float v, Point normal, byte[] color, int i) {
		currentList.vertices.add(new Vertex(p, u, v, normal, color));
	}

	public void addVertex(Point p, float[] uv, Point normal, float[] color, int vg) {
		currentList.vertices.add(new Vertex(p, uv, normal, color, vg));
	}

	public void addVertex(Point p) {
		currentList.vertices.add(new Vertex(p));
	}

	public void center() {
		Box box = new Box();
		for (VertexList list : getVertices()) {
			for (Vertex v : list.vertices) {
				box.add(new Point(v.x, v.y, v.z));
			}
		}
		Point c = box.getCenter();
		for (VertexList list : getVertices()) {
			for (Vertex v : list.vertices) {
				v.x -= c.x;
				v.y -= c.y;
				v.z -= c.z;
			}
		}
	}

	public Box getBox() {
		Box ret = new Box();
		for (VertexList list : getVertices()) {
			for (Vertex v : list.vertices) {
				ret.add(v.x, v.y, v.z);
			}
		}
		return ret;
	}

	public void translate(Point center) {
		for (VertexList list : getVertices()) {
			for (Vertex v : list.vertices) {
				v.x += center.x;
				v.y += center.y;
				v.z += center.z;
			}
		}
	}

	public MaterialPalette getMaterialPalette() {
		return matpal;
	}

	public List<VertexList> getVertices() {
		return vertexLists;
	}

	public VertexList getList() {
		return currentList;
	}

	@Override
	public void load() throws IOException {
		seek();
		int ver = info.version;
		debug("ver " + ver);
		if (ver == 0) {
			loadPrimBufferV0();
			return;
		}
		int dict_id = 0;
		if (ver > 1) {
			dict_id = readInt();
		}
		Set<Short> vgroups_seen = new HashSet<Short>(); // for debug
		int pbtype = readInt();
		int nmats = readInt();
		int nfaces = readInt();
		int unkown = readInt();
		int p1 = readInt();
		int p2 = readInt();
		int p3 = readInt();
		Point[] preTranslations = null;
		// SimpleSprite doesn't use preTranslations
		if (info.getParent().type == ObjType.SimpleSubSprite) {
			SimpleSubSprite parSprite = (SimpleSubSprite) info.getParent().getObj();
			if (parSprite.usePretrans) {
				// get preTranslations from the parent Zone object
				Zone zone = (Zone) info.getParent(ObjType.Zone).getObj();
				if (zone != null)
					preTranslations = zone.getZoneBase().getPreTranslations();
			}
		}
		int prevmat = -1;
		debug(String.format("PrimBuffer dict_id=%x pbtype=%d nmats=%d nfaces=%d unknown=%d par=%s pretrans=%s", dict_id,
				pbtype, nmats, nfaces, unkown, info.getParent(), preTranslations != null));
		ObjInfo par = info.getParent();
		debug("par=" + par);
		matpal = ((SimpleSprite) par.getObj()).getMatPal();
		debug("matpal=" + matpal);
		if (matpal != null)
			debug("has " + matpal.materials.size() + " materials");
		setPacking(p1, p2, p3);
		byte[] normal = new byte[3];
		byte[] color = new byte[4];
		int totalverts = 0;
		for (int fi = 0; fi < nfaces; fi++) {
			int nverts = readInt();
			int mat = readInt();
			setMaterial(mat, 0);
			if (pbtype == 4 || pbtype == 2) {
				setMaterial(mat, 0);
				begin();
				// list.layers=matpal.materials.get(mat).layers.length;
				currentList.layers = 1;
				for (int i = 0; i < nverts; i++) {
					totalverts++;
					short x = readShort();
					short y = readShort();
					short z = readShort();
					short u = readShort();
					short v = readShort();
					readBytes(normal);
					readBytes(color);
					short vgroup = 0;
					if (pbtype == 4) {
						vgroup = readShort();
						vgroups_seen.add(vgroup);
					}
					Point p = new Point(x * packing1, y * packing1, z * packing1);
					if (preTranslations != null)
						p.addTo(preTranslations[vgroup]);
					addVertex(p, u * packing2, v * packing2, normal, color, vgroup);
				}
				end();
			} else if (pbtype == 5) {
				setMaterial(mat, 0);
				begin();
				currentList.layers = 1;
				for (int i=0; i<nverts; i++) {
					short x = readShort();
					short y = readShort();
					short z = readShort();
					short u = readShort();
					short v = readShort();
					readBytes(normal);
					readBytes(color); // bones
					readBytes(color); // weights
					Point p = new Point(x * packing1, y * packing1, z * packing1);
					addVertex(p, u * packing2, v * packing2, normal, color, (short)-1);
				}
				end();
			} else {
				debug("unimplemented pbtype");
			}
		}
		debug("totalverts=" + totalverts + " vgroups seen: " + vgroups_seen);
		debug("box=" + box + ", dimensions=" + box.getDimensions());
	}

	// PrimBuffer V0 seems to be very rarely used.
	// Here the values are floats and preTranslations aren't used.

	void loadPrimBufferV0() throws IOException {
		int numMaterials = readInt();
		int numFaces = readInt();
		int numSomething = readInt();
		ObjInfo par = info.getParent();
		debug("par=" + par);
		matpal = ((SimpleSprite) par.getObj()).getMatPal();
		debug("matpal=" + matpal);
		if (matpal != null)
			debug("has " + matpal.materials.size() + " materials");
		for (int i = 0; i < numFaces; i++) {
			int numVertices = readInt();
			int material = readInt();
			setMaterial(material, 0);
			begin();
			for (int j = 0; j < numVertices; j++) {
				Point vertex = readPoint();
				float u = readFloat();
				float v = readFloat();
				Point normal = readPoint();
				byte[] color = new byte[4];
				readBytes(color);
				addVertex(vertex, u, v, normal, color);
			}
			end();
		}
	}

}
