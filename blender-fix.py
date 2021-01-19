# .OBJ files imported to Blender have "seams" in some textures.
# This script fixes them. Source:
# https://github.com/jmc2obj/j-mc-2-obj/issues/132#issuecomment-639310026

import bpy
mats=bpy.context.active_object.material_slots
for mat in mats:
    nodes=mat.material.node_tree.nodes
    for node in nodes:
        if node.type=="TEX_IMAGE":
            node.interpolation="Closest"

