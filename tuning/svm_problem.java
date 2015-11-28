package tuning;
public class svm_problem implements java.io.Serializable
{
	public int l; //the number of training files
	public double[] y; //labels for each training file: 1 or -1?
	public svm_node[][] x;
}
