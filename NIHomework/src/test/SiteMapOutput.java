package test;

public interface SiteMapOutput {

	/**
	 * Write line with the given parameters to the output
	 */
	void write(Integer id, int parentId, int level, String url, int numLinks);

	/**
	 * Write line with the given parameters to the output
	 */
	void write(Integer id, int parentId, int level, String url);

	/**
	 * Close the output
	 */
	void close();
}