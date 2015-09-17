import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws DockerCertificateException, DockerException, InterruptedException, IOException {
        final DockerClient docker = DefaultDockerClient.fromEnv().build();
        final List<ContainerInfo> infos = new ArrayList<>();
        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers(false));
        containers.forEach(c -> {
            try {
                infos.add(docker.inspectContainer(c.id()));
            } catch (DockerException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Machines machines = new Machines();
        Map<String, String> links = new HashMap<>();
        infos.forEach(info -> {
            Machine machine = new Machine(info.name());
            if (info.hostConfig().links() != null) {
                info.hostConfig().links().forEach(link -> links.put(info.name(), link));
            }
            if (info.hostConfig().portBindings() != null) {
                info.hostConfig().portBindings().keySet().forEach(machine::addPort);
            }
            machines.add(machine);
        });
        docker.close();
        // analyse links
        links.forEach((key, value) -> {
            int index = value.indexOf(":");
            if (index != -1) {
                String linkedTo = value.substring(0, index);
                Machine destinationMachine = machines.get(linkedTo);
                machines.get(key).addLink(destinationMachine);
            }
        });

        System.out.println(machines.toGraphviz(docker.getHost()));

        System.out.println("Outputing output.dot");
        Files.write(machines.toGraphviz(docker.getHost()), new File("output.dot"), Charsets.UTF_8);
        System.out.println("Outputing output.png");
        Runtime.getRuntime().exec(new String[]{"dot", "-Tpng", "-ooutput.png", "output.dot"});
    }


}
