package net.mograsim.logic.model.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.mograsim.logic.model.util.Version.VersionJSONAdapter;

@JsonAdapter(VersionJSONAdapter.class)
public final class Version implements Comparable<Version>
{
	public final int major, minor, patch;

	public Version(int major, int minor, int patch)
	{
		super();
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public int[] getVersionNumbers()
	{
		return new int[] { major, minor, patch };
	}

	@Override
	public String toString()
	{
		return toSemverString();
	}

	public String toSemverString()
	{
		return major + "." + minor + "." + patch;
	}

	public static Version parseSemver(String semver)
	{
		String[] semverParts = semver.split("\\.");
		return new Version(Integer.parseInt(semverParts[0]), semverParts.length > 1 ? Integer.parseInt(semverParts[1]) : 0,
				semverParts.length > 2 ? Integer.parseInt(semverParts[2]) : 0);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + minor;
		result = prime * result + patch;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof Version))
			return false;
		Version other = (Version) obj;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		if (patch != other.patch)
			return false;
		return true;
	}

	public boolean is(int major)
	{
		return major == this.major;
	}

	public boolean is(int major, int minor)
	{
		return is(major) && this.minor == minor;
	}

	public boolean is(int major, int minor, int patch)
	{
		return is(major, minor) && this.patch == patch;
	}

	/**
	 * Compares this {@link Version} with the specified version.<br>
	 * As required by {@link Comparable#compareTo(Object)}, returns a negative integer, zero, or a positive integer as this version is less
	 * (earlier) than, equal to, or greater (later) than the specified version.
	 * <p>
	 * If the versions are equal ({@link #major}, {@link #minor}, {@link #patch} are the same), returns 0. <br>
	 * If they differ in {@link #patch}, but neither {@link #major} or {@link #minor} , returns +-1. <br>
	 * If they differ in {@link #minor}, but not {@link #major}, returns +-2.<br>
	 * If they differ in {@link #major}, returns +-3.
	 */
	@Override
	public int compareTo(Version o)
	{
		if (major != o.major)
		{
			if (major > o.major)
				return 3;
			return -3;
		}
		if (minor != o.minor)
		{
			if (minor > o.minor)
				return 2;
			return -2;
		}
		if (patch != o.patch)
		{
			if (patch > o.patch)
				return 1;
			return -1;
		}
		return 0;
	}

	static class VersionJSONAdapter extends TypeAdapter<Version>
	{
		@Override
		public void write(JsonWriter out, Version value) throws IOException
		{
			out.value(value.toSemverString());
		}

		@Override
		public Version read(JsonReader in) throws IOException
		{
			return parseSemver(in.nextString());
		}
	}
}