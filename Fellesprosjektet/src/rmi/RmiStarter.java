package rmi;

public abstract class RmiStarter {

    /**
     *
     * @param classToAddToServerCodebase a class that should be in the java.rmi.server.codebase property.
     */
    public RmiStarter(@SuppressWarnings("rawtypes") Class classToAddToServerCodebase) {

        System.setProperty("java.rmi.server.codebase", classToAddToServerCodebase
            .getProtectionDomain().getCodeSource().getLocation().toString());

        System.setProperty("java.security.policy", PolicyLocator.getLocationOfPolicyFile());

        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        doCustomRmiHandling();
    }

    /**
     * extend this class and do RMI handling here
     */
    public abstract void doCustomRmiHandling();

}