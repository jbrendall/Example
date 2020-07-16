package traceModels;

import java.util.HashMap;

import links.Link;

public interface TraceModel {
	public HashMap<Link, Double> run();
}
