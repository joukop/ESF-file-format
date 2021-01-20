package tv.kiekko.eqoa.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjType {

	public static final int SurfaceArray = 0x1001;
	public static final int Surface = 0x1000;
	public static final int MaterialPalette = 0x1110;
	public static final int MaterialPaletteHeader = 0x1111;
	public static final int MaterialArray = 0x1101;
	public static final int Material = 0x1100;
	public static final int PrimBuffer = 0x1200;
	public static final int SkinPrimBuffer = 0x1210;
	public static final int SimpleSprite = 0x2000;
	public static final int SimpleSpriteHeader = 0x2001;
	public static final int HSprite = 0x2200;
	public static final int HSpriteHeader = 0x2210;
	public static final int HSpriteArray = 0x2220;
	public static final int HSpriteHierarchy = 0x2400;
	public static final int SimpleSubSprite = 0x2310;
	public static final int SimpleSubSpriteHeader = 0x2311;
	public static final int SkinSubSprite = 0x2320;
	public static final int SkinSubSprite2 = 0x2321;
	public static final int HSpriteAnim = 0x2600;
	public static final int RefMap = 0x5000;
	public static final int CSprite = 0x2700;
	public static final int CSpriteHeader = 0x2710;
	public static final int CSpriteArray = 0x2800;
	public static final int LODSprite = 0x2a10;
	public static final int LODSpriteArray = 0x2a20;
	public static final int PointLight = 0x2b00;
	public static final int GroupSprite = 0x2c00;
	public static final int GroupSpriteHeader = 0x2c10;
	public static final int GroupSpriteArray = 0x2c20;
	public static final int GroupSpriteMembers = 0x2c30;
	public static final int StreamAudioSprite = 0x2e00;
	public static final int PointSprite = 0x2d00;
	public static final int FloraSprite = 0x2f00;
	public static final int Zone = 0x3000;
	public static final int ZoneResources = 0x3100;
	public static final int ZoneBase = 0x3200;
	public static final int ZoneTree = 0x3220;
	public static final int ZoneRooms = 0x3230;
	public static final int ZoneRoom = 0x3240;
	public static final int ZonePreTranslations = 0x3250;
	public static final int ZoneRoomActors = 0x3270;
	public static final int ZoneRoomActors2 = 0x3280;
	// public static final int ZoneRoomStaticLightings = 0x3290;
	public static final int ZoneRoomStaticLightings2 = 0x32a0;
	public static final int ZoneStaticLightnings = 0x32b0;
	public static final int ZoneStaticTable = 0x32c0;
	public static final int ZoneFlora = 0x32d0;
	public static final int CollBuffer = 0x4200;
	public static final int ZoneActor = 0x6000;
	public static final int ZoneActors = 0x3290;
	public static final int StaticLighting = 0x6010;
	public static final int StaticLightingObj = 0x6020;
	public static final int ZoneRoomStaticLightings3 = 0x6030;
	public static final int ZoneRoomActors3 = 0x6040;
	public static final int Font = 0x7000;
	public static final int Root = 0x8000;
	public static final int World = 0x8100;
	public static final int WorldBase = 0x8200;
	public static final int WorldZoneProxies = 0x8210;
	public static final int WorldBaseHeader = 0x8220;
	public static final int WorldTree = 0x8230;
	public static final int WorldRegions = 0x8240;
	public static final int ResourceTable = 0x9000;
	public static final int ResourceDir = 0xa000;
	public static final int ResourceDir2 = 0xa010;
	public static final int Adpcm = 0xb000;
	public static final int ParticleDefinition = 0xc000;
	public static final int Xm = 0xb030;
	public static final int SoundSprite = 0xb100;
	public static final int SpellEffect = 0xc200;
	public static final int ParticleSprite = 0xc100;
	public static final int ParticleSpriteHeader = 0xc101;
	public static final int EffectVolumeSprite = 0xc300;

	int type;
	String name;
	Integer dictIdOffset; // TODO: they're all at zero? could just use a boolean

	public boolean hasID() {
		return dictIdOffset != null;
	}

	ObjType(int type, String name) {
		this(type, name, null);
	}

	ObjType(int type, String name, Integer dictIdOffset) {
		this.type = type;
		this.name = name;
		this.dictIdOffset = dictIdOffset;
	}

	// These types only contain the dictionary ID used to find the parent object

	public boolean isIdContainer() {
		if (type == ObjType.SimpleSpriteHeader || type == ObjType.GroupSpriteHeader || type == ObjType.HSpriteHeader
				|| type == ObjType.MaterialPaletteHeader || type==ObjType.CSpriteHeader) {
			return true;
		}
		return false;
	}
	
	// TODO: some kind of more compact/easy/automated way of defining ObjTypes

	static List<ObjType> objTypes = new ArrayList<>();
	static Map<Integer, ObjType> objTypeMap = new HashMap<>();
	static {
		objTypes.add(new ObjType(0x1001, "SurfaceArray"));
		objTypes.add(new ObjType(0x1000, "Surface", 0));
		objTypes.add(new ObjType(0x1110, "MaterialPalette"));
		objTypes.add(new ObjType(0x1100, "Material"));
		objTypes.add(new ObjType(0x1101, "MaterialArray"));
		objTypes.add(new ObjType(0x1111, "MaterialPaletteHeader", 0));
		objTypes.add(new ObjType(0x1200, "PrimBuffer", 0)); // PrimBufferV0 doesn't have an ID
		objTypes.add(new ObjType(0x1210, "SkinPrimBuffer"));
		objTypes.add(new ObjType(0x2000, "SimpleSprite"));
		objTypes.add(new ObjType(0x2001, "SimpleSpriteHeader", 0));
		objTypes.add(new ObjType(0x2200, "HSprite"));
		objTypes.add(new ObjType(0x2210, "HSpriteHeader", 0));
		objTypes.add(new ObjType(0x2220, "HSpriteArray"));
		objTypes.add(new ObjType(0x2400, "HSpriteHierarchy"));
		objTypes.add(new ObjType(0x2450, "HSpriteTriggers"));
		objTypes.add(new ObjType(0x2500, "HSpriteAttachments"));
		objTypes.add(new ObjType(0x5000, "RefMap", 0));
		objTypes.add(new ObjType(0x2310, "SimpleSubSprite"));
		objTypes.add(new ObjType(0x2320, "SkinSubSprite",0));
		objTypes.add(new ObjType(0x2321, "SkinSubSprite2"));
		objTypes.add(new ObjType(0x2600, "HSpriteAnim"));
		objTypes.add(new ObjType(0x2700, "CSprite"));
		objTypes.add(new ObjType(0x2710, "CSpriteHeader",0));
		objTypes.add(new ObjType(0x2800, "CSpriteArray"));
		objTypes.add(new ObjType(0x2a10, "LODSprite", 0));
		objTypes.add(new ObjType(0x2a20, "LodSpriteArray"));
		objTypes.add(new ObjType(0x2b00, "PointLight"));
		objTypes.add(new ObjType(0x2c00, "GroupSprite"));
		objTypes.add(new ObjType(0x2c10, "GroupSpriteHeader", 0));
		objTypes.add(new ObjType(0x2c20, "GroupSpriteArray"));
		objTypes.add(new ObjType(0x2c30, "GroupSpriteMembers"));
		objTypes.add(new ObjType(0x2e00, "StreamAudioSprite", 0));
		objTypes.add(new ObjType(0x2e10, "StreamAudioSpriteHeader"));
		objTypes.add(new ObjType(0x2d00, "PointSprite"));
		objTypes.add(new ObjType(0x2f00, "FloraSprite"));
		objTypes.add(new ObjType(0x3000, "Zone"));
		objTypes.add(new ObjType(0x3100, "ZoneResources"));
		objTypes.add(new ObjType(0x3200, "ZoneBase"));
		objTypes.add(new ObjType(0x3220, "ZoneTree"));
		objTypes.add(new ObjType(0x3230, "ZoneRooms"));
		objTypes.add(new ObjType(0x3240, "ZoneRoom"));
		objTypes.add(new ObjType(0x3250, "ZonePreTranslations"));
		objTypes.add(new ObjType(0x3270, "ZoneRoomActors"));
		objTypes.add(new ObjType(0x3280, "ZoneRoomActors2"));
		// objTypes.add(new ObjType(0x3290, "ZoneRoomStaticLightings"));
		objTypes.add(new ObjType(0x3290, "ZoneActors"));
		objTypes.add(new ObjType(0x32a0, "ZoneRoomStaticLightings2"));
		objTypes.add(new ObjType(0x32b0, "ZoneStaticLightnings"));
		objTypes.add(new ObjType(0x32c0, "ZoneStaticTable"));
		objTypes.add(new ObjType(0x32d0, "ZoneFlora"));
		objTypes.add(new ObjType(0x4200, "CollBuffer", 0));
		objTypes.add(new ObjType(0x6000, "ZoneActor"));
		objTypes.add(new ObjType(0x6010, "StaticLighting"));
		objTypes.add(new ObjType(0x6020, "StaticLightingObj"));
		objTypes.add(new ObjType(0x6030, "ZoneRoomStaticLightings3"));
		objTypes.add(new ObjType(0x6040, "ZoneRoomActors3"));
		objTypes.add(new ObjType(0x7000, "Font"));
		objTypes.add(new ObjType(0x8000, "Root"));
		objTypes.add(new ObjType(0x8100, "World"));
		objTypes.add(new ObjType(0x8200, "WorldBase"));
		objTypes.add(new ObjType(0x8210, "WorldZoneProxies"));
		objTypes.add(new ObjType(0x8220, "WorldBaseHeader"));
		objTypes.add(new ObjType(0x8230, "WorldTree"));
		objTypes.add(new ObjType(0x8240, "WorldRegions"));
		objTypes.add(new ObjType(0x9000, "ResourceTable"));
		objTypes.add(new ObjType(0xa000, "ResourceDir"));
		objTypes.add(new ObjType(0xa010, "ResourceDir2"));
		objTypes.add(new ObjType(0xb000, "Adpcm"));
		objTypes.add(new ObjType(0xc000, "ParticleDefinition"));
		objTypes.add(new ObjType(0xb030, "Xm"));
		objTypes.add(new ObjType(0xb100, "SoundSprite"));
		objTypes.add(new ObjType(0xc200, "SpellEffect"));
		objTypes.add(new ObjType(0xc100, "ParticleSprite"));
		objTypes.add(new ObjType(0xc101, "ParticleSpriteHeader", 0));
		objTypes.add(new ObjType(0xc300, "EffectVolumeSprite"));
		objTypes.add(new ObjType(0xc310, "EffectVolumeSpriteHeader", 0));

		for (ObjType t : objTypes)
			objTypeMap.put(t.type, t);

	}

}
