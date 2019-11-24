var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');

function initializeCoreMod() {
    return {
        'active_render_info_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.ActiveRenderInfo'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_216772_a",
                    name: "update",
                    desc: "(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/entity/Entity;ZZF)V",
                    patch: patchActiveRenderInfoUpdate
                }, classNode, "ActiveRenderInfo");
                return classNode;
            }
        }
    };
}

function findMethod(methods, entry) {
    var length = methods.length;
    for (var i = 0; i < length; i++) {
        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {
            return method;
        }
    }
    return null;
}

function patch(entry, classNode, name) {
    var method = findMethod(classNode.methods, entry);
    var flag;
    log("Patching " + name + "...");
    if (method !== null) {
        var obfuscated = method.name.equals(entry.obfName);
        flag = entry.patch(method, obfuscated);
    }
    if (flag) {
        log("Patching " + name + " was successful");
    } else {
        log("Patching " + name + " failed");
    }
}

function patchActiveRenderInfoUpdate(method, obfuscated) {
    var pitch = obfuscated ? "field_216797_i" : "pitch";
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if (node instanceof FieldInsnNode && node.getOpcode().equals(Opcodes.GETFIELD) && node.name.equals(pitch)) {
            if (node.getNext() instanceof InsnNode && node.getNext().getOpcode().equals(Opcodes.FCONST_1)) {
                if (node.getPrevious() instanceof VarInsnNode && node.getPrevious().getOpcode().equals(Opcodes.ALOAD)) {
                    foundNode = node;
                    break;
                }
            }
        }
    }
    if (foundNode != null) {
        var insnList = new InsnList();
        insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/fuzs/consoleexperience/handler/ElytraTiltHandler", "roll", "F"));
        insnList.add(new InsnNode(Opcodes.FCONST_0));
        insnList.add(new InsnNode(Opcodes.FCONST_0));
        insnList.add(new InsnNode(Opcodes.FCONST_1));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mojang/blaze3d/platform/GlStateManager", "rotatef", "(FFFF)V", false));
        method.instructions.insertBefore(getNthNode(foundNode, -2), insnList);
        return true;
    }
}

function getNthNode(node, n) {
    for (var i = 0; i < Math.abs(n); i++) {
        if (n < 0) {
            node = node.getPrevious();
        } else {
            node = node.getNext();
        }
    }
    return node;
}

function log(s) {
    print("[Console Experience Transformer]: " + s);
}