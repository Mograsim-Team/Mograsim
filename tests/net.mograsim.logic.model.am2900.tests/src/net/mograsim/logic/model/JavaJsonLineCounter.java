package net.mograsim.logic.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JavaJsonLineCounter
{
	public static void main(String[] args) throws IOException
	{
		printLineCount("../..", "java", "bin", "classes", "SWTHelper");
		printLineCount("../..", "json", "bin", "classes", "SWTHelper");
	}

	private static void printLineCount(String path, String filetype, String... excludedDirectoryNames) throws IOException
	{
		Set<Path> excludedDirectoryPaths = Arrays.stream(excludedDirectoryNames).map(Paths::get).collect(Collectors.toSet());
		AtomicLong lineCount = new AtomicLong();
		AtomicLong byteSize = new AtomicLong();
		AtomicLong fileCount = new AtomicLong();
		Files.walk(Paths.get(path)).filter(Files::isRegularFile).filter(p -> p.toString().endsWith('.' + filetype))
				.filter(p -> !StreamSupport.stream(p.spliterator(), false).anyMatch(excludedDirectoryPaths::contains)).forEach(p ->
				{
					try
					{
						lineCount.addAndGet(Files.lines(p).count());
						byteSize.addAndGet(Files.size(p));
						fileCount.incrementAndGet();
					}
					catch (IOException e)
					{
						throw new UncheckedIOException(e);
					}
				});
		System.out.println(filetype + ": " + fileCount + " files; " + lineCount + " lines; " + byteSize + " bytes");
	}
}