package org.lumi.dockerjava;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import org.apache.commons.io.FileUtils;

import java.util.List;

import static com.github.dockerjava.api.model.Capability.SYS_ADMIN;

/**
 * Created by John Tsantilis
 * (i [dot] tsantilis [at] yahoo [dot] com A.K.A lumi) on 3/2/2017.
 */

public class DockerJavaDriverApp {
    public static void main(String[] args) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://195.46.17.228:2375")
                .withDockerTlsVerify(false)
                //.withDockerCertPath("/home/user/.docker")
                //.withRegistryUrl("https://index.docker.io/v2/")
                //.withRegistryUsername("giannis20012001")
                //.withRegistryPassword("Floor53*")
                //.withRegistryEmail("i.tsantilis@yahoo.com")
                .withApiVersion("1.25")
                .build();

        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
                .withReadTimeout(1000)
                .withConnectTimeout(1000)
                .withMaxTotalConnections(100)
                .withMaxPerRouteConnections(10);

        DockerClient dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();

        //Search Docker repository
        List<SearchItem> dockerSearch = dockerClient.searchImagesCmd("giannis20012001/phpdashboard").exec();
        System.out.println("Search returned" + dockerSearch.toString());

        //
        ExposedPort tcp = ExposedPort.tcp(80);
        Ports portBindings = new Ports();
        System.out.println(tcp.toString());
        portBindings.bind(tcp, new Ports.Binding("0.0.0.0", "80"));

        final String testVariable = "dbhost 172.17.0.1";
        HostConfig hostConfig = new HostConfig().withPrivileged(true);
        CreateContainerResponse container = dockerClient.createContainerCmd("giannis20012001/phpdashboard")
                .withExposedPorts(tcp)
                .withPortBindings(portBindings)
                .withName("phpdashboard")
                .withPublishAllPorts(true)
                .withHostConfig(hostConfig)
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();

    }

}
