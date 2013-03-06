
public class PreventDefaultException extends Exception {

	private static final long serialVersionUID = -7902829951046680112L;
	
	public PreventDefaultException() { super(); }
	public PreventDefaultException(String message) { super(message); }
	public PreventDefaultException(String message, Throwable cause) { super(message, cause); }
	public PreventDefaultException(Throwable cause) { super(cause); }
}
