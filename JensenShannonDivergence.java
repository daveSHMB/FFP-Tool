
/**
 * Class computing the Jensen Shannon Divergence of two frequency lists
 * @author David McLintock
 */
public class JensenShannonDivergence implements DivergenceMethod {

	/**
	 * Default constructor for JSD object
	 */
	public JensenShannonDivergence(){

	}

	
	/**
	 * Computes the JSD of two frequency vectors
	 * @param p the first frequency vector
	 * @param q the second frequency vector
	 * @return the result of the JSD calculation
	 */
	public double computeDivergence(double[] p, double[] q){
		
		//normalise vectors
		p = normalise(p);
		q = normalise(q);

		double[] m = new double[p.length];

		for(int i = 0; i < m.length; i++){
			m[i] = (p[i] + q[i]) / 2;
		}

		double jsd = 0.5 * (kullbackLeiblerDivergence(p, m) + kullbackLeiblerDivergence(q, m)); 
		return jsd;
	}

	
	/**
	 * Computes the Kullback Leibler divergence of two frequency vectors
	 * @param p the first frequency vector
	 * @param m the second frequency vector
	 * @return the result of the KLD calculation
	 */
	public double kullbackLeiblerDivergence(double[] p, double[] m){

		double kld = 0.0;
		for (int i = 0; i < p.length; ++i) {
			
			//skip zero values
			if (p[i] == 0.0 || m[i] == 0.0) {
				continue; 
			}

			kld += p[i] * Math.log( p[i] / m[i] );
		}
		
		kld /= Math.log(2);
		return kld;
	}

	
	/**
	 * Normalises an frequency vector
	 * @param p the vector to be normalised
	 * @return the normalised vector
	 */
	public double[] normalise(double[] p){

		double sum = sum(p);
		//divide each value by the total sum
		for(int i=0; i<p.length; i++){
			if(p[i] != 0.0){
				double norm = p[i]/sum;
				p[i] = norm;	
			}
		}
		return p;
	}


	/**
	 * Gets the sum of a frequency vector
	 * @param p the frequency vector to be totalled
	 * @return the sum of the vector
	 */
	public double sum(double[] p){
		double sum = 0.0;
		for(double d: p){
			sum+=d;
		}
		return sum;
	}
}