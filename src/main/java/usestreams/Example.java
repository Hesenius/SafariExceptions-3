package usestreams;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

interface ExFunction<A, R> {
  R apply(A a) throws Throwable;

  static <A, R> Function<A, Optional<R>> wrap(ExFunction<A, R> op) {
    return a -> {
      try {
        return Optional.of(op.apply(a));
      } catch (Throwable t) {
        return Optional.empty();
      }
    };
  }
}

public class Example {
//  public static Stream<String> getLines(String fn) {
//    try {
//      return Files.lines(Path.of(fn));
//    } catch (Throwable t) {
////      throw new RuntimeException(t); // breaks the calling Stream processor
//      t.printStackTrace();
//      return Stream.empty();
//    }
//  }

  public static Optional<Stream<String>> getLines(String fn) {
    try {
      return Optional.of(Files.lines(Path.of(fn)));
    } catch (Throwable t) {
      return Optional.empty();
    }
  }

  public static void main(String[] args) {
//    try {
//      List.of("a.txt", "b.txt", "c.txt").stream()
//          .flatMap(fn -> getLines(fn))
//          .forEach(System.out::println);
//    } catch (Throwable t) {
//      t.printStackTrace();
//    }
//    System.out.println("all done");

    List.of("a.txt", "b.txt", "c.txt").stream()
//        .flatMap(fn -> getLines(fn))
//        .map(fn -> getLines(fn))
        .map(ExFunction.wrap(fn -> Files.lines(Path.of(fn))))
        .peek(opt -> {
          if (opt.isEmpty()) {
            System.out.println("Ooops a file was not found!");
          }
        })
        .filter(opt -> opt.isPresent())
        .flatMap(opt -> opt.get())
        .forEach(System.out::println);

  }
}
