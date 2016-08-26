
//
//
//public class JensenShannonDivergence {
//
//	double[] p, q, m;
//
//	public JensenShannonDivergence(){
//
//	}
//
//	public double computeDivergence(double[] p, double[] q){
//		
//		this.p = normalise(p);
//		this.q = normalise(q);
//		
//		m = new double[p.length];
//
//		for(int i = 0; i < m.length; i++){
//			m[i] = (p[i] + q[i]) / 2;
//		}
//
//		double jsd = 0.5 * (kullbackLeiblerDivergence(p, m) + kullbackLeiblerDivergence(q, m)); 
//		return jsd;
//	}
//
//	public double kullbackLeiblerDivergence(double[] p, double[] m){
//		
//		double log2 = Math.log(2);
//		double kld = sum(p) * Math.log(sum(p)/sum(m));
//		return kld / log2;
//	}
//
//
//	public double[] normalise(double[] p){
//
//		double sum = sum(p);
//
//		for(double d: p){
//			d = d/sum;
//		}
//
//		return p;
//	}
//	
//
//	public double sum(double[] p){
//		double sum = 0.0;
//		for(double d: p){
//			sum+=d;
//		}
//
//		return sum;
//	}
//}


/* Copyright (C) 2003 Univ. of Massachusetts Amherst, Computer Science Dept.
This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */

//package cc.mallet.util;

/**
 * 
 * 
 * @author <a href="mailto:casutton@cs.umass.edu">Charles Sutton</a>
 * @version $Id: ArrayUtils.java,v 1.1 2007/10/22 21:37:40 mccallum Exp $
 */
public class JensenShannonDivergence{


	public JensenShannonDivergence(){

	}

	/**
	 * Returns the Jensen-Shannon divergence.
	 */
	public static double computeDivergence(double[] p1, double[] p2) {
		assert(p1.length == p2.length);

		p1 = normalise(p1);
		p2 = normalise(p2);

		double[] average = new double[p1.length];
		for (int i = 0; i < p1.length; ++i) {
			average[i] += (p1[i] + p2[i])/2;
		}
		
		return (klDivergence(p1, average) + klDivergence(p2, average))/2;
	}


	public static final double log2 = Math.log(2);
	/**
	 * Returns the KL divergence, K(p1 || p2).
	 *
	 * The log is w.r.t. base 2. <p>
	 *
	 * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the KL-divergence
	 * is <tt>infinite</tt>. Limin changes it to zero instead of infinite. 
	 * 
	 */
	public static double klDivergence(double[] p1, double[] p2) {


		double klDiv = 0.0;

		for (int i = 0; i < p1.length; ++i) {
			if (p1[i] == 0) { continue; }
			if (p2[i] == 0.0) { continue; } // Limin

			klDiv += p1[i] * Math.log( p1[i] / p2[i] );
		}

		return klDiv / log2; // moved this division out of the loop -DM
	}

	public static double[] normalise(double[] p){

		double sum = sum(p);

		for(int i=0; i<p.length; i++){
			if(p[i] != 0.0){
				double norm = p[i]/sum;
				p[i] = norm;
			}
		}


		return p;
	}


	public static double sum(double[] p){
		double sum = 0.0;
		for(double d: p){
			sum+=d;
		}
		//System.out.println(sum);
		return sum;
	}
}
