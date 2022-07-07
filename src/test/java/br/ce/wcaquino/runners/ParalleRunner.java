package br.ce.wcaquino.runners;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParalleRunner extends BlockJUnit4ClassRunner {

    public ParalleRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        setScheduler(new ThreadPoll());
    }

    private static class ThreadPoll implements RunnerScheduler {
        private ExecutorService executar;

        public ThreadPoll() {
            executar = Executors.newFixedThreadPool(2);
        }

        @Override
        public void schedule(Runnable run) {
            executar.submit(run);
        }

        @Override
        public void finished() {
            executar.shutdown();
            try {
                executar.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
