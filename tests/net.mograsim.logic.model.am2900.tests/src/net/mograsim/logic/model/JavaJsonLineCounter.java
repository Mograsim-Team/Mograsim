package net.mograsim.logic.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

public class JavaJsonLineCounter
{
	public static void main(String[] args) throws IOException
	{
		printLineCount("..\\..", "java");
		printLineCount("..\\..", "json");
	}

	private static void printLineCount(String path, String filetype) throws IOException
	{
		AtomicLong lineCount = new AtomicLong();
		AtomicLong byteSize = new AtomicLong();
		AtomicLong fileCount = new AtomicLong();
		Files.walk(Paths.get(path)).filter(Files::isRegularFile).filter(p -> p.toString().endsWith('.' + filetype))
				.filter(p -> !StreamSupport.stream(p.spliterator(), false).anyMatch(Paths.get("bin")::equals))
				.filter(p -> !StreamSupport.stream(p.spliterator(), false).anyMatch(Paths.get("classes")::equals)).forEach(p ->
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