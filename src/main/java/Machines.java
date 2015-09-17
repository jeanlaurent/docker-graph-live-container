import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Machines {
    Map<String, Machine> hosts;

    public Machines() {
        hosts = new HashMap<>();
    }

    public void add(Machine machine) {
        hosts.put(machine.name, machine);
    }

    public Machine get(String name) {
        return hosts.get(name);
    }

    public void forEach(Consumer<? super Machine> action) {
        hosts.values().forEach(action);
    }

    public String toGraphviz(String host) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph dockerView {\n");
        builder.append("\trankdir=LR;\n");
        builder.append("\tsize=\"8,5\";\n");
        builder.append("\tnode [shape = circle];\n");
        builder.append("\tsubgraph clusterA {\n");
        builder.append("\t\tlabel=\"" + host + "\"\n");
        List<String> visited = new ArrayList<>();
        forEach(m -> m.links.forEach(link -> {
                    visited.add(m.name);
                    visited.add(link.name);
                    builder.append("\t\t\"" + m.name + "\" -> \"" + link.name + "\" [ label=\"" + link.ports.get(0) + "\" ];\n");
                })
        );
        forEach(m -> {
            if (!visited.contains(m.name)) {
                builder.append("\t\t\"" + m.name + "\"\n");
            }
        });
        builder.append("\t}\n");
        builder.append("}\n");
        return builder.toString();
    }

}
