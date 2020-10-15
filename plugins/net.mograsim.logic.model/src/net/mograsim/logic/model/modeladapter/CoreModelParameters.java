package net.mograsim.logic.model.modeladapter;

public class CoreModelParameters
{
	public final int wireTravelTime;
	public final int gateProcessTime;
	public final int hardcodedComponentProcessTime;

	public CoreModelParameters(int wireTravelTime, int gateProcessTime, int hardcodedComponentProcessTime)
	{
		this.wireTravelTime = wireTravelTime;
		this.gateProcessTime = gateProcessTime;
		this.hardcodedComponentProcessTime = hardcodedComponentProcessTime;
	}

	private CoreModelParameters(CoreModelParametersBuilder builder)
	{
		this.wireTravelTime = builder.wireTravelTime;
		this.gateProcessTime = builder.gateProcessTime;
		this.hardcodedComponentProcessTime = builder.hardcodedComponentProcessTime;
	}

	public static CoreModelParametersBuilder builder()
	{
		return new CoreModelParametersBuilder();
	}

	public static class CoreModelParametersBuilder
	{
		public int wireTravelTime;
		public int gateProcessTime;
		public int hardcodedComponentProcessTime;

		private CoreModelParametersBuilder()
		{
		}

		public CoreModelParametersBuilder wireTravelTime(int wireTravelTime)
		{
			this.wireTravelTime = wireTravelTime;
			return this;
		}

		public CoreModelParametersBuilder gateProcessTime(int gateProcessTime)
		{
			this.gateProcessTime = gateProcessTime;
			return this;
		}

		public CoreModelParametersBuilder hardcodedComponentProcessTime(int hardcodedComponentProcessTime)
		{
			this.hardcodedComponentProcessTime = hardcodedComponentProcessTime;
			return this;
		}

		public CoreModelParameters build()
		{
			return new CoreModelParameters(this);
		}
	}
}