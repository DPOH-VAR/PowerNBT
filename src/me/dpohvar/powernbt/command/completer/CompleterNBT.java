package me.dpohvar.powernbt.command.completer;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.command.CommandNBT;
import me.dpohvar.powernbt.command.action.Argument;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.nbt.NBTContainerVariable;
import me.dpohvar.powernbt.utils.nbt.NBTQuery;
import me.dpohvar.powernbt.utils.nbt.NBTType;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.utils.versionfix.StaticValues.*;

public class CompleterNBT extends Completer {
    TypeCompleter completer;
    NBTQuery emptyQuery = new NBTQuery();

    public CompleterNBT() {
        super();
        completer = PowerNBT.plugin.getTypeCompleter();
    }

    @Override
    public void fillTabs(Caller caller, TabFormer former) throws Exception {
        String word = former.poll(); // object
        if (word.isEmpty()) {
            //todo: add $tmpfile completer
            former.addIfStarts("me", "item", "block", "buffer", "list", "compound", "byte[]", "int[]", "debug");
            if (caller.getOwner() instanceof Player && former.getQuery().startsWith("id")) {
                Player p = (Player) caller.getOwner();
                List<Entity> ents = p.getNearbyEntities(20, 20, 20);
                Location pl = p.getLocation();
                while (true) {
                    if (ents.isEmpty()) break;
                    Entity b = ents.get(0);
                    double l = pl.distance(b.getLocation());
                    for (Entity e : ents) {
                        double x = pl.distance(e.getLocation());
                        if (x < l) {
                            b = e;
                            l = x;
                        }
                    }
                    ents.remove(b);
                    former.addIfStarts("id" + b.getEntityId() + "(" + b.getType().getName() + ")");
                }
            }
            if (former.getQuery().startsWith("%")) {
                for (String s : caller.getVariables().keySet()) {
                    former.addIfStarts("%" + s);
                }
            } else if (former.getQuery().startsWith("@")) {
                for (OfflinePlayer f : Bukkit.getOfflinePlayers()) {
                    former.addIfStarts("@" + f.getName());
                }
            } else if (former.getQuery().startsWith("*")) {
                for (Player f : Bukkit.getOnlinePlayers()) {
                    former.addIfStarts("*" + f.getName());
                }
            }
            return;
        }
        if (word.equals("debug")) {
            former.addIfStarts("on", "off", "toggle");
            return;
        }
        NBTContainer container = null;
        boolean future = true;
        try {
            container = Argument.getContainer(caller, word, null);
        } catch (Throwable ignored) {
            future = false;
        }
        String val1 = word;
        word = former.poll(); // query or type or command
        if (container == null && !future) return;
        if (word.isEmpty()) {
            if (container == null) {
                if (val1.matches("#-?[0-9a-fA-F]+") || Argument.colors.containsKey(val1)) {
                    former.addIfStarts("int", "byte", "short", "long");
                } else if (val1.matches("-?[0-9]+(.[0-9]*)?")) {
                    former.addIfStarts("int", "byte", "short", "long", "float", "double");
                } else if (val1.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                    former.addIfStarts("byte[]", "int[]");
                }
            } else {
                completeTag(container, former);
                former.addIfStarts("remove");
                former.addIfStarts("copy", "=", "as", "view", "swap");
            }
            if (container instanceof NBTContainerVariable) former.addIfStarts("set");
            return;
        }
        NBTQuery query = null;
        XNBTBase base = null;
        if (!CommandNBT.specialTokens.contains(word)) {
            //* ~~~ word => query or type ~~~
            container = Argument.getContainer(caller, val1, word);
            query = new NBTQuery(word);
            word = former.poll(); // command;
        }
        try {
            base = container.getBase(query);
        } catch (Throwable ignored) {
        }
        if (word.isEmpty()) {
            if (container == null) {
                former.addIfStarts("=", "remove", "copy", "set", "as", "view", "swap");
            } else {
                if (container instanceof NBTContainerVariable) former.addIfStarts("set");
                if (base != null) former.addIfStarts("copy", "remove");
                former.addIfStarts("=", "as", "view", "swap");
            }
            return;
        }
        if (matches(word, "swap", "=", "set", "select")) {
            if (matches(word, "set", "select") && !(container instanceof NBTContainerVariable)) return;
            word = former.poll();
            if (word.isEmpty()) {
                if (base != null && former.getQuery().isEmpty()) {
                    NBTType type = NBTType.fromBase(base);
                    switch (type) {
                        case BYTE:
                        case SHORT:
                        case INT:
                        case LONG:
                        case FLOAT:
                        case DOUBLE:
                        case BYTEARRAY:
                        case INTARRAY:
                        case STRING:
                            String s = me.dpohvar.powernbt.command.action.Action.getNBTValue(base, null);
                            former.add(s);
                            return;
                    }
                }
                //todo: add $tmpfile completer
                former.addIfStarts("me", "item", "block", "buffer", "list", "compound", "byte[]", "int[]");
                if (caller.getOwner() instanceof Player && former.getQuery().startsWith("id")) {
                    Player p = (Player) caller.getOwner();
                    List<Entity> ents = p.getNearbyEntities(20, 20, 20);
                    Location pl = p.getLocation();
                    while (true) {
                        if (ents.isEmpty()) break;
                        Entity b = ents.get(0);
                        double l = pl.distance(b.getLocation());
                        for (Entity e : ents) {
                            double x = pl.distance(e.getLocation());
                            if (x < l) {
                                b = e;
                                l = x;
                            }
                        }
                        ents.remove(b);
                        former.addIfStarts("id" + b.getEntityId() + "(" + b.getType().getName() + ")");
                    }
                }
                if (former.getQuery().startsWith("%")) {
                    for (String s : caller.getVariables().keySet()) {
                        former.addIfStarts("%" + s);
                    }
                } else if (former.getQuery().startsWith("@")) {
                    for (OfflinePlayer f : Bukkit.getOfflinePlayers()) {
                        former.addIfStarts("@" + f.getName());
                    }
                } else if (former.getQuery().startsWith("*")) {
                    for (Player f : Bukkit.getOnlinePlayers()) {
                        former.addIfStarts("*" + f.getName());
                    }
                }
                return;
            }


            NBTContainer container2 = null;
            boolean future2 = true;
            try {
                container2 = Argument.getContainer(caller, word, null);
            } catch (Throwable ignored) {
                future2 = false;
            }
            String val2 = word;
            word = former.poll(); // query or type or command
            if (container2 == null && !future2) return;
            if (word.isEmpty()) {
                if (container2 == null) {
                    if (val2.matches("#-?[0-9a-fA-F]+") || Argument.colors.containsKey(val2)) {
                        former.addIfStarts("int", "byte", "short", "long");
                    } else if (val2.matches("-?[0-9]+(.[0-9]*)?")) {
                        former.addIfStarts("int", "byte", "short", "long", "float", "double");
                    } else if (val2.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                        former.addIfStarts("byte[]", "int[]");
                    }
                } else {
                    if (word.equals("this") || word.equals("self")) container2 = container;
                    completeTag(container2, former);
                }
            }
        }
    }


