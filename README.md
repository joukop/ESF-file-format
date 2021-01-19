
# ESF file format

This package can be used to read .ESF and .CSF files from Everquest Online Adventures. Testing was done with the vanilla (not Frontiers) PAL version of the game.

Many things are still unimplemented but most of the 3D objects probably work. Loading e.g. terrain collision buffers is a small step towards interoperability between the PS2 game and third-party servers.

This project shouldn't be taken as an example of how to write good code - the loader was written in less than a week in January 2021 and about another week was spent re-thinking the class structure, cleaning up, adding support for some additional object types, writing comments, etc.  

We use [Netty-4.1.55.Final](https://netty.io/downloads.html) for byte buffer operations, so we can use the same operations as our (in-development) server.

## File structure

`|File header|Object 1 header|Object 1 body|Object 2 header|Object 2 body| ...`

##File header, 32 bytes

- 'OBJF', char[4]
- number of objects in the file, int32
- file type, int32
- unknown, int32
- offset, int64
- unknown, int64


## Object header, 12 bytes

- object type, int16
- object version, int16
- object size, int32
- number of sub-objects, int32


# Hierarchy
The objects are arranged as a hierarchy, for example

```
ZoneBase(3200) {
	ZoneTree(3220)
	ZonePreTranslations(3250)
	ZoneRooms(3230) {
		ZoneRoom(3240)
		ZoneRoom(3240)
		...
	}
	...
}
		
```
# Examples

The ``Examples`` class has some examples of using this library to extract data from ESF files. Supply the path to your Tunaria.ESF as the argument.

Some YouTube videos created with this package: [link](https://www.youtube.com/watch?v=7nm-pxD5xP8&list=PLSdoxXXW_vHCDT0EaQsc1IGtfGIXNLdhP)


# Types of objects in ESF files

- ``SimpleSprite``: the lowest level Sprite type in this implementation (any 3D object and a few other things are called Sprites). SimpleSprites can be e.g. buildings or sub-objects of other sprite types.
- ``SimpleSubSprite``: terrain pieces and some buildings are of this type. The difference to SimpleSprite is apparently that this doesn't have a MaterialPalette sub-object, but an ID referring one.
- ``GroupSprite``: contains a list of sub-sprites and their relative positions and rotations. Many reusable small and large objects are of this type.
- ``HSprite``: a complex sprite type with sub-sprites. Partially implemented at the moment.
- ``LODSprite``: contains a list of the same object with different levels of detail, e.g. trees.
- ``Material``: has information how to render the 3D objects. Refers to Surface (texture) objects. One material can have many layers with different texture setups.
- ``MaterialPalette``: collection of Material objects
- ``Surface``: a 2D bitmap image used as a texture or UI graphics
- ``PrimBuffer``: list of vertices composing a 3D object, with associated information such as texture U and V coordinates, material references, and face normals 
- ``CollBuffer``: a simplified version of PrimBuffer usable in collision detection. Doesn't contain information about materials, colors, etc.
- ``Zone``: container for zone-related data
- ``ZoneActors``: list of objects found in this zone, listed by Sprite ID, location and alignment

# Some other classes in this implementation
- ``ObjFile``: reads the file header, maintains a dictionary of objects by their ID's, calls the appropriate loader class of an object when needed
- ``ObjInfo``: holds information about an object, without the actual data (such as textures or vertices). The method getObj() loads the actual data.
- ``ObjExport``: used to export 3D objects into Wavefront .OBJ and .MTL files. Tested with Blender only.
- ``ObjBrowser``: tool for viewing the object hierarchy
