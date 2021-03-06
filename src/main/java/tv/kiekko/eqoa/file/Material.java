package tv.kiekko.eqoa.file;

import java.io.IOException;

public class Material extends Obj {

	int tessellate;
	byte[] emissiveCol;
	MaterialLayer[] layers;

	public MaterialLayer[] getLayers() {
		return layers;
	}

	public Material(ObjInfo info) throws IOException {
		super(info);
		seek();
		int nlayers = readInt();
		debug("nlayers=" + nlayers);
		int layerFlags = 0;
		int ver = info.getObjectVersion();
		if (ver > 1) {
			int tes = readInt();
			debug("tessellate=" + tes);
			// if ((tes&1)!=0) mat.setTesselate(1)
			tessellate = tes;
		}
		if (ver > 2) {
			emissiveCol = readColor();
		}
		layers = new MaterialLayer[nlayers];
		for (int i = 0; i < nlayers; i++) {
			MaterialLayer layer = new MaterialLayer();
			layers[i] = layer;
			layerFlags = readInt();
			layer.flags = layerFlags;
			debug("layer " + (i + 1) + "/" + nlayers + " flags=" + layerFlags);
			// if ((layerFlags&1)!=0) setLayerFilltype
			// if ((layerFlags&2)!=0) setLayerUVSet
			int tex_id = readInt();
			if (tex_id != 0) {
				layer.surface = (Surface) info.file.findObject(tex_id);
			}
			layer.tex_id = tex_id;
			debug("tex_id=" + String.format("%x", layer.tex_id) + " surface=" + layer.surface);
			layer.wrapmode = readInt();
			layer.blendmode = readInt();
			debug("wrapmode=" + layer.wrapmode + " blendmode=" + layer.blendmode);
			layer.color = readColor();
			float[] uvt = new float[9];
			String dbg = "uv transform: ";
			for (int j = 0; j < 9; j++) {
				uvt[j] = readFloat();
				dbg += "" + uvt[j] + " ";
			}
			debug(dbg);
			layer.uvTransform = uvt;
			float lodBias;
			if (ver == 0) {
				readFloat();
				lodBias = 0.80000001f;
			} else {
				lodBias = readFloat();
			}
			layer.lodBias = lodBias;
			layer.u_rate = readFloat();
			layer.v_rate = readFloat();
			debug("lodBias=" + lodBias + " uvrate=" + layer.u_rate + "," + layer.v_rate);
		}
	}

	@Override
	protected void debug(String s) {
	}

}