    private void completeTag(NBTContainer container, TabFormer former) throws Exception {
        String query = former.getQuery();
        String[] els = query.split("\\.|(?=\\[)");
        if (!query.endsWith(".") && (query.isEmpty() || els.length == 1)) {
            XNBTBase base = container.getRootBase();
            for (String type : container.getTypes()) {
                for (String s : completer.getNextKeys(type, emptyQuery)) {
                    former.addIfHas(s);
                }
            }
            if (base.getTypeId() == typeCompound) {
                for (String s : ((Map<String, Object>) base.getProxyField("map")).keySet()) {
                    former.addIfHas(s);
                }
            } else if (base.getTypeId() == typeList) {
                for (int i = 0; i < ((List<Object>) base.getProxyField("list")).size(); i++) {
                    former.addIfStarts("[" + i + "]");
                }
            } else if (base.getTypeId() == typeByteArray) {
                for (int i = 0; i < ((byte[]) base.getProxyField("data")).length; i++) {
                    former.addIfStarts("[" + i + "]");
                }
            } else if (base.getTypeId() == typeIntArray) {
                for (int i = 0; i < ((int[]) base.getProxyField("data")).length; i++) {
                    former.addIfStarts("[" + i + "]");
                }
            }
            return;
        }
        String qu = els[els.length - 1];
        String option = query.substring(0, query.length() - qu.length());
        if (query.endsWith(".")) {
            option = query.substring(0, query.length() - 1);
            qu = "";
        } else if (option.endsWith(".")) {
            option = option.substring(0, option.length() - 1);
        }
        NBTQuery q = new NBTQuery(option);
        for (String type : container.getTypes()) {
            for (String s : completer.getNextKeys(type, q)) {
                if (s.toUpperCase().startsWith(qu.toUpperCase())) {
                    if (!s.matches("\\[.*\\]")) s = "." + s;
                    former.add(option + s);
                }
            }
        }
        XNBTBase base = container.getBase(q);
        if (base == null) return;
        if (base.getTypeId() == typeCompound) {
            for (String s : ((Map<String, Object>) base.getProxyField("map")).keySet()) {
                if (s.toUpperCase().contains(qu.toUpperCase())) former.add(option + "." + s);
            }
        } else if (base.getTypeId() == typeList) {
            for (int i = 0; i < ((List<Object>) base.getProxyField("list")).size(); i++) {
                String s = "[" + i + "]";
                if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
            }
        } else if (base.getTypeId() == typeByteArray) {
            for (int i = 0; i < ((byte[]) base.getProxyField("data")).length; i++) {
                String s = "[" + i + "]";
                if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
            }
        } else if (base.getTypeId() == typeIntArray) {
            for (int i = 0; i < ((int[]) base.getProxyField("data")).length; i++) {
                String s = "[" + i + "]";
                if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
            }
        }
    }

}









