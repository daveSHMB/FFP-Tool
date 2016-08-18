


public class JensenShannonDivergence {

	Double[] p, q, m;

	public JensenShannonDivergence(Double[] p, Double[] q){

		this.p = normalise(p);
		this.q = normalise(q);


	}

	public Double computeDivergence(){
		m = new Double[p.length];

		for(int i = 0; i < m.length; i++){
			m[i] = (p[i] + q[i]) / 2;
		}

		double jsd = 0.5 * (kullbackLeiblerDivergence(p, m) + kullbackLeiblerDivergence(q, m)); 
		return jsd;
	}

	public double kullbackLeiblerDivergence(Double[] p, Double[] m){
		
		Double kld = sum(p) * Math.log(sum(p)/sum(m));
		return kld / Math.log(2);
	}


	public Double[] normalise(Double[] p){

		Double sum = sum(p);

		for(double d: p){
			d = d/sum;
		}

		return p;
	}

	public Double sum(Double[] p){
		double sum = 0.0;
		for(Double d: p){
			sum+=d;
		}

		return sum;
	}
}
