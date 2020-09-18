package client;

import com.redhat.example.Customer;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * EmbedMain
 */
public class EmbedMain {

    private KieContainer kieContainer; 

    public static void main(String[] args) {
        EmbedMain run = new EmbedMain();
        run.evaluateRules();
    }

    private void evaluateRules() {
        // get kie container
        kieContainer = getContainer();

        // start the stopwatch
        long start = System.currentTimeMillis();

        KieSession ksession = kieContainer.newKieSession("dbKieSession");

        Customer customer1 = new Customer(1L, 19);
        Customer customer2 = new Customer(2L, 27);
        Customer customer3 = new Customer(3L, 32);
        Customer customer4 = new Customer(4L, 60);

        ksession.insert(customer1);
        ksession.insert(customer2);
        ksession.insert(customer3);
        ksession.insert(customer4);

        int fired = ksession.fireAllRules();
        
        // stop watch
        long end = System.currentTimeMillis();

        // print the result
        System.out.format("fired rules: %d elapsed time: %d \n", fired, (end - start));

        if (ksession != null) {
            ksession.getFactHandles().forEach(f -> System.out.println(ksession.getObject(f)));
            ksession.dispose();
        }
    }

    private static KieContainer getContainer() {
        KieServices kieServices = KieServices.Factory.get();

        // Retrieve the decision project (kjar) from classpath
        // KieContainer kieContainer = kieServices.getKieClasspathContainer();

        // Dynamically pull the decision project from Maven repo
        ReleaseId releaseId = kieServices.newReleaseId("com.redhat.example","rule-template", "1.0.0-SNAPSHOT");
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        return kieContainer;
    }
}
