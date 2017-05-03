package test;

public interface SiteMapOutput {

	/**
	 * Write the given string to the file
	 * @param id
	 * @param parentId
	 * @param level
	 * @param url
	 * @param numLinks
	 */
	void write(Integer id, int parentId, int level, String url, int numLinks);

	/**
	 * Write the given string to the file
	 * @param id
	 * @param parentId
	 * @param level
	 * @param url
	 */
	void write(Integer id, int parentId, int level, String url);

	/**
	 *  Close the opened <code>OutputStreamWriter</code> 
	 */
	void close();

	

}