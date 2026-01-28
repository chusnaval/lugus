package lugus.exception;

public class LugusNotFoundException extends RuntimeException {

	/**
	 * Default Constructor
	 * 
	 * @param id
	 */
	public LugusNotFoundException(Integer id) {
		super("Resource not found {}" + id);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3688611888672321816L;

}
