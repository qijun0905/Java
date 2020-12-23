package com.dfundata.fund.schedule.web;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.BatchV1beta1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 2020/11/5 3:11 下午
 *
 * @author Seldom
 */
@Controller
public class K8sClientController {

    @RequestMapping("/delJob")
    @ResponseBody
    String delJobs(String jobName,String namespace) {
        try{
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
            spec.setSchedule("* * * * *");
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

        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return "hello";
    }



    @RequestMapping("/sinCot")
    @ResponseBody
    String homeTest(String jobName,String namespace) {
        try{
            System.out.println("jobs comming...jobName is {}"+jobName+" && namespace is {}"+namespace);
            ApiClient client = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(client);
            CoreV1Api coreV1Api = new CoreV1Api(client);
            AppsV1Api appsV1Api = new AppsV1Api(client);
            BatchV1Api batchV1Api = new BatchV1Api(client);

            boolean flag = findJob(jobName, namespace);
            System.out.println("flag is {}"+flag);
            if(flag){
                System.out.println("here");
                try {
                    deleteJobIfExist(batchV1Api,jobName,namespace);
                }catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("deleteJobIfExist Exception : "+e.getMessage());
                }
            }
            V1Pod pod = findPod(jobName, namespace);
            System.out.println("pod : "+pod);
            if(pod != null){
                try{
                    deletePodIfExist(coreV1Api,jobName,namespace);
                }catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("sleeping ");
                    Thread.sleep(30*1000);
                    System.out.println("deletePodIfExist Exception : "+e.getMessage());
                }
            }
            System.out.println("delete pass");
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
            container.setName("hello");
            container.setImage("harbor.vkdata.com/library/hello:v0.0.3");
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
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return "hello";
    }

