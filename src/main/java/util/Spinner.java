package util;



public class Spinner implements Runnable {

    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        String[] spin = {"|", "/", "-", "\\"};
        int i = 0;

        while (running) {
            System.out.print("\r⏳ Loading " + spin[i++ % spin.length]);
            try {
                Thread.sleep(120);
            } catch (Exception ignored) {}
        }

        System.out.print("\r");
    }
}