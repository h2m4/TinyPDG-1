package yoshikihigo.tinypdg.graphviz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import yoshikihigo.tinypdg.ast.TinyPDGASTVisitor;
import yoshikihigo.tinypdg.pe.MethodInfo;

public class Writer {

	public static void main(String[] args) {

		try {

			final Options options = new Options();

			{
				final Option d = new Option("d", "directory", true,
						"target directory");
				d.setArgName("directory");
				d.setArgs(1);
				d.setRequired(true);
				options.addOption(d);
			}

			{
				final Option c = new Option("c", "ControlFlowGraph", true,
						"control flow graph");
				c.setArgName("file");
				c.setArgs(1);
				c.setRequired(false);
				options.addOption(c);
			}

			{
				final Option p = new Option("p", "ProgramDepencencyGraph",
						true, "program dependency graph");
				p.setArgName("file");
				p.setArgs(1);
				p.setRequired(false);
				options.addOption(p);
			}

			// {
			// final Option o = new Option("o", "optimize", true,
			// "remove unnecessary nodes from CFGs and PDGs");
			// o.setArgName("boolean");
			// o.setArgs(1);
			// o.setRequired(false);
			// options.addOption(o);
			// }

			// {
			// final Option a = new Option("a", "atomic", true,
			// "dissolve complicated statements into simple statements");
			// a.setArgName("boolean");
			// a.setArgs(1);
			// a.setRequired(false);
			// options.addOption(a);
			// }

			final CommandLineParser parser = new PosixParser();
			final CommandLine cmd = parser.parse(options, args);

			final File target = new File(cmd.getOptionValue("d"));
			if (!target.exists()) {
				System.err
						.println("specified directory or file does not exist.");
				System.exit(0);
			}

			final List<File> files = getFiles(target);
			final List<MethodInfo> methods = new ArrayList<MethodInfo>();
			for (final File file : files) {
				final CompilationUnit unit = TinyPDGASTVisitor.createAST(file);
				final List<MethodInfo> m = new ArrayList<MethodInfo>();
				final TinyPDGASTVisitor visitor = new TinyPDGASTVisitor(
						file.getAbsolutePath(), unit, methods);
				unit.accept(visitor);
				methods.addAll(m);
			}

			if (cmd.hasOption("c")) {
				System.out.println("building and outputing CFGs ...");
				final BufferedWriter writer = new BufferedWriter(
						new FileWriter(cmd.getOptionValue("c")));

				writer.write("digraph CFG {");
				writer.newLine();

				int createdGraphNumber = 0;
				for (final MethodInfo method : methods) {
//					writeMethodCFG(method, createdGraphNumber++, writer, false,
//							false);
				}

				writer.write("}");

				writer.close();
			}

			if (cmd.hasOption("p")) {
				System.out.println("building and outputing PDGs ...");
				final BufferedWriter writer = new BufferedWriter(
						new FileWriter(cmd.getOptionValue("p")));

				writer.write("digraph {");
				writer.newLine();

				int createdGraphNumber = 0;
				for (final MethodInfo method : methods) {

					// final IntraProceduralPDG pdg = new IntraProceduralPDG(
					// method, new DefaultPDGNodeFactory(),
					// new DefaultCFGNodeFactory(), true, true, true,
					// state, optimize, dissolve);
					// writePDG(pdg, createdGraphNumber++, writer);
				}

				writer.write("}");

				writer.close();
			}

			System.out.println("successfully finished.");

		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
	}

//	static private void writeMethodCFG(final MethodInfo unit,
//			final int createdGraphNumber, final BufferedWriter writer,
//			final boolean optimize, final boolean dissolve) throws IOException {
//
//		final IntraProceduralCFG cfg = new IntraProceduralCFG(unit,
//				new DefaultCFGNodeFactory(), optimize, dissolve);
//
//		writer.write("subgraph cluster");
//		writer.write(Integer.toString(createdGraphNumber));
//		writer.write(" {");
//		writer.newLine();
//
//		writer.write("label = \"");
//		writer.write(unit.getSignatureText());
//		writer.write("\";");
//		writer.newLine();
//
//		final Map<CFGNode<? extends ExecutableElementInfo>, Integer> nodeLabels = new HashMap<CFGNode<? extends ExecutableElementInfo>, Integer>();
//		for (final CFGNode<?> node : cfg.getAllNodes()) {
//			nodeLabels.put(node, nodeLabels.size());
//		}
//
//		for (final Map.Entry<CFGNode<? extends ExecutableElementInfo>, Integer> entry : nodeLabels
//				.entrySet()) {
//			writer.write(Integer.toString(createdGraphNumber));
//			writer.write(".");
//			writer.write(Integer.toString(entry.getValue()));
//			writer.write(" [style = filled, label = \"");
//			writer.write(entry.getKey().getText().replace("\"", "\\\"")
//					.replace("\\\\\"", "\\\\\\\""));
//			writer.write("\"");
//
//			if (cfg.getEnterNode() == entry.getKey()) {
//				writer.write(", fillcolor = aquamarine");
//			} else if (cfg.getExitNodes().contains(entry.getKey())) {
//				writer.write(", fillcolor = deeppink");
//			} else {
//				writer.write(", fillcolor = white");
//			}
//
//			if (entry.getKey() instanceof CFGControlNode) {
//				writer.write(", shape = diamond");
//			} else {
//				writer.write(", shape = ellipse");
//			}
//
//			writer.write("];");
//			writer.newLine();
//		}
//
//		writeCFGEdges(cfg, nodeLabels, createdGraphNumber, writer);
//
//		writer.write("}");
//		writer.newLine();
//	}
//
//	static private void writeCFGEdges(
//			final CFG cfg,
//			final Map<CFGNode<? extends ExecutableElementInfo>, Integer> nodeLabels,
//			final int createdGraphNumber, final BufferedWriter writer)
//			throws IOException {
//
//		if (null == cfg) {
//			return;
//		}
//
//		final Set<CFGEdge> edges = new HashSet<CFGEdge>();
//		for (final CFGNode<?> node : cfg.getAllNodes()) {
//			edges.addAll(node.getBackwardEdges());
//			edges.addAll(node.getForwardEdges());
//		}
//
//		for (final CFGEdge edge : edges) {
//			writer.write(Integer.toString(createdGraphNumber));
//			writer.write(".");
//			final CFGNode<?> fromNode = edge.getFromNode();
//			writer.write(Integer.toString(nodeLabels.get(fromNode)));
//			writer.write(" -> ");
//			writer.write(Integer.toString(createdGraphNumber));
//			writer.write(".");
//			final CFGNode<?> toNode = edge.getToNode();
//			writer.write(Integer.toString(nodeLabels.get(toNode)));
//			writer.write(" [style = solid, label=\""
//					+ edge.getDependenceString() + "\"];");
//			writer.newLine();
//		}
//	}
//
//	static private void writePDG(final IntraProceduralPDG pdg,
//			final int createdGraphNumber, final BufferedWriter writer)
//			throws IOException {
//
//		final CallableUnitInfo method = pdg.getMethodInfo();
//
//		writer.write("subgraph cluster");
//		writer.write(Integer.toString(createdGraphNumber));
//		writer.write(" {");
//		writer.newLine();
//
//		writer.write("label = \"");
//		writer.write(method.getSignatureText());
//		writer.write("\";");
//		writer.newLine();
//
//		final Map<PDGNode<?>, Integer> nodeLabels = new HashMap<PDGNode<?>, Integer>();
//		for (final PDGNode<?> node : pdg.getAllNodes()) {
//			nodeLabels.put(node, nodeLabels.size());
//		}
//
//		for (final Map.Entry<PDGNode<?>, Integer> entry : nodeLabels.entrySet()) {
//			writer.write(Integer.toString(createdGraphNumber));
//			writer.write(".");
//			writer.write(Integer.toString(entry.getValue()));
//			writer.write(" [style = filled, label = \"");
//			writer.write(entry.getKey().getText().replace("\"", "\\\"")
//					.replace("\\\\\"", "\\\\\\\""));
//			writer.write("\"");
//
//			if (entry.getKey() instanceof PDGMethodEnterNode) {
//				writer.write(", fillcolor = aquamarine");
//			} else if (pdg.getExitNodes().contains(entry.getKey())) {
//				writer.write(", fillcolor = deeppink");
//			} else if (entry.getKey() instanceof PDGDataInNode) {
//				writer.write(", fillcolor = tomato");
//			} else if (entry.getKey() instanceof PDGDataOutNode) {
//				writer.write(", fillcolor = darkorange");
//			} else {
//				writer.write(", fillcolor = white");
//			}
//
//			if (entry.getKey() instanceof PDGControlNode) {
//				writer.write(", shape = diamond");
//			} else if (entry.getKey() instanceof PDGDataInNode
//					|| entry.getKey() instanceof PDGDataOutNode) {
//				writer.write(", shape = box");
//			} else {
//				writer.write(", shape = ellipse");
//			}
//
//			writer.write("];");
//			writer.newLine();
//		}
//
//		for (final PDGEdge edge : pdg.getAllEdges()) {
//			writer.write(Integer.toString(createdGraphNumber));
//			writer.write(".");
//			writer.write(Integer.toString(nodeLabels.get(edge.getFromNode())));
//			writer.write(" -> ");
//			writer.write(Integer.toString(createdGraphNumber));
//			writer.write(".");
//			writer.write(Integer.toString(nodeLabels.get(edge.getToNode())));
//			if (edge instanceof PDGDataDependenceEdge) {
//				writer.write(" [style = solid, label=\""
//						+ edge.getDependenceString() + "\"]");
//			} else if (edge instanceof PDGControlDependenceEdge) {
//				writer.write(" [style = dotted, label=\""
//						+ edge.getDependenceString() + "\"]");
//			} else if (edge instanceof PDGExecutionDependenceEdge) {
//				writer.write(" [style = bold, label=\""
//						+ edge.getDependenceString() + "\"]");
//			}
//			writer.write(";");
//			writer.newLine();
//		}
//
//		writer.write("}");
//		writer.newLine();
//	}
//
	static private List<File> getFiles(final File file) {

		final List<File> files = new ArrayList<File>();

		if (file.isFile() && file.getName().endsWith(".java")) {
			files.add(file);
		}

		else if (file.isDirectory()) {
			for (final File child : file.listFiles()) {
				final List<File> children = getFiles(child);
				files.addAll(children);
			}
		}

		return files;
	}
}
