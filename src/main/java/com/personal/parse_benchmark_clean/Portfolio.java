package com.personal.parse_benchmark_clean;

import java.util.List;
import java.util.ArrayList;

public class Portfolio {
	private String name;
	private List<PortfolioElements> portfolioElements = new ArrayList<PortfolioElements>();
	
	
	Portfolio(String name) {
		this.name = name;
	}
	
	
	public String getSecurityTicker(int i) {
		return this.portfolioElements.get(i).getSecurity().getTicker();
	}
	public String getSecurityBBGlobal(int i) {
		return this.portfolioElements.get(i).getSecurity().getBloombergGlobal();
	}
	public List<PortfolioElements> getBenchmarkElements() {
		return this.portfolioElements;
	}
	public PortfolioElements getBenchmarkElement(int i) {
		return this.portfolioElements.get(i);
	}
	public Security getSecurity(int index) {
		return this.portfolioElements.get(index).getSecurity();
	}
	
	
	public void addBenchmarkElement(PortfolioElements portfolioElements) {
		this.portfolioElements.add(portfolioElements);
	}
	public void removeBenchmarkElement(int index) {
		this.portfolioElements.remove(index);
	}
	public void addBenchmarkElement(int index, PortfolioElements portfolioElements) {
		this.portfolioElements.add(index, portfolioElements);
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((portfolioElements == null) ? 0 : portfolioElements.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Portfolio other = (Portfolio) obj;
		if (portfolioElements == null) {
			if (other.portfolioElements != null)
				return false;
		} else if (!portfolioElements.equals(other.portfolioElements))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Benchmark [name=" + name + ", benchmarkElements=" + portfolioElements + "]";
	}
	
	
	
}
