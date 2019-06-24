package net.mograsim.logic.ui.util;

public final class Version
{
	public final static Version jsonCompVersion = new Version(0, 1, 2);
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
		return major + "." + minor + "." + patch;
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
		return major != this.major;
	}

	public boolean is(int major, int minor)
	{
		return is(major) && this.minor == minor;
	}

	public boolean is(int major, int minor, int patch)
	{
		return is(major, minor) && this.patch == patch;
	}
}