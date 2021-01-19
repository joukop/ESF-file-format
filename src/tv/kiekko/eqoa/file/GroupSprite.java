package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.kiekko.eqoa.geom.Point;

public class GroupSprite extends SimpleSprite {
	ObjInfo array;
	List<SpritePlacement> placements;

	public GroupSprite(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() throws IOException {
		seek();
		skipBytes(4); // dict id
		box = readBox();
		array = info.getChild(ObjType.GroupSpriteArray);
		Obj members = info.getChild(ObjType.GroupSpriteMembers).getObj();
		members.seek();
		placements = new ArrayList<SpritePlacement>();
		int nmemb = members.readInt();
		for (int i = 0; i < nmemb; i++) {
			int memb_id = members.readInt();
			Point pos = members.readPoint();
			Point rot = members.readPoint();
			float f = members.readFloat(); // unknown?
			placements.add(new SpritePlacement(memb_id, pos, rot, 1));
		}
	}

	public List<SpritePlacement> getSprites() throws IOException {
		return placements;
	}

}