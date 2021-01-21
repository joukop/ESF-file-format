package tv.kiekko.eqoa.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import tv.kiekko.eqoa.file.PrimBuffer.Vertex;
import tv.kiekko.eqoa.file.PrimBuffer.VertexList;
import tv.kiekko.eqoa.geom.Point;

/* This is used in .OBJ exporting to contain a number of Sprites and
 * their materials.
 * 
 */

public class ObjExport {
	PrimBuffer prim;
	List<MaterialPalette> bigMats = new ArrayList<MaterialPalette>();
	float sizeCutoff = 0;
	int smallObjects = 0;
	int exportType = 0;	// 0 = primbuffer, 1 = collbuffer

	public ObjExport() throws IOException {
		prim = new PrimBuffer(null);
		bigMats = new ArrayList<MaterialPalette>();
	}

	public void write(String fn) throws IOException {
		ObjFile.debug("smallObjects=" + smallObjects);
		ObjFile.debug("writing " + fn);
		exportPrimBuffer(prim, fn, bigMats);
		ObjFile.debug("done");
	}

	public Point getCenter() {
		return prim.getBox().getCenter();
	}

	public void addColl(SimpleSprite s, SpritePlacement sp) throws IOException {
		CollBuffer cb = s.getCollBuffer();
		ObjFile.debug("addToBigObj s=" + s + " cb=" + cb);
		boolean first = true;
		for (CollBuffer.VertexList list : cb.getVertexLists()) {
			prim.begin();
			boolean skip = false;
			for (CollBuffer.Vertex v : list.vertices) {
				Point p = v.getXYZ();
				if (sp != null) {
					sp.transform(p);
				}
				prim.addVertex(p);
				if (first) {
					first = false;
					prim.addComment(s.toString());
				}
			}
			// if (skip) prim.cancel(); else
			prim.end();
		}
	}

	// If you want to export e.g. several zones for landscape viewing, it can be
	// useful to exclude the smallest
	// objects (tables, chairs, lamps, paintings on the walls...)

	public void setSizeCutoff(float s) {
		sizeCutoff = s;
	}

	public void addAll(List<SpritePlacement> sps, ObjFile file) throws IOException {
		for (SpritePlacement sp : sps) {
			add(sp, file);
		}
	}
	
	public void setExportType(int t) {
		exportType = t;
	}
	

	public void add(SpritePlacement sp, ObjFile file) throws IOException {
		SimpleSprite s = sp.getSprite(file);

		// don't omit even small pieces of ground SimpleSubSprites
		// TODO: check if it's actually a piece of terrain and not other SimpleSubSprite

		if (sizeCutoff > 0 && s.box.getSize() < sizeCutoff && !(s instanceof SimpleSubSprite)) {
			ObjFile.debug("export: omitting small object " + s);
			smallObjects++;
			return;
		}
		if (exportType == 0)
			addPrimBuffer(sp,file,s);
		else
			addCollBuffer(sp,file,s);
	}
	
	void addCollBuffer(SpritePlacement sp, ObjFile f, SimpleSprite s) throws IOException {
		CollBuffer cb = s.getCollBuffer();
		ObjFile.debug("export adding collbuffer s=" + s + " sp=" + sp);
		boolean first = true;
		for (CollBuffer.VertexList list : cb.getVertexLists())  {
			prim.begin();
			boolean skip = false;
			for (CollBuffer.Vertex v : list.vertices ) {
				Point p = v.getXYZ();
				if (sp != null) {
					sp.transform(p);
				}
				prim.addVertex(p);
				if (first) {
					first = false;
					prim.addComment(s.toString());
				}
			}
			// if (skip) prim.cancel(); else
			prim.end();
		}
	}
	
	void addPrimBuffer(SpritePlacement sp, ObjFile f, SimpleSprite s) throws IOException {
		PrimBuffer pb = s.getPrimBuffer();

		ObjFile.debug("export: adding s=" + s + " sp=" + sp);

		MaterialPalette mp = pb.getMaterialPalette();
		if (!bigMats.contains(mp)) {
			bigMats.add(mp);
			ObjFile.debug("bigMats now: " + bigMats);
		}
		int mat_idx = bigMats.indexOf(mp);
		int mat_offset = 0;
		for (int i = 0; i < mat_idx; i++)
			mat_offset += bigMats.get(i).getSize();

		boolean first = true;
		for (PrimBuffer.VertexList list : pb.getVertices()) {
			prim.setMaterial(list.material + mat_offset, list.layer);
			prim.begin();
			prim.getList().layers = list.layers;
			boolean skip = false;
			for (PrimBuffer.Vertex v : list.vertices) {
				Point p = v.getXYZ();
				float[] color = v.getColor();
				float[] uv = v.getUV();
				Point normal = v.getNormal();
				if (sp != null) {
					sp.transform(p);
				}
				prim.addVertex(p, uv, normal, color, 0);
				if (first) {
					first = false;
					prim.addComment(s.toString());
				}
			}
			// if (skip) prim.cancel(); else
			prim.end();
		}
	}

	public void center() {
		Point center = getCenter();
		center.negate();
		prim.translate(center);
	}

	public static void centerArray(ObjExport[] e) {
		Point center = new Point(0, 0, 0);
		ObjFile.debug("calculating center...");
		for (int i = 0; i < e.length; i++) {
			center.addTo(e[i].getCenter());
		}
		ObjFile.debug("center=" + center);
		center.multiplyWith(1f / e.length);
		center.negate();
		for (int i = 0; i < e.length; i++) {
			ObjFile.debug("translating " + (i + 1) + "/" + e.length);
			e[i].prim.translate(center);
		}
	}

