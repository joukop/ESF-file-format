import bpy
mats=bpy.context.active_object.material_slots
for mat in mats:
    nodes=mat.material.node_tree.nodes
    for node in nodes:
        if node.type=="TEX_IMAGE":
            node.interpolation="Closest"

