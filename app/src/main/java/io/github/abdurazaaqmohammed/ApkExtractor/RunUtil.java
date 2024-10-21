package io.github.abdurazaaqmohammed.ApkExtractor;

import android.os.Handler;
import android.widget.Toast;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RunUtil {

    private final Handler handler;
    private final MainActivity context;

    public RunUtil(Handler handler, MainActivity context) {
        this.handler = handler;
        this.context = context;
    }

    public void runInBackground(Callable<Boolean> callable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(callable);

        executor.submit(() -> {
            try {
                Boolean fail = future.get(); // passed errorOccurred

                if (!fail)
                    handler.post(() -> Toast.makeText(context, MainActivity.rss.getString(R.string.success_saved, "").replace("to", "").replace(':', ' '), Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                context.showError(e);
            } finally {
                MainActivity.errorOccurred = false;
            }
        });
    }
}