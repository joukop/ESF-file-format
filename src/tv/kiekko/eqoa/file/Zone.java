package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Zone extends Obj {

	public ZoneActors getZoneActors() throws IOException {
		ObjInfo actorsInfo = info.getChild(ObjType.ZoneActors);
		return (ZoneActors) actorsInfo.getObj();
	}

	public ZoneBase getZoneBase() throws IOException {
		ObjInfo baseInfo = info.getChild(ObjType.ZoneBase);
		return (ZoneBase) baseInfo.getObj();
	}

	@Override
	public void load() throws IOException {
		// nothing to do actually, we only need the sub-objects
	}

	// Debug: if there's a ZoneActor with an ID that's not found in the Dictionary,
	// it probably means our ObjType doesn't recognize an object type correctly.
	// Try to find the lost ID in all objects in the file.

	public void whatIs(int dict_id) {
		for (ObjInfo o : info.file.objects) {
			if (o.type == 0x6000)
				continue;
			info.file.seek(o);
			int testId = this.readInt();
			if (testId == dict_id) {
				debug("ObjType problem: the type " + String.format("%04x", o.type) + " has a dictionary ID");
			}
		}
	}

	// Create a list of all (known, implemented) 3D objects in the zone and
	// calculate their placements

	public List<SpritePlacement> getSpritePlacements() throws IOException {
		List<SpritePlacement> ret = new ArrayList<SpritePlacement>();
		ObjInfo resources = info.getChild(ObjType.ZoneResources);

		// directly export SimpleSubSprites
		List<ObjInfo> sprites = resources.getChildren(ObjType.SimpleSubSprite);
		for (ObjInfo s : sprites) {
			ret.add(new SpritePlacement((SimpleSprite) s.getObj()));
		}

		// all other types via ZoneActors?
		ZoneActors actors = getZoneActors();
		for (ZoneActor a : actors.getActors()) {
			Obj obj = info.file.findObject(a.placement.sprite_id);
			if (obj == null) {
				// problem in ObjType? debug
				debug("actor not found: " + String.format("%08x", a.placement.sprite_id));
				whatIs(a.placement.sprite_id);
				continue;
			}
			// TODO: this is still unnecessarily confusing
			if (obj instanceof GroupSprite) {
				for (SpritePlacement sp : ((GroupSprite) obj).getSprites()) {
					ObjFile.debug("export: gs=" + obj + " placement=" + sp);
					SimpleSprite o = sp.getSprite(info.file);
					if (o != null) {
						debug("groupsprite(" + obj.getClass().getSimpleName() + ") has " + o + " at " + sp);
						// GroupSprite has absolute rotation, ignore ZoneActor rotation
						// HSprite has relative rotation, add ZoneActor rotation
						SpritePlacement p = sp.add(a.placement);
						if (!(obj instanceof HSprite))
							p.rot = sp.rot;
						ret.add(new SpritePlacement((SimpleSprite) o, p));
					}
				}
			} else if (obj instanceof LODSprite) {
				SimpleSprite s = (SimpleSprite) ((LODSprite) obj).getSprite();
				ret.add(new SpritePlacement(s, a.placement));
			} else if (obj instanceof SimpleSprite) {
				ret.add(new SpritePlacement((SimpleSprite) obj, a.placement));
			} else {
				ObjFile.debug("can't place obj of this type: " + obj);
			}
		}
		return ret;
	}

	public Zone(ObjInfo info) {
		super(info);
	}

}