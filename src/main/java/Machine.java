import java.util.ArrayList;
import java.util.List;

public class Machine {
    String name;
    List<String> ports;
    List<Machine> links;

    public Machine(String name) {
        this.name = name;
        this.ports = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public void addPort(String port) {
        ports.add(port);
    }

    public void addLink(Machine machine) {
        links.add(machine);
    }

    @Override
    public String toString() {
        List<String> machineLinks = new ArrayList<>();
        links.forEach(m -> machineLinks.add(m.name));
        String linksAsString = String.join(",", machineLinks);
        return "Machine{" +
                "links=[" + linksAsString +
                "], name='" + name + '\'' +
                ", ports=" + ports +
                '}';
    }
}
