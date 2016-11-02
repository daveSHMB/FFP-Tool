

/**
 * Interface for implementing divergence measures
 * 
 * @author David McLintock
 *
 */
public interface DivergenceMethod {

	/**
	 * Computes the divergence of two frequency vectors
	 * 
	 * @param p the first frequency vector
	 * @param q the second frequency vector
	 * @return the computed divergence
	 */
	public double computeDivergence(double[] p, double[] q);

}


