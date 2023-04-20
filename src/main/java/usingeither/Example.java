package usingeither;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

interface ExFunction<A, R> {
  R apply(A a) throws Throwable;

  static <A, R> Function<A, Either<R, Throwable>> wrap(ExFunction<A, R> op) {
    return a -> {
      try {
        return Either.success(op.apply(a));
      } catch (Throwable t) {
        return Either.failure(t);
      }
    };
  }
}

class Either<S, F> {
  private S success;
  private F failure;

  private Either(S success, F failure) {
    this.success = success;
    this.failure = failure;
  }

  public static <S, F> Either<S, F> success(S s) {
    return new Either<>(s, null);
  }

  public static <S, F> Either<S, F> failure(F f) {
    return new Either<>(null, f);
  }

  public boolean isSuccess() {
    return failure == null;
  }

  public boolean isFailure() {
    return failure != null;
  }

  public S getSuccess() {
    if (isSuccess()) {
      return success;
    } else {
      throw new IllegalStateException("attempt to get success from a failure");
    }
  }

  public F getFailure() {
    if (isFailure()) {
      return failure;
    } else {
      throw new IllegalStateException("attempt to get failure from a success");
    }
  }

  public void ifFailure(Consumer<F> op) {
    if (isFailure()) {
      op.accept(failure);
    }
  }

//  public Either<S, F> recover(Function<F, Either<S, F>> op) {
//    if (isFailure()) {
//      return op.apply(failure);
//    } else {
//      return this;
//    }
//  }

  public Either<S, F> map(UnaryOperator<Either<S, F>> op) {
    return op.apply(this);
  }

  public Either<S, F> recover(Function<Either<S, F>, Either<S, F>> op) {
    if (isFailure()) {
      System.out.println("*** recovering");
      return op.apply(this);
    } else {
      return this;
    }
  }

  public static <S, F> Function<Either<S, F>, Either<S, F>> recoveries(
      Function<Either<S, F>, Either<S, F>> ... op) {

    return e -> {
      for (var funct : op) {
        if (e.isSuccess()) break;
        e = funct.apply(e);
      }
      return e;
    };
  }

  public static <S, F> Function<Either<S, F>, Either<S, F>> iterate(
      Function<Either<S, F>, Either<S, F>> op, int count) {
    return e -> {
      int c = count;
      while (e.isFailure() && c-- > 0 ){
        e = op.apply(e);
      }
      return e;
    };
  }

    @Override
  public String toString() {
    return "Either{" +
        "success=" + success +
        ", failure=" + failure +
        '}';
  }
}

public class Example {
  public static void main(String[] args) {
    Function<Either<Stream<String>, Throwable>,
        Either<Stream<String>, Throwable>> delay =
        e -> {
          try {
            System.out.println("delaying...");
            Thread.sleep(1_000);
          } catch (InterruptedException ie) {
            System.out.println("Hmm, interrupted??");
          }
          return e;
        };

    Function<Either<Stream<String>, Throwable>,
        Either<Stream<String>, Throwable>> fallback =
        ExFunction.wrap(e -> Files.lines(Path.of("d.txt")));

    Function<Either<Stream<String>, Throwable>,
        Either<Stream<String>, Throwable>> retry =
        ExFunction.wrap(e -> {
          String fn = e.getFailure().getMessage();
          System.out.println("*** trying " + fn + " again");
          return Files.lines(Path.of(fn));
        });

    List.of("a.txt", "b.txt", "c.txt").stream()
        .map(ExFunction.wrap(fn -> Files.lines(Path.of(fn))))
        .peek(e -> e.ifFailure(
            x -> System.out.println("uh oh, problem with " + x.getMessage())))
//        .map(e -> e.recover(delay))
//        .map(e -> e.recover(retry))
//        .map(e -> e.recover(fallback))
        .map(Either.recoveries(
            Either.iterate(
                Either.recoveries(delay, retry), 2),
            fallback))
        .filter(Either::isSuccess)
        .flatMap(Either::getSuccess)
        .forEach(System.out::println);
    ;
  }
}
