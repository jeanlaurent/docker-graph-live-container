import java.util.HashMap;
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

    public void forEach(Consumer<? super  Machine> action) {
        hosts.values().forEach(action);
    }

    public String toGraphviz() {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph dockerView {\n");
        builder.append("\trankdir=LR;\n");
        builder.append("\tsize=\"8,5\";\n");
        builder.append("\tnode [shape = circle];\n");
        forEach(m ->  m.links.forEach(link ->
                builder.append("\t\"" + m.name + "\" -> \"" + link.name + "\" [ label=\"" + link.ports.get(0) + "\" ];\n")
            )
        );
        builder.append("}\n");
        return builder.toString();
    }

}
