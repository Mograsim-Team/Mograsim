package net.mograsim.logic.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

public class JavaJsonLineCounter
{
	public static void main(String[] args) throws IOException
	{
		printLineCount("..", "java");
		printLineCount("..", "json");
	}

	private static void printLineCount(String path, String filetype) throws IOException
	{
		long lineCount = Files.walk(Paths.get(path)).filter(Files::isRegularFile).filter(p -> p.toString().endsWith('.' + filetype))
				.flatMap((Function<Path, Stream<String>>) p ->
				{
					try
					{
						return Files.lines(p);
					}
					catch (IOException e)
					{
						throw new UncheckedIOException(e);
					}
				}).count();
		System.out.println("Total lines in " + filetype + " files: " + lineCount);
	}
}