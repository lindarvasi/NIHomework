package test;

public interface SiteMapOutput {

	/**
	 * Write with the given parameters to the output
	 */
	void write(Integer id, int parentId, int level, String url, int numLinks);

	/**
	 * Write with the given parameters to the output
	 */
	void write(Integer id, int parentId, int level, String url);

	/**
	 * Close the output
	 */
	void close();
}