	void exportPrimBuffer(PrimBuffer pb, String filename, List<MaterialPalette> extra_mats) throws IOException {
		String mtlfile = exportPrimBufferMaterials(pb, filename, extra_mats);

		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		int[] first = new int[pb.getVertices().size()];

		out.write("mtllib " + mtlfile + "\n");

		int i = 0, s = 0;
		for (VertexList list : pb.getVertices()) {
			first[s++] = i;
			out.write("# verts " + (i + 1) + " .. " + (i + 1 + list.vertices.size()) + " material " + list.material
					+ "\n");
			for (int l = 0; l < 1 /* list.layers */; l++) {
				for (Vertex v : list.vertices) {
					if (v.comment != null)
						out.write("# " + v.comment + "\n");
					out.write(String.format("v %f %f %f\n", v.x, v.y, v.z));
				}
				for (Vertex v : list.vertices)
					out.write(String.format("vt %f %f\n", v.u, 1 - v.v));
				for (Vertex v : list.vertices)
					out.write(String.format("vn %f %f %f\n", v.nx, v.ny, v.nz));
				i += list.vertices.size();
			}
		}

		out.newLine();
		s = 0;
		for (VertexList list : pb.getVertices()) {
			boolean odd = true;
			out.write("list=" + s + " layers=" + list.layers + "\n");
			int a = 0;
			for (int layer = 0; layer < 1 /* list.layers */; layer++) {
				out.write("# layer " + layer + "/" + list.layers + "\n");
				out.write("usemtl material" + list.material + "-" + layer + "\n");
				out.write("# first=" + first[s] + " size=" + list.vertices.size() + "\n");
				for (int j = 1; j <= list.vertices.size() - 2; j++) {
					i = j + first[s] + a;
					if (!odd)
						out.write(String.format("f %d/%d/%d %d/%d/%d %d/%d/%d\n", i, i, i, i + 1, i + 1, i + 1, i + 2,
								i + 2, i + 2));
					else
						out.write(String.format("f %d/%d/%d %d/%d/%d %d/%d/%d\n", i, i, i, i + 2, i + 2, i + 2, i + 1,
								i + 1, i + 1));
					odd = !odd;
				}
				a += list.vertices.size();
			}
			s++;
		}
		out.close();

	}

	static String debugFloats(float[] f) {
		String s = "";
		for (int i = 0; i < f.length; i++)
			s += "" + f[i] + " ";
		return s;
	}

	
	// Only one material layer is exported, not sure how to implement several layers in OBJ/MTL
	
	String exportPrimBufferMaterials(PrimBuffer pb, String filename, List<MaterialPalette> extra_mats)
			throws IOException {
		MaterialPalette matpal = pb.matpal;
		if (matpal == null)
			return null;
		String filebase = filename;
		if (filebase.endsWith(".obj") || filebase.endsWith(".OBJ"))
			filebase = filebase.substring(0, filename.length() - 4);
		filename = filebase + ".mtl";
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		int i = 0;
		if (extra_mats == null) {
			extra_mats = new Vector<MaterialPalette>();
			extra_mats.add(matpal);
		}
		for (MaterialPalette mat : extra_mats) {
			for (Material m : mat.materials) {
				for (int j = 0; j < 1 /* m.layers.length */; j++) {
					MaterialLayer layer = m.layers[j];
					String texdir = filebase + "_tex";
					String texfile = filebase + i + "-" + j + ".png";
					String alpfile;
					if (layer.surface != null) {
						if (layer.surface.alpha != null)
							alpfile = filebase + i + "-" + j + "-alpha.png";
						else
							alpfile = null;
						layer.surface.saveTexture(texdir, texfile, alpfile);
					} else {
						texfile = null;
						alpfile = null;
					}
					byte[] col = m.layers[0].color;
					String cols = "" + (((int) col[0]) & 255) / 255f + " " + (((int) col[1]) & 255) / 255f + " "
							+ (((int) col[2]) & 255) / 255f;
					out.write("newmtl material" + i + "-" + j + "\n");
					out.write("# blendmode=" + layer.blendmode + " wrapmode=" + layer.wrapmode + " flags=" + layer.flags
							+ " uvrate=" + layer.u_rate + "," + layer.v_rate + " col=" + layer.color + " uvt="
							+ debugFloats(layer.uvTransform) + "\n");
					Surface s = layer.surface;
					if (s != null)
						out.write("# surface depth=" + s.depth + " mip=" + s.mip + "\n");
					out.write("Ka " + cols + "\n");
					out.write("Kd 1 1 1\n");
					out.write("Ks 1 1 1\n");
					// out.write("Ns 10.000\n");
					out.write("illum 1\n");
					String scale = "";
					if (layer.uvTransform[0] != 1 || layer.uvTransform[4] != 1) {
						scale = "" + (1f / layer.uvTransform[0]) + " " + (1f / layer.uvTransform[4]) + " 1 ";
					}
					if (alpfile != null)
						out.write("map_d " + scale + texdir + File.separator + alpfile + "\n");
					if (texfile != null)
						out.write("map_Kd " + scale + texdir + File.separator + texfile + "\n");
					out.write("\n");
				}
				i++;
			}
		}
		out.close();
		return filename;
	}

}