    public  V1Pod findPod(String podName,String namespace) {
        System.out.println("findPod : podName = "+podName+" namespace = "+namespace);
        try
        {
            ApiClient client = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();
            V1PodList listNamespacedPod =
                    api.listNamespacedPod(
                            namespace,
                            null,
                            null,
                            null,
                            null,
                            null,
                            Integer.MAX_VALUE,
                            null,
                            180,
                            Boolean.FALSE);
            int size = listNamespacedPod.getItems().size();
            System.out.println("size == "+size);
            for(V1Pod item:listNamespacedPod.getItems()){
                V1PodSpec spec = item.getSpec();
                List<V1Container> containers = spec.getContainers();
                if(containers.size() > 0){
                    for(V1Container container:containers){
                        String name = container.getName();
                        if(StringUtils.isNotBlank(name) && podName.equals(name)){
                            System.out.println("pod .... ~~~~~ : "+item.toString());
                            System.out.println("container is "+name+" and podName is "+podName);
                            return item;
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
//            ex.printStackTrace();
            System.out.println("findPod : "+ ex.getMessage());
        }
        return null;
    }

    public  boolean findJob(String jobName,String namespace) {
        try
        {
            ApiClient client = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(client);
            BatchV1Api batchV1Api = new BatchV1Api(client);
            V1JobList jobList = batchV1Api.listNamespacedJob(namespace, null, null,
                    null, null, null, Integer.MAX_VALUE,
                    null, 180, Boolean.FALSE);

            for(V1Job item:jobList.getItems()){
                V1JobSpec spec = item.getSpec();
                V1PodTemplateSpec template = spec.getTemplate();
                List<V1Container> containers = template.getSpec().getContainers();
                if(containers.size() > 0){
                    for(V1Container container:containers){
                        String name = container.getName();
                        if(StringUtils.isNotBlank(name) && jobName.equals(name)){
                            return true;
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
//            ex.printStackTrace();
            System.out.println("findjod : "+ ex.getMessage());
        }

        return false;
    }


    @RequestMapping("/tan")
    @ResponseBody
    String deploy(){
        try {
            ApiClient client = ClientBuilder.cluster().build();

            Configuration.setDefaultApiClient(client);


            AppsV1Api appsV1Api;
            appsV1Api = new AppsV1Api(client);

            //配置标签
            Map<String, String> matchLabels = new HashMap<>();
            matchLabels.put("app", "fund-hello");
//        matchLabels.put("env", env);

            //配置端口
            List<V1ContainerPort> portList = new ArrayList<>();
            V1ContainerPort port = new V1ContainerPort();
            port.setName("httpd");
            port.setContainerPort(80);
            portList.add(port);

            List<V1EnvVar> envs = new LinkedList<>();
//        V1EnvVar clientIdEnv = new V1EnvVar();
//        clientIdEnv.setName("client_id");
//        clientIdEnv.setValue(clientId);
//        envs.add(clientIdEnv);
//        V1EnvVar clientSecretEnv = new V1EnvVar();
//        clientSecretEnv.setName("client_secret");
//        clientSecretEnv.setValue(clientSecret);
//        envs.add(clientSecretEnv);
//        V1EnvVar redirectEnv = new V1EnvVar();
//        redirectEnv.setName("redirect_uri");
//        redirectEnv.setValue(redirectUri);
//        envs.add(redirectEnv);
            List<String> cmdList = new ArrayList<>();
            cmdList.add("'python'");
            cmdList.add("'hello.py'");

            //使用对象封装Deployment
            V1Deployment deploy =
                    new V1DeploymentBuilder()
                            .withApiVersion("batch/v1")
                            .withKind("Job")
                            .withNewMetadata()
                            .withName("fund-hello")
                            .endMetadata()
                            .withNewSpec()
//                        .withReplicas(1)
//                        .withNewSelector()
//                        .withMatchLabels(matchLabels)
//                        .endSelector()
                            .withNewTemplate()
                            .withNewMetadata()
//                        .withLabels(matchLabels)
                            .withName("fund-hello")
                            .endMetadata()
                            .withNewSpec()
                            .withServiceAccount("fund-account")
                            .withContainers(
                                    new V1Container()
                                            .name("hello")
                                            .image("harbor.vkdata.com/library/hello:v0.0.3")
//                                        .imagePullPolicy("IfNotPresent")
//                                        .ports(portList)
//                                        .env(envs)
                                            .command(cmdList)
                            )
                            .withRestartPolicy("Never")
                            .endSpec()
                            .endTemplate()
                            .endSpec()
                            .build();
            System.out.println("运行到这了"+deploy);
            //调用容器接口创建Deployment
            appsV1Api.createNamespacedDeployment(
                    "fund",	//namespace
                    deploy,		//Deployment对象
                    "true",		//pretty
                    null,		//dryRun
                    null);		//fieldManager
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return "succeed";
    }

    @RequestMapping("/cot")
    @ResponseBody
    String hhFunc(String dep,String type){
        try {
            String namespaceStr = "fund";
            // 配置client
            ApiClient client = ClientBuilder.cluster().build();

            Configuration.setDefaultApiClient(client);
            CoreV1Api coreV1Api = new CoreV1Api(client);
            AppsV1Api appsV1Api = new AppsV1Api(client);
            if("deploy".equals(type)){
                deleteDeploymentByYaml(appsV1Api,namespaceStr,dep);
            }else if("pod".equals(type)){
                deletePodIfExist(coreV1Api,dep,namespaceStr);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(" fail "+e.getMessage());
        }
        return "success";
    }


    /**
     * 删除对应的deployment
     * @param api
     * @param namespaceStr
     */
    private static void deleteDeploymentByYaml(AppsV1Api api, String namespaceStr,String deploymentName) throws Exception {
        // 传入deployment的名字，命名空间，就可以删除deployment以及所有的pod了
        V1Status v1Status = api.deleteNamespacedDeployment(deploymentName,
                namespaceStr,null,null,null,null,null,null);
        System.out.println(v1Status.getCode()+"删除完毕");
    }

    private  void deleteJobIfExist(BatchV1Api batchV1Api,String jobName,String namespaceStr) throws Exception {
        System.out.println("hello deleteJobIfExist... jobName is " + jobName + " && namespace is " + namespaceStr);
        V1DeleteOptions body = new V1DeleteOptions();
        body.setApiVersion("");
        body.setKind("");
        V1Status v1Status = batchV1Api.deleteNamespacedJob(jobName,
                namespaceStr, null, null, null, null, null, body);
        System.out.println(v1Status.getCode()+"删除完毕");
    }

    private void deletePodIfExist(CoreV1Api coreV1Api,String podName,String namespace) throws Exception{
        System.out.println("hello deletePodIfExist... podName is " + podName + " && namespace is " + namespace);
        V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
        v1DeleteOptions.setApiVersion(null);
        v1DeleteOptions.setKind(null);
        V1Status v1Status = coreV1Api.deleteNamespacedPod(podName, namespace, null, null, null,
                null, null,v1DeleteOptions );
        System.out.println(v1Status.getCode()+"删除完毕");
    }

}
