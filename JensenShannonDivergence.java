
public class JensenShannonDivergence {

	double[] p, q, m;

	public JensenShannonDivergence(){

	}

	public double getJSDDivergence(double[] p, double[] q){

		this.p = normalise(p);
		this.q = normalise(q);

		m = new double[p.length];

		for(int i = 0; i < m.length; i++){
			m[i] = (p[i] + q[i]) / 2;
		}

		double jsd = 0.5 * (kullbackLeiblerDivergence(p, m) + kullbackLeiblerDivergence(q, m)); 
		return jsd;
	}

	public double kullbackLeiblerDivergence(double[] p, double[] m){

		double kld = 0.0;
		for (int i = 0; i < p.length; ++i) {
			if (p[i] == 0.0 || m[i] == 0.0) {
				continue; 
			}

			kld += p[i] * Math.log( p[i] / m[i] );
		}
		
		kld /= Math.log(2);
		return kld;
	}

	public double[] normalise(double[] p){

		double sum = sum(p);

		for(int i=0; i<p.length; i++){
			if(p[i] != 0.0){
				double norm = p[i]/sum;
				p[i] = norm;	
			}
		}
		return p;
	}


	public double sum(double[] p){
		double sum = 0.0;
		for(double d: p){
			sum+=d;
		}
		return sum;
	}
}