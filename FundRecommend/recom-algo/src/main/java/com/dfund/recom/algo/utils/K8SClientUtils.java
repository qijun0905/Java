package com.dfund.recom.algo.utils;

import com.google.common.io.ByteStreams;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.BatchV1beta1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 2020/12/2 5:59 下午
 *
 * @author Seldom
 */
public class K8SClientUtils {

    public static void createCronJob(String jobName,String namespace) {
        try {
            System.out.println("jobs comming...jobName is {}"+jobName+" && namespace is {}"+namespace);
            ApiClient client = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(client);
            BatchV1beta1Api batchV1beta1Api = new BatchV1beta1Api(client);
            V1beta1CronJob beta1CronJob = new V1beta1CronJob();
            beta1CronJob.setApiVersion("batch/v1beta1");
            beta1CronJob.setKind("CronJob");
            V1ObjectMeta metadata = new V1ObjectMeta();
            metadata.setName(jobName);
            beta1CronJob.setMetadata(metadata);
            V1beta1CronJobSpec spec = new V1beta1CronJobSpec();
            // start spec1
            spec.setSuccessfulJobsHistoryLimit(1);
            spec.setFailedJobsHistoryLimit(0);
            V1beta1JobTemplateSpec jobTemplate = new V1beta1JobTemplateSpec();
            spec.setSchedule("* */4 * * *");
            V1JobSpec v1JobSpec =new V1JobSpec();
            // start template
            V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();
            //start spec2
            V1PodSpec v1PodSpec =new V1PodSpec();
            v1PodSpec.setServiceAccount("fund-account");
            // start containers
            List<V1Container> containers = new ArrayList<>();
            V1Container container = new V1Container();
            container.setName("hello");
            container.setImage("harbor.vkdata.com/library/hello:v0.0.3");
            List<String> cmdList = new ArrayList<>();
            cmdList.add("python");
            cmdList.add("hello.py");
            container.setCommand(cmdList);
            containers.add(container);
            v1PodSpec.setContainers(containers);
            // end contains
            v1PodSpec.setRestartPolicy("Never");
            // end spec2
            v1PodTemplateSpec.setSpec(v1PodSpec);
            // end template
            v1JobSpec.setTemplate(v1PodTemplateSpec);
            jobTemplate.setSpec(v1JobSpec);
            // end spec1
            spec.setJobTemplate(jobTemplate);
            beta1CronJob.setSpec(spec);
            System.out.println("cronJod = "+beta1CronJob.toString());
            V1beta1CronJob namespacedCronJob = batchV1beta1Api.createNamespacedCronJob(namespace, beta1CronJob, null, null, null);
            System.out.println("namespacedCronJob = "+ namespacedCronJob.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void getLog() throws IOException, ApiException {
        ApiClient client = ClientBuilder.cluster().build();
        Configuration.setDefaultApiClient(client);
        CoreV1Api coreApi = new CoreV1Api(client);
        PodLogs logs = new PodLogs();

        List<V1Pod> items = coreApi
                .listNamespacedPod(
                        "fund", "false", null, null, null, null, null, null, null, null)
                .getItems();
        if(items.get(0) != null ){
            System.out.println("看这里："+items.size());
            V1Pod pod = items.get(0);

//            InputStream is = logs.streamNamespacedPodLog(pod);
//            ByteStreams.copy(is, System.out);
        }


    }



    public static void createJob(String jobName,String namespace,String image) throws Exception{
        ApiClient client = ClientBuilder.cluster().build();
        Configuration.setDefaultApiClient(client);
        BatchV1Api batchV1Api = new BatchV1Api(client);
        V1Job v1Job = new V1Job();
        v1Job.setApiVersion("batch/v1");
        v1Job.setKind("Job");
        //设置metadata
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(jobName);
        v1Job.setMetadata(metadata);
        //v1JobSpec
        V1JobSpec v1JobSpec = new V1JobSpec();
        v1JobSpec.setTtlSecondsAfterFinished(80);
        //template
        V1PodTemplateSpec template = new V1PodTemplateSpec();
        V1ObjectMeta metadataTemp = new V1ObjectMeta();
        metadataTemp.setName(jobName);
        template.setMetadata(metadataTemp);
        V1PodSpec spec = new V1PodSpec();
        spec.setServiceAccount("fund-account");
        //containers
        List<V1Container> containers = new ArrayList<>();
        V1Container container = new V1Container();
        container.setName(jobName);
        container.setImage(image);
        List<String> cmdList = new ArrayList<>();
        cmdList.add("python");
        cmdList.add("hello.py");
        container.setCommand(cmdList);
        containers.add(container);
        spec.setContainers(containers);
        spec.setRestartPolicy("Never");
        template.setSpec(spec);
        //template
        v1JobSpec.setTemplate(template);
        //spec
        v1Job.setSpec(v1JobSpec);
        //创建Job
        V1Job job = batchV1Api.createNamespacedJob(namespace, v1Job, "false", null, null);
        System.out.println(job.toString());
//        V1PodStatus

    }


}
