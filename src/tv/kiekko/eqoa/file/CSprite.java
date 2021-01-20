package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.ArrayList;

/* This sprite type seems to contain mobs and characters.
 * Very unimplemented, but materials and primbuffer seem to work.
 */

public class CSprite extends GroupSprite {
	int dict_id;
	
	public CSprite(ObjInfo info) {
		super(info);
	}
	
	
	@Override
	public void load() throws IOException {
		seek();
		dict_id=readInt();
		box=readBox();
		int ver=info.getObjectVersion();
		int unk1=0;
		float unk2;
		int unk3;
		int unk4;
		if (ver!=0) {
			unk1=readInt();
		}
		if (ver<2) {
			unk2=1f;
		} else {
			unk2=readFloat();
		}
		if (ver<3) {
			unk3=9;
			unk1=0;
		} else {
			unk3=readInt();
			unk1=readInt();
		}
		if (ver<4) {
			unk4=1;
		} else {
			unk4=readInt();
		}
		placements=new ArrayList<SpritePlacement>();
		for (ObjInfo s : info.getChild(ObjType.CSpriteArray).getChildren()) {
			Obj subsprite=s.getObj();
			if (!(subsprite instanceof SimpleSprite)) {
				debug("Can't place "+subsprite);
				continue;
			}
			placements.add(new SpritePlacement((SimpleSprite)subsprite));
			break;
		}
	}
	
}
