package org.lumi.dockerjava;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;

/**
 * Created by John Tsantilis
 * (i [dot] tsantilis [at] yahoo [dot] com A.K.A lumi) on 3/2/2017.
 */

public class DockerJavaDriverApp {
    public static void main(String[] args) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://195.46.17.228:2375")
                .withDockerTlsVerify(false)
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

        //
        ExposedPort tcp = ExposedPort.tcp(80);
        Ports portBindings = new Ports();
        portBindings.bind(tcp, new Ports.Binding("0.0.0.0", "80"));

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
