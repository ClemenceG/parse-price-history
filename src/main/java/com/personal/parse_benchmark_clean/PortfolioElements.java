package com.personal.parse_benchmark_clean;

public class PortfolioElements {
	private double weight;
	private Security security;
	
	PortfolioElements(Security security, double weight) {
		this.weight = weight;
		this.security = security;
	}
	PortfolioElements(Security security) {
		this.security = security;
		this.weight = 0;
	}
	
	public Security getSecurity() {
		return this.security;
	}
	public double getWeight() {
		return this.weight;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((security == null) ? 0 : security.hashCode());
		long temp;
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PortfolioElements other = (PortfolioElements) obj;
		if (security == null) {
			if (other.security != null)
				return false;
		} else if (!security.equals(other.security))
			return false;
		if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BenchmarkElement [weight=" + weight + ", security=" + security + "]";
	}
	

	
}
