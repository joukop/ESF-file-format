package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MaterialPalette extends Obj {
	List<Material> materials;

	public List<Material> getMaterials() {
		return materials;
	}

	public MaterialPalette(ObjInfo info) {
		super(info);
		materials = new ArrayList<Material>();
	}

	@Override
	public void load() throws IOException {
		info = info.getChild(ObjType.MaterialArray);
		for (ObjInfo m : info.getChildren(ObjType.Material)) {
			materials.add((Material) m.getObj());
		}
	}

	@Override
	protected void debug(String s) {
	}

	public int getSize() {
		return materials.size();
	}
}