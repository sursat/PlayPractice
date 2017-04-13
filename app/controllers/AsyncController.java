package controllers;

import akka.actor.ActorSystem;
import javax.inject.*;
import play.*;
import play.libs.Json;
import play.mvc.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import scala.concurrent.duration.Duration;
import scala.concurrent.ExecutionContextExecutor;

/**
 * This controller contains an action that demonstrates how to write
 * simple asynchronous code in a controller. It uses a timer to
 * asynchronously delay sending a response for 1 second.
 *
 * @param actorSystem We need the {@link ActorSystem}'s
 * {@link Scheduler} to run code after a delay.
 * @param exec We need a Java {@link Executor} to apply the result
 * of the {@link CompletableFuture} and a Scala
 * {@link ExecutionContext} so we can use the Akka {@link Scheduler}.
 * An {@link ExecutionContextExecutor} implements both interfaces.
 */
@Singleton
public class AsyncController extends Controller {

    private final ActorSystem actorSystem;
    private final ExecutionContextExecutor exec;

    @Inject
    public AsyncController(ActorSystem actorSystem, ExecutionContextExecutor exec) {
      this.actorSystem = actorSystem;
      this.exec = exec;
    }

    /**
     * An action that returns a plain text message after a delay
     * of 1 second.
     *
     * The configuration in the <code>routes</code> file means that this method
     * will be called when the application receives a <code>GET</code> request with
     * a path of <code>/message</code>.
     */
    public CompletionStage<Result> message() {
        return getFutureMessage(15, TimeUnit.SECONDS).thenApplyAsync(Results::ok, exec);
    }

    private CompletionStage<String> getFutureMessage(long time, TimeUnit timeUnit) {
        CompletableFuture<String> future = new CompletableFuture<>();
        actorSystem.scheduler().scheduleOnce(
            Duration.create(time, timeUnit),
            () -> future.complete("Hi!"),
            exec
        );
        return future;
    }

    public CompletionStage<Result> getMe() throws InterruptedException {
        return testMe().thenApplyAsync(Json::toJson).thenApplyAsync(Results::ok);
    }

    private CompletionStage<List> testMe() throws InterruptedException {
        CompletableFuture<List> completableFuture = new CompletableFuture<>();
        List<Integer> helpers = new LinkedList<>();
        helpers.add(1);
        helpers.add(5);
        helpers.add(6);
        helpers = helpers.stream().filter(s -> s%2==1).collect(Collectors.toList());
        Thread.sleep(5000);
        completableFuture.complete(helpers);
        return completableFuture;
    }

}
