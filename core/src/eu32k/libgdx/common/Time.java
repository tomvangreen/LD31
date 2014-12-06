package eu32k.libgdx.common;

public class Time {
	private static final long time = System.currentTimeMillis();

	public static float getTime() {
		long diff = System.currentTimeMillis() - time;
		double d = diff / 1000.0;
		return (float) d;
	}
}