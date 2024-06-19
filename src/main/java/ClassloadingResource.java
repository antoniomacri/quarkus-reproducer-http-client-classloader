import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
@Path("/test")
public class ClassloadingResource {

    ManagedExecutor managedExecutor;

    HttpClient httpClient;


    @PostConstruct
    void createHttpClient() {
        this.managedExecutor = ManagedExecutor.builder().build();
        this.httpClient = HttpClient.newBuilder()
                .executor(managedExecutor)
                .version(HttpClient.Version.HTTP_2)
                .build();

        managedExecutor.supplyAsync(() -> "").thenApplyAsync(response -> {
            Log.infof("PostConstruct thread=%s", Thread.currentThread().hashCode());
            Log.infof("PostConstruct classLoaderAfter=%s", Thread.currentThread().getContextClassLoader());
            return Thread.currentThread().getContextClassLoader().getClass().getName();
        }, managedExecutor);
    }


    @GET
    @Produces({MediaType.TEXT_PLAIN})
    public CompletableFuture<String> test() {
        URI uri = URI.create("http://www.columbia.edu/~fdc/sample.html");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        Log.infof("classLoaderBefore=%s", Thread.currentThread().getContextClassLoader());
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApplyAsync(response -> {
            Log.infof("thread=%s", Thread.currentThread().hashCode());
            Log.infof("classLoaderAfter=%s", Thread.currentThread().getContextClassLoader());
            return Thread.currentThread().getContextClassLoader().getClass().getName();
        }, managedExecutor);
    }
}
